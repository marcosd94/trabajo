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

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.capacitacion.session.form.Tab1DatosPersonalesPorCarpetaFC;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.PersonaDiscapacidadHome;
import py.com.excelsis.sicca.session.PersonaHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("tab5DiscapacidadFC")
public class Tab5DiscapacidadFC implements Serializable {

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
	@In(create = true, required = false)
	Tab1DatosPersonalesPorCarpetaFC tab1DatosPersonalesPorCarpetaFC;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@RequestParameter
	private Boolean msjOk;
	@RequestParameter
	private Long personaIdPersona;
	@RequestParameter
	private Long concursoPuestoAgrIdConcursoPuestoAgr;

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
	private Boolean primeraVez = false;
	private String msjCopiaRealizada;
	private Postulacion postulacionCopia;
	private PersonaDTO personaDTO;

	private int indexUp;

	/**
	 * DOCUMENTO ADJUNTO
	 **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private String nombrePantalla = "Tab5DiscapacidadCarpeta_edit";
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;
	private Long idGrupo;
	private Long idPersona;

	private Boolean precondInit() {
		if (concursoPuestoAgrIdConcursoPuestoAgr == null || personaIdPersona == null) {
	
			return false;
		} else {
			idGrupo = concursoPuestoAgrIdConcursoPuestoAgr;
			idPersona = personaIdPersona;
		}
		return true;
	}

	private Boolean precondInit2() {
		if (idGrupo == null || idPersona == null) {
		
			return false;
		}
		return true;
	}

	public void init() {

		if (!precondInit())
			return;
		primeraVez = verficarCopiaRealizada(personaIdPersona, concursoPuestoAgrIdConcursoPuestoAgr);
		setear();
		guardarRepre = false;
		persona = em.find(Persona.class, personaIdPersona);
		if (persona != null)
			findDiscapacidad();
		esEdit = false;
	}

	public void initTab() {
		if (primeraVez == null)
			statusMessages.add(Severity.ERROR, "Debe cargar la Ficha de Datos Personales");
		else {
			statusMessages.clear();
		}
	}

	/**
	 * Indica si es la primera vez (no se ha realizado aún la copia de la postulación)
	 */
	public Boolean verficarCopiaRealizada(Long idPersona, Long idGrupo) {
		/* Si estamos acá se asume que existe postulación y que se ha cargado personaPostulante para la misma */

		Boolean primeraVez = false;
		tab1DatosPersonalesPorCarpetaFC =
			(Tab1DatosPersonalesPorCarpetaFC) Component.getInstance(Tab1DatosPersonalesPorCarpetaFC.class, true);
		postulacionCopia =
			tab1DatosPersonalesPorCarpetaFC.verficarCopiaRealizada(idPersona, idGrupo);
		if (postulacionCopia == null || postulacionCopia.getPersonaPostulante() == null) {
			return null;
		}

		Query q =
			em.createQuery(" select PersonaDiscapacidad from PersonaDiscapacidad PersonaDiscapacidad "
				+ " where PersonaDiscapacidad.activo is true "
				+ " and PersonaDiscapacidad.personaPostulante.idPersonaPostulante =:idPersonaPostulante ");
		q.setParameter("idPersonaPostulante", postulacionCopia.getPersonaPostulante().getIdPersonaPostulante());
		List lista = q.getResultList();
		if (lista.size() == 0) {
			primeraVez = true;
		} else {
			primeraVez = false;
		}

		return primeraVez;
	}

	public Postulacion getPostulacionCopia() {
		return postulacionCopia;
	}

	public void setPostulacionCopia(Postulacion postulacionCopia) {
		this.postulacionCopia = postulacionCopia;
	}

	public void verficarCopiaRealizada2() {
		if (primeraVez == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		if (primeraVez) {
			msjCopiaRealizada = "¿Está seguro de realizar la acción?";
		} else {
			msjCopiaRealizada =
				"Se guardarán los Datos de Discapacidad para la Postulación actual, este paso se realiza una sola vez.";
		}
	}

	public String guardar() {
		if (!precondInit2())
			return null;
		if (primeraVez == null) {
			statusMessages.add(Severity.ERROR, "No se puede verificar si ya se realizó la copia");
			return null;
		}
		if (primeraVez) {
			try {
				PersonaDiscapacidad pd = saveOriginal();
				if (pd == null) {
					return null;
				}
				saveCopia(pd.getDocumentos());
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				limpiar();
				cargarListaDisca();
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return null;
			}
		} else {
			// N+2 vez, donde N [0,infinito)
			try {
				PersonaDiscapacidad pd = saveOriginal();
				if (pd == null) {
					return null;
				}
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				limpiar();
				cargarListaDisca();
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
				return null;
			}
		}
	}

	private Long guardarDocAdjCopia(Documentos documentos, Long idPostulacion) throws IOException {
		Long idDocCopia;
		if (documentos != null)
			documentos = em.find(Documentos.class, documentos.getIdDocumento());
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class, idGrupo);
		Long idOee = agr.getConcurso().getConfiguracionUoCab().getIdConfiguracionUo();
		Long idTipoConcurso =
			agr.getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos();
		String nro_doc_ID = persona.getDocumentoIdentidad() + "_" + persona.getIdPersona();
		String direccionFisica =
			File.separator + "SICCA" + File.separator + General.anhoActual() + File.separator
				+ "OEE" + File.separator + idOee + File.separator + idTipoConcurso + File.separator
				+ agr.getConcurso().getIdConcurso() + File.separator + idGrupo + File.separator
				+ "POSTULANTES" + File.separator + nro_doc_ID;
		File fileFromDoc =
			new File(documentos.getUbicacionFisica() + File.separator
				+ documentos.getNombreFisicoDoc());
		byte[] archivoCopia = JasperReportUtils.getBytesFromFile(fileFromDoc);
		idDocCopia =
			admDocAdjuntoFormController.hacerCopiaDocPostulantes(idPostulacion, direccionFisica, archivoCopia, nombrePantalla
				+ "_copia", documentos);
		return idDocCopia;
	}

	private Long guardarDocAdjOriginal(String fileName, int fileSize, String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {

			UploadItem fileItem =
				seleccionUtilFormController.crearUploadItem(fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "PersonaDiscapacidad";
			String nro_doc_ID = persona.getDocumentoIdentidad() + "_" + persona.getIdPersona();
			String direccionFisica =
				File.separator + "SICCA" + File.separator + "USUARIO_PORTAL" + File.separator
					+ nro_doc_ID;
			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(fileItem, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public PersonaDiscapacidad saveOriginal() {
		if (!precondSaves())
			return null;
		PersonaDiscapacidad personaDiscapacidad1 = new PersonaDiscapacidad();
		personaDiscapacidad1.setActivo(true);
		personaDiscapacidad1.setActividadRealizar(personaDiscapacidad.getActividadRealizar());
		personaDiscapacidad1.setCausa(personaDiscapacidad.getCausa());
		personaDiscapacidad1.setDatosEspecificosByIdDatosEspecificosDiscapac(em.find(DatosEspecificos.class, idDiscapacidad));
		if (idGradoAutonom != null)
			personaDiscapacidad1.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em.find(DatosEspecificos.class, idGradoAutonom));
		personaDiscapacidad1.setDificultadActividad(personaDiscapacidad.getDificultadActividad());
		personaDiscapacidad1.setDocumentos(personaDiscapacidad.getDocumentos());
		personaDiscapacidad1.setFechaAlta(new Date());
		personaDiscapacidad1.setFechaEmision(personaDiscapacidad.getFechaEmision());
		personaDiscapacidad1.setGrado(personaDiscapacidad.getGrado());
		personaDiscapacidad1.setNroCertificado(personaDiscapacidad.getNroCertificado());
		personaDiscapacidad1.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
		personaDiscapacidad1.setFechaAltaOee(new Date());
		personaDiscapacidad1.setUsuAlta(tab1DatosPersonalesPorCarpetaFC.usuarioPostulante(idPersona).getCodigoUsuario());
		personaDiscapacidad1.setTipo("CARPETA");
		personaDiscapacidad1.setPersona(new Persona());
		personaDiscapacidad1.getPersona().setIdPersona(idPersona);
		personaDiscapacidad1.setPersonaPostulante(null);
		if (uploadedFile != null) {
			Long idDoc =
				guardarDocAdjOriginal(item.getFileName(), item.getFileSize(), item.getContentType(), uploadedFile, idTipoDoc, null);

			if (idDoc == null)
				return null;
			personaDiscapacidad1.setDocumentos(new Documentos());
			personaDiscapacidad1.getDocumentos().setIdDocumento(idDoc);

		}

		em.persist(personaDiscapacidad1);
		return personaDiscapacidad1;
	}

	private String saveCopia(Documentos doc) throws IOException {

		PersonaDiscapacidad personaDiscapacidad2 = new PersonaDiscapacidad();
		personaDiscapacidad2.setActivo(true);
		personaDiscapacidad2.setActividadRealizar(personaDiscapacidad.getActividadRealizar());
		personaDiscapacidad2.setCausa(personaDiscapacidad.getCausa());
		personaDiscapacidad2.setDatosEspecificosByIdDatosEspecificosDiscapac(em.find(DatosEspecificos.class, idDiscapacidad));
		if (idGradoAutonom != null)
			personaDiscapacidad2.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em.find(DatosEspecificos.class, idGradoAutonom));
		personaDiscapacidad2.setDificultadActividad(personaDiscapacidad.getDificultadActividad());
		personaDiscapacidad2.setDocumentos(personaDiscapacidad.getDocumentos());
		personaDiscapacidad2.setFechaAlta(new Date());
		personaDiscapacidad2.setFechaEmision(personaDiscapacidad.getFechaEmision());
		personaDiscapacidad2.setGrado(personaDiscapacidad.getGrado());
		personaDiscapacidad2.setNroCertificado(personaDiscapacidad.getNroCertificado());
		personaDiscapacidad2.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
		personaDiscapacidad2.setFechaAltaOee(new Date());
		personaDiscapacidad2.setUsuAlta(tab1DatosPersonalesPorCarpetaFC.usuarioPostulante(idPersona).getCodigoUsuario());
		personaDiscapacidad2.setTipo("CARPETA");
		personaDiscapacidad2.setPersona(null);
		personaDiscapacidad2.setPersonaPostulante(new PersonaPostulante());
		personaDiscapacidad2.getPersonaPostulante().setIdPersonaPostulante(postulacionCopia.getPersonaPostulante().getIdPersonaPostulante());
		if (doc != null) {
			Long idDoc = guardarDocAdjCopia(doc, postulacionCopia.getIdPostulacion());
			if (idDoc == null)
				return null;
			personaDiscapacidad2.setDocumentos(new Documentos());
			personaDiscapacidad2.getDocumentos().setIdDocumento(idDoc);
		}
		em.persist(personaDiscapacidad2);
		return "OK";
	}

	@SuppressWarnings("unchecked")
	public String save() {
		try {
			if (idPais == null) {
				statusMessages.add(Severity.ERROR, "Debe seleccionar el País. Verifique");
				return null;
			}
			if (docRep == null || docRep.trim().equals("")) {
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Doc. de Identidad. Verifique");
				return null;
			}
			if (idNacionalidad == null) {
				statusMessages.add(Severity.ERROR, "Debe seleccionar la Nacionalidad. Verifique");
				return null;
			}
			if (nomRep == null || nomRep.trim().equals("")) {
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Nombre del Representante. Verifique");
				return null;
			}
			if (apeRep == null || apeRep.trim().equals("")) {
				statusMessages.add(Severity.ERROR, "Debe Ingresar el Apellido del Representante. Verifique");
				return null;
			}

			if (!representante() && guardarRepre) {
				statusMessages.add(Severity.WARN, " Debe verificar los campos obligatorios del Representante");
				return null;
			}
			if (mail != null && !mail.trim().equals("")) {
				if (!General.isEmail((mail.toLowerCase()))) {
					statusMessages.add(Severity.WARN, "Ingrese un mail valido. Verifique");
					return null;
				}
				mail = mail.toLowerCase();

			}

			if (personaRepre.getIdPersona() == null) {
				List<Persona> p =
					em.createQuery("Select p from Persona p " + " where p.documentoIdentidad='"
						+ docRep + "' and p.paisByIdPaisExpedicionDoc.idPais=" + idPais).getResultList();
				if (!p.isEmpty()) {
					statusMessages.clear();
					statusMessages.add("La persona ingresada ya existe. Verifique");

				}

			}

			if (guardarRepre) {
				if (personaRepre.getIdPersona() != null) {
					// Actualiza los datos de la persona
					personaRepre.setEMail(mail);
					personaRepre.setNombres(nomRep);
					personaRepre.setApellidos(apeRep);
					personaRepre.setUsuMod(usuarioLogueado.getCodigoUsuario());
					personaRepre.setFechaMod(new Date());

					personaRepre = em.merge(personaRepre);
					em.flush();
					personaHome.setInstance(personaRepre);

				} else {
					// crea una nueva persona
					personaRepre.setEMail(mail);
					personaRepre.setNombres(nomRep);
					personaRepre.setApellidos(apeRep);
					personaRepre.setPaisByIdPaisExpedicionDoc(em.find(Pais.class, idPais));
					personaRepre.setDatosEspecificos(em.find(DatosEspecificos.class, idNacionalidad));
					personaRepre.setDocumentoIdentidad(docRep);
					personaRepre.setFechaAlta(new Date());
					personaRepre.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(personaRepre);
					em.flush();
					personaHome.setInstance(personaRepre);

				}
				if (reprPersonaDiscapacidad.getPersona() == null) {
					reprPersonaDiscapacidad.setPersona(new Persona());
					reprPersonaDiscapacidad.getPersona().setIdPersona(idPersona);
				}
				reprPersonaDiscapacidad.setActivo(true);
				reprPersonaDiscapacidad.setPersonaRep(personaHome.getInstance());
				if (reprPersonaDiscapacidad.getIdReprPersonaDiscapacidad() != null) {
					reprPersonaDiscapacidad.setFechaMod(new Date());
					reprPersonaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(reprPersonaDiscapacidad);
				} else {
					reprPersonaDiscapacidad.setFechaAlta(new Date());
					reprPersonaDiscapacidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					em.persist(reprPersonaDiscapacidad);
				}
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			}

			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public void eliminar() {
		reprPersonaDiscapacidad.setActivo(false);
		reprPersonaDiscapacidad.setFechaMod(new Date());
		reprPersonaDiscapacidad.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.merge(reprPersonaDiscapacidad);
		em.flush();
		reprPersonaDiscapacidad = new ReprPersonaDiscapacidad();
		habPaisExp = true;
		personaRepre = new Persona();
		nomRep = null;
		apeRep = null;
		idPais = null;
		idNacionalidad = null;
		docRep = null;
		mail = null;
		guardarRepre = false;
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

	}

	@SuppressWarnings("unchecked")
	public String update() {
		try {
			if (!precondSaves())
				return null;

			esEdit = false;
			personaDiscapacidad.setActivo(true);
			personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosDiscapac(em.find(DatosEspecificos.class, idDiscapacidad));
			if (idGradoAutonom != null)
				personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosGradoAutonom(em.find(DatosEspecificos.class, idGradoAutonom));
			else
				personaDiscapacidad.setDatosEspecificosByIdDatosEspecificosGradoAutonom(null);
			personaDiscapacidad.setPersona(persona);
			personaDiscapacidad.setFechaModOee(new Date());
			personaDiscapacidad.setUsuModOee(usuarioLogueado.getCodigoUsuario());
			personaDiscapacidad.setFechaMod(new Date());
			personaDiscapacidad.setUsuMod(tab1DatosPersonalesPorCarpetaFC.usuarioPostulante(idPersona).getCodigoUsuario());
			discapacidadsList.set(indexUp, personaDiscapacidad);

			/*
			 * Se edita el documento
			 */
			Long idDoc = null;
			if (inputFile != null && item != null) {
				if (personaDiscapacidad.getDocumentos() != null) {
					Documentos docDB =
						em.find(Documentos.class, personaDiscapacidad.getDocumentos().getIdDocumento());
					if (!docDB.getNombreFisicoDoc().toLowerCase().equals(nombreDoc.toLowerCase())
						|| !docDB.getTamanhoDoc().equals(String.valueOf(item.getFileSize()))) {
						idDoc =
							admDocAdjuntoFormController.guardarDoc(item, docDB.getUbicacionFisica(), nombrePantalla, idTipoDoc, "PersonaDsicapacidad", docDB.getIdDocumento());

						Documentos doc = new Documentos();
						doc = em.find(Documentos.class, idDoc);
						doc.setPersona(persona);
						doc = em.merge(doc);
						em.flush();
						personaDiscapacidad.setDocumentos(doc);

					} else {
						statusMessages.add("Error al adjuntar el registro. Verifique");
						return null;
					}
				}
			}

			/*
			 * Nuevo documento (lo indica inputFile), item!=null indica que se quiere crear uno
			 */
			if (inputFile == null && item != null) {
				if (personaDiscapacidad.getDocumentos() == null) {
					String direccionFisica = "";
					SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
					String anio = sdfSoloAnio.format(new Date());
					direccionFisica =
						"\\SICCA\\" + "USUARIO_PORTAL\\" + persona.getDocumentoIdentidad() + "_"
							+ persona.getIdPersona();
					idDoc =
						admDocAdjuntoFormController.guardarDoc(item, direccionFisica, nombrePantalla, idTipoDoc, "PersonaDsicapacidad", null);
					if (idDoc != null) {
						Documentos doc = new Documentos();
						doc = em.find(Documentos.class, idDoc);
						doc.setPersona(persona);
						em.merge(doc);
						em.flush();
						personaDiscapacidad.setDocumentos(doc);
					} else {
						statusMessages.add(Severity.ERROR, "Error al adjuntar el registro. Verifique");
						return null;
					}
				}
			}
			personaDiscapacidad = em.merge(personaDiscapacidad);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
			limpiarEdit();
			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
			esEdit = true;
			idDiscapacidad = personaDiscapacidad.getIdPersonaDiscapacidad();
		}
		return null;
	}

	private void limpiarEdit() {
		idDiscapacidad = null;
		idGradoAutonom = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		idTipoDoc = null;
		nombreDoc = null;
		esEdit = false;
		item = null;
		inputFile = null;
		nombreDoc = null;
	}

	private void completarDatosPersona(Persona persona, Boolean completo) {
		nomRep = persona.getNombres() + " " + persona.getApellidos();
		apeRep = persona.getTelefonos() == null ? "" : persona.getTelefonos();
		mail = persona.getEMail() == null ? "" : persona.getEMail();
		personaRepre = persona;
		if (persona.getDatosEspecificos() != null)
			idNacionalidad = persona.getDatosEspecificos().getIdDatosEspecificos();

		if (completo) {
			idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docRep = persona.getDocumentoIdentidad();

		}
	}

	public void buscarPersona() throws Exception {

		/* Validaciones */
		if (idPais == null
			|| !seguridadUtilFormController.validarInput(idPais.toString(), TiposDatos.LONG.getValor(), null)) {
			return;
		}
		Pais pais = em.find(Pais.class, idPais);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docRep, pais.getDescripcion());

		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());

			limpiarDatosPersona2();
			persona = null;
		} else if (personaDTO.getHabilitarBtn()) {

			persona = null;
			limpiarDatosPersona2();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {

			persona = personaDTO.getPersona();
			completarDatosPersona(personaDTO.getPersona(), false);
		}
	}

	public void limpiarDatosPersona2() {
		nomRep = null;
		apeRep = null;
		mail = null;
		idNacionalidad = null;
	}

	public void limpiarRepresentante() {
		personaRepre = new Persona();
		nomRep = null;
		apeRep = null;
		idNacionalidad = null;
		mail = null;
		docRep = null;
	}

	public void cancelar() {
		idDiscapacidad = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		idGradoAutonom = null;
		esEdit = false;
		idTipoDoc = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		nombreDoc = null;
		item = null;
		inputFile = null;

	}

	private Boolean precondSaves() {
		if (idDiscapacidad == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el Item Discapacidad antes de agregar. Verifique");
			return false;
		}
		if (idDiscapacidad == null || personaDiscapacidad.getGrado() == null) {
			statusMessages.add(Severity.ERROR, "Debe ingresar el Grado antes de agregar. Verifique");
			return false;
		}
		if (personaDiscapacidad.getGrado().intValue() <= 0) {
			statusMessages.add(Severity.ERROR, "El valor del grado debe ser mayor a cero. Verifique");
			return false;
		}

		if (personaDiscapacidad.getNroCertificado() != null
			&& personaDiscapacidad.getNroCertificado().intValue() <= 0) {
			statusMessages.add(Severity.ERROR, "El valor del Nro. de Certif debe ser mayor a cero. Verifique");
			return false;
		}
		// Vals. Documentos Adj.
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType, uploadedFile);
			else {
				statusMessages.add(Severity.ERROR, "No se permite la carga de ese tipo de archivos.");
				return false;
			}
		}
		if (personaDiscapacidad.getFechaEmision() != null
			&& personaDiscapacidad.getFechaEmision().after(new Date())) {
			statusMessages.add(Severity.ERROR, "La Fecha de Emisión no puede ser mayor a la fecha actual verifique ");
			return false;
		}
		if (personaDiscapacidad.getFechaEmision() != null
			&& !general.isFechaValida(personaDiscapacidad.getFechaEmision())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha de Emisión inválida");
			return false;
		}
		if (item != null && idTipoDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe Seleccionar El tipo de Documento. Verifique");
			return false;
		}
		if (item == null && idTipoDoc != null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar el archivo. Verifique");
			return false;
		}
		if (item != null) {
			String nomfinal = "";
			nomfinal = item.getFileName();
			String extension = nomfinal.substring(nomfinal.lastIndexOf("."));
			List<TipoArchivo> ta =
				em.createQuery("select t from TipoArchivo t " + " where lower(t.extension) like '"
					+ extension.toLowerCase() + "'").getResultList();
			if (!ta.isEmpty() && ta.get(0).getUnidMedidaByte() != null) {
				if (item.getFileSize() > ta.get(0).getUnidMedidaByte().intValue()) {
					statusMessages.add(Severity.ERROR, "El archivo supera el tamaño máximo permitido. Límite "
						+ ta.get(0).getTamanho() + ta.get(0).getUnidMedida() + ", verifique");
					return false;
				}
			}
		}
		return true;
	}

	public void editar(int index) {
		limpiarEdit();
		esEdit = true;
		personaDiscapacidad = discapacidadsList.get(index);
		idDiscapacidad =
			personaDiscapacidad.getDatosEspecificosByIdDatosEspecificosDiscapac().getIdDatosEspecificos();
		item = null;
		if (personaDiscapacidad.getDatosEspecificosByIdDatosEspecificosGradoAutonom() != null)
			idGradoAutonom =
				personaDiscapacidad.getDatosEspecificosByIdDatosEspecificosGradoAutonom().getIdDatosEspecificos();
		if (personaDiscapacidad.getDocumentos() != null) {
			Documentos doc =
				em.find(Documentos.class, personaDiscapacidad.getDocumentos().getIdDocumento());
			String url = doc.getUbicacionFisica();
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

	public void limpiar() {
		idDiscapacidad = null;
		idGradoAutonom = null;
		personaDiscapacidad = new PersonaDiscapacidad();
		idTipoDoc = null;
		nombreDoc = null;
		item = null;
		inputFile = null;
		nombreDoc = null;
		fileName = null;
		item = null;
		inputFile = null;
	}

	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p =
			em.createQuery(" Select p from Pais p" + " where lower(p.descripcion) like 'paraguay'").getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public PersonaDiscapacidad refreshDoc(PersonaDiscapacidad pd) {
		if (pd.getDocumentos() != null) {
			pd.setDocumentos(em.find(Documentos.class, pd.getDocumentos().getIdDocumento()));
		}
		return pd;
	}

	public void delItems(int index) {
		PersonaDiscapacidad pd =
			em.find(PersonaDiscapacidad.class, discapacidadsList.get(index).getIdPersonaDiscapacidad());
		pd.setActivo(false);
		pd.setUsuMod(usuarioLogueado.getCodigoUsuario());

		pd.setFechaMod(new Date());
		em.merge(pd);
		if (pd.getDocumentos() != null) {
			pd.getDocumentos().setActivo(false);
			em.merge(pd.getDocumentos());
		}
		em.flush();
		discapacidadsList.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getNacionalidadSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));

		List<Object[]> n =
			em.createNativeQuery("Select  de.id_datos_especificos, de.descripcion From seleccion.referencias  r,"
				+ "seleccion.datos_especificos de Where r.dominio like 'NACIONALIDADES' And  r.valor_num = de.id_datos_generales"
				+ " Order by de.descripcion ").getResultList();
		Iterator<Object[]> it = n.iterator();

		while (it.hasNext()) {
			Object[] fila = it.next();
			if (fila[0] != null)
				si.add(new SelectItem(fila[0], fila[1] != null ? fila[1].toString() : ""));
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
					&& (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
			} catch (IOException e1) {
				return;
			}

			if (offset < bytes.length) {
				try {
					throw new IOException("No se pudo leer el archivo por completo "
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

	public void changeNameDoc() {
		nombreDoc = null;
	}

	private void cargarListaDisca() {
		discapacidadsList =
			em.createQuery(" select d from PersonaDiscapacidad d where  d.activo = true and d.persona.idPersona= "
				+ persona.getIdPersona()).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findDiscapacidad() {
		cargarListaDisca();
		// Datos del Representante
		List<ReprPersonaDiscapacidad> rp =
			em.createQuery("select pr from ReprPersonaDiscapacidad pr "
				+ " where pr.persona.idPersona=" + persona.getIdPersona() + " and pr.activo=true").getResultList();
		if (!rp.isEmpty()) {
			reprPersonaDiscapacidad = rp.get(0);
			nomRep = reprPersonaDiscapacidad.getPersonaRep().getNombres();
			apeRep = reprPersonaDiscapacidad.getPersonaRep().getApellidos();
			idPais =
				reprPersonaDiscapacidad.getPersonaRep().getPaisByIdPaisExpedicionDoc().getIdPais();
			mail = reprPersonaDiscapacidad.getPersonaRep().getEMail();
			idNacionalidad =
				reprPersonaDiscapacidad.getPersonaRep().getDatosEspecificos().getIdDatosEspecificos();
			docRep = reprPersonaDiscapacidad.getPersonaRep().getDocumentoIdentidad();
			personaRepre = reprPersonaDiscapacidad.getPersonaRep();
			habPaisExp = false;
			guardarRepre = true;

		} else {
			idPais = idParaguay();
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
		habPaisExp = true;

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

	private boolean verifObligatorios() {
		if (nomRep == null || nomRep.trim().equals("") || apeRep == null
			|| apeRep.trim().equals("") || idNacionalidad == null || docRep == null
			|| docRep.trim().equals("") || idPais == null) {
			return false;
		}
		return true;

	}

	private void crearUploadItem(String fileName, int fileSize, String contentType, byte[] file) {
		item =
			new UploadItem(fileName, fileSize, contentType, AdminReclamoSugPortalFormController.crearFile(fileName, file));
		nombreDoc = item.getFileName();
	}

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

	public void setReprPersonaDiscapacidad(ReprPersonaDiscapacidad reprPersonaDiscapacidad) {
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

	public Boolean getMsjOk() {
		return msjOk;
	}

	public void setMsjOk(Boolean msjOk) {
		this.msjOk = msjOk;
	}

	public Long getPersonaIdPersona() {
		return personaIdPersona;
	}

	public void setPersonaIdPersona(Long personaIdPersona) {
		this.personaIdPersona = personaIdPersona;
	}

	public Long getConcursoPuestoAgrIdConcursoPuestoAgr() {
		return concursoPuestoAgrIdConcursoPuestoAgr;
	}

	public void setConcursoPuestoAgrIdConcursoPuestoAgr(Long concursoPuestoAgrIdConcursoPuestoAgr) {
		this.concursoPuestoAgrIdConcursoPuestoAgr = concursoPuestoAgrIdConcursoPuestoAgr;
	}

	public Boolean getPrimeraVez() {
		return primeraVez;
	}

	public void setPrimeraVez(Boolean primeraVez) {
		this.primeraVez = primeraVez;
	}

	public String getMsjCopiaRealizada() {
		return msjCopiaRealizada;
	}

	public void setMsjCopiaRealizada(String msjCopiaRealizada) {
		this.msjCopiaRealizada = msjCopiaRealizada;
	}

}
