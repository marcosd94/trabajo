package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PreguntasFrecuentes;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.TransicionEstado;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PreguntasFrecuentesHome;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("preguntasFrecuentesFormController")
@Scope(ScopeType.PAGE)
public class PreguntasFrecuentesFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -904163366624832558L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	PreguntasFrecuentesHome preguntasFrecuentesHome;

	PreguntasFrecuentes preguntasFrecuentes;
	private Long idTipoPregunta;
	private Integer nroOrden;
	private String publicarPortal;
	private Integer OrdenMax;

	public void init() {
		preguntasFrecuentes = preguntasFrecuentesHome.getInstance();
		if (preguntasFrecuentesHome.isIdDefined()) {

			idTipoPregunta = preguntasFrecuentes.getDatosEspecificos()
					.getIdDatosEspecificos();
			nroOrden = preguntasFrecuentes.getNroOrden();
			OrdenMax = nroOrden;
			if (preguntasFrecuentes.isPublicarSN())
				publicarPortal = "S";
			else
				publicarPortal = "N";
		} else {
			publicarPortal = "S";
			preguntasFrecuentes.setActivo(true);
		}
	}

	public void buscarOrderCorrespondiente() {
		String sql = "select max(p.nro_orden) from seleccion.preguntas_frecuentes p"
				+ " join seleccion.datos_especificos d"
				+ " on p.id_datos_especificos_tipo_preg = d.id_datos_especificos"
				+ " where p.id_datos_especificos_tipo_preg = " + idTipoPregunta;
		try {
			String dato = (String) em.createNativeQuery(sql).getSingleResult()
					.toString();
			if (!dato.equals(""))
				nroOrden = Integer.parseInt(dato);
			else
				nroOrden = 0;
			nroOrden++;
			OrdenMax = nroOrden;
		} catch (Exception e) {
			nroOrden = 0;
			nroOrden++;
			OrdenMax = nroOrden;
		}
	}

	public String save() {
		if (!verficarOrden()) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("CU286_msg_orden"));
		}
		if (preguntasFrecuentes.getRespuestaPregunta() == null
				|| preguntasFrecuentes.getRespuestaPregunta().trim().isEmpty()
				|| preguntasFrecuentes.getPreguntaFrecuente() == null
				|| preguntasFrecuentes.getPreguntaFrecuente().trim().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return null;
		}

			try {
				List<PreguntasFrecuentes> listaPreguntas = new ArrayList<PreguntasFrecuentes>();
				listaPreguntas = buscarCoicidentesOrden();
				preguntasFrecuentes.setDatosEspecificos(em.find(
						DatosEspecificos.class, idTipoPregunta));
				preguntasFrecuentes.setNroOrden(nroOrden);
				if (publicarPortal.equals("S"))
					preguntasFrecuentes.setPublicarSN(true);
				if (publicarPortal.equals("N"))
					preguntasFrecuentes.setPublicarSN(false);
				preguntasFrecuentesHome.setInstance(preguntasFrecuentes);
				String resultOperation = preguntasFrecuentesHome.persist();
				if (listaPreguntas.size() > 0)
					OrdenMax--;
				for (PreguntasFrecuentes obj : listaPreguntas) {
					OrdenMax++;
					obj.setNroOrden(OrdenMax);
					em.merge(obj);
					em.flush();
				}
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				return resultOperation;

			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
	}

	public String updated() {
		if (!verficarOrden()) {
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("CU286_msg_orden"));
		}
		if (preguntasFrecuentes.getRespuestaPregunta() == null
				|| preguntasFrecuentes.getRespuestaPregunta().trim().isEmpty()
				|| preguntasFrecuentes.getPreguntaFrecuente() == null
				|| preguntasFrecuentes.getPreguntaFrecuente().trim().isEmpty()){
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Ingrese los datos obligatorios");
			return null;
		}

		try {
			List<PreguntasFrecuentes> listaPreguntas = new ArrayList<PreguntasFrecuentes>();
			listaPreguntas = buscarCoicidentesOrden();
			preguntasFrecuentes.setDatosEspecificos(em.find(
					DatosEspecificos.class, idTipoPregunta));
			preguntasFrecuentes.setNroOrden(nroOrden);
			if (publicarPortal.equals("S"))
				preguntasFrecuentes.setPublicarSN(true);
			if (publicarPortal.equals("N"))
				preguntasFrecuentes.setPublicarSN(false);
			preguntasFrecuentesHome.setInstance(preguntasFrecuentes);
			String resultOperation = preguntasFrecuentesHome.update();
			if (listaPreguntas.size() > 0)
				OrdenMax--;
			for (PreguntasFrecuentes obj : listaPreguntas) {
				OrdenMax++;
				obj.setNroOrden(OrdenMax);
				em.merge(obj);
				em.flush();
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return resultOperation;

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private List<PreguntasFrecuentes> buscarCoicidentesOrden() {
		String sql = "select p.* from seleccion.preguntas_frecuentes p "
				+ "join seleccion.datos_especificos d "
				+ "on p.id_datos_especificos_tipo_preg = d.id_datos_especificos "
				+ "where p.id_datos_especificos_tipo_preg = " + idTipoPregunta
				+ " and p.nro_orden <= " + nroOrden + " order by p.nro_orden";
		List<PreguntasFrecuentes> lista = new ArrayList<PreguntasFrecuentes>();
		lista = em.createNativeQuery(sql, PreguntasFrecuentes.class)
				.getResultList();
		return lista;
	}

	private Boolean verficarOrden() {
		if (nroOrden <= OrdenMax)
			return true;
		return false;
	}

	public PreguntasFrecuentes getPreguntasFrecuentes() {
		return preguntasFrecuentes;
	}

	public void setPreguntasFrecuentes(PreguntasFrecuentes preguntasFrecuentes) {
		this.preguntasFrecuentes = preguntasFrecuentes;
	}

	public Long getIdTipoPregunta() {
		return idTipoPregunta;
	}

	public void setIdTipoPregunta(Long idTipoPregunta) {
		this.idTipoPregunta = idTipoPregunta;
	}

	public Integer getNroOrden() {
		return nroOrden;
	}

	public void setNroOrden(Integer nroOrden) {
		this.nroOrden = nroOrden;
	}

	public String getPublicarPortal() {
		return publicarPortal;
	}

	public void setPublicarPortal(String publicarPortal) {
		this.publicarPortal = publicarPortal;
	}

	public Integer getOrdenMax() {
		return OrdenMax;
	}

	public void setOrdenMax(Integer ordenMax) {
		OrdenMax = ordenMax;
	}

}
