package py.com.excelsis.sicca.session;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.EscalaReqMin;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("escalaReqMinList")
public class EscalaReqMinList extends EntityQuery<EscalaReqMin> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7833699750536099068L;

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<EscalaReqMin> listaEscalaReqMins;

	private static final String EJBQL = "select escalaReqMin from EscalaReqMin escalaReqMin";

	private static final String[] RESTRICTIONS =
		{
			"lower(escalaReqMin.descripcion) like lower(concat('%', concat(#{escalaReqMinList.escalaReqMin.descripcion},'%')))",
			"lower(escalaReqMin.usuAlta) like lower(concat(#{escalaReqMinList.escalaReqMin.usuAlta},'%'))",
			"lower(escalaReqMin.usuMod) like lower(concat(#{escalaReqMinList.escalaReqMin.usuMod},'%'))",
			"escalaReqMin.activo=#{escalaReqMinList.estado.valor}",
			"escalaReqMin.requisitoMinimoCpt.idRequisitoMinimoCpt=#{escalaReqMinList.idRequisitoMinCpt}", };
	private static final String ORDER =
		"escalaReqMin.requisitoMinimoCpt.descripcion, escalaReqMin.desde, escalaReqMin.hasta";
	private EscalaReqMin escalaReqMin = new EscalaReqMin();
	private Estado estado = Estado.ACTIVO;
	private Long idRequisitoMinCpt;

	public EscalaReqMinList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EscalaReqMin> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<EscalaReqMin> limpiar() {
		escalaReqMin = new EscalaReqMin();
		estado = Estado.ACTIVO;
		idRequisitoMinCpt = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public void print() {
		Connection conn = null;
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			Map<String, Object> param = new HashMap<String, Object>();

			StringBuffer whereClause = new StringBuffer();
			whereClause.append(" where r.activo is true ");

			if (escalaReqMin.getDescripcion() != null && !escalaReqMin.getDescripcion().isEmpty()) {
				if (!sufc.validarInput(escalaReqMin.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				whereClause.append(" and lower(esc.descripcion) like '%"
					+ sufc.caracterInvalido(escalaReqMin.getDescripcion().trim()) + "%' ");
			}

			if (idRequisitoMinCpt != null) {
				if (!sufc.validarInput(idRequisitoMinCpt.toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
				whereClause.append(" and esc.id_requisito_minimo_cpt = " + idRequisitoMinCpt);
			}

			if (estado != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return;
				}
				whereClause.append(" and esc.activo = " + estado.getValor());
			}

			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("ListarReqMinimosUC172", param, false, conn, usuarioLogueado);

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null)
				JpaResourceBean.closeConnection(conn);
		}
	}

	public EscalaReqMin getEscalaReqMin() {
		return escalaReqMin;
	}

	public List<EscalaReqMin> obtenerListaEscalaReqMins() {
		listaEscalaReqMins = getResultList();
		return listaEscalaReqMins;
	}

	public List<EscalaReqMin> getListaEscalaReqMins() {
		return listaEscalaReqMins;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdRequisitoMinCpt() {
		return idRequisitoMinCpt;
	}

	public void setIdRequisitoMinCpt(Long idRequisitoMinCpt) {
		this.idRequisitoMinCpt = idRequisitoMinCpt;
	}

}
