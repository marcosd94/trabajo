package py.com.excelsis.sicca.session.util;

import java.io.Serializable;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JpaResourceBean;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import py.com.excelsis.sicca.util.JasperReportUtils;

@Scope(ScopeType.PAGE)
@Name("reportUtilFormController")
public class ReportUtilFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -979560663262546380L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	private String nombreReporte = null;
	private HashMap<String, Object> param = new HashMap<String, Object>();

	public void init() {
		nombreReporte = null;
		param = obtenerMapaParametros();
	}

	public void imprimirReportePdf() {
		Connection conn = null;
		try {
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF(nombreReporte, param, false, conn, usuarioLogueado);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
		}
	}

	public void imprimirReporteXLS() {

		try {
			Connection conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondXLS(nombreReporte, param, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generarReporteXLS(String url,String prefijo) {

		try {
			Connection conn = JpaResourceBean.getConnection();
			JasperReportUtils.generarXLS(nombreReporte, param, conn, usuarioLogueado,url,prefijo);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void imprimirImagen() {
		try {
			JasperReportUtils.respondImage(nombreReporte, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		if (usuarioLogueado != null) {
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
		} else {
			param.put("usuario", "");
		}
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));

		return param;
	}

	public String getNombreReporte() {
		return nombreReporte;
	}

	public void setNombreReporte(String nombreReporte) {
		this.nombreReporte = nombreReporte;
	}

	public HashMap<String, Object> getParametros() {
		return param;
	}

	public void setParametros(HashMap<String, Object> param) {
		this.param = param;
	}

}
