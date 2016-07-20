package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.resource.spi.EISSystemException;
import javax.swing.text.StyledEditorKit.BoldAction;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PersonaDiscapacidadHome;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("administrarPersonaDiscapacidad")
public class AdministrarPersonaDiscapacidad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(required = false)
	Usuario usuarioLogueado;

	@In(create = true)
	PersonaDiscapacidadHome personaDiscapacidadHome;

	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	PersonaList personaList;
	@In(create = true)
	PersonaHome personaHome;

	@In(create = true, required = false)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;
	@In(create = true, required = false)
	Tab4AdjuntarDocumentosFormController tab4AdjuntarDocumentosFormController;
	
	@In (create=true)
	SeguridadUtilFormController seguridadUtilFormController;

	private PersonaDiscapacidad personaDiscapacidad;
	private General general;
	private List<PersonaDiscapacidad> discapacidadsList = new ArrayList<PersonaDiscapacidad>();
	private Long idPais;
	private Long idNacionalidad;
	private Long idDiscapacidad;
	private Long idGradoAutonom;
	private Long idTipoDoc;
	private ReprPersonaDiscapacidad reprPersonaDiscapacidad;
	private String nomRep;
	private String apeRep;
	private String docRep;
	private String mail;
	private Persona persona;
	private boolean esEdit = false;;
	private boolean guardarRepre = false;
	Persona personaRepre = new Persona();
	private boolean habPaisExp;
	
	private int indexUp;

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private String nombrePantalla;
	private Long idDoc;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	public void init() {
		try {
			setear();
			guardarRepre = false;
			persona = em.find(Persona.class, usuarioLogueado.getPersona()
					.getIdPersona());

			if (persona != null)
				findDiscapacidad();
			esEdit = false;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void abrirDoc(int index) {

		Documentos d = discapacidadsList.get(index).getDocumentos();
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(d.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}
	@SuppressWarnings("unchecked")
	public String save() {
		try {
			if ( idPais == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el País. Verifique");
				return null;
			}
			if ( docRep == null || docRep.trim().equals("") ) {
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Doc. de Identidad. Verifique");
				return null;
			}
			if (idNacionalidad == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar la Nacionalidad. Verifique");
				return null;
			}
			if (nomRep == null || nomRep.trim().equals("") ) {
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Nombre del Representante. Verifique");
				return null;
			}
			if (apeRep== null || apeRep.trim().equals("") ) {
				statusMessages.add(Severity.ERROR,"Debe Ingresar el Apellido del Representante. Verifique");
				return null;
			}
			
			
			
			if (!representante() && guardarRepre) {
				statusMessages
						.add(Severity.WARN," Debe verificar los campos obligatorios del Representante");
				return null;
			}
			if(mail!=null && !mail.trim().equals("")){
				if(!General.isEmail((mail.toLowerCase())))
				{
					statusMessages.add(Severity.WARN,"Ingrese un mail valido. Verifique");
					return null;
				}
				mail=mail.toLowerCase();
					
			}
			
			if(personaRepre.getIdPersona()==null){
				List<Persona> p=em.createQuery("Select p from Persona p " +
						" where p.documentoIdentidad='"+docRep+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPais).getResultList();
				if(!p.isEmpty()){
					statusMessages.clear();
					statusMessages.add("La persona ingresada ya existe. Verifique");
					
				}
					
			}

			if (guardarRepre) {
				if (personaRepre.getIdPersona()!=null) {
					personaRepre.setEMail(mail);
					//personaRepre.setNombres(nomRep);
					personaRepre.setApellidos(apeRep);
					personaRepre.setPaisByIdPaisExpedicionDoc(em.find(
							Pais.class, idPais));
					personaRepre.setDatosEspecificos(em.find(
							DatosEspecificos.class, idNacionalidad));
					personaRepre.setUsuMod(usuarioLogueado.getCodigoUsuario());
					personaRepre.setFechaMod(new Date());

					em.merge(personaRepre);
					em.flush();
					personaHome.setInstance(personaRepre);
					
				} else {
					personaRepre.setEMail(mail);
					personaRepre.setNombres(nomRep);
					personaRepre.setApellidos(apeRep);
					personaRepre.setPaisByIdPaisExpedicionDoc(em.find(
							Pais.class, idPais));
					personaRepre.setDatosEspecificos(em.find(
							DatosEspecificos.class, idNacionalidad));
					personaRepre.setDocumentoIdentidad(docRep);
					personaRepre.setFechaAlta(new Date());
					personaRepre.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(personaRepre);
					em.flush();
					personaHome.setInstance(personaRepre);
					
				}
				reprPersonaDiscapacidad.setPersona(persona);
				reprPersonaDiscapacidad.setActivo(true);
				reprPersonaDiscapacidad.setPersonaRep(personaHome.getInstance());
				if(reprPersonaDiscapacidad.getIdReprPersonaDiscapacidad()!=null){
					reprPersonaDiscapacidad.setFechaMod(new Date());
					reprPersonaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(reprPersonaDiscapacidad);
				}else{
					reprPersonaDiscapacidad.setFechaAlta(new Date());
					reprPersonaDiscapacidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(reprPersonaDiscapacidad);
				}
				
					
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				tab6VistaPreliminarFormController.init();

			}
			
			
			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	public void eliminar(){
		reprPersonaDiscapacidad.setActivo(false);
		reprPersonaDiscapacidad.setFechaMod(new Date());
		reprPersonaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(reprPersonaDiscapacidad);
		em.flush();
		reprPersonaDiscapacidad= new ReprPersonaDiscapacidad();
		habPaisExp=true;
		personaRepre= new Persona();
		nomRep=null;
		apeRep=null;
		idPais=null;
		idNacionalidad=null;
		docRep=null;
		mail= null;
		guardarRepre=false;
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		tab6VistaPreliminarFormController.init();
		tab4AdjuntarDocumentosFormController.init();
	}
	

	@SuppressWarnings("unchecked")
	public String update() {
		try {
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
			if (idDiscapacidad == null ) {
				statusMessages
						.add(Severity.ERROR,"Debe seleccionar el Item Discapacidad antes de agregar. Verifique");
				return null;
			}
			if (idDiscapacidad == null || personaDiscapacidad.getGrado() == null) {
				statusMessages
						.add(Severity.ERROR,"Debe ingresar el Grado antes de agregar. Verifique");
				return null;
			}
			if (personaDiscapacidad.getGrado().intValue() <= 0) {
				statusMessages
						.add(Severity.ERROR,"El valor del grado debe ser mayor a cero. Verifique");
				return null;
			}
			if (personaDiscapacidad.getNroCertificado() != null
					&& personaDiscapacidad.getNroCertificado().intValue() <= 0) {
				statusMessages
						.add(Severity.ERROR,"El valor del Nro. de Certif debe ser mayor a cero. Verifique");
				return null;
			}
			if (item != null && idTipoDoc == null) {
				statusMessages
						.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
				return null;
			}
			if ((item == null && inputFile==null) && idTipoDoc != null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return null;
			}
			if(personaDiscapacidad.getFechaEmision()!=null && personaDiscapacidad.getFechaEmision().after(new Date()))
			{
				statusMessages.add(Severity.ERROR,"La fecha de emisión no puede ser mayor a la fecha actual, verifique ");
				return null;
			}
			if(personaDiscapacidad.getFechaEmision()!=null && !general.isFechaValida(personaDiscapacidad.getFechaEmision())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha de Emisión inválida");
				return null;
			}
			
			if(item!=null){
				/**
				 * PARA EL CASO DE DOCUMENTO ADJUNTO
				 * 
				 **/

				/**
				 * VALIDACION AGREGADA
				 * */
				String nomfinal = "";
				nomfinal = item.getFileName();
				 String extension = nomfinal.substring( nomfinal.lastIndexOf( "." ) );
					List<TipoArchivo> ta = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
						if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
							return null;
						}
					}
					/**
					 * FIN
					 * */
			}
			
			
			
			esEdit = false;
			personaDiscapacidad.setActivo(true);
			personaDiscapacidad
					.setDatosEspecificosByIdDatosEspecificosDiscapac(em.find(
							DatosEspecificos.class, idDiscapacidad));
			if(idGradoAutonom!=null)
				personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em.find(DatosEspecificos.class, idGradoAutonom));
			else
				personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosGradoAutonom(null);
			personaDiscapacidad.setPersona(persona);
			personaDiscapacidad.setFechaMod(new Date());
			personaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
			
			discapacidadsList.set(indexUp, personaDiscapacidad);

			
			
			Long idDoc = null;
			if (inputFile != null && item != null) {// habia un archivo y se
													// modifica

				if (personaDiscapacidad.getDocumentos() != null) {
					Documentos docDB = em.find(Documentos.class,
							personaDiscapacidad.getDocumentos()
									.getIdDocumento());
					if (!docDB.getNombreFisicoDoc().toLowerCase()
							.equals(nombreDoc.toLowerCase())
							|| !docDB.getTamanhoDoc().equals(
									String.valueOf(item.getFileSize()))) {// significa  que  ubo algun cambio
																			// se
																			// envia
																			// los
																			// parametros
																			// del
																			// item
						idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
								Integer.valueOf(item.getFileSize()),
								item.getFileName(), item.getContentType(),
								docDB.getUbicacionFisica(),
								"Tab5Discapacidad_edit",
								docDB.getIdDocumento(), idTipoDoc,
								usuarioLogueado.getIdUsuario(),
								personaDiscapacidad.getIdPersonaDiscapacidad(),
								"PersonaDiscapacidad");
						if (idDoc != null) {

							if (idDoc.longValue() == -8) {
								statusMessages
										.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
								return null;
							}
							if (idDoc.longValue() == -7) {
								statusMessages
										.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
								return null;
							}
							if(idDoc.longValue()==-6){
								statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
								return null;	
							}
							Documentos doc = new Documentos();
							doc = em.find(Documentos.class, idDoc);
							doc.setPersona(persona);
							em.merge(doc);
							em.flush();
							personaDiscapacidad.setDocumentos(doc);

						} else {
							statusMessages
									.add("Error al adjuntar el registro. Verifique");
							return null;
						}
					}// porque no hubo modificacion! nohace nada!

				}
			}
			if (inputFile == null && item != null) {// no habia ningun archivo y
													// se carga
				if (personaDiscapacidad.getDocumentos() == null) {
					String direccionFisica = "";
					SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
					String anio = sdfSoloAnio.format(new Date());
					direccionFisica = "\\SICCA\\" + "USUARIO_PORTAL\\"
							+ persona.getDocumentoIdentidad() + "_"
							+ persona.getIdPersona();
					idDoc = AdmDocAdjuntoFormController.editar(item.getFile(),
							item.getFileSize(), item.getFileName(),
							item.getContentType(), direccionFisica,
							"Tab5Discapacidad_edit", null, idTipoDoc,
							usuarioLogueado.getIdUsuario(),
							personaDiscapacidad.getIdPersonaDiscapacidad(),
							"PersonaDiscapacidad");
					if (idDoc != null) {

						if (idDoc.longValue() == -8) {
							statusMessages
									.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
							return null;
						}
						if (idDoc.longValue() == -7) {
							statusMessages
									.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
							return null;
						}
						if(idDoc.longValue()==-6){
							statusMessages.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
							return null;	
						}
						Documentos doc = new Documentos();
						doc = em.find(Documentos.class, idDoc);
						doc.setPersona(persona);
						em.merge(doc);
						em.flush();
						personaDiscapacidad.setDocumentos(doc);

					} else {
						statusMessages
								.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
						return null;
					}

				}

			}
			/**
			 * fin doc
			 * **/

			em.merge(personaDiscapacidad);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			idDiscapacidad = null;
			idGradoAutonom = null;
			personaDiscapacidad = new PersonaDiscapacidad();
			idTipoDoc = null;
			nombreDoc = null;
			esEdit = false;
			item = null;
			inputFile = null;
			nombreDoc = null;
			tab4AdjuntarDocumentosFormController.init();
			tab6VistaPreliminarFormController.init();
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al intentar actualizar el registro");
			esEdit=true;
			idDiscapacidad=personaDiscapacidad.getIdPersonaDiscapacidad();
		}
		return null;
	}

	
	
	@SuppressWarnings("unchecked")
	public void seleccionoExDoc(){
		try {
			if(idPais==null){
				statusMessages.add(Severity.ERROR,"Debe completar previamente el país de expedición de documento");
				return ;
			}else{
				if(docRep!=null && !docRep.trim().equals(""))
					habPaisExp=false;
				else
					habPaisExp=true;
				
				List<Persona> p=em.createQuery("Select p from Persona p " +
						" where p.documentoIdentidad='"+seguridadUtilFormController.caracterInvalido(docRep)+"' and p.paisByIdPaisExpedicionDoc.idPais="+idPais).getResultList();
				if(!p.isEmpty()){
					personaRepre=em.find(Persona.class, p.get(0).getIdPersona());
					nomRep=personaRepre.getNombres();
					apeRep=personaRepre.getApellidos();
					if(personaRepre.getDatosEspecificos()!=null)
						idNacionalidad=personaRepre.getDatosEspecificos().getIdDatosEspecificos();
					else
						idNacionalidad=null;
					mail=personaRepre.getEMail();
				}else
				{
					personaRepre= new Persona();
					nomRep=null;
					apeRep=null;
					idNacionalidad=null;
					mail=null;
				}
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void cambioPais(){
		personaRepre= new Persona();
		nomRep=null;
		apeRep=null;
		idNacionalidad=null;
		mail=null;
		docRep=null;
	}
	public void cancelar() {
		idDiscapacidad = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		idGradoAutonom = null;
		esEdit = false;
		idTipoDoc = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		nombreDoc = null;
		item=null;
		inputFile=null;

	}
	

	public void editar(int index) {
		esEdit = true;
		personaDiscapacidad = discapacidadsList.get(index);
		idDiscapacidad = personaDiscapacidad
				.getDatosEspecificosByIdDatosEspecificosDiscapac()
				.getIdDatosEspecificos();
		item=null;
		if(personaDiscapacidad.getDatosEspecificosByIdDatosEspecificosGradoAutonom()!=null)
			idGradoAutonom = personaDiscapacidad
				.getDatosEspecificosByIdDatosEspecificosGradoAutonom()
				.getIdDatosEspecificos();
		if (personaDiscapacidad.getDocumentos() != null) {
			Documentos doc = em.find(Documentos.class, personaDiscapacidad
					.getDocumentos().getIdDocumento());
			String url = doc.getUbicacionFisica();
			if (url.contains("\\")) {
				url = url.replace("\\", System.getProperty("file.separator"));

			}
			if (url.contains("//")) {
				url = url.replace("//", System.getProperty("file.separator"));

			}
			String separador = System.getProperty("file.separator");
			if (!url.endsWith(separador))
				url = url + System.getProperty("file.separator");

			url += doc.getNombreFisicoDoc();
			
			inputFile = new File(url);
			idTipoDoc = doc.getDatosEspecificos().getIdDatosEspecificos();
			nombreDoc = doc.getNombreFisicoDoc();

		} else {
			inputFile = null;
			idTipoDoc = null;
			nombreDoc = null;
		}

		indexUp = index;
	}

	   
	  public void limpiar(){
		idDiscapacidad = null;
		idGradoAutonom = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		idTipoDoc = null;
		nombreDoc = null;
		item = null;
		inputFile = null;
		nombreDoc = null;
	}
		
		@SuppressWarnings("unchecked")
	private Long idParaguay(){
				List<Pais> p= em.createQuery(" Select p from Pais p" +
						" where lower(p.descripcion) like 'paraguay'").getResultList();
				if(!p.isEmpty())
					return p.get(0).getIdPais();
				
				return null;
	}
	  
	@SuppressWarnings("unchecked")
	public void addDiscapacidad() {
		try {
			
			if (idDiscapacidad == null ) {
				statusMessages
						.add(Severity.ERROR,"Debe seleccionar el Item Discapacidad antes de agregar. Verifique");
				return;
			}
			if (idDiscapacidad == null || personaDiscapacidad.getGrado() == null) {
				statusMessages
						.add(Severity.ERROR,"Debe ingresar el Grado antes de agregar. Verifique");
				return;
			}
			if (personaDiscapacidad.getGrado().intValue() <= 0) {
				statusMessages
						.add(Severity.ERROR,"El valor del grado debe ser mayor a cero. Verifique");
				return;
			}
			
			
			
			if (personaDiscapacidad.getNroCertificado() != null
					&& personaDiscapacidad.getNroCertificado().intValue() <= 0) {
				statusMessages
						.add(Severity.ERROR,"El valor del Nro. de Certif debe ser mayor a cero. Verifique");
				return;
			}
			if (uploadedFile != null) {
				if (AdmDocAdjuntoFormController.validarContentType(contentType))
					crearUploadItem(fileName, uploadedFile.length, contentType,
							uploadedFile);
				else {
					statusMessages.add(Severity.ERROR,
							"No se permite la carga de ese tipo de archivos.");
					return;
				}
				
			}
			if (idTipoDoc == null) {
				statusMessages.add(Severity.ERROR,"Debe Seleccionar El tipo de Documento. Verifique");
				return;
			}
			if (item == null) {
				statusMessages.add(Severity.ERROR,"Debe seleccionar el archivo. Verifique");
				return;
			}
			
			if(personaDiscapacidad.getFechaEmision()!=null && personaDiscapacidad.getFechaEmision().after(new Date()))
			{
				statusMessages.add(Severity.ERROR,"La Fecha de Emisión no puede ser mayor a la fecha actual verifique ");
				return;
			}
			if(personaDiscapacidad.getFechaEmision()!=null && !general.isFechaValida(personaDiscapacidad.getFechaEmision())){
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha de Emisión inválida");
				return;
			}
				

			/**
			 * Para el Caso de documento adjuntos
			 * */
			if (item != null) {
				/**
				 * VALIDACION AGREGADA
				 * */
				String nomfinal = "";
				nomfinal = item.getFileName();
				 String extension = nomfinal.substring( nomfinal.lastIndexOf( "." ) );
					List<TipoArchivo> ta = em
							.createQuery(
									"select t from TipoArchivo t "
											+ " where lower(t.extension) like '" 
											+ extension.toLowerCase() + "'").getResultList();
					if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
						if (item.getFileSize() > ta.get(0).getUnidMedidaByte()
								.intValue()) {
							statusMessages
									.add(Severity.ERROR,"El archivo supera el tamaño máximo permitido. Límite "+ta.get(0).getTamanho()+ta.get(0).getUnidMedida()+", verifique");
							return ;
						}
					}
					/**
					 * FIN
					 * */
				
				try {
					documento();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				if (idDoc != null) {

					if (idDoc.longValue() == -8) {
						statusMessages
								.add(Severity.ERROR,"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
						return;
					}
					if (idDoc.longValue() == -7) {
						statusMessages
								.add(Severity.ERROR,"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
						return;
					}
					if (idDoc.longValue() == -6) {
						statusMessages
								.add(Severity.ERROR,"El archivo que desea levantar ya existe. Seleccione otro");
						return;
					}
					Documentos doc = new Documentos();
					doc = em.find(Documentos.class, idDoc);
					doc.setPersona(persona);
					em.merge(doc);
					em.flush();
					personaDiscapacidad.setDocumentos(doc);

				} else {
					statusMessages.add(Severity.ERROR,"Error al adjuntar el registro. Verifique");
					return;
				}

			}
			personaDiscapacidad.setActivo(true);
			personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosDiscapac(em
					.find(DatosEspecificos.class, idDiscapacidad));
			if (idGradoAutonom != null)
				personaDiscapacidad
						.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em
								.find(DatosEspecificos.class, idGradoAutonom));
			personaDiscapacidad.setPersona(persona);
			personaDiscapacidad.setFechaAlta(new Date());
			personaDiscapacidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(personaDiscapacidad);
			em.flush();
			discapacidadsList.add(personaDiscapacidad);

			if (personaDiscapacidad.getDocumentos() != null && idDoc != null) {
				Documentos doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(personaDiscapacidad.getIdPersonaDiscapacidad());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			idDiscapacidad = null;
			idGradoAutonom = null;
			personaDiscapacidad = new PersonaDiscapacidad();
			idTipoDoc = null;
			nombreDoc = null;
			item = null;
			inputFile = null;
			nombreDoc = null;
			tab4AdjuntarDocumentosFormController.init();
			tab6VistaPreliminarFormController.init();
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,"Error al agregar el registro");
		}
		

	}

	public void documento() throws NamingException {
		nombrePantalla = "Tab5Discapacidad_edit";
		String direccionFisica = "";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		direccionFisica = "\\SICCA\\" + "USUARIO_PORTAL\\"
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();
		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "PersonaDiscapacidad");

	}

	public void delItems(int index) {
		PersonaDiscapacidad pd = em.find(PersonaDiscapacidad.class,
				discapacidadsList.get(index).getIdPersonaDiscapacidad());
		pd.setActivo(false);
		pd.setUsuMod(usuarioLogueado.getCodigoUsuario());
		pd.setFechaMod(new Date());
		em.merge(pd);
		if(pd.getDocumentos()!=null){
			Documentos documentos= em.find(Documentos.class, pd.getDocumentos().getIdDocumento());
			documentos.setActivo(false);
			documentos.setUsuMod(usuarioLogueado.getCodigoUsuario());
			documentos.setFechaMod(new Date());
			em.merge(documentos);
		}
		
		em.flush();
		discapacidadsList.remove(index);
		tab6VistaPreliminarFormController.init();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}
	
	
	

	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));

		List<Object[]> n = em
				.createNativeQuery(
						"Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r,"
								+ "seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales"
								+ " Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();

		while (it.hasNext()) {
			Object[] fila = it.next();
			if (fila[0] != null)
				si.add(new SelectItem(fila[0], fila[1] != null ? fila[1]
						.toString() : ""));
		}

		return si;
	}

	public void miListenerAdjuntar(UploadEvent event) {
		try {

			item = event.getUploadItem();
			item.getFile();
			FileInputStream fis;
			try {
				fis = new FileInputStream(item.getFile());
			} catch (FileNotFoundException e1) {
				return;
			}

			long length = item.getFileSize();

			if (length > 5242880) {
				return;
			}

			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			try {
				while (offset < bytes.length
						&& (numRead = fis.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return;
			}

			if (offset < bytes.length) {
				try {
					throw new IOException(
							"No se pudo leer el archivo por completo "
									+ item.getFileName());
				} catch (IOException e) {
					return;
				}
			}

			try {
				fis.close();
			} catch (IOException e) {
				return;
			}

			item.getFileSize();
			nombreDoc = item.getFileName();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeNameDoc(){
		nombreDoc=null;
	}
	
	// METODOS PRIVADO
	
	
	

	@SuppressWarnings("unchecked")
	private void findDiscapacidad() {
		discapacidadsList = em.createQuery(
				" select d from PersonaDiscapacidad d where  d.activo = true and d.persona.idPersona= "
						+ persona.getIdPersona()).getResultList();
		// representante
		List<ReprPersonaDiscapacidad> rp = em.createQuery(
				"select pr from ReprPersonaDiscapacidad pr "
						+ " where pr.persona.idPersona="
						+ persona.getIdPersona()+" and pr.activo=true").getResultList();
		if (!rp.isEmpty()) {
			reprPersonaDiscapacidad = rp.get(0);
			nomRep = reprPersonaDiscapacidad.getPersonaRep().getNombres();
			apeRep = reprPersonaDiscapacidad.getPersonaRep().getApellidos();
			idPais = reprPersonaDiscapacidad.getPersonaRep()
					.getPaisByIdPaisExpedicionDoc().getIdPais();
			mail = reprPersonaDiscapacidad.getPersonaRep().getEMail();
			idNacionalidad = reprPersonaDiscapacidad.getPersonaRep()
					.getDatosEspecificos().getIdDatosEspecificos();
			docRep = reprPersonaDiscapacidad.getPersonaRep()
					.getDocumentoIdentidad();
			personaRepre=reprPersonaDiscapacidad.getPersonaRep();
			habPaisExp=false;
			guardarRepre = true;
			
		}else{
			idPais=idParaguay();
		}

	}

	private void setear() {
		discapacidadsList = new ArrayList<PersonaDiscapacidad>();
		nomRep = null;
		apeRep = null;
		docRep = null;
		idDiscapacidad = null;
		idGradoAutonom = null;
		idNacionalidad = null;
		idPais = null;
		idTipoDoc = null;
		mail = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		reprPersonaDiscapacidad = new ReprPersonaDiscapacidad();
		habPaisExp=true;

	}

	private boolean representante() {
		if (nomRep != null && !nomRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (apeRep != null && !apeRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (idNacionalidad != null) {

			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (docRep != null && !docRep.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (idPais != null) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
		}
		if (mail != null && !mail.trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}
			
			
			
		}
		if (reprPersonaDiscapacidad.getObservacion() != null
				&& !reprPersonaDiscapacidad.getObservacion().trim().equals("")) {
			if (verifObligatorios()) {
				guardarRepre = true;
				return true;
			} else {
				guardarRepre = true;
				return false;
			}

		}
		guardarRepre = false;
		return true;
	}
//	private boolean validarEmail(String email) {
//		 Pattern p = Pattern.compile("[a-zA-Z0-9]+[.[a-zA-Z0-9_-]+]*@[a-z0-9][\\w\\.-]*[a-z0-9]\\.[a-z][a-z\\.]*[a-z]$");
//        Matcher m = p.matcher(email);
//        return m.matches();
//    }
	private boolean verifObligatorios() {
		if (nomRep == null || nomRep.trim().equals("") || apeRep == null
				|| apeRep.trim().equals("") || idNacionalidad == null
				|| docRep == null || docRep.trim().equals("") || idPais == null) {
			return false;
		}
		return true;

	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	// GETTERS Y SETTERS 	

	public PersonaDiscapacidad getPersonaDiscapacidad() {
		return personaDiscapacidad;
	}

	public void setPersonaDiscapacidad(PersonaDiscapacidad personaDiscapacidad) {
		this.personaDiscapacidad = personaDiscapacidad;
	}

	public List<PersonaDiscapacidad> getDiscapacidadsList() {
		return discapacidadsList;
	}

	public void setDiscapacidadsList(List<PersonaDiscapacidad> discapacidadsList) {
		this.discapacidadsList = discapacidadsList;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdNacionalidad() {
		return idNacionalidad;
	}

	public void setIdNacionalidad(Long idNacionalidad) {
		this.idNacionalidad = idNacionalidad;
	}

	public Long getIdDiscapacidad() {
		return idDiscapacidad;
	}

	public void setIdDiscapacidad(Long idDiscapacidad) {
		this.idDiscapacidad = idDiscapacidad;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public Long getIdGradoAutonom() {
		return idGradoAutonom;
	}

	public void setIdGradoAutonom(Long idGradoAutonom) {
		this.idGradoAutonom = idGradoAutonom;
	}

	public ReprPersonaDiscapacidad getReprPersonaDiscapacidad() {
		return reprPersonaDiscapacidad;
	}

	public void setReprPersonaDiscapacidad(
			ReprPersonaDiscapacidad reprPersonaDiscapacidad) {
		this.reprPersonaDiscapacidad = reprPersonaDiscapacidad;
	}

	public String getNomRep() {
		return nomRep;
	}

	public void setNomRep(String nomRep) {
		this.nomRep = nomRep;
	}

	public String getApeRep() {
		return apeRep;
	}

	public void setApeRep(String apeRep) {
		this.apeRep = apeRep;
	}

	public String getDocRep() {
		return docRep;
	}

	public void setDocRep(String docRep) {
		this.docRep = docRep;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public boolean isGuardarRepre() {
		return guardarRepre;
	}

	public void setGuardarRepre(boolean guardarRepre) {
		this.guardarRepre = guardarRepre;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public Persona getPersonaRepre() {
		return personaRepre;
	}

	public void setPersonaRepre(Persona personaRepre) {
		this.personaRepre = personaRepre;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
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

	
	
}
