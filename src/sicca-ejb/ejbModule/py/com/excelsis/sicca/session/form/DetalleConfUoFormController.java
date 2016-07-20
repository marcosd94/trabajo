package py.com.excelsis.sicca.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.session.ClasificadorUoList;
import py.com.excelsis.sicca.session.ConfiguracionUoDetHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("detalleConfUoFormController")
public class DetalleConfUoFormController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6947874656997889066L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
    EntityManager em;
	
	@In(create = true)
	ConfiguracionUoDetHome configuracionUoDetHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ClasificadorUoList clasificadorUoList;

	private Long codConfCab;
	private ConfiguracionUoCab configuracionUoCab;
	private ConfiguracionUoDet configuracionUoDet;
	private ConfiguracionUoDet configuracionUoDetPadre;
	private ClasificadorUo clasificadorUo = new ClasificadorUo();
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	
	private Long idConfiguracionPadre;
	private String codigoOrg, codigoUnidadOrgDependiente, unidadOrgDependiente;
	
	private Long idClasificadorUo;
	
	private boolean irAProceso = false;
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
//		DATOS DE LA CABECERA
		configuracionUoCab = em.find(ConfiguracionUoCab.class, codConfCab);
		validarOee(codConfCab);
		
		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(configuracionUoCab.getEntidad().getSinEntidad().getNenCodigo());
		sinNivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		codigoOrg = ""+sinNivelEntidad.getNenCodigo()+"."
							+configuracionUoCab.getEntidad().getSinEntidad().getEntCodigo()+"."
							+configuracionUoCab.getOrden();
//		DATOS DEL DETALLE
		if (idClasificadorUo == null){
			configuracionUoDet = configuracionUoDetHome.getInstance();
			if(configuracionUoDetHome.isIdDefined()){
				clasificadorUo = configuracionUoDet.getClasificadorUo() != null 
							? configuracionUoDet.getClasificadorUo() 
							: new ClasificadorUo();
				idConfiguracionPadre = configuracionUoDet.getConfiguracionUoDet() != null 
								? configuracionUoDet.getConfiguracionUoDet().getIdConfiguracionUoDet()
								: null;
				configuracionUoDetPadre = idConfiguracionPadre != null 
								? configuracionUoDet.getConfiguracionUoDet() 
								: null;
			} else {
				if(idConfiguracionPadre != null){
					configuracionUoDetPadre = em.find(ConfiguracionUoDet.class, idConfiguracionPadre);
//					PARA CARGAR EL VALOR DE LA UNIDAD DEPENDIENTE, SI APLICARA
					
				}
				configuracionUoDet.setActivo(true);
				configuracionUoDet.setOrden(orderSuggested());
			}
			codigoUnidadOrgDependiente = generateCodeConf();
			unidadOrgDependiente = configuracionUoDetPadre != null ? configuracionUoDetPadre.getDenominacionUnidad() : null;
		}
		else 
			clasificadorUo = em.find(ClasificadorUo.class, idClasificadorUo);
	}
	
	public String getUrlToPageClasificador() {

		String url = "/planificacion/clasificadorUo/ClasificadorUoList.xhtml?from=" +
				"planificacion/configuracionUoDet/ConfiguracionUoDetEdit&configuracionUoCabIdConfiguracionUo=" + configuracionUoCab.getIdConfiguracionUo();
		
		if (clasificadorUo != null && clasificadorUo.getIdClasificadorUo() != null && configuracionUoDetHome.getConfiguracionUoDetIdConfiguracionUoDet() != null)
			url += "&configuracionUoDetIdConfiguracionUoDet=" + configuracionUoDetHome.getConfiguracionUoDetIdConfiguracionUoDet();
		

		return url;
	}
	
	public String save(){
		String result = null;
		try {
			if(!checkDuplicateCode()){
				statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("ConfiguracionUoDet_msg_codigo_existente"));
				return null;
			}
			configuracionUoDet.setConfiguracionUoCab(configuracionUoCab);
			configuracionUoDet.setClasificadorUo(clasificadorUo.getIdClasificadorUo() != null ?  clasificadorUo : null);
//			PARA EL CASO QUE EL OBJECTO ALMACENADO SEA DEPENDENDIENTE DE OTRO
			configuracionUoDet.setConfiguracionUoDet(idConfiguracionPadre != null ? configuracionUoDetPadre : null);
			
			if(!configuracionUoDetHome.isIdDefined()){
				configuracionUoDet.setEstadoAnterior("A");
				configuracionUoDetHome.setInstance(configuracionUoDet);
				result = configuracionUoDetHome.persist();
			} else{
				List<ConfiguracionUoDet> listProcess = obtenerListaElementosProcesar(configuracionUoDet, null);
				if(!configuracionUoDet.getActivo()){
					if(!puedeInactivar(listProcess)){
						statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ConfiguracionUoDet_msg_imposible_inactivar"));
						return null;
					}
					result = inactivarRegistros(listProcess) ? "updated" : null;
				} else {
					result = activarRegistros(listProcess) ? "updated" : null;
				}
			}
			if(irAProceso){
				result = result != null ? "irAProceso" : null;
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(result != null){
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SICCAAppHelper.getBundleMessage("msg_operacion_exitosa"));
				configuracionUoDetHome.clearInstance();
				idClasificadorUo = null;
				clasificadorUo = new ClasificadorUo();
			}
		}
		return null;
	}

	public String generateCodeConf(){
		String code = "";
		if(configuracionUoDetPadre == null){
			code = codigoOrg;
			return code;
		}
		if(configuracionUoDetPadre.getOrden() != null){
			if(idConfiguracionPadre == null)
				code+= codigoOrg + "." + configuracionUoDet.getOrden();
			else{
				List<Integer> listCodes = obtenerListaCodigos(configuracionUoDetPadre, null);
				int pos = 0;
				for(Integer codigo : listCodes){
					if(pos == 0)
						code += codigo;
					else
						code += "."+codigo;
					pos++;
				}
				
			}
		}
		return code;
	}
	
	public void searchClasificadorUo(){
		if(clasificadorUo.getNivel() != null
			&& clasificadorUo.getSubnivel() != null 
				&& clasificadorUo.getNro() != null){
			clasificadorUoList.getClasificadorUo().setNivel(clasificadorUo.getNivel());
			clasificadorUoList.getClasificadorUo().setSubnivel(clasificadorUo.getSubnivel());
			clasificadorUoList.getClasificadorUo().setNro(clasificadorUo.getNro());
			
			List<ClasificadorUo> list = clasificadorUoList.obtainClasificador();
			if(list.isEmpty()){
				ClasificadorUo clasificadorUoAux = new ClasificadorUo();
				clasificadorUoAux.setNivel(clasificadorUo.getNivel());
				clasificadorUoAux.setSubnivel(clasificadorUo.getSubnivel());
				clasificadorUoAux.setNro(clasificadorUo.getNro());
				clasificadorUo = clasificadorUoAux;
				statusMessages.add(Severity.ERROR, SICCAAppHelper.getBundleMessage("ConfiguracionUoDet_msg_clasificador_uo_inexistente"));
				return;
			}
			clasificadorUo = list.get(0);
		}
	}
	
//	METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private Integer orderSuggested(){
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT o FROM ConfiguracionUoDet o ");
		hql.append("WHERE o.configuracionUoCab.idConfiguracionUo = :codCabecera " );

//		EN CASO DE NIVELES INFERIORES != 0
		if(idConfiguracionPadre != null)
			hql.append(" AND o.configuracionUoDet.idConfiguracionUoDet = :codPadre " );
		else
			hql.append(" AND o.configuracionUoDet.idConfiguracionUoDet IS NULL " );
		
		hql.append("ORDER BY o.orden DESC");
		
		Query q = em.createQuery(hql.toString())
					.setParameter("codCabecera", configuracionUoCab.getIdConfiguracionUo());
		
		if(idConfiguracionPadre != null)
			q.setParameter("codPadre", idConfiguracionPadre);
			
		q.setMaxResults(1);
		List<ConfiguracionUoDet> list = q.getResultList();
		if(list.isEmpty())
			return new Integer(1);
		else
			return (list.get(0).getOrden() + 1);
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicateCode(){
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT o FROM ConfiguracionUoDet o ");
		hql.append("WHERE o.configuracionUoCab.idConfiguracionUo = :codCabecera " );
		hql.append(" AND o.orden = :ordenDet " );
		
//		EN CASO DE NIVELES INFERIORES != 0
		if(idConfiguracionPadre != null){
			hql.append(" AND o.configuracionUoDet.idConfiguracionUoDet =  "+idConfiguracionPadre );
		}else
			hql.append(" AND o.configuracionUoDet.idConfiguracionUoDet is null " );
		
//		PARA CUANDO SE PROCEDA A ACTUALIZAR UN REGISTRO
		if(configuracionUoDet.getIdConfiguracionUoDet() != null)
			hql.append(" AND o.idConfiguracionUoDet !=  "+configuracionUoDet.getIdConfiguracionUoDet());
		
		
		
		List<ConfiguracionUoDet> list= em.createQuery(hql.toString())
		.setParameter("codCabecera", configuracionUoCab.getIdConfiguracionUo())
		.setParameter("ordenDet", configuracionUoDet.getOrden()).getResultList();
		
		
		 
		
		return list.isEmpty();
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
	
	/**
	 * METODO OBTENER LA LISTA DE LAS ENTIDADES QUE SE PUEDEN ACTIVAR/INACTIVAR DE ACUERDO AL CASO
	 * @author jmelgarejo
	 * @param configPrincipal : entidad a partir de la cual buscar sus dependientes
	 * @return List<ConfiguracionUoDet> : lista con las entidades a utilizar en otros procesos
	 * */
	@SuppressWarnings("unchecked")
	private List<ConfiguracionUoDet> obtenerListaElementosProcesar(ConfiguracionUoDet configPrincipal, List<ConfiguracionUoDet> listProcesar){
		if(listProcesar == null)
			listProcesar = new ArrayList<ConfiguracionUoDet>();

		listProcesar.add(configPrincipal);
		
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT o FROM ConfiguracionUoDet o ");
		hql.append("WHERE o.configuracionUoDet.idConfiguracionUoDet = :idPadre");
		
		Query q = em.createQuery(hql.toString()).setParameter("idPadre", configPrincipal.getIdConfiguracionUoDet());
		
		List<ConfiguracionUoDet> listHijos = q.getResultList();
		for(ConfiguracionUoDet hijo : listHijos){
			obtenerListaElementosProcesar(hijo, listProcesar);
		}
		
		return listProcesar;
	}
	
	
	/**
	 * SOLO SE PUEDE INACTIVAR SI EL REGISTRO Y TODOS SUS DEPENDIENTES CUENTEN
	 * CON ESTADO 'VACANTE' O SE ENCUENTREN INACTIVOS
	 * 
	 * METODO PARA VERIFICAR SI SE PUEDE INACTIVAR EL REGISTRO Y SUS DEPENDIENTES
	 * @author jmelgarejo
	 * @param List<ConfiguracionUoDet> listProcess : lista con entidades a verificar
	 * 				que cumplan con la condicion descrita
	 * @return true : si se puede inactivar;
	 * */
	@SuppressWarnings("unchecked")
	private boolean puedeInactivar(List<ConfiguracionUoDet> listProcess){
		StringBuffer hql = new StringBuffer();
			hql.append("SELECT p FROM PlantaCargoDet p ");
			hql.append("WHERE p.configuracionUoDet.idConfiguracionUoDet = :id ");
			hql.append("AND (p.activo = true and lower(p.estadoCab.descripcion) = 'vacante')");
			Query q = em.createQuery(hql.toString());
		
		for(ConfiguracionUoDet det : listProcess){
			q.setParameter("id", det.getIdConfiguracionUoDet());
			List<PlantaCargoDet> list = q.getResultList();
			if(!list.isEmpty())
				return false;
			
		}
		return true;
	}
	
	/**
	 * METODO PARA INACTIVAR LOS REGISTROS ESPECIFICADOS
	 * @author jmelgarejo
	 * @param List<ConfiguracionUoDet> listProcess : lista con las entidades a inactivar
	 * @return true : registros inactivados
	 * */
	private boolean inactivarRegistros(List<ConfiguracionUoDet> listProcess){
		try {
			for(ConfiguracionUoDet det : listProcess){
				if((det.getActivo() == null || det.getActivo().booleanValue()))
					det.setEstadoAnterior("A");
				else if(det.getIdConfiguracionUoDet().longValue() != configuracionUoDet.getIdConfiguracionUoDet().longValue())
					det.setEstadoAnterior("I");
				
				det.setActivo(false);
				configuracionUoDetHome.setInstance(det);
				configuracionUoDetHome.update();
				configuracionUoDetHome.clearInstance();
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private boolean activarRegistros(List<ConfiguracionUoDet> listProcess){
		try {
			for(ConfiguracionUoDet det : listProcess){
				if(det.getEstadoAnterior() != null && det.getEstadoAnterior().equalsIgnoreCase("A")){
					if(det.getActivo() == null || det.getActivo().booleanValue())
						det.setEstadoAnterior("A");
					else
						det.setEstadoAnterior("I");
					
					det.setActivo(true);
				}
				configuracionUoDetHome.setInstance(det);
				configuracionUoDetHome.update();
				configuracionUoDetHome.clearInstance();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return true;
	}
	
	
	
//	GETTERS Y SETTERS
	public Long getCodConfCab() {
		return codConfCab;
	}
	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}
	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
	}
	public void setCodConfCab(Long codConfCab) {
		this.codConfCab = codConfCab;
	}
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}
	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
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
	public ClasificadorUo getClasificadorUo() {
		return clasificadorUo;
	}
	public void setClasificadorUo(ClasificadorUo clasificadorUo) {
		this.clasificadorUo = clasificadorUo;
	}
	public Long getIdConfiguracionPadre() {
		return idConfiguracionPadre;
	}
	public void setIdConfiguracionPadre(Long idConfiguracionPadre) {
		this.idConfiguracionPadre = idConfiguracionPadre;
	}
	public ConfiguracionUoDet getConfiguracionUoDetPadre() {
		return configuracionUoDetPadre;
	}
	public void setConfiguracionUoDetPadre(ConfiguracionUoDet configuracionUoDetPadre) {
		this.configuracionUoDetPadre = configuracionUoDetPadre;
	}
	public void setIrAProceso(boolean irAProceso) {
		this.irAProceso = irAProceso;
	}
	public boolean isIrAProceso() {
		return irAProceso;
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

	public void setIdClasificadorUo(Long idClasificadorUo) {
		this.idClasificadorUo = idClasificadorUo;
	}

	public Long getIdClasificadorUo() {
		return idClasificadorUo;
	}
	
}
