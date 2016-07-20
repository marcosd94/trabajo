package py.com.excelsis.sicca.remuneracion.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("procesoUpdateEntidadOee763FC")
public class ProcesoUpdateEntidadOee763FC implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 19942425826280084L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;

	private Entidad entidad;
	private String resultadoFinal = null;
	private Integer anhoActual = null;
	private List<ConfiguracionUoCab> listaOees = new ArrayList<ConfiguracionUoCab>();

	public String procesoPrincipal() {
		try {
			Date fechaActual = new Date();
			anhoActual = fechaActual.getYear() + 1900;
			core();
			if (resultadoFinal == null
					|| resultadoFinal.equalsIgnoreCase("FAIL")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle
						.getBundle().getString("GENERICO_NO_MSG"));
				return "FAIL";
			}
			else if(resultadoFinal.equalsIgnoreCase("TODAS")){
				statusMessages.add(Severity.INFO, "Todas las OEE’s ya se encuentran configuradas");
				return "OK";
			}
			else {
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				return "OK";
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			return "FAIL";
		}
	}

	private void core() {
		try {
			obtenerOees();
			SinEntidad sinEntidadActual = new SinEntidad();
			if(listaOees.isEmpty()){
				resultadoFinal = "TODAS";
				return;
			}
			for (ConfiguracionUoCab oee : listaOees) {
				List<Object[]> listaFiltros = obtenerFiltros(oee.getIdConfiguracionUo());
				for (Object[] o : listaFiltros) {
					BigDecimal nenCodigo = (BigDecimal) new BigDecimal(o[0].toString());
					BigDecimal entCodigo = (BigDecimal) new BigDecimal(o[1].toString());
					sinEntidadActual = obtenerSinEntidad(nenCodigo, entCodigo);
					if(sinEntidadActual != null){
						entidad = new Entidad();
						entidad.setAnho(anhoActual);
						entidad.setSinEntidad(sinEntidadActual);
						entidad.setNenCodigo(nenCodigo);
						entidad.setEntCodigo(entCodigo);
						entidad.setConfiguracionUoCab(oee);
						em.persist(entidad);
						
						oee.setEntidad(entidad);
						oee.setAnho(anhoActual);
						em.merge(oee);
					}
				}
			}
			resultadoFinal = "OK";
			em.flush();
		} catch (Exception e) {
			e.printStackTrace();
			resultadoFinal = "FAIL";
		}
	}

	private void obtenerOees() {
		listaOees = em
				.createQuery(
						"select oee from ConfiguracionUoCab oee where oee.activo is true and oee.anho = "
								+ anhoActual).getResultList();
	}

	private List<Object[]> obtenerFiltros(Long oeeActual) {

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT s.nen_codigo, s.ent_codigo, oee.id_configuracion_uo  ");
		sql.append("FROM sinarh.sin_entidad s ");
		sql.append(" join planificacion.entidad e on s.id_sin_entidad = e.id_sin_entidad   ");
		sql.append("join planificacion.configuracion_uo_cab oee on e.id_entidad = oee.id_entidad ");
		sql.append("where oee.id_configuracion_uo = :id   ");

		Query q = em.createNativeQuery(sql.toString());
		q.setParameter("id", oeeActual);
		List<Object[]> lista = q.getResultList();
		return lista;
	}
	
	private SinEntidad obtenerSinEntidad(BigDecimal nenCod, BigDecimal entCod) {
		List<SinEntidad> l = em
				.createQuery(
						"select se from SinEntidad se where se.activo is true and se.aniAniopre = "
								+ anhoActual+" and se.nenCodigo = "+nenCod +" and se.entCodigo = "+entCod).getResultList();
		if(l.isEmpty())
			return null;
		return l.get(0);
	}

	public String getResultadoFinal() {
		return resultadoFinal;
	}

	public void setResultadoFinal(String resultadoFinal) {
		this.resultadoFinal = resultadoFinal;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public List<ConfiguracionUoCab> getListaOees() {
		return listaOees;
	}

	public void setListaOees(List<ConfiguracionUoCab> listaOees) {
		this.listaOees = listaOees;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}
	
	

}
