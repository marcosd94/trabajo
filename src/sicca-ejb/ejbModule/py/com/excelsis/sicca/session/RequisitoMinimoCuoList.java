package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.RequisitoMinimoCuo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("requisitoMinimoCuoList")
public class RequisitoMinimoCuoList extends EntityQuery<RequisitoMinimoCuo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7726910261157555089L;

	@In(value = "entityManager")
	EntityManager em;
	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<RequisitoMinimoCuo> listaRequisitoMinimoCuos;

	private static final String EJBQL =
		"select requisitoMinimoCuo from RequisitoMinimoCuo requisitoMinimoCuo";

	private static final String[] RESTRICTIONS =
		{
			"lower(requisitoMinimoCuo.descripcion) like lower(concat('%', concat(#{requisitoMinimoCuoList.requisitoMinimoCuo.descripcion},'%')))",
			"lower(requisitoMinimoCuo.usuAlta) like lower(concat(#{requisitoMinimoCuoList.requisitoMinimoCuo.usuAlta},'%'))",
			"lower(requisitoMinimoCuo.usuMod) like lower(concat(#{requisitoMinimoCuoList.requisitoMinimoCuo.usuMod},'%'))",
			"requisitoMinimoCuo.activo=#{requisitoMinimoCuoList.estado.valor}", };

	private static final String ORDER = "requisitoMinimoCuo.orden";
	private RequisitoMinimoCuo requisitoMinimoCuo = new RequisitoMinimoCuo();
	private Estado estado = Estado.ACTIVO;

	public RequisitoMinimoCuoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<RequisitoMinimoCuo> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<RequisitoMinimoCuo> limpiar() {
		requisitoMinimoCuo = new RequisitoMinimoCuo();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public void pdf() throws SQLException {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null)
				return;
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU123_Req_min", mapa, false, conn, new Usuario());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		 
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", "ADMIN");

		StringBuffer sql = new StringBuffer();

		sql.append("  select requisito.orden as orden,  ");
		sql.append(" requisito.descripcion as descripcion,   ");
		sql.append(" case when requisito.activo = 'f' ");
		sql.append("  then 'NO' else 'SI' end as activo  ");
		sql.append(" from planificacion.requisito_minimo_cuo requisito   ");
		sql.append("  where 1=1   ");
		if (requisitoMinimoCuo.getDescripcion() != null
			&& !requisitoMinimoCuo.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(requisitoMinimoCuo.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(requisito.descripcion) like '%"
				+ sufc.caracterInvalido(requisitoMinimoCuo.getDescripcion()).toLowerCase() + "%'");
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  requisito.activo = " + estado.getValor());
		}
		sql.append("   order by requisito.orden   ");
		param.put("sql", sql.toString());
		if (requisitoMinimoCuo.getDescripcion() != null)
			param.put("descripcion", !requisitoMinimoCuo.getDescripcion().equals("")
				? requisitoMinimoCuo.getDescripcion() : "TODOS");
		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	public RequisitoMinimoCuo getRequisitoMinimoCuo() {
		return requisitoMinimoCuo;
	}

	public List<RequisitoMinimoCuo> obtenerListaRequisitoMinimoCuos() {
		listaRequisitoMinimoCuos = getResultList();
		return listaRequisitoMinimoCuos;
	}

	public List<RequisitoMinimoCuo> getListaRequisitoMinimoCuos() {
		return listaRequisitoMinimoCuos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
