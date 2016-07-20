package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
import org.richfaces.model.UploadItem;

import enums.ActividadEnum;
import enums.ProcesoEnum;
import py.com.excelsis.sicca.entity.ActividadProceso;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadAnexo;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadDocTcl;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SolicitudTrasladoCab;
import py.com.excelsis.sicca.entity.SolicitudTrasladoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Name("trasladoDefConLineaConSolCU674FC")
@Scope(ScopeType.CONVERSATION)
public class TrasladoDefConLineaConSolCU674FC {
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
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;

	private Long idSolicitud;
	private Long idTipoDoc;
	private Long idOeeDestino;
	private Long idPlantaCargoDet;
	private Long idUO;
	private Integer nroDoc;
	private Date fechaDoc;
	private Date fechaInicio;
	private String fNameMostrar;
	private String fNameOrigen;
	private String typeOrigen = null;
	private BigDecimal codSinEntidad;
	private String nombreSinEntidad;
	private BigDecimal codNivelEntidad;
	private String nombreNivelEntidad;
	private Integer ordenUnidOrg;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private String denominacionUnidad;
	private String linea;
	private String descripcion;
	private String nombrePantalla;
	private byte[] fileOrigenByte = null;

	private SolicitudTrasladoCab solicitudTrasladoCab = new SolicitudTrasladoCab();
	private Documentos docOrigen = new Documentos();
	private UploadItem fileOrigen;
	private BuscadorDocsFC buscadorDocsDestino;
	private SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private NivelEntidadOeeUtil nivelEntidadOeeUtilPersona;
	private Persona persona = new Persona();
	private SinAnx sinAnxSeleccionado = null;
	private EmpleadoPuesto funcionario;
	private EmpleadoPuesto empleadoPuesto;

	// Datos del check
	private Boolean notaSol;
	private Boolean resMai;
	private Boolean copiaLegajo;
	private Boolean constanciaUaf;
	private Boolean presupuesto;
	private Boolean informeSfp;
	private Boolean informeMH;
	private Boolean resMH_SEAF;
	private Boolean decreto;

	private List<ConfiguracionUoDet> configuracionUoDetList;
	private List<PlantaCargoDet> plantaCargoDetList;
	private List<PlantaCargoDet> plantaCargoDetNuevasFuncionesList;
	private List<EmpleadoMovilidadAnexo> conceptoPagosActual = new ArrayList<EmpleadoMovilidadAnexo>();
	private List<EmpleadoMovilidadAnexo> conceptoPagosOrigen = new ArrayList<EmpleadoMovilidadAnexo>();
	
/**
 * Seccion Ver
 */
	private List<ReferenciaAdjuntos> adjuntos = new ArrayList<ReferenciaAdjuntos>();
	private List<SolicitudTrasladoDet> detalleSolicitud = new ArrayList<SolicitudTrasladoDet>();
	private List<EmpleadoConceptoPago> listaConceptoPagoAnterior = new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> listaConceptoPagoNuevo = new ArrayList<EmpleadoConceptoPago>();
	private EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();

	public void init() {
		if (idSolicitud != null) {
			selectedRow = -1;
			selectedRowPuesto = -1;
			solicitudTrasladoCab = em.find(SolicitudTrasladoCab.class,
					idSolicitud);
			if (buscadorDocsDestino == null)
				buscadorDocsDestino = (BuscadorDocsFC) Component.getInstance(
						BuscadorDocsFC.class, true);
			cargarNiveentidadOee();
			cargarDatos();
			cargarAdjuntos();
			cargarUnidadOrganizativa();
			seleccionarPuestoInit(solicitudTrasladoCab.getPlantaCargoDet());
			seleccionUtilFormController.setCodigoObj(solicitudTrasladoCab.getCodObjeto());
			seleccionUtilFormController.setCodigoCategoria(solicitudTrasladoCab.getCategoria());
			seleccionUtilFormController.setValorObj(solicitudTrasladoCab.getValorObj());
			seleccionUtilFormController.setCategoria(solicitudTrasladoCab.getCategoria());
			seleccionUtilFormController.setMonto(solicitudTrasladoCab.getMonto().intValue());
			linea = solicitudTrasladoCab.getLinea();
			descripcion = solicitudTrasladoCab.getDescripcion();
			agregarInit();
		}
		iniciaBooleanos();
	}

	public void initVer() {
		if (idSolicitud != null) {
			solicitudTrasladoCab = em.find(SolicitudTrasladoCab.class,
					idSolicitud);
			cargarDatosVer();
		
			persona = empleadoMovilidadCab.getEmpleadoPuestoAnterior().getPersona();
			funcionario = new EmpleadoPuesto();

			funcionario = empleadoMovilidadCab.getEmpleadoPuestoAnterior();
			cargarNiveentidadOeeVer();
		}
	}
	
	private void cargarNiveentidadOeeVer() {

		nivelEntidadOeeUtil.setIdConfigCab(empleadoMovilidadCab.getEmpleadoPuestoNuevo().getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

		codNivelEntidad = nivelEntidadOeeUtil.getCodNivelEntidad();
		nombreNivelEntidad = nivelEntidadOeeUtil.getNombreNivelEntidad();
		codSinEntidad = nivelEntidadOeeUtil.getCodSinEntidad();
		nombreSinEntidad = nivelEntidadOeeUtil.getNombreSinEntidad();
		denominacionUnidad = nivelEntidadOeeUtil.getDenominacionUnidad();
		ordenUnidOrg = nivelEntidadOeeUtil.getOrdenUnidOrg();
		idOeeDestino = nivelEntidadOeeUtil.getIdConfigCab();
		idUO = nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (nivelEntidadOeeUtilPersona == null)
			nivelEntidadOeeUtilPersona = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
		nivelEntidadOeeUtilPersona.setEm(em);
		
		nivelEntidadOeeUtilPersona.setIdConfigCab(empleadoMovilidadCab.getEmpleadoPuestoAnterior().getPlantaCargoDet().getConfiguracionUoDet().getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtilPersona.init2();

	}

	private void cargarDatosVer() {
		try {
			iniciaBooleanos();
			adjuntos = em.createQuery(
					"Select r from ReferenciaAdjuntos r "
							+ " where r.idRegistroTabla="
							+ solicitudTrasladoCab.getIdSolicitudTrasladoCab()
							+" and (r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO' or r.actividadProceso.actividad.descripcion='RESPONDER TRASLADO' or "
							+" r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO')")
					.getResultList();

			EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
			List<EmpleadoMovilidadCab> listaMov = em
					.createQuery(
							"Select e from EmpleadoMovilidadCab e "
									+ " where e.solicitudTrasladoCab.idSolicitudTrasladoCab="
									+ solicitudTrasladoCab
											.getIdSolicitudTrasladoCab())
					.getResultList();
			if (!listaMov.isEmpty()) {
				empleadoMovilidadCab = listaMov.get(0);
				EmpleadoMovilidadDocTcl docTcl = new EmpleadoMovilidadDocTcl();
				List<EmpleadoMovilidadDocTcl> listaTcl = em
				.createQuery(
						"Select tcl from EmpleadoMovilidadDocTcl tcl "
								+ " where tcl.empleadoMovilidadCab.idEmpleadoMovilidadCab="
								+ empleadoMovilidadCab.getIdEmpleadoMovilidadCab())
				.getResultList();
				if(!listaTcl.isEmpty()){
					docTcl = listaTcl.get(0);
					notaSol = docTcl.getNotaSolicitud();
					resMai = docTcl.getResolucionAceptacion();
					copiaLegajo = docTcl.getCopiaLegajo();
					constanciaUaf = docTcl.getConstanciaUaf();
					presupuesto = docTcl.getCuadroPresupuesto();
					informeSfp = docTcl.getInformeSfp();
					informeMH = docTcl.getInformeHacienda();
					resMH_SEAF = docTcl.getResolucionMhSeaf();
					decreto = docTcl.getDecreto();
				}
			}
			
			detalleSolicitud = em
			.createQuery(
					"Select s from SolicitudTrasladoDet s "
							+ " where s.solicitudTrasladoCab.idSolicitudTrasladoCab="
							+ solicitudTrasladoCab
									.getIdSolicitudTrasladoCab())
			.getResultList();

			List<EmpleadoMovilidadCab> listaMovCab = em
			.createQuery(
					"Select e from EmpleadoMovilidadCab e "
							+ " where e.solicitudTrasladoCab.idSolicitudTrasladoCab="
							+ solicitudTrasladoCab
									.getIdSolicitudTrasladoCab())
			.getResultList();
			if(!listaMovCab.isEmpty())
				this.empleadoMovilidadCab = listaMovCab.get(0);
			obtenerConceptoPagoAnterior();
			obtenerConceptoPagoNueva();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void cargarAdjuntos() {
		try {
			adjuntos = em.createQuery(
					"Select r from ReferenciaAdjuntos r "
							+ " where r.idRegistroTabla="
							+ solicitudTrasladoCab.getIdSolicitudTrasladoCab()
							+" and (r.actividadProceso.actividad.descripcion='SOLICITAR TRASLADO' or r.actividadProceso.actividad.descripcion='RESPONDER TRASLADO' or "
							+" r.actividadProceso.actividad.descripcion='REVISAR SOLICITUD SFP')")
					.getResultList();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public String toFindPersonaView() {
		if(persona.getIdPersona()==null)
		{
			statusMessages.add(Severity.ERROR,"Debe seleccionar el Funcionario");
			return null;
		}
		String from="/movilidadLaboral/trasladoDefConLineaConSol/VerTrasladoDefConLineaConSolCU674";
		
		return "/seleccion/persona/Persona.xhtml?personaFrom="+from+"&personaIdPersona="+persona.getIdPersona()+"&conversationPropagation=join";
	}

	private void obtenerConceptoPagoAnterior(){
		String sql = "SELECT pago.* " +
				"FROM movilidad.empleado_movilidad_cab mov_cab " +
				"INNER JOIN general.empleado_puesto e ON (mov_cab.id_empleado_puesto_anterior = e.id_empleado_puesto) " +
				"INNER JOIN general.empleado_concepto_pago pago ON (e.id_empleado_puesto = pago.id_empleado_puesto) " +
				"INNER JOIN general.persona p ON (e.id_persona = p.id_persona) " +
				"WHERE mov_cab.id_empleado_puesto_anterior = e.id_empleado_puesto AND  " +
				"mov_cab.activo = TRUE AND p.id_persona = "+persona.getIdPersona();
		listaConceptoPagoAnterior = em.createNativeQuery(sql, EmpleadoConceptoPago.class).getResultList();
	}
	
	private void obtenerConceptoPagoNueva(){
		String sql = "SELECT pago.* " +
				"FROM movilidad.empleado_movilidad_cab mov_cab " +
				"INNER JOIN general.empleado_puesto e ON (mov_cab.id_empleado_puesto_nuevo = e.id_empleado_puesto) " +
				"INNER JOIN general.empleado_concepto_pago pago ON (e.id_empleado_puesto = pago.id_empleado_puesto) " +
				"INNER JOIN general.persona p ON (e.id_persona = p.id_persona) " +
				"WHERE mov_cab.id_empleado_puesto_nuevo = e.id_empleado_puesto " +
				"AND mov_cab.activo = TRUE AND p.id_persona = "+empleadoMovilidadCab.getEmpleadoPuestoNuevo().getPersona().getIdPersona();
		listaConceptoPagoNuevo = em.createNativeQuery(sql, EmpleadoConceptoPago.class).getResultList();
	}
	
	public void descargarDocBD(Long id) throws FileNotFoundException,
			IOException {
		Documentos doc = em.find(Documentos.class, id);
		if (doc.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(
					doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	private void iniciaBooleanos() {
		notaSol = false;
		resMai = false;
		copiaLegajo = false;
		constanciaUaf = false;
		presupuesto = false;
		informeSfp = false;
		informeMH = false;
		resMH_SEAF = false;
		decreto = false;
	}

	private void cargarDatos() {
		persona = solicitudTrasladoCab.getPersona();
		funcionario = new EmpleadoPuesto();

		funcionario = solicitudTrasladoCab.getEmpleadoPuesto();
		obtenerCatOrigen();

	}

	@SuppressWarnings("unchecked")
	private void obtenerCatOrigen() {
		conceptoPagosOrigen = new ArrayList<EmpleadoMovilidadAnexo>();
		Long id = funcionario.getIdEmpleadoPuesto();
		List<EmpleadoConceptoPago> m = em
				.createQuery(
						"Select e from EmpleadoConceptoPago e "
								+ " where e.empleadoPuesto.idEmpleadoPuesto=:id")
				.setParameter("id", funcionario.getIdEmpleadoPuesto())
				.getResultList();
		for (EmpleadoConceptoPago empleadoConceptoPago : m) {
			EmpleadoMovilidadAnexo movilidadAnexo = new EmpleadoMovilidadAnexo();
			movilidadAnexo.setCategoria(empleadoConceptoPago.getCategoria());
			movilidadAnexo.setMonto(empleadoConceptoPago.getMonto());
			movilidadAnexo.setObjCodigo(empleadoConceptoPago.getObjCodigo());
			conceptoPagosOrigen.add(movilidadAnexo);
		}

	}

	private void cargarNiveentidadOee() {

		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

		codNivelEntidad = nivelEntidadOeeUtil.getCodNivelEntidad();
		nombreNivelEntidad = nivelEntidadOeeUtil.getNombreNivelEntidad();
		codSinEntidad = nivelEntidadOeeUtil.getCodSinEntidad();
		nombreSinEntidad = nivelEntidadOeeUtil.getNombreSinEntidad();
		denominacionUnidad = nivelEntidadOeeUtil.getDenominacionUnidad();
		ordenUnidOrg = nivelEntidadOeeUtil.getOrdenUnidOrg();
		idOeeDestino = nivelEntidadOeeUtil.getIdConfigCab();
		idUO = nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (nivelEntidadOeeUtilPersona == null)
			nivelEntidadOeeUtilPersona = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
		nivelEntidadOeeUtilPersona.setEm(em);
		Long id = solicitudTrasladoCab.getOeeOrigen().getIdConfiguracionUo();
		nivelEntidadOeeUtilPersona.setIdConfigCab(solicitudTrasladoCab
				.getOeeOrigen().getIdConfiguracionUo());
		nivelEntidadOeeUtilPersona.init2();

	}

	public List<SelectItem> updateTipoDocSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoDocumento())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

	@SuppressWarnings("unchecked")
	public List<DatosEspecificos> datosEspecificosByTipoDocumento() {
		try {
			List<DatosEspecificos> datosEspecificosLists = em
					.createQuery(
							"Select d from DatosEspecificos d "
									+ " where d.descripcion like 'RESOLUCION' and d.valorAlf like 'RES' and d.activo=true order by d.descripcion")
					.getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public void descargarDocOrigen() throws FileNotFoundException, IOException {
		if (docOrigen != null) {
			String resp = AdmDocAdjuntoFormController.abrirDocumentoFromCU(
					docOrigen.getIdDocumento(), usuarioLogueado.getIdUsuario());
			if (!resp.equalsIgnoreCase("OK")) {
				statusMessages.add(Severity.ERROR, resp);
			}
		} else if (fileOrigen != null) {
			admDocAdjuntoFormController.enviarArchivoANavegador(
					fileOrigen.getFileName(), fileOrigen.getFile());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	private void cargarUnidadOrganizativa() {

		configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
		configuracionUoDetList = em
				.createQuery(
						"Select det from ConfiguracionUoDet det "
								+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE and det.activo=TRUE order by det.orden asc ")
				.setParameter("idOEE", idOeeDestino).getResultList();
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
		setearConPago();
	}

	/**
	 * Obtiene el id del Estado Vacante
	 * 
	 * @return
	 */
	private Long getIdEstadoCabVacante() {
		EstadoCab cab = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		return cab.getIdEstadoCab();
	}

	private void setearConPago() {
		sinAnxSeleccionado = seleccionUtilFormController.getSinAnx();
		seleccionUtilFormController.setearValoresObjetosGasto();
		linea = null;
		descripcion = null;

	}

	public void seleccionarPuesto(Long idPuesto, int index) {
		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(
				PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = idPuesto;
		setearConPago();
		setSelectedRowPuesto(index);
		cargarPuestos();
	}
	
	public void  seleccionarPuestoInit(PlantaCargoDet puesto) {
		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(puesto);
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = puesto.getIdPlantaCargoDet();
		setearConPago();
		cargarPuestos();
	}

	private void cargarPuestos() {

		if (idUO != null) {
			plantaCargoDetNuevasFuncionesList = em
					.createQuery(
							"Select det from PlantaCargoDet det "
									+ " where det.configuracionUoDet.idConfiguracionUoDet=:idUO "
									+ " and det.descripcion='EN ESPERA DE NUEVAS FUNCIONES' and det.estadoCab.idEstadoCab=:idEstadoCab "
									+ " and det.estadoDet.idEstadoDet is null order by det.orden asc")
					.setParameter("idUO", idUO)
					.setParameter("idEstadoCab", getIdEstadoCabVacante())
					.getResultList();
			// cargarUo();
			if (plantaCargoDetNuevasFuncionesList.isEmpty()) {
				//statusMessages.add(Severity.ERROR,"No hay puestos disponibles. Debe gestionarse por Subsistema de Planificación la creación del puesto para esta movilidad");
			}
		} else
			plantaCargoDetNuevasFuncionesList = new ArrayList<PlantaCargoDet>();
	}

	public void agregarInit() {
		/*if (seleccionUtilFormController.getCodigoObj() == null
				|| seleccionUtilFormController.getValorObj() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Cod. Objeto Gasto");
			return;
		}*/
		/*
		 * if (seleccionUtilFormController.getCodigoObj().longValue() == 111) {
		 * statusMessages.add(Severity.ERROR,
		 * "Debe Ingresar el Cod. Objeto Gasto distinto a 111"); return; }
		 */
		/*if (seleccionUtilFormController.getMonto() == null) {
			statusMessages.add(Severity.ERROR, "Debe Ingresar el Monto");
			return;
		}
		if (seleccionUtilFormController.getMonto().intValue() < 0) {
			statusMessages
					.add(Severity.ERROR, "El Monto debe ser mayor a cero");
			return;
		}
		if (linea == null || linea.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar la Linea");
			return;
		}
		if (descripcion == null || descripcion.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar la Descripción");
			return;
		}*/
		EmpleadoMovilidadAnexo empleadoMovilidad = new EmpleadoMovilidadAnexo();

		empleadoMovilidad.setObjCodigo(seleccionUtilFormController
				.getCodigoObj());
		if (seleccionUtilFormController.getCategoria() != null)
			empleadoMovilidad.setCategoria(seleccionUtilFormController
					.getCodigoCategoria());
		empleadoMovilidad.setMonto(seleccionUtilFormController.getMonto());
		empleadoMovilidad.setActivo(true);
		empleadoMovilidad.setLinea(linea);
		empleadoMovilidad.setDescripcion(descripcion);
		conceptoPagosActual.add(empleadoMovilidad);

		setearConPago();
	}

	public void eliminar(int index) {
		conceptoPagosActual.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	public boolean chkDatos() {
		/*if (nroDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Numero de Documento");
			return false;
		}
		if (fechaDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (docOrigen == null && fileOrigen == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Inrgesar un archivo, verifique");
			return false;
		}*/
		if (buscadorDocsDestino.getNroDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Numero de Documento");
			return false;
		}
		if (buscadorDocsDestino.getFechaDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (buscadorDocsDestino.getIdTipoDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (buscadorDocsDestino.getDocDecreto() == null
				&& buscadorDocsDestino.getFileActoAdmin() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Inrgesar un archivo, verifique");
			return false;
		}

		if (conceptoPagosActual.isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe agregar almenos un Objeto de Gasto y Monto");
			return false;
		}
		if (!resMH_SEAF && !decreto) {
			statusMessages.add(Severity.ERROR,
					"Debe escoger al menos Resolución MH/SEAF o Decreto");
			return false;
		}

		return true;
	}

	public String save() {
		try {
			if (!chkDatos())
				return null;
			em.clear();
			PlantaCargoDet puestoSelec = em.find(PlantaCargoDet.class,
					idPlantaCargoDet);

			funcionario = em.find(EmpleadoPuesto.class,
					funcionario.getIdEmpleadoPuesto());
			funcionario.setPin(seleccionUtilFormController.generarPIN());
			funcionario.setActual(false);
			funcionario.setFechaFin(new Date());
			funcionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
			funcionario.setFechaMod(new Date());
			em.merge(funcionario);

			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al
			 * Funcionario seleccionado
			 */
			PlantaCargoDet plantaCargoDetFuncionario = em.find(
					PlantaCargoDet.class, funcionario.getPlantaCargoDet()
							.getIdPlantaCargoDet());
			plantaCargoDetFuncionario.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("VACANTE"));
			plantaCargoDetFuncionario.setUsuMod(usuarioLogueado
					.getCodigoUsuario());
			plantaCargoDetFuncionario.setFechaMod(new Date());
			plantaCargoDetFuncionario.setEstadoDet(null);
			em.merge(plantaCargoDetFuncionario);

			/***
			 * • Registra el histórico de cambios de estados del Puesto anterior
			 * en la tabla HISTORICOS_ESTADO , registrando el estado actual
			 */
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("VACANTE"));
			historico.setFechaMod(new Date());
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historico.setPlantaCargoDet(plantaCargoDetFuncionario);
			em.persist(historico);

			/**
			 * se guardan los datos en la tabla EMPLEADO_PUESTO
			 */
			empleadoPuesto = new EmpleadoPuesto();
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(searchDatosEsp(
							"TIPOS DE INGRESOS Y MOVILIDAD",
							"TRASLADO DEFINITIVO CON LINEAS DE CARGO"));
			empleadoPuesto.setFechaInicio(fechaInicio);
			empleadoPuesto.setActual(true);
			empleadoPuesto.setActivo(true);
			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
			empleadoPuesto.setContratado(false);
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspEstado(searchDatosEsp(
							"ESTADO EMPLEADO PUESTO", "PERMANENTE"));
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoRegistro(searchDatosEsp(
							"TIPOS DE REGISTRO INGRESOS Y MOVILIDAD",
							"MOVILIDAD"));
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(searchDatosEsp(
							"TIPOS DE INGRESOS Y MOVILIDAD",
							"TRASLADO DEFINITIVO CON LINEAS DE CARGO"));
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,
					idPlantaCargoDet));
			empleadoPuesto.setPersona(persona);
			empleadoPuesto.setIncidenAntiguedad(true);
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(empleadoPuesto);

			/**
			 * Cambiar el estado del puesto a OCUPADO
			 */
			puestoSelec.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
			puestoSelec.setFechaMod(new Date());
			em.merge(puestoSelec);

			/**
			 * Generar un registro del cambio de estado en la tabla
			 * HISTORICOS_ESTADO
			 */
			HistoricosEstado historial = new HistoricosEstado();
			historial.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
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
			empleadoMovilidadCab.setSolicitudTrasladoCab(solicitudTrasladoCab);
			em.persist(empleadoMovilidadCab);

			EmpleadoMovilidadDocTcl docTcl = new EmpleadoMovilidadDocTcl();
			docTcl.setActivo(true);
			docTcl.setConstanciaUaf(constanciaUaf);
			docTcl.setCopiaLegajo(copiaLegajo);
			docTcl.setCuadroPresupuesto(presupuesto);
			docTcl.setDecreto(decreto);
			docTcl.setEmpleadoMovilidadCab(empleadoMovilidadCab);
			docTcl.setFechaAlta(new Date());
			docTcl.setInformeHacienda(informeMH);
			docTcl.setInformeSfp(informeSfp);
			docTcl.setNotaSolicitud(notaSol);
			docTcl.setResolucionAceptacion(resMai);
			docTcl.setResolucionMhSeaf(resMH_SEAF);
			docTcl.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(docTcl);

			/**
			 * • Gestionar objetos y categorías para el puesto ocupado
			 */
			// De Categorías/remuneraciones en OEE Origen
			for (EmpleadoMovilidadAnexo movilidadAnexo : conceptoPagosOrigen) {
				EmpleadoConceptoPago conceptoPago = new EmpleadoConceptoPago();
				conceptoPago.setEmpleadoPuesto(empleadoPuesto);
				conceptoPago.setFechaAlta(new Date());
				conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				conceptoPago.setAnho(Integer.parseInt(sdfAnio
						.format(fechaInicio)));
				conceptoPago.setObjCodigo(movilidadAnexo.getObjCodigo());
				conceptoPago.setMonto(movilidadAnexo.getMonto());
				conceptoPago.setCategoria(movilidadAnexo.getCategoria());
				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 */
				if (conceptoPago.getCategoria() != null
						&& sinAnxSeleccionado != null) {
					SinAnx anx = em.find(SinAnx.class,
							sinAnxSeleccionado.getIdAnx());
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
				conceptoPago.setAnho(Integer.parseInt(sdfAnio
						.format(fechaInicio)));
				conceptoPago.setObjCodigo(movilidadAnexoDestino.getObjCodigo());
				conceptoPago.setMonto(movilidadAnexoDestino.getMonto());
				conceptoPago.setCategoria(movilidadAnexoDestino.getCategoria());
				em.persist(conceptoPago);
				// Inserta registro en EMPLEADO_MOVILIDAD ANEXO
				movilidadAnexoDestino.setAnexoOee("D");
				movilidadAnexoDestino.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				movilidadAnexoDestino.setFechaAlta(new Date());
				movilidadAnexoDestino
						.setEmpleadoMovilidadCab(empleadoMovilidadCab);
				movilidadAnexoDestino.setActivo(true);
				em.persist(movilidadAnexoDestino);
			}

			solicitudTrasladoCab.setDatosEspEstadoSolicitud(searchDatosEsp(
					"ESTADOS SOLICITUD MOVILIDAD", "GESTION FINALIZADA"));
			solicitudTrasladoCab.setUsuMod(usuarioLogueado.getCodigoUsuario());
			solicitudTrasladoCab.setFechaMod(new Date());
			em.merge(solicitudTrasladoCab);
			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
			 * Insertar registro en Documentos y el archivo pdf *
			 */
			// Por Acto administrativo de Entidad Origen
			/*Long idDocOrigen = null;
			if (fileOrigen != null) {

				idDocOrigen = guardarAdjuntos(fNameOrigen,
						fileOrigen.getFileSize(), fileOrigen.getContentType(),
						fileOrigen, idTipoDoc, null);
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

			//insertarAdjunto(idDocOrigen);

			// Por Acto administrativo de Entidad Destino
			Long idDocDestino = null;
			if (buscadorDocsDestino.getFileActoAdmin() != null) {

				idDocDestino = guardarAdjuntos(
						buscadorDocsDestino.getfName(),
						buscadorDocsDestino.getFileActoAdmin().getFileSize(),
						buscadorDocsDestino.getFileActoAdmin().getContentType(),
						buscadorDocsDestino.getFileActoAdmin(),
						buscadorDocsDestino.getIdTipoDoc(), null);
				if (idDocDestino == null)
					return null;
				Documentos doc = em.find(Documentos.class, idDocDestino);
				doc.setNroDocumento(buscadorDocsDestino.getNroDoc());
				doc.setAnhoDocumento(Integer.parseInt(sdfAnio
						.format(buscadorDocsDestino.getFechaDoc())));
				doc.setFechaDoc(buscadorDocsDestino.getFechaDoc());
				em.merge(doc);

			}
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 */

			insertarAdjunto(idDocDestino);

			jbpmUtilFormController
					.setActividadActual(ActividadEnum.MOV_REGISTRAR_TRASLADO_CON_LINEA);
			jbpmUtilFormController.setProcesoEnum(ProcesoEnum.MOVILIDAD);
			jbpmUtilFormController.setTransition("regTraDefConLin_TO_end");
			jbpmUtilFormController
					.setSolicitudTrasladoCab(solicitudTrasladoCab);

			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			}
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "next";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private DatosEspecificos searchDatosEsp(String gral, String esp) {
		javax.persistence.Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = '"
						+ esp
						+ "' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = '"
						+ gral + "'");
		return (DatosEspecificos) q.getSingleResult();

	}

	private Boolean precondAdjuntarDoc() {
		if (nroDoc == null || fechaDoc == null || idTipoDoc == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}
		if (nroDoc < 0) {
			statusMessages.add(Severity.ERROR,
					"El campo Nº Doc. debe ser mayor a cero");
			return false;
		}
		if (fileOrigenByte == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}

		return true;
	}

	public void adjuntarDoc() {
		if (!precondAdjuntarDoc())
			return;

		fileOrigen = seleccionUtilFormController.crearUploadItem(fNameOrigen,
				fileOrigenByte.length, typeOrigen, fileOrigenByte);
		fNameMostrar = fNameOrigen;

		statusMessages.add(Severity.INFO, "Documento Adjuntado ");

	}

	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto
	 * administrativo
	 */
	public void insertarAdjunto(Long idDocumento) {
		ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
		referenciaAdjuntos.setPersona(persona);
		referenciaAdjuntos
				.setDocumentos(em.find(Documentos.class, idDocumento));
		referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto
				.getIdEmpleadoPuesto());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		if(actividadProcesoActual() != null){
			referenciaAdjuntos.setActividadProceso(actividadProcesoActual());
		}
		em.persist(referenciaAdjuntos);
	}

	public Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoMovilidadCab";
			nombrePantalla = "trasladoDefConLineaConSolCU674";
			String direccionFisica = "";
			String sf = File.separator;
			direccionFisica = sf + "SICCA" + sf + anio + sf + "OEE" + sf
					+ nivelEntidadOeeUtil.getIdConfigCab() + sf + "MOVILIDAD";

			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(file,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void marcarTodos() {
		notaSol = true;
		resMai = true;
		copiaLegajo = true;
		constanciaUaf = true;
		presupuesto = true;
		informeSfp = true;
		informeMH = true;
		resMH_SEAF = true;
		decreto = true;
	}

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Long idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public SolicitudTrasladoCab getSolicitudTrasladoCab() {
		return solicitudTrasladoCab;
	}

	public void setSolicitudTrasladoCab(
			SolicitudTrasladoCab solicitudTrasladoCab) {
		this.solicitudTrasladoCab = solicitudTrasladoCab;
	}

	public Long getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Long idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
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

	public String getfNameMostrar() {
		return fNameMostrar;
	}

	public void setfNameMostrar(String fNameMostrar) {
		this.fNameMostrar = fNameMostrar;
	}

	public Documentos getDocOrigen() {
		return docOrigen;
	}

	public void setDocOrigen(Documentos docOrigen) {
		this.docOrigen = docOrigen;
	}

	public UploadItem getFileOrigen() {
		return fileOrigen;
	}

	public void setFileOrigen(UploadItem fileOrigen) {
		this.fileOrigen = fileOrigen;
	}

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
	}

	public String getfNameOrigen() {
		return fNameOrigen;
	}

	public void setfNameOrigen(String fNameOrigen) {
		this.fNameOrigen = fNameOrigen;
	}

	public String getTypeOrigen() {
		return typeOrigen;
	}

	public void setTypeOrigen(String typeOrigen) {
		this.typeOrigen = typeOrigen;
	}

	public byte[] getFileOrigenByte() {
		return fileOrigenByte;
	}

	public void setFileOrigenByte(byte[] fileOrigenByte) {
		this.fileOrigenByte = fileOrigenByte;
	}

	public BuscadorDocsFC getBuscadorDocsDestino() {
		return buscadorDocsDestino;
	}

	public void setBuscadorDocsDestino(BuscadorDocsFC buscadorDocsDestino) {
		this.buscadorDocsDestino = buscadorDocsDestino;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtilPersona() {
		return nivelEntidadOeeUtilPersona;
	}

	public void setNivelEntidadOeeUtilPersona(
			NivelEntidadOeeUtil nivelEntidadOeeUtilPersona) {
		this.nivelEntidadOeeUtilPersona = nivelEntidadOeeUtilPersona;
	}

	public BigDecimal getCodSinEntidad() {
		return codSinEntidad;
	}

	public void setCodSinEntidad(BigDecimal codSinEntidad) {
		this.codSinEntidad = codSinEntidad;
	}

	public String getNombreSinEntidad() {
		return nombreSinEntidad;
	}

	public void setNombreSinEntidad(String nombreSinEntidad) {
		this.nombreSinEntidad = nombreSinEntidad;
	}

	public BigDecimal getCodNivelEntidad() {
		return codNivelEntidad;
	}

	public void setCodNivelEntidad(BigDecimal codNivelEntidad) {
		this.codNivelEntidad = codNivelEntidad;
	}

	public String getNombreNivelEntidad() {
		return nombreNivelEntidad;
	}

	public void setNombreNivelEntidad(String nombreNivelEntidad) {
		this.nombreNivelEntidad = nombreNivelEntidad;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public String getDenominacionUnidad() {
		return denominacionUnidad;
	}

	public void setDenominacionUnidad(String denominacionUnidad) {
		this.denominacionUnidad = denominacionUnidad;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public List<ConfiguracionUoDet> getConfiguracionUoDetList() {
		return configuracionUoDetList;
	}

	public void setConfiguracionUoDetList(
			List<ConfiguracionUoDet> configuracionUoDetList) {
		this.configuracionUoDetList = configuracionUoDetList;
	}

	public Long getIdOeeDestino() {
		return idOeeDestino;
	}

	public void setIdOeeDestino(Long idOeeDestino) {
		this.idOeeDestino = idOeeDestino;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public List<PlantaCargoDet> getPlantaCargoDetList() {
		return plantaCargoDetList;
	}

	public void setPlantaCargoDetList(List<PlantaCargoDet> plantaCargoDetList) {
		this.plantaCargoDetList = plantaCargoDetList;
	}

	public int getSelectedRowPuesto() {
		return selectedRowPuesto;
	}

	public void setSelectedRowPuesto(int selectedRowPuesto) {
		this.selectedRowPuesto = selectedRowPuesto;
	}

	public SinAnx getSinAnxSeleccionado() {
		return sinAnxSeleccionado;
	}

	public void setSinAnxSeleccionado(SinAnx sinAnxSeleccionado) {
		this.sinAnxSeleccionado = sinAnxSeleccionado;
	}

	public Long getIdPlantaCargoDet() {
		return idPlantaCargoDet;
	}

	public void setIdPlantaCargoDet(Long idPlantaCargoDet) {
		this.idPlantaCargoDet = idPlantaCargoDet;
	}

	public Long getIdUO() {
		return idUO;
	}

	public void setIdUO(Long idUO) {
		this.idUO = idUO;
	}

	public List<PlantaCargoDet> getPlantaCargoDetNuevasFuncionesList() {
		return plantaCargoDetNuevasFuncionesList;
	}

	public void setPlantaCargoDetNuevasFuncionesList(
			List<PlantaCargoDet> plantaCargoDetNuevasFuncionesList) {
		this.plantaCargoDetNuevasFuncionesList = plantaCargoDetNuevasFuncionesList;
	}

	public List<EmpleadoMovilidadAnexo> getConceptoPagosActual() {
		return conceptoPagosActual;
	}

	public void setConceptoPagosActual(
			List<EmpleadoMovilidadAnexo> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}

	public List<EmpleadoMovilidadAnexo> getConceptoPagosOrigen() {
		return conceptoPagosOrigen;
	}

	public void setConceptoPagosOrigen(
			List<EmpleadoMovilidadAnexo> conceptoPagosOrigen) {
		this.conceptoPagosOrigen = conceptoPagosOrigen;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public Boolean getNotaSol() {
		return notaSol;
	}

	public void setNotaSol(Boolean notaSol) {
		this.notaSol = notaSol;
	}

	public Boolean getResMai() {
		return resMai;
	}

	public void setResMai(Boolean resMai) {
		this.resMai = resMai;
	}

	public Boolean getCopiaLegajo() {
		return copiaLegajo;
	}

	public void setCopiaLegajo(Boolean copiaLegajo) {
		this.copiaLegajo = copiaLegajo;
	}

	public Boolean getConstanciaUaf() {
		return constanciaUaf;
	}

	public void setConstanciaUaf(Boolean constanciaUaf) {
		this.constanciaUaf = constanciaUaf;
	}

	public Boolean getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(Boolean presupuesto) {
		this.presupuesto = presupuesto;
	}

	public Boolean getInformeSfp() {
		return informeSfp;
	}

	public void setInformeSfp(Boolean informeSfp) {
		this.informeSfp = informeSfp;
	}

	public Boolean getInformeMH() {
		return informeMH;
	}

	public void setInformeMH(Boolean informeMH) {
		this.informeMH = informeMH;
	}

	public Boolean getResMH_SEAF() {
		return resMH_SEAF;
	}

	public void setResMH_SEAF(Boolean resMH_SEAF) {
		this.resMH_SEAF = resMH_SEAF;
	}

	public Boolean getDecreto() {
		return decreto;
	}

	public void setDecreto(Boolean decreto) {
		this.decreto = decreto;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}

	public List<SolicitudTrasladoDet> getDetalleSolicitud() {
		return detalleSolicitud;
	}

	public void setDetalleSolicitud(List<SolicitudTrasladoDet> detalleSolicitud) {
		this.detalleSolicitud = detalleSolicitud;
	}

	public EmpleadoMovilidadCab getEmpleadoMovilidadCab() {
		return empleadoMovilidadCab;
	}

	public void setEmpleadoMovilidadCab(EmpleadoMovilidadCab empleadoMovilidadCab) {
		this.empleadoMovilidadCab = empleadoMovilidadCab;
	}

	public List<EmpleadoConceptoPago> getListaConceptoPagoAnterior() {
		return listaConceptoPagoAnterior;
	}

	public void setListaConceptoPagoAnterior(
			List<EmpleadoConceptoPago> listaConceptoPagoAnterior) {
		this.listaConceptoPagoAnterior = listaConceptoPagoAnterior;
	}

	public List<EmpleadoConceptoPago> getListaConceptoPagoNuevo() {
		return listaConceptoPagoNuevo;
	}

	public void setListaConceptoPagoNuevo(
			List<EmpleadoConceptoPago> listaConceptoPagoNuevo) {
		this.listaConceptoPagoNuevo = listaConceptoPagoNuevo;
	}
	
	public ActividadProceso actividadProcesoActual(){
		String sqlActProc = "select * from proceso.actividad_proceso act_proc join proceso.actividad act on act_proc.id_actividad = act.id_actividad "
				+"where act.descripcion = 'REGISTRAR TRASLADO CON LINEA';";
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
