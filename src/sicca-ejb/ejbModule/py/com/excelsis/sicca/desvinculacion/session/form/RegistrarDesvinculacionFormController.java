package py.com.excelsis.sicca.desvinculacion.session.form;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
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

import py.com.excelsis.sicca.desvinculacion.session.DesvinculacionHome;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Desvinculacion;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Inhabilitados;
import py.com.excelsis.sicca.entity.Jubilados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seleccion.session.form.AdmFecRecPosFC;
import py.com.excelsis.sicca.session.EmpleadoPuestoList;
import py.com.excelsis.sicca.session.PlantaCargoDetHome;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.form.UsuarioPortalFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("registrarDesvinculacionFormController")
public class RegistrarDesvinculacionFormController implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	
	@In(required = false, create = true)
	UsuarioPortalFormController usuarioPortalFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
	private Desvinculacion desvinculacion = new Desvinculacion();
	private DatosEspecificos motivoJubilacion = new DatosEspecificos();
	private Jubilados jubilados = new Jubilados();
	private Inhabilitados inhabilitados = new Inhabilitados();

	private Integer ordenUnidOrg;
	private Long idDesvinculacion;
	private Long idMotivoDesvinculacion;
	private Long idTipoJubilacion;
	private Long idEmpleadoPuesto;
	private Integer anhoActual;
	private String otro;
	private String ubicacionFisica;
	private String tipoJub;
	private Boolean inhabilitado;
	private Boolean mostrarPanelJubilacion;
	private Boolean mostrarOtro;
	private Boolean cuentaConCpt;
	private Date fechaDesvinculacion;
	private Integer edadFuncionario;

	private List<SelectItem> motivoDesvinculacionSelecItem = new ArrayList<SelectItem>();
	private List<SelectItem> tipoJubilacionSelecItem = new ArrayList<SelectItem>();

	/**
	 * Método que inicializa los datos
	 */
	public void init() {

		Date fechaActual = new Date();
		anhoActual = fechaActual.getYear() + 1900;
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		if (idDesvinculacion != null) {
			desvinculacion = new Desvinculacion();
			desvinculacion = em.find(Desvinculacion.class, idDesvinculacion);
		}
		if (desvinculacion.getIdDesvinculacion() == null) {
			if (idMotivoDesvinculacion == null) {
				updatedMotivoDesvinculacionSelectItem();
				updatedTipoJubilacionSelectItem();
				inhabilitado = true;
				mostrarOtro = false;
			}
			if (idEmpleadoPuesto != null)
				verificarDatosFuncionario();
		}
		if (desvinculacion.getIdDesvinculacion() != null) {

			updatedMotivoDesvinculacionSelectItem();
			idMotivoDesvinculacion = desvinculacion.getDatosEspecifMotivo()
					.getIdDatosEspecificos();
			empleadoPuesto = desvinculacion.getEmpleadoPuesto();
			fechaDesvinculacion = desvinculacion.getFechaDesvinculacion();
			esJubilacion();
			if (mostrarPanelJubilacion) {
				buscarDatosJubilados();
			}
		}
	}

	public void cargarCabecera() {
		grupoPuestosController = (GrupoPuestosController) Component
				.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController
				.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad
				.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	@SuppressWarnings("unchecked")
	private void buscarDatosJubilados() {
		String sql = "select jub.* " + "from desvinculacion.jubilados jub "
				+ "where jub.id_empleado_puesto = "
				+ empleadoPuesto.getIdEmpleadoPuesto();
		List<Jubilados> listaJubilados = new ArrayList<Jubilados>();
		listaJubilados = em.createNativeQuery(sql, Jubilados.class)
				.getResultList();
		if (listaJubilados != null && listaJubilados.size() > 0) {
			updatedTipoJubilacionSelectItem();
			if (listaJubilados.get(0).getDatosEspecifTipo() != null) {
				idTipoJubilacion = listaJubilados.get(0).getDatosEspecifTipo()
						.getIdDatosEspecificos();
				tipoJub = em.find(DatosEspecificos.class, idTipoJubilacion)
						.getDescripcion();
			}
			else
				tipoJub = "OTRO";
			otro = listaJubilados.get(0).getOtroTipo();
			inhabilitado = listaJubilados.get(0).getInhabilitado();
		}
	}

	private List<EmpleadoPuesto> empleadosAJubilar() {
		String cad = "select ep.* "
				+ "from general.empleado_puesto ep "
				+ "join general.persona p on p.id_persona = ep.id_persona "
				+ "join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = ep.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = cargo.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where ep.id_persona =  "
				+ empleadoPuesto.getPersona().getIdPersona()
				+ "and oee.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo();

		return em.createNativeQuery(cad, EmpleadoPuesto.class).getResultList();

	}

	/**
	 * Método que verifica los datos del funcionario seleccionado
	 */
	private void verificarDatosFuncionario() {
		empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
		if (idMotivoDesvinculacion != null) {
			motivoJubilacion = new DatosEspecificos();
			motivoJubilacion = em.find(DatosEspecificos.class,
					idMotivoDesvinculacion);
			// En caso de que el motivo sea retiro voluntario
			if (motivoJubilacion.getDescripcion().toUpperCase()
					.equals("RETIRO VOLUNTARIO")) {
				List<EmpleadoPuesto> lista = empleadosAJubilar();
				Integer resultado = 0;
				for (EmpleadoPuesto datos : lista) {
					if (datos.getFechaFin() != null) {
						Integer ini = datos.getFechaInicio().getYear() + 1900;
						Integer fin = datos.getFechaFin().getYear() + 1900;
						resultado += (fin - ini);
					} else {
						Integer ini = datos.getFechaInicio().getYear() + 1900;
						resultado += (anhoActual - ini);
					}
				}

				if (resultado < 2) {
					empleadoPuesto = new EmpleadoPuesto();
					statusMessages.clear();
					statusMessages.add(Severity.WARN, SeamResourceBundle
							.getBundle().getString("CU440_msg2"));
					return;
				}
				Integer edad = empleadoPuesto.getPersona().getFechaNacimiento()
						.getYear() + 1900;
				edadFuncionario = anhoActual - edad;
				if (edadFuncionario >= 65) {

					statusMessages.clear();
					statusMessages.add(Severity.WARN, SeamResourceBundle
							.getBundle().getString("CU440_msg3"));
					return;
				}
			}
			// En caso de que el retiro sea por Jubilacion
			if (motivoJubilacion.getDescripcion().toUpperCase()
					.equals("JUBILACION")) {
				cuentaConCpt = cuentaConCpt();
				if (!cuentaConCpt)
					mostrarOtro = true;
				if (idTipoJubilacion != null
						&& idTipoJubilacion.intValue() != 0) {
					Integer anho = empleadoPuesto.getPersona()
							.getFechaNacimiento().getYear() + 1900;
					Integer edad = anhoActual - anho;
					Integer valor = recuperarAnhosTipoJubilacion();
					if (valor.intValue() != edad.intValue()) {

						statusMessages.clear();
						String msg = SeamResourceBundle.getBundle().getString(
								"CU440_msg4")
								+ " "
								+ valor
								+ " "
								+ SeamResourceBundle.getBundle().getString(
										"CU440_msg5");
						statusMessages.add(Severity.WARN, msg);
						return;
					}
				}
			}

			// En caso de que el retiro sea por Revolucion en el cargo
			if (motivoJubilacion.getDescripcion().toUpperCase()
					.equals("REVOCACION O FENECIMIENTO CONTRATO")) {
				if (!cuentaConPuestoConfianza()) {
					empleadoPuesto = new EmpleadoPuesto();
					statusMessages.clear();
					statusMessages.add(Severity.ERROR, SeamResourceBundle
							.getBundle().getString("CU440_msg6"));
					return;
				}
			}

			// En caso de que el retiro sea rescision de contrato
			if (motivoJubilacion.getDescripcion().toUpperCase()
					.equals("RESCISION DE CONTRATOS")) {
				if (!empleadoPuesto.getContratado()) {
					empleadoPuesto = new EmpleadoPuesto();
					statusMessages.clear();
					statusMessages
							.add(Severity.ERROR,
									"El funcionario no cuenta con un Puesto del tipo CONTRATO");
					return;
				}

			}

		}

	}

	/**
	 * Método que verifica si el funcionario seleccionado cuenta con CPT
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Boolean cuentaConCpt() {
		String sql = "select cpt.* " + "from planificacion.planta_cargo_det p "
				+ "join planificacion.cpt cpt " + "on cpt.id_cpt = p.id_cpt "
				+ "join planificacion.tipo_cpt tipo "
				+ "on tipo.id_tipo_cpt = cpt.id_tipo_cpt "
				+ "where p.id_planta_cargo_det = "
				+ empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet()
				+ " and tipo.descripcion = 'CARRERA CIVIL'";
		List<Cpt> listaCpt = new ArrayList<Cpt>();
		listaCpt = em.createNativeQuery(sql, Cpt.class).getResultList();
		if (listaCpt == null || listaCpt.size() == 0)
			return false;
		return true;
	}

	/**
	 * Método que recupera la cantidad de años con que debe contar un
	 * funcionario para acceder al tipo de jubilacion seleccionada
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Integer recuperarAnhosTipoJubilacion() {
		String sql = "SELECT DE.* " + "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'TIPOS DE JUBILACION' "
				+ "AND DE.ACTIVO = TRUE  " + "AND DE.ID_DATOS_ESPECIFICOS = "
				+ idTipoJubilacion;
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();
		return lista.get(0).getValorNum();
	}

	/**
	 * Método que verifica si el funcionario seleccionado cuenta con puesto de
	 * confianza
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Boolean cuentaConPuestoConfianza() {
		String sql = "select tipo_nom.* "
				+ "from planificacion.det_tipo_nombramiento tipo_nom "
				+ "join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = tipo_nom.id_planta_cargo_det "
				+ "join planificacion.tipo_nombramiento nom "
				+ "on nom.id_tipo_nombramiento = tipo_nom.id_tipo_nombramiento "
				+ "where tipo_nom.tipo = 'PUESTO' "
				+ "and (nom.descripcion = 'NOMBRAMIENTO CONF. CARRERA' "
				+ "or nom.descripcion = 'NOMBRAMIENTO CONFIANZA' "
				+ "or nom.descripcion = 'ELECTIVO' "
				+ "or nom.descripcion = 'ELECTIVA') "
				+ "and cargo.id_planta_cargo_det = "
				+ empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet()
				+ " and cargo.activo is true";
		List<DetTipoNombramiento> listaDet = new ArrayList<DetTipoNombramiento>();
		listaDet = em.createNativeQuery(sql, DetTipoNombramiento.class)
				.getResultList();
		if (listaDet == null || listaDet.size() == 0)
			return false;
		return true;
	}

	/**
	 * Métodos que cargan los combos a ser desplegados
	 */
	// Combo motivo desvincualcion
	@SuppressWarnings("unchecked")
	public void updatedMotivoDesvinculacionSelectItem() {
		String cadena = " SELECT DE.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'MOTIVOS DE DESVINCULACION' "
				+ "AND DE.ACTIVO = TRUE  " + "AND DE.VALOR_ALF IS NULL  "
				+ " ORDER BY DE.DESCRIPCION";

		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(cadena, DatosEspecificos.class)
				.getResultList();
		motivoDesvinculacionSelecItem = new ArrayList<SelectItem>();
		motivoDesvinculacionSelecItem.add(new SelectItem(null,
				SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() > 0) {

			for (DatosEspecificos datos : lista) {
				motivoDesvinculacionSelecItem.add(new SelectItem(datos
						.getIdDatosEspecificos(), datos.getDescripcion()));
			}
		}
	}

	// Combo Tipo jubilacion
	@SuppressWarnings("unchecked")
	public void updatedTipoJubilacionSelectItem() {
		String cadena = " SELECT DE.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE	DG.DESCRIPCION = 'TIPOS DE JUBILACION' "
				+ "AND DE.ACTIVO = TRUE " + "ORDER BY DE.DESCRIPCION";

		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(cadena, DatosEspecificos.class)
				.getResultList();
		tipoJubilacionSelecItem = new ArrayList<SelectItem>();
		tipoJubilacionSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));

		if (lista.size() > 0) {
			for (DatosEspecificos datos : lista)
				tipoJubilacionSelecItem.add(new SelectItem(datos
						.getIdDatosEspecificos(), datos.getDescripcion()));
		}
		tipoJubilacionSelecItem.add(new SelectItem(0, "OTRO"));

	}

	/**
	 * Método que verifica si el tipo de Motivo seleccionado es Jubilacion
	 */
	public void esJubilacion() {
		if (idMotivoDesvinculacion != null) {
			motivoJubilacion = new DatosEspecificos();
			motivoJubilacion = em.find(DatosEspecificos.class,
					idMotivoDesvinculacion);
			if (motivoJubilacion.getDescripcion().toUpperCase()
					.equals("JUBILACION")) {
				mostrarPanelJubilacion = true;
				cuentaConCpt = true;
				return;
			}
		}
		mostrarPanelJubilacion = false;
	}

	public void esParaMostrarOtro() {
		if (idTipoJubilacion != null && idTipoJubilacion.intValue() == 0)
			mostrarOtro = true;
		else
			mostrarOtro = false;
	}

	/**
	 * Método que llama al cu 159 - Buscar Funcionario
	 * 
	 * @return
	 */
	public String getUrlToPageSearchFuncionario() {

		if (idMotivoDesvinculacion == null) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("CU440_msg1"));
			return null;
		}

		if (motivoJubilacion.getDescripcion().toUpperCase()
				.equals("JUBILACION")) {
			if (idTipoJubilacion == null) {
				statusMessages.clear();
				statusMessages.add(Severity.ERROR,
						"Debe seleccionar un Tipo de Jubilación");
				return null;
			}
		}
		return "/busquedas/funcionarios/EmpleadoPuestoList.xhtml?from=desvinculacion/registrarDesvinculacion/DesvinculacionEdit";
	}

	public void enviarMailRetiroVoluntario(Persona persona) {
		String cad = "SELECT REFERENCIAS.* "
				+ "FROM SELECCION.REFERENCIAS REFERENCIAS   " + "WHERE  "
				+ "REFERENCIAS.DOMINIO = 'EMAIL_COPIA_SFP'  "
				+ "AND REFERENCIAS.DESC_ABREV = 'DESV_RETIRO_VOL'  "
				+ "AND  REFERENCIAS.ACTIVO = TRUE";
		List<Referencias> listaRef = new ArrayList<Referencias>();

		listaRef = em.createNativeQuery(cad, Referencias.class).getResultList();

		String asunto = configuracionUoCab.getDenominacionUnidad()
				+ " – RETIRO VOLUNTARIO";
		String texto = "";
		try {
			texto = texto
					+ "<p align=\"justify\"> <b> Estimado/a:"
					+ "<p align=\"justify\">Le comunicamos que el OEE "
					+ configuracionUoCab.getDenominacionUnidad()
					+ " ha registrado una desvinculaci&oacute;n por RETIRO VOLUNTARIO a la Persona que no cumpli&oacute; con la edad establecida."
					+ "</p>" + "<p align=\"justify\">-Cédula de Identidad: "
					+ persona.getDocumentoIdentidad() + "</p>"
					+ "<p align=\"justify\">-País Exp. Doc.: "
					+ persona.getPaisByIdPaisExpedicionDoc().getDescripcion()
					+ "</p>" + "<p align=\"justify\">-Nombre y Apellido: "
					+ persona.getNombres() + " " + persona.getApellidos()
					+ "</p>"
					+ "<p align=\"justify\">-Motivo de Desvinculaci&oacute;n: "
					+ desvinculacion.getDatosEspecifMotivo().getDescripcion()
					+ "</p>" + "<p></p>" + "<p> Atentamente,</p> " + "<b>"
					+ configuracionUoCab.getDenominacionUnidad() + "</p></b>";
			for (Referencias r : listaRef) {
				usuarioPortalFormController.enviarMails(r.getDescLarga(),
						texto, asunto, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void save() {
		if (!check()) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Debe llenar los campos obligatorios");
			return;
		}
		if (desvinculacion.getMontoLiquidacion() != null
				&& desvinculacion.getMontoLiquidacion().intValue() < 0) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"El monto de la Liquidación no puede ser menor a cero");
			return;
		}
		em.clear();
		String separador = File.separator;
		ubicacionFisica = separador
				+ "SICCA"
				+ separador
				+ anhoActual
				+ separador
				+ "OEE"
				+ separador
				+ empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet()
						.getConfiguracionUoCab().getIdConfiguracionUo()
				+ separador
				+ "UO"
				+ separador
				+ empleadoPuesto.getPlantaCargoDet().getConfiguracionUoDet()
						.getIdConfiguracionUoDet() + separador + "EMP"
				+ separador + empleadoPuesto.getIdEmpleadoPuesto();
		empleadoPuesto.setActual(false);
		empleadoPuesto.setFechaFin(fechaDesvinculacion);
		empleadoPuesto.setFechaMod(new Date());
		empleadoPuesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
		try {
			em.merge(empleadoPuesto);
			em.flush();
			PlantaCargoDet puesto = new PlantaCargoDet();
			puesto = empleadoPuesto.getPlantaCargoDet();
			EstadoCab estadoCab = new EstadoCab();
			estadoCab = buscarEstadoVacante();
			puesto.setEstadoCab(estadoCab);
			puesto.setFechaMod(new Date());
			puesto.setEstadoDet(null);
			puesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(puesto);
			em.flush();
			HistoricosEstado historicosEstado = new HistoricosEstado();
			historicosEstado.setEstadoCab(estadoCab);
			historicosEstado.setFechaMod(new Date());
			historicosEstado.setPlantaCargoDet(puesto);
			historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.persist(historicosEstado);
			em.flush();
			desvinculacion.setFechaDesvinculacion(fechaDesvinculacion);
			desvinculacion.setDatosEspecifMotivo(em.find(
					DatosEspecificos.class, idMotivoDesvinculacion));
			desvinculacion.setEmpleadoPuesto(empleadoPuesto);
			desvinculacion.setFechaAlta(new Date());
			desvinculacion.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			
			em.persist(desvinculacion);
			if (motivoJubilacion.getDescripcion().equals("JUBILACION")) {
				jubilados = new Jubilados();
				jubilados.setEmpleadoPuesto(empleadoPuesto);
				jubilados.setPersona(empleadoPuesto.getPersona());
				jubilados.setConfiguracionUoCab(configuracionUoCab);
				jubilados.setFechaAlta(new Date());
				jubilados.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				jubilados.setDatosEspecifTipo(em.find(DatosEspecificos.class,
						idTipoJubilacion));
				jubilados.setInhabilitado(true);
				jubilados.setOtroTipo(otro);
				jubilados.setFecha(fechaDesvinculacion);
				em.persist(jubilados);
				em.flush();
				buscarPorMotivoJubilacion(empleadoPuesto.getPersona()
						.getIdPersona());

				
			}
			if (motivoJubilacion.getDescripcion().equals("RETIRO VOLUNTARIO")
					|| motivoJubilacion.getDescripcion()
							.equals("FALLECIMIENTO")
					|| motivoJubilacion.getDescripcion().equals(
							"CESANTIA POR INHABILIDAD FISICA O MENTAL")) {
				inhabilitados = new Inhabilitados();
				inhabilitados.setEmpleadoPuesto(empleadoPuesto);
				inhabilitados.setPersona(empleadoPuesto.getPersona());
				inhabilitados.setConfiguracionUoCab(configuracionUoCab);
				inhabilitados.setFechaAlta(new Date());
				inhabilitados.setFechaDesde(fechaDesvinculacion);
				inhabilitados.setInhabilitado(true);
				inhabilitados.setTipo("D");
				inhabilitados.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				if (motivoJubilacion.getDescripcion().equals(
						"RETIRO VOLUNTARIO")) {
					Integer anhoSumar = buscarAnhosRetiroVoluntario();
					Calendar calendario = Calendar.getInstance();
					calendario.add(Calendar.YEAR, anhoSumar);
					inhabilitados.setFechaHasta(calendario.getTime());
					// enviar email
					enviarMailRetiroVoluntario(empleadoPuesto.getPersona());
				}
				em.persist(inhabilitados);
				em.flush();
				if (motivoJubilacion.getDescripcion().equals("FALLECIMIENTO")) {
					buscarPorMotivoFallecimiento(empleadoPuesto.getPersona()
							.getIdPersona());
				}
			}
			aumentarCantidadDisponibleAnx(empleadoPuesto);
			em.flush();

		} catch (Exception e) {

			statusMessages.add(Severity.ERROR, e.getMessage());
			return;
		}
		statusMessages.clear();
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		statusMessages
				.add(Severity.WARN,
						"Si desea adjuntar un documento, presione el botón Anexos  si no, presione el botón Volver");
	}

	@SuppressWarnings("unchecked")
	private void buscarPorMotivoJubilacion(Long idPersona) {
		String sql = "select emp.* from general.empleado_puesto emp "
				+ "join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = emp.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = cargo.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where emp.id_persona = " + idPersona
				+ " and oee.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and emp.actual is true and emp.contratado is false";
		List<EmpleadoPuesto> listaEmpleado = new ArrayList<EmpleadoPuesto>();
		try {
			listaEmpleado = em.createNativeQuery(sql, EmpleadoPuesto.class)
					.getResultList();
			for (EmpleadoPuesto e : listaEmpleado) {
				// por cada registro actualiza la tabla Empleado Puesto
				e.setActual(false);
				e.setFechaMod(new Date());
				e.setUsuMod(usuarioLogueado.getCodigoUsuario());
				e.setFechaFin(fechaDesvinculacion);
				em.merge(e);
				em.flush();
				// por cada registro actualiza la tabla Planta Cargo Det
				PlantaCargoDet puesto = new PlantaCargoDet();
				puesto = e.getPlantaCargoDet();
				EstadoCab estadoCab = new EstadoCab();
				estadoCab = buscarEstadoVacante();
				puesto.setEstadoCab(estadoCab);
				puesto.setEstadoDet(null);
				puesto.setFechaMod(new Date());
				puesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puesto);
				em.flush();
				// Por cada registro de puesto actualizado guarda un histórico
				HistoricosEstado historicosEstado = new HistoricosEstado();
				historicosEstado.setEstadoCab(estadoCab);
				historicosEstado.setFechaMod(new Date());
				historicosEstado.setPlantaCargoDet(puesto);
				historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.persist(historicosEstado);
				em.flush();
				// Guarda una desvinculacion hija cuyo padre es la primera
				// desvinculación guardada
				Desvinculacion desvinculacionActual = new Desvinculacion();

				desvinculacionActual
						.setFechaDesvinculacion(fechaDesvinculacion);
				desvinculacionActual.setDatosEspecifMotivo(em.find(
						DatosEspecificos.class, idMotivoDesvinculacion));
				desvinculacionActual.setEmpleadoPuesto(em.find(
						EmpleadoPuesto.class, e.getIdEmpleadoPuesto()));
				desvinculacionActual.setDesvinculacion(desvinculacion);
				String obsJu = "SE DESVINCULA POR HABERSE JUBILADO. PERTENECE A LA DESVINCULACION PADRE EN EL PUESTO: "
						+ puesto.getDescripcion();
				desvinculacionActual.setObservacion(obsJu);

				if (desvinculacion.getConcepto() != null
						&& !desvinculacion.getConcepto().trim().isEmpty())
					desvinculacionActual.setConcepto(desvinculacion
							.getConcepto());
				desvinculacionActual.setFechaAlta(new Date());
				desvinculacionActual.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());

				em.persist(desvinculacionActual);
				em.flush();

			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@SuppressWarnings("unchecked")
	private void buscarPorMotivoFallecimiento(Long idPersona) {
		String sql = "select emp.* from general.empleado_puesto emp "
				+ "join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = emp.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = cargo.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
				+ "where emp.id_persona = " + idPersona
				+ " and oee.id_configuracion_uo = "
				+ configuracionUoCab.getIdConfiguracionUo()
				+ " and emp.actual is true";
		List<EmpleadoPuesto> listaEmpleado = new ArrayList<EmpleadoPuesto>();
		try {
			listaEmpleado = em.createNativeQuery(sql, EmpleadoPuesto.class)
					.getResultList();
			for (EmpleadoPuesto e : listaEmpleado) {
				// por cada registro actualiza la tabla Empleado Puesto
				e.setActual(false);
				e.setFechaMod(new Date());
				e.setUsuMod(usuarioLogueado.getCodigoUsuario());
				e.setFechaFin(fechaDesvinculacion);
				em.merge(e);
				em.flush();
				// por cada registro actualiza la tabla Planta Cargo Det
				PlantaCargoDet puesto = new PlantaCargoDet();
				puesto = e.getPlantaCargoDet();
				EstadoCab estadoCab = new EstadoCab();
				estadoCab = buscarEstadoVacante();
				puesto.setEstadoCab(estadoCab);
				puesto.setFechaMod(new Date());
				puesto.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.merge(puesto);
				em.flush();
				// Por cada registro de puesto actualizado guarda un histórico
				HistoricosEstado historicosEstado = new HistoricosEstado();
				historicosEstado.setEstadoCab(estadoCab);
				historicosEstado.setFechaMod(new Date());
				historicosEstado.setPlantaCargoDet(puesto);
				historicosEstado.setUsuMod(usuarioLogueado.getCodigoUsuario());
				em.persist(historicosEstado);
				em.flush();
				// Guarda una desvinculacion hija cuyo padre es la primera
				// desvinculación guardada
				Desvinculacion desvinculacionActual = new Desvinculacion();
				desvinculacionActual
						.setFechaDesvinculacion(fechaDesvinculacion);
				desvinculacionActual.setDatosEspecifMotivo(em.find(
						DatosEspecificos.class, idMotivoDesvinculacion));

				desvinculacionActual.setEmpleadoPuesto(em.find(
						EmpleadoPuesto.class, e.getIdEmpleadoPuesto()));
				desvinculacionActual.setDesvinculacion(desvinculacion);
				desvinculacionActual
						.setObservacion("SE DESVINCULA POR HABER FALLECIDO. PERTENECE A LA DESVINCULACION PADRE EN EL PUESTO: "
								+ puesto.getDescripcion());
				if (desvinculacion.getConcepto() != null
						&& !desvinculacion.getConcepto().trim().isEmpty())
					desvinculacionActual.setConcepto(desvinculacion
							.getConcepto());
				desvinculacionActual.setFechaAlta(new Date());
				desvinculacionActual.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());

				em.persist(desvinculacionActual);
				em.flush();

			}
			// Inactiva a la persona desvinculada
			Persona pers = new Persona();
			pers = em.find(Persona.class, idPersona);
			pers.setActivo(false);
			pers.setFechaMod(new Date());
			pers.setObsMotivo("FALLECIMIENTO");
			pers.setDatosEspecificosMotivo(obtenerMotivoFa());
			pers.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(pers);
			em.flush();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private DatosEspecificos obtenerMotivoFa() {
		Query q = em
				.createQuery("select d from DatosEspecificos d "
						+ "where d.datosGenerales.descripcion = 'MOTIVO INACTIVACION PERSONAS' "
						+ "and d.descripcion = 'FALLECIMIENTO'");
		List<DatosEspecificos> l = q.getResultList();
		if (l.isEmpty())
			return null;
		return l.get(0);
	}

	// Incrementar una unidad a la columna cant_disponible que cumpla
	// con las condiciones
	@SuppressWarnings("unchecked")
	private void aumentarCantidadDisponibleAnx(EmpleadoPuesto empleado) {
		Date fechaActual = new Date();
		Integer anhoActual = fechaActual.getYear() + 1900;
		String sql = "select anx.* "
				+ "from sinarh.sin_anx anx, general.empleado_concepto_pago pago "
				+ "join general.empleado_puesto empleado "
				+ "on empleado.id_empleado_puesto = pago.id_empleado_puesto "
				+ "join planificacion.planta_cargo_det p "
				+ "on empleado.id_planta_cargo_det = p.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det det "
				+ "on det.id_configuracion_uo_det = p.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab uo "
				+ "on uo.id_configuracion_uo = det.id_configuracion_uo "
				+ "where anx.ctg_codigo = pago.categoria "
				+ "and anx.obj_codigo = pago.obj_codigo "
				+ "and ani_aniopre = " + anhoActual
				+ " and empleado.id_empleado_puesto = "
				+ empleado.getIdEmpleadoPuesto()
				+ " and anx.nen_codigo ||'.'|| " + "anx.ent_codigo ||'.'|| "
				+ "anx.tip_codigo ||'.'|| " + "anx.pro_codigo = uo.codigo_sinarh ";
		
		List<SinAnx> listaAnx = new ArrayList<SinAnx>();
		try {
			listaAnx = em.createNativeQuery(sql, SinAnx.class).getResultList();
			for (SinAnx anx : listaAnx) {
				Integer cant = anx.getCantDisponible();
				cant = cant + 1;
				anx.setCantDisponible(cant);
				em.merge(anx);
			
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unchecked")
	private Integer buscarAnhosRetiroVoluntario() {
		String sql = "SELECT DE.* " + "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'MOTIVOS DE DESVINCULACION' "
				+ "AND DE.ACTIVO = TRUE "
				+ "AND DE.DESCRIPCION = 'RETIRO VOLUNTARIO'";
		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(sql, DatosEspecificos.class)
				.getResultList();
		if (lista == null || lista.size() == 0)
			return new Integer(0);
		return lista.get(0).getValorNum();
	}

	@SuppressWarnings("unchecked")
	private EstadoCab buscarEstadoVacante() {
		String sql = "select cab.* " + "from planificacion.estado_cab cab "
				+ "where cab.descripcion = 'VACANTE'";
		List<EstadoCab> listaEstado = new ArrayList<EstadoCab>();
		listaEstado = em.createNativeQuery(sql, EstadoCab.class)
				.getResultList();
		if (listaEstado != null && listaEstado.size() > 0)
			return listaEstado.get(0);
		return null;
	}

	private Boolean check() {
		if (idMotivoDesvinculacion == null || empleadoPuesto == null
				|| empleadoPuesto.getIdEmpleadoPuesto() == null
				|| fechaDesvinculacion == null)
			return false;
		return true;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public List<SelectItem> getMotivoDesvinculacionSelecItem() {
		return motivoDesvinculacionSelecItem;
	}

	public void setMotivoDesvinculacionSelecItem(
			List<SelectItem> motivoDesvinculacionSelecItem) {
		this.motivoDesvinculacionSelecItem = motivoDesvinculacionSelecItem;
	}

	public Long getIdMotivoDesvinculacion() {
		return idMotivoDesvinculacion;
	}

	public void setIdMotivoDesvinculacion(Long idMotivoDesvinculacion) {
		this.idMotivoDesvinculacion = idMotivoDesvinculacion;
	}

	public List<SelectItem> getTipoJubilacionSelecItem() {
		return tipoJubilacionSelecItem;
	}

	public void setTipoJubilacionSelecItem(
			List<SelectItem> tipoJubilacionSelecItem) {
		this.tipoJubilacionSelecItem = tipoJubilacionSelecItem;
	}

	public Long getIdTipoJubilacion() {
		return idTipoJubilacion;
	}

	public void setIdTipoJubilacion(Long idTipoJubilacion) {
		this.idTipoJubilacion = idTipoJubilacion;
	}

	public String getOtro() {
		return otro;
	}

	public void setOtro(String otro) {
		this.otro = otro;
	}

	public Boolean getInhabilitado() {
		return inhabilitado;
	}

	public void setInhabilitado(Boolean inhabilitado) {
		this.inhabilitado = inhabilitado;
	}

	public Boolean getMostrarPanelJubilacion() {
		return mostrarPanelJubilacion;
	}

	public void setMostrarPanelJubilacion(Boolean mostrarPanelJubilacion) {
		this.mostrarPanelJubilacion = mostrarPanelJubilacion;
	}

	public Boolean getMostrarOtro() {
		return mostrarOtro;
	}

	public void setMostrarOtro(Boolean mostrarOtro) {
		this.mostrarOtro = mostrarOtro;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public Desvinculacion getDesvinculacion() {
		return desvinculacion;
	}

	public void setDesvinculacion(Desvinculacion desvinculacion) {
		this.desvinculacion = desvinculacion;
	}

	public Date getFechaDesvinculacion() {
		return fechaDesvinculacion;
	}

	public void setFechaDesvinculacion(Date fechaDesvinculacion) {
		this.fechaDesvinculacion = fechaDesvinculacion;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public Boolean getCuentaConCpt() {
		return cuentaConCpt;
	}

	public void setCuentaConCpt(Boolean cuentaConCpt) {
		this.cuentaConCpt = cuentaConCpt;
	}

	public DatosEspecificos getMotivoJubilacion() {
		return motivoJubilacion;
	}

	public void setMotivoJubilacion(DatosEspecificos motivoJubilacion) {
		this.motivoJubilacion = motivoJubilacion;
	}

	public Jubilados getJubilados() {
		return jubilados;
	}

	public void setJubilados(Jubilados jubilados) {
		this.jubilados = jubilados;
	}

	public Inhabilitados getInhabilitados() {
		return inhabilitados;
	}

	public void setInhabilitados(Inhabilitados inhabilitados) {
		this.inhabilitados = inhabilitados;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public Integer getEdadFuncionario() {
		return edadFuncionario;
	}

	public void setEdadFuncionario(Integer edadFuncionario) {
		this.edadFuncionario = edadFuncionario;
	}

	public String getTipoJub() {
		return tipoJub;
	}

	public void setTipoJub(String tipoJub) {
		this.tipoJub = tipoJub;
	}

	public Long getIdDesvinculacion() {
		return idDesvinculacion;
	}

	public void setIdDesvinculacion(Long idDesvinculacion) {
		this.idDesvinculacion = idDesvinculacion;
	}

}
