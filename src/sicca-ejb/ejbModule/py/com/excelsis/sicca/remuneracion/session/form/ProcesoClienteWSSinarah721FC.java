package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;

import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RemunConfig;
import py.com.excelsis.sicca.entity.Remuneraciones;
import py.com.excelsis.sicca.util.JpaResourceBean;
import remuneracionserviceclient.RemuneracionServiceClient;

@Name("procesoClienteWSSinarah721FC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class ProcesoClienteWSSinarah721FC {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	ImportaMasivaRemuSinarh716FC importaMasivaRemuSinarh716FC;
	@In(create = true)
	ImportaMasivaRemuNomi680FC importaMasivaRemuNomi680FC;

	 
	private Map<String, List<String>> diccCodigos = new HashMap<String, List<String>>();

	private void cargarDiccCodigos() {
		Query q =
			em.createQuery("select RemunConfig from RemunConfig RemunConfig where RemunConfig.activo is true and RemunConfig.origen = 'S' ");
		List<RemunConfig> lista = q.getResultList();
		diccCodigos.clear();
		for (RemunConfig o : lista) {
			String codSinarh = o.getConfiguracionUoCab().getCodigoSinarh();
			String composCodSinarh[] = codSinarh.split("/");
			String elKey = "";
			for (String p : composCodSinarh) {
				elKey = o.getConfiguracionUoCab().getIdConfiguracionUo().toString();
				if (diccCodigos.get(elKey) == null) {
					diccCodigos.put(elKey, new ArrayList<String>());
				}
				diccCodigos.get(elKey).add(p);
			}
		}
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval)  {
		
		try {
			if (!ejecutar())
				return null;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		cargarDiccCodigos();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		try {
			RemuneracionServiceClient wsRemSC = new RemuneracionServiceClient();
			for (String o : diccCodigos.keySet()) {				
				for (String p : diccCodigos.get(o)) {
					String compos[] = p.split("\\.");
					List<String> lListaRemus =
						wsRemSC.traerRegistros(Integer.parseInt(sdf.format(new Date())), Integer.parseInt(compos[0]), Integer.parseInt(compos[1]), Integer.parseInt(compos[2]), Integer.parseInt(compos[3]));
					importaMasivaRemuNomi680FC.lLineasArch = lListaRemus;
					importaMasivaRemuSinarh716FC.massiveImport(Long.parseLong(o));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
		return null;
	}

	public Integer traerCantDias() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'REMUNERACION_CONF' "
				+ " and Referencias.descLarga = 'CANT_DIAS' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	public String traerHora() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'REMUNERACION_CONF'"
				+ " and Referencias.descLarga = 'HORA_DESDE' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorAlf();
	}

	public Remuneraciones traerRemuneracion() {
		Query q =
			em.createQuery("select Remuneraciones from Remuneraciones Remuneraciones ");
		q.setMaxResults(1);
		List<Remuneraciones> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

 

	public String traerFechaRemuneracion(Remuneraciones remu) {
		if (remu != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			String hora = sdf.format(remu.getFechaAlta());
			return hora;
		}
		return null;
	}

	public static Boolean decidirEnBaseHora(String horaHoy, String horaConf) {

		String cHoraConf[] = horaConf.split(":");
		String cHoraHoy[] = horaHoy.split(":");
		if (cHoraHoy[0].equalsIgnoreCase(cHoraConf[0])) {
			int intHoraHoy = Integer.parseInt(cHoraHoy[1]);
			int inHoraConf = Integer.parseInt(cHoraConf[1]);
			if (inHoraConf <= intHoraHoy) {
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	public Boolean ejecutar() throws ParseException {
		Integer dia = traerCantDias();
		String horaConf = traerHora();
		Remuneraciones remu = traerRemuneracion();
		String diaRemu = traerFechaRemuneracion(remu);
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		String horaHoy = sdfHora.format(new Date());
		if (diaRemu == null) {
			return decidirEnBaseHora(horaHoy, horaConf);
		} else {
			// Verificar si estamos dento del rango
			Calendar calRemu = Calendar.getInstance();
			calRemu.setTime(remu.getFechaAlta());
			Calendar calHoy = Calendar.getInstance();
			calRemu.add(Calendar.DAY_OF_MONTH, dia);
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			if (sdfFecha.parse(sdfFecha.format(calHoy.getTime())).getTime() >=sdfFecha.parse(sdfFecha.format(calRemu.getTime())).getTime()) {
				// verificar si es la hora marcada
				return decidirEnBaseHora(horaHoy, horaConf);
			}
		}
		return false;
	}
}
