package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.EscalaCondSegur;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("escalaCondSegurList")
public class EscalaCondSegurList extends EntityQuery<EscalaCondSegur> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<EscalaCondSegur> listaEscalaCondSegurs;

	@In(required = false)
	Usuario usuarioLogueado;

	private static final String EJBQL =
		"select escalaCondSegur from EscalaCondSegur escalaCondSegur";

	private static final String[] RESTRICTIONS =
		{
			"lower(escalaCondSegur.descripcion) like lower(concat('%',concat(#{escalaCondSegurList.escalaCondSegur.descripcion},'%')))",
			"lower(escalaCondSegur.usuAlta) like lower(concat(#{escalaCondSegurList.escalaCondSegur.usuAlta},'%'))",
			"lower(escalaCondSegur.usuMod) like lower(concat(#{escalaCondSegurList.escalaCondSegur.usuMod},'%'))",
			"escalaCondSegur.activo=#{escalaCondSegurList.estado.valor}", };

	private EscalaCondSegur escalaCondSegur = new EscalaCondSegur();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "escalaCondSegur.descripcion";

	public EscalaCondSegurList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EscalaCondSegur> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<EscalaCondSegur> limpiar() {
		escalaCondSegur = new EscalaCondSegur();
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
			JasperReportUtils.respondPDF("RPT_CU187_listar_escala_con_seg", mapa, false, conn, usuarioLogueado);
			 
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
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		StringBuffer sql = new StringBuffer();

		sql.append(" select e.descripcion as descripcion , e.desde as desde, e.hasta as hasta,   ");
		sql.append("case when e.activo = 'f'   then 'NO' else 'SI' end as activo   ");
		sql.append("  from  planificacion.escala_cond_segur e ");
		sql.append("  where 1=1   ");
		if (escalaCondSegur.getDescripcion() != null
			&& !escalaCondSegur.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(escalaCondSegur.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(e.descripcion) like "
				+ sufc.caracterInvalido(escalaCondSegur.getDescripcion()).toLowerCase() + "%");
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  e.activo = " + estado.getValor());
		}

		sql.append("   order by e.descripcion   ");
		param.put("sql", sql.toString());
		if (escalaCondSegur.getDescripcion() != null)
			param.put("descripcion", !escalaCondSegur.getDescripcion().equals("")
				? escalaCondSegur.getDescripcion().toUpperCase() : "TODOS");
		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	public EscalaCondSegur getEscalaCondSegur() {
		return escalaCondSegur;
	}

	public List<EscalaCondSegur> obtenerListaEscalaCondSegurs() {
		listaEscalaCondSegurs = getResultList();
		return listaEscalaCondSegurs;
	}

	public List<EscalaCondSegur> getListaEscalaCondSegurs() {
		return listaEscalaCondSegurs;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
