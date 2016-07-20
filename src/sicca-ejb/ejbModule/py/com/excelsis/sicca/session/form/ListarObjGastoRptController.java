package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

import enums.OrdenListadoObj;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("listarObjGastoRptController")
public class ListarObjGastoRptController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7829495553593473967L;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	private SinObj objetoGasto;
	private String ordenarPor = null;

	public void init() {
		clearAll();
	}

	public void print() {
		Connection conn = null;
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			Map<String, Object> param = new HashMap<String, Object>();
			String estado = "Todos";
			String order = "ORDER BY og." + ordenarPor;

			StringBuffer whereClause = new StringBuffer();

			if (objetoGasto.getAniAniopre() != null) {
				if (!sufc.validarInput(objetoGasto.getAniAniopre().toString(), TiposDatos.INTEGER.getValor(), null)) {
					return ;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "og.ani_aniopre = " + objetoGasto.getAniAniopre());
				param.put("anho", objetoGasto.getAniAniopre());
			}
			if (objetoGasto.getObjNombre() != null && !objetoGasto.getObjNombre().isEmpty()) {
				if (!sufc.validarInput(objetoGasto.getObjNombre().trim(), TiposDatos.STRING.getValor(), null)) {
					return ;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "lower(og.obj_nombre) like '%"
					+ sufc.caracterInvalido(objetoGasto.getObjNombre().trim()).toLowerCase() + "%' ");
				param.put("descripcion", "'" + objetoGasto.getObjNombre().trim().toUpperCase()
					+ "'");
			}

			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("order", order);
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("estado", estado);

			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("ListarObjetoGastoUC025", param, false, conn, usuarioLogueado);

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null) {
				JpaResourceBean.closeConnection(conn);
			}
		}
	}

	public void clearAll() {
		Calendar cal = new GregorianCalendar();
		objetoGasto = new SinObj();
		ordenarPor = OrdenListadoObj.ANHO.getId();
		objetoGasto.setAniAniopre(cal.get(Calendar.YEAR));
	}

	// GETTERS Y SETTERS
	public SinObj getObjetoGasto() {
		return objetoGasto;
	}

	public void setObjetoGasto(SinObj objetoGasto) {
		this.objetoGasto = objetoGasto;
	}

	public String getOrdenarPor() {
		return ordenarPor;
	}

	public void setOrdenarPor(String ordenarPor) {
		this.ordenarPor = ordenarPor;
	}

}
