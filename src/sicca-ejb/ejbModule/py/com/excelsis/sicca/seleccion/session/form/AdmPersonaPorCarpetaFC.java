package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.EntityManager;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.PasswordGenerator;
import py.com.excelsis.sicca.util.Sha1;

@Scope(ScopeType.PAGE)
@Name("admPersonaPorCarpetaFC")
public class AdmPersonaPorCarpetaFC implements
		Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;


	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	UsuarioPortalFormController usuarioPortalFormController;

	private Long idConcursoPuestoAgr;
	private Persona persona;
	private Long idPersona;
	private Long idNacionalidad;
	private Long idPaisExpe;
	private String documentoCi;
	private PersonaDTO personaDTO;
	private boolean habCamposPersona=true;// que campos habilitar y que no
	PasswordGenerator passDefault = new PasswordGenerator();
	Sha1 sha = new Sha1();
	String pass="";
	private boolean habGuardar=true;
	
	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		
			persona = new Persona();
			habGuardar=true;
			if(!General.validarDocIdentidad(documentoCi)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Documento Identidad inválido. Verifique");
				habGuardar=false;
				return ;
			}
			buscarPersona();
			persona.setActivo(true);
			
	}
	public void buscarPersona( ){
		try {
			/* Validaciones */
			if (idPaisExpe == null
				|| !seguridadUtilFormController.validarInput(idPaisExpe.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			if (documentoCi == null || documentoCi.trim().equals("")
					|| !seguridadUtilFormController.validarInput(documentoCi, TiposDatos.STRING.getValor(), null)) {
					return;
				}
			Pais pais = em.find(Pais.class, idPaisExpe);
			if (pais == null)
				return;
			
			/* fin validaciones */
			personaDTO = seleccionUtilFormController.buscarPersona(documentoCi, pais.getDescripcion());
			habCamposPersona=true;
			if (personaDTO.getHabilitarBtn() == null) {
				statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
				limpiarDatosPersona();
				habGuardar=false;
			} else if (personaDTO.getHabilitarBtn()) {
				if(personaDTO.getEstado().equals("NUEVO")){
					persona=personaDTO.getPersona();
					if(persona.getDatosEspecificos()!=null)
						idNacionalidad=persona.getDatosEspecificos().getIdDatosEspecificos();
					persona.setActivo(true);
					habCamposPersona=false;
				}else{
					limpiarDatosPersona();
				}
			}else{
				if(idPaisExpe.longValue()==General.getIdParaguay().longValue()){
					if(personaDTO.getPersona().getIdPersona()!=null){
						cargarDatosPersona(personaDTO.getPersona().getIdPersona());
						habCamposPersona=false;
						habGuardar=false;
						statusMessages.add(Severity.WARN,"La Persona ya existe");
					}
									
				}else{
					if(personaDTO.getPersona().getIdPersona()!=null){
						limpiarDatosPersona();
						habCamposPersona=true;
					}else
						limpiarDatosPersona();
				}
					
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void cargarDatosPersona(Long idPersona){
		persona = em.find(Persona.class, idPersona);
		documentoCi=persona.getDocumentoIdentidad();
		idNacionalidad=persona.getDatosEspecificos().getIdDatosEspecificos();
		
	}
	private void limpiarDatosPersona(){
		persona= new Persona();
		persona.setDocumentoIdentidad(documentoCi);
	}
	
	public void initVer() {
		cargarDatosPersona(idPersona);
		idPaisExpe=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
	}
		
	

	

	

	public String save() {
		if(!validacion())
			return null;
		
		
		try {
			/**
			 * SE CREA PERSONA
			 * */
				persona.setEMail(persona.getEMail().toLowerCase());
				persona.setApellidos(persona.getApellidos().trim());
			if(idPaisExpe!=null)
				persona.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, idPaisExpe));
			persona.setDatosEspecificos(em.find(DatosEspecificos.class, idNacionalidad));
			
			
			persona.setActivo(true);
			persona.setTipo("CARPETA");
			if(persona.getIdPersona()==null){
				persona.setFechaAlta(new Date());
				persona.setUsuAlta("PORTAL");
				persona.setFechaAltaOee(new Date());
				persona.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
				em.persist(persona);
				/**
				 * SE CREA EL USUARIO
				 * */
				 pass= passDefault.getPassword();
				
				Usuario usuario= new Usuario();
				usuario.setActivo(true);
				usuario.setFechaAlta(new Date());
				usuario.setContrasenha(sha.getHash(pass));
				usuario.setUsuAlta("PORTAL");
				
				ReentrantLock lock = new ReentrantLock();
				lock.lock();
				int numero_codigo = usuarioPortalFormController.obtenerUltimoNumero();
				usuario.setCodigo_usuario_numero(numero_codigo);
				usuario.setCodigoUsuario(usuarioPortalFormController.genCodUsuarioModif(numero_codigo));
				usuario.setPersona(persona);
				usuario.setTipo("CARPETA");
				usuario.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
				usuario.setFechaAltaOee(new Date());
				if(idConcursoPuestoAgr!=null)
					usuario.setConcursoPuestoAgr(em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr));
				em.persist(usuario);
				em.flush();
				
				lock.unlock();
				if(persona.getEMail()!=null)
					enviarMail(usuario);
				
				statusMessages.add(Severity.INFO,"Se gener&oacute; el Usuario del Portal:"+usuario.getCodigoUsuario()+"cuya contrase&ntilde;a es "+pass);
			}
				
			else{
				persona.setFechaModOee(new Date());
				persona.setUsuModOee(usuarioLogueado.getCodigoUsuario());
				em.merge(persona);
				em.flush();
			}
				
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			
			
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
	}
	private void enviarMail(Usuario usuario) {
		
		String dirEmail = null;
		String texto = "";
		Persona o =em.find(Persona.class, usuario.getPersona().getIdPersona());
		dirEmail = o.getEMail();
		String asunto = "Bienvenido al Portal SICCA";
		texto = cargarCuerpoMail(usuario, o);
		seleccionUtilFormController.enviarMails(dirEmail, texto, asunto, null);

		
	}
	
	private String cargarCuerpoMail(Usuario usuario, Persona persona) {
		VelocityEngine ve = new VelocityEngine();
		java.util.Properties p = new java.util.Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);
		VelocityContext context = new VelocityContext();
		context.put("personNombre", persona.getNombres());
		context.put("personApellido", persona.getApellidos());
		context.put("personApellido", persona.getApellidos());
		context.put("documentoCi", persona.getDocumentoIdentidad());
		context.put("usuarioCod",usuario.getCodigoUsuario());
		context.put("pass", pass);
		if(usuarioLogueado.getConfiguracionUoCab()!=null){
			ConfiguracionUoCab oee=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			context.put("uoDenominacion",oee.getDenominacionUnidad());	
		}
		else
			context.put("uoDenominacion","-");
		Template t = ve.getTemplate("/templates/email_CU513.vm");
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		return writer.toString();
	}
	@SuppressWarnings("unchecked")
	private boolean validacion(){
		try {
			if(idPaisExpe==null){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe ingresar el País de Expedición. Verifique");
				return false;
			}
			
			if(documentoCi==null ||documentoCi.trim().equals("")){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Documento de Identidad. Verifique");
				return false;
			}
			if(!General.validarDocIdentidad(documentoCi)){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Documento Identidad inválido. Verifique");
				return false;
			}
			persona.setDocumentoIdentidad(documentoCi);
			
			if(persona.getNombres().trim().equals("") ){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Nombre. Verifique");
				return false;
			}
			if( persona.getApellidos().trim().equals("")){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Apellido. Verifique");
				return false;
			}
			if(idPaisExpe.longValue()==General.getIdParaguay()){
				if(persona.getIdPersona()==null){//de modo guardar
					List<Persona> per=em.createQuery("Select p from Persona p " +
							" where p.documentoIdentidad = '"
							+persona.getDocumentoIdentidad()+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPaisExpe).getResultList();
					if(!per.isEmpty()){
						statusMessages.clear();
						statusMessages.add(Severity.ERROR,"La Persona ingresada ya existe. Verifique");
						return false;
					}
				
				}
			}
			
			

			if(General.compareDate(persona.getFechaNacimiento(), new Date()) >= 0){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La fecha de nacimiento no puede ser mayor o igual a la fecha actual. Verifique ");
				return false;
			}
			if( !General.isFechaValida(persona.getFechaNacimiento())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha de Nacimiento inválida. Verifique");
				return false;
			}
			if(!General.validarFechasNacimiento(persona.getFechaNacimiento())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "El rango de edades debe comprender entre "+General.edadMinima()+" y "+General.edadMaxima());
				return false;
			}
			
			
			
			
			if(!General.isEmail(persona.getEMail().toLowerCase()))
			{
				statusMessages.add(Severity.ERROR,"Ingrese un mail valido. Verifique");
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
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

	public Long getIdNacionalidad() {
		return idNacionalidad;
	}

	public void setIdNacionalidad(Long idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}

	public Long getIdPaisExpe() {
		return idPaisExpe;
	}

	public void setIdPaisExpe(Long idPaisExpe) {
		this.idPaisExpe = idPaisExpe;
	}

	public String getDocumentoCi() {
		return documentoCi;
	}

	public void setDocumentoCi(String documentoCi) {
		this.documentoCi = documentoCi;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}
	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}
	public boolean isHabCamposPersona() {
		return habCamposPersona;
	}
	public void setHabCamposPersona(boolean habCamposPersona) {
		this.habCamposPersona = habCamposPersona;
	}
	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}
	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}
	public boolean isHabGuardar() {
		return habGuardar;
	}
	public void setHabGuardar(boolean habGuardar) {
		this.habGuardar = habGuardar;
	}

}
