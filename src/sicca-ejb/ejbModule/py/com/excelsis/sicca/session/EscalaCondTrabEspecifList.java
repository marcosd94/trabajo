package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

import py.com.excelsis.sicca.dto.ValoracionTab05CPT;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("escalaCondTrabEspecifList")
public class EscalaCondTrabEspecifList extends EntityQuery<EscalaCondTrabEspecif> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3880601403514909590L;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<EscalaCondTrabEspecif> listaEscalaCondTrabEspecifs;
	@In(required = false)
	Usuario usuarioLogueado;

	private static final String EJBQL =
		"select escalaCondTrabEspecif from EscalaCondTrabEspecif escalaCondTrabEspecif";

	private static final String[] RESTRICTIONS =
		{
			"lower(escalaCondTrabEspecif.descripcion) like lower(concat('%', concat(#{escalaCondTrabEspecifList.escalaCondTrabEspecif.descripcion},'%')))",
			"lower(escalaCondTrabEspecif.usuAlta) like lower(concat(#{escalaCondTrabEspecifList.escalaCondTrabEspecif.usuAlta},'%'))",
			"lower(escalaCondTrabEspecif.usuMod) like lower(concat(#{escalaCondTrabEspecifList.escalaCondTrabEspecif.usuMod},'%'))",
			"escalaCondTrabEspecif.activo=#{escalaCondTrabEspecifList.estado.valor}",

		};

	private static final String ORDER =
		"escalaCondTrabEspecif.desde asc, escalaCondTrabEspecif.hasta asc";
	private EscalaCondTrabEspecif escalaCondTrabEspecif = new EscalaCondTrabEspecif();
	private Estado estado = Estado.ACTIVO;

	public EscalaCondTrabEspecifList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EscalaCondTrabEspecif> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<EscalaCondTrabEspecif> limpiar() {
		escalaCondTrabEspecif = new EscalaCondTrabEspecif();
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
			conn = JpaResourceBean.getConnection();
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null)
				return;
			JasperReportUtils.respondPDF("RPT_CU176_listar_esc_condic_trab_especf", mapa, false, conn, usuarioLogueado);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
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
		if (escalaCondTrabEspecif.getDescripcion() != null
			&& !escalaCondTrabEspecif.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(escalaCondTrabEspecif.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(cond.descripcion) like '%"
				+ sufc.caracterInvalido(escalaCondTrabEspecif.getDescripcion()).toLowerCase()
				+ "%'");
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  cond.activo = " + estado.getValor());
		}
		sql.append("   order by cond.orden   ");
		param.put("sql", sql.toString());
		if (escalaCondTrabEspecif.getDescripcion() != null)
			param.put("descripcion", !escalaCondTrabEspecif.getDescripcion().equals("")
				? escalaCondTrabEspecif.getDescripcion() : "TODOS");
		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	public EscalaCondTrabEspecif getEscalaCondTrabEspecif() {
		return escalaCondTrabEspecif;
	}

	public List<EscalaCondTrabEspecif> obtenerListaEscalaCondTrabEspecifs() {
		listaEscalaCondTrabEspecifs = getResultList();
		return listaEscalaCondTrabEspecifs;
	}

	public List<EscalaCondTrabEspecif> getListaEscalaCondTrabEspecifs() {
		return listaEscalaCondTrabEspecifs;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
