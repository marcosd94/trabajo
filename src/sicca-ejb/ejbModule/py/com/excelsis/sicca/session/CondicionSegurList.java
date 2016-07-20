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
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("condicionSegurList")
public class CondicionSegurList extends EntityQuery<CondicionSegur> {

	@In(create = true)
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<CondicionSegur> listaCondicionSegurs;

	private static final String EJBQL = "select condicionSegur from CondicionSegur condicionSegur";

	private static final String[] RESTRICTIONS =
		{
			"lower(condicionSegur.descripcion) like lower(concat('%',(concat(#{condicionSegurList.condicionSegur.descripcion},'%'))))",
			"lower(condicionSegur.usuAlta) like lower(concat(#{condicionSegurList.condicionSegur.usuAlta},'%'))",
			"lower(condicionSegur.usuMod) like lower(concat(#{condicionSegurList.condicionSegur.usuMod},'%'))",
			" condicionSegur.activo=#{condicionSegurList.estado.valor}", };

	private CondicionSegur condicionSegur = new CondicionSegur();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "condicionSegur.orden asc";

	public CondicionSegurList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CondicionSegur> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<CondicionSegur> limpiar() {
		condicionSegur = new CondicionSegur();
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
			JasperReportUtils.respondPDF("RPT_CU187_listar_escala_con_seg", obtenerMapaParametros(), false, conn, usuarioLogueado);
			conn.close();
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

		sql.append(" Select cs.descripcion as descripcion , cs.orden as orden,    ");
		sql.append(" case when cs.activo = 'f'   then 'NO' else 'SI' end as activo   ");
		sql.append("  from planificacion.condicion_segur cs ");
		sql.append("  where 1=1   ");
		if (condicionSegur.getDescripcion() != null
			&& !condicionSegur.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(condicionSegur.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(cs.descripcion) like '%"
				+ sufc.caracterInvalido(condicionSegur.getDescripcion()).toLowerCase() + "%'");
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  cs.activo = " + estado.getValor());
		}

		sql.append("   order by cs.orden asc  ");
		param.put("sql", sql.toString());
		if (condicionSegur.getDescripcion() != null)
			param.put("descripcion", !condicionSegur.getDescripcion().equals("")
				? condicionSegur.getDescripcion() : "TODOS");

		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	// GETTERS Y SETTERS
	public CondicionSegur getCondicionSegur() {
		return condicionSegur;
	}

	public List<CondicionSegur> obtenerListaCondicionSegurs() {
		listaCondicionSegurs = getResultList();
		return listaCondicionSegurs;
	}

	public List<CondicionSegur> getListaCondicionSegurs() {
		return listaCondicionSegurs;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
