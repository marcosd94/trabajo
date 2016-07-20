package py.com.excelsis.sicca.capacitacion.session.form;
import java.io.Serializable;
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

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.RedCapacitacion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("redCapacitacionEditFC")
@Scope(ScopeType.CONVERSATION)
public class RedCapacitacionEditFC implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;
	
	@In(required = false)
	Usuario usuarioLogueado;



	private Long idRedCap;

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	
	private RedCapacitacion redCapacitacion;


	
	private String telefono=null;
	private String email=null; 
	private boolean habUpdate=false;
	private Long idPaisExp=null;
	private String docIdentidad=null;
	private Persona persona= new Persona();
	private boolean habPersona=false;
	private Long idPersona=null;
	private Boolean primeraEntrada = true;
	private boolean habPaisExp = true;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;

	public void init() {
		if(idPersona!=null){
			persona=em.find(Persona.class, idPersona);
			completarDatosPersona(persona, true);
			habBtnAddPersons=false;
		}
			
		nivelEntidadOeeUtil.init();
		if (primeraEntrada) {
			redCapacitacion= new RedCapacitacion();
			primeraEntrada=false;
			if (idRedCap!= null) {
				redCapacitacion= em.find(RedCapacitacion.class,idRedCap);
				obtenerDatos();
			} else {
				setearDatos();
			}
		}				
		
		
		

	}
	

	
	public void buscarPersona() throws Exception {
		
		/* Validaciones */
		if (idPaisExp == null
			|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		if (docIdentidad == null
				|| !seguridadUtilFormController.validarInput(docIdentidad.toString(), TiposDatos.STRING.getValor(), null)) {
				return;
			}
		Pais pais = em.find(Pais.class, idPaisExp);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			limpiarDatosPersona2();
			persona = new Persona();
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = new Persona();
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}
	
	public void limpiarDatosPersona2() {
		telefono = null;
		email = null;
	}
	public void limpiarDatosPersona() {
		persona= new Persona();
		idPersona=null;
		telefono = null;
		email = null;
		docIdentidad = null;
	}
	private void completarDatosPersona(Persona persona, Boolean completo) {
		telefono = persona.getTelefonos() == null ? "" : persona.getTelefonos();
		email = persona.getEMail() == null ? "" : persona.getEMail();
		if (completo) {
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
	}
	
	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/capacitacion/redCapacitacion/RedCapacitacionEdit&conversationPropagation=join";
	}
	public String toFindPersona() {
		idPersona=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/capacitacion/redCapacitacion/RedCapacitacionEdit";
	}

	private void setearPersonas(){
		persona= new Persona();
		docIdentidad=null;
		telefono=null;
		email=null;
	}
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

		
		public String update() {
			try {
				if(!chkDatos("update"))
					return null;
					
				persona.setTelefonos(telefono);
				persona.setEMail(email.toLowerCase());
				persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
				persona.setFechaMod(new Date());
				em.merge(persona);
				
				redCapacitacion.setFechaMod(new Date());
				redCapacitacion.setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
				redCapacitacion.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
				em.merge(redCapacitacion);
			
				
				em.flush();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
				setearDatos();
				return "updated";
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, "Se ha producido un error: "+e.getMessage());
				return null;
			}
		
		
		}

		public String save() {
			try {
				if(!chkDatos("save"))
					return null;
					
				
				persona.setTelefonos(telefono);
				persona.setEMail(email.toLowerCase());
				persona.setUsuMod(usuarioLogueado.getCodigoUsuario());
				persona.setFechaMod(new Date());
				em.merge(persona);
				
				redCapacitacion.setFechaAlta(new Date());
				redCapacitacion.setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
				redCapacitacion.setConfiguracionUoCab(em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()));
				redCapacitacion.setPersona(persona);
				em.persist(redCapacitacion);
				
				em.flush();
				
				setearDatos();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
						.getString("GENERICO_MSG"));
				
				return "persisted";
			} catch (Exception e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
				return null;
			}
		
			
		}
		
		
		
		
//		METODOS PRIVADOS
		private boolean chkDatos(String op){
			if(persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Persona");
				return false;
			}
			
			if(email==null|| email.trim().equals("")){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Email");
				return false;
			}
			if(!General.isEmail(email.toLowerCase()))
			{
				statusMessages.add(Severity.ERROR,"Ingrese un e-mail válido. Verifique");
				return false;
			}
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Nivel. Verifique");
				return false;
			}
			if(nivelEntidadOeeUtil.getIdSinEntidad()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar la Entidad. Verifique");
				return false;
			}
			if(nivelEntidadOeeUtil.getIdConfigCab()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar el Oee. Verifique");
				return false;
			}
			if(redCapacitacion.getCargo()==null || redCapacitacion.getCargo().trim().equals("")){
				statusMessages.add(Severity.ERROR,"No se ingresó el dato correspondiente al campo obligatorio: Cargo");
				return false;
			}
			if(seguridadUtilFormController.contieneCaracter(redCapacitacion.getCargo().trim())){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
				return false;
			}
			if(checkDuplicate(op)){
				statusMessages.add(Severity.ERROR,"El registro  ingresado ya existe, favor verificar");
				return false;
			}
			return true;
		}
		
		@SuppressWarnings("unchecked")
		private boolean checkDuplicate(String operation){
			String hql ="SELECT r FROM RedCapacitacion r  " +
					" WHERE r.persona.idPersona=:idPersona "+
					" AND r.configuracionUoCab.idConfiguracionUo = :idConfiguracionUo " ;
			if(operation.equals("update"))
				hql+=" AND r.idRedCap!=  "+idRedCap;
			List<RedCapacitacion> redCapacList= em.createQuery(hql).setParameter("idPersona", persona.getIdPersona()).
			setParameter("idConfiguracionUo", nivelEntidadOeeUtil.getIdConfigCab()).getResultList();
			return !redCapacList.isEmpty(); 
		}
		private void obtenerDatos(){
			habUpdate=true;
			if(redCapacitacion.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(redCapacitacion.getConfiguracionUoCab().getIdConfiguracionUo());
				Long idEntidad=redCapacitacion.getConfiguracionUoCab().getEntidad().getIdEntidad();
				Entidad entidad=em.find(Entidad.class, idEntidad);
				nivelEntidadOeeUtil.setIdSinEntidad(entidad.getSinEntidad().getIdSinEntidad());
				Long idSinNivel=obtenernivel();
				if(idSinNivel!=null)
					nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivel);
				nivelEntidadOeeUtil.init();
			}
			persona=em.find(Persona.class, redCapacitacion.getPersona().getIdPersona());
			completarDatosPersona(persona, true);
			
		}
		@SuppressWarnings("unchecked")
		private Long obtenernivel(){
			SinEntidad sinEntidad = em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad());
			List<SinNivelEntidad> n= em.createQuery("Select s from SinNivelEntidad s" +
					" where s.nenCodigo= "+sinEntidad.getNenCodigo()
					+ " and  s.aniAniopre = "+sinEntidad.getAniAniopre()).getResultList();
			if(!n.isEmpty())
				 return n.get(0).getIdSinNivelEntidad();
			else
				return  null;
		}
		
		private void setearDatos(){
			persona= new Persona();
			redCapacitacion= new RedCapacitacion();
			redCapacitacion.setActivo(true);
			habUpdate=false;
			telefono=null;
			email=null;
			idPaisExp=idParaguay();
			nivelEntidadOeeUtil.limpiar();
			habPaisExp = true;
			habPersona=false;
		}
		
		
		
		
	// GETTER Y SETTER

	
	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public boolean isHabUpdate() {
		return habUpdate;
	}

	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public Long getIdRedCap() {
		return idRedCap;
	}

	public void setIdRedCap(Long idRedCap) {
		this.idRedCap = idRedCap;
	}

	public RedCapacitacion getRedCapacitacion() {
		return redCapacitacion;
	}

	public void setRedCapacitacion(RedCapacitacion redCapacitacion) {
		this.redCapacitacion = redCapacitacion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public boolean isHabPersona() {
		return habPersona;
	}

	public void setHabPersona(boolean habPersona) {
		this.habPersona = habPersona;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
	}



	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}



	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}



	public boolean isHabBtnAddPersons() {
		return habBtnAddPersons;
	}



	public void setHabBtnAddPersons(boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

}
