package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.RequisitoMinimoCptList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("requisitoMinimoCptListFormController")
public class RequisitoMinimoCptListFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8056907183696879087L;

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	RequisitoMinimoCptList requisitoMinimoCptList;

	private RequisitoMinimoCpt requisitoMinimoCpt = new RequisitoMinimoCpt();
	private List<RequisitoMinimoCpt> listResult = new ArrayList<RequisitoMinimoCpt>();
	private Estado estado = Estado.ACTIVO;

	public void init() {
		search();
	}

	public void search() {
		requisitoMinimoCptList.setActivo(estado.getValor());
		requisitoMinimoCptList.getRequisitoMinimoCpt().setDescripcion(requisitoMinimoCpt.getDescripcion() != null
			? requisitoMinimoCpt.getDescripcion().trim() : null);
		requisitoMinimoCptList.setOrder("requisitoMinimoCpt.orden");
		listResult = requisitoMinimoCptList.buscarResultados();
	}

	public void searchAll() {
		requisitoMinimoCpt = new RequisitoMinimoCpt();
		estado = Estado.ACTIVO;
		requisitoMinimoCpt.setActivo(true);
		search();
	}

	public void print() {
		Connection conn = null;
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		try {
			Map<String, Object> param = new HashMap<String, Object>();

			StringBuffer whereClause = new StringBuffer();
			whereClause.append(" where esc.activo is true ");

			if (requisitoMinimoCpt.getDescripcion() != null
				&& !requisitoMinimoCpt.getDescripcion().isEmpty()) {
				if (!sufc.validarInput(requisitoMinimoCpt.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				whereClause.append(" and lower(r.descripcion) like '%"
					+ sufc.caracterInvalido(requisitoMinimoCpt.getDescripcion().trim()).toLowerCase()
					+ "%' ");
			}

			if (estado.getValor() != null) {
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return;
				}
				whereClause.append(" and r.activo = " + estado.getValor());
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

	// GETTERS Y SETTERS
	public RequisitoMinimoCpt getRequisitoMinimoCpt() {
		return requisitoMinimoCpt;
	}

	public void setRequisitoMinimoCpt(RequisitoMinimoCpt requisitoMinimoCpt) {
		this.requisitoMinimoCpt = requisitoMinimoCpt;
	}

	public List<RequisitoMinimoCpt> getListResult() {
		return listResult;
	}

	public void setListResult(List<RequisitoMinimoCpt> listResult) {
		this.listResult = listResult;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
