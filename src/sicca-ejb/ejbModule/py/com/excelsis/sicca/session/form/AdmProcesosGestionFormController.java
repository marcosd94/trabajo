package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmprTercerizada;
import py.com.excelsis.sicca.entity.ProcesoGestion;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoDetHome;
import py.com.excelsis.sicca.session.ProcesoGestionHome;
import py.com.excelsis.sicca.session.ProcesoGestionList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.PAGE)
@Name("admProcesosGestionFormController")
public class AdmProcesosGestionFormController implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6178822167743145360L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
    EntityManager em;
	
	@In(create = true)
	ConfiguracionUoDetHome configuracionUoDetHome;
	@In(create = true)
	ProcesoGestionHome procesoGestionHome;
	@In(create = true)
	ProcesoGestionList procesoGestionList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(required=false)
	Usuario usuarioLogueado;
	
	private ConfiguracionUoDet configuracionUoDet;
	private ConfiguracionUoCab configuracionUoCab;
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private List<ProcesoGestion> listProcesos;
	private List<ProcesoGestion> listProcesosEliminar = new ArrayList<ProcesoGestion>();
	
	private String codigoOrg, codigoUnidadOrgDependiente, unidadOrgDependiente;
	private SeguridadUtilFormController seguridadUtilFormController;
	private void validarOee(Long idOee) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (!seguridadUtilFormController.verificarPerteneceOee(idOee)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	
	
	public void init(){
		configuracionUoDet = configuracionUoDetHome.getInstance(); 
//		DATOS DE LA CABECERA
		configuracionUoCab = configuracionUoDet.getConfiguracionUoCab();
		validarOee(configuracionUoCab.getIdConfiguracionUo());
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo());
		sinNivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		codigoOrg = ""+sinNivelEntidad.getNenCodigo()+"."
							+configuracionUoCab.getEntidad().getSinEntidad().getEntCodigo()+"."
							+configuracionUoCab.getOrden();
		codigoUnidadOrgDependiente = generateCodeConf();
		unidadOrgDependiente = configuracionUoDet.getConfiguracionUoDet() != null 
						? configuracionUoDet.getConfiguracionUoDet().getDenominacionUnidad()
						: configuracionUoDet.getDenominacionUnidad();
		cargarListaProcesos();
	}
	
	public String save(){
		String result = null;
		try {
			if(!checkData()){
				return null;
			}
			
//			SI EXITEN CONFIRMAMOS LA ELIMINACION DE REGISTROS POR PARTE DEL USUARIO
			for(ProcesoGestion procesoRemove : listProcesosEliminar){
				procesoGestionHome.setInstance(procesoRemove);
				result = procesoGestionHome.remove();
				procesoGestionHome.clearInstance();
			}
			
//			GUARDAMOS LOS REGISTROS DE LA LISTA
			for(ProcesoGestion procesoSave : listProcesos){
				if(!procesoSave.getDescripcion().trim().isEmpty()){
					procesoSave.setConfiguracionUoDet(configuracionUoDet);
					procesoGestionHome.setInstance(procesoSave);
					result = procesoGestionHome.persist();
					procesoGestionHome.clearInstance();
				}
			}
			
			configuracionUoDetHome.setConfiguracionUoDetIdConfiguracionUoDet(null);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null){
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
				procesoGestionHome.clearInstance();
			}
		}
		return null;
	}
	
	public boolean checkData(){
		if(listProcesos.isEmpty()){
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ProcesoGestion_msg_lista_sin_proceso"));
			return false;
		}
		if(listProcesos.size() == 1 && listProcesos.get(0).getDescripcion().isEmpty()){
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ProcesoGestion_msg_lista_sin_proceso"));
			return false;
		}
		
		return true;
	}
	
	public void addRow(){
		if(!checkRowToAdd()){
			return;
		}
		ProcesoGestion procesoGestion = new ProcesoGestion();
		procesoGestion.setActivo(true);
		listProcesos.add(procesoGestion);
	}
	
	public void removeRow(int pos){
		if(listProcesos.get(pos).getIdProcesoGestion() != null)
			listProcesosEliminar.add(listProcesos.get(pos));
		
//		SI LA LISTA TIENE UN SOLO ELEMENTO, Y SE QUIERE ELIMINAR LA PRIMERA POSICION
//		SETTEAMOS ESA POSICION EN LUGAR DE ELIMINARLA
		if(listProcesos.size() == 1 && pos == 0)
			listProcesos.set(pos, new ProcesoGestion());
		else
			listProcesos.remove(pos);
	}
	
//	METODOS PRIVADOS
	private String generateCodeConf(){
		String code = "";
		if(configuracionUoDet.getOrden() != null){
			if(configuracionUoDet.getConfiguracionUoDet() == null)
				code+= codigoOrg + "." + configuracionUoDet.getOrden();
			else{
				List<Integer> listCodes = obtenerListaCodigos(configuracionUoDet.getConfiguracionUoDet(), null);
				int pos = 0;
				for(Integer codigo : listCodes){
					if(pos == 0)
						code += codigo;
					else
						code += "."+codigo;
					pos++;
				}
				code+= "."+configuracionUoDet.getOrden();
			}
		}
		return code;
	}
	
	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos){
		uoDet.getDenominacionUnidad();
		if(listCodigos == null)
			listCodigos = new ArrayList<Integer>();
		
		listCodigos.add(0, uoDet.getOrden());
		if(uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else{
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}
	
	private void cargarListaProcesos(){
		procesoGestionList.getConfiguracionUoDet().setIdConfiguracionUoDet(configuracionUoDet.getIdConfiguracionUoDet());
		listProcesos = procesoGestionList.listProcessByConfigDet();
		addRow();
	}
	
	private boolean checkRowToAdd(){
		if(!listProcesos.isEmpty() && listProcesos.get(listProcesos.size()-1).getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ProcesoGestion_msg_descripcion_vacia"));
			return false;
		}
		return true;
	}
	
	public void print(){
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		
		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String,Object>>();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("entidad", configuracionUoCab.getEntidad().getSinEntidad().getEntNombre());
		param.put("unidadOrg", configuracionUoCab.getDenominacionUnidad());
		param.put("fechaDesde", configuracionUoCab.getVigenciaDesde());
		param.put("fechaHasta", configuracionUoCab.getVigenciaHasta());
		param.put("dependencia", configuracionUoDet.getDenominacionUnidad());
		
		procesoGestionList.getConfiguracionUoDet().setIdConfiguracionUoDet(configuracionUoDet.getIdConfiguracionUoDet());
		List<ProcesoGestion> listaProc = new ArrayList<ProcesoGestion>();
		listaProc = procesoGestionList.listProcessByConfigDet();
		
		if(listaProc == null || listaProc.isEmpty()){
			FacesContext.getCurrentInstance()
				.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error!", "No hay datos..."));
			return;
		}
		for(ProcesoGestion proc : listaProc){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("descripcion", proc.getDescripcion());
			map.put("activo", proc.getActivo() ? "SI" : "NO");
			
			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU127_listar_proc_gestionUO", false, listaDatosReporte, param);
	}
	
//	GETTERS Y SETTERS
	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}
	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}
	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}
	public List<ProcesoGestion> getListProcesos() {
		return listProcesos;
	}
	public void setListProcesos(List<ProcesoGestion> listProcesos) {
		this.listProcesos = listProcesos;
	}
	public List<ProcesoGestion> getListProcesosEliminar() {
		return listProcesosEliminar;
	}
	public void setListProcesosEliminar(List<ProcesoGestion> listProcesosEliminar) {
		this.listProcesosEliminar = listProcesosEliminar;
	}
	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}
	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}
	public String getCodigoOrg() {
		return codigoOrg;
	}
	public void setCodigoOrg(String codigoOrg) {
		this.codigoOrg = codigoOrg;
	}
	public void setCodigoUnidadOrgDependiente(String codigoUnidadOrgDependiente) {
		this.codigoUnidadOrgDependiente = codigoUnidadOrgDependiente;
	}
	public String getCodigoUnidadOrgDependiente() {
		return codigoUnidadOrgDependiente;
	}
	public void setUnidadOrgDependiente(String unidadOrgDependiente) {
		this.unidadOrgDependiente = unidadOrgDependiente;
	}
	public String getUnidadOrgDependiente() {
		return unidadOrgDependiente;
	}
}
