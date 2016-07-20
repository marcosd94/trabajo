package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.HistorialActividadesGrupo;
import py.com.excelsis.sicca.entity.Lista;
import py.com.excelsis.sicca.entity.ListaElegibles;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PublicacionPortal;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC;
import py.com.excelsis.sicca.seleccion.session.form.EvaluarDocPostulantesFormController;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("publicacionSeleccionadosCU635FC")
public class PublicacionSeleccionadosCU635FC {
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
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReponerCategoriasController reponerCategoriasController;

	private EvaluarDocPostulantesFormController evaluarDocPostulantesFormController;
	public AdmFecRecPosFC admFecRecPosFC;

	private ConcursoPuestoAgr concursoPuestoAgr;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;

	private String codConcurso;
	private String observacion;
	private String direccionFisica;
	private String codActividad;
	private Integer vacancia;
	private Integer estadoGrupoDesierto;
	private Integer cantidadPuestosActivos;
	private Boolean bloquearTodo;
	private Boolean bloquearControlPuesto;

	private List<EvalReferencialPostulante> listaPostulantesSeleccionados;
	private List<PlantaCargoDet> listaCargos;
	private List<ConcursoPuestoDet> listaPuestos;
	private SeguridadUtilFormController seguridadUtilFormController;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		bloquearTodo = false;
		bloquearControlPuesto = true;
		concursoPuestoAgr = new ConcursoPuestoAgr();
		concursoPuestoAgr = concursoPuestoAgrHome.getInstance();
		validarOee();
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
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

		if (!existenActivos()) {
			declararDesierto();
			bloquearTodo = true;
			statusMessages
					.add(Severity.ERROR,
							"Este grupo se declara desierto por no contar con Puestos Activos.");
			return;
		}

		buscarSeleccionados();
		if (listaPostulantesSeleccionados.isEmpty()) {
			if (!existenElegibles()) {
				declararDesierto();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				return;
			} else {
				actualizarEstadosElegibles();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Debe gestionar elegibles desde la Bandeja de Excepciones.");
				return;
			}
		}

		vacancia = calcularVacancias();
		obtenerDireccionFisica();
	}

	private void actualizarEstadosElegibles() {
		try {
			Referencias refCancelado = new Referencias();
			refCancelado = referenciasUtilFormController.getReferencia(
					"ESTADOS_GRUPO", "GRUPO CANCELADO");
			concursoPuestoAgr.setActivo(false);
			concursoPuestoAgr.setEstado(refCancelado.getValorNum());
			concursoPuestoAgr.setFechaMod(new Date());
			concursoPuestoAgr.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concursoPuestoAgr);
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CIO_PUBLICAR_ADJUDICADOS);
			jbpmUtilFormController.setTransition("next");

			if (jbpmUtilFormController.nextTask())
				em.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
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

	/**
	 * Declara desierto en caso de que no existan postulantes
	 */
	private void declararDesierto() {
		estadoGrupoDesierto = evaluarDocPostulantesFormController
				.obtenerEstadoGrupo("ESTADOS_GRUPO", "GRUPO DESIERTO");
		/* Actualizar el estado del Grupo: */
		cambiarEstadoGrupo(concursoPuestoAgr);
		/* Actualizar el ESTADO de los PUESTOS: */
		cambiarEstadoPuestos(evaluarDocPostulantesFormController
				.traerGrupoPuestoDet(concursoPuestoAgr.getIdConcursoPuestoAgr()));

		// Se pasa a la siguiente tarea
		try {
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CIO_PUBLICAR_ADJUDICADOS);
			jbpmUtilFormController.setTransition("next");

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
	private void cambiarEstadoGrupo(ConcursoPuestoAgr grupo) {
		grupo.setEstado(estadoGrupoDesierto);
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
				.obtenerEstadosVarios("EVALUACION ADJUDICADOS", "CONCURSO");
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

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOeeCIO(
				null,
				concursoPuestoAgr.getIdConcursoPuestoAgr(),
				seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO",
						"EVALUACION ADJUDICADOS") + "",
				ActividadEnum.CIO_PUBLICAR_ADJUDICADOS.getValor());
	}

	public void init2() {
		concurso = new Concurso();
		configuracionUoCab = new ConfiguracionUoCab();
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		concurso = concursoPuestoAgr.getConcurso();
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
		buscarSeleccionados();

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
	public void buscarSeleccionados() {
		String sql = "select eval.* "
				+ "from seleccion.eval_referencial_postulante eval "
				+ "join seleccion.postulacion post  "
				+ "on post.id_postulacion = eval.id_postulacion "
				+ "join seleccion.persona_postulante persona_post "
				+ "on post.id_persona_postulante = persona_post.id_persona_postulante "
				+ "where eval.sel_adjudicado is true "
				+ "and eval.activo is true  " + "and post.activo is true "
				+ "and eval.id_excepcion is null "
				+ "and (eval.excluido is null or eval.excluido is false) "
				+ "and eval.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " order by persona_post.nombres, persona_post.apellidos";
		try {
			listaPostulantesSeleccionados = new ArrayList<EvalReferencialPostulante>();
			listaPostulantesSeleccionados = em.createNativeQuery(sql,
					EvalReferencialPostulante.class).getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private Integer calcularVacancias() {
		String sql = "SELECT COUNT(*) " + "FROM seleccion.concurso_puesto_det "
				+ "WHERE id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ "AND ACTIVO = TRUE";
		Object config = em.createNativeQuery(sql).getSingleResult();
		if (config == null)
			return 0;
		return new Integer(config.toString());
	}

	/**
	 * Obtiene la direccion fisica a ser usada en la adjuncion de documentos
	 */
	private void obtenerDireccionFisica() {
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
				+ concurso.getConfiguracionUoCab().getIdConfiguracionUo()
				+ separador
				+ concurso.getDatosEspecificosTipoConc()
						.getIdDatosEspecificos() + separador
				+ concurso.getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
	}

	private Boolean verificacion() {
		Integer cantAdj = listaPostulantesSeleccionados.size();
		if (cantAdj.intValue() == vacancia.intValue())
			return true;
		else
			return false;
	}

	public String publicar() {
		bloquearControlPuesto = true;
		bloquearTodo = false;
		if (!verificacion()) {
			bloquearControlPuesto = false;
			bloquearTodo = true;
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Cantidad de adjudicados no es igual a cantidad de puestos. Verificar en control Puesto/Postulante.");
			return null;
		}
		if (verificarPublicacion()) {
			bloquearTodo = true;
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"La Lista de Postulantes Seleccionados ya ha sido publicada anteriormente");
			return null;
		}
		/**
		 * Incluido por elegibles
		 */

		if (!existenActivos()) {
			declararDesierto();
			bloquearTodo = true;
			statusMessages
					.add(Severity.ERROR,
							"Este grupo se declara desierto por no contar con Puestos Activos.");
			return null;
		}
		buscarSeleccionados();
		if (!listaPostulantesSeleccionados.isEmpty()) {
			if (cantidadPuestosActivos.intValue() != listaPostulantesSeleccionados
					.size()) {
				bloquearControlPuesto = false;
				bloquearTodo = true;
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"‘Cantidad de Adjudicados no es igual a cantidad de puestos. Debe gestionar en Control Puesto/Postulante.");
				return null;
			}

		} else {
			if (!existenElegibles()) {
				declararDesierto();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Este grupo se declara desierto por no contar con Postulantes suficientes.");
				return null;
			} else {
				actualizarEstadosElegibles();
				bloquearTodo = true;
				statusMessages
						.add(Severity.ERROR,
								"Debe gestionar elegibles desde la Bandeja de Excepciones.");
				return null;
			}
		}

		buscarCargos();
		buscarPuestos();
		try {
			Referencias ref = new Referencias();
			ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
					"ADJUDICADO");
			if (ref != null) {
				concursoPuestoAgr.setEstado(ref.getValorNum());
				concursoPuestoAgr.setFechaEvalDocHasta(new Date());
				concursoPuestoAgr.setUsuMod(usuarioLogueado.getCodigoUsuario());
				concursoPuestoAgr.setFechaMod(new Date());
				em.merge(concursoPuestoAgr);
				EstadoDet estadoDet = new EstadoDet();
				estadoDet = seleccionUtilFormController.buscarEstadoDet(
						"CONCURSO", "ADJUDICADO");
				for (PlantaCargoDet cargo : listaCargos) {
					cargo.setEstadoCab(estadoDet.getEstadoCab());
					cargo.setEstadoDet(estadoDet);
					cargo.setUsuMod(usuarioLogueado.getCodigoUsuario());
					cargo.setFechaMod(new Date());
					em.merge(cargo);
				}

				for (ConcursoPuestoDet puesto : listaPuestos) {

					puesto.setEstadoDet(estadoDet);
					puesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
					puesto.setFechaMod(new Date());
					em.merge(puesto);
				}
				em.flush();
			}

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
			em.flush();

			Lista lista = new Lista();
			lista.setConcursoPuestoAgr(concursoPuestoAgr);
			lista.setFechaPublicacion(new Date());
			lista.setUsuPublicacion(usuarioLogueado.getCodigoUsuario());
			lista.setTipo("ADJUDICADO");
			em.persist(lista);
			em.flush();
			for (EvalReferencialPostulante seleccionado : listaPostulantesSeleccionados) {
				enviarEmails(seleccionado);
			}
			insertarTablaPublicacion();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return nextTask();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	private String generarTextoPublicacion() {
		StringBuffer texto = new StringBuffer();
		String hr = "<hr>";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fechaPublicacion = sdf.format(new Date()).toString();
		String spanO = "<span >";
		String spanC = "</span>";
		String br = "</br>";
		texto.append(hr + fechaPublicacion + br + spanO + "Las personas seleccionadas son: " + spanC + br);
		texto.append(spanO);
		texto.append("<a href='/sicca/seleccion/verPostulacion/verPostulacionPortal.seam?imprimirCU=CU_161&#38;idConcursoPuestoAgr="
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ "'>Lista de seleccionados</a>");
		texto.append(spanC + br);
		return texto.toString();
	}

	private void insertarTablaPublicacion() {
		String textoHtml = generarTextoPublicacion();
		PublicacionPortal entity = new PublicacionPortal();
		entity.setConcurso(concurso);
		entity.setConcursoPuestoAgr(concursoPuestoAgr);
		entity.setTexto(textoHtml);
		entity.setFechaAlta(new Date());
		entity.setActivo(true);
		entity.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		em.persist(entity);
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
				+ " and cargo.activo is true "
				+ "and puesto_det.activo is true";
		try {
			listaCargos = new ArrayList<PlantaCargoDet>();
			listaCargos = em.createNativeQuery(sql, PlantaCargoDet.class)
					.getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	private void buscarPuestos() {
		String sql = "select det.* "
				+ "from seleccion.concurso_puesto_det det "
				+ "where det.activo is true  "
				+ "and id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		try {
			listaPuestos = new ArrayList<ConcursoPuestoDet>();
			listaPuestos = em.createNativeQuery(sql, ConcursoPuestoDet.class)
					.getResultList();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	public void enviarEmails(EvalReferencialPostulante referencial) {
		PersonaPostulante persona = new PersonaPostulante();
		persona = referencial.getPostulacion().getPersonaPostulante();
		String dirEmail = persona.getEMail();

		String asunto = null;
		asunto = " SICCA - Lista de Seleccionados";
		String texto = "";

		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a    "
					+ persona.getNombres()
					+ " "
					+ persona.getApellidos()
					+ "</p> "
					+ "</b> <p align=\"justify\"> Le comunicamos que ha sido seleccionado/a para cubrir el Puesto de: <b>"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "</b>, del Concurso: </p> "
					+ "<p> <b>"
					+ concurso.getNumero()
					+ "/"
					+ concurso.getAnhio()
					+ " de "
					+ configuracionUoCab.getDescripcionCorta()
					+ " - "
					+ concurso.getNombre()
					+ "</b> </p> "
					+ "<p align=\"center\"> <b> ¡¡¡FELICITACIONES!!!    </p> </b>"
					+ "</b> </p> "
					+ "<p align=\"justify\">Para mas informaci&oacute;n, consultar la web del Portal haciendo click <a href="
					+ "http://www.paraguayconcursa.gov.py/"
					+ "> aqui </a></p> " + "<p> Atentamente,</p> "
					+ "<p> SICCA</p> ";

			usuarioPortalFormController.enviarMails(dirEmail, texto, asunto,
					null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Boolean verificarPublicacion() {
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
				"ADJUDICADO");
		if (ref != null) {
			if (concursoPuestoAgr.getEstado().equals(ref.getValorNum()))
				return true;
		}
		return false;
	}

	public String nextTask() {
		if (!verificarPublicacion()) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Debe publicar la Lista de Postulantes Seleccionados antes de pasar a la siguiente tarea");
			return null;
		}
		try {
			// Se pasa a la siguiente tarea
			jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
			jbpmUtilFormController
					.setActividadActual(ActividadEnum.CIO_PUBLICAR_ADJUDICADOS);
			jbpmUtilFormController
					.setActividadSiguiente(ActividadEnum.CIO_ELABORAR_RESOLUCION_ADJUDICACION);
			jbpmUtilFormController.setTransition("next");

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return "next";
	}

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void print() {
		Connection conn = null;
		try {
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado == null ? "PORTAL"
					: usuarioLogueado.getCodigoUsuario());
			param.put("concurso", codConcurso + " - " + concurso.getNombre());
			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());
			param.put(
					"entidad",
					sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNomabre());
			param.put("oee", configuracionUoCab.getOrden() + " - "
					+ configuracionUoCab.getDenominacionUnidad());
			param.put("grupo", concursoPuestoAgr.getDescripcionGrupo());
			param.put("id", concursoPuestoAgr.getIdConcursoPuestoAgr());
			HistorialActividadesGrupo historial = obtenerFechaActividad(concursoPuestoAgr.getIdConcursoPuestoAgr(), ActividadEnum.PUBLICAR_ADJUDICADOS.getDescripcion());
			if (historial==null || historial.getFechaFin()==null)
				param.put("fecha", "Aun no se ha publicado");
			else{
				Format formato = new SimpleDateFormat("dd-MM-yyyy");
				param.put("fecha", formato.format(historial.getFechaFin()));
			}

			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF(
					"RPT_CU161_Lista_Postulantes_Seleccionados", param, false,
					conn, usuarioLogueado);
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		} finally {
			if (conn != null)
				JpaResourceBean.closeConnection(conn);
		}

	}
	
	private HistorialActividadesGrupo obtenerFechaActividad(Long idGrupo, String descripcionActividad) {
		String sql = "SELECT * FROM seleccion.historial_actividades_grupo hist " 
				+ "WHERE hist.descripcion = '" + descripcionActividad
				+ "' and hist.id_concurso_puesto_agr = "	+ idGrupo.toString()
				+ " and hist.fecha_fin is not null";
		
		HistorialActividadesGrupo resultado;
		try {
			resultado = (HistorialActividadesGrupo) em.createNativeQuery(sql, HistorialActividadesGrupo.class).getSingleResult();
		} catch (Exception e) {
			//El que llama este metodo tiene que verificar el valor de retorno.
			resultado = null;
		}
		return resultado;
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

	public List<EvalReferencialPostulante> getListaPostulantesSeleccionados() {
		return listaPostulantesSeleccionados;
	}

	public void setListaPostulantesSeleccionados(
			List<EvalReferencialPostulante> listaPostulantesSeleccionados) {
		this.listaPostulantesSeleccionados = listaPostulantesSeleccionados;
	}

	public Integer getVacancia() {
		return vacancia;
	}

	public void setVacancia(Integer vacancia) {
		this.vacancia = vacancia;
	}

	public List<PlantaCargoDet> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<PlantaCargoDet> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public Usuario getUsuarioLogueado() {
		return usuarioLogueado;
	}

	public void setUsuarioLogueado(Usuario usuarioLogueado) {
		this.usuarioLogueado = usuarioLogueado;
	}

	public ConcursoPuestoAgrHome getConcursoPuestoAgrHome() {
		return concursoPuestoAgrHome;
	}

	public void setConcursoPuestoAgrHome(
			ConcursoPuestoAgrHome concursoPuestoAgrHome) {
		this.concursoPuestoAgrHome = concursoPuestoAgrHome;
	}

	public List<ConcursoPuestoDet> getListaPuestos() {
		return listaPuestos;
	}

	public void setListaPuestos(List<ConcursoPuestoDet> listaPuestos) {
		this.listaPuestos = listaPuestos;
	}

	public Boolean getBloquearTodo() {
		return bloquearTodo;
	}

	public void setBloquearTodo(Boolean bloquearTodo) {
		this.bloquearTodo = bloquearTodo;
	}

	public Integer getEstadoGrupoDesierto() {
		return estadoGrupoDesierto;
	}

	public void setEstadoGrupoDesierto(Integer estadoGrupoDesierto) {
		this.estadoGrupoDesierto = estadoGrupoDesierto;
	}

	public Boolean getBloquearControlPuesto() {
		return bloquearControlPuesto;
	}

	public void setBloquearControlPuesto(Boolean bloquearControlPuesto) {
		this.bloquearControlPuesto = bloquearControlPuesto;
	}

	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	public Integer getCantidadPuestosActivos() {
		return cantidadPuestosActivos;
	}

	public void setCantidadPuestosActivos(Integer cantidadPuestosActivos) {
		this.cantidadPuestosActivos = cantidadPuestosActivos;
	}

}
