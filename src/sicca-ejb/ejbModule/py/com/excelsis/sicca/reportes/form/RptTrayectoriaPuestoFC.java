package py.com.excelsis.sicca.reportes.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PlantaCargoDetList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("rptTrayectoriaPuestoFC")
public class RptTrayectoriaPuestoFC {
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
	@In(required = false)
	PlantaCargoDetList plantaCargoDetList;

	private String puesto;
	
	private Long idPlantaCargoDet;

	public void init() {

	}
	
	
	public void limpiar(){
		nivelEntidadOeeUtil.limpiar();
		setPuesto(null);
	}
	
	public PlantaCargoDetList getPlantaCargoDetList(){
		return this.plantaCargoDetList;
	}
	
	public List<PlantaCargoDet> getListaPlantaCargoDet() throws Exception{
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() == null){
			ConfiguracionUoDet configuracionUoDet  = new ConfiguracionUoDet();
			configuracionUoDet.setIdConfiguracionUoDet(new Long(-1));
			plantaCargoDetList.setConfiguracionUoDet(configuracionUoDet);
			List<PlantaCargoDet> lista = plantaCargoDetList.listaResultadosCU434();
			return lista;
		}
			
		
		return buscar();
	}
	
	
	public List<PlantaCargoDet> buscar() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		if (validar()) {
			ConfiguracionUoDet configuracionUoDet  = new ConfiguracionUoDet();
			configuracionUoDet.setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
			
			if(puesto == null)
				puesto = "";
			else{
				if (!sufc.validarInput(this.puesto, TiposDatos.STRING.getValor(), null)) {
					return null;
				}
			}
			
			plantaCargoDetList.setPuesto(puesto);
			plantaCargoDetList.setEstado(true);
			plantaCargoDetList.setConfiguracionUoDet(configuracionUoDet);
			nivelEntidadOeeUtil.init();
			
			return plantaCargoDetList.listaResultadosCU434();
		}
		return new ArrayList<PlantaCargoDet>();
	}

	
	private void cargarParametros() {
		try{
			grupoPuestosController = (GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
			PlantaCargoDet plantaCargoDet = (PlantaCargoDet) em.find(PlantaCargoDet.class, idPlantaCargoDet);
			String codigoNivel = nivelEntidadOeeUtil.getCodNivelEntidad().toString();
			String codigoEntidad = codigoNivel + "." + nivelEntidadOeeUtil.getCodSinEntidad().toString();
			String codigoOEE = codigoEntidad + "." + nivelEntidadOeeUtil.getOrdenUnidOrg().toString();
			String codigoUo = nivelEntidadOeeUtil.getCodigoUnidOrgDep().toString();
			String codigoPuesto = grupoPuestosController.obtenerCodigoPuesto(plantaCargoDet);
			
			reportUtilFormController.getParametros().put("codigoPuesto",  codigoPuesto);
			reportUtilFormController.getParametros().put("nivel",  codigoNivel + " " + nivelEntidadOeeUtil.getNombreNivelEntidad());
			reportUtilFormController.getParametros().put("entidad", codigoEntidad + " " + nivelEntidadOeeUtil.getNombreSinEntidad());
			reportUtilFormController.getParametros().put("oee", codigoOEE + " " + nivelEntidadOeeUtil.getDenominacionUnidad());
			reportUtilFormController.getParametros().put("uo", codigoUo + " " + nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep());
			
			reportUtilFormController.getParametros().put("idPlantaCargoDet", idPlantaCargoDet);
			reportUtilFormController.getParametros().put("usuario", usuarioLogueado.getCodigoUsuario());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private Boolean validar() {
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
		else if ( nivelEntidadOeeUtil.getIdUnidadOrganizativa() == null) {
			statusMessages.add(Severity.ERROR, "Unidad Organizativa es un campo requerido. Verifique.");
			return false;
		}
		return true;
	}

	
	public void imprimir() {
		if (idPlantaCargoDet != null){
			reportUtilFormController = (ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();
			reportUtilFormController.setNombreReporte("RPT_CU434_trayectoria_puesto");
			cargarParametros();
			reportUtilFormController.imprimirReportePdf();
		}
	}


	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}


	public String getPuesto() {
		return puesto;
	}


	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}


	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}
}
