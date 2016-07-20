package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.AdjuntosCap;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("capacitacionesPortalFC")
public class CapacitacionesPortalFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	List<Capacitaciones> lCapaOtrasOEE;
	Map<String, AdjuntosCap> diccCapaPrincipal;

	public void init() {
		cargarCapaPrincipal();
		cargarCapaOtrasOee();
	}

	private Integer obtenerParam() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ "where Referencias.dominio = 'PUBLICACION_CAPACITACION' "
				+ "and Referencias.descAbrev = 'LIMITE_PUB' ");
		// Si o si debe traer el valor o sino debe ser una excepción del sistema

		Referencias ref = (Referencias) q.getSingleResult();
		return ref.getValorNum();

	}

	private void cargarCapaPrincipal() {
		diccCapaPrincipal = new HashMap<String, AdjuntosCap>();
		Query q =
			em.createQuery("select AdjuntosCap from AdjuntosCap AdjuntosCap"
				+ " where  AdjuntosCap.tipo in ('IM1', 'IM2','IM3') ");
		List<AdjuntosCap> lista = q.getResultList();
		for (AdjuntosCap o : lista) {
			diccCapaPrincipal.put(o.getTipo(), o);
		}
	}

	private void cargarCapaOtrasOee() {

		Query q =
			em.createQuery("select Capacitaciones from Capacitaciones Capacitaciones "
				+ " where Capacitaciones.activo is true "
				+ "and Capacitaciones.tipo = 'CAP_OG290'  "
				+ " and Capacitaciones.fechaPubDesde <=:fechaSistema "
				+ " and Capacitaciones.fechaPubHasta >=:fechaSistema "
				+ " order by Capacitaciones.fechaPubDesde ");
		q.setParameter("fechaSistema", new Date());
		q.setMaxResults(obtenerParam().intValue());
		lCapaOtrasOEE = q.getResultList();
	}

	public String ubicacionArchivo(Documentos docu) {
		if (docu != null) {
			return docu.getUbicacionFisica() + File.separator + docu.getNombreFisicoDoc();
		}
		return "";
	}

	public String formatearFecha(Date fecha) {
		if(fecha==null){
			return "-";
		}
			
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(fecha);
	}

	public String limitarSizeDesc(String cadena, Integer size) {
		if (cadena.length() > size) {
			return cadena.substring(0, size) + "...";
		}
		return cadena;
	}

	public List<Capacitaciones> getlCapaOtrasOEE() {
		return lCapaOtrasOEE;
	}

	public void setlCapaOtrasOEE(List<Capacitaciones> lCapaOtrasOEE) {
		this.lCapaOtrasOEE = lCapaOtrasOEE;
	}

	public Map<String, AdjuntosCap> getDiccCapaPrincipal() {
		return diccCapaPrincipal;
	}

	public void setDiccCapaPrincipal(Map<String, AdjuntosCap> diccCapaPrincipal) {
		this.diccCapaPrincipal = diccCapaPrincipal;
	}

}
