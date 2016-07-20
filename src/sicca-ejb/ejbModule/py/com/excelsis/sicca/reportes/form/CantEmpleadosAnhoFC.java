package py.com.excelsis.sicca.reportes.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("cantEmpleadosAnhoFC")
public class CantEmpleadosAnhoFC {
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

	private Integer anho;
	private Integer anhoActual;
	
	private String formato;
	private List<SelectItem> formatoSelectItem;

	public void init() {
		if(anho == null){
			Calendar c = Calendar.getInstance();
			anho = c.get(Calendar.YEAR); 
		}
		
		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)){
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		
		Calendar c = Calendar.getInstance();
		anhoActual = c.get(Calendar.YEAR); 
		
		buildFormatoSelectItem();
	}
	
	
	public void cargarCabecera(){
		grupoPuestosController = (GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();
		
		SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();
		
		//Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());
		
		nivelEntidadOeeUtil.init();
	}
	
	
	public void limpiar(){
		nivelEntidadOeeUtil.limpiar();
		anho = null;
	}
	
	
	private void cargarParametros() {
		try{
			nivelEntidadOeeUtil.init();

			reportUtilFormController.getParametros().put("idSinNivelEntidad", nivelEntidadOeeUtil.getIdSinNivelEntidad());
			reportUtilFormController.getParametros().put("idSinEntidad", nivelEntidadOeeUtil.getIdSinEntidad());
			reportUtilFormController.getParametros().put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
			reportUtilFormController.getParametros().put("anho", anho);
			reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
			
			
			Calendar c = Calendar.getInstance();
			Integer mes = c.get(Calendar.MONTH) + 1; 
			if (anho.intValue() < anhoActual.intValue())
				mes = 12;
			
			reportUtilFormController.getParametros().put("mes", mes);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private Boolean validar(Boolean controlarCab) {
		if (controlarCab){
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
		}
		
		if ( anho == null) {
			statusMessages.add(Severity.ERROR, "Año es un campo requerido. Verifique.");
			return false;
		}
		else if (anho.intValue() > anhoActual.intValue()){
			statusMessages.add(Severity.ERROR, "Año no válido. Verifique.");
			return false;
		}
		
		return true;
	}

	
	public void imprimir() {
		if (!validar(false))
			return;
		
		reportUtilFormController = (ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU438_cant_empleados");
		cargarParametros();
		
		if ("EXCEL".equals(formato))
			reportUtilFormController.imprimirReporteXLS();
		else
			reportUtilFormController.imprimirReportePdf();
	}
	
	public void imprimirGrafico(){
		if (!validar(true))
			return;
		
		reportUtilFormController = (ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("RPT_CU438_cant_empleados_grafico");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	
	private void buildFormatoSelectItem() {
		formatoSelectItem = new ArrayList<SelectItem>();
		formatoSelectItem.add(new SelectItem("PDF", "PDF"));
		formatoSelectItem.add(new SelectItem("EXCEL", "EXCEL"));
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public void setAnho(Integer anho) {
		this.anho = anho;
	}


	public Integer getAnho() {
		return anho;
	}


	


	public void setFormato(String formato) {
		this.formato = formato;
	}


	public String getFormato() {
		return formato;
	}


	public void setFormatoSelectItem(List<SelectItem> formatoSelectItem) {
		this.formatoSelectItem = formatoSelectItem;
	}


	public List<SelectItem> getFormatoSelectItem() {
		return formatoSelectItem;
	}
}
