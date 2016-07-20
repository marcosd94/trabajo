package py.com.excelsis.sicca.excseleccion.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.ActividadEnum;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.HistoricoEvaluacion;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabHome;
import py.com.excelsis.sicca.seleccion.session.form.EvaluarDocPostulantesFormController;
import py.com.excelsis.sicca.seleccion.session.form.Tab7VistaPrePostulacionActualFC;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("evalDocAdjudicadoExcFC")
public class EvalDocAdjudicadoExcFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(create = true)
	EvalDocumentalCabHome evalDocumentalCabHome;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	Tab7VistaPrePostulacionActualFC tab7VistaPrePostulacionActualFC;
	private EvaluarDocPostulantesFormController evaluarDocPostulantesFormController;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso = new Concurso();
	private Excepcion excepcion = new Excepcion();
	private ConcursoPuestoAgrExc concursoPuestoAgrExc = new ConcursoPuestoAgrExc();
	private List<EvalReferencialPostulante> listaReferencialPostulantes;
	private List<EvalDocumentalCab> listaAdjudicados;
	private List<MatrizDocConfigDet> listaDocAdjudicados;
	private List<EvalDocumentalDet> listaEvalDocDet;

	private Long idConcursoPuestoAgr;
	private Long idExcepcion;
	private Integer cantEvaluados;
	private Integer cantVacancia;
	private Boolean esPrimeraVez;
	private Boolean habControl;
	private Boolean todosEvaluados;
	private String codConcurso;
	private String direccionFisica;
	private String observacion;
	private String volvio;
	private String texto;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		habControl = false;
		evaluarDocPostulantesFormController =
			(EvaluarDocPostulantesFormController) Component.getInstance(EvaluarDocPostulantesFormController.class, true);
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		excepcion = em.find(Excepcion.class, idExcepcion);
		esPrimeraVez();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		if (concurso != null) {
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE " + configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}
		concursoPuestoAgrExc = obtenerAgrExc();
		if (cantPostulantes() == 0) {
			habControl = true;
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU593_msg_cancelar"));
			return;
		}

		if (esPrimeraVez) {
			buscarEvaluacionPostulantes();
			buscarDocsAdjudicados();
			insertarEvaluacionDocumental();
			actualizarDocumentalDetalle();
			cantEvaluados = 0;
			statusMessages.clear();
		} else
			cantEvaluados = buscarCantidadEvaluados();
		cantVacancia = buscarCantidadVacancia();
		obtenerDirFisica();
		buscarListaAdjudicados();
		verificarTodosEvaluados();
	}

	private void obtenerDirFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		direccionFisica =
			separador + "SICCA" + separador + anho + separador + "OEE" + separador
				+ concurso.getDatosEspecificosTipoConc().getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador + concursoPuestoAgr.getIdConcursoPuestoAgr();

	}

	@SuppressWarnings("unchecked")
	private void buscarListaAdjudicados() {
		String sql =
			"select cab.* " + "from seleccion.eval_documental_cab cab "
				+ "where cab.activo is true and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and cab.id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cab.id_excepcion = " + excepcion.getIdExcepcion();
		listaAdjudicados = new ArrayList<EvalDocumentalCab>();
		listaAdjudicados = em.createNativeQuery(sql, EvalDocumentalCab.class).getResultList();
	}

	/**
	 * Busca la lista de Documentos Adjudicados teniendo en cuenta ciertos criterios
	 */
	@SuppressWarnings("unchecked")
	private void buscarDocsAdjudicados() {
		String sql =
			"select matriz_det.* " + "from seleccion.matriz_doc_config_enc matriz_enc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = matriz_enc.id_concurso_puesto_agr "
				+ "join seleccion.matriz_doc_config_det matriz_det "
				+ "on matriz_det.id_matriz_doc_config_enc = matriz_enc.id_matriz_doc_config_enc "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and matriz_det.activo is true "
				+ "and matriz_det.adjudicacion is true " + "order by matriz_det.nro_orden";
		listaDocAdjudicados = new ArrayList<MatrizDocConfigDet>();
		listaDocAdjudicados = em.createNativeQuery(sql, MatrizDocConfigDet.class).getResultList();
	}

	/**
	 * Verifica que sea la primera vez que ingresa en usuario a esta pantalla
	 */
	@SuppressWarnings("unchecked")
	private void esPrimeraVez() {
		String sql =
			"select cab.*  " + "from seleccion.eval_documental_cab cab "
				+ "where cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and cab.id_excepcion = "
				+ excepcion.getIdExcepcion();
		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(sql, EvalDocumentalCab.class).getResultList();
		if (lista.size() > 0)
			esPrimeraVez = false;
		else
			esPrimeraVez = true;
	}

	private Integer cantPostulantes() {
		Query q =
			em.createQuery("select eval from EvalReferencialPostulante eval "
				+ " where eval.listaCorta is true and eval.seleccionado is true "
				+ "and eval.postulacion.activo is true and eval.postulacion.usuCancelacion is null "
				+ "and eval.incluido is true " + "and eval.excluido is false "
				+ "and eval.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and eval.excepcion.idExcepcion = :idExc");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("idExc", excepcion.getIdExcepcion());
		List<EvalReferencialPostulante> lista = q.getResultList();
		if (lista.isEmpty())
			return new Integer(0);
		return lista.size();
	}

	/**
	 * Busca la cantidad de vacancias para el puesto
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadVacancia() {
		Integer resultado = null;
		String sql =
			"select count(puesto_det.*) " + "from seleccion.concurso_puesto_det puesto_det "
				+ "where puesto_det.activo is true " + "and puesto_det.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and puesto_det.id_excepcion = "
				+ excepcion.getIdExcepcion();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	/**
	 * Busca la lista de postulantes a ser evaluados de acuerdo al grupo recibido desde la bandeja
	 */
	@SuppressWarnings("unchecked")
	private void buscarEvaluacionPostulantes() {
		Query q =
			em.createQuery("select eval from EvalReferencialPostulante eval "
				+ " where eval.listaCorta is true and eval.seleccionado is true "
				+ "and eval.incluido is true "
				+ "and eval.activo is true and (eval.excluido is null or eval.excluido is false) "
				+ "and eval.postulacion.activo is true "
				+ "and eval.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and eval.excepcion.idExcepcion = :idExc");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("idExc", excepcion.getIdExcepcion());

		listaReferencialPostulantes = new ArrayList<EvalReferencialPostulante>();
		listaReferencialPostulantes = q.getResultList();
	}

	/**
	 * Inserta datos tanto en la tabla eval_documental_cab como en la tabla eval_documental_det
	 */
	private void insertarEvaluacionDocumental() {
		try {
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				EvalDocumentalCab evalDocumentalCab = new EvalDocumentalCab();
				evalDocumentalCab.setActivo(true);
				evalDocumentalCab.setTipo("EVALUACION ADJUDICADOS");
				evalDocumentalCab.setPostulacion(eval.getPostulacion());
				evalDocumentalCab.setConcursoPuestoAgr(concursoPuestoAgr);
				evalDocumentalCab.setIncluido(true);
				evalDocumentalCab.setExcepcion(excepcion);
				evalDocumentalCabHome.setInstance(evalDocumentalCab);
				String resultado = evalDocumentalCabHome.persist();
				if (resultado.equals("persisted")) {
					listaEvalDocDet = new ArrayList<EvalDocumentalDet>();
					for (MatrizDocConfigDet matrizDet : listaDocAdjudicados) {
						EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();
						evalDocumentalDet.setActivo(true);
						evalDocumentalDet.setEvalDocumentalCab(evalDocumentalCabHome.getInstance());
						evalDocumentalDet.setMatrizDocConfigDet(matrizDet);
						evalDocumentalDet.setFechaAlta(new Date());
						evalDocumentalDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
						em.persist(evalDocumentalDet);
						listaEvalDocDet.add(evalDocumentalDet);
					}
					em.flush();
					evalDocumentalCabHome.clearInstance();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Actualiza los detalles en caso de que el documento haya sido presentado
	 */
	private void actualizarDocumentalDetalle() {
		try {
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				List<Documentos> listaDocs = new ArrayList<Documentos>();
				listaDocs = buscarDocumentos(eval.getIdEvalReferencialPostulante());
				for (EvalDocumentalDet det : listaEvalDocDet) {
					Boolean aprobado = false;
					if (listaDocs != null) {
						aprobado = verificarExistenciaTipoDoc(listaDocs, det);
					}
					det.setAprobadoConDocumentos(aprobado);
					det.setFechaMod(new Date());
					det.setUsuMod(usuarioLogueado.getCodigoUsuario());
					em.merge(det);
				}
				em.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Busca el nivel correspondiente a la entidad
	 * 
	 * @param nenCodigo
	 * @return
	 */
	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	@SuppressWarnings("unchecked")
	private List<Documentos> buscarDocumentos(Long id) {
		String sql =
			"select doc.* " + "from general.documentos doc "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post  "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_referencial_postulante eval_ref "
				+ "on eval_ref.id_postulacion = post.id_postulacion " + "where doc.activo is true "
				+ "and eval_ref.id_eval_referencial_postulante = " + id;
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}

	/**
	 * Verifica existencia de un tipo de documento
	 * 
	 * @param documentos
	 * @param d
	 * @return
	 */
	private Boolean verificarExistenciaTipoDoc(List<Documentos> documentos, EvalDocumentalDet d) {
		for (Documentos doc : documentos) {
			if (d.getMatrizDocConfigDet().getDatosEspecificos().getIdDatosEspecificos().equals(doc.getDatosEspecificos().getIdDatosEspecificos()))
				return true;
		}
		return false;
	}

	/**
	 * Busca la cantidad de evaluados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadEvaluados() {
		Integer resultado = null;
		String sql =
			"select count(cab.*) " + "from seleccion.eval_documental_cab cab "
				+ "where cab.activo is true "
				+ "and cab.evaluado is true and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and cab.id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cab.id_excepcion = " + excepcion.getIdExcepcion();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	@SuppressWarnings("unchecked")
	private void verificarTodosEvaluados() {
		String sql =
			"select count(cab.*) " + "from seleccion.eval_documental_cab cab "
				+ "where cab.activo is true " + "and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and cab.id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cab.id_excepcion = " + excepcion.getIdExcepcion()
				+ " and cab.evaluado is null";
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null)
			todosEvaluados = true;
		else {
			Integer nro = new Integer(config.toString());
			if (nro == 0)
				todosEvaluados = true;
			else
				todosEvaluados = false;
		}
	}

	public String nextTask() {
		habControl = false;
		try {
			if (!validacion1()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Debe evaluar a todos los postulantes y cerrar todas las evaluaciones para pasar a la siguiente actividad");
				return null;
			}
			if (!validacion2()) {
				habControl = true;
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "La cantidad de postulantes aprobados no es igual a cantidad de puestos. Verificar en control puesto/postulante");
				return null;
			}
			actualizarPostulantes();
			actualizarEstadoGrupoExcepcion();
			excepcion.setEstado("EVAL. ADJUDICADOS");
			excepcion.setFechaMod(new Date());
			excepcion.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(excepcion);
			actualizarEstadosGrupo();
			insertarHistorial();

			for (EvalDocumentalCab adjudicado : listaAdjudicados) {
				if (adjudicado.getPostulacion().getUsuCancelacion() == null)
					enviarEmails(adjudicado);

			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
			return null;
		}
		em.flush();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
		return "ok";
	}

	@SuppressWarnings("unchecked")
	public void enviarEmails(EvalDocumentalCab cab) {
		PersonaPostulante persona = new PersonaPostulante();
		persona = cab.getPostulacion().getPersonaPostulante();
		String dirEmail = persona.getEMail();
		String aprobado = "";
		if (cab.getAprobado() == null || !cab.getAprobado())
			aprobado = "no";
		String asunto = "NOTIF_RESULT_EVAL_DOCUM_ADJUDICADOS_";
		asunto = asunto + concurso.getNombre() + "_" + concursoPuestoAgr.getDescripcionGrupo();
		String texto = "";
		String obs = cab.getObservacion();
		if (obs == null)
			obs = "";

		try {
			texto =
				texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ persona.getNombres()
					+ " "
					+ persona.getApellidos()
					+ "</p> "
					+ "</b> <p align=\"justify\"> Le comunicamos que "
					+ aprobado
					+ " ha aprobado el Proceso de Evaluaci&oacute;n Documental para Adjudicados para el Puesto: <b>"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "</b> del Concurso: <b>"
					+ +concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ "</b> </p> "
					+ "<p align=\"justify\">Resaltamos la siguiente observac&oacute;n al respecto: </p>"
					+ "<p align=\"justify\">" + obs + "</p> " + "<p> Atentos saludos</p> ";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto, null);
			System.out.println("EL MAIL HA SIDO ENVIADO....");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void insertarHistorial() {
		HistorialExcepcion historialExcepcion = new HistorialExcepcion();
		historialExcepcion.setConcursoGrupoAgr(concursoPuestoAgr);
		historialExcepcion.setExcepcion(excepcion);
		historialExcepcion.setEstado("EVAL. ADJUDICADOS");
		historialExcepcion.setFechaAlta(new Date());
		historialExcepcion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		if (observacion != null && !observacion.trim().isEmpty())
			historialExcepcion.setObservacion(observacion);
		em.persist(historialExcepcion);

	}

	private void actualizarEstadosGrupo() {

		cambiarEstadoPuestosTare(traerGrupoPuestoDet());
	}

	private List<ConcursoPuestoDet> traerGrupoPuestoDet() {
		Query q =
			em.createQuery("select ConcursoPuestoDet from ConcursoPuestoDet ConcursoPuestoDet"
				+ " where ConcursoPuestoDet.activo is true "
				+ "and ConcursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr =  "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and ConcursoPuestoDet.excepcion.idExcepcion = " + excepcion.getIdExcepcion());
		return q.getResultList();
	}

	private void actualizarEstadoGrupoExcepcion() {

		List<ConcursoPuestoAgrExc> listaExc = obtenerListaAgrExc();

		for (ConcursoPuestoAgrExc exc : listaExc) {
			exc.setEstado("EVAL. ADJUDICADOS");
			exc.setFechaMod(new Date());
			exc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(exc);
		}
	}

	private List<ConcursoPuestoAgrExc> obtenerListaAgrExc() {
		Query q =
			em.createQuery("select exc from ConcursoPuestoAgrExc exc "
				+ " where exc.activo is true "
				+ "and exc.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and exc.excepcion.idExcepcion = :idExc");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("idExc", excepcion.getIdExcepcion());
		return q.getResultList();
	}

	private ConcursoPuestoAgrExc obtenerAgrExc() {
		List<ConcursoPuestoAgrExc> lista = obtenerListaAgrExc();
		if (lista.isEmpty())
			return null;
		else
			return lista.get(0);
	}

	/**
	 * Cambia el estado de los puestos a desierto en caso de que no existan postulantes
	 * 
	 * @param lista
	 */
	public void cambiarEstadoPuestosTare(List<ConcursoPuestoDet> lista) {

		EstadoDet estadoEval =
			evaluarDocPostulantesFormController.obtenerEstadosVarios("EVALUACION ADJUDICADOS", "CONCURSO");
		EstadoDet estadoDet =
			evaluarDocPostulantesFormController.obtenerEstadosVarios("LISTA CORTA", "CONCURSO");
		for (ConcursoPuestoDet concursoPuesto : lista) {
			// Actualizar el Puesto
			EstadoCab est = concursoPuesto.getPlantaCargoDet().getEstadoCab();
			Long id = concursoPuesto.getPlantaCargoDet().getIdPlantaCargoDet();
			if (concursoPuesto.getPlantaCargoDet().getEstadoDet() != null && concursoPuesto.getPlantaCargoDet().getEstadoDet().getIdEstadoDet().equals(estadoEval.getIdEstadoDet())) {
				concursoPuesto.getPlantaCargoDet().setEstadoCab(estadoEval.getEstadoCab());
				concursoPuesto.getPlantaCargoDet().setEstadoDet(null);
				concursoPuesto.getPlantaCargoDet().setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuesto.getPlantaCargoDet().setFechaMod(new Date());
				concursoPuesto.setPlantaCargoDet(em.merge(concursoPuesto.getPlantaCargoDet()));
				concursoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuesto.setFechaMod(new Date());
				concursoPuesto.setActivo(false);

				concursoPuesto.setEstadoDet(estadoEval);
				concursoPuesto.setActivo(false);
				concursoPuesto = em.merge(concursoPuesto);
			}
		}
	}

	private void actualizarPostulantes() {
		buscarEvaluacionPostulantes();
		for (EvalReferencialPostulante ev : listaReferencialPostulantes) {
			ev.setSelAdjudicado(true);
			ev.setUsuMod(usuarioLogueado.getCodigoUsuario());
			ev.setFechaMod(new Date());
			em.merge(ev);
		}
	}

	private Boolean validacion1() {
		Query q =
			em.createQuery("select docCab from EvalDocumentalCab docCab "
				+ " where docCab.activo is true and (docCab.evaluado is null or docCab.evaluado is false) "
				+ "and docCab.postulacion.activo is true "
				+ "and docCab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and docCab.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and docCab.excepcion.idExcepcion = :idExc");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q.setParameter("idExc", excepcion.getIdExcepcion());
		List<EvalDocumentalCab> lista = q.getResultList();
		if (lista.isEmpty())
			return true;
		return false;
	}

	private Boolean validacion2() {
		Query q1 =
			em.createQuery("select det from ConcursoPuestoDet det " + " where det.activo is true "
				+ "and det.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and det.excepcion.idExcepcion = :idExc");
		q1.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q1.setParameter("idExc", excepcion.getIdExcepcion());
		List<ConcursoPuestoDet> listaDet = q1.getResultList();
		Integer cantActivos = new Integer(0);
		if (!listaDet.isEmpty())
			cantActivos = listaDet.size();
		Query q2 =
			em.createQuery("select cab from EvalDocumentalCab cab " + " where cab.activo is true "
				+ "and cab.postulacion.activo is true " + "and cab.incluido is true "
				+ "and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and cab.concursoPuestoAgr.idConcursoPuestoAgr = :id "
				+ "and cab.excepcion.idExcepcion = :idExc");
		q2.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		q2.setParameter("idExc", excepcion.getIdExcepcion());
		List<EvalDocumentalCab> listaApro = q2.getResultList();
		Integer cantApr = new Integer(0);
		if (!listaApro.isEmpty())
			cantApr = listaApro.size();
		if (cantActivos.intValue() == cantApr.intValue())
			return true;
		return false;
	}

	public String obtenerEstadoAdjudicado(Integer row) {
		EvalDocumentalCab eval = new EvalDocumentalCab();
		eval = listaAdjudicados.get(row);
		if (eval.getAprobado() == null)
			return null;
		if (eval.getAprobado())
			return "APROBADO";
		if (!eval.getAprobado())
			return "NO APROBADO";
		return null;

	}

	public void imprimir(EvalDocumentalCab evalDocumentalCab) throws Exception {
		Long id =
			evalDocumentalCab.getPostulacion().getPersonaPostulante().getIdPersonaPostulante();
		tab7VistaPrePostulacionActualFC.setIdPersonaPostulante(id);
		tab7VistaPrePostulacionActualFC.init2();
		tab7VistaPrePostulacionActualFC.pdf();
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public Boolean getEsPrimeraVez() {
		return esPrimeraVez;
	}

	public void setEsPrimeraVez(Boolean esPrimeraVez) {
		this.esPrimeraVez = esPrimeraVez;
	}

	public Boolean getHabControl() {
		return habControl;
	}

	public void setHabControl(Boolean habControl) {
		this.habControl = habControl;
	}

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public List<EvalReferencialPostulante> getListaReferencialPostulantes() {
		return listaReferencialPostulantes;
	}

	public void setListaReferencialPostulantes(List<EvalReferencialPostulante> listaReferencialPostulantes) {
		this.listaReferencialPostulantes = listaReferencialPostulantes;
	}

	public List<MatrizDocConfigDet> getListaDocAdjudicados() {
		return listaDocAdjudicados;
	}

	public void setListaDocAdjudicados(List<MatrizDocConfigDet> listaDocAdjudicados) {
		this.listaDocAdjudicados = listaDocAdjudicados;
	}

	public List<EvalDocumentalDet> getListaEvalDocDet() {
		return listaEvalDocDet;
	}

	public void setListaEvalDocDet(List<EvalDocumentalDet> listaEvalDocDet) {
		this.listaEvalDocDet = listaEvalDocDet;
	}

	public Integer getCantEvaluados() {
		return cantEvaluados;
	}

	public void setCantEvaluados(Integer cantEvaluados) {
		this.cantEvaluados = cantEvaluados;
	}

	public Integer getCantVacancia() {
		return cantVacancia;
	}

	public void setCantVacancia(Integer cantVacancia) {
		this.cantVacancia = cantVacancia;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public List<EvalDocumentalCab> getListaAdjudicados() {
		return listaAdjudicados;
	}

	public void setListaAdjudicados(List<EvalDocumentalCab> listaAdjudicados) {
		this.listaAdjudicados = listaAdjudicados;
	}

	public Boolean getTodosEvaluados() {
		return todosEvaluados;
	}

	public void setTodosEvaluados(Boolean todosEvaluados) {
		this.todosEvaluados = todosEvaluados;
	}

	public String getVolvio() {
		return volvio;
	}

	public void setVolvio(String volvio) {
		this.volvio = volvio;
	}

	public EvaluarDocPostulantesFormController getEvaluarDocPostulantesFormController() {
		return evaluarDocPostulantesFormController;
	}

	public void setEvaluarDocPostulantesFormController(EvaluarDocPostulantesFormController evaluarDocPostulantesFormController) {
		this.evaluarDocPostulantesFormController = evaluarDocPostulantesFormController;
	}

	public ConcursoPuestoAgrExc getConcursoPuestoAgrExc() {
		return concursoPuestoAgrExc;
	}

	public void setConcursoPuestoAgrExc(ConcursoPuestoAgrExc concursoPuestoAgrExc) {
		this.concursoPuestoAgrExc = concursoPuestoAgrExc;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

}
