package py.com.excelsis.sicca.reportes.form;

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
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("cantPersonaSexoFC")
public class CantPersonaSexoFC {
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
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;


	public void init() {

	}
	
	
	public void limpiar(){
		nivelEntidadOeeUtil.limpiar();
	}
	
	
	private void cargarParametros() {
		try{
			grupoPuestosController = (GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);

			String codigoNivel = nivelEntidadOeeUtil.getCodNivelEntidad().toString();
			String codigoEntidad = codigoNivel + "." + nivelEntidadOeeUtil.getCodSinEntidad().toString();
			String codigoOEE = codigoEntidad + "." + nivelEntidadOeeUtil.getOrdenUnidOrg().toString();
			
			reportUtilFormController.getParametros().put("nivel",  codigoNivel + " " + nivelEntidadOeeUtil.getNombreNivelEntidad());
			reportUtilFormController.getParametros().put("entidad", codigoEntidad + " " + nivelEntidadOeeUtil.getNombreSinEntidad());
			reportUtilFormController.getParametros().put("oee", codigoOEE + " " + nivelEntidadOeeUtil.getDenominacionUnidad());
			
			reportUtilFormController.getParametros().put("idSinEntidad", nivelEntidadOeeUtil.getIdSinEntidad());
			reportUtilFormController.getParametros().put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
			reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private Boolean validar() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if ( nivelEntidadOeeUtil.getIdSinNivelEntidad()== null) {
			statusMessages.add(Severity.ERROR, "Nivel es un campo requerido. Verifique.");
			return false;
		}
		else if ( nivelEntidadOeeUtil.getIdSinEntidad() == null) {
			statusMessages.add(Severity.ERROR, "Entidad es un campo requerido. Verifique.");
			return false;
		}
		else if ( nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "OEE es un campo requerido. Verifique.");
			return false;
		}
		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdSinNivelEntidad().toString(), TiposDatos.LONG.getValor(), null)) {
			return false;
		}
		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdSinEntidad().toString(), TiposDatos.LONG.getValor(), null)) {
			return false;
		}
		if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
			return false;
		}

		return true;
	}

	
	public void imprimir() throws Exception {
		if (!validar())
			return;
		
		reportUtilFormController = (ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU437_cant_personas_grupo_edad");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}


	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}
}
