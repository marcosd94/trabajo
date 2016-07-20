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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("paisList")
public class PaisList extends EntityQuery<Pais> {

	
	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<Pais> listaPaiss;

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false)
	StatusMessages statusMessages;
	private static final String EJBQL = "select pais from Pais pais";

	private static final String[] RESTRICTIONS = {
		"lower(pais.descripcion) like concat('%',lower(concat(#{paisList.pais.descripcion},'%')))",
		"lower(pais.usuAlta) like lower(concat(#{paisList.pais.usuAlta},'%'))",
		"lower(pais.usuMod) like lower(concat(#{paisList.pais.usuMod},'%'))",
		" pais.activo=#{paisList.estado.valor}", };
	private static final String[] RESTRICTIONS_SEARCH = {
		"lower(pais.descripcion) like concat('%',lower(concat(#{paisList.pais.descripcion},'%')))",
		" pais.activo=#{paisList.estado.valor}", };

	private Pais pais = new Pais();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "pais.descripcion";

	public PaisList() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Pais> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<Pais> limpiar() {
		pais = new Pais();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public Pais searchPais() {
		pais.setDescripcion("PARAGUAY");
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_SEARCH));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		if (getResultList().size() > 0)
			return getResultList().get(0);
		return null;
	}

	public void pdf() throws SQLException {
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				return;
			}else
				statusMessages.clear();
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU100_listar_pais", mapa, false, conn, usuarioLogueado);

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

		sql.append(" Select p.descripcion as descripcion, p.pais_codigo_sinarh as cod ,   ");
		sql.append(" case when p.activo = 'f'   then 'NO' else 'SI' end as activo, p.prefijo as codInt  ");
		sql.append("  from   general.pais p ");
		sql.append("  where 1=1   ");
		if (pais.getDescripcion() != null && !pais.getDescripcion().trim().isEmpty()) {
			if (!sufc.validarInput(pais.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			sql.append("  and  lower(p.descripcion) like '%"
				+ sufc.caracterInvalido(pais.getDescripcion()).toLowerCase() + "%'");
		}

		if (estado.getValor() != null) {
			if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
				return null;
			}
			sql.append("  and  p.activo = " + estado.getValor());
		}

		sql.append("   order by p.descripcion   ");
		param.put("sql", sql.toString());
		if (pais.getDescripcion() != null)
			param.put("descripcion", !pais.getDescripcion().equals("") ? pais.getDescripcion()
				: "TODOS");
		param.put("estado", estado.getDescripcion());
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	// GETTERS Y SETTERS
	public Pais getPais() {
		return pais;
	}

	public List<Pais> obtenerListaPaiss() {
		listaPaiss = getResultList();
		return listaPaiss;
	}

	public List<Pais> getListaPaiss() {
		return listaPaiss;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
