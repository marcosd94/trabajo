package py.com.excelsis.sicca.circuitoCIO.session.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.AuthorizationException;

import py.com.excelsis.sicca.entity.ComisionGrupo;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PromocionConcursoAgr;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConcursoHome;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.ConcursoPuestoDetList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import enums.ActividadEnum;
import enums.ProcesoEnum;

@Scope(ScopeType.CONVERSATION)
@Name("admGrupoCU615FC")
public class AdmGrupoCU615FC implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9382609022748967L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	ConcursoHome concursoHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	@In(create = true)
	ConcursoPuestoDetList concursoPuestoDetList;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(required = false, create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(required = false, create = true)
	private Actor actor;

	private Concurso concurso;
	private ConcursoPuestoAgr puestoAgr;
	private String codigo;
	private Integer orden;
	private String denominacion;
	private String observacion;
	private Long idNivelEntidad;
	private Long idSinEntidad;
	private Long idConfiguracionUoCab;
	private String operacion;
	private String ubicacionFisica;
	private String fromConcurso;
	private Long idAgr;
	private Integer nroActividad;

	private List<ConcursoPuestoDet> puestosSinAsignar = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoDet> puestosAsignados = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoDet> puestosAsignadosAux = new ArrayList<ConcursoPuestoDet>();
	private List<ConcursoPuestoAgr> puestosAgrupados = new ArrayList<ConcursoPuestoAgr>();
	private List<ConcursoPuestoAgr> puestosAgrupadosAux = new ArrayList<ConcursoPuestoAgr>();
	
	
	private List<PromocionConcursoAgr> PromocionSalarialSinAsignar = new ArrayList<PromocionConcursoAgr>();
	private List<PromocionConcursoAgr> PromocionSalarialAsignados = new ArrayList<PromocionConcursoAgr>();
	private List<PromocionConcursoAgr> PromocionSalarialAsignadosAux = new ArrayList<PromocionConcursoAgr>();
	private List<ConcursoPuestoAgr> PromocionSalarialAgrupados = new ArrayList<ConcursoPuestoAgr>();
	private List<ConcursoPuestoAgr> PromocionSalarialAgrupadosAux = new ArrayList<ConcursoPuestoAgr>();
	
	
	private boolean primeraEntrada = true;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Boolean mostrarModal = false;
	private static String CONCURSO_INTERNO_INTERINSTITUCIONAL = "CONCURSO INTERNO DE OPOSICION INTER INSTITUCIONAL";

	/**
	 * Método que inicializa los datos
	 */
	public void init() {
		asignarRolesTarea();
		puestoAgr = new ConcursoPuestoAgr();

		puestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		concurso = concursoHome.getInstance();
		/* Incidencia 1014 */
		validarOee();
		/**/
		orden = valorMaximoOrden() + 1;
		codigo = valorMaximoCodigo().toString();
		traerDatos();

		if (concursoPuestoAgrHome.isIdDefined() && primeraEntrada) {

			operacion = "updated";

			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separador = File.separator;
			ubicacionFisica = separador
					+ "SICCA"
					+ separador
					+ anho
					+ separador
					+ "OEE"
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos() + separador
					+ concurso.getIdConcurso() + separador
					+ puestoAgr.getIdConcursoPuestoAgr();

			codigo = puestoAgr.getCodGrupo();
			orden = puestoAgr.getNroOrden();
			denominacion = puestoAgr.getDescripcionGrupo();
			observacion = puestoAgr.getObservacion();
			puestosAgrupadosAux = new ArrayList<ConcursoPuestoAgr>();
			if (puestosAgrupados != null && puestosAgrupados.size() > 0)
				puestosAgrupadosAux.addAll(puestosAgrupados);
		}

		if (primeraEntrada)
			primeraEntrada = false;

	}
	
	
	
	
	public void initPromocionSalarial() {
		asignarRolesTarea();
		puestoAgr = new ConcursoPuestoAgr();

		puestoAgr = concursoPuestoAgrHome.getInstance();
		concurso = new Concurso();
		concurso = concursoHome.getInstance();
		/* Incidencia 1014 */
		validarOee();
		/**/
		orden = valorMaximoOrden() + 1;
		codigo = valorMaximoCodigo().toString();
		traerDatosPromocionSalarial();

		if (concursoPuestoAgrHome.isIdDefined() && primeraEntrada) {

			operacion = "updated";

			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			String separador = File.separator;
			ubicacionFisica = separador
					+ "SICCA"
					+ separador
					+ anho
					+ separador
					+ "OEE"
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos() + separador
					+ concurso.getIdConcurso() + separador
					+ puestoAgr.getIdConcursoPuestoAgr();

			codigo = puestoAgr.getCodGrupo();
			orden = puestoAgr.getNroOrden();
			denominacion = puestoAgr.getDescripcionGrupo();
			observacion = puestoAgr.getObservacion();
			PromocionSalarialAgrupadosAux = new ArrayList<ConcursoPuestoAgr>();
			if (PromocionSalarialAgrupados != null && PromocionSalarialAgrupados.size() > 0)
				PromocionSalarialAgrupadosAux.addAll(PromocionSalarialAgrupados);
		}

		if (primeraEntrada)
			primeraEntrada = false;

	}

	private void asignarRolesTarea() {
		roles = seleccionUtilFormController.getRolesTarea(
				ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO.getValor(),
				ProcesoEnum.CONCURSO.getValor());
	}

	private void validarOee() {
		/* Incidencia 1014 */
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController = (SeguridadUtilFormController) Component
					.getInstance(SeguridadUtilFormController.class, true);
		}
		if (concurso == null) {
			throw new javax.persistence.EntityNotFoundException();
		}
		if (puestoAgr != null && puestoAgr.getConcurso() != null) {
			if (!seguridadUtilFormController.verificarPerteneceOee(puestoAgr
					.getConcurso().getConfiguracionUoCab()
					.getIdConfiguracionUo())) {
				throw new AuthorizationException(SeamResourceBundle.getBundle()
						.getString("GENERICO_OEE_NO_VALIDA"));
			}
		}

		if (!seguridadUtilFormController.verificarPerteneceOee(concurso
				.getConfiguracionUoCab().getIdConfiguracionUo())) {
			throw new AuthorizationException(SeamResourceBundle.getBundle()
					.getString("GENERICO_OEE_NO_VALIDA"));
		}
		/**/
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> traerPuestosAsignados() {
		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		Long id = puestoAgr.getIdConcursoPuestoAgr();
		if (puestoAgr != null && puestoAgr.getIdConcursoPuestoAgr() != null) {
			String cadena = "select distinct(puesto_det.*) "
					+ "from seleccion.concurso_puesto_det puesto_det "
					+ "join seleccion.concurso_puesto_agr puesto_agr "
					+ "on puesto_agr.id_concurso_puesto_agr=puesto_det.id_concurso_puesto_agr "
					+ "join planificacion.planta_cargo_det cargo  "
					+ "on cargo.id_planta_cargo_det = puesto_det.id_planta_cargo_det "
					+ "where puesto_det.nro_orden = 3 "
					+ " and cargo.permanente is true";

			cadena = cadena + " and puesto_agr.id_concurso_puesto_agr = "
					+ puestoAgr.getIdConcursoPuestoAgr();

			lista = em.createNativeQuery(cadena, ConcursoPuestoDet.class)
					.getResultList();
			puestosAgrupados = new ArrayList<ConcursoPuestoAgr>();
			for (ConcursoPuestoDet o : lista) {
				puestosAgrupados.add(o.getConcursoPuestoAgr());
			}
		}

		return lista;
	}
	
	
	//METODO QUE RECUPERA LOS REGISTROS DE PROMOCION SALARIAL QUE HAN SIDO AGRUPADOS.
	@SuppressWarnings("unchecked")
	private List<PromocionConcursoAgr> traerPromocionConcursoAgrAsignados() {
		List<PromocionConcursoAgr> lista = new ArrayList<PromocionConcursoAgr>();
		Long id = puestoAgr.getIdConcursoPuestoAgr();
		if (puestoAgr != null && puestoAgr.getIdConcursoPuestoAgr() != null) {
			String cadena = "select distinct(puesto_det.*) "
					+ "from seleccion.promocion_concurso_agr puesto_det "
					+ "join seleccion.concurso_puesto_agr puesto_agr "
					+ "on puesto_agr.id_concurso_puesto_agr=puesto_det.id_concurso_puesto_agr "
					+ "join seleccion.promocion_salarial cargo  "
					+ "on cargo.id_promocion_salarial = puesto_det.id_promocion_salarial "
					+ "where puesto_det.nro_orden = 3 "
					//+ " and cargo.permanente is true"
					;

			cadena = cadena + " and puesto_agr.id_concurso_puesto_agr = "
					+ puestoAgr.getIdConcursoPuestoAgr();

			lista = em.createNativeQuery(cadena, PromocionConcursoAgr.class)
					.getResultList();
			PromocionSalarialAgrupados = new ArrayList<ConcursoPuestoAgr>();
			for (PromocionConcursoAgr o : lista) {
				PromocionSalarialAgrupados.add(o.getConcursoPuestoAgr());
			}
		}

		return lista;
	}

	private List<ConcursoPuestoDet> listaPuestosPosibles() {
		String select = " select distinct(puesto_det.*) "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
				+ "join planificacion.estado_cab  "
				+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab  "
				+ "join planificacion.planta_cargo_det cargo  "
				+ "on cargo.id_planta_cargo_det = puesto_det.id_planta_cargo_det "
				+ " where puesto_det.id_concurso_puesto_agr is null "
				+ "and lower(estado_det.descripcion) = 'en reserva' "
				+ "and lower(estado_cab.descripcion) = 'concurso' "
				+ "and puesto_det.id_concurso = " + concurso.getIdConcurso()
				+ " and cargo.permanente is true";

		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		lista = em.createNativeQuery(select, ConcursoPuestoDet.class)
				.getResultList();

		return lista;
	}
	
	
	//METODO QUE RECUPERA LOS REGISTROS DE PROMOCION SALARIAL SIN ASIGNAR
	private List<PromocionConcursoAgr> listaPromocionConcursoAgr() {
		String select = " select distinct(puesto_det.*) "
				+ "from seleccion.promocion_concurso_agr puesto_det "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det  "
				+ "join planificacion.estado_cab  "
				+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab  "
				+ "join seleccion.promocion_salarial cargo  "
				+ "on cargo.id_promocion_salarial = puesto_det.id_promocion_salarial "
				+ " where puesto_det.id_concurso_puesto_agr is null "
				+ "and lower(estado_det.descripcion) = 'en reserva' "
				+ "and lower(estado_cab.descripcion) = 'concurso' "
				//+ "and puesto_det.id_concurso = " + concurso.getIdConcurso()
				//+ " and cargo.permanente is true"
				;

		List<PromocionConcursoAgr> lista = new ArrayList<PromocionConcursoAgr>();
		lista = em.createNativeQuery(select, PromocionConcursoAgr.class)
				.getResultList();

		return lista;
	}


	private boolean validarPermanente(ConcursoPuestoDet det) {
		if (det.getConcurso().getDatosEspecificosTipoConc().getDescripcion()
				.equalsIgnoreCase(CONCURSO_INTERNO_INTERINSTITUCIONAL)) {
			if (cuentaConCpt(det.getPlantaCargoDet()))
				return true;
			return false;
		} else {
			if (cuentaConCpt(det.getPlantaCargoDet())) {
				if (estaEnPuestoConceptoPago(det))
					return true;
				return false;
			}
			return false;

		}
	}

	private boolean estaEnPuestoConceptoPago(ConcursoPuestoDet det) {
		Referencias referenciaNoRef = new Referencias();
		referenciaNoRef = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "RESERVADO");
		Referencias referenciaRef = new Referencias();
		referenciaRef = referenciasUtilFormController.getReferencia(
				"ESTADOS_CATEGORIA", "PENDIENTE");
		Query q = em.createQuery("select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true and pago.objCodigo = :cod "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  "
				+ " and pago.estado = :estado  ");
		q.setParameter("cod", new Integer(111));
		q.setParameter("id", det.getPlantaCargoDet().getIdPlantaCargoDet());
		if (concurso.getAdReferendum())
			q.setParameter("estado", referenciaRef.getValorNum());
		else
			q.setParameter("estado", referenciaNoRef.getValorNum());
		if (q.getResultList().isEmpty())
			return false;

		return true;
	}

	private boolean cuentaConCpt(PlantaCargoDet planta) {
		if (planta.getCpt() != null && planta.getCpt().getIdCpt() != null)
			return true;
		return false;
	}

	private List<ConcursoPuestoDet> traerPuestos() {

		List<ConcursoPuestoDet> listaRetorno = new ArrayList<ConcursoPuestoDet>();
		try {
			List<ConcursoPuestoDet> puestosPosibles = listaPuestosPosibles();
			for (ConcursoPuestoDet puesto : puestosPosibles) {
				if (validarPermanente(puesto))
					listaRetorno.add(puesto);
			}

			return listaRetorno;
		} catch (Exception ex) {
			return new Vector<ConcursoPuestoDet>();
		}
	}
	
	
	private List<PromocionConcursoAgr> traerPromocionConcursoAgr() {

		List<PromocionConcursoAgr> listaRetorno = new ArrayList<PromocionConcursoAgr>();
		try {
			List<PromocionConcursoAgr> puestosPosibles = listaPromocionConcursoAgr();
			for (PromocionConcursoAgr puesto : puestosPosibles) {
				
					listaRetorno.add(puesto);
			}

			return listaRetorno;
		} catch (Exception ex) {
			return new Vector<PromocionConcursoAgr>();
		}
	}


	public void traerDatos() {
		puestosAsignados = new ArrayList<ConcursoPuestoDet>();
		puestosAsignadosAux = new ArrayList<ConcursoPuestoDet>();
		puestosSinAsignar = new ArrayList<ConcursoPuestoDet>();
		puestosSinAsignar = traerPuestos();
		puestosAsignados = traerPuestosAsignados();
		if (puestosAsignados != null && puestosAsignados.size() > 0)
			puestosAsignadosAux.addAll(puestosAsignados);

	}
	
	
	public void traerDatosPromocionSalarial() {
		PromocionSalarialAsignados = new ArrayList<PromocionConcursoAgr>();
		PromocionSalarialAsignadosAux = new ArrayList<PromocionConcursoAgr>();
		PromocionSalarialSinAsignar = new ArrayList<PromocionConcursoAgr>();
		PromocionSalarialSinAsignar = traerPromocionConcursoAgr();
		PromocionSalarialAsignados = traerPromocionConcursoAgrAsignados();
		if (PromocionSalarialAsignados != null && PromocionSalarialAsignados.size() > 0)
			PromocionSalarialAsignadosAux.addAll(PromocionSalarialAsignados);

	}


	@SuppressWarnings("unchecked")
	private Boolean existeOrden() {
		String cad = "select agr.* "
				+ "from seleccion.concurso_puesto_agr agr "
				+ "join seleccion.concurso concurso  "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where agr.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr()
				+ " and concurso.id_concurso = " + concurso.getIdConcurso()
				+ " and agr.nro_orden = " + orden;
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		if (lista.size() > 0)
			return true;
		return false;
	}

	public String startTask() throws Exception {
		/***** Incidencia 0001114 ************/
		boolean tareaAsignada = jbpmUtilFormController
				.asignarTaskInstanceActual(puestoAgr.getIdProcessInstance(),
						usuarioLogueado.getCodigoUsuario());
		if (!tareaAsignada) {
			throw new Exception("No se pudo asignar el Task Instance Actual.");
		}
		return "OK";
		/*****************************************/

	}

	private String validacion() {

		Long idCpt = null;
		String ms = "";
		for (ConcursoPuestoDet c : puestosAsignados) {
			Boolean cumple = false;

			if (c.getPlantaCargoDet().getCpt() != null) {
				if (idCpt == null) {
					idCpt = c.getPlantaCargoDet().getCpt().getId();
				}

				if (c.getPlantaCargoDet().getCpt().getId().longValue() == idCpt
						.longValue()) {
					cumple = true;
				} else {
					ms += c.getPlantaCargoDet().getIdPlantaCargoDet() + " - "
							+ c.getPlantaCargoDet().getDescripcion();
				}
			}

			if (!cumple) {
				return ms;
			}
		}
		return "";

	}
	
	
	private String validacionPromocionSalarial() {

		Long idCpt = null;
		String ms = "";
		for (PromocionConcursoAgr c : PromocionSalarialAsignados) {
			Boolean cumple = false;

			if (c.getPromocionSalarial().getCpt() != null) {
				if (idCpt == null) {
					idCpt = c.getPromocionSalarial().getCpt().getId();
				}

				if (c.getPromocionSalarial().getCpt().getId().longValue() == idCpt
						.longValue()) {
					cumple = true;
				} else {
					ms += c.getPromocionSalarial().getIdPromocionSalarial() + " - "
							+ c.getPromocionSalarial().getDescripcion();
				}
			}

			if (!cumple) {
				return ms;
			}
		}
		return "";

	}

	/**
	 * Método que setea todos los datos necesarios para luego guardarlos.
	 * 
	 * @return
	 */
	public String guardar() {
		String returnResp = "persisted";
		if (puestosAsignados == null || puestosAsignados.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos un puesto para agrupar");
			return null;
		}
		if (orden.intValue() <= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El número de Orden debe ser mayor a 0. Verifique");
			return null;
		}

		if (exiteNroOrden("save")) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El número de Orden ingresado ya existe. Verifique");
			return null;
		}
		List<String> lIn = Arrays.asList("1", "8");
		if (nroActividad == null
				|| (nroActividad != null && !lIn.contains(nroActividad
						.toString()))) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Se debe proveer el número de Actividad, este debe ser 1 u 8. Verifique");
			return null;
		}
		try {
			puestoAgr = new ConcursoPuestoAgr();
			puestoAgr.setActivo(true);
			puestoAgr.setCodGrupo(codigo);
			puestoAgr.setConcurso(concurso);
			puestoAgr.setDescripcionGrupo(denominacion);
			Referencias ref = new Referencias();
			if (nroActividad.intValue() == 1) {
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "CREADO");
			} else {
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR");
			}
			if (ref != null) {
				puestoAgr.setEstado(ref.getValorNum());
			}
			EstadoDet estadoDet = buscarEstado("agrupado");
			if (!existeOrden())
				puestoAgr.setNroOrden(orden);
			else {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El orden ingresado ya existe o no es valido");
				return null;
			}

			String mensaje = validacion();

			if (!mensaje.trim().isEmpty()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Los puestos: " + mensaje
						+ " no pueden ser agrupados, son de CPT's distintos");
				return null;
			}
			/***** Incidencia 1110 ****/
			if (nroActividad.intValue() == 8
					&& puestoAgr.getIdProcessInstance() == null) {
				/***** Incidencia 1059 ****/
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO);
				jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CONCURSO);
				jbpmUtilFormController.setTransition("next4");
				actor.setId(usuarioLogueado.getCodigoUsuario());
				Long processId = jbpmUtilFormController
						.initProcess("seleccion");
				if (processId != null) {
					puestoAgr.setIdProcessInstance(processId);
					mostrarModal = true;
					returnResp = "START_TASK";
				} else {
					throw new Exception(
							"No se pudo generar el process Instance.");
				}

			} else {
				mostrarModal = false;
			}
			/*****************/

			puestoAgr.setObservacion(observacion);
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;

			puestoAgr.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoAgr.setFechaAlta(new Date());
			em.persist(puestoAgr);
			concursoPuestoAgrHome.setInstance(puestoAgr);
			String separador = File.separator;
			ubicacionFisica = separador
					+ "SICCA"
					+ separador
					+ anho
					+ separador
					+ "OEE"
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos()
					+ separador
					+ concurso.getIdConcurso()
					+ separador
					+ concursoPuestoAgrHome.getInstance()
							.getIdConcursoPuestoAgr();

			for (ConcursoPuestoDet c : puestosAsignados) {
				c.setNroOrden(3);
				c.setEstadoDet(estadoDet);
				c.setConcursoPuestoAgr(concursoPuestoAgrHome.getInstance());
				em.merge(c);

				PlantaCargoDet planta = new PlantaCargoDet();
				planta = c.getPlantaCargoDet();
				planta.setEstadoDet(estadoDet);
				em.merge(planta);
			}

			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return returnResp;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}
	
	
	public String guardarPromocionSalarial() {
		String returnResp = "persisted";
		if (PromocionSalarialAsignados == null || PromocionSalarialAsignados.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos un puesto para agrupar");
			return null;
		}
		if (orden.intValue() <= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El número de Orden debe ser mayor a 0. Verifique");
			return null;
		}

		if (exiteNroOrden("save")) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El número de Orden ingresado ya existe. Verifique");
			return null;
		}
		List<String> lIn = Arrays.asList("1", "8");
		if (nroActividad == null
				|| (nroActividad != null && !lIn.contains(nroActividad
						.toString()))) {
			statusMessages.clear();
			statusMessages
					.add(Severity.ERROR,
							"Se debe proveer el número de Actividad, este debe ser 1 u 8. Verifique");
			return null;
		}
		try {
			puestoAgr = new ConcursoPuestoAgr();
			puestoAgr.setActivo(true);
			puestoAgr.setCodGrupo(codigo);
			puestoAgr.setConcurso(concurso);
			puestoAgr.setDescripcionGrupo(denominacion);
			Referencias ref = new Referencias();
			if (nroActividad.intValue() == 1) {
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "CREADO");
			} else {
				ref = referenciasUtilFormController.getReferencia(
						"ESTADOS_GRUPO", "CREADO EN ACTIVIDAD MODIFICAR");
			}
			if (ref != null) {
				puestoAgr.setEstado(ref.getValorNum());
			}
			EstadoDet estadoDet = buscarEstado("agrupado");
			if (!existeOrden())
				puestoAgr.setNroOrden(orden);
			else {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"El orden ingresado ya existe o no es valido");
				return null;
			}

			String mensaje = validacionPromocionSalarial();

			if (!mensaje.trim().isEmpty()) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR, "Las Promociones : " + mensaje
						+ " no pueden ser agrupados, son de CPT's distintos");
				return null;
			}
			/***** Incidencia 1110 ****/
			if (nroActividad.intValue() == 8
					&& puestoAgr.getIdProcessInstance() == null) {
				/***** Incidencia 1059 ****/
				jbpmUtilFormController
						.setActividadSiguiente(ActividadEnum.CIO_MODIFICAR_DATOS_CONCURSO);
				jbpmUtilFormController.setProcesoEnum(ProcesoEnum.CONCURSO);
				jbpmUtilFormController.setTransition("next4");
				actor.setId(usuarioLogueado.getCodigoUsuario());
				Long processId = jbpmUtilFormController
						.initProcess("seleccion");
				if (processId != null) {
					puestoAgr.setIdProcessInstance(processId);
					mostrarModal = true;
					returnResp = "START_TASK";
				} else {
					throw new Exception(
							"No se pudo generar el process Instance.");
				}

			} else {
				mostrarModal = false;
			}
			/*****************/

			puestoAgr.setObservacion(observacion);
			Date fechaActual = new Date();
			Integer anho = fechaActual.getYear() + 1900;
			
			

			puestoAgr.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoAgr.setFechaAlta(new Date());
			em.persist(puestoAgr);
			concursoPuestoAgrHome.setInstance(puestoAgr);
			String separador = File.separator;
			ubicacionFisica = separador
					+ "SICCA"
					+ separador
					+ anho
					+ separador
					+ "OEE"
					+ separador
					+ concurso.getDatosEspecificosTipoConc()
							.getIdDatosEspecificos()
					+ separador
					+ concurso.getIdConcurso()
					+ separador
					+ concursoPuestoAgrHome.getInstance()
							.getIdConcursoPuestoAgr();

			for (PromocionConcursoAgr c : PromocionSalarialAsignados) {
				c.setNroOrden(3);
				c.setEstadoDet(estadoDet);
				c.setConcursoPuestoAgr(concursoPuestoAgrHome.getInstance());
				em.merge(c);

//				PlantaCargoDet planta = new PlantaCargoDet();
//				planta = c.getPlantaCargoDet();
//				planta.setEstadoDet(estadoDet);
//				em.merge(planta);
			}
			em.flush();
			
			int cantidadVacancia = 0;
			//SI ES PROMOCION SALARIAL
			if(concurso.getPromocion())
				cantidadVacancia =Integer.parseInt( em.createNativeQuery("SELECT COUNT(*)FROM seleccion.promocion_concurso_agr WHERE id_concurso_puesto_agr="+puestoAgr.getIdConcursoPuestoAgr()).getSingleResult().toString());
				
			else
			//SI NO ES PROMOCION SALARIAL
				cantidadVacancia =Integer.parseInt( em.createNativeQuery("SELECT COUNT(*)FROM seleccion.concurso_puesto_det WHERE id_concurso_puesto_agr="+puestoAgr.getIdConcursoPuestoAgr()).getSingleResult().toString());
			
			puestoAgr.setCantidadPuestos(cantidadVacancia);
			em.merge(puestoAgr);
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return returnResp;
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}
	

	@SuppressWarnings("unchecked")
	private boolean exiteNroOrden(String op) {
		String sql = "Select c from ConcursoPuestoAgr c "
				+ " where c.nroOrden= " + orden + " and c.concurso.idConcurso="
				+ concurso.getIdConcurso();
		if (op.equals("update"))
			sql += " and c.idConcursoPuestoAgr !="
					+ puestoAgr.getIdConcursoPuestoAgr();
		List<ConcursoPuestoAgr> agrs = em.createQuery(sql).getResultList();
		if (!agrs.isEmpty())
			return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	private PuestoConceptoPago buscarObjeto(Long id, Concurso c) {
		Referencias referenciasPendiente = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "PENDIENTE");
		Referencias referenciasReservado = referenciasUtilFormController
				.getReferencia("ESTADOS_CATEGORIA", "RESERVADO");
		String cad = "select pago.* from planificacion.puesto_concepto_pago pago "
				+ "where pago.obj_codigo = 111 "
				+ "and pago.id_planta_cargo_det = " + id;
		if (c.getAdReferendum())
			cad += " and pago.estado = " + referenciasPendiente.getValorNum();
		if (!c.getAdReferendum())
			cad += " and pago.estado = " + referenciasReservado.getValorNum();

		List<PuestoConceptoPago> lista = em.createNativeQuery(cad,
				PuestoConceptoPago.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	/**
	 * Método que setea todos los datos necesarios para luego actualizarlos.
	 * 
	 * @return
	 */
	public String actualizar() {
		/*
		 * Referencias ref = new Referencias(); ref =
		 * referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
		 * "CREADO");
		 */
		if (puestosAsignados == null || puestosAsignados.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos un puesto para agrupar");
			return null;
		}
		if (orden.intValue() <= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El numero de Orden debe ser mayor a 0. Verifique");
			return null;
		}

		if (exiteNroOrden("update")) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El numero de Orden ingresado ya existe. Verifique");
			return null;
		}
		try {
			puestoAgr.setCodGrupo(codigo);
			puestoAgr.setDescripcionGrupo(denominacion);
			puestoAgr.setObservacion(observacion);
			if (!orden.equals(puestoAgr.getNroOrden())) {
				if (!existeOrden())
					puestoAgr.setNroOrden(orden);
				else {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El orden ingresado ya existe o no es valido");
					return null;
				}
			}

			puestoAgr.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoAgr.setFechaAlta(new Date());
			em.persist(puestoAgr);
			concursoPuestoAgrHome.setInstance(puestoAgr);
			// String resultado = concursoPuestoAgrHome.update();
			String mensaje = "";
			try {

				for (ConcursoPuestoDet agr : puestosAsignadosAux) {
					Boolean esta = false;
					for (ConcursoPuestoDet c : puestosAsignados) {
						if (c.getIdConcursoPuestoDet() != null
								&& c.getIdConcursoPuestoDet().equals(
										agr.getIdConcursoPuestoDet())) {
							esta = true;
						}
					}
					if (!esta) {
						List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
						lista = buscarConcurso();
						for (ConcursoPuestoDet l : lista) {
							l.setConcursoPuestoAgr(null);
							l.setEstadoDet(buscarEstado("en reserva"));
							em.merge(l);
							PlantaCargoDet planta = new PlantaCargoDet();
							planta = l.getPlantaCargoDet();
							planta.setEstadoDet(buscarEstado("en reserva"));
							em.merge(planta);
						}
						// em.remove(agr);
						// em.flush();

					}
				}

				Long idCpt = null;
				String categoria = null;
				for (ConcursoPuestoDet c : puestosAsignados) {
					// if (c.getConcursoPuestoAgr() == null) {
					Boolean cumple = false;

					if (c.getPlantaCargoDet().getPermanente()) {
						if (c.getPlantaCargoDet().getCpt() != null) {
							if (c.getPlantaCargoDet().getCpt() != null) {
								if (idCpt == null) {
									idCpt = c.getPlantaCargoDet().getCpt()
											.getId();
								}

								if (c.getPlantaCargoDet().getCpt().getId()
										.longValue() == idCpt.longValue()) {

									PuestoConceptoPago puestoConceptoPago = buscarObjeto(
											c.getPlantaCargoDet()
													.getIdPlantaCargoDet(),
											c.getConcurso());
									if (puestoConceptoPago != null) {
										if (categoria == null) {
											categoria = puestoConceptoPago
													.getCategoria();
										}

										if (categoria == null
												|| categoria
														.equals(puestoConceptoPago
																.getCategoria())) {
											cumple = true;
										}
									}
								}
							}
						}
					}

					if (cumple) {
						c.setNroOrden(3);
						c.setEstadoDet(buscarEstado("agrupado"));
						c.setConcursoPuestoAgr(concursoPuestoAgrHome
								.getInstance());
						if (c.getConcursoPuestoAgr() == null)
							em.persist(c);
						else
							em.merge(c);
						// em.flush();
					} else {
						mensaje += " " + c.getPlantaCargoDet().getDescripcion();

					}
					PlantaCargoDet planta = new PlantaCargoDet();
					planta = c.getPlantaCargoDet();
					planta.setEstadoDet(buscarEstado("agrupado"));
					em.merge(planta);
					// em.flush();
					// }
				}
				if (!mensaje.trim().isEmpty()) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"Los puestos: "
											+ mensaje
											+ " no pueden ser agrupados, no cumplen las condiciones");
					concursoPuestoAgrHome.setInstance(null);
					return null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (mensaje.trim().isEmpty()) {
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			}
			operacion = "volver";
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}
	
	
	
	public String actualizarPromocionSalarial() {
		/*
		 * Referencias ref = new Referencias(); ref =
		 * referenciasUtilFormController.getReferencia("ESTADOS_GRUPO",
		 * "CREADO");
		 */
		if (PromocionSalarialAsignados == null || PromocionSalarialAsignados.size() == 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos un puesto para agrupar");
			return null;
		}
		if (orden.intValue() <= 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El numero de Orden debe ser mayor a 0. Verifique");
			return null;
		}

		if (exiteNroOrden("update")) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El numero de Orden ingresado ya existe. Verifique");
			return null;
		}
		try {
			puestoAgr.setCodGrupo(codigo);
			puestoAgr.setDescripcionGrupo(denominacion);
			puestoAgr.setObservacion(observacion);
			if (!orden.equals(puestoAgr.getNroOrden())) {
				if (!existeOrden())
					puestoAgr.setNroOrden(orden);
				else {
					statusMessages.clear();
					statusMessages.add(Severity.ERROR,
							"El orden ingresado ya existe o no es valido");
					return null;
				}
			}

			puestoAgr.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			puestoAgr.setFechaAlta(new Date());
			em.persist(puestoAgr);
			concursoPuestoAgrHome.setInstance(puestoAgr);
			// String resultado = concursoPuestoAgrHome.update();
			String mensaje = "";
			try {

				for (PromocionConcursoAgr agr : PromocionSalarialAsignadosAux) {
					Boolean esta = false;
					for (PromocionConcursoAgr c : PromocionSalarialAsignados) {
						if (c.getIdPromocionConcursoAgr() != null
								&& c.getIdPromocionConcursoAgr().equals(
										agr.getIdPromocionConcursoAgr())) {
							esta = true;
						}
					}
					if (!esta) {
						List<PromocionConcursoAgr> lista = new ArrayList<PromocionConcursoAgr>();
						lista = buscarConcursoPromocionSalarial();
						for (PromocionConcursoAgr l : lista) {
							l.setConcursoPuestoAgr(null);
							l.setEstadoDet(buscarEstado("en reserva"));
							em.merge(l);
//							PlantaCargoDet planta = new PlantaCargoDet();
//							planta = l.getPromocionSalarial();
//							planta.setEstadoDet(buscarEstado("en reserva"));
//							em.merge(planta);
						}
						// em.remove(agr);
						// em.flush();

					}
				}

				Long idCpt = null;
				String categoria = null;
				for (PromocionConcursoAgr c : PromocionSalarialAsignados) {
					// if (c.getConcursoPuestoAgr() == null) {
					Boolean cumple = false;

				
					if (c.getPromocionSalarial().getCpt() != null) {
						if (c.getPromocionSalarial().getCpt() != null) {
							if (idCpt == null) {
								idCpt = c.getPromocionSalarial().getCpt()
										.getId();
							}

							if (c.getPromocionSalarial().getCpt().getId()
									.longValue() == idCpt.longValue()) {

								if (c.getPromocionSalarial().getCategoria() != null) {
									if (categoria == null) {
										categoria =c.getPromocionSalarial().getCategoria();
									}

									if (categoria == null
											|| categoria
													.equals(c.getPromocionSalarial().getCategoria())) {
										cumple = true;
									}
								}
							}
						}
					}
					

					if (cumple) {
						c.setNroOrden(3);
						c.setEstadoDet(buscarEstado("agrupado"));
						c.setConcursoPuestoAgr(concursoPuestoAgrHome
								.getInstance());
						if (c.getConcursoPuestoAgr() == null)
							em.persist(c);
						else
							em.merge(c);
						// em.flush();
					} else {
						mensaje += " " + c.getPromocionSalarial().getDescripcion();

					}
//					PlantaCargoDet planta = new PlantaCargoDet();
//					planta = c.getPromocionSalarial();
//					planta.setEstadoDet(buscarEstado("agrupado"));
//					em.merge(planta);
					// em.flush();
					// }
				}
				if (!mensaje.trim().isEmpty()) {
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"Los puestos: "
											+ mensaje
											+ " no pueden ser agrupados, no cumplen las condiciones");
					concursoPuestoAgrHome.setInstance(null);
					return null;
				}
				
				
				int cantidadVacancia = 0;
				//SI ES PROMOCION SALARIAL
				if(concurso.getPromocion())
					cantidadVacancia =Integer.parseInt( em.createNativeQuery("SELECT COUNT(*)FROM seleccion.promocion_concurso_agr WHERE id_concurso_puesto_agr="+puestoAgr.getIdConcursoPuestoAgr()).getSingleResult().toString());
					
				else
				//SI NO ES PROMOCION SALARIAL
					cantidadVacancia =Integer.parseInt( em.createNativeQuery("SELECT COUNT(*)FROM seleccion.concurso_puesto_det WHERE id_concurso_puesto_agr="+puestoAgr.getIdConcursoPuestoAgr()).getSingleResult().toString());
				
				puestoAgr.setCantidadPuestos(cantidadVacancia);

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (mensaje.trim().isEmpty()) {
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle
						.getBundle().getString("GENERICO_MSG"));
			}
			operacion = "volver";
			return "updated";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ComisionGrupo> searchComisionGrupo() {
		String sql = "select gr.* " + "from seleccion.comision_grupo gr "
				+ "where gr.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr();
		return em.createNativeQuery(sql, ComisionGrupo.class).getResultList();
	}

	/**
	 * Método que setea todos los datos necesarios para luego actualizarlos.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String eliminar() {
		try {
			List<ComisionGrupo> listaGr = searchComisionGrupo();
			if (listaGr != null && listaGr.size() > 0) {
				statusMessages.clear();
				statusMessages
						.add(Severity.ERROR,
								"El grupo no puede eliminarse, ya cuenta con comisión de selección");

				return "ok";
			}

			String sqlDoc = "select doc.* " + "from general.documentos doc "
					+ "where doc.nombre_tabla = 'ConcursoPuestoAgr' "
					+ "and doc.id_tabla = "
					+ puestoAgr.getIdConcursoPuestoAgr();

			List<Documentos> listaDocumentos = new ArrayList<Documentos>();
			listaDocumentos = em.createNativeQuery(sqlDoc, Documentos.class)
					.getResultList();
			List<ConcursoPuestoDet> listaDet = new ArrayList<ConcursoPuestoDet>();
			listaDet = puestoAgr.getConcursoPuestoDets();
			for (Documentos doc : listaDocumentos) {
				em.remove(doc);
				// em.flush();
			}
			if (listaDet.size() > 0) {
				for (ConcursoPuestoDet d : listaDet) {
					PlantaCargoDet planta = d.getPlantaCargoDet();
					planta.setEstadoDet(buscarEstado("libre"));
					em.merge(planta);

					em.remove(d);
				}
				// em.flush();
			}
			try {
				em.remove(puestoAgr);
				em.flush();
			} catch (Exception e) {
				// TODO: handle exception
			}

			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private List<ConcursoPuestoDet> buscarConcurso() {
		String cad = " select puesto_det.* "
				+ "from seleccion.concurso_puesto_det puesto_det "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det "
				+ "join planificacion.estado_cab  "
				+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab "
				+ "where puesto_det.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr()
				+ " and lower(estado_det.descripcion) = 'agrupado' "
				+ "and lower(estado_cab.descripcion) = 'concurso'";
		List<ConcursoPuestoDet> lista = new ArrayList<ConcursoPuestoDet>();
		lista = em.createNativeQuery(cad, ConcursoPuestoDet.class)
				.getResultList();
		return lista;
	}
	
	private List<PromocionConcursoAgr> buscarConcursoPromocionSalarial() {
		String cad = " select puesto_det.* "
				+ "from seleccion.promocion_concurso_Agr puesto_det "
				+ "join planificacion.estado_det estado_det "
				+ "on estado_det.id_estado_det = puesto_det.id_estado_det "
				+ "join planificacion.estado_cab  "
				+ "on estado_cab.id_estado_cab = estado_det.id_estado_cab "
				+ "where puesto_det.id_concurso_puesto_agr = "
				+ puestoAgr.getIdConcursoPuestoAgr()
				+ " and lower(estado_det.descripcion) = 'agrupado' "
				+ "and lower(estado_cab.descripcion) = 'concurso'";
		List<PromocionConcursoAgr> lista = new ArrayList<PromocionConcursoAgr>();
		lista = em.createNativeQuery(cad, PromocionConcursoAgr.class)
				.getResultList();
		return lista;
	}

	/**
	 * Busca el id correspondiente a un estado
	 * 
	 * @param estado
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private EstadoDet buscarEstado(String estado) {
		String cad = "select det.* from planificacion.estado_cab cab "
				+ "join planificacion.estado_det det  "
				+ "on det.id_estado_cab = cab.id_estado_cab "
				+ "where lower(cab.descripcion)='concurso' "
				+ "and lower(det.descripcion)='" + estado + "'";
		List<EstadoDet> lista = new ArrayList<EstadoDet>();
		lista = em.createNativeQuery(cad, EstadoDet.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	private Integer valorMaximoOrden() {
		String cad = "select max(puesto_agr.nro_orden) as valor from "
				+ "seleccion.concurso_puesto_agr puesto_agr "
				+ "join seleccion.concurso concurso "
				+ "on concurso.id_concurso = puesto_agr.id_concurso "
				+ "where concurso.id_concurso = " + concurso.getIdConcurso();

		List<Object[]> config = em.createNativeQuery(cad).getResultList();
		if (config == null) {
			return 0;
		}
		Object obj1 = config.get(0);
		if (obj1 == null)
			return 0;
		String v = obj1.toString();

		return new Integer(v);
	}
	
	private Integer valorMaximoCodigo() {
		String cad = "select agr.* "
				+ "from seleccion.concurso_puesto_agr agr "
				+ "join seleccion.concurso concurso  "
				+ "on concurso.id_concurso = agr.id_concurso "
				+ "where concurso.id_concurso = " + concurso.getIdConcurso();
		List<ConcursoPuestoAgr> lista = new ArrayList<ConcursoPuestoAgr>();
		lista = em.createNativeQuery(cad, ConcursoPuestoAgr.class)
				.getResultList();
		Integer valorMax = 0;
		for (ConcursoPuestoAgr ag : lista) {
			
			try {
				Integer valorActual = new Integer(ag.getCodGrupo());
				if(valorActual.intValue() > valorMax.intValue())
					valorMax = valorActual.intValue();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		valorMax = valorMax.intValue() + 1;
		return valorMax;

	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public ConcursoPuestoAgr getPuestoAgr() {
		return puestoAgr;
	}

	public void setPuestoAgr(ConcursoPuestoAgr puestoAgr) {
		this.puestoAgr = puestoAgr;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getDenominacion() {
		return denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Long getIdNivelEntidad() {
		return idNivelEntidad;
	}

	public void setIdNivelEntidad(Long idNivelEntidad) {
		this.idNivelEntidad = idNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Long getIdConfiguracionUoCab() {
		return idConfiguracionUoCab;
	}

	public void setIdConfiguracionUoCab(Long idConfiguracionUoCab) {
		this.idConfiguracionUoCab = idConfiguracionUoCab;
	}

	public List<ConcursoPuestoDet> getPuestosSinAsignar() {
		return puestosSinAsignar;
	}

	public void setPuestosSinAsignar(List<ConcursoPuestoDet> puestosSinAsignar) {
		this.puestosSinAsignar = puestosSinAsignar;
	}

	public List<ConcursoPuestoDet> getPuestosAsignados() {
		return puestosAsignados;
	}

	public void setPuestosAsignados(List<ConcursoPuestoDet> puestosAsignados) {
		this.puestosAsignados = puestosAsignados;
	}

	public List<ConcursoPuestoAgr> getPuestosAgrupados() {
		return puestosAgrupados;
	}

	public void setPuestosAgrupados(List<ConcursoPuestoAgr> puestosAgrupados) {
		this.puestosAgrupados = puestosAgrupados;
	}

	public List<ConcursoPuestoAgr> getPuestosAgrupadosAux() {
		return puestosAgrupadosAux;
	}

	public void setPuestosAgrupadosAux(
			List<ConcursoPuestoAgr> puestosAgrupadosAux) {
		this.puestosAgrupadosAux = puestosAgrupadosAux;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public String getFromConcurso() {
		if (fromConcurso == null || "".equals(fromConcurso))
			return "/home.xhtml";

		if (fromConcurso.contains(".seam")) {
			if (!fromConcurso.startsWith("/"))
				return "/" + fromConcurso;
			else
				return fromConcurso;
		}

		if (fromConcurso.contains("?")) {
			if (!fromConcurso.startsWith("/"))
				return "/"
						+ fromConcurso.substring(0, fromConcurso.indexOf("?"))
						+ ".seam"
						+ fromConcurso.substring(fromConcurso.indexOf("?"),
								fromConcurso.length());
			else
				return fromConcurso.substring(0, fromConcurso.indexOf("?"))
						+ ".seam"
						+ fromConcurso.substring(fromConcurso.indexOf("?"),
								fromConcurso.length());
		}

		if (!fromConcurso.contains(".xhtml")) {
			if (!fromConcurso.startsWith("/"))
				return "/" + fromConcurso + ".xhtml";
			else
				return fromConcurso + ".xhtml";
		}
		return fromConcurso;
	}

	public void setFromConcurso(String fromConcurso) {
		this.fromConcurso = fromConcurso;
	}

	public Long getIdAgr() {
		return idAgr;
	}

	public void setIdAgr(Long idAgr) {
		this.idAgr = idAgr;
	}

	public List<ConcursoPuestoDet> getPuestosAsignadosAux() {
		return puestosAsignadosAux;
	}

	public void setPuestosAsignadosAux(
			List<ConcursoPuestoDet> puestosAsignadosAux) {
		this.puestosAsignadosAux = puestosAsignadosAux;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public Integer getNroActividad() {
		return nroActividad;
	}

	public void setNroActividad(Integer nroActividad) {
		this.nroActividad = nroActividad;
	}

	public Boolean getMostrarModal() {
		return mostrarModal;
	}

	public void setMostrarModal(Boolean mostrarModal) {
		this.mostrarModal = mostrarModal;
	}




	public List<PromocionConcursoAgr> getPromocionSalarialSinAsignar() {
		return PromocionSalarialSinAsignar;
	}




	public void setPromocionSalarialSinAsignar(
			List<PromocionConcursoAgr> promocionSalarialSinAsignar) {
		PromocionSalarialSinAsignar = promocionSalarialSinAsignar;
	}




	public List<PromocionConcursoAgr> getPromocionSalarialAsignados() {
		return PromocionSalarialAsignados;
	}




	public void setPromocionSalarialAsignados(
			List<PromocionConcursoAgr> promocionSalarialAsignados) {
		PromocionSalarialAsignados = promocionSalarialAsignados;
	}




	public List<PromocionConcursoAgr> getPromocionSalarialAsignadosAux() {
		return PromocionSalarialAsignadosAux;
	}




	public void setPromocionSalarialAsignadosAux(
			List<PromocionConcursoAgr> promocionSalarialAsignadosAux) {
		PromocionSalarialAsignadosAux = promocionSalarialAsignadosAux;
	}




	public List<ConcursoPuestoAgr> getPromocionSalarialAgrupados() {
		return PromocionSalarialAgrupados;
	}




	public void setPromocionSalarialAgrupados(
			List<ConcursoPuestoAgr> promocionSalarialAgrupados) {
		PromocionSalarialAgrupados = promocionSalarialAgrupados;
	}




	public List<ConcursoPuestoAgr> getPromocionSalarialAgrupadosAux() {
		return PromocionSalarialAgrupadosAux;
	}




	public void setPromocionSalarialAgrupadosAux(
			List<ConcursoPuestoAgr> promocionSalarialAgrupadosAux) {
		PromocionSalarialAgrupadosAux = promocionSalarialAgrupadosAux;
	}

}
