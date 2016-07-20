package py.com.excelsis.sicca.reportes.form;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("rptDiscapacitadosEntidadFC")
public class RptDiscapacitadosEntidadFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private String nombre;
	private String apellido;
	private String documento;

	public void init() {

	}

	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		setNombre(null);
		setApellido(null);
		setDocumento(null);
	}

	private String cargarParametros() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		nivelEntidadOeeUtil.init();
		reportUtilFormController.getParametros().put("nivel", nivelEntidadOeeUtil.getCodNivelEntidad()
			+ " - " + nivelEntidadOeeUtil.getNombreNivelEntidad());
		reportUtilFormController.getParametros().put("nivelOrden", nivelEntidadOeeUtil.getCodNivelEntidad().toString());
		reportUtilFormController.getParametros().put("idEntidad", new BigDecimal(nivelEntidadOeeUtil.getIdSinEntidad()));
		reportUtilFormController.getParametros().put("entidadOrden", nivelEntidadOeeUtil.getCodSinEntidad().toString());
		reportUtilFormController.getParametros().put("entidad", nivelEntidadOeeUtil.getCodSinEntidad().toString()
			+ " - " + nivelEntidadOeeUtil.getNombreSinEntidad());
		reportUtilFormController.getParametros().put("idOEE", nivelEntidadOeeUtil.getIdConfigCab() == null
			? null : new BigDecimal(nivelEntidadOeeUtil.getIdConfigCab()));
		reportUtilFormController.getParametros().put("idUO", nivelEntidadOeeUtil.getIdUnidadOrganizativa() == null
			? null : new BigDecimal(nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
		reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());

		String nombre = "%";
		if (this.nombre != null && !"".equals(this.nombre)) {
			if (!sufc.validarInput(this.nombre, TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			nombre += this.nombre + "%";
		}

		String apellido = "%";
		if (this.apellido != null && !"".equals(this.apellido)) {
			if (!sufc.validarInput(this.apellido, TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			apellido += this.apellido + "%";
		}

		String documento = "%";
		if (this.documento != null && !"".equals(this.documento)) {
			if (!sufc.validarInput(this.documento, TiposDatos.STRING.getValor(), null)) {
				return null;
			}
			documento += this.documento + "%";
		}

		reportUtilFormController.getParametros().put("nombre", nombre.toUpperCase());
		reportUtilFormController.getParametros().put("apellido", apellido.toUpperCase());
		reportUtilFormController.getParametros().put("documento", documento.toUpperCase());
		return "OK";
	}

	private Boolean validar() {
		if (nivelEntidadOeeUtil.getCodNivelEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		} else if (nivelEntidadOeeUtil.getCodSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		}
		return true;
	}

	public void imprimir() throws Exception {
		if (validar()) {
			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.setNombreReporte("RPT_CU152_discapacitados_entidad");
			if (cargarParametros() == null) {
				return;
			}
			reportUtilFormController.imprimirReportePdf();
		}
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getApellido() {
		return apellido;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getDocumento() {
		return documento;
	}

}
