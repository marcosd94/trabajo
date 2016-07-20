package py.com.excelsis.sicca.capacitacion.session.form;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import enums.ActividadEnum;
import enums.ProcesoEnum;

import py.com.excelsis.sicca.capacitacion.session.CapacitacionesList;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("admCapacitacionListFC")
public class AdmCapacitacionListFC {
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
	CapacitacionesList capacitacionesList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	private Actor actor;
	
	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idTipo;
	private boolean habUo;
	private Boolean primeraEntrada=true;
	private List<Capacitaciones> capacitacionesLista= new ArrayList<Capacitaciones>();
	


	public void init() {
		if(primeraEntrada)
		{
			nivelEntidadOeeUtil.limpiar();
			
			primeraEntrada=false;
			if(usuarioLogueado.getConfiguracionUoDet()==null)
				habUo=true;
			else{
				//nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
				if(esRolHomologar())
					habUo=true;
				else
					habUo=false;
			}
		}
		
		cargarCabecera();
		asignarRolesTarea();	
		search();
	}
	public void asignarRolesTarea() {
		roles =
			seleccionUtilFormController.getRolesTarea(ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB .getValor(), ProcesoEnum.CAPACITACION.getValor());
	}
	
	public void searchAll(){
	
		
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		else
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		capacitaciones= new Capacitaciones();
		idTipo=null;
		nivelEntidadOeeUtil.limpiar();
		
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		else
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		
		capacitacionesList.setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		capacitacionesList.setIdConfiguracionCab(nivelEntidadOeeUtil.getIdConfigCab());
		capacitacionesList.setTipoCap("CAP_SFP");
		capacitacionesLista=capacitacionesList.limpiarCU471();
		cargarCabecera();
		
	}
	
	public void search(){
		capacitacionesList.getCapacitaciones().setNombre(capacitaciones.getNombre());
		capacitacionesList.setTipoCap("CAP_SFP");
		capacitacionesList.setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		capacitacionesList.setIdConfiguracionCab(nivelEntidadOeeUtil.getIdConfigCab());
		capacitacionesList.setIdTipo(idTipo);
		
		capacitacionesLista=capacitacionesList.listaResultados(); 
	}
	
	
	public boolean habLink(int estado){
		if(estado==estadoCarga()|| estado==estadoIniCircuito())
			return true;
		
		return false;
			
	}
	public boolean habLinkEliminar(int estado){
		if(estado==estadoCarga())
			return true;
		
		return false;
			
	}
	@SuppressWarnings("unchecked")
	private int estadoCarga(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'CARGA'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	@SuppressWarnings("unchecked")
	private int estadoIniCircuito(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
		" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'INICIADO CIRCUITO'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	
		
	}
	@SuppressWarnings("unchecked")
	public String estado(Integer valor){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
		" where r.dominio='ESTADOS_CAPACITACION' and r.valorNum= "+valor).getResultList();
		if(referencias.isEmpty())
			return null;
		 return referencias.get(0).getDescAbrev(); 
	
		
	}
	
	public void eliminar(Long idCapa){
		Capacitaciones c= em.find(Capacitaciones.class, idCapa);
		c.setActivo(false);
		c.setFechaMod(new Date());
		c.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(c);
		em.flush();
		cargarCabecera();
		search();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
			
	}
	
	/**
	 * Inicializa el proceso del workflow para un concurso.
	 * 
	 * @param id
	 *            : identificador del concurso para el cual se inicializara el proceso.
	 * @return
	 */
	public void iniciarProceso(Long idCap) {
		try {
			Capacitaciones capacitacion=em.find(Capacitaciones.class, idCap);
			
			
			capacitacion.setEstado(estadoIniCircuito());
			capacitacion.setFechaMod(new Date());
			capacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(capacitacion);
			
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CAPA_CARGAR_CAPACITACION_ENVIAR_APROB);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CAPACITACION);
			jbpmUtilFormController.setTransition("start_TO_carCapEnvApr");
			actor.setId(usuarioLogueado.getCodigoUsuario());
		
			Long processId = jbpmUtilFormController.initProcess("capacitacion");
			
			if (processId != null) {
				capacitacion.setIdProcessInstance(processId);
			} else {
				throw new Exception("");
			}
			cargarCabecera();
			search();
			em.flush();
			statusMessages.add("El Proyecto de la Capacitación "+capacitacion.getNombre().toUpperCase()+" ha iniciado satisfactoriamente");
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"No se ha podido iniciar. Verifique");
		}
		
	}
	

	public void cargarCabecera(){
			//OEE
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		
	}
	

	@SuppressWarnings("unchecked")
	private boolean esRolHomologar(){
		List<UsuarioRol> uRols= em.createQuery("Select d FROM UsuarioRol d " +
				" WHERE d.usuario.idUsuario="+usuarioLogueado.getIdUsuario() + " AND d.rol.homologador=TRUE ").getResultList();
		if(uRols.isEmpty())
			return false;
		return true;
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


	public Long getIdTipo() {
		return idTipo;
	}


	public void setIdTipo(Long idTipo) {
		this.idTipo = idTipo;
	}


	public boolean isHabUo() {
		return habUo;
	}


	public void setHabUo(boolean habUo) {
		this.habUo = habUo;
	}
	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}
	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	public List<Capacitaciones> getCapacitacionesLista() {
		return capacitacionesLista;
	}
	public void setCapacitacionesLista(List<Capacitaciones> capacitacionesLista) {
		this.capacitacionesLista = capacitacionesLista;
	}



	
	

	
}
