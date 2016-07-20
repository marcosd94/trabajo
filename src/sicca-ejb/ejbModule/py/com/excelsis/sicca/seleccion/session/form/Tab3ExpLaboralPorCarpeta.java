package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.Estado;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ExperienciaLaboralHome;
import py.com.excelsis.sicca.session.ExperienciaLaboralList;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("tab3ExpLaboralPorCarpeta")
public class Tab3ExpLaboralPorCarpeta {

	@In(required = false)
	Usuario usuarioLogueado;

	@In(value = "entityManager")
	EntityManager em;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	ExperienciaLaboralHome experienciaLaboralHome;
	@In(create = true)
	ExperienciaLaboralList experienciaLaboralList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	@RequestParameter
	private Long personaIdPersona;
	@RequestParameter
	private Long concursoPuestoAgrIdConcursoPuestoAgr;

	private Long idTipoDocumento;
	private Long idTipoVinculacion;

	private Long idPais;
	private Long idSector;
	private Long idPersona;
	private Long idGrupo;
	private String experiencia;

	private boolean esOtrovinculo;
	private boolean esOtroSector;
	private boolean mostrarMsg = false;
	private boolean esPrimeraVez;
	private boolean copiarInicial;
	private boolean bloquearTab;
	private Persona persona;
	private ExperienciaLaboral experienciaLaboral;
	private General general;
	private Postulacion postulacion;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Concurso concurso;

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

	private Long idDocCopia;
	private List<ExperienciaLaboral> listDetails = new ArrayList<ExperienciaLaboral>();
	private List<ExperienciaLaboral> listDetailsIni = new ArrayList<ExperienciaLaboral>();

	/**
	 * Inicializa los datos al ingresar a la ficha
	 */
	public void init() {
		try {

			buscarPostulacion();
			if (postulacion == null || postulacion.getIdPostulacion() == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"La persona no cuenta con postulaciones");
				return;
			} else {
				bloquearTab = seleccionUtilFormController
						.existePersonaPostulante(
								concursoPuestoAgrIdConcursoPuestoAgr,
								personaIdPersona);
				idPersona = personaIdPersona;
				idGrupo = concursoPuestoAgrIdConcursoPuestoAgr;
				if (!bloquearTab) {
					esPrimeraVez = esPrimeraVez();
					if (!esPrimeraVez) {
						mostrarMsg = true;
						copiarInicial = false;
					} else {
						mostrarMsg = false;
						copiarInicial = true;
					}

				}

			}
			initTab();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inicializa los datos al ingresar al tab
	 */
	public void initTab() {
		try {

			experienciaLaboral = new ExperienciaLaboral();
			experienciaLaboralList.getPersona().setIdPersona(idPersona);
			persona = em.find(Persona.class, idPersona);
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idGrupo);
			concurso = buscarConcursoCorrespondiente();
			if(postulacion.getPersonaPostulante() != null)
			experienciaLaboralList.setIdPersonaPostulante(postulacion.getPersonaPostulante().getIdPersonaPostulante());
			experienciaLaboralList.getExperienciaLaboral().setActivo(
					Estado.ACTIVO.getValor());
			experienciaLaboralList.setMaxResults(null);
			listDetails = new ArrayList<ExperienciaLaboral>(
					experienciaLaboralList.getResultList());
			listDetailsIni = new ArrayList<ExperienciaLaboral>();
			listDetailsIni.addAll(listDetails);

			idPais = idParaguay();
			experiencia = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Busca registros en la tabla postulacion para verificar si es su primera
	 * entrada a la ficha
	 */
	private void buscarPostulacion() {

		Query q = em
				.createQuery("select Postulacion from Postulacion Postulacion "
						+ " where Postulacion.activo is true and Postulacion.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
						+ " and Postulacion.persona.idPersona = :idPersona  ");
		q.setParameter("idGrupo", concursoPuestoAgrIdConcursoPuestoAgr);
		q.setParameter("idPersona", personaIdPersona);

		postulacion = new Postulacion();
		postulacion = (Postulacion) q.getSingleResult();

	}

	/**
	 * Verifica si es la primera entrada a la ficha
	 * 
	 * @return
	 */
	private Boolean esPrimeraVez() {

		Query q = em
				.createQuery("select exp from ExperienciaLaboral exp where exp.personaPostulante.idPersonaPostulante = :id");
		q.setParameter("id", postulacion.getPersonaPostulante()
				.getIdPersonaPostulante());
		/*
		 * String sql = "select exp.*" +
		 * " from seleccion.experiencia_laboral exp " +
		 * "where exp.id_persona_postulante = " +
		 * postulacion.getPersonaPostulante().getIdPersonaPostulante();
		 */
		List<ExperienciaLaboral> lista = new ArrayList<ExperienciaLaboral>();
		lista = q.getResultList();
		if (lista.isEmpty())
			return true;
		return false;
	}

	/**
	 * Pone el combo pais por defecto con el valor Paraguay
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public void otipoVinculo() {
		if (idTipoVinculacion != null) {
			DatosEspecificos tv = em.find(DatosEspecificos.class,
					idTipoVinculacion);
			if (tv.getDescripcion().toLowerCase().equals("otros")
					|| tv.getDescripcion().toLowerCase().equals("otro"))
				esOtrovinculo = true;
			else
				esOtrovinculo = false;
		}
	}

	public void oSector() {
		if (idSector != null) {
			DatosEspecificos tv = em.find(DatosEspecificos.class, idSector);
			if (tv.getDescripcion().toLowerCase().equals("otros")
					|| tv.getDescripcion().toLowerCase().equals("otro"))
				esOtroSector = true;
			else
				esOtroSector = false;

		}
	}

	/**
	 * Verfica que los datos obligatorios sean cargados y que sus formatos sean
	 * los correctos
	 * 
	 * @return
	 */

	private boolean toDetailOk() {
		Date fechaActual = new Date();
		if (experienciaLaboral.getEmpresa() == null
				|| experienciaLaboral.getEmpresa().isEmpty()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SICCAAppHelper
					.getBundleMessage("ExperienciaLaboral_msg_sin_empresa"));
			return false;
		}
		if (experienciaLaboral.getEmpresa() != null
				&& !experienciaLaboral.getEmpresa().isEmpty()
				&& !experienciaLaboral.getEmpresa().equals("NINGUNA")) {

			if (esOtroSector
					&& (experienciaLaboral.getOtroSector() == null || experienciaLaboral
							.getOtroSector().trim().equals(""))) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe ingresar El campo Otro Sector");
				return false;
			}

			if (esOtrovinculo
					&& (experienciaLaboral.getOtroVincu() == null || experienciaLaboral
							.getOtroVincu().trim().equals(""))) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe ingresar El campo Otro Tipo Vinculo");
				return false;
			}

			if (experienciaLaboral.getFechaDesde() == null) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								SICCAAppHelper
										.getBundleMessage("ExperienciaLaboral_msg_sin_fecha_desde"));
				return false;
			}

			if (idPais == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe especificar el Pais. Verifique");
				return false;
			}
			if (experienciaLaboral.getPuestoCargo() == null
					|| experienciaLaboral.getPuestoCargo().trim().equals("")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe ingresar un Puesto/Cargo");
				return false;
			}

			if (idTipoVinculacion == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe especificar el Tipo de Vinculación. Verifique");
				return false;
			}
			if (esOtrovinculo
					&& (experienciaLaboral.getOtroVincu() == null || experienciaLaboral
							.getOtroVincu().trim().equals(""))) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe especificar Otro Tipo de Vinculación. Verifique");
				return false;
			}
			if (idSector == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe especificar el Item Sector. Verifique");
				return false;
			}
			if (esOtroSector
					&& (experienciaLaboral.getOtroSector() == null || experienciaLaboral
							.getOtroSector().trim().equals(""))) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe especificar Otro Sector. Verifique");
				return false;
			}

			if (experienciaLaboral.getFuncionesRealizadas() == null
					|| experienciaLaboral.getFuncionesRealizadas().trim()
							.isEmpty()) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								SICCAAppHelper
										.getBundleMessage("ExperienciaLaboral_msg_sin_funcion"));
				return false;
			}

			if (experienciaLaboral.getReferenciasLaborales() == null
					|| experienciaLaboral.getReferenciasLaborales().trim()
							.equals("")) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe ingresar las Referencias Laborales ");
				return false;
			}

		}

		if (experienciaLaboral.getFechaHasta() != null
				&& experienciaLaboral.getFechaHasta().before(
						experienciaLaboral.getFechaDesde())) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							SICCAAppHelper
									.getBundleMessage("ExperienciaLaboral_msg_fecha_hasta_incorrecta"));
			return false;
		}
		if (experienciaLaboral.getFechaDesde() != null
				&& experienciaLaboral.getFechaDesde().after(fechaActual)) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							SICCAAppHelper
									.getBundleMessage("ExperienciaLaboral_msg_fecha_desde_incorrecta"));
			return false;
		}
		if (experienciaLaboral.getFechaHasta() != null
				&& experienciaLaboral.getFechaHasta().after(fechaActual)) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							SICCAAppHelper
									.getBundleMessage("ExperienciaLaboral_msg_fecha_hasta_incorrecta_dos"));
			return false;
		}
		if (experienciaLaboral.getFechaDesde() != null
				&& !general.isFechaValida(experienciaLaboral.getFechaDesde())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Desde inválida");
			return false;
		}
		if (experienciaLaboral.getFechaHasta() != null
				&& !general.isFechaValida(experienciaLaboral.getFechaHasta())) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, "Fecha Hasta inválida");
			return false;
		}

		return true;
	}

	/**
	 * Crea el UploadItem para guardar el archivo
	 * 
	 * @param fileName
	 * @param fileSize
	 * @param contentType
	 * @param file
	 */

	private void crearUploadItem(String fileName, int fileSize,
			String contentType, byte[] file) {
		item = new UploadItem(fileName, fileSize, contentType,
				AdminReclamoSugPortalFormController.crearFile(fileName, file));

		nombreDoc = item.getFileName();
	}

	/**
	 * Retorna el concurso correspondiente al grupo recibido por parametro
	 * 
	 * @return
	 */
	private Concurso buscarConcursoCorrespondiente() {
		List<ConcursoPuestoDet> listaConcurso = new ArrayList<ConcursoPuestoDet>();
		String sql = "Select c.* from seleccion.concurso_puesto_det c where c.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaConcurso = em.createNativeQuery(sql, ConcursoPuestoDet.class)
				.getResultList();

		if (listaConcurso.isEmpty())
			return null;
		return listaConcurso.get(0).getConcurso();
	}

	/**
	 * En caso de que sea el primer ingreso y que no se hayan copiado la lista
	 * inicial, se realiza la copia
	 * 
	 * @throws IOException
	 */
	private void copiarListaInicial() throws IOException {
		for (ExperienciaLaboral ex : listDetailsIni) {
			ex.setFechaMod(new Date());
			ex.setUsuMod(seleccionUtilFormController
					.getUsuarioPostulante(persona.getIdPersona()));
			ex.setTipo("CARPETA");
			em.merge(ex);
			ExperienciaLaboral experiencia = new ExperienciaLaboral();
			experiencia.setActivo(ex.getActivo());
			experiencia.setDatosEspecificosSector(ex
					.getDatosEspecificosSector());
			experiencia.setDatosEspecificosTipoVinculo(ex
					.getDatosEspecificosTipoVinculo());
			Documentos d = new Documentos();
			if (experiencia.getDocumentos() != null) {
				crearDocumentoCopia(experiencia.getDocumentos());
				if (idDocCopia != null) {
					d = em.find(Documentos.class, idDocCopia);
					experiencia.setDocumentos(d);
				}
			}
			experiencia.setEmpresa(ex.getEmpresa());
			experiencia.setUsuAlta(seleccionUtilFormController
					.getUsuarioPostulante(persona.getIdPersona()));
			experiencia.setFechaAlta(new Date());
			experiencia.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
			experiencia.setFechaAltaOee(new Date());
			experiencia.setFechaDesde(ex.getFechaDesde());
			experiencia.setFechaHasta(ex.getFechaHasta());
			experiencia.setFuncionesRealizadas(ex.getFuncionesRealizadas());
			experiencia.setOtroSector(ex.getOtroSector());
			experiencia.setOtroVincu(ex.getOtroVincu());
			experiencia.setPais(ex.getPais());
			experiencia
					.setPersonaPostulante(postulacion.getPersonaPostulante());
			experiencia.setPosicion(ex.getPosicion());
			experiencia.setPuestoCargo(ex.getPuestoCargo());
			experiencia.setReferenciasLaborales(ex.getReferenciasLaborales());
			experiencia.setTipo("CARPETA");
			em.persist(experiencia);
			if (idDocCopia != null) {
				d.setIdTabla(experiencia.getIdExperienciaLab());
				d.setUsuMod(usuarioLogueado.getCodigoUsuario());
				d.setFechaMod(new Date());
				em.merge(d);

				PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
				postulacionAdjuntos.setActivo(true);
				postulacionAdjuntos.setDocumento(d);
				postulacionAdjuntos.setFechaAlta(new Date());
				postulacionAdjuntos.setPostulacion(postulacion);
				postulacionAdjuntos.setUsuAlta(seleccionUtilFormController
						.getUsuarioPostulante(persona.getIdPersona()));
				postulacionAdjuntos.setDocumento(d);
				em.persist(postulacionAdjuntos);

			}
		}
		em.flush();
		copiarInicial = false;
	}

	/**
	 * Crea los documentos a copiar en caso de que ya existan Experiencias
	 * 
	 * @param d
	 * @throws IOException
	 */
	private void crearDocumentoCopia(Documentos d) throws IOException {
		File inputFile = new File(d.getUbicacionFisica() + File.separator
				+ d.getNombreFisicoDoc());
		byte[] pdf = getBytesFromFile(inputFile);
		idDocCopia = guardarAdjuntosCopia(d.getNombreFisicoDoc(),
				new Integer(d.getTamanhoDoc()).intValue(), d.getMimetype(),
				pdf, d.getDatosEspecificos().getIdDatosEspecificos(), null,
				personaIdPersona);
	}

	/**
	 * Guarda los documentos adjuntos en la inserción principal
	 * 
	 * @param fileName
	 * @param fileSize
	 * @param contentType
	 * @param file
	 * @param idTipoDoc
	 * @param idDocumento
	 * @param idPersona
	 * @return
	 */
	private Long guardarAdjuntosPrincipal(String fileName, int fileSize,
			String contentType, byte[] file, Long idTipoDoc, Long idDocumento,
			Long idPersona) {
		try {

			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
					fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "ExperienciaLaboral";
			String nombrePantalla = "experienciaLaboralCarpeta_edit";
			String direccionFisica = "";
			Persona per = em.find(Persona.class, idPersona);
			String nro_doc_ID = per.getDocumentoIdentidad()
					+ per.getIdPersona();
			direccionFisica = File.separator + "SICCA" + File.separator
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

	/**
	 * Guarda los documentos adjuntos insertados en la copia
	 * 
	 * @param fileName
	 * @param fileSize
	 * @param contentType
	 * @param file
	 * @param idTipoDoc
	 * @param idDocumento
	 * @param idPersona
	 * @return
	 */
	private Long guardarAdjuntosCopia(String fileName, int fileSize,
			String contentType, byte[] file, Long idTipoDoc, Long idDocumento,
			Long idPersona) {
		try {

			UploadItem fileItem = seleccionUtilFormController.crearUploadItem(
					fileName, fileSize, contentType, file);
			Long idDocuGenerado;
			String nombreTabla = "ExperienciaLaboral";
			String nombrePantalla = "experienciaLaboralCarpeta_edit_copia";
			String direccionFisica = "";
			SimpleDateFormat sdfSoloAnio = new SimpleDateFormat("yyyy");
			String anio = sdfSoloAnio.format(new Date());
			Persona per = em.find(Persona.class, idPersona);
			String nro_doc_ID = per.getDocumentoIdentidad()
					+ per.getIdPersona();
			String separador = File.separator;

			direccionFisica = separador
					+ "SICCA"
					+ separador
					+ anio
					+ separador
					+ "OEE"
					+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos() + separador
					+ concurso.getIdConcurso() + separador
					+ concursoPuestoAgrIdConcursoPuestoAgr + separador
					+ "POSTULANTES" + separador + nro_doc_ID;
			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(fileItem,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Método principar que agrega los datos a la grilla, a la base de datos y
	 * realiza la copia
	 * 
	 * @throws IOException
	 */
	public void addRow() throws IOException {
		if (!toDetailOk() || !chequearVarios())
			return;
		try {
			if (copiarInicial)
				copiarListaInicial();

			insertarPrincipal();
			if (esPrimeraVez)
				insertarCopia();
			em.flush();
			clearDataDetail();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Realiza la inserción principal
	 */
	private void insertarPrincipal() {
		try {
			if (uploadedFile != null)
				idDoc = guardarAdjuntosPrincipal(fileName, uploadedFile.length,
						contentType, uploadedFile, idTipoDocumento, null,
						persona.getIdPersona());

			if (idPais != null)
				experienciaLaboral.setPais(em.find(Pais.class, idPais));
			if (idSector != null)
				experienciaLaboral.setDatosEspecificosSector(em.find(
						DatosEspecificos.class, idSector));
			if (idTipoVinculacion != null)
				experienciaLaboral.setDatosEspecificosTipoVinculo(em.find(
						DatosEspecificos.class, idTipoVinculacion));
			experienciaLaboral.setPersona(persona);
			experienciaLaboral.setPersonaPostulante(postulacion.getPersonaPostulante());
			experienciaLaboral.setActivo(Estado.ACTIVO.getValor());
			experienciaLaboral.setUsuAlta(seleccionUtilFormController
					.getUsuarioPostulante(persona.getIdPersona()));
			experienciaLaboral.setFechaAlta(new Date());
			experienciaLaboral.setTipo("CARPETA");
			experienciaLaboral
					.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
			experienciaLaboral.setFechaAltaOee(new Date());
			if (experiencia != null) {
				if (experiencia.equalsIgnoreCase("G")) {
					experienciaLaboral.setGeneral(true);
					experienciaLaboral.setEspecifica(false);
				}
				if (experiencia.equalsIgnoreCase("E")) {
					experienciaLaboral.setGeneral(false);
					experienciaLaboral.setEspecifica(true);
				}
			}
			Documentos doc = new Documentos();
			if (idDoc != null) {
				doc = em.find(Documentos.class, idDoc);
				experienciaLaboral.setDocumentos(doc);
			}
			em.persist(experienciaLaboral);
			listDetails.add(experienciaLaboral);
			if (idDoc != null) {
				doc.setIdTabla(experienciaLaboral.getIdExperienciaLab());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);

				PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
				postulacionAdjuntos.setActivo(true);
				postulacionAdjuntos.setDocumento(doc);
				postulacionAdjuntos.setFechaAlta(new Date());
				postulacionAdjuntos.setPostulacion(postulacion);
				postulacionAdjuntos.setUsuAlta(seleccionUtilFormController
						.getUsuarioPostulante(persona.getIdPersona()));
				postulacionAdjuntos.setDocumento(doc);
				em.persist(postulacionAdjuntos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// em.flush();
	}

	/**
	 * Realiza la insercion de la copia
	 */
	private void insertarCopia() {
		try {
			if (uploadedFile != null)
				idDocCopia = guardarAdjuntosCopia(fileName,
						uploadedFile.length, contentType, uploadedFile,
						idTipoDocumento, null, persona.getIdPersona());
			if (idDocCopia != null) {
				ExperienciaLaboral experiencia = new ExperienciaLaboral();

				experiencia.setDatosEspecificosSector(experienciaLaboral
						.getDatosEspecificosSector());
				experiencia.setDatosEspecificosTipoVinculo(experienciaLaboral
						.getDatosEspecificosTipoVinculo());
				experiencia
						.setDocumentos(em.find(Documentos.class, idDocCopia));
				experiencia.setEmpresa(experienciaLaboral.getEmpresa());
				experiencia.setUsuAlta(seleccionUtilFormController
						.getUsuarioPostulante(persona.getIdPersona()));
				experiencia.setFechaAlta(new Date());
				experiencia.setUsuAltaOee(usuarioLogueado.getCodigoUsuario());
				experiencia.setFechaAltaOee(new Date());
				experiencia.setFechaDesde(experienciaLaboral.getFechaDesde());
				experiencia.setFechaHasta(experienciaLaboral.getFechaHasta());
				experiencia.setFuncionesRealizadas(experienciaLaboral
						.getFuncionesRealizadas());
				experiencia.setOtroSector(experienciaLaboral.getOtroSector());
				experiencia.setOtroVincu(experienciaLaboral.getOtroVincu());
				experiencia.setPais(experienciaLaboral.getPais());
				experiencia.setPersonaPostulante(postulacion
						.getPersonaPostulante());
				experiencia.setPosicion(experienciaLaboral.getPosicion());
				experiencia.setPuestoCargo(experienciaLaboral.getPuestoCargo());
				experiencia.setReferenciasLaborales(experienciaLaboral
						.getReferenciasLaborales());
				experiencia.setActivo(true);
				Documentos doc = em.find(Documentos.class, idDocCopia);
				experiencia.setDocumentos(doc);
				experiencia.setTipo("CARPETA");
				em.persist(experiencia);

				doc.setIdTabla(experiencia.getIdExperienciaLab());
				doc.setUsuMod(usuarioLogueado.getCodigoUsuario());
				doc.setFechaMod(new Date());
				em.merge(doc);

				PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
				postulacionAdjuntos.setActivo(true);
				postulacionAdjuntos.setDocumento(doc);
				postulacionAdjuntos.setFechaAlta(new Date());
				postulacionAdjuntos.setPostulacion(postulacion);
				postulacionAdjuntos.setUsuAlta(seleccionUtilFormController
						.getUsuarioPostulante(persona.getIdPersona()));
				postulacionAdjuntos.setDocumento(doc);
				em.persist(postulacionAdjuntos);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Elimina el registro seleccionado de la grilla y de la base de datos
	 * 
	 * @param pos
	 */
	public void removeRow(int pos) {
		statusMessages.clear();
		listDetails.get(pos).setActivo(Estado.INACTIVO.getValor());
		ExperienciaLaboral el = em.find(ExperienciaLaboral.class, listDetails
				.get(pos).getIdExperienciaLab());
		el.setFechaMod(new Date());
		el.setUsuMod(seleccionUtilFormController.getUsuarioPostulante(persona
				.getIdPersona()));
		if (el.getDocumentos() != null) {
			Documentos doc = em.find(Documentos.class, el.getDocumentos()
					.getIdDocumento());
			doc.setActivo(false);
			doc.setUsuMod(seleccionUtilFormController
					.getUsuarioPostulante(persona.getIdPersona()));
			doc.setFechaMod(new Date());
			em.merge(doc);

		}
		em.merge(el);

		for (ExperienciaLaboral ex : listDetailsIni) {
			if (el.getIdExperienciaLab().equals(ex.getIdExperienciaLab()))
				listDetailsIni.remove(ex);
		}
		listDetails.remove(pos);
		em.flush();
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	/**
	 * Carga los datos del registro que quiere modificarse
	 * 
	 * @param pos
	 */
	public void selectToEdit(int pos) {
		try {
			experienciaLaboral = listDetails.get(pos);
			experienciaLaboral.setPosicion(pos);

			if (experienciaLaboral.getPais() != null)
				idPais = experienciaLaboral.getPais().getIdPais();
			if (experienciaLaboral.getDatosEspecificosTipoVinculo() != null) {
				idTipoVinculacion = experienciaLaboral
						.getDatosEspecificosTipoVinculo()
						.getIdDatosEspecificos();
				if (experienciaLaboral.getDatosEspecificosTipoVinculo()
						.getDescripcion().toLowerCase().equals("otros")
						|| experienciaLaboral.getDatosEspecificosTipoVinculo()
								.getDescripcion().toLowerCase().equals("otro")) {
					esOtrovinculo = true;
				} else
					esOtrovinculo = false;
			}

			if (experienciaLaboral.getDatosEspecificosSector() != null) {
				idSector = experienciaLaboral.getDatosEspecificosSector()
						.getIdDatosEspecificos();
				if (experienciaLaboral.getDatosEspecificosSector()
						.getDescripcion().toLowerCase().equals("otros")
						|| experienciaLaboral.getDatosEspecificosSector()
								.getDescripcion().toLowerCase().equals("otro")) {
					esOtroSector = true;
				} else
					esOtroSector = false;
			}

			if (experienciaLaboral.getDocumentos() != null) {
				Documentos doc = em.find(Documentos.class, experienciaLaboral
						.getDocumentos().getIdDocumento());
				String url = doc.getUbicacionFisica();
				if (url.contains("\\")) {
					url = url.replace("\\",
							System.getProperty("file.separator"));

				}
				if (url.contains("//")) {
					url = url.replace("//",
							System.getProperty("file.separator"));

				}
				String separador = System.getProperty("file.separator");
				if (!url.endsWith(separador))
					url = url + System.getProperty("file.separator");

				url += doc.getNombreFisicoDoc();

				inputFile = new File(url);
				idTipoDocumento = doc.getDatosEspecificos()
						.getIdDatosEspecificos();
				nombreDoc = doc.getNombreFisicoDoc();

			} else
				inputFile = null;
			experiencia = null;
			if (experienciaLaboral.getGeneral() != null
					&& experienciaLaboral.getGeneral())
				experiencia = "G";
			if (experienciaLaboral.getEspecifica() != null
					&& experienciaLaboral.getEspecifica())
				experiencia = "E";
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateOnList() {
		if (!toDetailOk())
			return;
		if (experiencia != null) {
			if (experiencia.equalsIgnoreCase("G")) {
				experienciaLaboral.setGeneral(true);
				experienciaLaboral.setEspecifica(false);
			}
			if (experiencia.equalsIgnoreCase("E")) {
				experienciaLaboral.setGeneral(false);
				experienciaLaboral.setEspecifica(true);
			}
		}
		for (ExperienciaLaboral ex : listDetails) {
			if (ex.getIdExperienciaLab() != null
					&& ex.getIdExperienciaLab().intValue() != experienciaLaboral
							.getIdExperienciaLab().intValue()) {
				if (ex.getEmpresa()
						.trim()
						.toLowerCase()
						.equals(experienciaLaboral.getEmpresa().trim()
								.toLowerCase())) {

					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									SICCAAppHelper
											.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
					return;
				}
			} else if (ex.getIdExperienciaLab() == null) {
				if (ex.getEmpresa()
						.trim()
						.toLowerCase()
						.equals(experienciaLaboral.getEmpresa().trim()
								.toLowerCase())) {

					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									SICCAAppHelper
											.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
					return;
				}
			}

		}

		if (uploadedFile != null)
			idDoc = guardarAdjuntosPrincipal(fileName, uploadedFile.length,
					contentType, uploadedFile, idTipoDocumento,
					experienciaLaboral.getDocumentos().getIdDocumento(),
					persona.getIdPersona());
		if (idDoc != null) {
			Documentos docActual = em.find(Documentos.class, idDoc);
			experienciaLaboral.setDocumentos(docActual);

			docActual.setIdTabla(experienciaLaboral.getIdExperienciaLab());
			docActual.setUsuMod(usuarioLogueado.getCodigoUsuario());
			docActual.setFechaMod(new Date());
			em.merge(docActual);
			em.merge(experienciaLaboral);
			actualizarListaPrincipal(docActual);

			if (listDetailsIni.size() > experienciaLaboral.getPosicion())
				actualizarListaInicial(docActual);

		}

		else
			return;

		clearDataDetail();
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	private void actualizarListaPrincipal(Documentos d) {
		listDetails.get(experienciaLaboral.getPosicion()).setOtroSector(
				experienciaLaboral.getOtroSector());
		listDetails.get(experienciaLaboral.getPosicion()).setOtroVincu(
				experienciaLaboral.getOtroVincu());
		listDetails.get(experienciaLaboral.getPosicion()).setPuestoCargo(
				experienciaLaboral.getPuestoCargo());
		if (idSector != null)
			listDetails.get(experienciaLaboral.getPosicion())
					.setDatosEspecificosSector(
							em.find(DatosEspecificos.class, idSector));
		if (idTipoVinculacion != null)
			listDetails.get(experienciaLaboral.getPosicion())
					.setDatosEspecificosTipoVinculo(
							em.find(DatosEspecificos.class, idTipoVinculacion));
		listDetails.get(experienciaLaboral.getPosicion()).setEmpresa(
				experienciaLaboral.getEmpresa().trim());
		listDetails.get(experienciaLaboral.getPosicion()).setFechaDesde(
				experienciaLaboral.getFechaDesde());
		listDetails.get(experienciaLaboral.getPosicion()).setFechaHasta(
				experienciaLaboral.getFechaHasta());
		listDetails.get(experienciaLaboral.getPosicion())
				.setFuncionesRealizadas(
						experienciaLaboral.getFuncionesRealizadas().trim());
		listDetails.get(experienciaLaboral.getPosicion())
				.setReferenciasLaborales(
						experienciaLaboral.getReferenciasLaborales().trim());
		listDetails.get(experienciaLaboral.getPosicion()).setDocumentos(d);
	}

	private void actualizarListaInicial(Documentos d) {
		listDetailsIni.get(experienciaLaboral.getPosicion()).setOtroSector(
				experienciaLaboral.getOtroSector());
		listDetailsIni.get(experienciaLaboral.getPosicion()).setOtroVincu(
				experienciaLaboral.getOtroVincu());
		listDetailsIni.get(experienciaLaboral.getPosicion()).setPuestoCargo(
				experienciaLaboral.getPuestoCargo());
		if (idSector != null)
			listDetailsIni.get(experienciaLaboral.getPosicion())
					.setDatosEspecificosSector(
							em.find(DatosEspecificos.class, idSector));
		if (idTipoVinculacion != null)
			listDetailsIni.get(experienciaLaboral.getPosicion())
					.setDatosEspecificosTipoVinculo(
							em.find(DatosEspecificos.class, idTipoVinculacion));
		listDetailsIni.get(experienciaLaboral.getPosicion()).setEmpresa(
				experienciaLaboral.getEmpresa().trim());
		listDetailsIni.get(experienciaLaboral.getPosicion()).setFechaDesde(
				experienciaLaboral.getFechaDesde());
		listDetailsIni.get(experienciaLaboral.getPosicion()).setFechaHasta(
				experienciaLaboral.getFechaHasta());
		listDetailsIni.get(experienciaLaboral.getPosicion())
				.setFuncionesRealizadas(
						experienciaLaboral.getFuncionesRealizadas().trim());
		listDetailsIni.get(experienciaLaboral.getPosicion())
				.setReferenciasLaborales(
						experienciaLaboral.getReferenciasLaborales().trim());
		listDetailsIni.get(experienciaLaboral.getPosicion()).setDocumentos(d);
	}

	public void clearDataDetail() {
		experienciaLaboral = new ExperienciaLaboral();
		nombreDoc = null;
		idTipoDocumento = null;
		item = null;
		idPais = idParaguay();
		idTipoDocumento = null;
		idSector = null;
		esOtroSector = false;
		esOtrovinculo = false;
		experiencia = null;
		idTipoVinculacion = null;

	}

	private Boolean chequearVarios() {

		for (ExperienciaLaboral ex : listDetails) {
			if (ex.getEmpresa()
					.trim()
					.toLowerCase()
					.equals(experienciaLaboral.getEmpresa().trim()
							.toLowerCase())) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, SICCAAppHelper
						.getBundleMessage("ExperienciaLaboral_msg_en_detalle"));
				return false;
			}
		}
		return true;
	}

	// Returns the contents of the file in a byte array.
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
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public ExperienciaLaboral getExperienciaLaboral() {
		return experienciaLaboral;
	}

	public void setExperienciaLaboral(ExperienciaLaboral experienciaLaboral) {
		this.experienciaLaboral = experienciaLaboral;
	}

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
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

	public Long getIdTipoVinculacion() {
		return idTipoVinculacion;
	}

	public void setIdTipoVinculacion(Long idTipoVinculacion) {
		this.idTipoVinculacion = idTipoVinculacion;
	}

	public boolean isEsOtrovinculo() {
		return esOtrovinculo;
	}

	public void setEsOtrovinculo(boolean esOtrovinculo) {
		this.esOtrovinculo = esOtrovinculo;
	}

	public Long getIdSector() {
		return idSector;
	}

	public void setIdSector(Long idSector) {
		this.idSector = idSector;
	}

	public boolean isEsOtroSector() {
		return esOtroSector;
	}

	public void setEsOtroSector(boolean esOtroSector) {
		this.esOtroSector = esOtroSector;
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

	public void changeNameDoc() {
		nombreDoc = null;
	}

	public List<ExperienciaLaboral> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<ExperienciaLaboral> listDetails) {
		this.listDetails = listDetails;
	}

	public boolean isMostrarMsg() {
		return mostrarMsg;
	}

	public void setMostrarMsg(boolean mostrarMsg) {
		this.mostrarMsg = mostrarMsg;
	}

	public General getGeneral() {
		return general;
	}

	public void setGeneral(General general) {
		this.general = general;
	}

	public Postulacion getPostulacion() {
		return postulacion;
	}

	public void setPostulacion(Postulacion postulacion) {
		this.postulacion = postulacion;
	}

	public boolean isEsPrimeraVez() {
		return esPrimeraVez;
	}

	public void setEsPrimeraVez(boolean esPrimeraVez) {
		this.esPrimeraVez = esPrimeraVez;
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

	public void setConcursoPuestoAgrIdConcursoPuestoAgr(
			Long concursoPuestoAgrIdConcursoPuestoAgr) {
		this.concursoPuestoAgrIdConcursoPuestoAgr = concursoPuestoAgrIdConcursoPuestoAgr;
	}

	public boolean isCopiarInicial() {
		return copiarInicial;
	}

	public void setCopiarInicial(boolean copiarInicial) {
		this.copiarInicial = copiarInicial;
	}

	public List<ExperienciaLaboral> getListDetailsIni() {
		return listDetailsIni;
	}

	public void setListDetailsIni(List<ExperienciaLaboral> listDetailsIni) {
		this.listDetailsIni = listDetailsIni;
	}

	public Long getIdDocCopia() {
		return idDocCopia;
	}

	public void setIdDocCopia(Long idDocCopia) {
		this.idDocCopia = idDocCopia;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public boolean isBloquearTab() {
		return bloquearTab;
	}

	public void setBloquearTab(boolean bloquearTab) {
		this.bloquearTab = bloquearTab;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getExperiencia() {
		return experiencia;
	}

	public void setExperiencia(String experiencia) {
		this.experiencia = experiencia;
	}

}
