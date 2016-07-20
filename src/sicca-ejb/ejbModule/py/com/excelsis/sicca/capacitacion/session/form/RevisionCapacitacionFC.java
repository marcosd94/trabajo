package py.com.excelsis.sicca.capacitacion.session.form;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("revisionCapacitacionFC")
public class RevisionCapacitacionFC {
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

	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idCapacitacion;
	private List<RevisionCapacitacion> revisionCapacitacionList= new ArrayList<RevisionCapacitacion>();
	private String tipoCapac=null;
	private Long idTipoDocumento=null;
	private String nomDoc=null;
	private String obs;
	

	
	



	public void init() {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		seguridadUtilFormController.validarCapacitacion(idCapacitacion, estadoRevicionCapacitacion(), ActividadEnum.CAPA_REVISAR_CAPACITACION.getValor());
		
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		if(capacitaciones.getDocumentos()!=null)
			nomDoc=capacitaciones.getDocumentos().getNombreFisicoDoc();
		findReviciones();
		
		
	}
	public void iniVer(){
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		if(capacitaciones.getDocumentos()!=null)
			nomDoc=capacitaciones.getDocumentos().getNombreFisicoDoc();
		findReviciones();
	}
	
	public String enviarAprobacion(){
		try {
			if(obs==null || obs.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe especificar una Respuesta antes de realizar esta acción");
				return null;
			}
			
			
			
		    /**
			 * SE ACTUALIZA LA TABLA
			 * **/
			
			capacitaciones.setEstado(estadoEnviadoAprobacion());
			capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario());
			capacitaciones.setFechaMod(new Date());
			em.merge(capacitaciones);
			/**
			 * SE ACTUALIZA LA OBSERVACION DE EL ULTIMO
			 * **/
			int lastIndex=revisionCapacitacionList.size()-1;
			RevisionCapacitacion revisionCapacitacion = em.find(RevisionCapacitacion.class, revisionCapacitacionList.get(lastIndex).getIdRevision());
			revisionCapacitacion.setRespuesta(obs);
			revisionCapacitacion.setUsuRpta(usuarioLogueado.getUsuAlta());
			revisionCapacitacion.setFechaRpta(new Date());
			em.merge(revisionCapacitacion);
			
		
			/**
			 *SE PASA A SGT. TAREA
			 * */
			jbpmUtilFormController.setActividadActual(ActividadEnum.CAPA_REVISAR_CAPACITACION);
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_APROBAR_CAPACITACION);
		    jbpmUtilFormController.setTransition("revCap_TO_aprCap");
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
			statusMessages.add(Severity.ERROR,"Se ha producido un error: "+e.getMessage());
			return null;
		}
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
	private int estadoRevicionCapacitacion(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'REVISION DE CAPACITACION' and r.activo=true").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	

	public void cargarCabecera(){
		
		//Nivel
		ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti= em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(enti.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(enti.getSinEntidad().getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());
		
		if(capacitaciones.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
		
		nivelEntidadOeeUtil.init();
		
			
	}

	
	public void abrirDocumento() {

		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU( capacitaciones.getDocumentos().getIdDocumento(), usuarioLogueado.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	@SuppressWarnings("unchecked")
	private void findReviciones(){
		revisionCapacitacionList=em.createQuery("SELECT r FROM  RevisionCapacitacion r" +
				" WHERE r.capacitaciones.idCapacitacion="+idCapacitacion+" ORDER BY r.idRevision ASC").getResultList();
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

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public List<RevisionCapacitacion> getRevisionCapacitacionList() {
		return revisionCapacitacionList;
	}

	public void setRevisionCapacitacionList(
			List<RevisionCapacitacion> revisionCapacitacionList) {
		this.revisionCapacitacionList = revisionCapacitacionList;
	}

	public String getTipoCapac() {
		return tipoCapac;
	}

	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public String getNomDoc() {
		return nomDoc;
	}

	public void setNomDoc(String nomDoc) {
		this.nomDoc = nomDoc;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}



}
