package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
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

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("listarEntidadesRptController")
public class ListarEntidadesRptController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7965587989810914293L;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;

	private SinEntidad entidad;
	private SinNivelEntidad nivelEntidad;

	public void init() {
		clearAll();
	}

	public void print() {
		Connection conn = null;
		try {
			SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			Map<String, Object> param = new HashMap<String, Object>();
			String estadoParam = "Todos";

			StringBuffer whereClause = new StringBuffer();
			if (entidad.getAniAniopre() != null) {
				if (!sufc.validarInput(entidad.getAniAniopre().toString(), TiposDatos.INTEGER.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "se.ani_aniopre = " + entidad.getAniAniopre());
				param.put("anho", entidad.getAniAniopre().intValue());
			}
			if (nivelEntidad.getNenCodigo() != null) {
				if (!sufc.validarInput(nivelEntidad.getNenCodigo().toString(), TiposDatos.BIGDECIMAL.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "ne.nen_codigo = " + nivelEntidad.getNenCodigo());
				param.put("codigoNivel", nivelEntidad.getNenCodigo().intValue());
			}
			if (nivelEntidad.getNenNombre() != null && !nivelEntidad.getNenNombre().isEmpty()) {
				if (!sufc.validarInput(nivelEntidad.getNenNombre().trim(), TiposDatos.STRING.getValor(), null)) {
					return;
				}
				whereClause.append((whereClause.toString().contains("WHERE") ? " AND " : " WHERE ")
					+ "lower(ne.nen_nombre) like '%"
					+ sufc.caracterInvalido(nivelEntidad.getNenNombre().trim()).toLowerCase()
					+ "%' ");
				param.put("nivelEntidad", nivelEntidad.getNenNombre().trim().toUpperCase());
			}

			ServletContext servletContext =
				(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("estado", estadoParam);

			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("ListarEntidadesUC039", param, false, conn, usuarioLogueado);

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
		entidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		entidad.setAniAniopre(new BigDecimal(cal.get(Calendar.YEAR)));
		statusMessages.clear();
	}

	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nivelEntidad.getNenCodigo());
			SinNivelEntidad nivel = sinNivelEntidadList.nivelEntidadMaxAnho();
			nivelEntidad = nivel != null ? nivel : new SinNivelEntidad();
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	// GETTERS Y SETTERS
	public SinEntidad getEntidad() {
		return entidad;
	}

	public void setEntidad(SinEntidad entidad) {
		this.entidad = entidad;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}
}
