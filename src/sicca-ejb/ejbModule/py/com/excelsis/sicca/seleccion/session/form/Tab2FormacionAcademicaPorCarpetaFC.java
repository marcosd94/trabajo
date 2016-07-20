package py.com.excelsis.sicca.seleccion.session.form;

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
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.print.Doc;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.transaction.SystemException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.ManagedPersistenceContext;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import enums.HorasAnios;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Especialidad;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.EstudiosRealizados;
import py.com.excelsis.sicca.entity.IdiomasPersona;
import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.NivelEstudio;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.entity.TituloAcademico;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EstudiosRealizadosHome;
import py.com.excelsis.sicca.session.PersonaDiscapacidadHome;
import py.com.excelsis.sicca.session.PersonaList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("tab2FormacionAcademicaPorCarpetaFC")
public class Tab2FormacionAcademicaPorCarpetaFC implements Serializable {

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
	EstudiosRealizadosHome estudiosRealizadosHome;

	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	@RequestParameter
	Long concursoPuestoAgrIdConcursoPuestoAgr;

	@RequestParameter
	Long personaIdPersona;

	General general;

	private EstudiosRealizados estudiosRealizados = new EstudiosRealizados();
	private List<EstudiosRealizados> estudiosRealizadosList = new ArrayList<EstudiosRealizados>();
	private List<EstudiosRealizados> estudiosRealizadosCopiaList = new ArrayList<EstudiosRealizados>();
	private Persona persona = new Persona();
	private Long idPais;
	private Long idNivelEstudio;
	private Long idProfesion;
	private Long idIstUniversidad;
	private Long idIdioma;
	private Long idHabla;
	private Long idEscribe;
	private Long idLee;
	private Long idTipoDoc;
	private boolean esEdit = false;
	private Long idTituloAcademico;
	private int indexUp;
	private List<IdiomasPersona> idiomasPersonasList = new ArrayList<IdiomasPersona>();
	private List<IdiomasPersona> idiomasPersonasCopiaList = new ArrayList<IdiomasPersona>();
	private List<SelectItem> tituloSelecItem;
	private List<SelectItem> profesionSelecItem;
	private List<SelectItem> paisSelecItem;
	private boolean esOtro;// para el titulo
	private boolean esOtraInst;
	private String nombrePantalla = "tab2FormacionAcademicaCarpeta_edit";
	private Long idDoc;
	private HorasAnios horasAnios;
	private boolean habPais;
	private boolean esNingunaInsNingunTit = false;// en le caso que se
													// seleccione ningun titulo
													// y ninguna institucion
	private boolean tieneTitulo = false;
	private boolean esOtroNivel;// para el Nivel de Estudios
	private Long idPersona;
	private Long idConcursoPuestoAgr;
	private Long idPersonaPostulante;
	private Long idPostulacion;
	/**
	 * SE CONSIDETA LA PRIMERA VES AL INGRESAR EN LA PANTALLA Y NO TENGA NADA
	 * CARGADO PARA LOS 2 CASOS POR SEPARADO ESTO SE VERIFICA EN EL
	 * initPrincipal
	 * 
	 * */
	private Boolean primeraVezEstudio = false;
	private Boolean primeraVezIdioma = false;
	private Boolean copiaInicialEstudios = false;
	private Boolean copiaInicialIdioma = false;
	private boolean bloquearTab;

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	private UploadItem item;
	private String nombreDoc;
	private File inputFile;
	private byte[] uploadedFile;
	private String contentType;
	private String fileName;

	public void initTab() {

		try {
			esOtroNivel = false;
			setear();
			persona = em.find(Persona.class, idPersona);
			findEstudioPersona();
			paisSelecItem = new ArrayList<SelectItem>();
			idiomasPersonasList = new ArrayList<IdiomasPersona>();
			if (persona != null) {
				findEstudiosRealizados();
				findIdiomaPersona();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void init() throws Exception {
		/**
		 * validacion
		 * */
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (personaIdPersona != null) {
			if (!sufc.validarInput(personaIdPersona.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			idPersona = personaIdPersona;
		}
		if (concursoPuestoAgrIdConcursoPuestoAgr != null) {
			if (!sufc.validarInput(
					concursoPuestoAgrIdConcursoPuestoAgr.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			idConcursoPuestoAgr = concursoPuestoAgrIdConcursoPuestoAgr;
		}

		/**
		 * verificar si ya se hizo la COPIA para esta Postulación:
		 * */
		List<Postulacion> postulacionEstudio = em
				.createQuery(
						"Select p from Postulacion p"
								+ " where p.persona.idPersona=:idPersona and p.concursoPuestoAgr.idConcursoPuestoAgr=:idGrupo  "
								+ " and p.usuCancelacion is null	and p.fechaCancelacion is null")
				.setParameter("idPersona", idPersona)
				.setParameter("idGrupo", idConcursoPuestoAgr).getResultList();
		if (postulacionEstudio.isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"La persona no cuenta con postulaciones");
			bloquearTab = true;
			return;

		}
		if (!seleccionUtilFormController.existePersonaPostulante(
				idConcursoPuestoAgr, idPersona))
			bloquearTab = true;

		initTab();

		idPostulacion = postulacionEstudio.get(0).getIdPostulacion();
		if (!postulacionEstudio.isEmpty()
				&& postulacionEstudio.get(0).getPersonaPostulante() != null) {
			idPersonaPostulante = postulacionEstudio.get(0)
					.getPersonaPostulante().getIdPersonaPostulante();
			List<EstudiosRealizados> realizados = em
					.createQuery(
							"Select e from EstudiosRealizados e"
									+ " where e.personaPostulante.idPersonaPostulante=:idPersonaPostulante ")
					.setParameter("idPersonaPostulante", idPersonaPostulante)
					.getResultList();
			if (realizados.isEmpty()) {
				setPrimeraVezEstudio(true);
				setCopiaInicialEstudios(true);
			}

			List<IdiomasPersona> idiomPersonas = em
					.createQuery(
							"Select i from IdiomasPersona i"
									+ " where i.personaPostulante.idPersonaPostulante=:idPersonaPostulante")
					.setParameter("idPersonaPostulante", idPersonaPostulante)
					.getResultList();
			if (idiomPersonas.isEmpty()) {
				setPrimeraVezIdioma(true);
				setCopiaInicialIdioma(true);
			}

		} else {
			primeraVezEstudio = true;
			primeraVezIdioma = true;

		}

	}

	public String save() {
		try {
			for (int i = 0; i < estudiosRealizadosList.size(); i++) {
				if (estudiosRealizadosList.get(i).getIdEstudiosRealizados() != null) {
					em.merge(estudiosRealizadosList.get(i));
					em.flush();
					if (estudiosRealizados.getDocumentos() != null
							&& idDoc != null) {
						Documentos doc = em.find(Documentos.class, idDoc);
						doc.setIdTabla(estudiosRealizados
								.getIdEstudiosRealizados());
						doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
						doc.setFechaMod(new Date());
						doc.setPersona(persona);
						em.merge(doc);
						em.flush();
					}

				} else {
					em.persist(estudiosRealizadosList.get(i));
					em.flush();
				}

			}

			return "persisted";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String update() {
		try {

			if (!checkDato("update"))
				return null;

			/**
			 * Para el Caso de documento adjuntos
			 * */
			if (item != null) {
				Long idDoc = null;
				if (estudiosRealizados.getDocumentos() != null
						&& estudiosRealizados.getDocumentos().getIdDocumento() != null)
					idDoc = estudiosRealizados.getDocumentos().getIdDocumento();

				Long idDocEdit = guardarAdjuntos(item.getFileName(),
						item.getFileSize(), item.getContentType(),
						uploadedFile, idTipoDoc, idDoc);
				if (idDocEdit == null)
					return null;
				estudiosRealizados.setDocumentos(em.find(Documentos.class,
						idDocEdit));
			}
			if (!tieneTitulo) {
				estudiosRealizados.setFechaFin(null);
				estudiosRealizados.setDuracionHoras(null);
				horasAnios = HorasAnios.Anios;
			}

			estudiosRealizados.setActivo(true);
			if (idProfesion != null)
				estudiosRealizados.setEspecialidades((em.find(
						Especialidades.class, idProfesion)));
			else
				estudiosRealizados.setEspecialidades(null);
			TituloAcademico ta = em.find(TituloAcademico.class,
					idTituloAcademico);

			if (ta.getDescripcion().toLowerCase().equals("ninguno")
					|| ta.getDescripcion().toLowerCase().equals("ningunos"))
				estudiosRealizados.setFinalizo(false);
			else
				estudiosRealizados.setFinalizo(true);
			if (horasAnios != null)
				estudiosRealizados.setTipoDuracion(horasAnios.getValor());
			estudiosRealizados.setFechaMod(new Date());
			estudiosRealizados.setUsuMod(seleccionUtilFormController
					.getUsuarioPostulante(idPersona));
			estudiosRealizados.setUsuModOee(usuarioLogueado.getCodigoUsuario());
			estudiosRealizados.setFechaModOee(new Date());
			estudiosRealizados.setTipo("CARPETA");
			estudiosRealizados.setInstitucionEducativa(em.find(
					InstitucionEducativa.class, idIstUniversidad));
			estudiosRealizados.setTituloAcademico(em.find(
					TituloAcademico.class, idTituloAcademico));

			// SI NO TIENE TITULO NI iNSTI. INACTIVA EL DOC.
			if (esNingunaInsNingunTit) {
				if (estudiosRealizados.getDocumentos() != null) {
					Documentos docIna = em
							.find(Documentos.class, estudiosRealizados
									.getDocumentos().getIdDocumento());
					docIna.setActivo(false);
					docIna.setFechaMod(new Date());
					docIna.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(docIna);

				}
				estudiosRealizados.setDocumentos(null);

			}

			em.merge(estudiosRealizados);
			em.flush();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			limpiar();

			return "updated";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages
					.add(Severity.ERROR, "Error al actualizar. Verifique");
		}
		return null;
	}

	public void cancelar() {
		estudiosRealizados = new EstudiosRealizados();
		idNivelEstudio = null;
		idPais = null;
		idProfesion = null;
		idIstUniversidad = null;
		idTituloAcademico = null;
		idTipoDoc = null;
		nombreDoc = null;
		horasAnios = HorasAnios.Anios;
		habPais = false;
		uploadedFile = null;
		item = null;
		esNingunaInsNingunTit = false;
		esOtraInst = false;
		esOtro = false;
		tieneTitulo = true;
		fileName = null;
		contentType = null;
		esEdit = false;
		estudiosRealizadosList = new ArrayList<EstudiosRealizados>();
		em.clear();
		findEstudiosRealizados();

	}

	public void editar(int index) {
		esEdit = true;

		estudiosRealizados = em.find(EstudiosRealizados.class,
				estudiosRealizadosList.get(index).getIdEstudiosRealizados());
		if (estudiosRealizados.getTituloAcademico().getNivelEstudio()
				.getDescripcion().trim().toLowerCase()
				.equalsIgnoreCase("otros"))
			esOtroNivel = true;
		else
			esOtroNivel = false;
		idNivelEstudio = estudiosRealizados.getTituloAcademico()
				.getNivelEstudio().getIdNivelEstudio();
		idTituloAcademico = estudiosRealizados.getTituloAcademico()
				.getIdTituloAcademico();
		idIstUniversidad = estudiosRealizados.getInstitucionEducativa()
				.getIdInstEducativa();
		getTituloAcademicos();

		if (estudiosRealizados.getPais() != null) {
			idPais = estudiosRealizados.getPais().getIdPais();
		}
		TituloAcademico ta = em.find(TituloAcademico.class, idTituloAcademico);
		if (idTituloAcademico != null && idIstUniversidad != null) {
			if (estudiosRealizados.getInstitucionEducativa().getDescripcion()
					.toLowerCase().equals("ninguna")) {
				habPais = false;
				if (ta.getDescripcion().toLowerCase().equals("ninguno")) {
					esNingunInstitucionTituto();
				} else
					esNingunaInsNingunTit = false;
			} else
				habPais = true;
		}
		if (ta.getDescripcion().toLowerCase().equals("ninguno")) {
			noTienTitulo();
		} else
			tieneTitulo = true;
		if (estudiosRealizados.getTituloAcademico().getDescripcion()
				.toLowerCase().equals("otros"))
			esOtro = true;
		else
			esOtro = false;
		if (estudiosRealizados.getInstitucionEducativa().getDescripcion()
				.toLowerCase().equals("otras")
				|| estudiosRealizados.getInstitucionEducativa()
						.getDescripcion().toLowerCase().equals("otra"))
			esOtraInst = true;
		else
			esOtraInst = false;
		if (estudiosRealizados.getEspecialidades() != null)
			idProfesion = estudiosRealizados.getEspecialidades()
					.getIdEspecialidades();
		else
			idProfesion = null;
		if (estudiosRealizados.getTipoDuracion() != null)
			horasAnios = HorasAnios.getHorasAniosPorId(estudiosRealizados
					.getTipoDuracion());
		if (idPais != null) {
			estudiosRealizados.setPais(em.find(Pais.class, idPais));
		}

		idTituloAcademico = estudiosRealizados.getTituloAcademico()
				.getIdTituloAcademico();
		if (idProfesion != null)
			upEspecialidad();
		if (estudiosRealizados.getDocumentos() != null) {
			Documentos doc = em.find(Documentos.class, estudiosRealizados
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

		} else
			inputFile = null;
		if (estudiosRealizados.getPais() != null) {
			idPais = estudiosRealizados.getPais().getIdPais();
			upInstitucionOtro();
		}
		indexUp = index;

	}

	public void addEstudios() {
		try {
			if (!checkDato("save"))
				return;

			Long idDoc = null;

			/**
			 * Para el Caso de documento adjuntos
			 * */
			if (item != null) {

				idDoc = guardarAdjuntos(item.getFileName(), item.getFileSize(),
						item.getContentType(), uploadedFile, idTipoDoc, null);
				if (idDoc == null)
					return;
				estudiosRealizados.setDocumentos(em.find(Documentos.class,
						idDoc));

			}
			TituloAcademico ta = em.find(TituloAcademico.class,
					idTituloAcademico);
			if (ta.getDescripcion().toLowerCase().equals("ninguno")
					|| ta.getDescripcion().toLowerCase().equals("ningunos"))
				estudiosRealizados.setFinalizo(false);
			else
				estudiosRealizados.setFinalizo(true);
			if (!tieneTitulo) {
				estudiosRealizados.setFechaFin(null);
				estudiosRealizados.setDuracionHoras(null);
				horasAnios = HorasAnios.Anios;
			}
			if (horasAnios != null)
				estudiosRealizados.setTipoDuracion(horasAnios.getValor());
			estudiosRealizados.setActivo(true);
			if (idProfesion != null)
				estudiosRealizados.setEspecialidades((em.find(
						Especialidades.class, idProfesion)));

			estudiosRealizados.setPersona(persona);
			estudiosRealizados.setInstitucionEducativa(em.find(
					InstitucionEducativa.class, idIstUniversidad));
			estudiosRealizados.setTituloAcademico(em.find(
					TituloAcademico.class, idTituloAcademico));
			if (habPais && idPais != null) {
				estudiosRealizados.setPais(em.find(Pais.class, idPais));
			}
			estudiosRealizados.setTipo("CARPETA");
			estudiosRealizados.setFechaAltaOee(new Date());
			estudiosRealizados
					.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
			estudiosRealizados.setFechaAlta(new Date());
			estudiosRealizados.setUsuAlta(seleccionUtilFormController
					.getUsuarioPostulante(idPersona));
			em.persist(estudiosRealizados);

			if (idDoc != null) {
				Documentos doc = em.find(Documentos.class, idDoc);
				doc.setIdTabla(estudiosRealizados.getIdEstudiosRealizados());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				doc.setPersona(persona);
				em.merge(doc);

			}

			if (primeraVezEstudio) {
				if (!copiaEstudio(estudiosRealizados, false)) {
					statusMessages.add(Severity.ERROR,
							"No se ha podido realizar la copia. Verifique");
					return;
				}
				if (copiaInicialEstudios) {
					if (!copiaRegistroAnterior()) {
						statusMessages.add(Severity.ERROR,
								"No se ha podido realizar la copia. Verifique");
						return;
					}
					setCopiaInicialEstudios(false);
				}
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("CU516_sav_first"));

			}
			em.flush();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR,
					"Error no se puedo agregar el registro, verifique");
		}

		limpiar();
	}

	private boolean checkDato(String op) {
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType))
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
			else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
			}

		}

		if (idNivelEstudio == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar un Nivel de Estudio. Verifique");
			return false;
		}
		if (idTituloAcademico == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleciconar el Titulo Obtenido");
			return false;
		}
		if (idIstUniversidad == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar una Institución/Univ. Verifique");
			return false;
		}
		TituloAcademico ta = em.find(TituloAcademico.class, idTituloAcademico);

		if (habPais) {
			if (idPais == null) {
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar un Pais. Verifique");
				return false;
			}
			/*if (estudiosRealizados.getFechaInicio() == null) {
				statusMessages.add(Severity.ERROR,
						"Debe ingresar la fecha de inicio. Verifique");
				return false;
			}*/
		}
		InstitucionEducativa ie = em.find(InstitucionEducativa.class,
				idIstUniversidad);
		if (!ta.getDescripcion().toLowerCase().equals("ninguno")
				&& !ta.getDescripcion().toLowerCase().equals("ningunos")) {// si
																			// tiene
																			// titulo

			if (ie.getDescripcion().toLowerCase().equals("ninguna")) {
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar la institución. Verifique");
				return false;
			}

			if (idPais == null) {
				statusMessages.add(Severity.ERROR,
						"Seleccione el País. Verifique");
				return false;
			}
			/*if (estudiosRealizados.getFechaInicio() == null) {
				statusMessages.add(Severity.ERROR,
						"Ingrese la Fecha de Inicio. Verifique");
				return false;
			}*/
			if (estudiosRealizados.getFechaFin() == null) {
				statusMessages.add(Severity.ERROR,
						"Ingrese la Fecha Fin. Verifique");
				return false;
			}
			if (estudiosRealizados.getFechaFin() != null
					&& estudiosRealizados.getFechaInicio().after(
							estudiosRealizados.getFechaFin())) {
				statusMessages
						.add(Severity.ERROR,
								"La fecha de Inicio no puede ser mayor a la Fecha Fin. Verifique");
				return false;
			}

			if (estudiosRealizados.getDuracionHoras() == null) {
				statusMessages.add(Severity.ERROR,
						"Ingrese la Duración. Verifique");
				return false;
			}
			if (estudiosRealizados.getDuracionHoras() != null
					&& estudiosRealizados.getDuracionHoras().intValue() <= 0) {
				statusMessages
						.add(Severity.ERROR,
								"La Duración ingresada debe ser mayor a cero. Verifique");
				return false;
			}
			if (idTipoDoc == null) {
				statusMessages
						.add(Severity.ERROR,
								"Debe Seleccionar el Tipo de Documento y el Documento. Verifique");
				return false;
			}

		}

		if (!esNingunaInsNingunTit) {
			if (existeEstudio(op)) {
				statusMessages.add(Severity.ERROR,
						"El registro  ingresado ya existe, favor verificar");
				return false;
			}
		}

		if (esOtraInst
				&& (estudiosRealizados.getOtraInstit() == null || estudiosRealizados
						.getOtraInstit().trim().equals(""))) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar en el campo Otra de Institución  antes de agregar . Verifique");
			return false;
		}
		if (esOtro
				&& (estudiosRealizados.getOtroTituloObt() == null || estudiosRealizados
						.getOtroTituloObt().trim().equals(""))) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar en el campo Otros de Titulo antes de agregar. Verifique");
			return false;
		}
		if (uploadedFile != null && idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar el Tipo de documento. Verifique");
			return false;
		}
		if (op.equals("save")) {
			if (uploadedFile == null && idTipoDoc != null) {
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar el archivo. Verifique");
				return false;
			}
		} else {
			if ((uploadedFile == null && estudiosRealizados.getDocumentos() == null)
					&& idTipoDoc != null) {
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar el archivo. Verifique");
				return false;
			}
		}

		if (estudiosRealizados.getFechaInicio() != null) {
			if (!general.isFechaValida(estudiosRealizados.getFechaInicio())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Fecha de Inicio inválida. Verifique");
				return false;
			}
			if (estudiosRealizados.getFechaInicio().after(new Date())) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"La Fecha de Inicio no puede ser mayor a la fecha actual.  Verifique ");
				return false;
			}
		}
		if (estudiosRealizados.getFechaInicio() != null
				&& estudiosRealizados.getFechaFin() != null) {
			if (estudiosRealizados.getFechaInicio().after(
					estudiosRealizados.getFechaFin())) {
				statusMessages
						.add(Severity.ERROR,
								"La Fecha de Inicio no puede ser mayor a la fecha Fin. Verifique");
				return false;
			}
		}

		if (estudiosRealizados.getFechaFin() != null) {
			if (!general.isFechaValida(estudiosRealizados.getFechaFin())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Fecha Fin inválida");
				return false;
			}
			if (estudiosRealizados.getFechaFin().after(new Date())) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"La Fecha Fin no puede ser mayor a la fecha actual.  Verifique ");
				return false;
			}
		}

		if (estudiosRealizados.getDuracionHoras() != null
				&& estudiosRealizados.getDuracionHoras().intValue() <= 0) {
			statusMessages.add(Severity.ERROR,
					"La Duración ingresada debe ser mayor a cero. Verifique");
			return false;
		}

		return true;
	}

	/**
	 * LA COPIA SE HACE UNA SOLA VEZ POR GRUPO
	 * */
	private boolean copiaEstudio(EstudiosRealizados esRealizados, boolean existe) {
		Long idDocCopia = null;
		try {

			EstudiosRealizados estudiosRealizadosCopia = new EstudiosRealizados();
			estudiosRealizadosCopia.setActivo(true);
			/**
			 * SE CARGA EL ID DEL DOCUMENTO COPIA REALIZADO, solo si existia
			 * documento
			 * */

			if (esRealizados.getDocumentos() != null
					&& esRealizados.getDocumentos().getIdDocumento() != null) {
				idDocCopia = copiaDocumento(esRealizados.getDocumentos(),
						existe);
				// Si viene null hubo error en la copia
				if (idDocCopia == null)
					return false;
				estudiosRealizadosCopia.setDocumentos(new Documentos());
				estudiosRealizadosCopia.getDocumentos().setIdDocumento(
						idDocCopia);
			}

			estudiosRealizadosCopia.setDuracionHoras(esRealizados
					.getDuracionHoras());
			estudiosRealizadosCopia.setEspecialidades(esRealizados
					.getEspecialidades());
			estudiosRealizadosCopia.setFechaAlta(new Date());
			estudiosRealizadosCopia.setFechaAltaOee(new Date());
			estudiosRealizadosCopia.setFechaFin(esRealizados.getFechaFin());
			estudiosRealizadosCopia.setFechaInicio(esRealizados
					.getFechaInicio());
			estudiosRealizadosCopia.setFechaMod(esRealizados.getFechaMod());
			estudiosRealizadosCopia.setFechaModOee(esRealizados
					.getFechaModOee());
			estudiosRealizadosCopia.setFinalizo(esRealizados.getFinalizo());
			estudiosRealizadosCopia.setInstitucionEducativa(esRealizados
					.getInstitucionEducativa());
			estudiosRealizadosCopia.setOtraInstit(esRealizados.getOtraInstit());
			estudiosRealizadosCopia.setOtroTituloObt(esRealizados
					.getOtroTituloObt());
			estudiosRealizadosCopia.setPais(esRealizados.getPais());
			estudiosRealizadosCopia.setPersona(null);
			estudiosRealizadosCopia.setPersonaPostulante(em.find(
					PersonaPostulante.class, idPersonaPostulante));
			estudiosRealizadosCopia.setTipo("CARPETA");
			estudiosRealizadosCopia.setTipoDuracion(esRealizados
					.getTipoDuracion());
			estudiosRealizadosCopia.setTituloAcademico(esRealizados
					.getTituloAcademico());
			estudiosRealizadosCopia.setUsuAlta(esRealizados.getUsuAlta());
			estudiosRealizadosCopia.setUsuAltaOee(esRealizados.getUsuAltaOee());
			estudiosRealizadosCopia.setUsuMod(esRealizados.getUsuMod());
			estudiosRealizadosCopia.setUsuModOee(estudiosRealizadosCopia
					.getUsuModOee());
			em.persist(estudiosRealizadosCopia);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * SE HACE LA COPA PARA LOS REGISTROS QUE YA EXISTIAN Y QUE NO SE REALIZARON
	 * TODAVIA LA COPIA
	 * */
	private boolean copiaRegistroAnterior() {
		boolean copio;
		try {
			for (EstudiosRealizados e : estudiosRealizadosCopiaList) {
				copio = copiaEstudio(e, true);
				if (!copio)
					return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @return ID_DOCUMENTO COPIA
	 * @param DOCUMENTO
	 *            ANTERIOR PARA REALIZAR LA COPIA
	 * @throws IOException
	 * */
	private Long copiaDocumento(Documentos documentos, boolean exite)
			throws IOException {
		Long idDocCopia;

		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
				idConcursoPuestoAgr);
		Long idOee = agr.getConcurso().getConfiguracionUoCab()
				.getIdConfiguracionUo();
		Long idTipoConcurso = agr.getConcurso().getDatosEspecificosTipoConc()
				.getIdDatosEspecificos();
		String nro_doc_ID = persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();

		String direccionFisica = File.separator + "SICCA" + File.separator
				+ General.anhoActual() + File.separator + "OEE"
				+ File.separator + idOee + File.separator + idTipoConcurso
				+ File.separator + agr.getConcurso().getIdConcurso()
				+ File.separator + idConcursoPuestoAgr + File.separator
				+ "POSTULANTES" + File.separator + nro_doc_ID;
		byte[] archivoCopia;
		if (exite) {
			File fileFromDoc = null;
			fileFromDoc = new File(documentos.getUbicacionFisica()
					+ File.separator + documentos.getNombreFisicoDoc());
			archivoCopia = JasperReportUtils.getBytesFromFile(fileFromDoc);
		} else
			archivoCopia = uploadedFile;

		idDocCopia = admDocAdjuntoFormController.hacerCopiaDocPostulantes(
				idPostulacion, direccionFisica, archivoCopia, nombrePantalla
						+ "_copia", documentos);
		return idDocCopia;
	}

	private boolean copiaIdiomas(IdiomasPersona idiomaPersona) {
		try {
			IdiomasPersona idiomasPersonaCopia = new IdiomasPersona();
			idiomasPersonaCopia.setActivo(true);
			idiomasPersonaCopia.setDatosEspecificos(idiomaPersona
					.getDatosEspecificos());
			idiomasPersonaCopia.setEscribe(idiomaPersona.getEscribe());
			idiomasPersonaCopia.setFechaAlta(new Date());
			idiomasPersonaCopia.setFechaAltaOee(new Date());
			idiomasPersonaCopia.setUsuAlta(seleccionUtilFormController
					.getUsuarioPostulante(idPersona));
			idiomasPersonaCopia.setUsuAltaOee(usuarioLogueado
					.getCodigoUsuario());
			idiomasPersonaCopia.setTipo("CARPETA");
			idiomasPersonaCopia.setHabla(idiomaPersona.getHabla());
			idiomasPersonaCopia.setLee(idiomaPersona.getLee());
			idiomasPersonaCopia.setPersona(null);
			idiomasPersonaCopia.setPersonaPostulante(em.find(
					PersonaPostulante.class, idPersonaPostulante));
			em.persist(idiomasPersonaCopia);

			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * SE HACE LA COPA PARA LOS REGISTROS QUE YA EXISTIAN Y QUE NO SE REALIZARON
	 * TODAVIA LA COPIA
	 * */
	private boolean copiaIdiomasAnteriores() {
		for (IdiomasPersona ip : idiomasPersonasCopiaList) {
			if (!copiaIdiomas(ip))
				return false;
		}
		return true;
	}

	public void limpiar() {
		estudiosRealizados = new EstudiosRealizados();
		idIstUniversidad = null;
		idNivelEstudio = null;
		idPais = null;
		idTituloAcademico = null;
		idProfesion = null;
		esOtroNivel = false;
		idTipoDoc = null;
		horasAnios = HorasAnios.Anios;
		habPais = false;
		uploadedFile = null;
		nombreDoc = null;
		item = null;
		esNingunaInsNingunTit = false;
		esOtraInst = false;
		esOtro = false;
		tieneTitulo = true;
		fileName = null;
		contentType = null;
		esEdit = false;
		findEstudiosRealizados();
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, byte[] file, Long idTipoDoc, Long idDocumento) {
		try {
			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
					fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "EstudiosRealizados";
			String nro_doc_ID = persona.getDocumentoIdentidad() + "_"
					+ persona.getIdPersona();
			String direccionFisica = File.separator + "SICCA" + File.separator
					+ "USUARIO_PORTAL" + File.separator + nro_doc_ID;
			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(fileItem,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void abrirDoc(int index) {

		EstudiosRealizados e = estudiosRealizadosList.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}

	public void delEstudio(int index) {
		EstudiosRealizados e = estudiosRealizadosList.get(index);
		e.setActivo(false);
		e.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
		e.setFechaAltaOee(new Date());
		e.setUsuAlta(seleccionUtilFormController
				.getUsuarioPostulante(idPersona));
		e.setFechaAlta(new Date());
		if (e.getDocumentos() != null) {
			Documentos aux = e.getDocumentos();
			aux.setActivo(false);
			aux.setFechaMod(new Date());
			aux.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(aux);
			em.flush();
		}

		em.merge(e);
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		estudiosRealizadosList.remove(index);

	}

	public void addIdiomas() {
		try {
			if (!checkDatosIdiomas())
				return;
			Referencias escribe = em.find(Referencias.class, idEscribe);
			Referencias habla = em.find(Referencias.class, idHabla);
			Referencias lee = em.find(Referencias.class, idLee);
			/**
			 * 
			 * */
			IdiomasPersona idiomasPersona = new IdiomasPersona();
			idiomasPersona.setDatosEspecificos(em.find(DatosEspecificos.class,
					idIdioma));
			idiomasPersona.setEscribe(escribe.getDescAbrev());
			idiomasPersona.setHabla(habla.getDescAbrev());
			idiomasPersona.setLee(lee.getDescAbrev());
			idiomasPersona.setPersona(persona);
			idiomasPersona.setUsuAlta(seleccionUtilFormController
					.getUsuarioPostulante(idPersona));
			idiomasPersona.setFechaAlta(new Date());
			idiomasPersona.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
			idiomasPersona.setFechaAltaOee(new Date());
			idiomasPersona.setTipo("CARPETA");
			idiomasPersona.setActivo(true);
			em.persist(idiomasPersona);
			if (primeraVezIdioma) {

				if (!copiaIdiomas(idiomasPersona)
						|| (copiaInicialIdioma && !copiaIdiomasAnteriores())) {
					statusMessages.add(Severity.ERROR,
							"Error al realizar la copia. Verifique");
					return;
				}
				setCopiaInicialIdioma(false);
			}

			em.flush();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			idiomasPersonasList.add(idiomasPersona);
			idEscribe = null;
			idHabla = null;
			idIdioma = null;
			idLee = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean checkDatosIdiomas() {
		if (idIdioma == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Item Idioma");
			return false;
		}
		if (idHabla == null) {
			statusMessages
					.add(Severity.ERROR, "Debe Seleccionar el Item Habla");
			return false;
		}
		if (idEscribe == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Item Escribe");
			return false;
		}

		if (idLee == null) {
			statusMessages.add(Severity.ERROR, "Debe Seleccionar el Item Lee");
			return false;
		}
		if (existeIdioma()) {
			statusMessages.add(Severity.ERROR,
					"El registro  ingresado ya existe, favor verificar");
			return false;
		}
		return true;
	}

	public void nombre() {
		nombreDoc = null;
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

	public void delIdioma(int index) {
		IdiomasPersona i = idiomasPersonasList.get(index);
		i.setActivo(false);
		i.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
		i.setFechaAltaOee(new Date());
		i.setUsuAlta(seleccionUtilFormController
				.getUsuarioPostulante(idPersona));
		em.merge(i);
		em.flush();
		idiomasPersonasList.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

	}

	// METODOS PRIVADO
	private void esNingunInstitucionTituto() {
		idPais = null;
		habPais = false;
		idTipoDoc = null;
		estudiosRealizados.setFechaInicio(null);
		estudiosRealizados.setFechaFin(null);
		estudiosRealizados.setDuracionHoras(null);
		esNingunaInsNingunTit = true;
		nombreDoc = null;
		horasAnios = HorasAnios.Anios;
	}

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));

	}

	@SuppressWarnings("unchecked")
	private void findEstudiosRealizados() {
		estudiosRealizadosList = em.createQuery(
				" select d from EstudiosRealizados d where d.persona.idPersona= "
						+ persona.getIdPersona() + ""
						+ " and  d.activo= true order by d.fechaAlta desc")
				.getResultList();
		estudiosRealizadosCopiaList = estudiosRealizadosList;
	}

	@SuppressWarnings("unchecked")
	private boolean existeEstudio(String op) {
		try {
			SimpleDateFormat formato = new SimpleDateFormat("yyyy-dd-MM");
			String sql = " select e from EstudiosRealizados e "
					+ " where e.tituloAcademico.nivelEstudio.idNivelEstudio="
					+ idNivelEstudio + ""
					+ "  and  e.tituloAcademico.idTituloAcademico="
					+ idTituloAcademico + "" + "  and  e.pais.idPais=" + idPais
					+ "  and e.activo=true"
					+ " and  date(e.fechaInicio) = to_date('"
					+ formato.format(estudiosRealizados.getFechaInicio())
					+ "','YYYY-DD-MM') " + " and e.persona.idPersona= "
					+ idPersona.longValue();
			if (!esOtraInst)
				sql += "  and e.institucionEducativa.idInstEducativa="
						+ idIstUniversidad;
			else if (estudiosRealizados.getOtraInstit() != null)
				sql += "  and lower(and e.otraInstit) like '"
						+ estudiosRealizados.getOtraInstit().toLowerCase()
						+ "'";
			if (idProfesion != null)
				sql += "  and e.especialidades.idEspecialidades ="
						+ idProfesion + "";

			if (op.equals("update"))
				sql += " and e.idEstudiosRealizados!="
						+ estudiosRealizados.getIdEstudiosRealizados();
			List<EstudiosRealizados> e = em.createQuery(sql).getResultList();
			return !e.isEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	private boolean existeIdioma() {
		String consulta = "select i from IdiomasPersona i "
				+ " where i.datosEspecificos.idDatosEspecificos=" + idIdioma
				+ " and i.activo = true " + " and i.persona.idPersona = "
				+ idPersona;
		List<IdiomasPersona> ip = em.createQuery(consulta).getResultList();
		return !ip.isEmpty();
	}

	private void setear() {
		estudiosRealizadosList = new ArrayList<EstudiosRealizados>();
		estudiosRealizados = new EstudiosRealizados();
		estudiosRealizados.setFinalizo(false);
		idEscribe = null;
		idHabla = null;
		idIdioma = null;
		idIstUniversidad = null;
		idLee = null;
		idNivelEstudio = null;
		idPais = null;
		idProfesion = null;
		idTituloAcademico = null;
		esOtro = false;
		esEdit = false;
		getTituloAcademicos();
		upEspecialidad();

	}

	@SuppressWarnings("unchecked")
	private void findEstudioPersona() {
		estudiosRealizadosList = em.createQuery(
				"select e from EstudiosRealizados e where "
						+ " e.persona.idPersona =" + persona.getIdPersona()
						+ " and e.activo=true order by e.fechaAlta desc")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private void findIdiomaPersona() {
		idiomasPersonasList = em.createQuery(
				"select e from IdiomasPersona e where "
						+ " e.persona.idPersona =" + persona.getIdPersona()
						+ " AND e.activo = true").getResultList();
		idiomasPersonasCopiaList = idiomasPersonasList;
	}

	public List<TituloAcademico> upTituloAcademico() {
		idTituloAcademico = null;
		if (idNivelEstudio != null) {
			NivelEstudio ta = em.find(NivelEstudio.class, idNivelEstudio);
			if (ta.getDescripcion().toLowerCase().equals("otros"))
				esOtroNivel = true;
			else
				esOtroNivel = false;
		}
		return getTituloAcademicos();
	}

	@SuppressWarnings("unchecked")
	public List<TituloAcademico> getTituloAcademicos() {
		List<TituloAcademico> ti = new Vector<TituloAcademico>();
		try {
			if (idNivelEstudio != null) {
				setIdNivelEstudio(idNivelEstudio);
				ti = em.createQuery(
						" select o from " + TituloAcademico.class.getName()
								+ " o where o.activo = true "
								+ " and o.nivelEstudio.idNivelEstudio="
								+ idNivelEstudio + " order by o.descripcion")
						.getResultList();
			}
			getTituloAcademicoSelectItems(ti);
			habFechaFin();
			return ti;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ti;
		}
	}

	public List<SelectItem> getTituloAcademicoSelectItems(
			List<TituloAcademico> ti) {
		List<SelectItem> si = new Vector<SelectItem>();
		tituloSelecItem = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (TituloAcademico o : ti)
			si.add(new SelectItem(o.getIdTituloAcademico(), o.getDescripcion()));

		tituloSelecItem = si;

		upEspecialidad();
		return si;
	}

	public List<Especialidades> upEspecialidad() {
		List<Especialidades> es = new ArrayList<Especialidades>();
		if (idTituloAcademico != null) {
			TituloAcademico ta = em.find(TituloAcademico.class,
					idTituloAcademico);
			if (ta.getDescripcion().toLowerCase().equals("otros"))
				esOtro = true;
			else
				esOtro = false;

			if (ta.getDescripcion().toLowerCase().equals("ninguno")) {
				noTienTitulo();
			} else
				tieneTitulo = true;

			if (idIstUniversidad != null) {
				InstitucionEducativa ie = em.find(InstitucionEducativa.class,
						idIstUniversidad);
				if (ie.getDescripcion().toLowerCase().equals("ninguna")) {
					habPais = false;
					if (ta.getDescripcion().toLowerCase().equals("ninguno")) {
						esNingunInstitucionTituto();// selecciono ningun titulo
													// ninguna institucion

					} else {
						esNingunaInsNingunTit = false;

					}
				} else
					habPais = true;
			} else
				habPais = false;

		} else {
			esNingunaInsNingunTit = false;
			esOtro = false;
			noTienTitulo();
			if (idIstUniversidad != null) {
				InstitucionEducativa ie = em.find(InstitucionEducativa.class,
						idIstUniversidad);
				if (ie.getDescripcion().toLowerCase().equals("ninguna"))
					habPais = false;
				else
					habPais = true;

			} else
				habPais = false;
		}

		// getEspecialidadSelecSelectItems(es);
		habFechaFin();
		return es;

	}

	public Boolean habFechaFin() {
		return !esNingunaInsNingunTit & tieneTitulo;
	}

	@SuppressWarnings("unchecked")
	public List<Pais> upInstitucionOtro() {
		List<Pais> pais = new ArrayList<Pais>();
		if (idIstUniversidad != null) {
			InstitucionEducativa ie = em.find(InstitucionEducativa.class,
					idIstUniversidad);
			List<InstitucionEducativa> ieList = em.createQuery(
					"select t from InstitucionEducativa t "
							+ " where lower(t.descripcion) like '"
							+ ie.getDescripcion().toLowerCase() + "'")
					.getResultList();
			habPais = true;
			esNingunaInsNingunTit = false;
			if (ie.getDescripcion().toLowerCase().equals("otras")
					|| ie.getDescripcion().toLowerCase().equals("otra")) {
				esOtraInst = true;

			} else
				esOtraInst = false;
			if (ie.getDescripcion().toLowerCase().equals("ninguna"))
				habPais = false;
			else
				habPais = true;

			if (idTituloAcademico != null) {
				TituloAcademico ta = em.find(TituloAcademico.class,
						idTituloAcademico);
				if (ie.getDescripcion().toLowerCase().equals("ninguna")
						&& ta.getDescripcion().toLowerCase().equals("ninguno")) {
					esNingunInstitucionTituto();
				} else
					esNingunaInsNingunTit = false;

			} else
				habPais = true;

			if (!esOtraInst) {
				for (int i = 0; i < ieList.size(); i++) {
					if (ieList.get(i).getPais() != null) {
						Pais p = em.find(Pais.class, ieList.get(i).getPais()
								.getIdPais());
						pais.add(p);
					}

				}
			} else {
				pais = em
						.createQuery(
								"Select p from Pais p "
										+ "where p.activo = true order by p.descripcion")
						.getResultList();
			}

		} else
			habPais = false;

		getPaisInstSelectItems(pais);

		return pais;

	}

	public List<SelectItem> getPaisInstSelectItems(List<Pais> p) {
		List<SelectItem> si = new Vector<SelectItem>();
		paisSelecItem = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Pais e : p)
			si.add(new SelectItem(e.getIdPais(), "" + e.getDescripcion()));
		paisSelecItem = si;
		return si;
	}

	public List<SelectItem> getEspecialidadSelecSelectItems(
			List<Especialidades> es) {
		List<SelectItem> si = new Vector<SelectItem>();
		profesionSelecItem = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (Especialidades e : es)
			si.add(new SelectItem(e.getIdEspecialidades(), ""
					+ e.getDescripcion()));
		profesionSelecItem = si;
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

	private void noTienTitulo() {
		tieneTitulo = false;
		horasAnios = null;
	}

	public void setearFecha() {
		System.out.println(estudiosRealizados.getFechaFin());
	}

	// GETTERS Y SETTERS
	public EstudiosRealizados getEstudiosRealizados() {
		return estudiosRealizados;
	}

	public void setEstudiosRealizados(EstudiosRealizados estudiosRealizados) {
		this.estudiosRealizados = estudiosRealizados;
	}

	public List<EstudiosRealizados> getEstudiosRealizadosList() {
		return estudiosRealizadosList;
	}

	public void setEstudiosRealizadosList(
			List<EstudiosRealizados> estudiosRealizadosList) {
		this.estudiosRealizadosList = estudiosRealizadosList;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Long getIdNivelEstudio() {
		return idNivelEstudio;
	}

	public void setIdNivelEstudio(Long idNivelEstudio) {
		this.idNivelEstudio = idNivelEstudio;
	}

	public Long getIdProfesion() {
		return idProfesion;
	}

	public void setIdProfesion(Long idProfesion) {
		this.idProfesion = idProfesion;
	}

	public Long getIdIstUniversidad() {
		return idIstUniversidad;
	}

	public void setIdIstUniversidad(Long idIstUniversidad) {
		this.idIstUniversidad = idIstUniversidad;
	}

	public Long getIdIdioma() {
		return idIdioma;
	}

	public void setIdIdioma(Long idIdioma) {
		this.idIdioma = idIdioma;
	}

	public Long getIdHabla() {
		return idHabla;
	}

	public void setIdHabla(Long idHabla) {
		this.idHabla = idHabla;
	}

	public Long getIdEscribe() {
		return idEscribe;
	}

	public void setIdEscribe(Long idEscribe) {
		this.idEscribe = idEscribe;
	}

	public Long getIdLee() {
		return idLee;
	}

	public void setIdLee(Long idLee) {
		this.idLee = idLee;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public boolean isEsEdit() {
		return esEdit;
	}

	public void setEsEdit(boolean esEdit) {
		this.esEdit = esEdit;
	}

	public int getIndexUp() {
		return indexUp;
	}

	public void setIndexUp(int indexUp) {
		this.indexUp = indexUp;
	}

	public List<IdiomasPersona> getIdiomasPersonasList() {
		return idiomasPersonasList;
	}

	public void setIdiomasPersonasList(List<IdiomasPersona> idiomasPersonasList) {
		this.idiomasPersonasList = idiomasPersonasList;
	}

	public List<SelectItem> getTituloSelecItem() {
		return tituloSelecItem;
	}

	public void setTituloSelecItem(List<SelectItem> tituloSelecItem) {
		this.tituloSelecItem = tituloSelecItem;
	}

	public List<SelectItem> getProfesionSelecItem() {
		return profesionSelecItem;
	}

	public void setProfesionSelecItem(List<SelectItem> profesionSelecItem) {
		this.profesionSelecItem = profesionSelecItem;
	}

	public Long getIdTituloAcademico() {
		return idTituloAcademico;
	}

	public void setIdTituloAcademico(Long idTituloAcademico) {
		this.idTituloAcademico = idTituloAcademico;
	}

	public boolean isEsOtro() {
		return esOtro;
	}

	public void setEsOtro(boolean esOtro) {
		this.esOtro = esOtro;
	}

	public boolean isEsOtraInst() {
		return esOtraInst;
	}

	public void setEsOtraInst(boolean esOtraInst) {
		this.esOtraInst = esOtraInst;
	}

	public List<SelectItem> getPaisSelecItem() {
		return paisSelecItem;
	}

	public void setPaisSelecItem(List<SelectItem> paisSelecItem) {
		this.paisSelecItem = paisSelecItem;
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

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public HorasAnios getHorasAnios() {
		return horasAnios;
	}

	public void setHorasAnios(HorasAnios horasAnios) {
		this.horasAnios = horasAnios;
	}

	public boolean isHabPais() {
		return habPais;
	}

	public void setHabPais(boolean habPais) {
		this.habPais = habPais;
	}

	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
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

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public boolean isEsNingunaInsNingunTit() {
		return esNingunaInsNingunTit;
	}

	public void setEsNingunaInsNingunTit(boolean esNingunaInsNingunTit) {
		this.esNingunaInsNingunTit = esNingunaInsNingunTit;
	}

	public boolean isTieneTitulo() {
		return tieneTitulo;
	}

	public void setTieneTitulo(boolean tieneTitulo) {
		this.tieneTitulo = tieneTitulo;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Boolean getPrimeraVezEstudio() {
		return primeraVezEstudio;
	}

	public void setPrimeraVezEstudio(Boolean primeraVezEstudio) {
		this.primeraVezEstudio = primeraVezEstudio;
	}

	public Boolean getPrimeraVezIdioma() {
		return primeraVezIdioma;
	}

	public void setPrimeraVezIdioma(Boolean primeraVezIdioma) {
		this.primeraVezIdioma = primeraVezIdioma;
	}

	public Boolean getCopiaInicialEstudios() {
		return copiaInicialEstudios;
	}

	public void setCopiaInicialEstudios(Boolean copiaInicialEstudios) {
		this.copiaInicialEstudios = copiaInicialEstudios;
	}

	public List<EstudiosRealizados> getEstudiosRealizadosCopiaList() {
		return estudiosRealizadosCopiaList;
	}

	public void setEstudiosRealizadosCopiaList(
			List<EstudiosRealizados> estudiosRealizadosCopiaList) {
		this.estudiosRealizadosCopiaList = estudiosRealizadosCopiaList;
	}

	public List<IdiomasPersona> getIdiomasPersonasCopiaList() {
		return idiomasPersonasCopiaList;
	}

	public void setIdiomasPersonasCopiaList(
			List<IdiomasPersona> idiomasPersonasCopiaList) {
		this.idiomasPersonasCopiaList = idiomasPersonasCopiaList;
	}

	public Boolean getCopiaInicialIdioma() {
		return copiaInicialIdioma;
	}

	public void setCopiaInicialIdioma(Boolean copiaInicialIdioma) {
		this.copiaInicialIdioma = copiaInicialIdioma;
	}

	public Long getIdPersonaPostulante() {
		return idPersonaPostulante;
	}

	public void setIdPersonaPostulante(Long idPersonaPostulante) {
		this.idPersonaPostulante = idPersonaPostulante;
	}

	public Long getIdPostulacion() {
		return idPostulacion;
	}

	public void setIdPostulacion(Long idPostulacion) {
		this.idPostulacion = idPostulacion;
	}

	public boolean isBloquearTab() {
		return bloquearTab;
	}

	public void setBloquearTab(boolean bloquearTab) {
		this.bloquearTab = bloquearTab;
	}

	public boolean isEsOtroNivel() {
		return esOtroNivel;
	}

	public void setEsOtroNivel(boolean esOtroNivel) {
		this.esOtroNivel = esOtroNivel;
	}

}
