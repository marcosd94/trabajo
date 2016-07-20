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

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("condicionTrabajoEspecifList")
public class CondicionTrabajoEspecifList extends EntityQuery<CondicionTrabajoEspecif> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<CondicionTrabajoEspecif> listaCondicionTrabajoEspecifs;

	@In(required = false)
	Usuario usuarioLogueado;

	private static final String EJBQL =
		"select condicionTrabajoEspecif from CondicionTrabajoEspecif condicionTrabajoEspecif";

	private static final String[] RESTRICTIONS =
		{
			"lower(condicionTrabajoEspecif.tipo) like lower(concat('%', concat(#{condicionTrabajoEspecifList.condicionTrabajoEspecif.tipo},'%')))",
			"lower(condicionTrabajoEspecif.descripcion) like lower(concat('%', concat(#{condicionTrabajoEspecifList.condicionTrabajoEspecif.descripcion},'%')))",
			"condicionTrabajoEspecif.activo = #{condicionTrabajoEspecifList.estado.valor}", };
	private static final String ORDER = "condicionTrabajoEspecif.orden";

	private CondicionTrabajoEspecif condicionTrabajoEspecif = new CondicionTrabajoEspecif();
	private Estado estado = Estado.ACTIVO;

	public CondicionTrabajoEspecifList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<CondicionTrabajoEspecif> buscarResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<CondicionTrabajoEspecif> limpiarResultados() {
		condicionTrabajoEspecif = new CondicionTrabajoEspecif();
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
			JasperReportUtils.respondPDF("RPT_CU176_listar_esc_condic_trab_especf", mapa, false, conn, usuarioLogueado);

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

		sql.append("select cond.tipo as tipo, ");
		sql.append(" cond.descripcion as descripcion ");
		sql.append("  from planificacion.condicion_trabajo_especif cond ");

		sql.append("  where 1=1 ");
		if (condicionTrabajoEspecif.getTipo() != null
			&& !condicionTrabajoEspecif.getTipo().trim().isEmpty()) {
			if (!sufc.validarInput(condicionTrabajoEspecif.getTipo(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(cond.tipo) like '%"
				+ sufc.caracterInvalido(condicionTrabajoEspecif.getTipo()).toLowerCase() + "%'");
		}
		if (condicionTrabajoEspecif.getDescripcion() != null
			&& !condicionTrabajoEspecif.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(condicionTrabajoEspecif.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(cond.descripcion) like '%"
				+ sufc.caracterInvalido(condicionTrabajoEspecif.getDescripcion()).toLowerCase()
				+ "%'");
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  cond.activo = " + estado.getValor());
		}

		sql.append("   order by cond.orden   ");
		param.put("sql", sql.toString());
		if (condicionTrabajoEspecif.getDescripcion() != null)
			param.put("descripcion", !condicionTrabajoEspecif.getDescripcion().equals("")
				? condicionTrabajoEspecif.getDescripcion() : "TODOS");
		if (estado.getValor() == null)
			param.put("estado", "TODOS");
		if (estado.getValor() != null)
			param.put("estado", estado.getDescripcion().toUpperCase());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	// GETTERS Y SETTERS
	public CondicionTrabajoEspecif getCondicionTrabajoEspecif() {
		return condicionTrabajoEspecif;
	}

	public List<CondicionTrabajoEspecif> obtenerListaCondicionTrabajoEspecifs() {
		listaCondicionTrabajoEspecifs = getResultList();
		return listaCondicionTrabajoEspecifs;
	}

	public List<CondicionTrabajoEspecif> getListaCondicionTrabajoEspecifs() {
		return listaCondicionTrabajoEspecifs;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
