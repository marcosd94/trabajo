package py.com.excelsis.sicca.capacitacion.session.form;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Instructores;
import py.com.excelsis.sicca.entity.ObsCapEval;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;


@Scope(ScopeType.CONVERSATION)
@Name("instructoresFC")
public class InstructoresFC {
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
	SeleccionUtilFormController seleccionUtilFormController;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	

	private Capacitaciones capacitaciones= new Capacitaciones();
	private Long idCapacitacion;
	private Long idPaisExp= null;
	private Persona persona= new Persona();
	private Long idPersona=null;
	private String docIdentidad= null;
	private boolean esEdit;
	private String tipoCapac=null;
	private Long idIntructorEdit;
	private String descripcionCV=null;
	private Long idTipoDocumento=null;
	private int indexUP=-1;
	private List<Instructores> instructoresList= new ArrayList<Instructores>();
	private List<Instructores> instructoresRemoveList= new ArrayList<Instructores>();
	private String from;
	private PersonaDTO personaDTO;
	private boolean habBtnAddPersons=false;
	
	/**
	 * DOCUMENTO ADJUNTO
	 **/
	private UploadItem item=null;
	private String nombreDoc=null;
	private String nombrePantalla;
	private Long idDoc=null;
	private Boolean primeraEntrada = true;
	private byte[] uploadedFile=null;
	private String contentType=null;
	private String fileName=null;
	private Documentos docEdit=null;
	private byte[] uploadedFileCopia=null;
	private String contentTypeCopia=null;
	private String fileNameCopia=null;

	/**
	 * @throws Exception 
	 * 
	 * */



	public void init() throws Exception {
		capacitaciones=em.find(Capacitaciones.class, idCapacitacion);
		if (!seguridadUtilFormController.validarInput(idCapacitacion.toString(), TiposDatos.LONG.getValor(), null))
			return;
		if(capacitaciones.getDatosEspecificosTipoCap()!=null)
			tipoCapac=capacitaciones.getDatosEspecificosTipoCap().getDescripcion();
		if (idPersona != null) {
			persona = em.find(Persona.class, idPersona);
			completarDatosPersona(persona);
		}
		if(primeraEntrada){
			em.clear();
			idPaisExp=idParaguay();
			cargarCabecera();
			findInstructores();
			idPersona=null;
			persona= new Persona();
			limpiar();
			instructoresRemoveList= new ArrayList<Instructores>();
			primeraEntrada=false;
			
		}
		
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

	@SuppressWarnings("unchecked")
	public void addInstructores(){
		try {
			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return ;
				}
				uploadedFileCopia=uploadedFile;
				contentTypeCopia=contentType;
				fileNameCopia=fileName;
				
			}else{
				if(uploadedFileCopia!=null){
					uploadedFile=uploadedFileCopia;
					contentType=contentTypeCopia;
					fileName=fileNameCopia;
				}else{
					item = null;
					uploadedFileCopia=null;
				}
			}
			if(persona==null ||persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Persona antes de realizar esta acción. Verifique");
				return;
			}
			if(!validarRepetido())
				return ;
			if(descripcionCV==null || descripcionCV.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Descripción breve del CV antes de realizar esta  acción. Verifique ");
				return;
			}
		
			if(idTipoDocumento!=null&& item==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar un Documento");
				return ;
			}
			if (item != null) {
				if (idTipoDocumento == null) {
					statusMessages.add(Severity.ERROR, "Debe seleccionar el Tipo de documento");
					return  ;
				}
				/**
				 * SEA AGREGO PARA VERIFICACION DE TAMAÑO
				 * */
				 String extension = item.getFileName().substring( item.getFileName().lastIndexOf( "." ) );
					List<TipoArchivo> ta = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
						Integer tam=item.getFileSize();
						if (tam.intValue() > ta.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
							return ;
						}
					}
				/**
				 * FIN
				 * */
			}
			
			Instructores instructores= new Instructores();
			Documentos documentos = new Documentos();
			if(item!=null){
				
				documentos.setActivo(true);
				documentos.setFechaAlta(new Date());
				documentos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				documentos.setMimetype(item.getContentType());
				documentos.setNombreTabla("Instructores");
				documentos.setNombreFisicoDoc(item.getFileName());
				documentos.setNroDocumento(item.getFileSize());
				documentos.setTamanhoDoc(String.valueOf(item.getFileSize()));
				documentos.setUbicacionFisica(item.getFileName());
				documentos.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDocumento));
				documentos.setInputFile(item.getFile());
				instructores.setDocumento(documentos);
			}
			
			instructores.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			instructores.setFechaAlta(new Date());
			instructores.setPersona(persona);
			instructores.setDescripcionCv(descripcionCV);
			instructores.setActivo(true);
			instructoresList.add(instructores);
			limpiar();
			return ;
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"A ocurrido un error: "+e.getMessage());
			return ;
		}
		
		
	}
	 private boolean validarRepetido() {
			if (instructoresList != null && instructoresList.size() > 0) {
				for (Instructores det : instructoresList) {
					if (det.getPersona() != null
						&& det.getPersona().getIdPersona() != null
						&& det.getPersona().getIdPersona().longValue() == persona.getIdPersona().longValue()) {
						statusMessages.add(Severity.ERROR, "Ya existe la persona seleccionada. Verifique");
						return false;
					}
				}
			}
			return true;
		}
	 
	 private boolean validarRepetidoUP() {
			if (instructoresList != null && instructoresList.size() > 0) {
				for (int i = 0; i < instructoresList.size(); i++) {
					if (instructoresList.get(i).getPersona() != null
						&& instructoresList.get(i).getPersona().getIdPersona() != null
						&& instructoresList.get(i).getPersona().getIdPersona().longValue() == persona.getIdPersona().longValue()) {
						if(i!=indexUP){
							statusMessages.add(Severity.ERROR, "Ya existe la persona seleccionada. Verifique");
							return false;
						}
						
					}
				}
			}
			return true;
		}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	public void editRow(int index){
		try {
			Instructores aux= new Instructores();
			aux= instructoresList.get(index);
			if(aux.getIdInstructor()!=null)
				idIntructorEdit=aux.getIdInstructor();
			if(aux.getDocumento()!=null){
				nombreDoc=aux.getDocumento().getNombreFisicoDoc();
				idTipoDocumento= aux.getDocumento().getDatosEspecificos().getIdDatosEspecificos();
				docEdit=aux.getDocumento();
			}
			persona= aux.getPersona();
			idPaisExp=persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad=persona.getDocumentoIdentidad();
			descripcionCV=aux.getDescripcionCv();
			idPersona=persona.getIdPersona();
			esEdit=true;
			indexUP= index;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"A ocurrido un error: "+e.getMessage());
			return ;
		}
	}
	@SuppressWarnings("unchecked")
	public void upRow(){
		try {
			if(persona==null ||persona.getIdPersona()==null){
				statusMessages.add(Severity.ERROR,"Debe seleccionar una Persona antes de realizar esta acción. Verifique");
				return;
			}
			if(!validarRepetidoUP())
				return ;
			if(descripcionCV==null || descripcionCV.trim().equals("")){
				statusMessages.add(Severity.ERROR,"Debe Ingresar la Descripción breve del CV antes de realizar esta  acción. Verifique ");
				return;
			}
			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return ;
				}
				
			}else{
				item=null;
			}
//			if(idTipoDocumento!=null&& item==null){
//				statusMessages.add(Severity.ERROR,"Debe seleccionar un Documento");
//				return ;
//			}
			if (item != null) {
				if (idTipoDocumento == null) {
					statusMessages.add(Severity.ERROR, "Debe seleccionar el Tipo de documento");
					return  ;
				}
				/**
				 * SEA AGREGO PARA VERIFICACION DE TAMAÑO
				 * */
				 String extension = item.getFileName().substring( item.getFileName().lastIndexOf( "." ) );
					List<TipoArchivo> ta = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
						Integer tam=item.getFileSize();
						if (tam.intValue() > ta.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
							return ;
						}
					}
				/**
				 * FIN
				 * */
			}
			Instructores instructores= new Instructores();
			if(idIntructorEdit!=null)
				instructores= em.find(Instructores.class, idIntructorEdit);
			Documentos documentos = new Documentos();
			if(item!=null){
				
				documentos.setActivo(true);
				documentos.setFechaAlta(new Date());
				documentos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				documentos.setMimetype(item.getContentType());
				documentos.setNombreTabla("Instructores");
				documentos.setNombreFisicoDoc(item.getFileName());
				documentos.setNroDocumento(item.getFileSize());
				documentos.setTamanhoDoc(String.valueOf(item.getFileSize()));
				documentos.setUbicacionFisica(item.getFileName());
				documentos.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDocumento));
				documentos.setInputFile(item.getFile());
				instructores.setDocumento(documentos);
			}else{
				if(docEdit!=null)
					instructores.setDocumento(docEdit);
			}
			if(idIntructorEdit!=null){
				instructores.setUsuMod(usuarioLogueado.getCodigoUsuario());
				instructores.setFechaMod(new Date());
			}else{
				instructores.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				instructores.setFechaAlta(new Date());
			}
			instructores.setActivo(true);
			instructores.setPersona(persona);
			instructores.setDescripcionCv(descripcionCV);
			instructoresList.remove(indexUP);
			instructoresList.add(indexUP, instructores);
			limpiar();
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"A ocurrido un error: "+e.getMessage());
			return ;
		}
	}
	
	public void deleteRow(int index){
		try {
			Instructores aux= instructoresList.get(index);
			if(aux.getIdInstructor()!=null)
				instructoresRemoveList.add(aux);
			instructoresList.remove(index);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"A ocurrido un error: "+e.getMessage());
			return ;
		}
	}
	public void abrirDocumento(int index) {

		try {
			Instructores aux= instructoresList.get(index);
			AdmDocAdjuntoFormController.abrirDocumentoFromCU( aux.getDocumento().getIdDocumento(), usuarioLogueado.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	private  void borrar(Instructores instructores){
		ManagedPersistenceContext persistenceContext = (ManagedPersistenceContext)Contexts.getConversationContext().get("entityManager");
		EntityManager em;
		try {
			em = persistenceContext.getEntityManager();
			em.remove(instructores);
			em.flush();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SystemException e) {
			e.printStackTrace();
		}
	}
	
	public String save(){
		try {
			if(instructoresList.isEmpty()){
				statusMessages.add(Severity.ERROR,"Debe agregar almenos un Instructor antes de realizar esta accion. Verifique");
				return null;
			}
			Instructores del = new Instructores();
			for (Instructores i : instructoresRemoveList) {
				 try{
					 	del=(Instructores)em.createQuery("select i from Instructores i where i.idInstructor =:id").setParameter("id", i.getIdInstructor()).getSingleResult();
						borrar(del);
					}catch(NoResultException e){
					}
			}
			for (int i = 0; i < instructoresList.size(); i++) {
				em.clear();
				Instructores instructores = instructoresList.get(i);
				instructores.setEmpleadoPuesto(findEmpleadoPuesto());
				if(instructores.getIdInstructor()!=null){
					/**
					 * Para el Caso de documento adjuntos
					 * */
					Long idDocumentoViejo=docAnterior(instructores.getIdInstructor());
					if(instructores.getDocumento()!=null && instructores.getDocumento().getNombreFisicoDoc()!=null && instructores.getDocumento().getIdDocumento()==null){
						if(idDocumentoViejo!=null){
							Documentos docAnterior=em.find(Documentos.class, idDocumentoViejo);
							docAnterior.setActivo(false);
							docAnterior.setFechaMod(new Date());
							docAnterior.setUsuMod(usuarioLogueado.getCodigoUsuario());
							em.merge(docAnterior);
							em.flush();
						}
						
						documento(instructores.getDocumento());
						
						if(idDoc!=null){
							
							if(idDoc.longValue()==-8){
								statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
								return null;					
							}
							if(idDoc.longValue()==-7){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
								return null;	
							}
							if(idDoc.longValue()==-6){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
								return null ;	
							}
							
						instructores.setDocumento(em.find(Documentos.class, idDoc));
						}else{
							statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
							return null;
						}
					}
					/**
					 * fin
					 * */
					
					em.merge(instructores);
					
				}else{
					/**
					 * Para el Caso de documento adjuntos
					 * */
					if(instructores.getDocumento()!=null && instructores.getDocumento().getNombreFisicoDoc()!=null){
						documento(instructores.getDocumento());
					
						if(idDoc!=null){
							
							if(idDoc.longValue()==-8){
								statusMessages.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
								return null;					
							}
							if(idDoc.longValue()==-7){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
								return null;	
							}
							if(idDoc.longValue()==-6){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
								return null ;	
							}
							
						instructores.setDocumento(em.find(Documentos.class, idDoc));
						}else{
							statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
							return null;
						}
					}
					instructores.setCapacitaciones(capacitaciones);
					em.persist(instructores);
				}
				
				em.flush();
					
					
			}
		
			
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			primeraEntrada=true;
			return "persisted";
			
			
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"A ocurrido un error: "+e.getMessage());
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	private EmpleadoPuesto findEmpleadoPuesto(){
		List<EmpleadoPuesto> ePuestos=em.createQuery("SELECT empleadoPuesto FROM EmpleadoPuesto empleadoPuesto " +
				" where empleadoPuesto.actual=true").getResultList();
		/**
		 * si retorna varios registros, verificar si uno de ellos pertenece al OEE 
		 * **/
		if(ePuestos.size()>1){
			//SE COMPRUEBA SI UNOS DE ELLOS PERTENECE AL OEE DE LA CAPACITACION
			for (EmpleadoPuesto empleadoPuesto : ePuestos) {
				if(empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet()!=null&& empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab()!=null){
					if(empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo().longValue()==capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo().longValue());
						return empleadoPuesto;
				}
				
			}
			//SI NO HAY,SE ASIGNA CUALQUIERA
			return ePuestos.get(0);
		}
		
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	private Long docAnterior(Long idInstructor){
		List<Documentos> aux= em.createQuery("select i.documento from Instructores i " +
				" where i.idInstructor ="+idInstructor).getResultList();
		if(!aux.isEmpty())
			return aux.get(0).getIdDocumento();
		
		return null;
	}
	public void documento(Documentos doc) {
		try {
			crearUploadItem(doc.getNombreFisicoDoc(), Integer.parseInt(doc.getTamanhoDoc()), doc.getMimetype(), getBytesFromFile(doc.getInputFile()));
			String nombrePantalla="instructores_edit";
			String direccionFisica="";
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio=sdfSoloAnio.format(new Date());
			direccionFisica="\\SICCA\\"+anio+"\\OEE\\"+capacitaciones.getConfiguracionUoCab().getIdConfiguracionUo()+
			"\\UO\\"+capacitaciones.getConfiguracionUoDet().getIdConfiguracionUoDet()+"\\C\\"+capacitaciones.getIdCapacitacion();
			idDoc=AdmDocAdjuntoFormController.guardarDirecto(item, direccionFisica,nombrePantalla, doc.getDatosEspecificos().getIdDatosEspecificos(),usuarioLogueado.getIdUsuario(),"Capacitaciones");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	public void limpiar(){
		idIntructorEdit=null;
		indexUP=-1;
		descripcionCV=null;
		docIdentidad=null;
		persona=new Persona();
		idPersona=null;
		item=null;
		uploadedFile=null;
		idTipoDocumento=null;
		esEdit=false;
		idPaisExp=idParaguay();
		nombreDoc=null;
		docEdit=null;
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
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			persona = personaDTO.getPersona();
			
		}
	}
	public void limpiarDatosPersona2() {
		persona = new Persona();
	}
	public void limpiarDatosPersona() {
		persona= new Persona();
		idPersona=null;
		docIdentidad = null;
	}
	private void completarDatosPersona(Persona persona) {
		
		idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		docIdentidad = persona.getDocumentoIdentidad();
		
	}
	
	public String toFindPersonaList() {
		return "/seleccion/persona/PersonaList.xhtml?from=/capacitacion/instructores/InstructoresEdit";
	}
	public String toFindPersona() {
		idPersona=null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/capacitacion/instructores/InstructoresEdit";
	}
	@SuppressWarnings("unchecked")
	private void findInstructores(){
		instructoresList= em.createQuery("SELECT i FROM Instructores i WHERE i.activo=true" +
				" AND i.capacitaciones.idCapacitacion="+idCapacitacion).getResultList();
	}

	
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}
	private void setearVar() {
		persona= new Persona();
		docIdentidad=null;
		
	}
	public static byte[] getBytesFromFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    // Get the size of the file
	    long length = file.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
	}
	
	
	public String back(){
		primeraEntrada=true;
		return "ok";
	}
	public boolean habLinkEliminar(int index){
		if(index==indexUP)
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


	public Long getIdCapacitacion() {
		return idCapacitacion;
	}



	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}




	public Long getIdPaisExp() {
		return idPaisExp;
	}



	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
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



	public boolean isEsEdit() {
		return esEdit;
	}




	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}




	public Long getIdIntructorEdit() {
		return idIntructorEdit;
	}





	public void setIdIntructorEdit(Long idIntructorEdit) {
		this.idIntructorEdit = idIntructorEdit;
	}


	public String getDocIdentidad() {
		return docIdentidad;
	}



	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}


	public String getDescripcionCV() {
		return descripcionCV;
	}


	public void setDescripcionCV(String descripcionCV) {
		this.descripcionCV = descripcionCV;
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




	public String getNombrePantalla() {
		return nombrePantalla;
	}



	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}



	public Long getIdDoc() {
		return idDoc;
	}



	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}



	


	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
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

	public int getIndexUP() {
		return indexUP;
	}

	public void setIndexUP(int indexUP) {
		this.indexUP = indexUP;
	}

	public List<Instructores> getInstructoresList() {
		return instructoresList;
	}

	public void setInstructoresList(List<Instructores> instructoresList) {
		this.instructoresList = instructoresList;
	}

	public List<Instructores> getInstructoresRemoveList() {
		return instructoresRemoveList;
	}

	public void setInstructoresRemoveList(
			List<Instructores> instructoresRemoveList) {
		this.instructoresRemoveList = instructoresRemoveList;
	}

	public Documentos getDocEdit() {
		return docEdit;
	}

	public void setDocEdit(Documentos docEdit) {
		this.docEdit = docEdit;
	}

	public String getTipoCapac() {
		return tipoCapac;
	}

	public void setTipoCapac(String tipoCapac) {
		this.tipoCapac = tipoCapac;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public byte[] getUploadedFileCopia() {
		return uploadedFileCopia;
	}

	public void setUploadedFileCopia(byte[] uploadedFileCopia) {
		this.uploadedFileCopia = uploadedFileCopia;
	}

	public String getContentTypeCopia() {
		return contentTypeCopia;
	}

	public void setContentTypeCopia(String contentTypeCopia) {
		this.contentTypeCopia = contentTypeCopia;
	}

	public String getFileNameCopia() {
		return fileNameCopia;
	}

	public void setFileNameCopia(String fileNameCopia) {
		this.fileNameCopia = fileNameCopia;
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
