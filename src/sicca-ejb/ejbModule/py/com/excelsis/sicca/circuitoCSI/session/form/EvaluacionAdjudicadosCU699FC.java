package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import py.com.excelsis.sicca.dto.EvalDocumentalDetDTO;
import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalDocumentalComis;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionAdjuntos;
import py.com.excelsis.sicca.entity.TipoArchivo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.util.IntegrantesDTO;
import py.com.excelsis.sicca.util.JpaResourceBean;
@Scope(ScopeType.CONVERSATION)
@Name("evaluacionAdjudicadosCU699FC")
public class EvaluacionAdjudicadosCU699FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	EvalDocumentalCabHome evalDocumentalCabHome;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private Concurso concurso;
	private EvalDocumentalCab evalDocumentalCab;
	private ComisionSeleccionCab comisionSeleccionCab;

	private String direccionFisica;
	private String nombrePantalla;
	private String observacion;
	private String comision;
	private String msgSave;
	private Long idIntegranteComision;
	private Long idDoc;
	private Long idTipoDoc;
	private Integer indice;

	/**
	 * DOCUMENTO ADJUNTO
	 * **/
	
	private byte[] uploadedFile;
	private UploadItem item;
	private String contentType;
	private String fileName;
	private String nombreDoc;

	private List<EvalDocumentalDet> listaEvalDocumentalDet;
	private List<EvalDocumentalDetDTO> listaEvalDocumentalDetDTO;
	private List<IntegrantesDTO> listaEvaluadoresDTO;
	private List<IntegrantesDTO> listaEvaluadoresDTOEliminar;
	private List<IntegrantesDTO> listaEvaluadoresDTOAux;
	private List<SelectItem> integrantesComisionSelecItem;
	private List<Documentos> listaDocAmostrar;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		evalDocumentalCab = new EvalDocumentalCab();
		evalDocumentalCab = evalDocumentalCabHome.getInstance();
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = evalDocumentalCab.getConcursoPuestoAgr();
		listaEvaluadoresDTO = new ArrayList<IntegrantesDTO>();
		listaEvaluadoresDTOAux = new ArrayList<IntegrantesDTO>();
		listaEvaluadoresDTOEliminar = new ArrayList<IntegrantesDTO>();
		recuperarListaEvaluadores();
		concurso = new Concurso();
		concurso = concursoPuestoAgr.getConcurso();
		buscarDetalles();
		buscarComision();
		cargarComboIntegrantesComision();

		String valor_persona = evalDocumentalCab.getPostulacion()
				.getPersonaPostulante().getDocumentoIdentidad()
				+ "_";
		valor_persona = valor_persona
				+ evalDocumentalCab.getPostulacion().getPersonaPostulante()
						.getPersona().getIdPersona();
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separator = File.separator;
		direccionFisica = separator+"SICCA"+separator
				+ anho
				+ separator
				+ "OEE"+separator
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
				+ separator
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separator
				+ concurso.getIdConcurso() + separator
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + separator
				+ "POSTULANTES"+separator + valor_persona;
	}

	/**
	 * Busca los detalles de la cabecera
	 */
	@SuppressWarnings("unchecked")
	private void buscarDetalles() {
		String sql = "select documental_det.*  "
				+ "from seleccion.eval_documental_det documental_det  "
				+ "join seleccion.eval_documental_cab documental_cab  "
				+ "on documental_cab.id_eval_documental_cab = documental_det.id_eval_documental_cab  "
				+ "where documental_det.activo is true  "
				+ "and documental_cab.id_eval_documental_cab = "
				+ evalDocumentalCab.getIdEvalDocumentalCab();
		listaEvalDocumentalDet = new ArrayList<EvalDocumentalDet>();
		listaEvalDocumentalDet = em.createNativeQuery(sql,
				EvalDocumentalDet.class).getResultList();
		listaEvalDocumentalDetDTO = new ArrayList<EvalDocumentalDetDTO>();
		Integer cont = 1;
		for (EvalDocumentalDet det : listaEvalDocumentalDet) {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			dto.setAprobado(det.isAprobadoPorComision());
			dto.setEvalDocumentalDet(det);
			dto.setNro(cont);
			dto.setObligatorio(det.getMatrizDocConfigDet().getObligatorio());
			dto.setPresento(det.isAprobadoConDocumentos());
			dto.setTipoDocumento(det.getMatrizDocConfigDet()
					.getDatosEspecificos());
			dto.setDocumentos(buscarDocumentos(det.getIdEvalDocumentalDet(),
					det.getMatrizDocConfigDet().getDatosEspecificos()
							.getIdDatosEspecificos()));
			listaEvalDocumentalDetDTO.add(dto);
			cont++;
		}
	}

	/**
	 * metodo que es llamado desde la busqueda de detalles
	 * 
	 * @param idDet
	 * @param idDato
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Documentos buscarDocumentos(Long idDet, Long idDato) {
		String sql = "select doc.* "
				+ "from general.documentos doc "
				+ "join seleccion.datos_especificos datos_esp "
				+ "on datos_esp.id_datos_especificos = doc.id_datos_especificos_tipo_documento "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_documental_cab eval_cab "
				+ "on eval_cab.id_postulacion = post.id_postulacion "
				+ "join seleccion.eval_documental_det eval_det "
				+ "on eval_det.id_eval_documental_cab = eval_cab.id_eval_documental_cab "
				+ "where eval_det.id_eval_documental_det = " + idDet
				+ " and datos_esp.id_datos_especificos = " + idDato
				+ " and doc.activo is true";
		List<Documentos> lista = new ArrayList<Documentos>();
		lista = em.createNativeQuery(sql, Documentos.class).getResultList();
		if (lista.size() > 0)
			return lista.get(lista.size() - 1);
		return null;
	}

	/**
	 * metodo que selecciona todos
	 * 
	 */
	public void marcarTodos() {
		for (int i = 0; i < listaEvalDocumentalDetDTO.size(); i++) {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			dto = listaEvalDocumentalDetDTO.get(i);
			dto.setAprobado(true);
			listaEvalDocumentalDetDTO.set(i, dto);
		}
	}

	/**
	 * metodo que desmarca todo
	 */
	public void desmarcarTodos() {
		for (int i = 0; i < listaEvalDocumentalDetDTO.size(); i++) {
			EvalDocumentalDetDTO dto = new EvalDocumentalDetDTO();
			dto = listaEvalDocumentalDetDTO.get(i);
			dto.setAprobado(false);
			listaEvalDocumentalDetDTO.set(i, dto);
		}
	}

	/**
	 * buscar la comision de seleccion correspondiente al grupo
	 */
	@SuppressWarnings("unchecked")
	private void buscarComision() {
		String cad = "select com_cab.*  "
				+ "from seleccion.comision_seleccion_cab com_cab "
				+ "join seleccion.comision_grupo com_grupo  "
				+ "on com_grupo.id_comision_cab = com_cab.id_comision_sel "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = com_grupo.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<ComisionSeleccionCab> lista = new ArrayList<ComisionSeleccionCab>();
		lista = em.createNativeQuery(cad, ComisionSeleccionCab.class)
				.getResultList();
		if (lista.size() > 0) {
			comisionSeleccionCab = new ComisionSeleccionCab();
			comisionSeleccionCab = lista.get(0);
			comision = lista.get(0).getDescripcion();
		}
	}

	/**
	 * agrega registros a la columna de evaluadores
	 */
	public void agregar() {
		if (idIntegranteComision == null) {
			statusMessages.add(Severity.ERROR,
					"Escoja un integrante para la comision de evaluadores.");
			return;
		}
		if (estaMiembro()) {
			statusMessages
					.add(Severity.ERROR,
							"El integrante ya forma parte del Comite de Evaluadores. Verifique");
			return;
		}
		IntegrantesDTO dto = new IntegrantesDTO();
		if (comision != null) {
			dto.setComision(comision);
			dto.setComisionSeleccionCab(comisionSeleccionCab);
		}
		if (idIntegranteComision != null) {
			Persona persona = new Persona();
			persona = em.find(Persona.class, idIntegranteComision);
			dto.setPersonaComision(persona);
		}
		listaEvaluadoresDTO.add(dto);
		idIntegranteComision = null;
	}

	private Boolean estaMiembro() {
		for (IntegrantesDTO dto : listaEvaluadoresDTO) {
			if (dto.getPersonaComision().getIdPersona().longValue() == idIntegranteComision
					.longValue())
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void recuperarListaEvaluadores() {
		String sql = "select comis.*  "
				+ "from seleccion.eval_documental_comis comis  "
				+ "join seleccion.eval_documental_cab cab  "
				+ "on cab.id_eval_documental_cab = comis.id_eval_documental_cab "
				+ "where comis.activo is true "
				+ "and cab.id_eval_documental_cab = "
				+ evalDocumentalCab.getIdEvalDocumentalCab();
		List<EvalDocumentalComis> listaComis = new ArrayList<EvalDocumentalComis>();
		listaComis = em.createNativeQuery(sql, EvalDocumentalComis.class)
				.getResultList();

		for (EvalDocumentalComis c : listaComis) {
			IntegrantesDTO dto = new IntegrantesDTO();
			dto.setId(c.getIdEvalDocumentalComis());
			dto.setComisionSeleccionCab(c.getComisionSeleccionDet()
					.getComisionSeleccionCab());
			dto.setPersonaComision(c.getComisionSeleccionDet().getPersona());
			dto.setComision(c.getComisionSeleccionDet()
					.getComisionSeleccionCab().getDescripcion());
			listaEvaluadoresDTO.add(dto);
			listaEvaluadoresDTOAux.add(dto);
		}
	}

	/**
	 * elimina registros de la lista de evaluadores
	 * 
	 * @param row
	 */
	public void eliminar(Integer row) {
		IntegrantesDTO dto = new IntegrantesDTO();
		dto = listaEvaluadoresDTO.get(row);
		listaEvaluadoresDTO.remove(dto);
		listaEvaluadoresDTOEliminar.add(dto);
	}

	

	public void changeNameDoc() {
		nombreDoc = fileName;
	}

	private Boolean validacionDocumentos() {
		if (uploadedFile != null) {
			if (AdmDocAdjuntoFormController.validarContentType(contentType)) {
				crearUploadItem(fileName, uploadedFile.length, contentType,
						uploadedFile);
				String nomfinal1 = "";
				nomfinal1 = item.getFileName();
				String extension = nomfinal1.substring(nomfinal1
						.lastIndexOf("."));
				List<TipoArchivo> tam = em.createQuery(
						"select t from TipoArchivo t "
								+ " where lower(t.extension) like '"
								+ extension.toLowerCase() + "'")
						.getResultList();
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
			} else {
				statusMessages.add(Severity.ERROR,
						"No se permite la carga de ese tipo de archivos.");
				return false;
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

	private Boolean checkDatosObligatorios() {

		if (uploadedFile == null) {
			statusMessages.add(Severity.WARN, "Adjunte un documento");
			return false;
		}
		if (contentType == null || fileName == null || uploadedFile == null) {
			statusMessages.add(Severity.WARN, "Adjunte un documento");
			return false;
		}
		return true;
	}

	public void adjuntarDocumento() throws NamingException {
		if (!checkDatosObligatorios())
			return;
		if (!validacionDocumentos())
			return;
		documento();
	}

	public void documento() throws NamingException {
		nombrePantalla = "evaluar_adjudicados";

		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "EvalDocumentalCab");
		if (idDoc != null) {
			if (idDoc == -6) {
				statusMessages.add(Severity.WARN,
						"Ya existe el mismo archivo, Verifique");
				return;
			}

			try {
				PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
				postulacionAdjuntos.setDocumento(em.find(Documentos.class,
						idDoc));
				postulacionAdjuntos.setPostulacion(evalDocumentalCab
						.getPostulacion());
				postulacionAdjuntos.setActivo(true);
				postulacionAdjuntos.setFechaAlta(new Date());
				postulacionAdjuntos.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				em.persist(postulacionAdjuntos);
				em.flush();
				buscarDetalles();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void buscarDocumentosAlistar(int index) {
		EvalDocumentalDetDTO e = listaEvalDocumentalDetDTO.get(index);
		String sql = "select doc.* "
				+ "from general.documentos doc "
				+ "join seleccion.datos_especificos datos_esp "
				+ "on datos_esp.id_datos_especificos = doc.id_datos_especificos_tipo_documento "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_documental_cab eval_cab "
				+ "on eval_cab.id_postulacion = post.id_postulacion "
				+ "join seleccion.eval_documental_det eval_det "
				+ "on eval_det.id_eval_documental_cab = eval_cab.id_eval_documental_cab "
				+ "where eval_det.id_eval_documental_det = "
				+ e.getEvalDocumentalDet().getIdEvalDocumentalDet()
				+ " and datos_esp.id_datos_especificos = "
				+ e.getTipoDocumento().getIdDatosEspecificos()
				+ " and doc.activo is true";
		listaDocAmostrar = new ArrayList<Documentos>();
		listaDocAmostrar = em.createNativeQuery(sql, Documentos.class)
				.getResultList();

	}

	/**
	 * llama al metodo abrir del cu 289
	 * 
	 * @param index
	 */
	public void seleccionadoParaAdjuntar(int index) {
		item = null;
		nombreDoc = null;
		indice = index;
		EvalDocumentalDetDTO e = listaEvalDocumentalDetDTO.get(index);
		idTipoDoc = e.getTipoDocumento().getIdDatosEspecificos();

	}

	/**
	 * llama al metodo abrir del cu 289
	 * 
	 * @param index
	 */
	public void abrirDoc(int index) {

		EvalDocumentalDetDTO e = listaEvalDocumentalDetDTO.get(index);
		AdmDocAdjuntoFormController.abrirDocumentoFromCU(e.getDocumentos()
				.getIdDocumento(), usuarioLogueado.getIdUsuario());

	}

	
	private Boolean todoAprobados() {
		for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
			if (!detDto.getAprobado() && detDto.getObligatorio())
				return false;
		}
		return true;
	}

	private Boolean verificarPresentacionDocumentos() {
		for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {

			if (detDto.getAprobado() && detDto.getDocumentos() == null) {
				msgSave = "Debe adjuntar el documento correspondiente.";
				return false;
			}
		
		}
		return true;
	}

	public void save() {
		msgSave = null;
		if (listaEvaluadoresDTO.size() == 0) {
			msgSave = "Debe contar con al menos un evaluador.";
			return;
		}
		if (!verificarPresentacionDocumentos())
			return;
		try {
			for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
				EvalDocumentalDet documentalDet = new EvalDocumentalDet();
				documentalDet = detDto.getEvalDocumentalDet();

				if (detDto.getAprobado())
					documentalDet.setAprobadoPorComision(true);
				else
					documentalDet.setAprobadoPorComision(false);
				em.merge(documentalDet);
				em.flush();

			}
			evalDocumentalCab.setFechaEvaluacion(new Date());
			evalDocumentalCab.setAprobado(todoAprobados());
		
			if (observacion != null && !observacion.trim().isEmpty())
				evalDocumentalCab.setObservacion(observacion);
			em.merge(evalDocumentalCab);
			em.flush();
			for (IntegrantesDTO integrantesDTO : listaEvaluadoresDTO) {
				if (integrantesDTO.getId() == null) {
					if (integrantesDTO.getPersonaComision() != null) {
						List<ComisionSeleccionDet> listaSeleccionDet = new ArrayList<ComisionSeleccionDet>();
						listaSeleccionDet = buscarComisionesAInsertar(integrantesDTO
								.getPersonaComision().getIdPersona());
						if (listaSeleccionDet != null
								&& listaSeleccionDet.size() > 0) {
							for (ComisionSeleccionDet eval : listaSeleccionDet) {
								EvalDocumentalComis comis = new EvalDocumentalComis();
								comis.setActivo(true);
								comis.setComisionSeleccionDet(eval);
								comis.setEvalDocumentalCab(evalDocumentalCab);
								comis.setFechaAlta(new Date());
								comis.setUsuAlta(usuarioLogueado
										.getCodigoUsuario());
								em.persist(comis);
							}

							em.flush();
						}
					}
				}
			}

			for (IntegrantesDTO aux : listaEvaluadoresDTOEliminar) {

				EvalDocumentalComis com = new EvalDocumentalComis();
				com = em.find(EvalDocumentalComis.class, aux.getId());
				em.remove(com);
				em.flush();

			}

			if (cumple()) {
				String msg = "El Adjudicado "
						+ evalDocumentalCab.getUsuAlta()
						+ " ha aprobado la Evaluación Documental de Adjudicados";
				statusMessages.clear();
				statusMessages.add(Severity.INFO, msg);
				return;

			} else {
				String msg = "El Adjudicado "
						+ evalDocumentalCab.getUsuAlta()
						+ " no ha aprobado la Evaluación Documental de Adjudicados";
				statusMessages.clear();
				statusMessages.add(Severity.INFO, msg);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String cancelar() {
		return getUrlToPageEvaluarDocAdjudicado();
	}

	public String aceptar() {
		evalDocumentalCab.setEvaluado(true);
		evalDocumentalCab.setFechaMod(new Date());
		evalDocumentalCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
		if(evalDocumentalCab.getAprobado() == null){
			evalDocumentalCab.setAprobado(false);
			evalDocumentalCab.setFechaEvaluacion(new Date());
		}
		em.merge(evalDocumentalCab);
		em.flush();
		return getUrlToPageEvaluarDocAdjudicado();
	}

	public void cancel() {

	}

	/**
	 * Métodos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPageEvaluarDocAdjudicado() {
		return "/circuitoCSI/evaluacionMatrizDocumental/EvaluarDocAdjudicadoCU699.xhtml?fromToPage=circuitoCSI/evaluacionMatrizDocumental/EvalDocumentalCabEditCU699&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	private Boolean cumple() {
		for (EvalDocumentalDetDTO detDto : listaEvalDocumentalDetDTO) {
			if (detDto.getObligatorio()) {
				Boolean esta = false;
				if (detDto.getAprobado())
					esta = true;
				else
					return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	private List<ComisionSeleccionDet> buscarComisionesAInsertar(Long id) {
		String sql = "select com_det.* "
				+ "from seleccion.comision_seleccion_det com_det "
				+ "join seleccion.comision_seleccion_cab com_cab "
				+ "on com_cab.id_comision_sel = com_det.id_comision_sel "
				+ "join general.persona p "
				+ "on p.id_persona = com_det.id_persona "
				+ "where com_cab.id_comision_sel = "
				+ comisionSeleccionCab.getIdComisionSel()
				+ "and p.id_persona = " + id;

		return em.createNativeQuery(sql, ComisionSeleccionDet.class)
				.getResultList();
	}

	/**
	 * metodo que carga el combo correspondiente a la los integrantes de la
	 * comision
	 */
	@SuppressWarnings("unchecked")
	private void cargarComboIntegrantesComision() {
		String cad = "select p.* "
				+ "from general.persona p "
				+ "join seleccion.comision_seleccion_det com_det "
				+ "on com_det.id_persona = p.id_persona "
				+ "join seleccion.comision_seleccion_cab com_cab "
				+ "on com_cab.id_comision_sel = com_det.id_comision_sel "
				+ "join seleccion.comision_grupo com_grupo "
				+ "on com_grupo.id_comision_cab = com_cab.id_comision_sel "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = com_grupo.id_concurso_puesto_agr "
				+ "where p.activo is true "
				+ "and agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and com_cab.id_comision_sel = "
				+ comisionSeleccionCab.getIdComisionSel();

		List<Persona> lista = new ArrayList<Persona>();
		lista = em.createNativeQuery(cad, Persona.class).getResultList();
		integrantesComisionSelecItem = new ArrayList<SelectItem>();
		buildIntegrantesComisionSelectItem(lista);
	}

	private void buildIntegrantesComisionSelectItem(List<Persona> personaList) {
		if (integrantesComisionSelecItem == null)
			integrantesComisionSelecItem = new ArrayList<SelectItem>();
		else
			integrantesComisionSelecItem.clear();
		integrantesComisionSelecItem.add(new SelectItem(null,
				SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Persona p : personaList) {
			String nombres = p.getNombres() + " " + p.getApellidos();
			integrantesComisionSelecItem.add(new SelectItem(p.getIdPersona(),
					nombres));
		}
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

	public EvalDocumentalCab getEvalDocumentalCab() {
		return evalDocumentalCab;
	}

	public void setEvalDocumentalCab(EvalDocumentalCab evalDocumentalCab) {
		this.evalDocumentalCab = evalDocumentalCab;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public List<EvalDocumentalDetDTO> getListaEvalDocumentalDetDTO() {
		return listaEvalDocumentalDetDTO;
	}

	public void setListaEvalDocumentalDetDTO(
			List<EvalDocumentalDetDTO> listaEvalDocumentalDetDTO) {
		this.listaEvalDocumentalDetDTO = listaEvalDocumentalDetDTO;
	}

	public List<IntegrantesDTO> getListaEvaluadoresDTO() {
		return listaEvaluadoresDTO;
	}

	public void setListaEvaluadoresDTO(List<IntegrantesDTO> listaEvaluadoresDTO) {
		this.listaEvaluadoresDTO = listaEvaluadoresDTO;
	}

	public List<IntegrantesDTO> getListaEvaluadoresDTOAux() {
		return listaEvaluadoresDTOAux;
	}

	public void setListaEvaluadoresDTOAux(
			List<IntegrantesDTO> listaEvaluadoresDTOAux) {
		this.listaEvaluadoresDTOAux = listaEvaluadoresDTOAux;
	}

	public List<SelectItem> getIntegrantesComisionSelecItem() {
		return integrantesComisionSelecItem;
	}

	public void setIntegrantesComisionSelecItem(
			List<SelectItem> integrantesComisionSelecItem) {
		this.integrantesComisionSelecItem = integrantesComisionSelecItem;
	}

	public List<EvalDocumentalDet> getListaEvalDocumentalDet() {
		return listaEvalDocumentalDet;
	}

	public void setListaEvalDocumentalDet(
			List<EvalDocumentalDet> listaEvalDocumentalDet) {
		this.listaEvalDocumentalDet = listaEvalDocumentalDet;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}

	public void setComisionSeleccionCab(
			ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
	}

	public String getComision() {
		return comision;
	}

	public void setComision(String comision) {
		this.comision = comision;
	}

	public Long getIdIntegranteComision() {
		return idIntegranteComision;
	}

	public void setIdIntegranteComision(Long idIntegranteComision) {
		this.idIntegranteComision = idIntegranteComision;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public List<Documentos> getListaDocAmostrar() {
		return listaDocAmostrar;
	}

	public void setListaDocAmostrar(List<Documentos> listaDocAmostrar) {
		this.listaDocAmostrar = listaDocAmostrar;
	}

	public byte[] getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(byte[] uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public UploadItem getItem() {
		return item;
	}

	public void setItem(UploadItem item) {
		this.item = item;
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

	public String getNombreDoc() {
		return nombreDoc;
	}

	public void setNombreDoc(String nombreDoc) {
		this.nombreDoc = nombreDoc;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}

	public String getMsgSave() {
		return msgSave;
	}

	public void setMsgSave(String msgSave) {
		this.msgSave = msgSave;
	}

	public List<IntegrantesDTO> getListaEvaluadoresDTOEliminar() {
		return listaEvaluadoresDTOEliminar;
	}

	public void setListaEvaluadoresDTOEliminar(
			List<IntegrantesDTO> listaEvaluadoresDTOEliminar) {
		this.listaEvaluadoresDTOEliminar = listaEvaluadoresDTOEliminar;
	}

}
