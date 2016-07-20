package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Familiares;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.session.RolList;
import py.com.excelsis.sicca.seguridad.session.UsuarioHome;
import py.com.excelsis.sicca.seguridad.session.UsuarioRolHome;
import py.com.excelsis.sicca.seguridad.session.UsuarioRolList;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.FuncionarioUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.PasswordGenerator;
import py.com.excelsis.sicca.util.Sha1;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;

@Scope(ScopeType.CONVERSATION)
@Name("usuarioFormController")
public class UsuarioFormController implements Serializable {

	private static final String passDefault = "12345";

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	PersonaList personaList;
	@In(create = true)
	UsuarioRolList usuarioRolList;
	@In(create = true)
	RolList rolList;
	
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(create = true)
	UsuarioHome usuarioHome;
	@In(create = true)
	UsuarioRolHome usuarioRolHome;
	@In(create = true)
	FuncionarioUtilFormController funcionarioUtilFormController;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(required = false, create = true)
	ReportUtilFormController reportUtilFormController;

	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	
	private Persona persona = new Persona();
	private Usuario usuario;
	private String codigoUsuario;
	private Long idPaisExp=null;
	private String docIdentidad=null;
	private Boolean habBtnAddPersons = false;
	private PersonaDTO personaDTO;
	private String email;
	private String confirmarEmail;

	private Long idPersona;

	private String codBusquedaUnidadOrg = null;

	private List<Rol> rolesAsignados;
	private List<Rol> rolesSinAsignar;
	private List<UsuarioRol> rolesAsignadosUsuario = new ArrayList<UsuarioRol>();

	private String pass;

	
	private Long idConfigDet;

	private boolean primeraEntrada = true;
	private String codUndOrg;
	
	private Documentos documentos = new Documentos();
	private UploadItem item = null;
	private String nombreDoc = null;
	private File inputFile = null;
	private byte[] uploadedFile = null;
	private String contentType = null;
	private String fileName = null;
	private Long idTipoDocumento = null;
	private Long idDocumento = null;
	private String fromCU = null;// recibe del CU que le llamo
	private String fName;
	private byte[] uploadedFileCopia = null;
	private String contentTypeCopia = null;
	private String fileNameCopia = null;
	
	Boolean primeraVez = true;
	
	private String rolDesc;
	
	//PARA LA CARGA DE OTROS DOCUMENTOS ADJUNTOS
	private List <Documentos> usuarioDocList = new ArrayList<Documentos>();
	private Long idUsuario;

	public void init() {
		 
		
		rolesAsignados = new ArrayList<Rol>();
		rolesSinAsignar = new ArrayList<Rol>();
		usuario = usuarioHome.getInstance();
		pass = "";
		
		if (primeraVez) {
			nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
					.getConfiguracionUoCab().getIdConfiguracionUo());
			primeraVez = false;
			nivelEntidadOeeUtil.init2();
		} else {
			nivelEntidadOeeUtil.init();
		}
		
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null ||nivelEntidadOeeUtil.getIdConfigCab() != null ||nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null ) 
			cargarRolesAsignados();

		if (primeraEntrada) {
			codigoUsuario=usuario.getCodigoUsuario();
			primeraEntrada = false;
			if (usuarioHome.isIdDefined()) {

				persona = usuario.getPersona();
				idPersona=persona.getIdPersona();
				if (usuario.getConfiguracionUoDet() != null) {
					idConfigDet = usuario.getConfiguracionUoDet().getIdConfiguracionUoDet();
				} else {
					idConfigDet = null;
				}

				nivelEntidadOeeUtil.setIdConfigCab(usuario.getConfiguracionUoCab() != null ? usuario.getConfiguracionUoCab().getIdConfiguracionUo()
						: null);
				if (!"PORTAL".equals(usuario.getUsuAlta())) {
					nivelEntidadOeeUtil.init2();
					if(nivelEntidadOeeUtil.getCodSinEntidad()!=null)
						codBusquedaUnidadOrg =nivelEntidadOeeUtil.getCodNivelEntidad()+"."+nivelEntidadOeeUtil.getCodSinEntidad()+"."+nivelEntidadOeeUtil.getOrdenUnidOrg();
						
					if (usuario.getConfiguracionUoDet()!=null ){
						codUndOrg = codBusquedaUnidadOrg + usuario.getConfiguracionUoDet().getOrden();
						nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuario.getConfiguracionUoDet().getIdConfiguracionUoDet());
						codigoUsuario = usuario.getCodigoUsuario();
					}
						
				
					}

				cargarRolesAsignados();
			} else 
				limpiarValores();
			
			
		}
		if (idPersona != null) {
			completarDatosPersona(idPersona, true);
			usuario.setPersona(persona);
			habBtnAddPersons=false;
			email = persona.getEMailFuncionario();
			confirmarEmail = persona.getEMailFuncionario();
			if(usuario.getIdDocumentoAlta() != null && getDocumentoAlta() != null){
				idTipoDocumento = getDocumentoAlta().getDatosEspecificos().getIdDatosEspecificos();
				nombreDoc = getDocumentoAlta().getNombreFisicoDoc();
				documentos = em.find(Documentos.class, getDocumentoAlta().getIdDocumento());
			}
		}

		nivelEntidadOeeUtil.init();
		cargarRolesSinAsignar();
	}
	private void  limpiarValores(){
		usuario.setActivo(true);
		idPaisExp=General.getIdParaguay();
		limpiarDatosPersona();
		rolesAsignadosUsuario= new ArrayList<UsuarioRol>();
	}

	public String splitLastCode(String cadenaCodUnd) {
		if (cadenaCodUnd != null) {
			String[] compos = cadenaCodUnd.split("\\.");
			if (compos.length > 0) {
				return compos[compos.length - 1];
			}
		}
		return "";
	}

	public String save() throws NoSuchAlgorithmException {
		String result = null;
		try {
			
			String sqlSiglaOEE = "select * from planificacion.configuracion_uo_cab where configuracion_uo_cab.id_configuracion_uo = "+ nivelEntidadOeeUtil.getIdConfigCab() +" and activo = TRUE";
			Query qSiglaOEE = em.createNativeQuery(sqlSiglaOEE, ConfiguracionUoCab.class);
			ConfiguracionUoCab oee = (ConfiguracionUoCab) qSiglaOEE.getResultList().get(0);
			this.codigoUsuario = oee.getDescripcionCorta() +"."+ persona.getDocumentoIdentidad();
			
			if (!checkData()) {
				return null;
			}

			if (usuario.getIdUsuario() != null) {
				statusMessages.add(Severity.ERROR, "El usuario ya ha sido guardado");
				return "persisted";
			}

			Sha1 sha = new Sha1();
			PasswordGenerator passDefault = new PasswordGenerator();

			pass = passDefault.getPassword();
			usuario.setContrasenha(sha.getHash(pass));
			if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
				usuario.setConfiguracionUoDet(new ConfiguracionUoDet());
				usuario.getConfiguracionUoDet().setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
			}
			for (UsuarioRol usuRol : rolesAsignadosUsuario) {
				usuarioRolHome.setInstance(usuRol);
				usuarioRolHome.remove();
			}
			
			/**
			 * Para el Caso de documento adjuntos
			 * */
			Documentos docAnterior = new Documentos();
			if (idPersona == null) {
				documento();

			} else if (idPersona != null && item != null) {
				docAnterior = em.find(Documentos.class, getDocumentoAlta().getIdDocumento());
				idDocumento = AdmDocAdjuntoFormController.editar(
						item.getFile(), item.getFileSize(), item.getFileName(),
						item.getContentType(),
						docAnterior.getUbicacionFisica(),
						"usuario_edit",docAnterior.getIdDocumento(), idTipoDocumento,
						usuarioLogueado.getIdUsuario(),
						0L, "Usuario");

			}
			if (item != null) {
				if (idDocumento != null) {

					if (idDocumento.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocumento.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocumento.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						if(getDocumentoAlta() != null){
							nombreDoc=getDocumentoAlta().getNombreFisicoDoc();
							uploadedFileCopia=null;
							contentTypeCopia=null;
							fileNameCopia=null;
						}else
							nombreDoc=null;
						return null;
					}

				} else {
					statusMessages.add(Severity.ERROR,
							"Error al adjuntar el registro. Verifique");
					return null;
				}

				usuario.setIdDocumentoAlta(idDocumento);
			}
			/**
			 * fin
			 * */
			
			usuario.setCodigoUsuario(codigoUsuario);
			
			persona.setEMailFuncionario(email);
			em.persist(persona);

			usuario.setPersona(persona);
			usuario.setConfiguracionUoCab(new ConfiguracionUoCab());
			usuario.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
			usuarioHome.setInstance(usuario);
			result = usuarioHome.save();

			for (Rol rol : rolesAsignados) {
				UsuarioRol usuRol = new UsuarioRol();
				usuRol.setUsuario(usuario);
				usuRol.setRol(rol);
				usuarioRolHome.setInstance(usuRol);
				result = usuarioRolHome.persist();
				usuarioRolHome.clearInstance();
			}
			//ZD 16/06/16 SETEAR DATOS DE ID_TABLA Y ID_PERSONA
			if(idDocumento != null && usuario != null && persona != null){
				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDocumento);
				doc.setIdTabla(usuario.getIdUsuario());
				doc.setPersona(persona);
				em.merge(doc);
				em.flush();
			}
			//imprimirContrasenha(pass);
			seleccionUtilFormController.enviarMails(email, "Bienvenido/a al SICCA estimado/a "+persona.getNombreCompleto()+", su usuario para ingresar al sistema es "+codigoUsuario+" y su contraseña de acceso es " + pass, "Portal Paraguay Concursa  SICCA", null);
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				//statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("msg_operacion_exitosa"));
				statusMessages.add(Severity.INFO, "Operación finalizada exitosamente. Usuario "+this.codigoUsuario+" creado. Los datos de acceso fueron enviados al correo electrónico del funcionario.");
				
				usuarioHome.clearInstance();
			}
		}
		return null;
	}

	public String update() throws NoSuchAlgorithmException {
		String result = null;
		try {
			if (!checkData()) {
				return null;
			}

			pass = null;

			for (UsuarioRol usuRol : rolesAsignadosUsuario) {
				usuarioRolHome.setInstance(usuRol);
				usuarioRolHome.remove();
			}
			usuario.setCodigoUsuario(codigoUsuario);
			if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null) {
				usuario.setConfiguracionUoDet(new ConfiguracionUoDet());
				usuario.getConfiguracionUoDet().setIdConfiguracionUoDet(nivelEntidadOeeUtil.getIdUnidadOrganizativa());
			}
			persona.setEMailFuncionario(email);
			em.persist(persona);
			
			/**
			 * Para el Caso de documento adjuntos
			 * */
			Documentos docAnterior = new Documentos();
			if (idPersona == null || usuario.getIdDocumentoAlta() == null) {
				documento();

			} else if (idPersona != null && item != null) {
				docAnterior = em.find(Documentos.class, getDocumentoAlta().getIdDocumento());
				idDocumento = AdmDocAdjuntoFormController.editar(
						item.getFile(), item.getFileSize(), item.getFileName(),
						item.getContentType(),
						docAnterior.getUbicacionFisica(),
						"usuario_edit",docAnterior.getIdDocumento(), idTipoDocumento,
						usuarioLogueado.getIdUsuario(),
						0L, "Usuario");

			}
			if (item != null) {
				if (idDocumento != null) {

					if (idDocumento.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,
										"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return null;
					}
					if (idDocumento.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return null;
					}
					if (idDocumento.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,
										"El archivo que desea levantar ya existe. Seleccione otro");
						if(getDocumentoAlta() != null){
							nombreDoc=getDocumentoAlta().getNombreFisicoDoc();
							uploadedFileCopia=null;
							contentTypeCopia=null;
							fileNameCopia=null;
						}else
							nombreDoc=null;
						return null;
					}

				} else {
					statusMessages.add(Severity.ERROR,
							"Error al adjuntar el registro. Verifique");
					return null;
				}

				usuario.setIdDocumentoAlta(idDocumento);
			}
			/**
			 * fin
			 * */
			
			usuario.setPersona(persona);
			usuario.setConfiguracionUoCab(new ConfiguracionUoCab());
			usuario.getConfiguracionUoCab().setIdConfiguracionUo(nivelEntidadOeeUtil.getIdConfigCab());
			usuarioHome.setInstance(usuario);
			result = usuarioHome.save();

			for (Rol rol : rolesAsignados) {
				UsuarioRol usuRol = new UsuarioRol();
				usuRol.setUsuario(usuario);
				usuRol.setRol(rol);
				usuarioRolHome.setInstance(usuRol);
				result = usuarioRolHome.persist();
				usuarioRolHome.clearInstance();
			}
			//ZD 16/06/16 SETEAR DATOS DE ID_TABLA Y ID_PERSONA
			if(idDocumento != null && usuario != null && persona != null){
				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDocumento);
				doc.setIdTabla(usuario.getIdUsuario());
				doc.setPersona(persona);
				em.merge(doc);
				em.flush();
			}
			nivelEntidadOeeUtil.limpiar();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (result != null) {
				statusMessages.clear();
				//statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("msg_operacion_exitosa"));
				statusMessages.add(Severity.INFO, "Operación finalizada exitosamente. Usuario "+this.codigoUsuario+" actualizado.");
				usuarioHome.clearInstance();
			}
		}
		return null;
	}

	public String restaurarPass() throws NoSuchAlgorithmException {
		Sha1 sha = new Sha1();
		PasswordGenerator passDefault = new PasswordGenerator();
		pass = passDefault.getPassword();
		usuario.setContrasenha(sha.getHash(pass));
		usuarioHome.setInstance(usuario);

		//imprimirContrasenha(pass);
		seleccionUtilFormController.enviarMails(email, "Estimado/a "+persona.getNombreCompleto()+" (usuario de sistema "+codigoUsuario+"), su nueva contraseña de acceso es " + pass, "Portal Paraguay Concursa  SICCA", null);

		usuarioHome.save();
		return null;
	}

	public boolean isNew() {
		if (usuario == null || usuario.getIdUsuario() == null)
			return true;
		return false;
	}
	

	private void imprimirContrasenha(String contrasenha) {
		if (reportUtilFormController == null)
			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);

		reportUtilFormController.init();
		reportUtilFormController.setNombreReporte("CU01"); //
		reportUtilFormController.getParametros().put("idUsuario", usuario.getIdUsuario());
		reportUtilFormController.getParametros().put("pass", contrasenha);
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		reportUtilFormController.getParametros().put("SUBREPORT_DIR",servletContext.getRealPath("/reports/jasper/"));
		reportUtilFormController.getParametros().put("path_logo", servletContext.getRealPath("/img/"));

		reportUtilFormController.imprimirReportePdf();
	}


	public void buscarPersona() throws Exception {
		
		/* Validaciones */
		if (idPaisExp == null
			|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		if (docIdentidad == null || docIdentidad.trim().equals("")
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
			persona = new Persona();
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = new Persona();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();;
			completarDatosPersona(personaDTO.getPersona().getIdPersona(), false);
			persona=em.find(Persona.class, personaDTO.getPersona().getIdPersona());
			if(persona.getEMailFuncionario() != null){
				email = persona.getEMailFuncionario();
				confirmarEmail = email;
			}
		}
		
	}
	public void buscarPersonaLocal() throws Exception {
		
		if (idPaisExp == null
				|| !seguridadUtilFormController.validarInput(idPaisExp.toString(), TiposDatos.LONG.getValor(), null)) {
			statusMessages.add(Severity.WARN, "Debe ingresar el Doc. Identidad.");
				return;
			}
		if (docIdentidad == null || docIdentidad.trim().equals("")
				|| !seguridadUtilFormController.validarInput(docIdentidad.toString(), TiposDatos.STRING.getValor(), null)) {
			statusMessages.add(Severity.WARN, "Debe ingresar el Doc. Identidad.");
				return;
			}
		Pais pais = em.find(Pais.class, idPaisExp);
		if (pais == null)
			return;

		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad, pais.getDescripcion());
		if (personaDTO == null || personaDTO.getPersona() == null) {
			statusMessages.add(Severity.ERROR, "El Doc. Identidad especificado no existe en la BD Identificaciones. Verifique o consulte con la SFP");
			persona = new Persona();
		} else {
			persona = personaDTO.getPersona();
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
		
	}
	public void limpiarDatosPersona() {
		persona=new Persona();
		docIdentidad = null;
		idPersona=null;
	}
	private void completarDatosPersona(Long idPersona, Boolean completo) {
		persona=em.find(Persona.class, idPersona);
		if (completo) {
			idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
	}

	public List<SelectItem> getTipoUsuarioSelectItem() {
		List<SelectItem> lista = new ArrayList<SelectItem>();

		lista.add(new SelectItem(null, "TODOS"));
		lista.add(new SelectItem("S", "SICCA"));
		lista.add(new SelectItem("P", "PORTAL"));

		return lista;
	}

	public String getUrlToPageNivel() {

		return "/planificacion/sinNivelEntidad/SinNivelEntidadList.xhtml?from=seguridad/usuario/UsuarioEdit";
	}

	// METODOS PRIVADOS
	@SuppressWarnings("unchecked")
	private boolean checkData() {
		if (codigoUsuario == null || codigoUsuario.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el nombre del usuario");
			return false;
		}
		if (persona == null || persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar una persona");
			return false;
		}
		
		if (email == null || email.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el email");
			return false;
		}
		
		if (!General.isEmail(email.toLowerCase()) || !email.equalsIgnoreCase(confirmarEmail)) {
			statusMessages.add(Severity.ERROR,
					"Correo electrónico no coincide con su confirmacion. Verifique");
			return false;
		}
		// if (configuracionUoCab == null || configuracionUoCab.getIdConfiguracionUo() == null) {
		// statusMessages.add(Severity.ERROR, "Debe escoger una Unidad Org.");
		// return false;
		// }
		if (nivelEntidadOeeUtil.getIdConfigCab() == null) {
			statusMessages.add(Severity.ERROR, "Debe escoger una Unidad Org.");
			return false;
		}
		if (rolesAsignados.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe asignar al menos un rol al usuario");
			return false;
		}
		
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}
			uploadedFileCopia=uploadedFile;
			contentTypeCopia=contentType;
			fileNameCopia=fileName;

		} else {
			if(uploadedFileCopia!=null){
				uploadedFile=uploadedFileCopia;
				contentType=contentTypeCopia;
				fileName=fileNameCopia;
			}else{
				item = null;
				uploadedFileCopia=null;
			}
			
		}

		if (idTipoDocumento == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
			return false;
		}

		if (fileName == null /*&& getDocumentoAlta() == null*/) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el archivo. Verifique");
			return false;
		}
		
		if (item != null) {
			String nomfinal = "";
			nomfinal = item.getFileName();
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> tam = em.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!tam.isEmpty() && tam.get(0).getUnidMedidaByte() != null) {
				if (item.getFileSize() > tam.get(0).getUnidMedidaByte()
						.intValue()) {
					statusMessages.add(Severity.ERROR,
							"El archivo supera el tamaño máximo permitido. Límite "
									+ tam.get(0).getTamanho()
									+ tam.get(0).getUnidMedida()
									+ ", verifique");
					return false;
				}
			}
		}

		return true;
	}

	private void cargarRolesAsignados() {
		if (!rolesAsignados.isEmpty())
			rolesAsignados.clear();

		if(usuario.getIdUsuario()!=null){
			usuarioRolList.getUsuario().setIdUsuario(usuario.getIdUsuario());
			rolesAsignadosUsuario = usuarioRolList.listByUser();
			for (UsuarioRol usuRol : rolesAsignadosUsuario) {
				rolesAsignados.add(usuRol.getRol());
			}
		}
		
	}

	private void cargarRolesSinAsignar() {
		if(esRolHomologar()){
			if (!rolesAsignados.isEmpty()) {
				for (Rol rol : rolesAsignados) {
					rolList.getListIdsRoles().add(rol.getIdRol());
				}
			}
			List<Rol> rolesAsignadosList = rolList.listRolesNotAssigned();
			for (Rol rol : rolesAsignadosList) {
				rolesSinAsignar.add(rol);
			}
		}
		
		else{
			if (!rolesAsignados.isEmpty()) {
				for (Rol rol : rolesAsignados) {
					if(!rol.getHomologador())
						rolList.getListIdsRoles().add(rol.getIdRol());
				}
			}
			List<Rol> rolesAsignadosList = rolList.listRolesNotAssigned();
			for (Rol rol : rolesAsignadosList) {
				if(!rol.getHomologador())
					rolesSinAsignar.add(rol);
			}
		}
	}
	
	public boolean esRolHomologar(){
		List<UsuarioRol> uRols= em.createQuery("Select d FROM UsuarioRol d " +
				" WHERE d.usuario.idUsuario="+usuarioLogueado.getIdUsuario() + " AND d.rol.homologador=TRUE ").getResultList();
		if(uRols.isEmpty())
			return false;
		return true;
	}
	
	/*public void findUnidadOrganizativaSetCodUsuario(){
		nivelEntidadOeeUtil.findUnidadOrganizativa();
		String sqlSiglaOEE = "select * from planificacion.configuracion_uo_cab where configuracion_uo_cab.id_configuracion_uo = "+ nivelEntidadOeeUtil.getIdConfigCab() +" and activo = TRUE";
		Query qSiglaOEE = em.createNativeQuery(sqlSiglaOEE, ConfiguracionUoCab.class);
		ConfiguracionUoCab oee = (ConfiguracionUoCab) qSiglaOEE.getResultList().get(0);
		this.codigoUsuario = oee.getDescripcionCorta() +"."+ persona.getDocumentoIdentidad();
	}*/

	// GETTERS Y SETTERS
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<Rol> getRolesAsignados() {
		return rolesAsignados;
	}

	public void setRolesAsignados(List<Rol> rolesSeleccionados) {
		this.rolesAsignados = rolesSeleccionados;
	}

	public List<Rol> getRolesSinAsignar() {
		return rolesSinAsignar;
	}

	public void setRolesSinAsignar(List<Rol> rolesSinAsignar) {
		this.rolesSinAsignar = rolesSinAsignar;
	}

	public void setCodBusquedaUnidadOrg(String codBusquedaUnidadOrg) {
		this.codBusquedaUnidadOrg = codBusquedaUnidadOrg;
	}

	public String getCodBusquedaUnidadOrg() {
		return codBusquedaUnidadOrg;
	}

	

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

	public String getCodigoUsuario() {
		return codigoUsuario;
	}


	public String getCodUndOrg() {
		return codUndOrg;
	}

	public void setCodUndOrg(String codUndOrg) {
		this.codUndOrg = codUndOrg;
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

	public Boolean getHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(Boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}

	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmarEmail() {
		return confirmarEmail;
	}

	public void setConfirmarEmail(String confirmarEmail) {
		this.confirmarEmail = confirmarEmail;
	}
	
	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getFromCU() {
		return fromCU;
	}

	public void setFromCU(String fromCU) {
		this.fromCU = fromCU;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}
	
	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}
	
	public void abrirDocumento() {

		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(getDocumentoAlta().getIdDocumento(), usuarioLogueado
					.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void documento() {
		try {
			String nombrePantalla = "usuario_edit";
			String direccionFisica = "";
			String separador = File.separator;
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			direccionFisica = separador + "SICCA" + separador + "USUARIO_PORTAL"
					+ separador + persona.getDocumentoIdentidad() + "_"
					+ persona.getIdPersona();
			idDocumento = AdmDocAdjuntoFormController.guardarDirecto(item,
					direccionFisica, nombrePantalla, idTipoDocumento,
					usuarioLogueado.getIdUsuario(), "Usuario");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void crearUploadItem(String fileName, int fileSize, String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}
	
	public Documentos getDocumentoAlta(){
		
		String sqlTipoDocAlta = "select * from seleccion.datos_especificos where datos_especificos.valor_alf = 'AUDOC' and activo = TRUE";
		Query qTipoDocAlta = em.createNativeQuery(sqlTipoDocAlta, DatosEspecificos.class);
		DatosEspecificos tipoDocAlta = (DatosEspecificos) qTipoDocAlta.getResultList().get(0);
		
		String sqlDocAlta = "select * from general.documentos where documentos.id_documento = "+ usuario.getIdDocumentoAlta() + " and id_datos_especificos_tipo_documento = "+ tipoDocAlta.getIdDatosEspecificos() +" and activo = TRUE";
		Query qDocAlta = em.createNativeQuery(sqlDocAlta, Documentos.class);
		Documentos docAlta = new Documentos();
		if(qDocAlta.getResultList() != null && qDocAlta.getResultList().size() > 0){
			docAlta = (Documentos) qDocAlta.getResultList().get(0);
			return docAlta;
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void actualizarRolSinAsignar(){
		rolesSinAsignar = new ArrayList<Rol>();
		if (rolDesc != null && !rolDesc.equals("")){
			try {
				
				String consulta = " select r from " + Rol.class.getName() + " r "
				+ " where activo is true and upper(descripcion) like '%" + rolDesc.toUpperCase() + "%'"
				+ " order by descripcion";

				rolesSinAsignar = em.createQuery(consulta).getResultList();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}else{
			cargarRolesSinAsignar();
		}
	}
	public String getRolDesc() {
		return rolDesc;
	}
	public void setRolDesc(String rolDesc) {
		this.rolDesc = rolDesc;
	}
	public void storeItemInList() {
	}
	
	//PARA AGREGAR DOCUMENTOS NUEVOS
	public void initAgregarDocumento(){
	this.persona = em.find(Persona.class, this.idPersona);
	this.usuario = em.find(Usuario.class, this.idUsuario);
	this.usuarioDocList = obtenerDocumentosUsuario(this.idPersona);
		
		
	}
	private List<Documentos> obtenerDocumentosUsuario(Long idPersona){
		String sql = "select * from general.documentos where activo = true and nombre_pantalla = 'usuario_edit' and nombre_tabla = 'Usuario' "
				+ "and id_persona =  " + idPersona;
		
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}
	public void btnAgregarDocumento(){
		if(this.addDocumento() != null){
			this.usuarioDocList = obtenerDocumentosUsuario(this.idPersona);
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Documento Adjuntado Correctamente");
		}else{
			return;
		}
	}
	
	public void eliminarDocumento(Long idDocumento){
		if(idDocumento != null){
			Documentos doc =  em.find(Documentos.class,idDocumento);
			doc.setActivo(false);
			em.merge(doc);
			em.flush();
			this.usuarioDocList = obtenerDocumentosUsuario(this.idPersona);
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Documento Eliminado Correctamente");
		}else{
			statusMessages.clear();
			statusMessages.add(Severity.INFO, "Debe Seleccionar un Documento");
		}

	}
	
	public void imprimirDocumento(Long idDocumento){
		
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(idDocumento,usuarioLogueado.getCodigoUsuario());
	}
	
	@SuppressWarnings("unchecked")
	public Documentos addDocumento() {
				
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return null;
			}

		}

		if (idTipoDocumento == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Tipo de Documento");
			return null;
		}
		if (item == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Archivo");
			return null;
		}
		/**
		 * Para el Caso de documento adjuntos
		 */
		if (item != null) {
			/**
			 * VALIDACION AGREGADA
			 * */
			String nomfinal = "";
			nomfinal = item.getFileName();
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> ta = em.createQuery(
					"select t from TipoArchivo t "
							+ " where lower(t.extension) like '"
							+ extension.toLowerCase() + "'").getResultList();
			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
				if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
						.intValue()) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo supera el tamaño máximo permitido. Límite "
											+ ta.get(0).getTamanho()
											+ ta.get(0).getUnidMedida()
											+ ", verifique");
					return null;
				}
			}
			if(persona != null)
				documento();
			if (idDocumento != null) {

				if (idDocumento.longValue() == -8) {
					statusMessages
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return null;
				}
				if (idDocumento.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return null;
				}
				if (idDocumento.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return null;
				}

				if(idDocumento != null && usuario != null && persona != null){
					Documentos doc = new Documentos();
					doc = em.find(Documentos.class, idDocumento);
					doc.setIdTabla(usuario.getIdUsuario());
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
					item = null;
					nombreDoc = null;
					return doc;
				}
				
			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return null;
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public Boolean isOriginal(Long idDocumento){
		String sql = "select * from seguridad.usuario where id_documento =  " + idDocumento;
		List <Documentos> lista = em.createNativeQuery(sql, Documentos.class).getResultList();
		if(lista != null && lista.size() > 0)
			return true;
		else
			return false;
	}
	public List <Documentos> getUsuarioDocList() {
		return usuarioDocList;
	}
	public void setUsuarioDocList(List <Documentos> usuarioDocList) {
		this.usuarioDocList = usuarioDocList;
	}
	public Long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}
}
