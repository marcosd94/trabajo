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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.AgrupamientoCce;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("agrupamientoCceList")
public class AgrupamientoCceList extends EntityQuery<AgrupamientoCce> {

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<AgrupamientoCce> listaNivelGradoSalarials;

	private static final String EJBQL =
		"select agrupamientoCce from AgrupamientoCce agrupamientoCce";

	private static final String[] RESTRICTIONS =
		{
			"agrupamientoCce.numero = #{agrupamientoCceList.agrupamientoCce.numero}",
			"lower(agrupamientoCce.descripcion) like lower(concat(#{agrupamientoCceList.agrupamientoCce.descripcion},'%'))",
			"agrupamientoCce.activo = #{agrupamientoCceList.estado.valor}",
			"agrupamientoCce.tipoCce.idTipoCce =#{agrupamientoCceList.idTipoCce}", };
	private static final String ORDER = "agrupamientoCce.numero";
	private static final String ORDER2 = "agrupamientoCce.numero desc";

	private Estado estado= Estado.ACTIVO;
	private Long idTipoCce;

	private AgrupamientoCce agrupamientoCce = new AgrupamientoCce();

	public AgrupamientoCceList() {
		setEjbql(EJBQL);
		if (agrupamientoCce.getDescripcion() != null)
			agrupamientoCce.setDescripcion(seguridadUtilFormController.caracterInvalido(agrupamientoCce.getDescripcion()));
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<AgrupamientoCce> buscarResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList();
	}

	public List<AgrupamientoCce> limpiarResultados() {
		agrupamientoCce = new AgrupamientoCce();
		estado = Estado.ACTIVO;
		idTipoCce = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList();
	}

	public void print() {
		Connection conn = null;
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		try {
			Map<String, Object> param = new HashMap<String, Object>();

			StringBuffer whereClause = new StringBuffer();
			if (idTipoCce != null) {
				if (!sufc.validarInput(idTipoCce.toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "tc.id_tipo_cce = " + idTipoCce);
			}

			if (agrupamientoCce.getNumero() != null) {
				if (!sufc.validarInput(agrupamientoCce.getNumero().toString(), TiposDatos.INTEGER.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "a.numero = " + agrupamientoCce.getNumero());
			}
			if (agrupamientoCce.getDescripcion() != null
				&& !agrupamientoCce.getDescripcion().isEmpty()) {
				if (!sufc.validarInput(agrupamientoCce.getDescripcion(), TiposDatos.STRING.getValor(), 200)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "lower(a.descripcion) like '%"
					+ agrupamientoCce.getDescripcion().trim().toLowerCase() + "%' ");
			}
			if (estado.getValor() != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "a.activo = " + estado.getValor());
			}

			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());

			conn = JpaResourceBean.getConnection();

			JasperReportUtils.respondPDF("AgrupamientoCceUC205", param, false, conn, usuarioLogueado);

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			JpaResourceBean.closeConnection(conn);
		}
	}

	public List<AgrupamientoCce> listaTodos() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(null);
		setOrder(ORDER2);

		return getResultList();
	}

	// GETTERS Y SETTERS

	public List<AgrupamientoCce> obtenerListaNivelGradoSalarials() {
		listaNivelGradoSalarials = getResultList();
		return listaNivelGradoSalarials;
	}

	public AgrupamientoCce getAgrupamientoCce() {
		return agrupamientoCce;
	}

	public List<AgrupamientoCce> getListaNivelGradoSalarials() {
		return listaNivelGradoSalarials;
	}

	

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdTipoCce() {
		return idTipoCce;
	}

	public void setIdTipoCce(Long idTipoCce) {
		this.idTipoCce = idTipoCce;
	}

}
