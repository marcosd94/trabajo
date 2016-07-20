package py.com.excelsis.sicca.capacitacion.session.form;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.capacitacion.session.CapacitacionesList;
import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.CapacitacionFinalizada;
import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Instructores;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;

import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("cursosOg290ListFC")
public class CursosOg290ListFC {
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
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
				if(esRolHomologar())
					habUo=true;
				else
					habUo=false;
			}
		}
			
		cargarCabecera();
		search();
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
		capacitacionesList.setTipoCap("CAP_OG290");
		capacitacionesLista=capacitacionesList.limpiarCU471();
		cargarCabecera();
		
	}
	
	public void search(){
		capacitacionesList.getCapacitaciones().setNombre(capacitaciones.getNombre());
		capacitacionesList.setTipoCap("CAP_OG290");
		capacitacionesList.setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
		capacitacionesList.setIdTipo(idTipo);
		capacitacionesList.setIdConfiguracionCab(nivelEntidadOeeUtil.getIdConfigCab());
		
		capacitacionesLista=capacitacionesList.listaResultados(); 
	}
	
	
	/**
	 * f.	El link Editar se encuentra solo habilitado para los registros con estado “CARGA”Ó “PUBLICADO”
	 *  @return true habilita
	 * **/
	public boolean habLinkEditar(Integer estado){
		if(estado!=null){
			if(estado.intValue()==estadoCarga()|| estado.intValue()==publicado())
				return true;
		}
		
		
		return false;
			
	}
	
	/**
	 * 
	 * g.	El link Eliminar se encuentra habilitado solo para los registros con estado “CARGA” y
	 *  aún no tenga registros asociados y activos en las tablas 
	 *  INSTRUCTORES, CAPACITACION_FINANCIACION Ó CAPACITACION_CERRADA
	 *  @return true habilita
	 * **/
	public boolean habLinkEliminar(Long idCapacitacion){
		if (idCapacitacion == null)
			return false;
		Capacitaciones cap = em.find(Capacitaciones.class, idCapacitacion);
		if (cap == null)
			return false;
		if (estadoCarga() != cap.getEstado().intValue())
			return false;
		if (tieneInstructor(idCapacitacion))
			return false;
		if (tieneCapFinaciacion(idCapacitacion))
			return false;
		if (tieneCapCerrada(idCapacitacion))
			return false;

		return true;
		
		
	}
	
	@SuppressWarnings("unchecked")
	private boolean tieneInstructor(Long idCapacitacion){
		List<Instructores> instructores=em.createQuery("Select i from Instructores i " +
				" where i.capacitaciones.idCapacitacion="+idCapacitacion +" and i.activo=true ").getResultList();
		if(instructores.isEmpty())
			return false;
		
		return true;
		
	}
	@SuppressWarnings("unchecked")
	private boolean tieneCapFinaciacion(Long idCapacitacion){
		List<CapacitacionFinanciacion> financiacions=em.createQuery("Select c from CapacitacionFinanciacion c " +
				" where c.capacitaciones.idCapacitacion="+idCapacitacion+" and c.activo=true ").getResultList();
		if(financiacions.isEmpty())
			return false;
		return true;
		
	}
	@SuppressWarnings("unchecked")
	private boolean tieneCapCerrada(Long idCapacitacion){
		List<CapacitacionCerrada> cerradas=em.createQuery("Select c from CapacitacionCerrada c " +
				" where c.capacitaciones.idCapacitacion="+idCapacitacion+" and c.activo=true ").getResultList();
		if(cerradas.isEmpty())
			return false;
		
		return true;
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
	private int publicado(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'PUBLICADO'").getResultList();
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
	
	
	

	public void cargarCabecera(){
		
		
		//Nivel
		
		ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti= em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad=em.find(SinEntidad.class, enti.getSinEntidad().getIdSinEntidad());
		Long idSinNivelEntidad=nivelEntidadOeeUtil.getIdSinNivelEntidad(enti.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);
		
		//Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		
		//OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());
		
		
		nivelEntidadOeeUtil.init();
		
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
