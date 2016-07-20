package py.com.excelsis.sicca.movilidadLaboral.session.form;

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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;
import enums.TiposDatos;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarReasignacionInsercionMasivaCU710FC")

public class GestionarReasignacionInsercionMasivaCU710FC {
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(create = true, required = false)
	JasperReportUtils jasperReportUtils;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	BuscadorDocsFC buscadorDocsFC;
	@In(create = true)
	SinarhUtiles sinarhUtiles;

	private Long idPaisExp = null;
	private Long idPersona = null;
	private Long idEmpleadoPuesto;
	private Long idFuncionario;
	private Long idPlantaCargoDet;
	private Long idPais;
	private Integer monto;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private boolean habUpdate = false;
	private boolean habPaisExp = true;
	private boolean habBtnAddPersons = false;
	private Boolean confirmacionEnPuesto = false;
	private Boolean primeraEntrada = true;
	private Boolean mostrarPanelConfirmacion = true;
	private String docIdentidad = null;
	private String nombreNivelEntidad;
	private BigDecimal codNivelEntidad;
	private Date fechaInicio;
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private EmpleadoPuesto empleadoPuesto;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private EmpleadoPuesto funcionario;
	private EmpleadoPuesto funcionarioNvo;
	private Long idPlantaCargoDetParaConcurso;
	private Long idConcurso;
	private Persona persona;
	private Referencias refMontoPromocion;
	private EmpleadoConceptoPago conceptoPagoAnterior;
	private ConfiguracionUoDet configuracionUoDetNuevo;	
	private EmpleadoMovilidadCab movilidad;	
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private List<ConfiguracionUoDet> configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList = new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos = new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosFuncionario = new ArrayList<EmpleadoConceptoPago>();

	public void init() throws Exception {

		if (idPersona != null) {
			if (!seguridadUtilFormController.validarInput(idPersona.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}

		}
		idPaisExp = General.getIdParaguay();
		cargarNiveentidadOee();
		if (primeraEntrada) {
			selectedRow = -1;
			empleadoPuesto = new EmpleadoPuesto();
			primeraEntrada = false;
			if (idEmpleadoPuesto != null) {
				empleadoPuesto = em
						.find(EmpleadoPuesto.class, idEmpleadoPuesto);
				obtenerDatos();
			} else {
				setearDatos();
			}
		}
		if (idFuncionario != null) {
			funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
			if (funcionario.getPlantaCargoDet().getContratado()) {
				funcionario = new EmpleadoPuesto();
				statusMessages.add(Severity.ERROR,
						"Escoja un Funcionario Permanente");
				return;
			}
			obtenerEmpleadoConceptoPagoAnterior();
		}
		searchReferencia();
	}

	private void obtenerEmpleadoConceptoPagoAnterior() {
		String sql = "select p.* from general.empleado_concepto_pago p where p.id_empleado_puesto = "
				+ funcionario.getIdEmpleadoPuesto() + " and p.obj_codigo = 111";
		List<EmpleadoConceptoPago> lista = em.createNativeQuery(sql,
				EmpleadoConceptoPago.class).getResultList();

		if (lista != null && lista.size() > 0)
			conceptoPagoAnterior = lista.get(0);
	}

	private void searchReferencia() {
		String sql = "select r.* " + "from seleccion.referencias r "
				+ "where dominio = 'MONTO_PROMOCION' " + "and activo is true";

		List<Referencias> lista = em.createNativeQuery(sql, Referencias.class)
				.getResultList();

		if (lista != null && lista.size() > 0)
			refMontoPromocion = lista.get(0);
	}

	public void initView() throws Exception {

		if (idEmpleadoPuesto != null) {
			if (!seguridadUtilFormController.validarInput(
					idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(),
					null)) {
				return;
			}
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			persona = empleadoPuesto.getPersona();

			habBtnAddPersons = false;
		}
		idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		cargarNiveentidadOee();
		obtenerDatosView();

		fechaInicio = empleadoPuesto.getFechaInicio();
	}

	private void buscarConceptoPagoFuncionario() {
		String sql = "SELECT pago.* "
				+ "FROM movilidad.empleado_movilidad_cab cab "
				+ "INNER JOIN general.empleado_puesto emp ON (cab.id_empleado_puesto_anterior = emp.id_empleado_puesto) "
				+ "INNER JOIN general.empleado_concepto_pago pago ON (emp.id_empleado_puesto = pago.id_empleado_puesto) "
				+ "INNER JOIN general.persona persona ON (emp.id_persona = persona.id_persona) "
				+ "WHERE cab.id_empleado_puesto_anterior = emp.id_empleado_puesto AND  "
				+ "cab.activo = TRUE  " + "AND persona.id_persona = "
				+ persona.getIdPersona();
		empleadoConceptoPagosFuncionario = new ArrayList<EmpleadoConceptoPago>();
		empleadoConceptoPagosFuncionario = em.createNativeQuery(sql,
				EmpleadoConceptoPago.class).getResultList();
	}

	private void obtenerDatosView() {
		em.clear();
		persona = em.find(Persona.class, empleadoPuesto.getPersona()
				.getIdPersona());

		List<EmpleadoMovilidadCab> mov = em.createQuery(
				"Select r from EmpleadoMovilidadCab r "
						+ " where r.empleadoPuestoNuevo.idEmpleadoPuesto="
						+ idEmpleadoPuesto).getResultList();
		if (!mov.isEmpty()) {
			movilidad = mov.get(0);
			funcionario = mov.get(0).getEmpleadoPuestoAnterior();
			persona = funcionario.getPersona();
			Long idSinNivelEntidad = getIdSinNivelEntidad(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getEntidad().getSinEntidad()
					.getNenCodigo());
			SinNivelEntidad nivelEntidad = em.find(SinNivelEntidad.class,
					idSinNivelEntidad);
			nombreNivelEntidad = nivelEntidad.getNenNombre();
			codNivelEntidad = nivelEntidad.getNenCodigo();
			buscarConceptoPagoFuncionario();
			
			funcionarioNvo = mov.get(0).getEmpleadoPuestoNuevo();
		}

		plantaCargoDetView = em.find(PlantaCargoDet.class, empleadoPuesto
				.getPlantaCargoDet().getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet = em.find(ConfiguracionUoDet.class,
				plantaCargoDetView.getConfiguracionUoDet()
						.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
		empleadoConceptoPagos = em
				.createQuery(
						"Select e from EmpleadoConceptoPago e "
								+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
				.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();
	}

	public Long getIdSinNivelEntidad(BigDecimal codNivelEntidad) {
		if (em == null) {
			em = (EntityManager) Component.getInstance("entityManager");
		}
		Query q = em
				.createQuery("select sinNivelEntidad from SinNivelEntidad sinNivelEntidad "
						+ "WHERE sinNivelEntidad.nenCodigo ="
						+ codNivelEntidad
						+ " and sinNivelEntidad.activo is true "
						+ "order by sinNivelEntidad.aniAniopre DESC");
		List<SinNivelEntidad> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0).getIdSinNivelEntidad();
		}
		return null;
	}

	
	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList = em
				.createQuery(
						"Select det from ConfiguracionUoDet det "
								+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE order by det.denominacionUnidad ")
				.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab())
				.getResultList();		
						
	}

	@SuppressWarnings("unchecked")
	private void obtenerDatos() {
		plantaCargoDetView = em.find(PlantaCargoDet.class, empleadoPuesto
				.getPlantaCargoDet().getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet = em.find(ConfiguracionUoDet.class,
				plantaCargoDetView.getConfiguracionUoDet()
						.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
		empleadoConceptoPagos = em
				.createQuery(
						"Select e from EmpleadoConceptoPago e "
								+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
				.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();
	}

	private void setearDatos() {

		idPaisExp = General.getIdParaguay();
		habPaisExp = true;
		plantaCargoDetList = new ArrayList<PlantaCargoDet>();
		funcionario = new EmpleadoPuesto();
		idFuncionario = null;
	}

	public String getUrlToPageSearchFuncionario() {
		return "/busquedas/funcionarios/EmpleadoPuestoList.xhtml?from=movilidadLaboral/gestionarReasignacionInsercionMasiva/GestionarReasignacionInsercionMasivaEditCU710";
	}

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet
	 * seleccionado
	 * **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(int index) {
		plantaCargoDetList = em
				.createQuery(
						"Select p from PlantaCargoDet p "
								+ " where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true"
								+ " and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab and p.permanente=true")
				.setParameter("idEstadoCab", getIdEstadoCabVacante())
				.setParameter(
						"idDet",
						configuracionUoDetList.get(index)
								.getIdConfiguracionUoDet()).getResultList();
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
	}

	private Long getIdEstadoCabVacante() {
		EstadoCab cab = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		return cab.getIdEstadoCab();
	}
	
	public String getUrlToPageCategoria() {
//		if (seleccionUtilFormController
//				.getCodigoObj() == null) {
//			statusMessages.add(Severity.ERROR,
//					"El campo Código OBJ es requerido");
//			return null;
//		}
		String url = "/seleccion/asignarCategoria/categoriaReasignacion/AsignarCategoriaReasignacion.xhtml?from=movilidadLaboral/"
				+ "gestionarReasignacionInsercionMasiva/GestionarReasignacionInsercionMasivaEditCU710&objCodigo="
				+ seleccionUtilFormController.getCodigoObj()
				+ "&cadenaRecibida=PERMANENTE";
		return url;
	}
	
	public Long toFindConcurso(){
		Query q = em
				.createQuery("select det.concursoPuestoAgr.idConcursoPuestoAgr from ConcursoPuestoDet det "
						+ " where det.activo is true and det.plantaCargoDet.idPlantaCargoDet = :idPlanta");
		q.setParameter("idPlanta", idPlantaCargoDetParaConcurso);
		idConcurso=Long.parseLong((String) q.getSingleResult());
		return idConcurso;
	}
	public String esContratadoOpermanente(Long id) {
		Query q = em
				.createQuery("select det from ConcursoPuestoDet det "
						+ " where det.activo is true and det.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", id);
		List<ConcursoPuestoDet> lista = q.getResultList();
		if (!lista.isEmpty()) {
			if (lista.get(0).getPlantaCargoDet().getContratado())
				return "CONTRATADO";
			if (lista.get(0).getPlantaCargoDet().getPermanente())
				return "PERMANENTE";
		}
		return null;
	}

	public void agregar() {
		if (seleccionUtilFormController.getCodigoObj() == null) {
//			statusMessages.add(Severity.ERROR,
//					"Debe Ingresar el Cod. Objeto Gasto");
			return;
		}
		if (seleccionUtilFormController.getMonto() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Monto");
			return;
		}
		if (seleccionUtilFormController.getMonto().intValue() < 0) {
			statusMessages
					.add(Severity.ERROR, "El Monto debe ser mayor a cero");
			return;
		}
		EmpleadoConceptoPago empleadoConceptoPago = new EmpleadoConceptoPago();
		empleadoConceptoPago.setObjCodigo(seleccionUtilFormController
				.getCodigoObj());
		empleadoConceptoPago.setCategoria(seleccionUtilFormController
				.getCodigoCategoria());
		empleadoConceptoPago.setMonto(seleccionUtilFormController.getMonto());
		empleadoConceptoPagos.add(empleadoConceptoPago);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

		setearConPago();
	}

	private void setearConPago() {
		seleccionUtilFormController.setearValoresObjetosGasto();
	}

	public void eliminar(int index) {
		//empleadoConceptoPagos.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	public String getRowPuestoClass(int currentRow) {
		if (selectedRowPuesto == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

//	public void seleccionarPuesto(Long idPuesto, Integer index) {
//		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class,
//				nivelEntidadOeeUtil.getIdConfigCab());
//		seleccionUtilFormController.setPlantaCargoDet(em.find(
//				PlantaCargoDet.class, idPuesto));
//		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
//		idPlantaCargoDet = idPuesto;
//		idPlantaCargoDetParaConcurso=idPlantaCargoDet;
//		setearConPago();
//		setSelectedRowPuesto(index);
//	}
	
	public void seleccionarPuesto() {
		
		if (funcionario == null
				|| funcionario.getIdEmpleadoPuesto() == null) {
//			statusMessages
//					.add(Severity.ERROR,
//							"Debe seleccionar el funcionario antes de realizar la accion");
			return;
		} 
		
		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtil.getIdConfigCab());
		
		seleccionUtilFormController.setPlantaCargoDet(em.find(
				PlantaCargoDet.class, funcionario.getPlantaCargoDet().getIdPlantaCargoDet()));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = funcionario.getPlantaCargoDet().getIdPlantaCargoDet();
		idPlantaCargoDetParaConcurso = idPlantaCargoDet;
		setearConPago();
		
	}

	private List<PuestoConceptoPago> buscarPuestoConceptoPago(
			PlantaCargoDet puesto) {

		String query = "select pago from PuestoConceptoPago pago "
				+ " where pago.activo is true "
				+ " and pago.plantaCargoDet.idPlantaCargoDet = :id  ";
		Query q = em.createQuery(query);
		q.setParameter("id", puesto.getIdPlantaCargoDet());
		return q.getResultList();
	}

	public String save() {
		try {			
			
			if (!chkDatos())
				return null;
			
			seleccionarPuesto();
			
//			if(!chequearObj())
//				return "persisted";
			/**
			 * Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
			 * */
			
			fechaInicio=new Date();
			//funcionario.setActual(false);
			//funcionario.setActivo(false);
			//funcionario.setFechaFin(new Date());
			funcionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
			funcionario.setFechaMod(new Date());
			funcionario.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(searchDatosEsp());
			funcionario.setPin(seleccionUtilFormController.generarPIN());
			funcionario.setDatosEspecificosByIdDatosEspTipoRegistro(searchTipoRegistro());
			funcionario.setFechaAlta(new Date());
			funcionario.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.merge(funcionario);
			
			/** Obtiene datos de plata_cargo_det del funcionario seleccionado
			 * */
			PlantaCargoDet plantaCargoDetFuncionario = em.find(
					PlantaCargoDet.class, funcionario.getPlantaCargoDet()
							.getIdPlantaCargoDet());
			/**
			 * Guarda un registro en la tabla de historicos de estado
			 * */
			// ya no va a cambiar de estado
//			HistoricosEstado historico = new HistoricosEstado();
//			historico.setEstadoCab(seleccionUtilFormController
//					.buscarEstadoCab("VACANTE"));
//			historico.setFechaMod(new Date());
//			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			historico.setPlantaCargoDet(plantaCargoDetFuncionario);
//			em.persist(historico);

			/**
			 * Se reponen las categorias utilizadas
			 */
			/*List<PuestoConceptoPago> lista = buscarPuestoConceptoPago(plantaCargoDetFuncionario);
			if (!lista.isEmpty()) {
				for (PuestoConceptoPago pcp : lista) {
					actualizarDisponibles(pcp.getCategoria(),
							pcp.getObjCodigo());
					pcp.setActivo(false);
					em.merge(pcp);
				}
							

			}*/
			PlantaCargoDet puestoSelec = new PlantaCargoDet();
			if (!confirmacionEnPuesto)
				puestoSelec = em.find(PlantaCargoDet.class, idPlantaCargoDet);
			else
				puestoSelec = em.find(PlantaCargoDet.class, funcionario
						.getPlantaCargoDet().getIdPlantaCargoDet());
			/**
			 * Guardan en la tabla EMPLEADO_PUESTO
			 * */

//			empleadoPuesto.setFechaInicio(fechaInicio);
//			empleadoPuesto.setActual(true);
//			empleadoPuesto.setActivo(true);
//			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
//			empleadoPuesto.setContratado(false);
//			empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(datosEspecificosHome.getDatosPermanenteEstadoEmpleado());
//			empleadoPuesto
//					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(searchDatosEsp());
//			empleadoPuesto.setFechaAlta(new Date());
//			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//			empleadoPuesto
//					.setDatosEspecificosByIdDatosEspTipoRegistro(searchTipoRegistro());
//			empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,
//					idPlantaCargoDet));
//			empleadoPuesto.setPersona(funcionario.getPersona());
//			empleadoPuesto.setIncidenAntiguedad(true);
//			em.persist(empleadoPuesto);

			/**
			 * Registra la movilidad en la tabla EMPLEADO_MOVILIDAD_CAB
			 */
			EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
			empleadoMovilidadCab.setActivo(true);
			empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);

			empleadoMovilidadCab.setEmpleadoPuestoNuevo(funcionario);
			empleadoMovilidadCab.setMovilidadSicca(true);
			empleadoMovilidadCab.setFechaInicio(fechaInicio);
			empleadoMovilidadCab.setFechaAlta(new Date());
			empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoMovilidadCab.setConfiguracionUoDetAnterior(plantaCargoDetFuncionario.getConfiguracionUoDet());
			empleadoMovilidadCab.setConfiguracionUoDetNuevo(configuracionUoDetNuevo);
			
			em.persist(empleadoMovilidadCab);

			/**
			 * Cambiar el estado del puesto a OCUPADO
			 * */
//			puestoSelec.setEstadoCab(seleccionUtilFormController
//					.buscarEstadoCab("OCUPADO"));
//			puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			puestoSelec.setFechaMod(new Date());
//
//			em.merge(puestoSelec);

			/**
			 * Guarda un registro en la tabla de historicos de estado
			 * */
//			HistoricosEstado historial = new HistoricosEstado();
//			historial.setEstadoCab(seleccionUtilFormController
//					.buscarEstadoCab("OCUPADO"));
//			historial.setFechaMod(new Date());
//			historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
//			historial.setPlantaCargoDet(puestoSelec);

//			em.persist(historial);

			/**
			 * Se insertan datos de categorías/remuneraciones:
			 * */
//			for (EmpleadoConceptoPago conceptoPago : empleadoConceptoPagos) {
//				EmpleadoConceptoPago emp = new EmpleadoConceptoPago();
//				emp.setCategoria(conceptoPago.getCategoria());
//				emp.setMonto(conceptoPago.getMonto());
//				emp.setObjCodigo(conceptoPago.getObjCodigo());
//				emp.setEmpleadoPuesto(funcionario);
//				emp.setFechaAlta(new Date());
//				emp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
//				emp.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));
//
//				em.persist(emp);
//
//				/**
//				 * PARA EL CASO QUE TENGA CATEGORIA
//				 * */
//				if (seleccionUtilFormController.getCategoria() != null
//						&& seleccionUtilFormController.getSinAnx() != null) {
//					SinAnx anx = em.find(SinAnx.class,
//							seleccionUtilFormController.getSinAnx().getIdAnx());
//					anx.setCantDisponible(anx.getCantDisponible() - 1);
//					em.merge(anx);
//				}
//			}
			
			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al
			 * Funcionario seleccionado
			 * */
			
			//plantaCargoDetFuncionario.setEstadoCab(seleccionUtilFormController
			//		.buscarEstadoCab("VACANTE"));
			plantaCargoDetFuncionario.setUsuMod(usuarioLogueado
					.getCodigoUsuario());
			plantaCargoDetFuncionario.setFechaMod(new Date());
			plantaCargoDetFuncionario.setEstadoDet(null);
			plantaCargoDetFuncionario.setConfiguracionUoDet(configuracionUoDetNuevo);
			plantaCargoDetFuncionario.setFundamentacionTecnica(null);
			plantaCargoDetFuncionario.setOrden(calcNroOrden(plantaCargoDetFuncionario));
			em.merge(plantaCargoDetFuncionario);
			
			em.flush();
			setearDatos();

			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));
			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	private DatosEspecificos searchDatosEsp() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'REASIGNACION POR INSERCION MASIVA' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();

	}

	private DatosEspecificos searchTipoRegistro() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'MOVILIDAD' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'TIPOS DE REGISTRO INGRESOS Y MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();
	}

	private void actualizarDisponibles(String categoria, Integer codigo) {
		List<SinAnx> listaAnx = sinarhUtiles.obtenerListaSinAnx(null,
				new Integer(50), codigo, categoria, null);
		if (!listaAnx.isEmpty()) {
			SinAnx anx = new SinAnx();
			anx = listaAnx.get(0);
			anx.setCantDisponible(anx.getCantDisponible() + 1);
			em.merge(anx);
		}
	}
	private Boolean chequearObj(){
		for (EmpleadoConceptoPago conceptoPago : empleadoConceptoPagos) {
			if (conceptoPago.getObjCodigo() == 111) {
				if (conceptoPagoAnterior != null) {
					Integer valor = conceptoPago.getMonto().intValue()
							- conceptoPagoAnterior.getMonto().intValue();
					if (valor.intValue() < refMontoPromocion.getValorNum()
							.intValue()) {
						statusMessages
								.add(Severity.ERROR,
										"No cumple con monto mínimo de categoría superior. Debe registrarse a través de una 'Promoción sin concurso'");
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean chkDatos() {
		if (funcionario == null || funcionario.getIdEmpleadoPuesto() == null) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio: Funcionario");
			return false;
		}
//		if (empleadoConceptoPagos.isEmpty()) {
//			statusMessages.add(Severity.ERROR,
//					"Debe agregar almenos un Objeto de Gasto y Monto");
//			return false;
//		}
		if (configuracionUoDetNuevo == null || configuracionUoDetNuevo.getIdConfiguracionUoDet() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar la Unidad Organizacional para la reasignacion");
			return false;
		}
		
		return true;
	}

	public String toFindPersonaToView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/movilidadLaboral/gestionarReasignacionInsercionMasiva/GestionarReasignacionInsercionMasivaView&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
	}

	public void cambiarEstadoConfirmacion() {
		if (confirmacionEnPuesto) {
			mostrarPanelConfirmacion = false;
			idPlantaCargoDet = funcionario.getPlantaCargoDet()
					.getIdPlantaCargoDet();
			seleccionUtilFormController.setPlantaCargoDet(em.find(
					PlantaCargoDet.class, idPlantaCargoDet));
		} else {
			mostrarPanelConfirmacion = true;
			idPlantaCargoDet = null;
		}
	}
	
	public Integer calcNroOrden(PlantaCargoDet plantaCargoDet) {
		StringBuffer var = new StringBuffer();
		var.append("select count(planta.orden) from planificacion.planta_cargo_det planta ");
		var.append(" where planta.id_configuracion_uo_det = :idConfUODet ");
		var.append(" and planta.activo = TRUE  and planta.orden = :orden ");
		Query q = em.createNativeQuery(var.toString());
				
		q.setParameter("idConfUODet", plantaCargoDet.getConfiguracionUoDet().getIdConfiguracionUoDet());
		q.setParameter("orden", plantaCargoDet.getOrden());
		Object o = q.getSingleResult();
		if (o == null || o.toString().equals("0"))
			return plantaCargoDet.getOrden();
		
		StringBuffer var1 = new StringBuffer();
		var1.append("SELECT Max(plantacarg0_.orden) FROM planificacion.planta_cargo_det ");
		var1.append(" plantacarg0_ WHERE plantacarg0_.activo = TRUE AND plantacarg0_.id_configuracion_uo_det = :idUndOrg ");

	    q = em.createNativeQuery(var1.toString());
		q.setParameter("idUndOrg", plantaCargoDet.getConfiguracionUoDet().getIdConfiguracionUoDet());
		Object obj = q.getSingleResult();
		if (obj == null)
			return 1;

		return ((Integer) q.getSingleResult()).intValue() + 1;
	}
	 
	public void seleccionarConfiguracionUODet(Long idConfiguracionUODet){
		configuracionUoDetNuevo = em.find(
				ConfiguracionUoDet.class, idConfiguracionUODet);
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}

	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public int getSelectedRowPuesto() {
		return selectedRowPuesto;
	}

	public void setSelectedRowPuesto(int selectedRowPuesto) {
		this.selectedRowPuesto = selectedRowPuesto;
	}

	public boolean isHabUpdate() {
		return habUpdate;
	}

	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public boolean isHabPaisExp() {
		return habPaisExp;
	}

	public void setHabPaisExp(boolean habPaisExp) {
		this.habPaisExp = habPaisExp;
	}

	public boolean isHabBtnAddPersons() {
		return habBtnAddPersons;
	}

	public void setHabBtnAddPersons(boolean habBtnAddPersons) {
		this.habBtnAddPersons = habBtnAddPersons;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}

	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
	}

	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}

	public void setConfiguracionUoDetList(
			List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}

	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}

	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagos() {
		return empleadoConceptoPagos;
	}

	public void setEmpleadoConceptoPagos(
			List<EmpleadoConceptoPago> empleadoConceptoPagos) {
		this.empleadoConceptoPagos = empleadoConceptoPagos;
	}
	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}

	public Long getIdFuncionario() {
		return idFuncionario;
	}

	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getNombreNivelEntidad() {
		return nombreNivelEntidad;
	}

	public void setNombreNivelEntidad(String nombreNivelEntidad) {
		this.nombreNivelEntidad = nombreNivelEntidad;
	}

	public BigDecimal getCodNivelEntidad() {
		return codNivelEntidad;
	}

	public void setCodNivelEntidad(BigDecimal codNivelEntidad) {
		this.codNivelEntidad = codNivelEntidad;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagosFuncionario() {
		return empleadoConceptoPagosFuncionario;
	}

	public void setEmpleadoConceptoPagosFuncionario(
			List<EmpleadoConceptoPago> empleadoConceptoPagosFuncionario) {
		this.empleadoConceptoPagosFuncionario = empleadoConceptoPagosFuncionario;
	}

	public Boolean getConfirmacionEnPuesto() {
		return confirmacionEnPuesto;
	}

	public void setConfirmacionEnPuesto(Boolean confirmacionEnPuesto) {
		this.confirmacionEnPuesto = confirmacionEnPuesto;
	}

	public Boolean getMostrarPanelConfirmacion() {
		return mostrarPanelConfirmacion;
	}

	public void setMostrarPanelConfirmacion(Boolean mostrarPanelConfirmacion) {
		this.mostrarPanelConfirmacion = mostrarPanelConfirmacion;
	}

	public Referencias getRefMontoPromocion() {
		return refMontoPromocion;
	}

	public void setRefMontoPromocion(Referencias refMontoPromocion) {
		this.refMontoPromocion = refMontoPromocion;
	}

	public EmpleadoConceptoPago getConceptoPagoAnterior() {
		return conceptoPagoAnterior;
	}

	public void setConceptoPagoAnterior(
			EmpleadoConceptoPago conceptoPagoAnterior) {
		this.conceptoPagoAnterior = conceptoPagoAnterior;
	}
	
	public EmpleadoPuesto getFuncionarioNvo() {
		return funcionarioNvo;
	}

	public void setFuncionarioNvo(EmpleadoPuesto funcionarioNvo) {
		this.funcionarioNvo = funcionarioNvo;
	}

	public ConfiguracionUoDet getConfiguracionUoDetNuevo() {
		return configuracionUoDetNuevo;
	}

	public void setConfiguracionUoDetNuevo(
			ConfiguracionUoDet configuracionUoDetNuevo) {
		this.configuracionUoDetNuevo = configuracionUoDetNuevo;
	}

	public EmpleadoMovilidadCab getMovilidad() {
		return movilidad;
	}

	public void setMovilidad(EmpleadoMovilidadCab movilidad) {
		this.movilidad = movilidad;
	}

	
	

}
