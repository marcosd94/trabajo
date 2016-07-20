package py.com.excelsis.sicca.capacitacion.session.form;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.transaction.Transaction;

import enums.ActividadEnum;
import enums.ProcesoEnum;

import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Instructores;
import py.com.excelsis.sicca.entity.ObsCapEval;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("gestionarDatosCapacitacionFC")
public class GestionarDatosCapacitacionFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	
	private SeguridadUtilFormController seguridadUtilFormController;	
	private Capacitaciones capacitaciones;
	private Long idCapacitacion;

	
	private String observacion;
	private ObsCapEval obsCapEval;
	private String tipoCapac=null;


	public void init() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		cargarCabecera();
		validarCapacitacion(idCapacitacion);
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		if(capacitaciones.getIdProcessInstance()!=null){
			long idTaskInstance = jbpmUtilFormController.getIdTaskInstanceActual(capacitaciones.getIdProcessInstance());
			obsCapEval= cargarObservacion(idTaskInstance);
			if(obsCapEval!=null)
				observacion=obsCapEval.getObservacion();
		}
	}
	public void initVer() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		cargarCabecera();
		if(capacitaciones.getIdProcessInstance()!=null){
			long idTaskInstance = jbpmUtilFormController.getIdTaskInstanceActual(capacitaciones.getIdProcessInstance());
			obsCapEval= cargarObservacion(idTaskInstance);
			if(obsCapEval!=null)
				observacion=obsCapEval.getObservacion();
		}
	}
	
	private void validarCapacitacion(Long id) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (id == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceCapacitacionActor(id, ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB.getValor())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}
		if (!seguridadUtilFormController.verificarPerteneceCapacitacionEstado(id, estadoIniciadoCircuito())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}
		
	}
	
	
	
	@SuppressWarnings("unchecked")
	public ObsCapEval cargarObservacion(long idTaskInstance){
		try {
			String cad = 
				  " select o.* " +
			  	  " from capacitacion.obs_cap_eval o " +
			  	  " where id_task_instance = " + idTaskInstance + " ";
				
			List<ObsCapEval> lista = em.createNativeQuery(cad, Observacion.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
		return null;

	}
	
	

	public void cargarCabecera(){
		
		//Nivel
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(capacitaciones.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(capacitaciones.getConfiguracionUoCab().getEntidad().getSinEntidad().getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
		
		if(capacitaciones.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
		
		nivelEntidadOeeUtil.init();
		
			
	}

	
	public String enviarAprobacion(){
		try {
			if(!chkDatos())
				return null;
			
			/**
			 * SE ACTUALIZA LA TABLA
			 * **/
			
			capacitaciones.setEstado(estadoEnviadoAprobacion());
			capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitaciones.setFechaMod(new Date());
			em.merge(capacitaciones);
			/**
			 * SE GUARDA LA OBSERVACION, EN CASO QUE HAYA
			 * **/
			if(observacion!=null && !observacion.trim().equals("")){
				ObsCapEval obsCapEval= new ObsCapEval();
				obsCapEval.setObservacion(observacion);
				obsCapEval.setIdTaskInstance(jbpmUtilFormController.getIdTaskInstanceActual(capacitaciones.getIdProcessInstance()));
				obsCapEval.setFechaAlta(new Date());
				obsCapEval.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				obsCapEval.setCapacitacion(capacitaciones);
				em.persist(obsCapEval);
			}
		
			/**
			 *SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_APROBAR_CAPACITACION);
		    jbpmUtilFormController.setTransition("carCapEnvApr_TO_aprCap");
		    jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
		    jbpmUtilFormController.setCapacitacion(capacitaciones);
		    
		    if (jbpmUtilFormController.nextTask()){
				em.flush();
			}	
			
			
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"No se ha podido evaluar, ha ocurrido un error "+e.getMessage());
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private boolean chkDatos(){
		if(capacitaciones.getTipoSeleccion()==null || capacitaciones.getTipoSeleccion().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Tipo de Seleccion antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getModalidad()==null|| capacitaciones.getModalidad().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Modalidad antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getRecepcionPostulacion()==null){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Recepción Postulacion antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getSede()==null|| capacitaciones.getSede().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Sede antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getCiudad()==null){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Ciudad antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getFechaInicio()==null){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Fecha de Inicio antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getFechaFin()==null){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Fecha Fin antes de realizar esta acción");
			return false;
		}
		
		
		List<CapacitacionFinanciacion> cf= em.createQuery("SELECT cf FROM CapacitacionFinanciacion cf " +
				" WHERE cf.capacitaciones.idCapacitacion="+capacitaciones.getIdCapacitacion()).getResultList();
		if(cf.isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe ingresar por lo menos una fuente de Financiación antes de realizar esta acción");
			return false;
		}
		if(capacitaciones.getDestinadoA()==null || capacitaciones.getDestinadoA().trim().equals("")){
			statusMessages.add(Severity.ERROR,"Debe completar el campo Destinado A, antes de realizar esta acción");
			return false;
		}
		/*if(capacitaciones.getDocumentos()==null ){
			statusMessages.add(Severity.ERROR,"Debe seleccionar un Documento antes de realizar esta acción");
			return false;
		}
		List<Instructores> ins= em.createQuery(" SELECT i  FROM Instructores i " +
				" WHERE i.capacitaciones.idCapacitacion="+capacitaciones.getIdCapacitacion()).getResultList();
		
		if(ins.isEmpty()){
			statusMessages.add(Severity.ERROR,"Debe ingresar por lo menos un instructor  antes de realizar esta acción");
			return false;
		}*/
		return true;
	}
	

	@SuppressWarnings("unchecked")
	private int estadoEnviadoAprobacion(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'ENVIADO A APROBACION' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	@SuppressWarnings("unchecked")
	private int estadoIniciadoCircuito(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'INICIADO CIRCUITO' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	
	
	/** GETTER'S && SETTER'S **/
	
	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}
	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}


	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}


	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}



	public Long getIdCapacitacion() {
		return idCapacitacion;
	}






	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}










	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getTipoCapac() {
		return tipoCapac;
	}
	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}
















	
	

	
}
