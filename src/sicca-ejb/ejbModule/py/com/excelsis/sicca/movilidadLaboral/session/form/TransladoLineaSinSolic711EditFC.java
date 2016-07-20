package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.Query;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jfree.data.time.MovingAverage;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;
import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Name("transladoLineaSinSolic711EditFC")
@Scope(ScopeType.CONVERSATION)
public class TransladoLineaSinSolic711EditFC implements Serializable {

	private static final long serialVersionUID = 3174062745467083893L;

	@In(create = true)
	JpaResourceBean jpaResourceBean;

	@In
	StatusMessages statusMessages;

	@In(value = "entityManager")
	EntityManager em;

	@In(required = false)
	Usuario usuarioLogueado;

	private Long idRedCap;

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
	ValidadorController validadorController;

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private boolean habUpdate = false;
	private Persona persona = new Persona();
	private Long idPersona = null;
	private Boolean primeraEntrada = true;
	private Long idEmpleadoPuesto;
	private List<ConfiguracionUoDet> configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList = new ArrayList<PlantaCargoDet>();
	private Integer monto;
	private EmpleadoPuesto empleadoPuesto;
	private EmpleadoPuesto funcionario;
	private Long idFuncionario;
	private List<ReferenciaAdjuntos> adjuntos = new ArrayList<ReferenciaAdjuntos>();
	private Date fechaInicio;
	private Long idPlantaCargoDet;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private SinAnx sinAnxSeleccionado = null;
	private String observacion;
	private List<EmpleadoMovilidadAnexo> conceptoPagosActual =
		new ArrayList<EmpleadoMovilidadAnexo>();
	private List<EmpleadoMovilidadAnexo> conceptoPagosOrigen =
		new ArrayList<EmpleadoMovilidadAnexo>();
	private BuscadorDocsFC buscadorDocsDesctino;
	private Integer nroDoc;
	private Date fechaDoc;
	private Long idTipoDoc;
	private String fNameMostrar;
	private boolean habDescargar;
	private String fName;
	private byte[] uFile = null;
	private String cType = null;
	private Documentos docDecreto;
	private Documentos docActoAdmin;
	private UploadItem fileActoAdmin;
	String nombrePantalla = "transladoLineaSinSolic711_edit";

	public void init() throws Exception {
		if (idFuncionario != null) {

			funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
			em.refresh(funcionario);
			empleadoPuesto = funcionario;
			if (!funcionario.getPlantaCargoDet().getPermanente()) {
				statusMessages.add(Severity.ERROR, "El funcionario Seleccionado no es Permanente");
				funcionario = new EmpleadoPuesto();
				return;
			} else {
				idPersona = funcionario.getPersona().getIdPersona();
				persona = em.find(Persona.class, idPersona);
				obtenerCatOrigen();
				cargarNEOFuncionario();
			}

		}

		if (buscadorDocsDesctino == null)
			buscadorDocsDesctino =
				(BuscadorDocsFC) Component.getInstance(BuscadorDocsFC.class, true);
		
		cargarNiveentidadOee();
		
		if (primeraEntrada) {
			em.clear();
			//cargarNiveentidadOee();
			selectedRow = -1;
			selectedRowPuesto = -1;
			empleadoPuesto = new EmpleadoPuesto();
			primeraEntrada = false;
			setearDatos();
		}

	}

	private void cargarNEOFuncionario() {
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilAnterior.setIdUnidadOrganizativa(funcionario.getPlantaCargoDet().getConfiguracionUoDet().getIdConfiguracionUoDet());
		entidadOeeUtilAnterior.init2();
	}

	@SuppressWarnings("unchecked")
	private void obtenerCatOrigen() {
		conceptoPagosOrigen = new ArrayList<EmpleadoMovilidadAnexo>();
		/*List<EmpleadoConceptoPago> m =
			em.createQuery("Select e from EmpleadoConceptoPago e "
				+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpleadoPuesto").setParameter("idEmpleadoPuesto", idFuncionario).getResultList();*/
		Query q = em.createQuery("Select e from EmpleadoConceptoPago e "
				+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpleadoPuesto");
		q.setParameter("idEmpleadoPuesto", idFuncionario);
		List<EmpleadoConceptoPago> m = q.getResultList();
		
		for (EmpleadoConceptoPago empleadoConceptoPago : m) {
			EmpleadoMovilidadAnexo movilidadAnexo = new EmpleadoMovilidadAnexo();
			movilidadAnexo.setCategoria(empleadoConceptoPago.getCategoria());
			movilidadAnexo.setMonto(empleadoConceptoPago.getMonto());
			movilidadAnexo.setObjCodigo(empleadoConceptoPago.getObjCodigo());
			conceptoPagosOrigen.add(movilidadAnexo);
		}
	}

	public void initVer() throws Exception {
		if (idEmpleadoPuesto != null) {
			if (!seguridadUtilFormController.validarInput(idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(), null)) {
				return;
			}
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);

		}

		obtenerDatos();
	}

	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList =
			em.createQuery("Select det from ConfiguracionUoDet det "
				+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE and det.activo=TRUE order by det.orden asc").setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab()).getResultList();
	}

	public String toFindPersonaView() {

		String from = "";
		if (idEmpleadoPuesto == null)
			from = "/movilidadLaboral/transladoLineaSinSolic/TransladoLineaSinSolic711Edit";
		else
			from = "/movilidadLaboral/transladoLineaSinSolic/TransladoLineaSinSolic711";
		return "/seleccion/persona/Persona.xhtml?personaFrom=" + from + "&personaIdPersona="
			+ funcionario.getPersona().getIdPersona() + "&conversationPropagation=join";
	}

	/**
	 * Obtener registros de Puestos de PLANTA_CARGO_DET del idConfiguracionUoDet seleccionado
	 **/
	@SuppressWarnings("unchecked")
	public void obtenerPuestos(int index) {

		plantaCargoDetList =
			em.createQuery("Select p from PlantaCargoDet p "
				+ " where p.configuracionUoDet.idConfiguracionUoDet=:idDet and p.activo=true"
				+ " and p.estadoDet is null and p.estadoCab.idEstadoCab=:idEstadoCab ").setParameter("idEstadoCab", getIdEstadoCabVacante()).setParameter("idDet", configuracionUoDetList.get(index).getIdConfiguracionUoDet()).getResultList();
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
		setearConPago();
	}

	public void seleccionarPuesto(Long idPuesto, int index) {
		ConfiguracionUoCab cab =
			em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = idPuesto;
		setearConPago();
		setSelectedRowPuesto(index);
	}
	
	public void seleccionarPuestoInit(PlantaCargoDet puesto) {
		ConfiguracionUoCab cab =
			em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(puesto);
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = puesto.getId();
		setearConPago();
	}

	public String save() {
		try {
			if (!chkDatos("save"))
				return null;
			PlantaCargoDet puestoSelec = em.find(PlantaCargoDet.class, idPlantaCargoDet);

			/**
			 * Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
			 */
			em.clear();
			funcionario.setPin(seleccionUtilFormController.generarPIN());
			funcionario.setActual(false);
			funcionario.setFechaFin(new Date());
			funcionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
			funcionario.setFechaMod(new Date());
			em.merge(funcionario);
			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al Funcionario seleccionado
			 */
			PlantaCargoDet plantaCargoDetFuncionario =
				em.find(PlantaCargoDet.class, funcionario.getPlantaCargoDet().getIdPlantaCargoDet());
			plantaCargoDetFuncionario.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("LIBRE"));
			plantaCargoDetFuncionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
			plantaCargoDetFuncionario.setFechaMod(new Date());
			plantaCargoDetFuncionario.setEstadoDet(null);
			em.merge(plantaCargoDetFuncionario);
			/***
			 * • Registra el histórico de cambios de estados del Puesto anterior en la tabla HISTORICOS_ESTADO , registrando el estado actual
			 */
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("LIBRE"));
			historico.setFechaMod(new Date());
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historico.setPlantaCargoDet(plantaCargoDetFuncionario);
			em.persist(historico);

			/**
			 * se guardan los datos en la tabla EMPLEADO_PUESTO
			 */
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "TRASLADO DEFINITIVO CON LINEAS DE CARGO"));
			empleadoPuesto.setFechaInicio(fechaInicio);
			empleadoPuesto.setActual(true);
			empleadoPuesto.setActivo(true);
			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
			empleadoPuesto.setContratado(false);
			empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE"));
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "MOVILIDAD"));
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class, idPlantaCargoDet));
			empleadoPuesto.setPersona(persona);
			empleadoPuesto.setObservacion(observacion);
			empleadoPuesto.setIncidenAntiguedad(true);

			em.persist(empleadoPuesto);
			/**
			 * Cambiar el estado del puesto a OCUPADO
			 */
			puestoSelec.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
			puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
			puestoSelec.setFechaMod(new Date());
			em.merge(puestoSelec);

			/**
			 * Generar un registro del cambio de estado en la tabla HISTORICOS_ESTADO
			 */
			HistoricosEstado historial = new HistoricosEstado();
			historial.setEstadoCab(seleccionUtilFormController.buscarEstadoCab("OCUPADO"));
			historial.setFechaMod(new Date());
			historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historial.setPlantaCargoDet(puestoSelec);

			em.persist(historial);

			/**
			 * Registrar datos de movilidad. en la tabla EMPLEADO_MOVILIDAD_CAB
			 */
			EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
			empleadoMovilidadCab.setActivo(true);
			empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);
			empleadoMovilidadCab.setEmpleadoPuestoNuevo(empleadoPuesto);
			empleadoMovilidadCab.setMovilidadSicca(false);
			empleadoMovilidadCab.setFechaInicio(fechaInicio);
			empleadoMovilidadCab.setFechaAlta(new Date());
			empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoMovilidadCab.setObservacion(observacion);
			em.persist(empleadoMovilidadCab);

			/**
			 * • Gestionar objetos y categorías para el puesto ocupado
			 */
			// De Categorías/remuneraciones en OEE Origen
			for (EmpleadoMovilidadAnexo movilidadAnexo : conceptoPagosOrigen) {
				EmpleadoConceptoPago conceptoPago = new EmpleadoConceptoPago();
				conceptoPago.setEmpleadoPuesto(empleadoPuesto);
				conceptoPago.setFechaAlta(new Date());
				conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				conceptoPago.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));
				conceptoPago.setObjCodigo(movilidadAnexo.getObjCodigo());
				conceptoPago.setMonto(movilidadAnexo.getMonto());
				conceptoPago.setCategoria(movilidadAnexo.getCategoria());
				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 */
				if (conceptoPago.getCategoria() != null && sinAnxSeleccionado != null) {
					SinAnx anx = em.find(SinAnx.class, sinAnxSeleccionado.getIdAnx());
					anx.setCantDisponible(anx.getCantDisponible() - 1);
					em.merge(anx);
				}
				em.persist(conceptoPago);
				// Inserta registro en EMPLEADO_MOVILIDAD ANEXO

				movilidadAnexo.setAnexoOee("O");
				movilidadAnexo.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				movilidadAnexo.setFechaAlta(new Date());
				movilidadAnexo.setEmpleadoMovilidadCab(empleadoMovilidadCab);
				movilidadAnexo.setActivo(true);
				em.persist(movilidadAnexo);
			}
			// De Categorías/remuneraciones en OEE Destino
			for (EmpleadoMovilidadAnexo movilidadAnexoDestino : conceptoPagosActual) {
				EmpleadoConceptoPago conceptoPago = new EmpleadoConceptoPago();
				conceptoPago.setEmpleadoPuesto(empleadoPuesto);
				conceptoPago.setFechaAlta(new Date());
				conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				conceptoPago.setAnho(Integer.parseInt(sdfAnio.format(fechaInicio)));
				conceptoPago.setObjCodigo(movilidadAnexoDestino.getObjCodigo());
				conceptoPago.setMonto(movilidadAnexoDestino.getMonto());
				conceptoPago.setCategoria(movilidadAnexoDestino.getCategoria());
				em.persist(conceptoPago);
				// Inserta registro en EMPLEADO_MOVILIDAD ANEXO
				movilidadAnexoDestino.setAnexoOee("D");
				movilidadAnexoDestino.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				movilidadAnexoDestino.setFechaAlta(new Date());
				movilidadAnexoDestino.setEmpleadoMovilidadCab(empleadoMovilidadCab);
				movilidadAnexoDestino.setActivo(true);
				em.persist(movilidadAnexoDestino);
			}

			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok Insertar registro en Documentos y el archivo pdf *
			 */
			// Por Acto administrativo de Entidad Origen
			Long idDocOrigen = null;
			if (fileActoAdmin != null) {

				idDocOrigen =
					guardarAdjuntos(fName, fileActoAdmin.getFileSize(), fileActoAdmin.getContentType(), fileActoAdmin, idTipoDoc, null);
				if (idDocOrigen == null)
					return null;
				Documentos doc = em.find(Documentos.class, idDocOrigen);
				doc.setNroDocumento(nroDoc);
				doc.setAnhoDocumento(Integer.parseInt(sdfAnio.format(fechaDoc)));
				doc.setFechaDoc(fechaDoc);
				em.merge(doc);

			}
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 */

			insertarRerAdjunto(idDocOrigen);

			// Por Acto administrativo de Entidad Destino
			Long idDocDestino = null;
			if (buscadorDocsDesctino.getFileActoAdmin() != null) {

				idDocDestino =
					guardarAdjuntos(buscadorDocsDesctino.getfName(), buscadorDocsDesctino.getFileActoAdmin().getFileSize(), buscadorDocsDesctino.getFileActoAdmin().getContentType(), buscadorDocsDesctino.getFileActoAdmin(), buscadorDocsDesctino.getIdTipoDoc(), null);
				if (idDocDestino == null)
					return null;
				Documentos doc = em.find(Documentos.class, idDocDestino);
				doc.setNroDocumento(buscadorDocsDesctino.getNroDoc());
				doc.setAnhoDocumento(Integer.parseInt(sdfAnio.format(buscadorDocsDesctino.getFechaDoc())));
				doc.setFechaDoc(buscadorDocsDesctino.getFechaDoc());
				em.merge(doc);

			}
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 */

			insertarRerAdjunto(idDocDestino);

			em.flush();

			setearDatos();
			primeraEntrada = true;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

	public List<SelectItem> updateTipoDocSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoDocumento())
			si.add(new SelectItem(o.getIdDatosEspecificos(), "" + o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoDocumento() {
		try {
			List<DatosEspecificos> datosEspecificosLists =
				em.createQuery("Select d from DatosEspecificos d "
					+ " where d.descripcion like 'RESOLUCION' and d.valorAlf like 'RES' and d.activo=true order by d.descripcion").getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	/*public void eliminar(int index) {
		conceptoPagosActual.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
	}*/
	
	public void agregar() {
		if (seleccionUtilFormController.getCodigoObj() == null
			|| seleccionUtilFormController.getValorObj() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Cod. Objeto Gasto");
			return;
		}
		
		if (seleccionUtilFormController.getMonto() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Monto");
			return;
		}
		if (seleccionUtilFormController.getMonto().intValue() < 0) {
			statusMessages.add(Severity.ERROR, "El Monto debe ser mayor a cero");
			return;
		}
		EmpleadoMovilidadAnexo empleadoMovilidadAnexo = new EmpleadoMovilidadAnexo();
		empleadoMovilidadAnexo.setObjCodigo(seleccionUtilFormController.getCodigoObj());
		if (seleccionUtilFormController.getCategoria() != null)
			empleadoMovilidadAnexo.setCategoria(seleccionUtilFormController.getCodigoCategoria());
		empleadoMovilidadAnexo.setMonto(seleccionUtilFormController.getMonto());
		conceptoPagosActual.add(empleadoMovilidadAnexo);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		setearConPago();
	}
	
	public void agregarInit() {
		EmpleadoMovilidadAnexo empleadoMovilidadAnexo = new EmpleadoMovilidadAnexo();
		empleadoMovilidadAnexo.setObjCodigo(seleccionUtilFormController.getCodigoObj());
		if (seleccionUtilFormController.getCategoria() != null)
			empleadoMovilidadAnexo.setCategoria(seleccionUtilFormController.getCodigoCategoria());
		empleadoMovilidadAnexo.setMonto(seleccionUtilFormController.getMonto());
		conceptoPagosActual.add(empleadoMovilidadAnexo);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

		setearConPago();
	}

	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto administrativo
	 */
	public void insertarRerAdjunto(Long idDocumento) {
		ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
		referenciaAdjuntos.setPersona(persona);
		referenciaAdjuntos.setDocumentos(em.find(Documentos.class, idDocumento));
		referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		if(actividadProcesoActual() != null){
			referenciaAdjuntos.setActividadProceso(actividadProcesoActual());
		}
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}

	public Long guardarAdjuntos(String fileName, int fileSize, String contentType, UploadItem file, Long idTipoDoc, Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			nombrePantalla = "transladoLineaSinSolic711_edit";
			String direccionFisica = "";
			String sf = File.separator;
			direccionFisica =
				sf + "SICCA" + sf + anio + sf + "OEE" + sf + nivelEntidadOeeUtil.getIdConfigCab()
					+ sf + "MOVILIDAD";

			idDocuGenerado =
				admDocAdjuntoFormController.guardarDoc(file, direccionFisica, nombrePantalla, idTipoDoc, nombreTabla, idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void setearConPago() {
		sinAnxSeleccionado = seleccionUtilFormController.getSinAnx();
		seleccionUtilFormController.setearValoresObjetosGasto();
	}

	private Long getIdEstadoCabVacante() {
		EstadoCab cab = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		return cab.getIdEstadoCab();
	}

	public boolean chkDatos(String op) {
		/*if (nroDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Numero de Documento");
			return false;
		}
		if (fechaDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (idTipoDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (docDecreto == null && fileActoAdmin == null) {
			statusMessages.add(Severity.ERROR, "Debe Inrgesar un archivo, verifique");
			return false;
		}*/
		if (buscadorDocsDesctino.getNroDoc() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Numero de Documento");
			return false;
		}
		if (buscadorDocsDesctino.getFechaDoc() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (buscadorDocsDesctino.getIdTipoDoc() == null) {
			statusMessages.add(Severity.ERROR, "Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (buscadorDocsDesctino.getDocDecreto() == null
			&& buscadorDocsDesctino.getFileActoAdmin() == null) {
			statusMessages.add(Severity.ERROR, "Debe Inrgesar un archivo, verifique");
			return false;
		}

		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "No se ingresó el dato correspondiente al campo obligatorio: Persona");
			return false;
		}
		if (conceptoPagosActual.isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe agregar almenos un Objeto de Gasto y Monto");
			return false;
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private void obtenerDatos() {

		fechaInicio = empleadoPuesto.getFechaInicio();
		adjuntos =
			em.createQuery("Select r from ReferenciaAdjuntos r " + " where r.idRegistroTabla="
				+ idEmpleadoPuesto).getResultList();

		plantaCargoDetView =
			em.find(PlantaCargoDet.class, empleadoPuesto.getPlantaCargoDet().getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet =
			em.find(ConfiguracionUoDet.class, plantaCargoDetView.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init();
		/**
		 * El nuevo puesto destino
		 */
		catDestino();

		/**
		 * datos anteriores
		 */
		List<EmpleadoMovilidadCab> mov =
			em.createQuery("Select r from EmpleadoMovilidadCab r "
				+ " where r.empleadoPuestoNuevo.idEmpleadoPuesto=" + idEmpleadoPuesto).getResultList();
		if (!mov.isEmpty()) {
			idFuncionario = mov.get(0).getEmpleadoPuestoAnterior().getIdEmpleadoPuesto();
			funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
			catOrigen();
			cargarNEOFuncionario();
		}
	}

	@SuppressWarnings("unchecked")
	private void catOrigen() {
		conceptoPagosOrigen =
			em.createQuery("Select e from EmpleadoMovilidadAnexo e "
				+ " where e.empleadoMovilidadCab.empleadoPuestoNuevo.idEmpleadoPuesto=:idEmpleadoPuesto"
				+ " and e.activo=true and e.anexoOee='O' ").setParameter("idEmpleadoPuesto", idEmpleadoPuesto).getResultList();
	}

	@SuppressWarnings("unchecked")
	private void catDestino() {
		conceptoPagosActual =
			em.createQuery("Select e from EmpleadoMovilidadAnexo e "
				+ " where e.empleadoMovilidadCab.empleadoPuestoNuevo.idEmpleadoPuesto=:idEmpleadoPuesto "
				+ " and  e.activo=true and e.anexoOee='D'").setParameter("idEmpleadoPuesto", idEmpleadoPuesto).getResultList();
	}

	public void setearDatos() {
		persona = new Persona();
		funcionario = new EmpleadoPuesto();
		idFuncionario = null;
		observacion = null;
		plantaCargoDetList = new ArrayList<PlantaCargoDet>();
		conceptoPagosActual = new Vector<EmpleadoMovilidadAnexo>();
		conceptoPagosOrigen = new Vector<EmpleadoMovilidadAnexo>();
	}

	@SuppressWarnings("static-access")
	public void descargarDocBD(Long id) throws FileNotFoundException, IOException {
		Documentos doc = em.find(Documentos.class, id);
		if (doc.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		} else {
			statusMessages.add(Severity.ERROR, "No existe documento que descargar");
		}
	}

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	public String getRowPuestoClass(int currentRow) {
		if (selectedRowPuesto == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
	}

	public void adjuntarDoc() {
		if (!precondAdjuntarDoc())
			return;
		docActoAdmin = docDecreto;
		fileActoAdmin =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);
		fNameMostrar = fName;

		statusMessages.add(Severity.INFO, "Documento Adjuntado ");

	}

	@SuppressWarnings("static-access")
	public void descargarDoc() throws FileNotFoundException, IOException {
		if (docDecreto != null) {
			String resp =
				AdmDocAdjuntoFormController.abrirDocumentoFromCU(docDecreto.getIdDocumento(), usuarioLogueado.getIdUsuario());
			if (!resp.equalsIgnoreCase("OK")) {
				statusMessages.add(Severity.ERROR, resp);
			}
		} else if (fileActoAdmin != null) {
			admDocAdjuntoFormController.enviarArchivoANavegador(fileActoAdmin.getFileName(), fileActoAdmin.getFile());
		} else {
			statusMessages.add(Severity.ERROR, "No existe documento que descargar");
		}
	}

	private Boolean precondAdjuntarDoc() {
		if (nroDoc == null || fechaDoc == null || idTipoDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}
		if (nroDoc < 0) {
			statusMessages.add(Severity.ERROR, "El campo Nº Doc. debe ser mayor a cero");
			return false;
		}
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}

		return true;
	}

	// GETTER Y SETTER

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public boolean isHabUpdate() {
		return habUpdate;
	}

	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public Long getIdRedCap() {
		return idRedCap;
	}

	public void setIdRedCap(Long idRedCap) {
		this.idRedCap = idRedCap;
	}

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
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

	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}

	public void setConfiguracionUoDetList(List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}

	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}

	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
	}

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}

	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}

	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}

	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
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

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}

	public void setEntidadOeeUtilAnterior(NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}

	public SinAnx getSinAnxSeleccionado() {
		return sinAnxSeleccionado;
	}

	public void setSinAnxSeleccionado(SinAnx sinAnxSeleccionado) {
		this.sinAnxSeleccionado = sinAnxSeleccionado;
	}

	public List<EmpleadoMovilidadAnexo> getConceptoPagosActual() {
		return conceptoPagosActual;
	}

	public void setConceptoPagosActual(List<EmpleadoMovilidadAnexo> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}

	public List<EmpleadoMovilidadAnexo> getConceptoPagosOrigen() {
		return conceptoPagosOrigen;
	}

	public void setConceptoPagosOrigen(List<EmpleadoMovilidadAnexo> conceptoPagosOrigen) {
		this.conceptoPagosOrigen = conceptoPagosOrigen;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}

	public BuscadorDocsFC getBuscadorDocsDesctino() {
		return buscadorDocsDesctino;
	}

	public void setBuscadorDocsDesctino(BuscadorDocsFC buscadorDocsDesctino) {
		this.buscadorDocsDesctino = buscadorDocsDesctino;
	}

	public Integer getNroDoc() {
		return nroDoc;
	}

	public void setNroDoc(Integer nroDoc) {
		this.nroDoc = nroDoc;
	}

	public Date getFechaDoc() {
		return fechaDoc;
	}

	public void setFechaDoc(Date fechaDoc) {
		this.fechaDoc = fechaDoc;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getfNameMostrar() {
		return fNameMostrar;
	}

	public void setfNameMostrar(String fNameMostrar) {
		this.fNameMostrar = fNameMostrar;
	}

	public boolean isHabDescargar() {
		return habDescargar;
	}

	public void setHabDescargar(boolean habDescargar) {
		this.habDescargar = habDescargar;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public byte[] getuFile() {
		return uFile;
	}

	public void setuFile(byte[] uFile) {
		this.uFile = uFile;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public Documentos getDocDecreto() {
		return docDecreto;
	}

	public void setDocDecreto(Documentos docDecreto) {
		this.docDecreto = docDecreto;
	}

	public Documentos getDocActoAdmin() {
		return docActoAdmin;
	}

	public void setDocActoAdmin(Documentos docActoAdmin) {
		this.docActoAdmin = docActoAdmin;
	}

	public UploadItem getFileActoAdmin() {
		return fileActoAdmin;
	}

	public void setFileActoAdmin(UploadItem fileActoAdmin) {
		this.fileActoAdmin = fileActoAdmin;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}
	
	public ActividadProceso actividadProcesoActual(){
		String sqlActProc = "select * from proceso.actividad_proceso act_proc join proceso.actividad act on act_proc.id_actividad = act.id_actividad "
				+"where act.descripcion = 'REGISTRAR TRASLADO SIN LINEA';";
		Query qActProc = em.createNativeQuery(sqlActProc, ActividadProceso.class);
		List<ActividadProceso> listaActProc = qActProc.getResultList();

		if(listaActProc.size() > 0){
			return listaActProc.get(0);
		}
		else
		{
			return null;
		}
	}

}
