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

import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("departamentoList")
public class DepartamentoList extends EntityQuery<Departamento> {

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<Departamento> listaDepartamentos;

	private static final String EJBQL = "select departamento from Departamento departamento";

	private static final String[] RESTRICTIONS =
		{
			"lower(departamento.descripcion) like lower(concat('%',concat(#{departamentoList.departamento.descripcion},'%')))",
			"departamento.activo = #{departamentoList.estado.valor}",
			"departamento.pais.idPais = #{departamentoList.pais.idPais}", };
	private static final String ORDER = "departamento.descripcion";
	private Estado estado = Estado.ACTIVO;

	private Departamento departamento = new Departamento();
	private Pais pais = new Pais();

	public DepartamentoList() {
		departamento.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}

	public List<Departamento> litarPorPais() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(null);
		return getResultList();
	}

	public List<Departamento> buscarResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<Departamento> limpiarResultados() {
		departamento = new Departamento();
		estado = Estado.ACTIVO;
		departamento.setActivo(Estado.ACTIVO.getValor());
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
			if (pais.getIdPais() != null) {
				if (!sufc.validarInput(pais.getIdPais().toString(), TiposDatos.LONG.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "p.id_pais = " + pais.getIdPais());
			}
			if (departamento.getDescripcion() != null && !departamento.getDescripcion().isEmpty()) {
				if (!sufc.validarInput(departamento.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "lower(d.descripcion) like '%"
					+ sufc.caracterInvalido(departamento.getDescripcion().trim()).toLowerCase() + "%' ");
			}
			if (estado.getValor() != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "d.activo = " + estado.getValor());
			}

			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());

			conn = JpaResourceBean.getConnection();

			JasperReportUtils.respondPDF("ListarDepartamentosUC102", param, false, conn, usuarioLogueado);

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null)
				JpaResourceBean.closeConnection(conn);
		}
	}

	// GETTERS Y SETTERS
	public Departamento getDepartamento() {
		return departamento;
	}

	public Pais getPais() {
		return pais;
	}

	public List<Departamento> obtenerListaDepartamentos() {
		listaDepartamentos = getResultList();
		return listaDepartamentos;
	}

	public List<Departamento> getListaDepartamentos() {
		return listaDepartamentos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
