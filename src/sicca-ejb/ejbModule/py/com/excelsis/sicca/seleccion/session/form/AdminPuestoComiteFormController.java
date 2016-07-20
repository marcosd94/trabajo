package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.CU226DTO;
import py.com.excelsis.sicca.util.CU229DTO;
import py.com.excelsis.sicca.util.CU230DTO;
import py.com.excelsis.sicca.util.CU231DTO;
import py.com.excelsis.sicca.util.CU232DTO;
import py.com.excelsis.sicca.util.ContenFuncionalDTO;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("admPuestoComiFormController")
public class AdminPuestoComiteFormController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -914426695961659122L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	GrupoPuestosController grupoPuestosController;
	// Link funcion (cu225)
	private List<ContenFuncionalDTO> lContenFuncional;
	// Lista cu231
	private List<CU231DTO> lCU231;
	// Lista cu232
	private List<CU232DTO> lCU232;
	// Lista cu230
	private List<CU230DTO> lCU230;
	// Lista cu229
	private List<CU229DTO> lCU229;
	// Lista cu226
	private List<CU226DTO> lCU226;
	private static String TIPO_PUESTO = "PUESTO";
	private static String TIPO_GRUPO = "GRUPO";
	private static String LINK_225 = "LINK_225";
	private static String LINK_230 = "LINK_230";
	private static String LINK_231 = "LINK_231";
	private static String LINK_232 = "LINK_232";
	private static String LINK_229 = "LINK_229";
	private static String LINK_226 = "LINK_226";
	private Boolean putMassiveSeleccionarTodosOn = false;
	// Esta variable contiene las acciones que todavia no fueron realizadas
	private Map<String, String> mapaAccioneNoRealizadas;
	private List<String> lFalta;
	private String current = null;
	private String from;

	private Boolean showPanelLinkFunciones = false;
	private Boolean showPanelLink230 = false;
	private Boolean showPanelLink231 = false;
	private Boolean showPanelLink232 = false;
	private Boolean showPanelLink229 = false;
	private Boolean showPanelLink226 = false;
	private SeguridadUtilFormController seguridadUtilFormController;

	private boolean modoLectura = false;

	public void seleccionarTodos() {
		if (showPanelLinkFunciones || putMassiveSeleccionarTodosOn) {
			for (ContenFuncionalDTO o : lContenFuncional) {
				o.setSelected(true);
			}
		}
		if (showPanelLink230 || putMassiveSeleccionarTodosOn) {
			for (CU230DTO o : lCU230) {
				o.setSelected(true);
			}
		}
		if (showPanelLink231 || putMassiveSeleccionarTodosOn) {
			for (CU231DTO o : lCU231) {
				o.setSelected(true);
			}
		}
		if (showPanelLink232 || putMassiveSeleccionarTodosOn) {
			for (CU232DTO o : lCU232) {
				o.setSelected(true);
			}
		}
		if (showPanelLink229 || putMassiveSeleccionarTodosOn) {
			for (CU229DTO o : lCU229) {
				if (!esPermanente(o.getDet()))
					o.setSelected(true);
			}
		}

		if (showPanelLink226 || putMassiveSeleccionarTodosOn) {
			Iterator iter = null;
			for (CU226DTO o : lCU226) {
				iter = o.getDet().getDetMinimosRequeridoses().iterator();
				while (iter.hasNext()) {
					DetMinimosRequeridos detMin = (DetMinimosRequeridos) iter
							.next();
					detMin.setSelected(true);
				}
				iter = o.getDet().getDetOpcionesConvenienteses().iterator();
				while (iter.hasNext()) {
					DetOpcionesConvenientes detMin = (DetOpcionesConvenientes) iter
							.next();
					detMin.setSelected(true);
				}
			}
		}
	}

	public Boolean esPermanente(PuestoConceptoPago puestoConceptoPago) {
		if (puestoConceptoPago != null) {
			puestoConceptoPago = em.find(PuestoConceptoPago.class,
					puestoConceptoPago.getIdPuestoConceptoPago());
			if (puestoConceptoPago.getPlantaCargoDet() != null
					&& puestoConceptoPago.getPlantaCargoDet().getPermanente() != null
					&& puestoConceptoPago.getPlantaCargoDet().getPermanente()) {
				return true;
			}
			if (puestoConceptoPago.getPlantaCargoDet() != null
					&& puestoConceptoPago.getPlantaCargoDet().getContratado() != null
					&& puestoConceptoPago.getPlantaCargoDet().getContratado()) {
				return true;
			}
		}
		return false;

	}

	public void cancelar() {
		current = null;
		showPanelLinkFunciones = false;
		showPanelLink230 = false;
		showPanelLink231 = false;
		showPanelLink232 = false;
		showPanelLink229 = false;
		showPanelLink226 = false;
		putMassiveSeleccionarTodosOn = false;
		limpiarListas();
	}

	private DetReqMin crearDetReqMin226(DetReqMin detReqMinOriginal) {
		DetReqMin detReqMin = new DetReqMin();
		detReqMin.setConcursoPuestoAgr(new ConcursoPuestoAgr());
		detReqMin.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
				grupoPuestosController.getIdConcursoPuestoAgr());
		detReqMin.setPuntajeReqMin(detReqMinOriginal.getPuntajeReqMin());
		detReqMin.setRequisitoMinimoCpt(new RequisitoMinimoCpt());
		detReqMin.getRequisitoMinimoCpt().setIdRequisitoMinimoCpt(
				detReqMinOriginal.getRequisitoMinimoCpt()
						.getIdRequisitoMinimoCpt());
		detReqMin.setTipo(TIPO_GRUPO);
		detReqMin.setActivo(true);
		em.persist(detReqMin);
		return detReqMin;
	}

	private void save226() {
		Iterator iter = null;
		// DetReqMin detReqMin = new DetReqMin();
		Map<String, Long> mapaCabDetReqMin = new HashMap<String, Long>();

		for (CU226DTO o : lCU226) {
			// DetMinimosRequeridos

			iter = o.getDet().getDetMinimosRequeridoses().iterator();
			while (iter.hasNext()) {
				DetMinimosRequeridos det = (DetMinimosRequeridos) iter.next();

				if (det.isSelected()) {
					String elKey = det.getDetReqMin().getIdDetReqMin()
							.toString();
					if (mapaCabDetReqMin.get(elKey) == null) {
						mapaCabDetReqMin.put(elKey,
								crearDetReqMin226(o.getDet()).getIdDetReqMin());
					}

					// Se crea DetMinimosRequeridos
					DetMinimosRequeridos detMinimosRequeridos = new DetMinimosRequeridos();
					detMinimosRequeridos.setActivo(true);
					detMinimosRequeridos.setDetReqMin(new DetReqMin());
					detMinimosRequeridos.getDetReqMin().setIdDetReqMin(
							mapaCabDetReqMin.get(elKey));
					detMinimosRequeridos.setMinimosRequeridos(det
							.getMinimosRequeridos());
					em.persist(detMinimosRequeridos);
				}
			}
			// DetOpcionesConvenientes
			iter = o.getDet().getDetOpcionesConvenienteses().iterator();
			while (iter.hasNext()) {
				DetOpcionesConvenientes det = (DetOpcionesConvenientes) iter
						.next();
				if (det.isSelected()) {
					String elKey = det.getDetReqMin().getIdDetReqMin()
							.toString();
					if (mapaCabDetReqMin.get(elKey) == null) {
						mapaCabDetReqMin.put(elKey,
								crearDetReqMin226(o.getDet()).getIdDetReqMin());
					}

					// Se crea DetMinimosRequeridos
					DetOpcionesConvenientes detOpcionesConvenientes = new DetOpcionesConvenientes();
					detOpcionesConvenientes.setActivo(true);
					detOpcionesConvenientes.setDetReqMin(new DetReqMin());
					detOpcionesConvenientes.getDetReqMin().setIdDetReqMin(
							mapaCabDetReqMin.get(elKey));
					detOpcionesConvenientes.setOpcConvenientes(det
							.getOpcConvenientes());
					em.persist(detOpcionesConvenientes);
				}
			}
			// em.flush();
		}
		// em.flush();
	}

	private void save229() {
		Date fecha = new Date();
		for (CU229DTO o : lCU229) {
			if (o.getSelected()) {
				GrupoConceptoPago elDet = new GrupoConceptoPago();
				elDet.setAnho(o.getDet().getAnho());
				elDet.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				elDet.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
						grupoPuestosController.getIdConcursoPuestoAgr());
				elDet.setCategoria(o.getDet().getCategoria());
				elDet.setFechaAlta(fecha);
				elDet.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				elDet.setMonto(o.getDet().getMonto());
				elDet.setObjCodigo(o.getDet().getObjCodigo());
				elDet.setTipo(TIPO_GRUPO);
				elDet.setActivo(true);
				em.persist(elDet);
			}
		}
		// em.flush();
	}

	private void save232() {
		for (CU232DTO o : lCU232) {
			if (o.getSelected()) {
				DetCondicionSegur elDet = new DetCondicionSegur();
				elDet.setActivo(true);
				elDet.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				elDet.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
						grupoPuestosController.getIdConcursoPuestoAgr());
				elDet.setCondicionSegur((new CondicionSegur()));
				elDet.getCondicionSegur().setIdCondicionSegur(
						o.getDet().getCondicionSegur().getIdCondicionSegur());
				elDet.setPuntajeCondSegur(o.getDet().getPuntajeCondSegur());
				elDet.setTipo(TIPO_GRUPO);
				elDet.setAjustes(o.getDet().getAjustes());
				elDet.setJustificacion(o.getDet().getJustificacion());
				em.persist(elDet);
			}
		}
	}

	private void save231() {
		for (CU231DTO o : lCU231) {
			if (o.getSelected()) {
				DetCondicionTrabajoEspecif elDet = new DetCondicionTrabajoEspecif();
				elDet.setActivo(true);
				elDet.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				elDet.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
						grupoPuestosController.getIdConcursoPuestoAgr());
				elDet.setCondicionTrabajoEspecif((new CondicionTrabajoEspecif()));
				elDet.getCondicionTrabajoEspecif()
						.setIdCondicionesTrabajoEspecif(
								o.getDet().getCondicionTrabajoEspecif()
										.getIdCondicionesTrabajoEspecif());
				elDet.setPuntajeCondTrabEspecif(o.getDet()
						.getPuntajeCondTrabEspecif());
				elDet.setTipo(TIPO_GRUPO);
				elDet.setAjustes(o.getDet().getAjustes());
				elDet.setJustificacion(o.getDet().getJustificacion());
				em.persist(elDet);
			}
		}
	}

	private void save230() {
		for (CU230DTO o : lCU230) {
			if (o.getSelected()) {
				DetCondicionTrabajo elDet = new DetCondicionTrabajo();
				elDet.setActivo(true);
				elDet.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				elDet.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
						grupoPuestosController.getIdConcursoPuestoAgr());
				elDet.setCondicionTrabajo(new CondicionTrabajo());
				elDet.getCondicionTrabajo().setIdCondicionTrabajo(
						o.getDet().getCondicionTrabajo()
								.getIdCondicionTrabajo());
				elDet.setPuntajeCondTrab(o.getDet().getPuntajeCondTrab());
				elDet.setTipo(TIPO_GRUPO);
				em.persist(elDet);
			}
		}
	}

	private void save225() {
		Date fecha = new Date();
		Map<String, List<DetDescripcionContFuncional>> mapaDet = new HashMap<String, List<DetDescripcionContFuncional>>();
		// El key = IdDetContenidoFuncional#IdContenidoFuncional#Puntaje
		String elKey = null;

		String cSeparador = "#";
		// Se agrupan los elementos a ser incertados
		for (ContenFuncionalDTO o : lContenFuncional) {
			if (o.getMostrarAcciones())
				if (o.getSelected()) {
					elKey = o.getDetDesc().getDetContenidoFuncional()
							.getIdDetContenidoFuncional().toString()
							+ cSeparador
							+ o.getDetDesc().getDetContenidoFuncional()
									.getContenidoFuncional()
									.getIdContenidoFuncional()
							+ cSeparador
							+ o.getDetDesc().getDetContenidoFuncional()
									.getPuntaje();
					if (!mapaDet.keySet().contains(elKey)) {
						mapaDet.put(elKey,
								new ArrayList<DetDescripcionContFuncional>());
						mapaDet.get(elKey).add(o.getDetDesc());

					} else {
						mapaDet.get(elKey).add(o.getDetDesc());
					}
				}
		}
		// Se recorre el mapa para hacer la persistencia
		String[] compos = null;
		for (String o : mapaDet.keySet()) {
			if (true) {
				// truco
				/*
				 * El key = IdDetContenidoFuncional#IdContenidoFuncional#Puntaje
				 */
				compos = o.split("#");
				DetContenidoFuncional elDet = new DetContenidoFuncional();
				elDet.setTipo(TIPO_GRUPO);
				elDet.setConcursoPuestoAgr(new ConcursoPuestoAgr());
				elDet.getConcursoPuestoAgr().setIdConcursoPuestoAgr(
						grupoPuestosController.getIdConcursoPuestoAgr());
				elDet.setContenidoFuncional(new ContenidoFuncional());
				elDet.getContenidoFuncional().setIdContenidoFuncional(
						new Long(compos[1]));
				elDet.setPuntaje(new Float(compos[2]));
				elDet.setActivo(true);
				em.persist(elDet);
				for (DetDescripcionContFuncional p : mapaDet.get(o)) {
					DetDescripcionContFuncional elDetDet = new DetDescripcionContFuncional();
					elDetDet.setActivo(true);
					elDetDet.setDescripcion(p.getDescripcion());
					elDetDet.setDetContenidoFuncional(new DetContenidoFuncional());
					elDetDet.getDetContenidoFuncional()
							.setIdDetContenidoFuncional(
									elDet.getIdDetContenidoFuncional());
					em.persist(elDetDet);

				}

			}
		}
		// em.flush();
	}

	public List<String> volver() {
		if (lFalta != null) {
			lFalta.clear();
		} else {
			lFalta = new ArrayList<String>();
		}
		lFalta = new ArrayList<String>();
		if (mapaAccioneNoRealizadas.keySet().size() > 0) {
			for (String o : mapaAccioneNoRealizadas.keySet()) {
				lFalta.add(mapaAccioneNoRealizadas.get(o));
			}
		}
		return lFalta;
	}

	/******* Valida que no exista en la bd datos para ese grupo para poder insertar *************/
	private Boolean cuentaConFunciones() {
		Query q = em
				.createQuery("select det from DetContenidoFuncional det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (q.getResultList().isEmpty())
			return false;
		return true;

	}

	private Boolean cuentaConRequisitos() {
		Query q = em
				.createQuery("select det from DetReqMin det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (q.getResultList().isEmpty())
			return false;
		return true;

	}

	private Boolean cuentaConCondicionGral() {
		Query q = em
				.createQuery("select det from DetCondicionTrabajo det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (q.getResultList().isEmpty())
			return false;
		return true;
	}

	private Boolean cuentaConCondicionEsp() {
		Query q = em
				.createQuery("select det from DetCondicionTrabajoEspecif det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (q.getResultList().isEmpty())
			return false;
		return true;
	}

	private Boolean cuentaConCondicionSeguridad() {
		Query q = em
				.createQuery("select det from DetCondicionSegur det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo",
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (q.getResultList().isEmpty())
			return false;
		return true;
	}

	@Transactional
	public String saveTodo() {
		try {
			// Se cargan las listas
			cargar226();
			cargar229();
			cargar230();
			cargar231();
			cargar232();
			cargarFunciones();
			// Se selecciona Todos de cada uno
			putMassiveSeleccionarTodosOn = true;
			seleccionarTodos();
			// Se guardan
			if (!cuentaConFunciones())
				save225();
			if (!cuentaConRequisitos())
				save226();
			save229();
			if (!cuentaConCondicionGral())
				save230();
			if (!cuentaConCondicionEsp())
				save231();
			if (!cuentaConCondicionSeguridad())
				save232();
			em.flush();
			statusMessages.clear();
			statusMessages
					.add(Severity.INFO,
							"Fueron copiados todos los datos del puesto. Ir a Ver y Editar Bases y Condiciones");
			cancelar();
			// Se limpia el mapa puesto que todas las acciones fuern realizadas
			mapaAccioneNoRealizadas.clear();
			return "persisted";
		} catch (Exception ex) {
			cancelar();
			ex.printStackTrace();
			statusMessages.add(Severity.ERROR, ex.getMessage());
			return null;
		}
	}

	@Transactional
	public String save() {
		if (showPanelLinkFunciones) {
			try {
				save225();
				em.flush();
				mapaAccioneNoRealizadas.remove(LINK_225);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				cancelar();
				return "persisted";
			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		if (showPanelLink230) {
			try {
				save230();
				em.flush();
				mapaAccioneNoRealizadas.remove(LINK_230);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				cancelar();
				return "persisted";
			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		if (showPanelLink231) {
			try {
				save231();
				em.flush();
				mapaAccioneNoRealizadas.remove(LINK_231);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				cancelar();
				return "persisted";
			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		if (showPanelLink232) {
			try {
				save232();
				em.flush();
				mapaAccioneNoRealizadas.remove(LINK_232);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				cancelar();
				return "persisted";
			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		if (showPanelLink229) {
			try {
				save229();
				em.flush();
				mapaAccioneNoRealizadas.remove(LINK_229);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				cancelar();
				return "persisted";
			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}
		if (showPanelLink226) {
			try {
				save226();
				em.flush();
				mapaAccioneNoRealizadas.remove(LINK_226);
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
				cancelar();
				return "persisted";
			} catch (Exception ex) {
				ex.printStackTrace();
				statusMessages.add(Severity.ERROR, ex.getMessage());
				return null;
			}
		}

		cancelar();
		return "persited";
	}

	public void limpiarListas() {
		if (lContenFuncional != null) {
			lContenFuncional.clear();
		}
		if (lCU230 != null) {
			lCU230.clear();
		}
		if (lCU231 != null) {
			lCU231.clear();
		}
		if (lCU232 != null) {
			lCU232.clear();
		}
		if (lCU229 != null) {
			lCU229.clear();
		}
		if (lCU226 != null) {
			lCU226.clear();
		}
	}

	public void updateCurrent() {
		modoLectura = false;
		if (current.equalsIgnoreCase(LINK_225)) {
			cancelar();
			current = LINK_225;
			showPanelLinkFunciones = true;
			cargarFunciones();
			/**
			 * Incidencia 0002187
			 * */
			validarModoLecturaReqFunc();
			/** FIN */
		} else if (current.equalsIgnoreCase(LINK_230)) {
			cancelar();
			current = LINK_230;
			showPanelLink230 = true;
			cargar230();
			validarModoLecturaCondTrabajoGral();
		} else if (current.equalsIgnoreCase(LINK_231)) {
			cancelar();
			current = LINK_231;
			showPanelLink231 = true;
			cargar231();

			validarModoLecturaCondTrabajoEsp();
		} else if (current.equalsIgnoreCase(LINK_232)) {
			cancelar();
			current = LINK_232;
			showPanelLink232 = true;
			cargar232();
			validarModoLecturaCondSeguridad();
		} else if (current.equalsIgnoreCase(LINK_229)) {
			cancelar();
			current = LINK_229;
			showPanelLink229 = true;
			cargar229();
			modoLectura = true;
		} else if (current.equalsIgnoreCase(LINK_226)) {
			cancelar();
			current = LINK_226;
			showPanelLink226 = true;
			cargar226();
			cargar230();
			/**
			 * Incidencia 0002187
			 * */
			validarModoLecturaReqMin();
			/** FIN */
		}

	}

	public void cargar226() {
		if (lCU226 == null) {
			lCU226 = new ArrayList<CU226DTO>();
		} else {
			lCU226.clear();
		}
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(DetReqMin.class);
		Criteria critCab = crit.createCriteria("requisitoMinimoCpt", "cab");
		Criteria critPuesto = crit.createCriteria("plantaCargoDet", "puesto");
		crit.add(Restrictions.eq("cab.activo", true));
		crit.add(Restrictions.eq("puesto.idPlantaCargoDet",
				grupoPuestosController.getIdPuesto()));
		crit.addOrder(Order.asc("cab.idRequisitoMinimoCpt"));

		List<DetReqMin> lDet = crit.list();
		int ultIndice = 0;
		Iterator iter = null;
		for (DetReqMin o : lDet) {
			lCU226.add(new CU226DTO());
			ultIndice = lCU226.size() - 1;
			lCU226.get(ultIndice).setDet(o);
			// Inicializando DetMinimosRequeridos
			iter = o.getDetMinimosRequeridoses().iterator();
			while (iter.hasNext()) {
				DetMinimosRequeridos det = (DetMinimosRequeridos) iter.next();
				det.setSelected(false);
			}
			// Inicializando DetOpcionesConvenientes
			iter = o.getDetOpcionesConvenienteses().iterator();
			while (iter.hasNext()) {
				DetOpcionesConvenientes det = (DetOpcionesConvenientes) iter
						.next();
				det.setSelected(false);
			}

		}
	}

	public void cargar230() {
		if (lCU230 == null) {
			lCU230 = new ArrayList<CU230DTO>();
		} else {
			lCU230.clear();
		}
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(DetCondicionTrabajo.class);
		Criteria critCab = crit.createCriteria("condicionTrabajo", "cab");
		Criteria critPuesto = crit.createCriteria("plantaCargoDet", "puesto");
		crit.add(Restrictions.eq("cab.activo", true));
		crit.add(Restrictions.eq("puesto.idPlantaCargoDet",
				grupoPuestosController.getIdPuesto()));
		crit.addOrder(Order.asc("cab.idCondicionTrabajo"));

		List<DetCondicionTrabajo> lDet = crit.list();
		int ultIndice = 0;
		for (DetCondicionTrabajo o : lDet) {
			lCU230.add(new CU230DTO());
			ultIndice = lCU230.size() - 1;
			lCU230.get(ultIndice).setDet(o);
		}
	}

	public void cargar229() {
		if (lCU229 == null) {
			lCU229 = new ArrayList<CU229DTO>();
		} else {
			lCU229.clear();
		}
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(PuestoConceptoPago.class);
		Criteria critPuesto = crit.createCriteria("plantaCargoDet", "puesto");
		crit.add(Restrictions.eq("puesto.idPlantaCargoDet",
				grupoPuestosController.getIdPuesto()));
		crit.addOrder(Order.asc("objCodigo"));

		List<PuestoConceptoPago> lDet = crit.list();
		int ultIndice = 0;
		for (PuestoConceptoPago o : lDet) {
			lCU229.add(new CU229DTO());
			ultIndice = lCU229.size() - 1;
			lCU229.get(ultIndice).setDet(o);
		}
	}

	public void cargar232() {
		if (lCU232 == null) {
			lCU232 = new ArrayList<CU232DTO>();
		} else {
			lCU232.clear();
		}
		Session session = jpaResourceBean.getSession();
		Criteria crit = session.createCriteria(DetCondicionSegur.class);
		Criteria critCab = crit.createCriteria("condicionSegur", "cab");
		Criteria critPuesto = crit.createCriteria("plantaCargoDet", "puesto");
		crit.add(Restrictions.eq("cab.activo", true));
		crit.add(Restrictions.eq("puesto.idPlantaCargoDet",
				grupoPuestosController.getIdPuesto()));
		crit.addOrder(Order.asc("cab.idCondicionSegur"));

		List<DetCondicionSegur> lDet = crit.list();
		int ultIndice = 0;
		for (DetCondicionSegur o : lDet) {
			lCU232.add(new CU232DTO());
			ultIndice = lCU232.size() - 1;
			lCU232.get(ultIndice).setDet(o);
		}
	}

	public void cargar231() {
		if (lCU231 == null) {
			lCU231 = new ArrayList<CU231DTO>();
		} else {
			lCU231.clear();
		}
		Session session = jpaResourceBean.getSession();
		Criteria crit = session
				.createCriteria(DetCondicionTrabajoEspecif.class);
		Criteria critCab = crit
				.createCriteria("condicionTrabajoEspecif", "cab");
		Criteria critPuesto = crit.createCriteria("plantaCargoDet", "puesto");
		crit.add(Restrictions.eq("cab.activo", true));
		crit.add(Restrictions.eq("puesto.idPlantaCargoDet",
				grupoPuestosController.getIdPuesto()));
		crit.addOrder(Order.asc("cab.idCondicionesTrabajoEspecif"));

		List<DetCondicionTrabajoEspecif> lDet = crit.list();
		int ultIndice = 0;
		for (DetCondicionTrabajoEspecif o : lDet) {
			lCU231.add(new CU231DTO());
			ultIndice = lCU231.size() - 1;
			lCU231.get(ultIndice).setDet(o);
		}
	}

	public void cargarFunciones() {
		if (lContenFuncional == null) {
			lContenFuncional = new ArrayList<ContenFuncionalDTO>();
		} else {
			lContenFuncional.clear();
		}
		Session session = jpaResourceBean.getSession();
		Criteria crit = session
				.createCriteria(DetDescripcionContFuncional.class);
		Criteria critDet = crit.createCriteria("detContenidoFuncional", "det");
		Criteria critCab = critDet.createCriteria("contenidoFuncional", "cab");
		Criteria critPuesto = critDet
				.createCriteria("plantaCargoDet", "puesto");

		crit.add(Restrictions.eq("det.tipo", TIPO_PUESTO));
		crit.add(Restrictions.eq("puesto.idPlantaCargoDet",
				grupoPuestosController.getIdPuesto()));
		crit.add(Restrictions.eq("cab.activo", true));
		crit.add(Restrictions.eq("activo", true));
		List<DetDescripcionContFuncional> lDetDet = crit.list();
		int ultIndice = 0;
		// Permite meter un det por vez con todos los datos necesarios para
		// mostrar
		List<String> lDetProcesados = new ArrayList<String>();
		for (DetDescripcionContFuncional o : lDetDet) {
			if (!lDetProcesados.contains(o.getDetContenidoFuncional()
					.getIdDetContenidoFuncional().toString())) {
				// Cabecera
				lContenFuncional.add(new ContenFuncionalDTO());
				ultIndice = lContenFuncional.size() - 1;
				lContenFuncional.get(ultIndice).setMostrarAcciones(false);
				lContenFuncional.get(ultIndice).setDetDesc(o);

				lDetProcesados.add(o.getDetContenidoFuncional()
						.getIdDetContenidoFuncional().toString());
			}
			// Detalle
			lContenFuncional.add(new ContenFuncionalDTO());
			ultIndice = lContenFuncional.size() - 1;
			lContenFuncional.get(ultIndice).setMostrarAcciones(true);
			lContenFuncional.get(ultIndice).setDetDesc(o);
		}
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}

		if (!seguridadUtilFormController
				.verificarPerteneceOee(grupoPuestosController
						.getConcursoPuestoAgr().getConcurso()
						.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
		if (grupoPuestosController.getPuesto() != null) {
			if (grupoPuestosController.getPuesto().getConfiguracionUoDet() != null
					&& grupoPuestosController.getPuesto()
							.getConfiguracionUoDet().getConfiguracionUoCab() != null)
				if (!seguridadUtilFormController
						.verificarPerteneceOee(grupoPuestosController
								.getPuesto().getConfiguracionUoDet()
								.getConfiguracionUoCab().getIdConfiguracionUo())) {
					throw new AuthorizationException(SeamResourceBundle
							.getBundle().getString("GENERICO_OEE_NO_VALIDA"));
				}
		}
	}

	public void init() {
		/* Incidencia 1014 */
		if (grupoPuestosController.getIdConcursoPuestoAgr() != null) {
			validarOee();

		}
		/**/
		mapaAccioneNoRealizadas = new HashMap<String, String>();
		if (mostarFunciones())
			mapaAccioneNoRealizadas.put(LINK_225, SeamResourceBundle
					.getBundle().getString("CU409_falta_link225"));
		if (mostrarRequisitos())
			mapaAccioneNoRealizadas.put(LINK_226, SeamResourceBundle
					.getBundle().getString("CU409_falta_link226"));
		if (mostrarTrabajoGral())
			mapaAccioneNoRealizadas.put(LINK_230, SeamResourceBundle
					.getBundle().getString("CU409_falta_link230"));
		if (mostrarTrabajoEsp())
			mapaAccioneNoRealizadas.put(LINK_231, SeamResourceBundle
					.getBundle().getString("CU409_falta_link231"));
		if (mostrarCondicionSeguridad())
			mapaAccioneNoRealizadas.put(LINK_232, SeamResourceBundle
					.getBundle().getString("CU409_falta_link232"));
	}

	private Boolean mostarFunciones() {
		Query q = em
				.createQuery("select det from DetContenidoFuncional det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetContenidoFuncional> l = q.getResultList();
		if (l.isEmpty())
			return true;
		return false;
	}

	private Boolean mostrarRequisitos() {
		Query q = em
				.createQuery("select det from DetReqMin det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetReqMin> l = q.getResultList();
		if (l.isEmpty())
			return true;
		return false;
	}

	private Boolean mostrarTrabajoGral() {
		Query q = em
				.createQuery("select det from DetCondicionTrabajo det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetCondicionTrabajo> l = q.getResultList();
		if (l.isEmpty())
			return true;
		return false;
	}

	private Boolean mostrarTrabajoEsp() {
		Query q = em
				.createQuery("select det from DetCondicionTrabajoEspecif det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetCondicionTrabajoEspecif> l = q.getResultList();
		if (l.isEmpty())
			return true;
		return false;
	}

	private Boolean mostrarCondicionSeguridad() {
		Query q = em
				.createQuery("select det from DetCondicionSegur det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetCondicionSegur> l = q.getResultList();
		if (l.isEmpty())
			return true;
		return false;
	}

	private void validarModoLecturaReqFunc() {
		modoLectura = false;
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (agr.getHomologacionPerfilMatriz() != null
				&& agr.getHomologar() != null && !agr.getHomologar())
			modoLectura = true;
		else
			modoLectura = buscarFuncion(agr.getIdConcursoPuestoAgr());
	}

	private Boolean buscarFuncion(Long id) {
		Query q = em
				.createQuery("select det from DetContenidoFuncional det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ id);
		List<DetContenidoFuncional> l = q.getResultList();
		if (l.isEmpty())
			return false;
		return true;
	}

	private void validarModoLecturaReqMin() {
		modoLectura = false;
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
				grupoPuestosController.getIdConcursoPuestoAgr());
		if (agr.getHomologacionPerfilMatriz() != null
				&& agr.getHomologar() != null && !agr.getHomologar())
			modoLectura = true;
		else
			modoLectura = buscarRegMin(agr.getIdConcursoPuestoAgr());
	}

	private Boolean buscarRegMin(Long id) {
		Query q = em
				.createQuery("select det from DetReqMin det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ id);
		List<DetReqMin> l = q.getResultList();
		if (l.isEmpty())
			return false;
		return true;
	}

	private void validarModoLecturaCondSeguridad() {
		modoLectura = false;
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
				grupoPuestosController.getIdConcursoPuestoAgr());
		modoLectura = buscarCondSeguridad(agr.getIdConcursoPuestoAgr());
	}

	private Boolean buscarCondSeguridad(Long id) {
		Query q = em
				.createQuery("select det from DetCondicionSegur det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ id);
		List<DetCondicionSegur> l = q.getResultList();
		if (l.isEmpty())
			return false;
		return true;
	}

	private void validarModoLecturaCondTrabajoEsp() {
		modoLectura = false;
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
				grupoPuestosController.getIdConcursoPuestoAgr());
		modoLectura = buscarCondTrabajoEsp(agr.getIdConcursoPuestoAgr());
	}

	private Boolean buscarCondTrabajoEsp(Long id) {
		Query q = em
				.createQuery("select det from DetCondicionTrabajoEspecif det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ id);
		List<DetCondicionTrabajoEspecif> l = q.getResultList();
		if (l.isEmpty())
			return false;
		return true;
	}

	private void validarModoLecturaCondTrabajoGral() {
		modoLectura = false;
		ConcursoPuestoAgr agr = em.find(ConcursoPuestoAgr.class,
				grupoPuestosController.getIdConcursoPuestoAgr());
		modoLectura = buscarCondTrabajoGral(agr.getIdConcursoPuestoAgr());
	}

	private Boolean buscarCondTrabajoGral(Long id) {
		Query q = em
				.createQuery("select det from DetCondicionTrabajo det "
						+ "  where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = "
						+ id);
		List<DetCondicionTrabajo> l = q.getResultList();
		if (l.isEmpty())
			return false;
		return true;
	}

	public List<ContenFuncionalDTO> getlContenFuncional() {
		return lContenFuncional;
	}

	public void setlContenFuncional(List<ContenFuncionalDTO> lContenFuncional) {
		this.lContenFuncional = lContenFuncional;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public Boolean getShowPanelLinkFunciones() {
		return showPanelLinkFunciones;
	}

	public void setShowPanelLinkFunciones(Boolean showPanelLinkFunciones) {
		this.showPanelLinkFunciones = showPanelLinkFunciones;
	}

	public List<CU230DTO> getlCU230() {
		return lCU230;
	}

	public void setlCU230(List<CU230DTO> lCU230) {
		this.lCU230 = lCU230;
	}

	public Boolean getShowPanelLink230() {
		return showPanelLink230;
	}

	public void setShowPanelLink230(Boolean showPanelLink230) {
		this.showPanelLink230 = showPanelLink230;
	}

	public List<CU231DTO> getlCU231() {
		return lCU231;
	}

	public void setlCU231(List<CU231DTO> lCU231) {
		this.lCU231 = lCU231;
	}

	public Boolean getShowPanelLink231() {
		return showPanelLink231;
	}

	public void setShowPanelLink231(Boolean showPanelLink231) {
		this.showPanelLink231 = showPanelLink231;
	}

	public List<CU232DTO> getlCU232() {
		return lCU232;
	}

	public void setlCU232(List<CU232DTO> lCU232) {
		this.lCU232 = lCU232;
	}

	public Boolean getShowPanelLink232() {
		return showPanelLink232;
	}

	public void setShowPanelLink232(Boolean showPanelLink232) {
		this.showPanelLink232 = showPanelLink232;
	}

	public List<CU229DTO> getlCU229() {
		return lCU229;
	}

	public void setlCU229(List<CU229DTO> lCU229) {
		this.lCU229 = lCU229;
	}

	public Boolean getShowPanelLink229() {
		return showPanelLink229;
	}

	public void setShowPanelLink229(Boolean showPanelLink229) {
		this.showPanelLink229 = showPanelLink229;
	}

	public Boolean getShowPanelLink226() {
		return showPanelLink226;
	}

	public void setShowPanelLink226(Boolean showPanelLink226) {
		this.showPanelLink226 = showPanelLink226;
	}

	public List<CU226DTO> getlCU226() {
		return lCU226;
	}

	public void setlCU226(List<CU226DTO> lCU226) {
		this.lCU226 = lCU226;
	}

	public List<String> getlFalta() {
		return lFalta;
	}

	public void setlFalta(List<String> lFalta) {
		this.lFalta = lFalta;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isModoLectura() {
		return modoLectura;
	}

	public void setModoLectura(boolean modoLectura) {
		this.modoLectura = modoLectura;
	}

}
