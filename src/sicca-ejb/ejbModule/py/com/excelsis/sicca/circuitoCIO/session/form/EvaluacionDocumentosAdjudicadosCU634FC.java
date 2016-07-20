package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.entity.EvalDocumentalDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.ListaElegibles;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.EvalDocumentalCabHome;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC;
import py.com.excelsis.sicca.seleccion.session.form.EvaluarDocPostulantesFormController;
import py.com.excelsis.sicca.seleccion.session.form.ListaElegiblesFormController;
import py.com.excelsis.sicca.seleccion.session.form.Tab7VistaPrePostulacionActualFC;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.Tab6VistaPreliminarFormController;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("evaluacionDocumentosAdjudicadosCU634FC")
public class EvaluacionDocumentosAdjudicadosCU634FC {
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
	ReponerCategoriasController reponerCategoriasController;
	@In(create = true)
	Tab7VistaPrePostulacionActualFC tab7VistaPrePostulacionActualFC;
	@In(create = true)
	Tab6VistaPreliminarFormController tab6VistaPreliminarFormController;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;

	private String codConcurso;
	private String volvio;
	private String observacion;
	private String direccionFisica;
	private Boolean esPrimeraVez;
	private Boolean todosEvaluados;
	private Boolean habilitarControPuesto = false;
	private Boolean habilitarElegibles;
	private Boolean bloquearTodo;
	private Integer cantVacancia;
	private Integer cantEvaluados;
	private Integer cantAprobados;
	private Integer cantidadPuestosActivos;

	private List<EvalReferencialPostulante> listaReferencialPostulantes;
	private List<MatrizDocConfigDet> listaDocAdjudicados;
	private List<EvalDocumentalDet> listaEvalDocDet;
	private List<EvalDocumentalCab> listaAdjudicados;
	private List<PlantaCargoDet> listaCargos;
	private EvaluarDocPostulantesFormController evaluarDocPostulantesFormController;
	public AdmFecRecPosFC admFecRecPosFC;
	ListaElegiblesFormController listaElegiblesFormController;
	private SeguridadUtilFormController seguridadUtilFormController;
	
	private void validarOee(Concurso concurso) {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}

		seguridadUtilFormController.verificarPerteneceOeeCIO(concurso.getConfiguracionUoCab().getIdConfiguracionUo(), concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "LISTA CORTA")+"", ActividadEnum.CIO_EVALUACION_DOCUMENTAL.getValor());
		
	}

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		bloquearTodo = false;
		habilitarControPuesto = false;
		listaElegiblesFormController = (ListaElegiblesFormController) Component
				.getInstance(ListaElegiblesFormController.class, true);
		evaluarDocPostulantesFormController = (EvaluarDocPostulantesFormController) Component
				.getInstance(EvaluarDocPostulantesFormController.class, true);
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		esPrimeraVez();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
		if (volvio != null && !volvio.trim().isEmpty()) {
			guardarIncluidos();
			statusMessages.add(Severity.INFO,
					"Debe evlauar a los Postulantes incluidos.");
		}
		if (!existenActivos()) {
			declararDesierto();
			bloquearTodo = true;
			statusMessages
					.add(Severity.ERROR,
							"Este grupo se declara desierto por no contar con Puestos Activos.");
			return;
		}		
		if (cantPostulantes().intValue() == 0) {
			if (!existenElegibles()) {
				declararDesierto();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				return;
			} else {

				bloquearTodo = true;
				habilitarControPuesto = true;
				statusMessages
						.add(Severity.ERROR,
								"Debe gestionar elegibles desde Control Puesto/Postulante");
				return;
			}
		}
		if (concurso != null) {
			codConcurso = concurso.getNumero() + "/" + concurso.getAnhio();
			configuracionUoCab = concurso.getConfiguracionUoCab();
			if (configuracionUoCab != null) {
				codConcurso = codConcurso + " DE "
						+ configuracionUoCab.getDescripcionCorta();
				sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
			}
			if (sinEntidad != null)
				nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
		}

		buscarEvaluacionPostulantes();
		buscarDocsAdjudicados();
		insertarEvaluacionDocumental();
		actualizarDocumentalDetalle();
		cantEvaluados = buscarCantidadEvaluados();
		cantVacancia = buscarCantidadVacancia();
		obtenerDirFisica();
		buscarListaAdjudicados();
		verificarTodosEvaluados();
		cantAprobados = buscarCantidadAprobados();
		if (listaAdjudicados.size() > cantVacancia && todosEvaluados)
			habilitarElegibles = false;
		if (listaAdjudicados.size() <= cantVacancia && todosEvaluados)
			habilitarElegibles = true;
		habilitarControPuesto = false;
		statusMessages.clear();
		
		if (buscarCantidadAdjudicadosAprobados().intValue() == 0) {
			if (!existenElegibles()) {
				declararDesierto();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				return;
			} else {

				bloquearTodo = true;
				habilitarControPuesto = true;
				statusMessages
						.add(Severity.ERROR,
								"Debe gestionar elegibles desde Control Puesto/Postulante");
				return;
			}
		}
	}
	
	private Integer buscarCantidadAdjudicadosAprobados() {
		String sql = "select cab.* "
				+ "from seleccion.eval_documental_cab cab "
				+ " join seleccion.postulacion postulacion on postulacion.id_postulacion = cab.id_postulacion "
				+ "where cab.activo is true and cab.tipo = 'EVALUACION ADJUDICADOS' and "
				//+ "(cab.aprobado is true or cab.aprobado is null) and (cab.evaluado is true or cab.evaluado is null)"
				+ "(cab.aprobado is true or cab.aprobado is null)"
				+ "and postulacion.usu_cancelacion is null "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(sql, EvalDocumentalCab.class).getResultList();
		if (lista.isEmpty())
			return new Integer(0);
		return lista.size();
	}

	private Boolean existenElegibles() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT c.* " + " FROM seleccion.lista_elegibles_cab  c, "
				+ "seleccion.lista_elegibles_det   d  "
				+ "WHERE c.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " AND c.vto_validez_lista >= to_date('"
				+ sdf.format(new Date()) + "','DD-MM-YYYY') "
				+ "AND c.id_lista_elegibles_cab = d.id_lista_elegibles_cab "
				+ "AND d.disponible is true";
		try {
			List<ListaElegiblesCab> eleg = new ArrayList<ListaElegiblesCab>();
			eleg = em.createNativeQuery(sql, ListaElegiblesCab.class)
					.getResultList();
			if (eleg.isEmpty())
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private Boolean existenActivos() {
		try {
			String sql = "SELECT d.* FROM seleccion.concurso_puesto_det d "
					+ "WHERE d.activo = true "
					+ "AND   d.id_concurso_puesto_agr = "
					+ concursoPuestoAgr.getIdConcursoPuestoAgr();
			List<ConcursoPuestoDet> det = new ArrayList<ConcursoPuestoDet>();
			det = em.createNativeQuery(sql, ConcursoPuestoDet.class)
					.getResultList();
			if (det.isEmpty()) {
				cantidadPuestosActivos = 0;
				return false;
			}
			cantidadPuestosActivos = det.size();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void imprimir(EvalDocumentalCab evalDocumentalCab) throws Exception {
		Long id = evalDocumentalCab.getPostulacion().getPersonaPostulante()
				.getIdPersonaPostulante();
		//tab6VistaPreliminarFormController.setFromCU("350");
		tab6VistaPreliminarFormController.setIdPostulacion(evalDocumentalCab.getPostulacion().getIdPostulacion());
		tab6VistaPreliminarFormController.pdf();
		/*tab7VistaPrePostulacionActualFC.setIdPersonaPostulante(id);
		tab7VistaPrePostulacionActualFC.init2();
		tab7VistaPrePostulacionActualFC.pdf();*/
	}

	private void obtenerDirFisica() {
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		direccionFisica = separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();

	}

	private void guardarIncluidos() {
		Query q = em
				.createQuery("select eval from EvalReferencialPostulante eval "
						+ " where eval.activo is true and "
						+ "eval.listaCorta is true and eval.seleccionado is true "
						+ "and (eval.excluido is null or eval.excluido is false) "
						+ "and eval.incluido is true "
						+ "and eval.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<EvalReferencialPostulante> lista = q.getResultList();
		insertarEvaluacionDocumentalIncluidos(lista);
		volvio = null;
	}

	private void insertarEvaluacionDocumentalIncluidos(
			List<EvalReferencialPostulante> lista) {
		buscarDocsAdjudicados();
		try {
			for (EvalReferencialPostulante eval : lista) {
				EvalDocumentalCab evalDocumentalCab = new EvalDocumentalCab();
				evalDocumentalCab.setActivo(true);
				evalDocumentalCab.setTipo("EVALUACION ADJUDICADOS");
				evalDocumentalCab.setPostulacion(eval.getPostulacion());
				evalDocumentalCab.setConcursoPuestoAgr(concursoPuestoAgr);
				evalDocumentalCab.setIncluido(true);
				evalDocumentalCabHome.setInstance(evalDocumentalCab);
				String resultado = evalDocumentalCabHome.persist();
				if (resultado.equals("persisted")) {
					listaEvalDocDet = new ArrayList<EvalDocumentalDet>();
					for (MatrizDocConfigDet matrizDet : listaDocAdjudicados) {
						EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();
						evalDocumentalDet.setActivo(true);
						evalDocumentalDet
								.setEvalDocumentalCab(evalDocumentalCabHome
										.getInstance());
						evalDocumentalDet.setMatrizDocConfigDet(matrizDet);
						evalDocumentalDet.setFechaAlta(new Date());
						evalDocumentalDet.setUsuAlta(usuarioLogueado
								.getCodigoUsuario());
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

	private Integer cantPostulantes() {
		Query q = em
				.createQuery("select eval from EvalReferencialPostulante eval "
						+ " where eval.listaCorta is true and eval.seleccionado is true "
						+ "and eval.postulacion.activo is true and eval.postulacion.usuCancelacion is null "
						+ "and eval.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<EvalReferencialPostulante> lista = q.getResultList();
		if (lista.isEmpty())
			return new Integer(0);
		return lista.size();
	}

	/**
	 * Declara desierto en caso de que no existan postulantes
	 */
	private void declararDesierto() {
		Integer estadoGrupoDesierto = evaluarDocPostulantesFormController
				.obtenerEstadoGrupo("ESTADOS_GRUPO", "GRUPO DESIERTO");
		/* Actualizar el estado del Grupo: */
		cambiarEstadoGrupo(concursoPuestoAgr, estadoGrupoDesierto);
		/* Actualizar el ESTADO de los PUESTOS: */
		cambiarEstadoPuestos(evaluarDocPostulantesFormController
				.traerGrupoPuestoDet(concursoPuestoAgr.getIdConcursoPuestoAgr()));

		// Se pasa a la siguiente tarea
		try {
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CIO_VALIDAR_MATRIZ_DOCUMENTAL_ADJ);
			jbpmUtilFormController.setTransition("end");

			if (jbpmUtilFormController.nextTask())
				em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		/* Enviar mails a los integrantes de la Comisión de Selección */
		evaluarDocPostulantesFormController
				.enviarMailComision(concursoPuestoAgr);
		/* Enviar mail al Postulante (si hubiere) para el Grupo */
		if (admFecRecPosFC == null) {
			admFecRecPosFC = (py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC) Component
					.getInstance(AdmFecRecPosFC.class, true);
		}
		admFecRecPosFC.enviarMail2(concursoPuestoAgr);
		admFecRecPosFC.insertarPublicacionPortal(concursoPuestoAgr
				.getIdConcursoPuestoAgr(), concursoPuestoAgr.getConcurso()
				.getIdConcurso(), admFecRecPosFC.genTextoPublicacion2());

		reponerCategoriasController.reponerCategorias(concursoPuestoAgr,
				"GRUPO", "CONCURSO");

	}

	/**
	 * Cambia el estado de los grupos a desierto en caso de que no existan
	 * postulantes
	 * 
	 * @param grupo
	 */
	private void cambiarEstadoGrupo(ConcursoPuestoAgr grupo, Integer estado) {
		grupo.setEstado(estado);
		grupo.setUsuMod(usuarioLogueado.getCodigoUsuario());
		grupo.setFechaMod(new Date());
		grupo = em.merge(grupo);
	}

	/**
	 * Cambia el estado de los puestos a desierto en caso de que no existan
	 * postulantes
	 * 
	 * @param lista
	 */
	public void cambiarEstadoPuestos(List<ConcursoPuestoDet> lista) {
		EstadoCab estadoCab = evaluarDocPostulantesFormController
				.obtenerEstadosCabecera("VACANTE");
		EstadoDet estadoEval = evaluarDocPostulantesFormController
				.obtenerEstadosVarios("LISTA CORTA", "CONCURSO");
		EstadoDet estadoDet = evaluarDocPostulantesFormController
				.obtenerEstadosVarios("DESIERTO", "CONCURSO");
		for (ConcursoPuestoDet concursoPuesto : lista) {
			// Actualizar el Puesto
			if (concursoPuesto.getPlantaCargoDet().getEstadoDet()
					.getIdEstadoDet().equals(estadoEval.getIdEstadoDet())) {
				concursoPuesto.getPlantaCargoDet().setEstadoCab(estadoCab);
				concursoPuesto.getPlantaCargoDet().setEstadoDet(null);
				concursoPuesto.getPlantaCargoDet().setUsuMod(
						usuarioLogueado.getCodigoUsuario());
				concursoPuesto.getPlantaCargoDet().setFechaMod(new Date());
				concursoPuesto.setPlantaCargoDet(em.merge(concursoPuesto
						.getPlantaCargoDet()));
				concursoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuesto.setFechaMod(new Date());
				concursoPuesto.setActivo(false);
				EstadoDet estado = new EstadoDet();
				estado.setIdEstadoDet(estadoDet.getIdEstadoDet());
				concursoPuesto.setEstadoDet(estado);
				concursoPuesto.setActivo(false);
				concursoPuesto = em.merge(concursoPuesto);
			}
		}
	}

	/**
	 * Verifica que sea la primera vez que ingresa en usuario a esta pantalla
	 */
	@SuppressWarnings("unchecked")
	private void esPrimeraVez() {
		String sql = "select cab.*  "
				+ "from seleccion.eval_documental_cab cab "
				+ "where cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cab.tipo = 'EVALUACION ADJUDICADOS'";
		List<EvalDocumentalCab> lista = new ArrayList<EvalDocumentalCab>();
		lista = em.createNativeQuery(sql, EvalDocumentalCab.class)
				.getResultList();
		if (lista.size() > 0)
			esPrimeraVez = false;
		else
			esPrimeraVez = true;
	}

	/**
	 * Busca la lista de postulantes a ser evaluados de acuerdo al grupo
	 * recibido desde la bandeja
	 */
	@SuppressWarnings("unchecked")
	private void buscarEvaluacionPostulantes() {
		Query q = em
				.createQuery("select eval from EvalReferencialPostulante eval "
						+ " where eval.listaCorta is true and eval.seleccionado is true "
						+ "and eval.activo is true and (eval.excluido is null or eval.excluido is false) "
						+ "and eval.postulacion.activo is true "
						+ "and eval.postulacion.usuCancelacion is null "
						+ "and eval.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());

		listaReferencialPostulantes = new ArrayList<EvalReferencialPostulante>();
		listaReferencialPostulantes = q.getResultList();
	}

	/**
	 * Busca la lista de Documentos Adjudicados teniendo en cuenta ciertos
	 * criterios
	 */
	@SuppressWarnings("unchecked")
	private void buscarDocsAdjudicados() {
		String sql = "select matriz_det.* "
				+ "from seleccion.matriz_doc_config_enc matriz_enc "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = matriz_enc.id_concurso_puesto_agr "
				+ "join seleccion.matriz_doc_config_det matriz_det "
				+ "on matriz_det.id_matriz_doc_config_enc = matriz_enc.id_matriz_doc_config_enc "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and matriz_det.activo is true "
				+ "and matriz_det.adjudicacion is true "
				+ "order by matriz_det.nro_orden";
		listaDocAdjudicados = new ArrayList<MatrizDocConfigDet>();
		listaDocAdjudicados = em.createNativeQuery(sql,
				MatrizDocConfigDet.class).getResultList();
	}

	/**
	 * Inserta datos tanto en la tabla eval_documental_cab como en la tabla
	 * eval_documental_det
	 */
	private void insertarEvaluacionDocumental() {
		try {
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				if (!existeEvalCab(eval)) {
					EvalDocumentalCab evalDocumentalCab = new EvalDocumentalCab();
					evalDocumentalCab.setActivo(true);
					evalDocumentalCab.setTipo("EVALUACION ADJUDICADOS");
					evalDocumentalCab.setPostulacion(eval.getPostulacion());
					evalDocumentalCab.setConcursoPuestoAgr(concursoPuestoAgr);
					evalDocumentalCabHome.setInstance(evalDocumentalCab);
					String resultado = evalDocumentalCabHome.persist();
					if (resultado.equals("persisted")) {
						listaEvalDocDet = new ArrayList<EvalDocumentalDet>();
						for (MatrizDocConfigDet matrizDet : listaDocAdjudicados) {
							EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();
							evalDocumentalDet.setActivo(true);
							evalDocumentalDet
									.setEvalDocumentalCab(evalDocumentalCabHome
											.getInstance());
							evalDocumentalDet.setMatrizDocConfigDet(matrizDet);
							evalDocumentalDet.setFechaAlta(new Date());
							evalDocumentalDet.setUsuAlta(usuarioLogueado
									.getCodigoUsuario());
							em.persist(evalDocumentalDet);
							listaEvalDocDet.add(evalDocumentalDet);
						}
						em.flush();
						evalDocumentalCabHome.clearInstance();
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private Boolean existeEvalCab(EvalReferencialPostulante eval) {
		Query q = em.createQuery("select eval from EvalDocumentalCab eval "
				+ " where eval.activo is true "
				+ "and eval.postulacion.usuCancelacion is null "
				+ "and eval.tipo = 'EVALUACION ADJUDICADOS'"
				+ "and eval.postulacion.idPostulacion = "
				+ eval.getPostulacion().getIdPostulacion()
				+ " and eval.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ eval.getConcursoPuestoAgr().getIdConcursoPuestoAgr());

		List<EvalDocumentalCab> listaDocEval = new ArrayList<EvalDocumentalCab>();
		listaDocEval = q.getResultList();
		if (listaDocEval.isEmpty())
			return false;
		return true;
	}

	/**
	 * Actualiza los detalles en caso de que el documento haya sido presentado
	 */
	private void actualizarDocumentalDetalle() {
		try {
			for (EvalReferencialPostulante eval : listaReferencialPostulantes) {
				List<Documentos> listaDocs = new ArrayList<Documentos>();
				listaDocs = buscarDocumentos(eval
						.getIdEvalReferencialPostulante());
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
	 * Verifica existencia de un tipo de documento
	 * 
	 * @param documentos
	 * @param d
	 * @return
	 */
	private Boolean verificarExistenciaTipoDoc(List<Documentos> documentos,
			EvalDocumentalDet d) {
		for (Documentos doc : documentos) {
			if (d.getMatrizDocConfigDet().getDatosEspecificos()
					.getIdDatosEspecificos()
					.equals(doc.getDatosEspecificos().getIdDatosEspecificos()))
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Documentos> buscarDocumentos(Long id) {
		String sql = "select doc.* " + "from general.documentos doc "
				+ "join seleccion.postulacion_adjuntos post_adj "
				+ "on post_adj.id_documento = doc.id_documento "
				+ "join seleccion.postulacion post  "
				+ "on post.id_postulacion = post_adj.id_postulacion "
				+ "join seleccion.eval_referencial_postulante eval_ref "
				+ "on eval_ref.id_postulacion = post.id_postulacion "
				+ "where doc.activo is true "
				+ "and eval_ref.id_eval_referencial_postulante = " + id;
		return em.createNativeQuery(sql, Documentos.class).getResultList();
	}

	/**
	 * Busca la cantidad de vacancias para el puesto
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadVacancia() {
		Integer resultado = null;
		String sql = "select count(puesto_det.*) "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "where puesto_det.activo is true "
				+ "and puesto_det.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	/**
	 * Busca la cantidad de evaluados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadEvaluados() {
		Integer resultado = null;
		String sql = "select count(cab.*) "
				+ "from seleccion.eval_documental_cab cab "
				+ "join seleccion.postulacion postulacion "
				+ "on postulacion.id_postulacion = cab.id_postulacion "
				+ "where cab.activo is true "
				+ "and cab.evaluado is true and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and postulacion.usu_cancelacion is null "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	@SuppressWarnings("unchecked")
	private void buscarListaAdjudicados() {
		String sql = "select cab.* "
				+ "from seleccion.eval_documental_cab cab "
				+ " join seleccion.postulacion postulacion on postulacion.id_postulacion = cab.id_postulacion "
				+ "where cab.activo is true and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and postulacion.usu_cancelacion is null "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaAdjudicados = new ArrayList<EvalDocumentalCab>();
		listaAdjudicados = em.createNativeQuery(sql, EvalDocumentalCab.class)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private void verificarTodosEvaluados() {
		String sql = "select count(cab.*) "
				+ "from seleccion.eval_documental_cab cab "
				+ " join seleccion.postulacion postulacion on postulacion.id_postulacion = cab.id_postulacion "
				+ "where cab.activo is true "
				+ "and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and postulacion.usu_Cancelacion is null "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
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

	/**
	 * Busca la cantidad de aprobados
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer buscarCantidadAprobados() {
		Integer resultado = null;
		String sql = "select count(cab.*) "
				+ "from seleccion.eval_documental_cab cab "
				+ "join seleccion.postulacion postulacion "
				+ "on postulacion.id_postulacion = cab.id_postulacion "
				+ "where cab.activo is true " + "and cab.aprobado is true "
				+ "and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and postulacion.usu_Cancelacion is null "
				+ "and cab.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null) {
			return 0;
		}
		resultado = new Integer(config.toString());
		return resultado;
	}

	private Boolean logica2(Integer canAprobados, Integer cantVacancias,
			String elFrom) {
		if (listaElegiblesFormController == null) {
			listaElegiblesFormController = (ListaElegiblesFormController) Component
					.getInstance(ListaElegiblesFormController.class, true);
		}
		listaElegiblesFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		listaElegiblesFormController.setCantVacancia(cantVacancias);
		listaElegiblesFormController.setCantAprobados(canAprobados);

		evaluarDocPostulantesFormController.continuaValSgteTarea = true;
		if (canAprobados.intValue() < 2) {

			/* Actualizar el estado del Grupo: */
			evaluarDocPostulantesFormController
					.cambiarEstadoGrupo(concursoPuestoAgr, "ESTADOS_GRUPO",
							"GRUPO DESIERTO", false);
			/* Actualizar el ESTADO de los PUESTOS: */
			evaluarDocPostulantesFormController
					.cambiarEstadoPuestos(evaluarDocPostulantesFormController
							.traerGrupoPuestoDet(concursoPuestoAgr
									.getIdConcursoPuestoAgr()));
			if (evaluarDocPostulantesFormController.admFecRecPosFC == null) {
				evaluarDocPostulantesFormController.admFecRecPosFC = (py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC) Component
						.getInstance(AdmFecRecPosFC.class, true);
				evaluarDocPostulantesFormController.admFecRecPosFC.setEm(em);
			}
			/* Enviar mails a los integrantes de la Comisión de Selección */
			evaluarDocPostulantesFormController
					.enviarMailComision(concursoPuestoAgr);
			/* Enviar mail al Postulante (si hubiere) para el Grupo */

			evaluarDocPostulantesFormController.admFecRecPosFC
					.enviarMail2(concursoPuestoAgr);
			evaluarDocPostulantesFormController.admFecRecPosFC
					.insertarPublicacionPortal(concursoPuestoAgr
							.getIdConcursoPuestoAgr(), concursoPuestoAgr
							.getConcurso().getIdConcurso(),
							evaluarDocPostulantesFormController.admFecRecPosFC
									.genTextoPublicacion2());
			evaluarDocPostulantesFormController.continuaValSgteTarea = true;
		}

		return evaluarDocPostulantesFormController.continuaValSgteTarea;
	}

	private Boolean validacion1() {
		Query q = em
				.createQuery("select docCab from EvalDocumentalCab docCab "
						+ " where docCab.activo is true and (docCab.evaluado is null or docCab.evaluado is false) "
						+ "and docCab.postulacion.activo is true "
						+ "and docCab.tipo = 'EVALUACION ADJUDICADOS' "
						+ "and docCab.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<EvalDocumentalCab> lista = q.getResultList();
		if (lista.isEmpty())
			return true;
		return false;
	}

	private Boolean validacion2() {
		Query q1 = em.createQuery("select det from ConcursoPuestoDet det "
				+ " where det.activo is true "
				+ "and det.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q1.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<ConcursoPuestoDet> listaDet = q1.getResultList();
		Integer cantActivos = new Integer(0);
		if (!listaDet.isEmpty())
			cantActivos = listaDet.size();
		Query q2 = em.createQuery("select cab from EvalDocumentalCab cab "
				+ " where cab.activo is true "
				+ "and cab.postulacion.activo is true "
				+ "and cab.aprobado is true "
				//+ "and cab.evaluado is true "
				+ "and cab.tipo = 'EVALUACION ADJUDICADOS' "
				+ "and cab.concursoPuestoAgr.idConcursoPuestoAgr = :id");
		q2.setParameter("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<EvalDocumentalCab> listaApro = q2.getResultList();
		Integer cantApr = new Integer(0);
		if (!listaApro.isEmpty())
			cantApr = listaApro.size();
		if (cantActivos.intValue() == cantApr.intValue())
			return true;
		return false;
	}

	private void actualizarPostulantes() {
		buscarEvaluacionPostulantes();
		for (EvalReferencialPostulante ev : listaReferencialPostulantes) {
			ev.setSelAdjudicado(functionGetAprobado(ev));
			ev.setUsuMod(usuarioLogueado.getCodigoUsuario());
			ev.setFechaMod(new Date());
			em.merge(ev);
		}
	}
	
	private boolean functionGetAprobado(EvalReferencialPostulante ev){
		for(EvalDocumentalCab eval : listaAdjudicados){
			if(ev.getPostulacion().getIdPostulacion() == eval.getPostulacion().getIdPostulacion()){
				return eval.getAprobado();
			}
		}
		return false;
	}

	private void actualizarEstadosGrupo() {
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"EVALUACION ADJUDICADOS");
		if (ref != null) {
			buscarCargos();
			concursoPuestoAgr.setEstado(ref.getValorNum());
			concursoPuestoAgr.setFechaEvalDocHasta(new Date());
			em.merge(concursoPuestoAgr);
			cambiarEstadoPuestosTare(evaluarDocPostulantesFormController
					.traerGrupoPuestoDet(concursoPuestoAgr
							.getIdConcursoPuestoAgr()));

		}
	}

	/**
	 * Cambia el estado de los puestos a desierto en caso de que no existan
	 * postulantes
	 * 
	 * @param lista
	 */
	public void cambiarEstadoPuestosTare(List<ConcursoPuestoDet> lista) {

		EstadoDet estadoEval = evaluarDocPostulantesFormController
				.obtenerEstadosVarios("EVALUACION ADJUDICADOS", "CONCURSO");
		EstadoDet estadoDet = evaluarDocPostulantesFormController
				.obtenerEstadosVarios("LISTA CORTA", "CONCURSO");
		for (ConcursoPuestoDet concursoPuesto : lista) {
			// Actualizar el Puesto
			if (concursoPuesto.getPlantaCargoDet().getEstadoDet()
					.getIdEstadoDet().equals(estadoDet.getIdEstadoDet())) {
				concursoPuesto.getPlantaCargoDet().setEstadoCab(
						estadoEval.getEstadoCab());
				concursoPuesto.getPlantaCargoDet().setEstadoDet(null);
				concursoPuesto.getPlantaCargoDet().setUsuMod(
						usuarioLogueado.getCodigoUsuario());
				concursoPuesto.getPlantaCargoDet().setFechaMod(new Date());
				concursoPuesto.setPlantaCargoDet(em.merge(concursoPuesto
						.getPlantaCargoDet()));
				concursoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuesto.setFechaMod(new Date());
				// concursoPuesto.setActivo(true);

				concursoPuesto.setEstadoDet(estadoEval);

				concursoPuesto = em.merge(concursoPuesto);
			}
		}
	}

	private void guardarObs() {
		Observacion tablaObservacion = new Observacion();
		tablaObservacion.setConcurso(concurso);
		if (observacion != null && observacion.trim().isEmpty())
			tablaObservacion.setObservacion(observacion);

		tablaObservacion.setFechaAlta(new Date());
		tablaObservacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		tablaObservacion.setUsuMod(usuarioLogueado.getCodigoUsuario());
		tablaObservacion.setIdTaskInstance(jbpmUtilFormController
				.getIdTaskInstanceActual(concursoPuestoAgr
						.getIdProcessInstance()));
		em.persist(tablaObservacion);
	}

	public String nextTask() {

		try {
			if (!validacion1()) {
				bloquearTodo = true;
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"Debe evaluar a todos los postulantes y cerrar todas las evaluaciones para pasar a la siguiente actividad");
				return null;
			}

			if (!existenActivos()) {
				declararDesierto();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Puestos Activos.");
				return null;
			}

			if (cantPostulantes().intValue() == 0) {
				if (!existenElegibles()) {
					declararDesierto();
					bloquearTodo = true;
					statusMessages
							.add(Severity.ERROR,
									"Este grupo se declara desierto por no contar con Postulantes suficientes.");
					return null;
				} else {

					bloquearTodo = true;
					habilitarControPuesto = true;
					statusMessages
							.add(Severity.ERROR,
									"Debe gestionar elegibles desde Control Puesto/Postulante");
					return null;
				}
			}

			if (!validacion2()) {
				bloquearTodo = true;
				habilitarControPuesto = true;
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"La cantidad de postulantes aprobados no es igual a cantidad de puestos. Verificar en control puesto/postulante");
				return null;
			}
			actualizarPostulantes();
			actualizarEstadosGrupo();

			if (observacion != null && !observacion.trim().isEmpty())
				guardarObs();

			for (EvalDocumentalCab adjudicado : listaAdjudicados) {
				if (adjudicado.getPostulacion().getUsuCancelacion() == null)
					enviarEmails(adjudicado);

			}
			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CIO_VALIDAR_MATRIZ_DOCUMENTAL_ADJ);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.PUBLICAR_ADJUDICADOS);
			jbpmUtilFormController.setTransition("next");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		} catch (Exception e) {
			return null;
		}

		return "next";
	}

	@SuppressWarnings("unchecked")
	public void enviarEmails(EvalDocumentalCab cab) {
		PersonaPostulante persona = new PersonaPostulante();
		persona = cab.getPostulacion().getPersonaPostulante();
		persona = em.find(PersonaPostulante.class, persona.getIdPersonaPostulante());
		String dirEmail = persona.getEMail();
		String aprobado = "";
		if (!cab.getAprobado())
			aprobado = "no";
		String asunto = "Evaluación Documental de Adjudicados -"+aprobado+" Aprobado";
		
		String texto = "";
		String obs = cab.getObservacion();
		if (obs != null && !obs.trim().equals("")){
			obs  = "<p align=\"justify\">Resaltamos la siguiente observac&oacute;n al respecto: </p> "+ "<p align=\"justify\">" + obs + "</p> ";
			
		}
		else{
			obs = "";
			
		}

		try {
			texto = texto
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
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ "</b> </p> "
					+ obs
					+ "<p> Atentamente, </p> "
					+ "<p>"+ configuracionUoCab.getDenominacionUnidad() +"</p> ";
			
			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void buscarCargos() {
		String sql = "select cargo.* "
				+ "from planificacion.planta_cargo_det cargo "
				+ "join seleccion.concurso_puesto_det puesto_det "
				+ "on puesto_det.id_planta_cargo_det = cargo.id_planta_cargo_det "
				+ "join seleccion.concurso_puesto_agr agr "
				+ "on agr.id_concurso_puesto_agr = puesto_det.id_concurso_puesto_agr "
				+ "where agr.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and cargo.activo is true";
		try {
			listaCargos = new ArrayList<PlantaCargoDet>();
			listaCargos = em.createNativeQuery(sql, PlantaCargoDet.class)
					.getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * Métodos que obtienen los urls necesarios
	 * 
	 * @return
	 */
	public String getUrlToPageListaElegibles() {
		return "/seleccion/evaluacionDocumentalAdjudicados/ListaElegibles.xhtml?fromToPage=seleccion/evaluacionDocumentalAdjudicados/EvaluarDocAdjudicado&concursoPuestoAgrIdConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
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

	public String getCodConcurso() {
		return codConcurso;
	}

	public void setCodConcurso(String codConcurso) {
		this.codConcurso = codConcurso;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDireccionFisica() {
		return direccionFisica;
	}

	public void setDireccionFisica(String direccionFisica) {
		this.direccionFisica = direccionFisica;
	}

	public Boolean getEsPrimeraVez() {
		return esPrimeraVez;
	}

	public void setEsPrimeraVez(Boolean esPrimeraVez) {
		this.esPrimeraVez = esPrimeraVez;
	}

	public List<EvalReferencialPostulante> getListaReferencialPostulantes() {
		return listaReferencialPostulantes;
	}

	public void setListaReferencialPostulantes(
			List<EvalReferencialPostulante> listaReferencialPostulantes) {
		this.listaReferencialPostulantes = listaReferencialPostulantes;
	}

	public List<MatrizDocConfigDet> getListaDocAdjudicados() {
		return listaDocAdjudicados;
	}

	public void setListaDocAdjudicados(
			List<MatrizDocConfigDet> listaDocAdjudicados) {
		this.listaDocAdjudicados = listaDocAdjudicados;
	}

	public List<EvalDocumentalDet> getListaEvalDocDet() {
		return listaEvalDocDet;
	}

	public void setListaEvalDocDet(List<EvalDocumentalDet> listaEvalDocDet) {
		this.listaEvalDocDet = listaEvalDocDet;
	}

	public Integer getCantVacancia() {
		return cantVacancia;
	}

	public void setCantVacancia(Integer cantVacancia) {
		this.cantVacancia = cantVacancia;
	}

	public Integer getCantEvaluados() {
		return cantEvaluados;
	}

	public void setCantEvaluados(Integer cantEvaluados) {
		this.cantEvaluados = cantEvaluados;
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

	public Boolean getHabilitarElegibles() {
		return habilitarElegibles;
	}

	public void setHabilitarElegibles(Boolean habilitarElegibles) {
		this.habilitarElegibles = habilitarElegibles;
	}

	public Integer getCantAprobados() {
		return cantAprobados;
	}

	public void setCantAprobados(Integer cantAprobados) {
		this.cantAprobados = cantAprobados;
	}

	public List<PlantaCargoDet> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<PlantaCargoDet> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public Boolean getHabilitarControPuesto() {
		return habilitarControPuesto;
	}

	public void setHabilitarControPuesto(Boolean habilitarControPuesto) {
		this.habilitarControPuesto = habilitarControPuesto;
	}

	public String getVolvio() {
		return volvio;
	}

	public void setVolvio(String volvio) {
		this.volvio = volvio;
	}

	public Integer getCantidadPuestosActivos() {
		return cantidadPuestosActivos;
	}

	public void setCantidadPuestosActivos(Integer cantidadPuestosActivos) {
		this.cantidadPuestosActivos = cantidadPuestosActivos;
	}

	public Boolean getBloquearTodo() {
		return bloquearTodo;
	}

	public void setBloquearTodo(Boolean bloquearTodo) {
		this.bloquearTodo = bloquearTodo;
	}

}
