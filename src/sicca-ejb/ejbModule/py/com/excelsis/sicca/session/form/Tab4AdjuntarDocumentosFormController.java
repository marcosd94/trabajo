package py.com.excelsis.sicca.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.print.Doc;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Naming;
import org.jboss.system.server.profileservice.repository.clustered.local.file.FileUtil;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.HorasAnios;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.EstudiosRealizadosLegajo;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.NivelEstudio;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosList;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.PAGE)
@Name("tab4AdjuntarDocumentosFormController")
public class Tab4AdjuntarDocumentosFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4633189665635912313L;

	@RequestParameter
	Long personaIdPersona;

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In(value = "entityManager")
	EntityManager em;

	@PersistenceUnit
	EntityManagerFactory emf;

	@In(required = false)
	Usuario usuarioLogueado;

	private List<Documentos> documentoDTOList = new ArrayList<Documentos>();
	private Documentos documentos;
	private Long idTipoDoc;
	private Integer numTipoDoc;
	private String descTipoDoc;
	private Boolean esOtro;
	
	private Persona persona;
	boolean esEdit;
	
	/**
	 * DOCUMENTO ADJUNTO
	 **/
	private UploadItem item;
	private String nombreDoc;
	private String nombrePantalla;
	private Long idDoc;
	private Long idPersona;
	private byte[] uploadedFile;
	private String uploadedFileToString;
	private String contentType;
	private String fileName;
	private String texto;
	private List<DatosEspecificos> listaDE = new ArrayList<DatosEspecificos>();
	private Long id1;
	private Long id2;
	private Documentos documentoEditable;
	
	private Date fechaActo;

	/****
	 * 
	 * */
	public void init() {
		try {
			cargarLista();
			idPersona = usuarioLogueado.getPersona().getIdPersona();
			documentos = new Documentos();
			documentoDTOList = new ArrayList<Documentos>();
			idTipoDoc = null;
			nombreDoc = null;
			numTipoDoc = null;
			descTipoDoc = null;
			esEdit = false;
			esOtro = false;
			fechaActo = null;
			persona = em.find(Persona.class, usuarioLogueado.getPersona()
					.getIdPersona());
			findDocs();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void initLegajo() {
		try {
			cargarLista();
			documentos = new Documentos();
			documentoDTOList = new ArrayList<Documentos>();
			idTipoDoc = null;
			nombreDoc = null;
			numTipoDoc = null;
			descTipoDoc = null;
			esEdit = false;
			fechaActo = null;
			if(idPersona != null)
				persona = em.find(Persona.class, idPersona);
			findDocsLegajo();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("unchecked")
	public void cargarLista() {
		listaDE = (List<DatosEspecificos>) em
				.createQuery(
						"select datosEspecificos from DatosEspecificos datosEspecificos "
								+ " where datosEspecificos.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS'"
								+ " and datosEspecificos.valorAlf like 'FPOS' ")
				.getResultList();
	}

	public void initCarpeta() {
		try {
			idPersona = personaIdPersona;
			documentos = new Documentos();
			documentoDTOList = new ArrayList<Documentos>();
			idTipoDoc = null;
			persona = em.find(Persona.class, usuarioLogueado.getPersona()
					.getIdPersona());
			findDocs();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void adddocumentos() {

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
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Tipo de Documento");
			return;
		}
		if (item == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Archivo");
			return;
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
					return;
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
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return;
				}
				if (idDoc.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return;
				}
				if (idDoc.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return;
				}

				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(persona.getIdPersona());
				doc.setPersona(em.find(Persona.class, idPersona));
				em.merge(doc);
				em.flush();
				documentoDTOList.add(doc);
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				
				if (cuentaConLegajo())
					this.adddocumentosLegajo();
				
				idTipoDoc = null;
				idDoc = null;
				item = null;
				nombreDoc = null;
				findDocs();
				
			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return;
			}
		}
	}
	
	
	public Boolean cuentaConLegajo() {
		Query q = em.createQuery("select l from Legajos l"
				+ " where  l.persona.idPersona = :id");
		q.setParameter("id", usuarioLogueado.getPersona().getIdPersona());
		List<Legajos> lista = q.getResultList();
		
		Query q1 = em
					.createQuery("select e from EmpleadoPuesto e"
							+ " where e.actual is true and e.activo is true and  e.persona.idPersona = :id1");
			q1.setParameter("id1", usuarioLogueado.getPersona().getIdPersona());
			List<Legajos> lista2 = q1.getResultList();
			
				
		if (!lista.isEmpty() && !lista2.isEmpty())
			return true;
		return false;
	}

	public void documento() throws NamingException {
		nombrePantalla = "Tab4AdjuntarDocumentos_edit";
		String direccionFisica = "";
		SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
		String anio = sdfSoloAnio.format(new Date());
		direccionFisica = "\\SICCA" + "\\USUARIO_PORTAL\\"
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();
		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "Persona");

	}
	
	
	@SuppressWarnings("unchecked")
	public void adddocumentosLegajo() {
				
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem("legajo_"+fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}

		}

		if (idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Tipo de Documento");
			return;
		}
		if (item == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Archivo");
			return;
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
					return;
				}
			}
			/**
			 * FIN
			 * */
			try {
				documentoLegajo();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			if (idDoc != null) {

				if (idDoc.longValue() == -8) {
					statusMessages
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return;
				}
				if (idDoc.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return;
				}
				if (idDoc.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return;
				}

				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(persona.getIdPersona());
				doc.setPersona(em.find(Persona.class, idPersona));
				em.merge(doc);
				em.flush();
				documentoDTOList.add(doc);
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				idTipoDoc = null;
				idDoc = null;
				item = null;
				nombreDoc = null;
				findDocsLegajo();

			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return;
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void adddocumentosLegajoValidado() {
				
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem("legajo_"+fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}

		}

		if (idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Tipo de Documento");
			return;
		}
		if (item == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Archivo");
			return;
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
					return;
				}
			}
			/**
			 * FIN
			 * */
			try {
				documentoLegajo();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			if (idDoc != null) {

				if (idDoc.longValue() == -8) {
					statusMessages
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return;
				}
				if (idDoc.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return;
				}
				if (idDoc.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return;
				}

				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(persona.getIdPersona());
				doc.setPersona(em.find(Persona.class, idPersona));//nroDocumento
				doc.setValidoLegajo(true);
				doc.setNroDocumento(numTipoDoc);
				doc.setDescripcion(descTipoDoc);
				em.merge(doc);
				em.flush();
				documentoDTOList.add(doc);
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				idTipoDoc = null;
				idDoc = null;
				item = null;
				nombreDoc = null;
				descTipoDoc = null;
				numTipoDoc = null;
				findDocsLegajo();

			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void updatedocumentosLegajoValidado() {
		
		if (uploadedFile == null) {
			documentoEditable.setDatosEspecificos(em.find(DatosEspecificos.class, idTipoDoc));
			documentoEditable.setNombreFisicoDoc(nombreDoc);
			documentoEditable.setNroDocumento(numTipoDoc);
			documentoEditable.setDescripcion(descTipoDoc);
			documentoEditable.setFechaMod(new Date());
			documentoEditable.setFechaDoc(fechaActo);
			em.merge(documentoEditable);
			em.flush();
		}		
		else {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem("legajo_"+fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return;
			}

		}

		/**
		 * Para el Caso de documento adjuntos
		 */
		if (item != null) {
			/**
			 * VALIDACION AGREGADA
			 * */
			documentoEditable.setActivo(false);
			em.merge(documentoEditable);
			em.flush();
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
					return;
				}
			}
			/**
			 * FIN
			 * */
			try {
				documentoLegajo();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			if (idDoc != null) {

				if (idDoc.longValue() == -8) {
					statusMessages
							.add(Severity.ERROR,
									"La cantidad de archivos permitidos supera lo permitido. Consulte con el administrador del sistema");
					return;
				}
				if (idDoc.longValue() == -7) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar, supera el tamaño permitido. Seleccione otro");
					return;
				}
				if (idDoc.longValue() == -6) {
					statusMessages
							.add(Severity.ERROR,
									"El archivo que desea levantar ya existe. Seleccione otro");
					return;
				}

				Documentos doc = new Documentos();
				doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(persona.getIdPersona());
				doc.setPersona(em.find(Persona.class, idPersona));//nroDocumento
				doc.setValidoLegajo(true);
				doc.setNroDocumento(numTipoDoc);
				doc.setDescripcion(descTipoDoc);
				em.merge(doc);
				em.flush();
				documentoDTOList.add(doc);
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				idTipoDoc = null;
				idDoc = null;
				item = null;
				nombreDoc = null;
				descTipoDoc = null;
				numTipoDoc = null;
				findDocsLegajo();

			} else {
				statusMessages.add("Error al adjuntar el registro. Verifique");
				return;
			}
		}
		esEdit = false;
	}
	
	public void cancelar() {
		idTipoDoc = null;
		nombreDoc = null;
		numTipoDoc = null;
		descTipoDoc = null;
		esEdit = false;
	}
	
	public void documentoLegajo() throws NamingException {
		nombrePantalla = "adjuntarDocumentos_legajo";
		String separador = File.separator;
		String direccionFisica = separador
		+ "LEGAJO" + separador
		+ persona.getDocumentoIdentidad() + "_"
		+ persona.getIdPersona()+ separador+"DOC_ADJUNTOS"+ separador;
		idDoc = AdmDocAdjuntoFormController.guardarConFechaDoc(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "Persona", fechaActo);

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
	
	@SuppressWarnings("unchecked")
	public void validarLegajo(int index){
		Documentos aux = em.find(Documentos.class,
				documentoDTOList.get(index).getIdDocumento());
		aux.setValidoLegajo(true);
		aux.setFechaMod(new Date());
		aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(aux);
		em.flush();
	}
	
	@SuppressWarnings("unchecked")
	public void invalidarLegajo(int index){
		Documentos aux = em.find(Documentos.class,
				documentoDTOList.get(index).getIdDocumento());
		aux.setValidoLegajo(false);
		aux.setFechaMod(new Date());
		aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(aux);
		em.flush();
	}

	@SuppressWarnings("unchecked")
	public void eliminar(int index) {

		try {

			Documentos aux = em.find(Documentos.class,
					documentoDTOList.get(index).getIdDocumento());
			aux.setActivo(false);
			aux.setFechaMod(new Date());
			aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(aux);
			em.flush();
			if (aux.getNombreTabla() != null) {
				if (aux.getNombreTabla().equals("EstudiosRealizados")) {
					List<EstudiosRealizados> eList = em.createQuery(
							"Select e from EstudiosRealizados e "
									+ " where e.documentos.idDocumento ="
									+ aux.getIdDocumento()).getResultList();
					EstudiosRealizados e = em.find(EstudiosRealizados.class,
							eList.get(0).getIdEstudiosRealizados());
					e.setDocumentos(null);
					e.setUsuMod(usuarioLogueado.getCodigoUsuario());
					e.setFechaMod(new Date());
					em.merge(e);
					em.flush();
				}
				if (aux.getNombreTabla().equals("PersonaDiscapacidad")) {
					List<PersonaDiscapacidad> pdList = em.createQuery(
							"Select e from PersonaDiscapacidad e "
									+ " where e.documentos.idDocumento ="
									+ aux.getIdDocumento()).getResultList();
					PersonaDiscapacidad p = em.find(PersonaDiscapacidad.class,
							pdList.get(0).getIdPersonaDiscapacidad());
					p.setDocumentos(null);
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					em.merge(p);
					em.flush();
				}
				if (aux.getNombreTabla().equals("ExperienciaLaboral")) {
					List<ExperienciaLaboral> elaboList = em.createQuery(
							"Select e from ExperienciaLaboral e "
									+ " where e.documentos.idDocumento ="
									+ aux.getIdDocumento()).getResultList();
					ExperienciaLaboral ex = em.find(ExperienciaLaboral.class,
							elaboList.get(0).getIdExperienciaLab());
					ex.setDocumentos(null);
					ex.setUsuMod(usuarioLogueado.getCodigoUsuario());
					ex.setFechaMod(new Date());
					em.merge(ex);
					em.flush();
				}
			}

			documentoDTOList.remove(index);
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void editar(int index) {
		esEdit = true;
		try {

			documentoEditable = em.find(Documentos.class,
					documentoDTOList.get(index).getIdDocumento());
			
			idTipoDoc = documentoEditable.getDatosEspecificos().getIdDatosEspecificos();
			nombreDoc = documentoEditable.getNombreFisicoDoc();
			numTipoDoc = documentoEditable.getNroDocumento();
			descTipoDoc = documentoEditable.getDescripcion();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Boolean verificar(Long idDatosEspecificos) {
		Boolean p = false;
		for (DatosEspecificos o : listaDE)
			if(idDatosEspecificos.equals(o.getIdDatosEspecificos()))
					p = true;
		return p;
	}

	public void abrirDoc(int index) {

		Documentos d = documentoDTOList.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(d.getIdDocumento(),
				usuarioLogueado.getIdUsuario());

	}

	@SuppressWarnings("unchecked")
	private void findDocs() {

		documentoDTOList = em.createQuery(
				" Select d from Documentos d "
						+ " where d.activo= TRUE  and d.persona.idPersona ="
						+ idPersona 
						+ " and d.nombrePantalla = 'Tab4AdjuntarDocumentos_edit'"
						+ " order by d.fechaDoc desc ")
				.getResultList();

	}
	
	@SuppressWarnings("unchecked")
	private void findDocsLegajo() {

		documentoDTOList = em.createQuery(
				" Select d from Documentos d "
						+ " where d.activo= TRUE "
						+ " and d.nombrePantalla = 'adjuntarDocumentos_legajo'"
						+ " and d.persona.idPersona ="
						+ idPersona + " order by d.fechaAlta desc ")
				.getResultList();

	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

	// GETTERS Y SETTERS

	public List<Documentos> getDocumentoDTOList() {
		return documentoDTOList;
	}

	public void setDocumentoDTOList(List<Documentos> documentoDTOList) {
		this.documentoDTOList = documentoDTOList;
	}

	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
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

	public List<DatosEspecificos> getListaDE() {
		return listaDE;
	}

	public void setListaDE(List<DatosEspecificos> listaDE) {
		this.listaDE = listaDE;
	}

	public Long getId1() {
		return id1;
	}

	public void setId1(Long id1) {
		this.id1 = id1;
	}

	public Long getId2() {
		return id2;
	}

	public void setId2(Long id2) {
		this.id2 = id2;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getUploadedFileToString() {
		return uploadedFileToString;
	}

	public void setUploadedFileToString(String uploadedFileToString) {
		this.uploadedFileToString = uploadedFileToString;
	}
	
	public String uploadedFileToString(){
		if(uploadedFile != null)
			this.uploadedFileToString = new String (uploadedFile);
		
		return uploadedFileToString;
	}
	
	
	public byte [] stringToUploadedFile(){
		return uploadedFileToString.getBytes();
	}
	
	public Integer getNumTipoDoc() {
		return numTipoDoc;
	}

	public void setNumTipoDoc(Integer numTipoDoc) {
		this.numTipoDoc = numTipoDoc;
	}
	

	public String getDescTipoDoc() {
		return descTipoDoc;
	}

	public void setDescTipoDoc(String descTipoDoc) {
		this.descTipoDoc = descTipoDoc;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public Documentos getDocumentoEditable() {
		return documentoEditable;
	}

	public void setDocumentoEditable(Documentos documentoEditable) {
		this.documentoEditable = documentoEditable;
	}

	public Boolean getEsOtro(){
		return esOtro;
	}
	
	public void setEsOtro(Boolean esOtro){
		this.esOtro = esOtro;
	}
	
	public void esOtroTipo() {
		if(idTipoDoc != null){
			DatosEspecificos datos= em.find(DatosEspecificos.class, idTipoDoc);
			if(datos.getDescripcion().equalsIgnoreCase("OTROS")){
				esOtro=true;
			} else {
				esOtro=false;
			}
		}
	}

	public Date getFechaActo() {
		return fechaActo;
	}

	public void setFechaActo(Date fechaActo) {
		this.fechaActo = fechaActo;
	}

}
