package py.com.excelsis.sicca.movilidadLaboral.session.form;

import java.awt.BufferCapabilities.FlipContents;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.form.ListarConfiguracionUoDetFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Name("trasladoTmpSinSolEditCU663FC")
@Scope(ScopeType.CONVERSATION)
public class TrasladoTmpSinSolEditCU663FC {
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
	ValidadorController validadorController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(required = false, create = true)
	ListarConfiguracionUoDetFormController listarConfiguracionUoDetFormController;

	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private BuscadorDocsFC buscadorDocsDestino;
	private Persona persona;
	private EmpleadoPuesto empleadoPuesto;
	private EmpleadoPuesto funcionario;
	private Documentos docOrigen;
	private PlantaCargoDet plantaCargoDetView;
	private EmpleadoMovilidadCab empleadoMovilidadCabNuevasFunciones = new EmpleadoMovilidadCab();
	private SinAnx sinAnxSeleccionado = null;
	private UploadItem fileOrigen;
	private SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private Boolean comisionamiento = false;

	private List<ConfiguracionUoDet> configuracionUoDetList;
	private List<PlantaCargoDet> plantaCargoDetList;
	private List<EmpleadoConceptoPago> conceptoPagosActual;
	
	
	private List<ReferenciaAdjuntos> adjuntos = new ArrayList<ReferenciaAdjuntos>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosActual = new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagosAnterior = new ArrayList<EmpleadoConceptoPago>();
	private byte[] fileOrigenByte = null;

	private Date fechaDoc;
	private Date fechaInicio;
	private Date fechaFin;
	private Long idTipoDoc;
	private Long idPersona;
	private Long idFuncionario;
	private Long idSolicitudTransladoCab;
	private Long idPlantaCargoDet;
	private Long idPlantaCargoDetNuevasFunciones;
	private Long idEmpleadoPuesto;
	private Integer nroDoc;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private String fNameMostrar;
	private String fNameOrigen;
	private String typeOrigen = null;
	

	private Boolean primeraEntrada = true;
	private Boolean descargarOrigen = false;
	private Boolean adjuntarOrigen = false;

	public void init() throws Exception {
		if (primeraEntrada) {
			em.clear();
			cargarNiveentidadOee();
			selectedRow = -1;
			selectedRowPuesto = -1;
			empleadoPuesto = new EmpleadoPuesto();
			primeraEntrada = false;
			setearDatos();
		} 
		if (idFuncionario != null) {
			funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
			if (funcionario.getPlantaCargoDet().getContratado()) {
				funcionario = new EmpleadoPuesto();
				statusMessages.add(Severity.ERROR,
						"El funcionario Seleccionado no es Permanente");
				persona = new Persona();
				return;
			} else {
				persona = funcionario.getPersona();
				cargarNEOFuncionario();
				
			}
		}

		if (buscadorDocsDestino == null)
			buscadorDocsDestino = (BuscadorDocsFC) Component.getInstance(
					BuscadorDocsFC.class, true);
		if (primeraEntrada) {
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
			nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
		}
		nivelEntidadOeeUtil.init();
		// cargarPuestos();

	}

	public void initVer() throws Exception {
		if (idEmpleadoPuesto != null) {
			if (!seguridadUtilFormController.validarInput(
					idEmpleadoPuesto.toString(), TiposDatos.LONG.getValor(),
					null)) {
				return;
			}
			empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
			Long idComisionamiento = seleccionUtilFormController
			.traerDatosEspecificos("SITUACION EMPLEADO PUESTO",
			"COMISIONAMIENTO INTERNO").getIdDatosEspecificos();
			if(empleadoPuesto.getDatosEspecificosByIdDatosEspSituacion().getIdDatosEspecificos().intValue() == idComisionamiento.intValue())
				comisionamiento = true;
			else
				comisionamiento = false;
		}

		obtenerDatos();

		fechaInicio = empleadoPuesto.getFechaInicio();
	}

	@SuppressWarnings("unchecked")
	private void obtenerDatos() {

		empleadoPuesto = em.find(EmpleadoPuesto.class, idEmpleadoPuesto);
		idFuncionario = idEmpleadoPuesto;
		funcionario = em.find(EmpleadoPuesto.class, idFuncionario);
		adjuntos = em
				.createQuery(
						"Select r from ReferenciaAdjuntos r "
								+ " where r.idRegistroTabla=:idRegTabla")
				.setParameter("idRegTabla", idEmpleadoPuesto).getResultList();
		plantaCargoDetView = em.find(PlantaCargoDet.class, empleadoPuesto
				.getPlantaCargoDet().getIdPlantaCargoDet());
		ConfiguracionUoDet uoDet = em.find(ConfiguracionUoDet.class,
				plantaCargoDetView.getConfiguracionUoDet()
						.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(uoDet
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.setIdConfigCab(uoDet.getConfiguracionUoCab()
				.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();

		empleadoConceptoPagosActual = em
				.createQuery(
						"Select e from EmpleadoConceptoPago e "
								+ " where e.empleadoPuesto.idEmpleadoPuesto=:idEmpPuesto")
				.setParameter("idEmpPuesto", idEmpleadoPuesto).getResultList();

		List<EmpleadoMovilidadCab> mov = em.createQuery(
				"Select r from EmpleadoMovilidadCab r "
						+ " where r.empleadoPuestoNuevo.idEmpleadoPuesto="
						+ idEmpleadoPuesto).getResultList();
		// datos viejos
		if (!mov.isEmpty()) {
			funcionario = mov.get(0).getEmpleadoPuestoAnterior();
			buscarConceptoPagoFuncionario();// traer de la tabla empleado
			if (entidadOeeUtilAnterior == null)
				entidadOeeUtilAnterior = (NivelEntidadOeeUtil) Component
						.getInstance(NivelEntidadOeeUtil.class, true);
			entidadOeeUtilAnterior.setEm(em);
			entidadOeeUtilAnterior.setIdConfigCab(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getConfiguracionUoCab().getIdConfiguracionUo());
			entidadOeeUtilAnterior.setIdUnidadOrganizativa(funcionario
					.getPlantaCargoDet().getConfiguracionUoDet()
					.getIdConfiguracionUoDet());
			entidadOeeUtilAnterior.init2();
		}
		try {
			List<EmpleadoMovilidadCab> lista = em
					.createQuery(
							"Select e from EmpleadoMovilidadCab e "
									+ " where e.empleadoPuestoNuevo.idEmpleadoPuesto=:idEmpleadoPuesto")
					.setParameter("idEmpleadoPuesto", idEmpleadoPuesto)
					.getResultList();
			empleadoMovilidadCabNuevasFunciones = lista.get(0);
			Long id = empleadoMovilidadCabNuevasFunciones
					.getIdEmpleadoMovilidadCab();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unchecked")
	private void buscarConceptoPagoFuncionario() {
		String sql = "SELECT pago.* "
				+ "FROM movilidad.empleado_movilidad_cab cab "
				+ "INNER JOIN general.empleado_puesto emp ON (cab.id_empleado_puesto_anterior = emp.id_empleado_puesto) "
				+ "INNER JOIN general.empleado_concepto_pago pago ON (emp.id_empleado_puesto = pago.id_empleado_puesto) "
				+ "INNER JOIN general.persona persona ON (emp.id_persona = persona.id_persona) "
				+ "WHERE cab.id_empleado_puesto_anterior = emp.id_empleado_puesto AND  "
				+ "cab.activo = TRUE  " + "AND persona.id_persona = "
				+ funcionario.getPersona().getIdPersona();
		empleadoConceptoPagosAnterior = new ArrayList<EmpleadoConceptoPago>();
		empleadoConceptoPagosAnterior = em.createNativeQuery(sql,
				EmpleadoConceptoPago.class).getResultList();
	}

	private void cargarNiveentidadOee() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		// nivelEntidadOeeUtil.init2();
		configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
		configuracionUoDetList = em
				.createQuery(
						"Select det from ConfiguracionUoDet det "
								+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE order by det.orden asc ")
				.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab())
				.getResultList();
	}

	private void setearDatos() {
		persona = new Persona();
		funcionario = new EmpleadoPuesto();
		idFuncionario = null;
		plantaCargoDetList = new ArrayList<PlantaCargoDet>();
		conceptoPagosActual = new Vector<EmpleadoConceptoPago>();
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(null);
		nivelEntidadOeeUtil.setCodigoUnidOrgDep(null);
	}

	

	private void cargarNEOFuncionario() {
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(funcionario.getPlantaCargoDet()
				.getConfiguracionUoDet().getConfiguracionUoCab()
				.getIdConfiguracionUo());
		entidadOeeUtilAnterior.setIdUnidadOrganizativa(funcionario
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getIdConfiguracionUoDet());
		entidadOeeUtilAnterior.init2();
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

	/**
	 * Combo tipo de Documento
	 * 
	 * @return
	 */
	public List<SelectItem> updateTipoDocSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString(
				"opt_select_one")));
		for (DatosEspecificos o : datosEspecificosByTipoDocumento())
			si.add(new SelectItem(o.getIdDatosEspecificos(), ""
					+ o.getDescripcion()));
		return si;
	}

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

	public void descargarDoc() throws FileNotFoundException, IOException {
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

	private Boolean precondAdjuntarDocOrigen() {
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

	public void adjuntarDocOrigen() {
		if (!precondAdjuntarDocOrigen())
			return;
		fileOrigen = seleccionUtilFormController.crearUploadItem(fNameOrigen,
				fileOrigenByte.length, typeOrigen, fileOrigenByte);
		fNameMostrar = fNameOrigen;

		statusMessages.add(Severity.INFO, "Documento Adjuntado ");

	}

	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}

	public void setEntidadOeeUtilAnterior(
			NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}

	public String getUrlToPageSearchFuncionario() {

		return "/busquedas/funcionarios/EmpleadoPuestoList.xhtml?from=movilidadLaboral/transladoTmpSinSolicitud/TrasladoTmpSinSolEditCU663";
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

		// cargarPuestos();
		setearConPago();
	}

	public void agregar() {
		if (seleccionUtilFormController.getCodigoObj() == null
				|| seleccionUtilFormController.getValorObj() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Cod. Objeto Gasto");
			return;
		}
		if (seleccionUtilFormController.getCodigoObj().longValue() == 111) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Cod. Objeto Gasto distinto a 111");
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
		if (seleccionUtilFormController.getCategoria() != null)
			empleadoConceptoPago.setCategoria(seleccionUtilFormController
					.getCodigoCategoria());
		empleadoConceptoPago.setMonto(seleccionUtilFormController.getMonto());
		conceptoPagosActual.add(empleadoConceptoPago);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

		setearConPago();
	}

	private void setearConPago() {
		sinAnxSeleccionado = seleccionUtilFormController.getSinAnx();
		seleccionUtilFormController.setearValoresObjetosGasto();

	}

	public void eliminar(int index) {
		conceptoPagosActual.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
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
		
	}

	
	
	private boolean chkDatos() {
		if (nroDoc == null) {
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
		}
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

		if (fechaInicio == null) {
			statusMessages.add(Severity.ERROR,
					"Debe ingresar la Fecha de Inicio");
			return false;
		}
		if (fechaFin == null) {
			statusMessages.add(Severity.ERROR,
					"Debe ingresar la Fecha de Finalizacion");
			return false;
		}
		if (fechaInicio.after(fechaFin)) {
			statusMessages.add(Severity.ERROR,
					"La fecha de Inicion no puede ser mayo a la Fecha Fin");
		}

		if (conceptoPagosActual.isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe agregar almenos un Objeto de Gasto y Monto");
			return false;
		}
		

		return true;
	}

	private Boolean precondOrigenSearch() {
		if (nroDoc == null || fechaDoc == null && idTipoDoc != null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}
		return true;
	}

	private Documentos searchDoc() {
		String sql = "select Documentos from Documentos Documentos "
				+ " where Documentos.activo is true "
				+ " and Documentos.nroDocumento = :nroDoc and Documentos.fechaDoc = :fechaDoc"
				+ " and Documentos.datosEspecificos.idDatosEspecificos = :idTipoDoc";
		Query q = em.createQuery(sql);
		q.setParameter("nroDoc", nroDoc);
		q.setParameter("fechaDoc", fechaDoc);
		q.setParameter("idTipoDoc", idTipoDoc);

		List<Documentos> lista = q.getResultList();
		if (lista.size() > 0) {
			fNameMostrar = lista.get(0).getNombreFisicoDoc();
			return lista.get(0);
		}
		return null;
	}

	public void buscarDocOrigen() {
		if (!precondOrigenSearch())
			return;
		docOrigen = searchDoc();

		if (docOrigen != null) {
			fNameOrigen = docOrigen.getNombreFisicoDoc();
			fNameMostrar = docOrigen.getNombreFisicoDoc();
			descargarOrigen = true;
			adjuntarOrigen = false;
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			fNameMostrar = null;
			descargarOrigen = false;
			adjuntarOrigen = true;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");
		}
	}

	public String save() {
		try {
			if (!chkDatos())
				return null;
			em.clear();
			PlantaCargoDet puestoSelec = em.find(PlantaCargoDet.class,
					idPlantaCargoDet);
			/**
			 * Actualiza la tabla EMPLEADO_PUESTO el Funcionario seleccionado
			 * */

			funcionario.setPin(seleccionUtilFormController.generarPIN());
			funcionario.setActual(false);
			funcionario.setFechaFin(new Date());
			funcionario.setUsuMod(usuarioLogueado.getCodigoUsuario());
			funcionario.setFechaMod(new Date());

			em.merge(funcionario);

			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al
			 * Funcionario seleccionado
			 * */
			PlantaCargoDet plantaCargoDetFuncionario = em.find(
					PlantaCargoDet.class, funcionario.getPlantaCargoDet()
							.getIdPlantaCargoDet());
			plantaCargoDetFuncionario.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("LIBRE"));
			plantaCargoDetFuncionario.setUsuMod(usuarioLogueado
					.getCodigoUsuario());
			plantaCargoDetFuncionario.setFechaMod(new Date());
			plantaCargoDetFuncionario.setEstadoDet(null);

			em.merge(plantaCargoDetFuncionario);

			/***
			 * • Registra el histórico de cambios de estados del Puesto anterior
			 * en la tabla HISTORICOS_ESTADO , registrando el estado actual
			 * */
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("LIBRE"));
			historico.setFechaMod(new Date());
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historico.setPlantaCargoDet(plantaCargoDetFuncionario);

			em.persist(historico);

			/**
			 * se guardan los datos en la tabla EMPLEADO_PUESTO
			 * */

			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(seleccionUtilFormController
							.traerDatosEspecificos(
									"TIPOS DE INGRESOS Y MOVILIDAD",
									"TRASLADO TEMPORAL"));
			empleadoPuesto.setFechaInicio(fechaInicio);
			empleadoPuesto.setActual(true);
			empleadoPuesto.setActivo(true);
			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
			empleadoPuesto.setContratado(false);
			empleadoPuesto.setFechaFin(fechaFin);
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController
							.traerDatosEspecificos("ESTADO EMPLEADO PUESTO",
									"PERMANENTE"));
			if(!comisionamiento)
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspSituacion(seleccionUtilFormController
							.traerDatosEspecificos("SITUACION EMPLEADO PUESTO",
									"COMISIONADO DE"));
			else
				empleadoPuesto
				.setDatosEspecificosByIdDatosEspSituacion(seleccionUtilFormController
						.traerDatosEspecificos("SITUACION EMPLEADO PUESTO",
								"COMISIONAMIENTO INTERNO"));
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoRegistro(seleccionUtilFormController
							.traerDatosEspecificos(
									"TIPOS DE REGISTRO INGRESOS Y MOVILIDAD",
									"MOVILIDAD"));
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(searchDatosEsp());
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,
					idPlantaCargoDet));
			empleadoPuesto.setPersona(persona);
			empleadoPuesto.setIncidenAntiguedad(true);
			empleadoPuesto.setFechaInicio(fechaInicio);

			em.persist(empleadoPuesto);

			/**
			 * Cambiar el estado del puesto a OCUPADO
			 * */
			puestoSelec.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			puestoSelec.setUsuMod(usuarioLogueado.getCodigoUsuario());
			puestoSelec.setFechaMod(new Date());

			em.merge(puestoSelec);

			/**
			 * Generar un registro del cambio de estado en la tabla
			 * HISTORICOS_ESTADO
			 * */
			HistoricosEstado historial = new HistoricosEstado();
			historial.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			historial.setFechaMod(new Date());
			historial.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historial.setPlantaCargoDet(puestoSelec);

			em.persist(historial);

			/**
			 * • Gestionar objetos y categorías para el puesto ocupado
			 * 
			 * */

			for (EmpleadoConceptoPago conceptoPago : conceptoPagosActual) {
				conceptoPago.setEmpleadoPuesto(empleadoPuesto);
				conceptoPago.setFechaAlta(new Date());
				conceptoPago.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				conceptoPago.setAnho(Integer.parseInt(sdfAnio
						.format(fechaInicio)));

				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 * */
				if (conceptoPago.getCategoria() != null
						&& sinAnxSeleccionado != null) {
					SinAnx anx = em.find(SinAnx.class,
							sinAnxSeleccionado.getIdAnx());
					anx.setCantDisponible(anx.getCantDisponible() - 1);
					em.merge(anx);
				}

				em.persist(conceptoPago);

			}

			/**
			 * Registrar datos de movilidad.EMPLEADO_MOVILIDAD_CAB
			 * **/

			EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
			empleadoMovilidadCab.setActivo(true);
			empleadoMovilidadCab.setEmpleadoPuestoAnterior(funcionario);
			empleadoMovilidadCab.setEmpleadoPuestoNuevo(empleadoPuesto);
			
			empleadoMovilidadCab.setMovilidadSicca(true);
			empleadoMovilidadCab.setFechaInicio(fechaInicio);
			empleadoMovilidadCab.setFechaFin(fechaFin);
			empleadoMovilidadCab.setFechaAlta(new Date());
			empleadoMovilidadCab.setUsuAlta(usuarioLogueado.getCodigoUsuario());

			em.persist(empleadoMovilidadCab);

			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
			 * Insertar registro en Documentos y el archivo pdf
			 * 
			 * *
			 */
			// Por Acto administrativo de Entidad Origen
			Long idDocOrigen = null;
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
			else if(docOrigen != null)
				idDocOrigen = docOrigen.getIdDocumento();
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */

			insertarAdjunto(idDocOrigen);

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
			 * */

			insertarAdjunto(idDocDestino);

			em.flush();

			setearDatos();
			primeraEntrada = true;
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";
		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "trasladoTmpSinSolic663_edit";
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

	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto
	 * administrativo
	 * */
	private void insertarAdjunto(Long idDocumento) {
		ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
		referenciaAdjuntos.setPersona(persona);
		referenciaAdjuntos
				.setDocumentos(em.find(Documentos.class, idDocumento));
		referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto
				.getIdEmpleadoPuesto());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}

	private DatosEspecificos searchDatosEsp() {
		Query q = em
				.createQuery("select especif from DatosEspecificos especif "
						+ " where especif.activo is true and especif.descripcion = 'TRASLADO TEMPORAL' "
						+ "and especif.datosGenerales.activo is true and especif.datosGenerales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD'");
		return (DatosEspecificos) q.getSingleResult();

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

	public String toFindPersonaView() {
		if (funcionario == null || funcionario.getPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		String from = "";
		if (idEmpleadoPuesto == null)
			from = "/movilidadLaboral/transladoTmpSinSolicitud/TrasladoTmpSinSolViewCU663";
		else
			from = "/movilidadLaboral/transladoTmpSinSolicitud/TrasladoTmpSinSolViewCU663";
		return "/seleccion/persona/Persona.xhtml?personaFrom=" + from
				+ "&personaIdPersona="
				+ funcionario.getPersona().getIdPersona()
				+ "&conversationPropagation=join";
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

	public BuscadorDocsFC getBuscadorDocsDestino() {
		return buscadorDocsDestino;
	}

	public void setBuscadorDocsDestino(BuscadorDocsFC buscadorDocsDestino) {
		this.buscadorDocsDestino = buscadorDocsDestino;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public EmpleadoPuesto getEmpleadoPuesto() {
		return empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	public EmpleadoPuesto getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(EmpleadoPuesto funcionario) {
		this.funcionario = funcionario;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Long getIdFuncionario() {
		return idFuncionario;
	}

	public void setIdFuncionario(Long idFuncionario) {
		this.idFuncionario = idFuncionario;
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

	public Boolean getPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(Boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
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

	public List<EmpleadoConceptoPago> getConceptoPagosActual() {
		return conceptoPagosActual;
	}

	public void setConceptoPagosActual(
			List<EmpleadoConceptoPago> conceptoPagosActual) {
		this.conceptoPagosActual = conceptoPagosActual;
	}

	public Long getIdSolicitudTransladoCab() {
		return idSolicitudTransladoCab;
	}

	public void setIdSolicitudTransladoCab(Long idSolicitudTransladoCab) {
		this.idSolicitudTransladoCab = idSolicitudTransladoCab;
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

	public byte[] getFileOrigenByte() {
		return fileOrigenByte;
	}

	public void setFileOrigenByte(byte[] fileOrigenByte) {
		this.fileOrigenByte = fileOrigenByte;
	}

	public String getTypeOrigen() {
		return typeOrigen;
	}

	public void setTypeOrigen(String typeOrigen) {
		this.typeOrigen = typeOrigen;
	}

	public String getfNameOrigen() {
		return fNameOrigen;
	}

	public void setfNameOrigen(String fNameOrigen) {
		this.fNameOrigen = fNameOrigen;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
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

	public Long getIdPlantaCargoDetNuevasFunciones() {
		return idPlantaCargoDetNuevasFunciones;
	}

	public void setIdPlantaCargoDetNuevasFunciones(
			Long idPlantaCargoDetNuevasFunciones) {
		this.idPlantaCargoDetNuevasFunciones = idPlantaCargoDetNuevasFunciones;
	}

	

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}

	public PlantaCargoDet getPlantaCargoDetView() {
		return plantaCargoDetView;
	}

	public void setPlantaCargoDetView(PlantaCargoDet plantaCargoDetView) {
		this.plantaCargoDetView = plantaCargoDetView;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagosActual() {
		return empleadoConceptoPagosActual;
	}

	public void setEmpleadoConceptoPagosActual(
			List<EmpleadoConceptoPago> empleadoConceptoPagosActual) {
		this.empleadoConceptoPagosActual = empleadoConceptoPagosActual;
	}

	public List<EmpleadoConceptoPago> getEmpleadoConceptoPagosAnterior() {
		return empleadoConceptoPagosAnterior;
	}

	public void setEmpleadoConceptoPagosAnterior(
			List<EmpleadoConceptoPago> empleadoConceptoPagosAnterior) {
		this.empleadoConceptoPagosAnterior = empleadoConceptoPagosAnterior;
	}

	public EmpleadoMovilidadCab getEmpleadoMovilidadCabNuevasFunciones() {
		return empleadoMovilidadCabNuevasFunciones;
	}

	public void setEmpleadoMovilidadCabNuevasFunciones(
			EmpleadoMovilidadCab empleadoMovilidadCabNuevasFunciones) {
		this.empleadoMovilidadCabNuevasFunciones = empleadoMovilidadCabNuevasFunciones;
	}

	

	public SimpleDateFormat getSdfAnio() {
		return sdfAnio;
	}

	public void setSdfAnio(SimpleDateFormat sdfAnio) {
		this.sdfAnio = sdfAnio;
	}

	public Boolean getDescargarOrigen() {
		return descargarOrigen;
	}

	public void setDescargarOrigen(Boolean descargarOrigen) {
		this.descargarOrigen = descargarOrigen;
	}

	public Boolean getAdjuntarOrigen() {
		return adjuntarOrigen;
	}

	public void setAdjuntarOrigen(Boolean adjuntarOrigen) {
		this.adjuntarOrigen = adjuntarOrigen;
	}

	public Boolean getComisionamiento() {
		return comisionamiento;
	}

	public void setComisionamiento(Boolean comisionamiento) {
		this.comisionamiento = comisionamiento;
	}
	
	

}
