package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionPart;
import py.com.excelsis.sicca.entity.EvaluacionTipo;
import py.com.excelsis.sicca.entity.ListaDet;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoArchivo;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.AdminReclamoSugPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("evaluacionParticipantesFC")
public class EvaluacionParticipantesFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6686443756878620066L;
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
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private List<SelectItem> mostrarSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> siNoSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> tipoDocSelecItem = new ArrayList<SelectItem>();
	private List<EvaluacionPart> listaEvalParticipantes = new ArrayList<EvaluacionPart>();
	private List<ListaDet> listaDet = new ArrayList<ListaDet>();

	private Capacitaciones capacitacion = new Capacitaciones();
	private Long idCapacitacion;
	private Long idPais;
	private Long idTipoDoc;
	private Long idDoc;
	private Long totalAsistencia;

	private Integer valorMostrar;
	private Integer index;

	private String ci;
	private String from;
	private String tipoCapacitacion;

	private Boolean esPrimeraVez = false;
	private Boolean cumpleAsist = false;
	private Boolean tieneDoc = false;

	/* Variables para adjuncion de documentos */
	private byte[] uploadedFile;
	private UploadItem item;
	private String contentType;
	private String fileName;
	private String nombreDoc;
	private String nombrePantalla;

	public String init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return null;
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);
			Long idDato = capacitacion.getDatosEspecificosTipoCap()
					.getIdDatosEspecificos();
			tipoCapacitacion = em.find(DatosEspecificos.class, idDato)
					.getDescripcion();
			seguridadUtilFormController
					.validarCapacitacionPerteneceUO(idCapacitacion);
			cargarDatosNivel();

			verificarPrimeraVez();
			if (esPrimeraVez) {
				// VERIFICAR QUE EXISTAN REGISTROS EN LISTA_DET SINO MOSTRAR
				// PANTALLA DE ERROR CON MENSAJE PERSONALIZADO
				if (validarParticipantes(searchParticipantesActivos()) != null
						&& validarParticipantes(searchParticipantesActivos())
								.equals("fallo"))
					return "fallo";

				searchParticipantesActivos();
				registrarTablaPrimeraEntrada();
			} else {
				EvaluacionTipo evalTipo = new EvaluacionTipo();
				evalTipo = buscarEvaluacionTipo();
				totalAsistencia = evalTipo.getCantidad();
				for (ListaDet l : listaDet) {
					if (!estaEvaluacionPart(l.getPostulacionCap()
							.getIdPostulacion())) {
						EvaluacionPart evaluacionPart = new EvaluacionPart();
						evaluacionPart.setActivo(true);
						evaluacionPart.setEvaluacionTipo(evalTipo);
						evaluacionPart.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
						evaluacionPart.setFechaAlta(new Date());
						evaluacionPart.setPostulacionCap(l.getPostulacionCap());
						em.persist(evaluacionPart);
					}
				}
				em.flush();
			}

			cargarListaPorDefecto();
			cargarSiNO();

		}
		cargarComboMostrar();
		return "ok";
	}

	private Boolean estaEvaluacionPart(Long id) {
		String sql = "SELECT PART.* FROM CAPACITACION.EVALUACION_PART PART "
				+ "WHERE PART.ACTIVO IS TRUE " + "AND PART.ID_POSTULACION = "
				+ id;
		List<EvaluacionPart> l = em
				.createNativeQuery(sql, EvaluacionPart.class).getResultList();
		if (l.isEmpty())
			return false;
		return true;
	}

	public String desvincular(Integer inx) {
		EvaluacionPart evl = new EvaluacionPart();
		evl = listaEvalParticipantes.get(inx);
		String sql = "SELECT D.* FROM CAPACITACION.LISTA_DET D "
				+ "WHERE D.ID_POSTULACION = "
				+ evl.getPostulacionCap().getIdPostulacion();

		List<ListaDet> l = em.createNativeQuery(sql, ListaDet.class)
				.getResultList();
		return "/capacitacion/desvinculacionCapacitacion/AdmDesvinculacionCapacitacion.xhtml?from=capacitacion/evaluacionParticipantes/EvaluacionParticipantes&listaDetIdlistaDet="
				+ l.get(0).getIdListaDet() + "&cu=CU490";
	}

	public void imprimir() {
		reportUtilFormController = (ReportUtilFormController) Component
				.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();
		reportUtilFormController
				.setNombreReporte("RPT_CU531_imprimir_eval_participantes");
		cargarParametros();
		reportUtilFormController.imprimirReportePdf();
	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab configuracionUoCab = em.find(
					ConfiguracionUoCab.class, usuarioLogueado
							.getConfiguracionUoCab().getIdConfiguracionUo());
			String elWhere = " ";
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil
					.descNivelEntidadOee();
			reportUtilFormController.getParametros().put("nivel",
					diccDscNEO.get("NIVEL"));
			reportUtilFormController.getParametros().put("entidad",
					diccDscNEO.get("ENTIDAD"));
			reportUtilFormController.getParametros().put("oee",
					diccDscNEO.get("OEE"));
			reportUtilFormController.getParametros().put("unidadOrga",
					diccDscNEO.get("UND_ORG"));
			reportUtilFormController.getParametros().put("nombreCapa",
					capacitacion.getNombre());
			reportUtilFormController.getParametros().put("tipoCapa",
					capacitacion.getDatosEspecificosTipoCap().getDescripcion());
			reportUtilFormController.getParametros().put("oeeUsuarioLogueado",
					configuracionUoCab.getDenominacionUnidad());

			elWhere = "  AND C.ID_CAPACITACION = " + idCapacitacion;
			if (idPais == null)
				reportUtilFormController.getParametros().put("pais", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(
						idPais.toString(), TiposDatos.INTEGER.getValor(), null)) {
					elWhere += "  AND P.id_pais_expedicion_doc = "
							+ (new Date()).getTime();
				} else {
					reportUtilFormController.getParametros().put("pais",
							em.find(Pais.class, idPais).getDescripcion());
					elWhere += "  AND P.id_pais_expedicion_doc = " + idPais;
				}
			}
			if (ci == null || ci.trim().isEmpty())
				reportUtilFormController.getParametros().put("cedula", "TODOS");
			else {
				if (!seguridadUtilFormController.validarInput(ci,
						TiposDatos.STRING.getValor(), null)) {
					reportUtilFormController.getParametros().put("cedula", ci);
					elWhere += "  AND P.documento_identidad = '"
							+ (new Date()).getTime() + "'";
				} else {
					reportUtilFormController.getParametros().put("cedula", ci);
					elWhere += "  AND P.documento_identidad = '"
							+ seguridadUtilFormController.caracterInvalido(ci)
							+ "'";
				}
			}
			if (valorMostrar == null) {
				reportUtilFormController.getParametros().put("mostrar",
						"PARTICIPANTES");
				elWhere += " AND EPART.ACTIVO IS TRUE";
			} else {
				if (!seguridadUtilFormController.validarInput(
						valorMostrar.toString(), TiposDatos.INTEGER.getValor(),
						null)) {
					elWhere += "  AND EPART.ACTIVO = null ";
				} else {
					String mostrarParam = "";
					if (valorMostrar == 1) {
						elWhere += " AND EPART.ACTIVO IS FALSE";
						mostrarParam = "DESVINCULADOS";
					} else {
						mostrarParam = "TODOS";
					}
					reportUtilFormController.getParametros().put("mostrar",
							mostrarParam);
				}
			}
			reportUtilFormController.getParametros().put("usuAlta",
					usuarioLogueado.getCodigoUsuario());
			reportUtilFormController.getParametros().put("elWhere", elWhere);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initEdit() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);

			cargarDatosNivel();

			EvaluacionTipo evalTipo = new EvaluacionTipo();
			evalTipo = buscarEvaluacionTipo();
			if (evalTipo != null && evalTipo.getCantidad() != null)
				totalAsistencia = evalTipo.getCantidad();

			cargarListaPorDefecto();
			cargarSiNO();

		}
		cargarComboMostrar();

	}

	public String validarParticipantes(Integer valor) {

		if (valor == 0) {
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("CU490_novalida"));
			return "fallo";
		}
		return null;

	}

	private void registrarTablaPrimeraEntrada() {
		try {
			EvaluacionTipo evaluacionTipo = new EvaluacionTipo();
			evaluacionTipo.setActivo(true);
			evaluacionTipo.setCapacitaciones(capacitacion);
			evaluacionTipo.setFechaAlta(new Date());
			evaluacionTipo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setFechaInicio(new Date());
			evaluacionTipo.setUsuInicio(usuarioLogueado.getCodigoUsuario());
			evaluacionTipo.setTipo("EVAL_PART");
			em.persist(evaluacionTipo);

			for (ListaDet l : listaDet) {
				EvaluacionPart evaluacionPart = new EvaluacionPart();
				evaluacionPart.setActivo(true);
				evaluacionPart.setEvaluacionTipo(evaluacionTipo);
				evaluacionPart.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				evaluacionPart.setFechaAlta(new Date());
				evaluacionPart.setPostulacionCap(l.getPostulacionCap());
				em.persist(evaluacionPart);
			}
			em.flush();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Integer searchParticipantesActivos() {
		String sql = "SELECT DET.* " + "FROM CAPACITACION.LISTA_DET DET "
				+ "JOIN CAPACITACION.LISTA_CAB CAB "
				+ "ON CAB.ID_LISTA = DET.ID_LISTA "
				+ "WHERE CAB.ID_CAPACITACION = " + idCapacitacion
				+ " AND CAB.ACTIVO IS TRUE AND DET.ACTIVO IS TRUE AND DET.TIPO = 'P'";

		listaDet = em.createNativeQuery(sql, ListaDet.class).getResultList();
		if (listaDet.isEmpty()) {
			return new Integer(0);
		}
		return new Integer(listaDet.size());
	}

	private void cargarComboMostrar() {
		mostrarSelecItem = new ArrayList<SelectItem>();
		mostrarSelecItem.add(new SelectItem(null, "PARTICIPANTES"));
		mostrarSelecItem.add(new SelectItem(1, "DESVINCULADOS"));
		mostrarSelecItem.add(new SelectItem(2, "TODOS"));
	}

	private void cargarSiNO() {
		siNoSelecItem = new ArrayList<SelectItem>();
		siNoSelecItem.add(new SelectItem(true, "Si"));
		siNoSelecItem.add(new SelectItem(false, "No"));
	}

	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitacion.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	public void cargarCabecera() {

		Long idOee = capacitacion.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class, idOee);
		Long idEnt = cab.getEntidad().getIdEntidad();
		Entidad ent = em.find(Entidad.class, idEnt);
		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(ent
				.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(ent.getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(idOee);

		nivelEntidadOeeUtil.init();

	}

	private void cargarListaPorDefecto() {
		String sql = obtenerSql() + " AND EPART.ACTIVO IS TRUE";
		listaEvalParticipantes = new ArrayList<EvaluacionPart>();
		listaEvalParticipantes = em
				.createNativeQuery(sql, EvaluacionPart.class).getResultList();
		for (int i = 0; i < listaEvalParticipantes.size(); i++) {
			EvaluacionPart eval = new EvaluacionPart();
			eval = listaEvalParticipantes.get(i);
			if (eval.getSatisfactoriamente() != null) {
				if (eval.getSatisfactoriamente())
					eval.setSino("SI");
				if (!eval.getSatisfactoriamente())
					eval.setSino("NO");
				listaEvalParticipantes.set(i, eval);
			}

		}
	}

	private String obtenerSql() {
		String sql = "SELECT EPART.* "
				+ "FROM CAPACITACION.EVALUACION_PART EPART "
				+ "JOIN CAPACITACION.EVALUACION_TIPO TIPO "
				+ "ON TIPO.ID_EVAL = EPART.ID_EVAL "
				+ "JOIN CAPACITACION.POSTULACION_CAP POST "
				+ "ON POST.ID_POSTULACION = EPART.ID_POSTULACION "
				+ "JOIN GENERAL.PERSONA P "
				+ "ON P.ID_PERSONA = POST.ID_PERSONA "
				+ "JOIN GENERAL.PAIS PAIS "
				+ "ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC "
				+ "LEFT JOIN GENERAL.DOCUMENTOS DOC "
				+ "ON DOC.ID_DOCUMENTO = EPART.ID_DOCUMENTO "
				+ "WHERE TIPO.ID_CAPACITACION = " + idCapacitacion
				+ " AND TIPO.TIPO = 'EVAL_PART' ";
		return sql;

	}

	public void changeSiNo(Integer row) {
		verificarAsistencia();
		if (cumpleAsist) {
			EvaluacionPart eval = new EvaluacionPart();
			eval = listaEvalParticipantes.get(row);
			if (eval.getSino().equals("SI"))
				eval.setSatisfactoriamente(true);
			if (eval.getSino().equals("NO"))
				eval.setSatisfactoriamente(false);
			listaEvalParticipantes.set(row, eval);
		}
	}

	public void search() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		String sql = obtenerSql();
		if (idPais != null) {
			if (!sufc.validarInput(idPais.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			sql += " AND PAIS.ID_PAIS = " + idPais;
		}
		if (ci != null && !ci.trim().isEmpty()) {
			if (!sufc.validarInput(ci, TiposDatos.STRING.getValor(), null))
				return;
			sql += " AND P.documento_identidad = '"
					+ seguridadUtilFormController.caracterInvalido(ci) + "' ";
		}
		if (valorMostrar == null)
			sql += " AND EPART.ACTIVO IS TRUE";
		if (valorMostrar != null && valorMostrar != 2)

			sql += " AND EPART.ACTIVO IS FALSE";

		listaEvalParticipantes = new ArrayList<EvaluacionPart>();
		listaEvalParticipantes = em
				.createNativeQuery(sql, EvaluacionPart.class).getResultList();
		for (int i = 0; i < listaEvalParticipantes.size(); i++) {
			EvaluacionPart eval = new EvaluacionPart();
			eval = listaEvalParticipantes.get(i);
			if (eval.getSatisfactoriamente() != null) {
				if (eval.getSatisfactoriamente())
					eval.setSino("SI");
				if (!eval.getSatisfactoriamente())
					eval.setSino("NO");
				listaEvalParticipantes.set(i, eval);
			}

		}

	}

	public void searchAll() {
		limpiar();
		cargarListaPorDefecto();
	}

	private void limpiar() {
		ci = null;
		idPais = null;
		valorMostrar = null;
		cargarComboMostrar();
	}

	private void verificarPrimeraVez() {
		String sql = "SELECT T.* " + "FROM CAPACITACION.EVALUACION_TIPO T "
				+ "WHERE T.ACTIVO IS TRUE " + "AND T.TIPO = 'EVAL_PART' "
				+ "AND T.ID_CAPACITACION = " + idCapacitacion;
		List<EvaluacionTipo> lista = em.createNativeQuery(sql,
				EvaluacionTipo.class).getResultList();
		if (lista.isEmpty())
			esPrimeraVez = true;
		else
			esPrimeraVez = false;
	}

	public void verificarAsistencia() {

		if (totalAsistencia == null) {
			cumpleAsist = false;
			statusMessages
					.add(Severity.WARN,
							"Debe primeramente ingresar la Cantidad Total de Asistencia");
		} else
			cumpleAsist = true;
		return;
	}

	public void cumpleAsistencia(Integer row) {
		verificarAsistencia();
		if (cumpleAsist) {
			statusMessages.clear();
			EvaluacionPart evaluacionActual = new EvaluacionPart();
			evaluacionActual = listaEvalParticipantes.get(row);
			if (evaluacionActual.getAsistencia() != null
					&& (evaluacionActual.getAsistencia().intValue() > totalAsistencia
							.intValue())) {
				statusMessages
						.add(Severity.WARN,
								"El campo Asistencia no debe ser mayor a la Cantidad Total de Asistencia");
			}
		}
	}

	public void cargarTipoDoc(Integer idx) {
		index = idx;
		String sql = "SELECT DATOS_ESPECIFICOS.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DATOS_ESPECIFICOS "
				+ "JOIN SELECCION.DATOS_GENERALES DATOS_GENERALES ON DATOS_GENERALES.ID_DATOS_GENERALES = DATOS_ESPECIFICOS.ID_DATOS_GENERALES "
				+ "WHERE DATOS_GENERALES.DESCRIPCION = 'TIPOS DE DOCUMENTOS' "
				+ "AND DATOS_ESPECIFICOS.VALOR_ALF in ('F1_CAP', 'F2_CAP', 'F3_CAP') "
				+ "AND DATOS_ESPECIFICOS.ACTIVO = TRUE ORDER BY DATOS_ESPECIFICOS.DESCRIPCION";
		List<DatosEspecificos> listaEspec = new ArrayList<DatosEspecificos>();
		listaEspec = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();

		tipoDocSelecItem = new ArrayList<SelectItem>();

		tipoDocSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (DatosEspecificos d : listaEspec) {
			tipoDocSelecItem.add(new SelectItem(d.getIdDatosEspecificos(), d
					.getDescripcion()));
		}
		EvaluacionPart part = new EvaluacionPart();
		part = listaEvalParticipantes.get(index);
		if (part.getDocumento() != null)
			tieneDoc = true;
		else
			tieneDoc = false;

	}

	public void changeNameDoc1() {
		nombreDoc = null;
	}

	/**
	 * Valida el documento a ser adjuntado
	 * 
	 * @return
	 */
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

	private void documento() throws NamingException {
		EvaluacionPart part = new EvaluacionPart();
		part = listaEvalParticipantes.get(index);
		Persona persona = new Persona();
		persona = em.find(Persona.class, part.getPostulacionCap().getPersona()
				.getIdPersona());
		nombrePantalla = "evaluarParticipantes";
		String direccionFisica = "";
		String separador = File.separator;

		direccionFisica = separador + "SICCA" + separador + "USUARIO_PORTAL"
				+ persona.getDocumentoIdentidad() + "_"
				+ persona.getIdPersona();

		idDoc = AdmDocAdjuntoFormController.guardarDirecto(item,
				direccionFisica, nombrePantalla, idTipoDoc,
				usuarioLogueado.getIdUsuario(), "postulacionCap");
		part.setDocumento(em.find(Documentos.class, idDoc));
		try {
			em.merge(part);
			em.flush();
			listaEvalParticipantes.set(index, part);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void guardarAdjunto() throws NamingException {
		if (validacionDocumentos()) {
			if (tieneDoc) {
				EvaluacionPart part = new EvaluacionPart();
				part = listaEvalParticipantes.get(index);
				Documentos doc = new Documentos();
				doc = part.getDocumento();
				doc.setActivo(false);
				em.merge(doc);
				part.setDocumento(null);
				em.merge(part);
			}
			documento();
		}
	}

	public void abrirDocumento(Integer indx) {
		EvaluacionPart part = new EvaluacionPart();
		part = listaEvalParticipantes.get(indx);
		try {
			AdmDocAdjuntoFormController.abrirDocumentoFromCU(part
					.getDocumento().getIdDocumento(), usuarioLogueado
					.getIdUsuario());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deshacerDesvinculacion(Integer idx) {
		EvaluacionPart part = new EvaluacionPart();
		part = listaEvalParticipantes.get(idx);
		PostulacionCap postCap = new PostulacionCap();

		try {
			postCap = em.find(PostulacionCap.class, part.getPostulacionCap()
					.getIdPostulacion());
			postCap.setActivo(true);
			postCap.setUsuCancelacion(null);
			postCap.setFechaCancelacion(null);
			postCap.setObsCancelacion(null);
			em.merge(postCap);
			part.setActivo(true);
			part.setUsuMod(usuarioLogueado.getCodigoUsuario());
			part.setFechaMod(new Date());
			em.merge(part);
			for (ListaDet det : obtenerListaDet(postCap.getIdPostulacion(),
					postCap.getCapacitacion().getIdCapacitacion())) {
				Documentos doc = new Documentos();
				doc = det.getDocumento();
				det.setTipo("P");
				det.setUsuMod(usuarioLogueado.getCodigoUsuario());
				det.setFechaMod(new Date());
				det.setDatosEspecificosDesv(null);
				det.setObservacion(null);
				det.setFechaDesv(null);
				det.setUsuDesv(null);
				det.setFechaDesv(null);
				det.setDocumento(null);
				doc.setActivo(false);
				em.merge(doc);
				em.merge(det);
			}
			listaEvalParticipantes.set(idx, part);
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private List<ListaDet> obtenerListaDet(Long idPost, Long idCap) {
		String sql = "SELECT DET.* " + "FROM CAPACITACION.LISTA_DET DET "
				+ "JOIN CAPACITACION.LISTA_CAB CAB "
				+ "ON CAB.ID_LISTA = DET.ID_LISTA "
				+ "WHERE CAB.ID_CAPACITACION = " + idCap
				+ "AND DET.ID_POSTULACION = " + idPost;
		return em.createNativeQuery(sql, ListaDet.class).getResultList();
	}

	public void guardar() {
		statusMessages.clear();
		if (!check())
			return;
		EvaluacionTipo evalTipo = new EvaluacionTipo();
		try {
			evalTipo = buscarEvaluacionTipo();
			evalTipo.setCantidad(totalAsistencia);
			em.merge(evalTipo);
			for (EvaluacionPart p : listaEvalParticipantes) {
				if (p.getAsistencia() != null) {
					p.setFinalizo(true);
					if (p.getAsistencia() != null
							&& p.getCalificacion() != null)
						p.setCertificado("A");
					if (p.getAsistencia() != null
							&& p.getCalificacion() == null)
						p.setCertificado("P");
					p.setUsuMod(usuarioLogueado.getCodigoUsuario());
					p.setFechaMod(new Date());
					em.merge(p);
				}

			}
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

	}

	private EvaluacionTipo buscarEvaluacionTipo() {
		String sql = "SELECT EV_TIPO.* "
				+ "FROM CAPACITACION.EVALUACION_TIPO EV_TIPO "
				+ "WHERE EV_TIPO.TIPO = 'EVAL_PART' "
				+ "AND EV_TIPO.ID_CAPACITACION = " + idCapacitacion;
		try {
			List<EvaluacionTipo> listaTip = em.createNativeQuery(sql,
					EvaluacionTipo.class).getResultList();
			if (listaTip.isEmpty())
				return null;
			return listaTip.get(0);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	private Boolean check() {
		if (totalAsistencia == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar la Cantidad Total de Asistencia para realizar esta acción");
			return false;
		}
		for (EvaluacionPart evP : listaEvalParticipantes) {
			if (evP.getAsistencia() != null) {
				Long asist = evP.getAsistencia();
				if (asist.intValue() > totalAsistencia.intValue()) {
					statusMessages
							.add(Severity.ERROR,
									"Debe ingresar la Cantidad Total de Asistencia para realizar esta acción");
					return false;
				}
			}
			if (evP.getAsistencia() != null && evP.getSino() == null) {
				statusMessages.add(Severity.ERROR,
						"El campo Satisfactoriamente es obligatorio");
			}
			if (evP.getAsistencia() == null && evP.getSino() != null) {
				statusMessages.add(Severity.ERROR,
						"El campo Asistencia es obligatorio");
			}
		}
		return true;
	}

	public String finalizar() {
		statusMessages.clear();
		if (!check())
			return null;
		Referencias referencias = referenciasUtilFormController.getReferencia(
				"ESTADOS_CAPACITACION", "EVALUACION PARTICIPANTES");

		try {
			capacitacion.setEstado(referencias.getValorNum());
			em.merge(capacitacion);
			EvaluacionTipo evalTipo = new EvaluacionTipo();
			evalTipo = buscarEvaluacionTipo();
			evalTipo.setFechaCierre(new Date());
			evalTipo.setUsuCierrre(usuarioLogueado.getCodigoUsuario());
			em.merge(evalTipo);
			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "ok";
		} catch (Exception e) {
			return null;
		}
	}

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public Long getTotalAsistencia() {
		return totalAsistencia;
	}

	public void setTotalAsistencia(Long totalAsistencia) {
		this.totalAsistencia = totalAsistencia;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public List<SelectItem> getMostrarSelecItem() {
		return mostrarSelecItem;
	}

	public void setMostrarSelecItem(List<SelectItem> mostrarSelecItem) {
		this.mostrarSelecItem = mostrarSelecItem;
	}

	public Integer getValorMostrar() {
		return valorMostrar;
	}

	public void setValorMostrar(Integer valorMostrar) {
		this.valorMostrar = valorMostrar;
	}

	public List<EvaluacionPart> getListaEvalParticipantes() {
		return listaEvalParticipantes;
	}

	public void setListaEvalParticipantes(
			List<EvaluacionPart> listaEvalParticipantes) {
		this.listaEvalParticipantes = listaEvalParticipantes;
	}

	public List<SelectItem> getSiNoSelecItem() {
		return siNoSelecItem;
	}

	public void setSiNoSelecItem(List<SelectItem> siNoSelecItem) {
		this.siNoSelecItem = siNoSelecItem;
	}

	public Boolean getEsPrimeraVez() {
		return esPrimeraVez;
	}

	public void setEsPrimeraVez(Boolean esPrimeraVez) {
		this.esPrimeraVez = esPrimeraVez;
	}

	public List<ListaDet> getListaDet() {
		return listaDet;
	}

	public void setListaDet(List<ListaDet> listaDet) {
		this.listaDet = listaDet;
	}

	public Boolean getCumpleAsist() {
		return cumpleAsist;
	}

	public void setCumpleAsist(Boolean cumpleAsist) {
		this.cumpleAsist = cumpleAsist;
	}

	public List<SelectItem> getTipoDocSelecItem() {
		return tipoDocSelecItem;
	}

	public void setTipoDocSelecItem(List<SelectItem> tipoDocSelecItem) {
		this.tipoDocSelecItem = tipoDocSelecItem;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
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

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Long getIdDoc() {
		return idDoc;
	}

	public void setIdDoc(Long idDoc) {
		this.idDoc = idDoc;
	}

	public Boolean getTieneDoc() {
		return tieneDoc;
	}

	public void setTieneDoc(Boolean tieneDoc) {
		this.tieneDoc = tieneDoc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTipoCapacitacion() {
		return tipoCapacitacion;
	}

	public void setTipoCapacitacion(String tipoCapacitacion) {
		this.tipoCapacitacion = tipoCapacitacion;
	}

}
