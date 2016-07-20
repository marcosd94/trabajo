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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("especialidadesList")
public class EspecialidadesList extends EntityQuery<Especialidades> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<Especialidades> listaEspecialidades;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private static final String EJBQL = "select especialidades from Especialidades especialidades";

	private static final String[] RESTRICTIONS =
		{
			"lower(especialidades.descripcion) like concat('%',lower(concat(#{especialidadesList.especialidades.descripcion},'%')))",
			"lower(especialidades.usuAlta) like lower(concat(#{especialidadesList.especialidades.usuAlta},'%'))",
			"lower(especialidades.usuMod) like lower(concat(#{especialidadesList.especialidades.usuMod},'%'))",
			" especialidades.activo=#{especialidadesList.estado.valor}", };

	private Especialidades especialidades = new Especialidades();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "especialidades.descripcion";

	public EspecialidadesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Especialidades> listaResultados() {
		seguridadUtilFormController.caracterInvalido(especialidades.getDescripcion());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<Especialidades> limpiar() {
		especialidades = new Especialidades();
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
			JasperReportUtils.respondPDF("RPT_CU207_listar_especialidades", obtenerMapaParametros(), false, conn, usuarioLogueado);
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

		sql.append("select especialidades.descripcion as descripcion, ");
		sql.append(" case when especialidades.activo = 't' then 'SI' else 'NO' end as estado ");
		sql.append(" from planificacion.especialidades especialidades ");

		sql.append("  where 1=1 ");
		if (especialidades.getDescripcion() != null
			&& !especialidades.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(especialidades.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(especialidades.descripcion) like '%"
				+ sufc.caracterInvalido(especialidades.getDescripcion()).toLowerCase() + "%'");
		}
		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  especialidades.activo = " + estado.getValor());
		}
		sql.append("   order by especialidades.descripcion ");
		param.put("sql", sql.toString());
		if (especialidades.getDescripcion() != null)
			param.put("descripcion", !especialidades.getDescripcion().equals("")
				? especialidades.getDescripcion() : "TODOS");
		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	// GETTERS Y SETTERS
	public Especialidades getEspecialidades() {
		return especialidades;
	}

	public List<Especialidades> obtenerListaEspecialidades() {
		listaEspecialidades = getResultList();
		return listaEspecialidades;
	}

	public List<Especialidades> getListaTipoPuestos() {
		return listaEspecialidades;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
