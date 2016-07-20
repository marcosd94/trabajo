package py.com.excelsis.sicca.capacitacion.session.form;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
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

import enums.Estado;

import py.com.excelsis.sicca.capacitacion.session.CapacitacionesHome;
import py.com.excelsis.sicca.capacitacion.session.CapacitacionesList;
import py.com.excelsis.sicca.desvinculacion.session.InhabilitadosList;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;

@Scope(ScopeType.CONVERSATION)
@Name("admCapacitacionesEditFC")
public class AdmCapacitacionesEditFC {
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
	
		
	private Capacitaciones capacitaciones;
	private Long idTipo;
	private Boolean modoUpdate;
	private Long idCapacitacion;
	private Boolean primeraEntrada=true;
	private SeguridadUtilFormController seguridadUtilFormController;
	private boolean habUo;

	public void init() {
		
		
		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)){
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			if(usuarioLogueado.getConfiguracionUoDet()!=null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
		
		
		if(primeraEntrada)
		{
			nivelEntidadOeeUtil.limpiar();
			if(usuarioLogueado.getConfiguracionUoDet()!=null && idCapacitacion==null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
			primeraEntrada=false;
			if(idCapacitacion!=null){
				capacitaciones= em.find(Capacitaciones.class, idCapacitacion);
				idTipo=capacitaciones.getDatosEspecificosTipoCap().getIdDatosEspecificos();
				modoUpdate=true;
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
				
				validarOee(capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo());
				if(capacitaciones.getConfiguracionUoDet()!=null)
					validarUoDet(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
				
				cargarCabecera();
				if(capacitaciones.getConfiguracionUoDet()==null)
					habUo=true;
				else{
					nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
					if(esRolHomologar())
						habUo=true;
					else
						habUo=false;
				}
			}else{
				if(usuarioLogueado.getConfiguracionUoDet()==null)
					habUo=true;
				else{
					nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
					if(esRolHomologar())
						habUo=true;
					else
						habUo=false;
				}
				capacitaciones= new Capacitaciones();
				idTipo=null;
				modoUpdate=false;
				cargarCabecera();
			}
		}
			
		
		
		
		
	}
	
		public void initVer() {
		
		
		if (nivelEntidadOeeUtil == null || (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)){
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			if(usuarioLogueado.getConfiguracionUoDet()!=null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
		
		
		if(primeraEntrada)
		{
			nivelEntidadOeeUtil.limpiar();
			if(usuarioLogueado.getConfiguracionUoDet()!=null && idCapacitacion==null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
			primeraEntrada=false;
			if(idCapacitacion!=null){
				capacitaciones= em.find(Capacitaciones.class, idCapacitacion);
				idTipo=capacitaciones.getDatosEspecificosTipoCap().getIdDatosEspecificos();
				modoUpdate=true;
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
				
				
				
				cargarCabecera();
				if(capacitaciones.getConfiguracionUoDet()==null)
					habUo=true;
				else{
					nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet());
					if(esRolHomologar())
						habUo=true;
					else
						habUo=false;
				}
			}else{
				if(usuarioLogueado.getConfiguracionUoDet()==null)
					habUo=true;
				else{
					nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
					if(esRolHomologar())
						habUo=true;
					else
						habUo=false;
				}
				capacitaciones= new Capacitaciones();
				idTipo=null;
				modoUpdate=false;
				cargarCabecera();
			}
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
	
	public String save() {
		try {
			
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar la Unidad Organizativa. Verifique");
				return null;
				
			}
			if(capacitaciones.getNombre()==null||capacitaciones.getNombre().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Titulo. Verifique");
				return null;
			}
			if (seguridadUtilFormController == null) {
				seguridadUtilFormController =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			if(seguridadUtilFormController.contieneCaracter(capacitaciones.getNombre().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
			if(idTipo==null){
				statusMessages.add(Severity.ERROR,"Debe selecicionar el Tipo de Capacitacion");
				return null;
			}
				
			capacitaciones.setFechaAlta(new Date());
			capacitaciones.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			capacitaciones.setDatosEspecificosTipoCap(em.find(DatosEspecificos.class, idTipo));
			capacitaciones.setEstado(estadoCarga());
			capacitaciones.setTipo("CAP_SFP");
			capacitaciones.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class,usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo()));
			capacitaciones.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class, nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
			capacitaciones.setActivo(true);
			em.persist(capacitaciones);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
				
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	
	
	}
	public String update() {
		try {
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()==null){
				statusMessages.add(Severity.ERROR,"Debe Seleccionar la Unidad Organizativa. Verifique");
				return null;
				
			}
			if(capacitaciones.getNombre()==null||capacitaciones.getNombre().trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Titulo. Verifique");
				return null;
			}
			if (seguridadUtilFormController == null) {
				seguridadUtilFormController =
					(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
			}
			if(seguridadUtilFormController.contieneCaracter(capacitaciones.getNombre().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return null;
			}
			if(capacitaciones.getObservacion()!=null){
				if(seguridadUtilFormController.contieneCaracter(capacitaciones.getObservacion().trim())){
					statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
					return null;
				}
			}
			if(idTipo==null){
				statusMessages.add(Severity.ERROR,"Debe selecicionar el Tipo de Capacitacion");
				return null;
			}
			capacitaciones.setFechaMod(new Date());
			capacitaciones.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
			capacitaciones.setDatosEspecificosTipoCap(em.find(DatosEspecificos.class, idTipo));
			capacitaciones.setConfiguracionUoDet(em.find(ConfiguracionUoDet.class, nivelEntidadOeeUtil.getIdUnidadOrganizativa()));
			em.merge(capacitaciones);
			em.flush();
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "updated";	
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		
		
}

	
	@SuppressWarnings("unchecked")
	private int estadoCarga(){
		List<Referencias> referencias= em.createQuery(" Select r from Referencias r " +
				" where r.dominio='ESTADOS_CAPACITACION' and r.descAbrev= 'CARGA'").getResultList();
		if(referencias.isEmpty())
			return 0;
		 return referencias.get(0).getValorNum(); 
	}
	
	
	private void validarOee(Long id) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (id == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(id)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
	}
	private void validarUoDet(Long id) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (id == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		if (!seguridadUtilFormController.verificarPerteneceUoDet(id)) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
		}
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






	public Long getIdCapacitacion() {
		return idCapacitacion;
	}






	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}






	public Boolean getModoUpdate() {
		return modoUpdate;
	}






	public void setModoUpdate(Boolean modoUpdate) {
		this.modoUpdate = modoUpdate;
	}






	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}






	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}






	public boolean isHabUo() {
		return habUo;
	}






	public void setHabUo(boolean habUo) {
		this.habUo = habUo;
	}



	
	

	
}
