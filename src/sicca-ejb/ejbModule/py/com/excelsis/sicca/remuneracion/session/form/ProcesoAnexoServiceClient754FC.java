package py.com.excelsis.sicca.remuneracion.session.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import py.com.excelsis.sicca.entity.SinAnxOriginal;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import anexoserviceclient.AnexoServiceClient;

@Name("procesoAnexoServiceClient754FC")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class ProcesoAnexoServiceClient754FC {

	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false, create = true)
	ImportAnexo750FC importAnexo750FC;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	public Integer traerCantDias() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'ANEXO_CONF' "
				+ " and Referencias.descLarga = 'CANT_DIAS' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();
	}

	public String traerHora() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ " where Referencias.dominio = 'ANEXO_CONF'"
				+ " and Referencias.descLarga = 'HORA_DESDE' ");
		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorAlf();
	}

	public String traerFechaSinAnxOriginal(SinAnxOriginal remu) {
		if (remu != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd");
			String hora = sdf.format(remu.getAnxFching());
			return hora;
		}
		return null;
	}

	public SinAnxOriginal traerSinAnxOriginal() {
		Query q = em.createQuery("select SinAnxOriginal from SinAnxOriginal SinAnxOriginal ");
		q.setMaxResults(1);
		List<SinAnxOriginal> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	@Transactional
	@Asynchronous
	public QuartzTriggerHandle procesoPrincipal(@Expiration Date when, @IntervalCron String interval) {
		
		try {
			if (!ejecutar())
				return null;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			AnexoServiceClient wsAnexo = new AnexoServiceClient();
			List<String> lista = wsAnexo.getRegistros();
			if (importAnexo750FC == null)
				importAnexo750FC =
					(ImportAnexo750FC) org.jboss.seam.Component.getInstance(ImportAnexo750FC.class, true);
			importAnexo750FC.procesarLineas(lista, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean ejecutar() throws ParseException {
		Integer dia = traerCantDias();
		String horaConf = traerHora();
		SinAnxOriginal sinax = traerSinAnxOriginal();
		String diaRemu = traerFechaSinAnxOriginal(sinax);
		SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");
		String horaHoy = sdfHora.format(new Date());
		if (diaRemu == null) {
			return ProcesoClienteWSSinarah721FC.decidirEnBaseHora(horaHoy, horaConf);
		} else {
			// Verificar si estamos dento del rango
			Calendar calRemu = Calendar.getInstance();
			calRemu.setTime(sinax.getAnxFching());
			Calendar calHoy = Calendar.getInstance();
			calRemu.add(Calendar.DAY_OF_MONTH, dia);
			SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
			if (sdfFecha.parse(sdfFecha.format(calHoy.getTime())).getTime() >= sdfFecha.parse(sdfFecha.format(calRemu.getTime())).getTime()) {
				// verificar si es la hora marcada
				return ProcesoClienteWSSinarah721FC.decidirEnBaseHora(horaHoy, horaConf);
			}
		}
		return false;
	}
}
