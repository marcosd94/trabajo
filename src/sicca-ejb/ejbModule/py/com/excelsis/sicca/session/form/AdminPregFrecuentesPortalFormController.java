package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.entity.PreguntasFrecuentes;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PreguntasFrecuentesHome;
import py.com.excelsis.sicca.session.PreguntasFrecuentesList;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("adminPregFrecuentesPortalFormController")
@Scope(ScopeType.PAGE)
public class AdminPregFrecuentesPortalFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1397193588394412251L;

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
	@In(create = true)
	PreguntasFrecuentesList preguntasFrecuentesList;

	PreguntasFrecuentes preguntasFrecuentes;
	private Long idTipoPregunta;
	List<PreguntasFrecuentes> listaPreguntasFrecuentes = new ArrayList<PreguntasFrecuentes>();

	public void init() {
		preguntasFrecuentes = preguntasFrecuentesHome.getInstance();
		listaPreguntasFrecuentes = new ArrayList<PreguntasFrecuentes>();
		List<PreguntasFrecuentes> aux = new ArrayList<PreguntasFrecuentes>();

		aux = buscarBD();
		Integer cont = 0;
		Long idTipoActual = null;
		if (aux.size() > 0)
			idTipoActual = aux.get(0).getDatosEspecificos()
					.getIdDatosEspecificos();
		for (PreguntasFrecuentes obj : aux) {
			if (obj.getDatosEspecificos().getIdDatosEspecificos()
					.equals(idTipoActual)) {
				cont++;
				if (cont == 1) {
					obj.setMostrar(true);
					listaPreguntasFrecuentes.add(obj);
				} else {
					obj.setMostrar(false);
					listaPreguntasFrecuentes.add(obj);
				}
			} else {
				cont = 1;
				idTipoActual = obj.getDatosEspecificos()
						.getIdDatosEspecificos();
				obj.setMostrar(true);
				listaPreguntasFrecuentes.add(obj);
			}
		}

	}

	@SuppressWarnings("unchecked")
	private List<PreguntasFrecuentes> buscarBD() {
		String cadena = "select pf.* "
				+ "from seleccion.preguntas_frecuentes pf "
				+ "join seleccion.datos_especificos de on de.id_datos_especificos = pf.id_datos_especificos_tipo_preg "
				+ "where pf.activo is true and pf.publicar_s_n is true ";
		if (idTipoPregunta != null)
			cadena = cadena + " and pf.id_datos_especificos_tipo_preg = "
					+ idTipoPregunta;

		String cad = " order by pf.id_datos_especificos_tipo_preg, pf.nro_orden ";
		cadena = cadena + cad;
		List<PreguntasFrecuentes> lista = new ArrayList<PreguntasFrecuentes>();
		lista = em.createNativeQuery(cadena, PreguntasFrecuentes.class)
				.getResultList();
		return lista;
	}

	public void buscarPreguntasFrecuentes() {
		listaPreguntasFrecuentes = new ArrayList<PreguntasFrecuentes>();
		List<PreguntasFrecuentes> aux = new ArrayList<PreguntasFrecuentes>();
		aux = buscarBD();
		Integer cont = 0;
		Long idTipoActual = null;
		if (aux.size() > 0)
			idTipoActual = aux.get(0).getDatosEspecificos()
					.getIdDatosEspecificos();
		for (PreguntasFrecuentes obj : aux) {
			if (obj.getDatosEspecificos().getIdDatosEspecificos()
					.equals(idTipoActual)) {
				cont++;
				if (cont == 1) {
					obj.setMostrar(true);
					listaPreguntasFrecuentes.add(obj);
				} else {
					obj.setMostrar(false);
					listaPreguntasFrecuentes.add(obj);
				}
			} else {
				cont = 1;
				idTipoActual = obj.getDatosEspecificos()
						.getIdDatosEspecificos();
				obj.setMostrar(true);
				listaPreguntasFrecuentes.add(obj);
			}
		}
	}

	public void pdf() {
		try {

			Connection conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU368_Preguntas_Frec_Portal",
					obtenerMapaParametros(), false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private HashMap<String, Object> obtenerMapaParametros() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		if (usuarioLogueado != null)
			param.put("usuario", usuarioLogueado.getCodigoUsuario());

		StringBuffer sql = new StringBuffer();

		sql.append("select distinct(pf.id_datos_especificos_tipo_preg) as id, ");
		sql.append(" de.descripcion as tema ");
		sql.append(" from seleccion.preguntas_frecuentes pf ");
		sql.append(" join seleccion.datos_especificos de ");
		sql.append(" on de.id_datos_especificos = pf.id_datos_especificos_tipo_preg ");
		sql.append(" where pf.activo is true ");
		sql.append(" and pf.publicar_s_n is true ");

		if(idTipoPregunta != null)
			sql.append("  and  pf.id_datos_especificos_tipo_preg = "+idTipoPregunta);
		sql.append("  order by pf.id_datos_especificos_tipo_preg ");
		 param.put("sql", sql.toString());
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
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

	public List<PreguntasFrecuentes> getListaPreguntasFrecuentes() {
		return listaPreguntasFrecuentes;
	}

	public void setListaPreguntasFrecuentes(
			List<PreguntasFrecuentes> listaPreguntasFrecuentes) {
		this.listaPreguntasFrecuentes = listaPreguntasFrecuentes;
	}

}
