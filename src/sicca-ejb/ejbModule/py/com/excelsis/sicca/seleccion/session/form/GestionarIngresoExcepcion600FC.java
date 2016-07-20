package py.com.excelsis.sicca.seleccion.session.form;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
import org.jboss.seam.security.AuthorizationException;
import org.richfaces.model.UploadItem;
import org.richfaces.model.selection.SimpleSelection;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.EvalReferencialPostulante;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarIngresoExcepcion600FC")
public class GestionarIngresoExcepcion600FC {
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
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ValidadorController validadorController;

	List<EvalReferencialPostulante> lPostulantes;
	List<ConcursoPuestoDet> lPuestos;
	List<EmpleadoPuesto> lIngresos;
	private List<SelectItem> tipoNombramientoSelecItem;
	EvalReferencialPostulante selePostulante;
	ConcursoPuestoDet selePuesto;
	SimpleSelection selePuesto2;
	SimpleSelection selePostulante2;

	String observacion;
	private Boolean mostrarContratado = false;

	Date fechaIngreso;
	Long idTipoNombramiento;
	Date fechaFirmaContrato;
	Date fechaFinContrato;
	Integer nroContrato;
	public static String ESTADO_OCUPADO = "OCUPADO";
	public String direFisicaAdDocu;
	public String direFisicaAdContrato;
	private String TIPO_PUESTO = "PUESTO";
	private String TIPO_CONTRATADO = "CONTRATADO";
	private EstadoCab estadoOcupado;
	private SeguridadUtilFormController seguridadUtilFormController;
	private Integer nroDoc;
	private Date fechaDoc;
	private Long idTipoDoc;
	private String fNameMostrar;
	private String fName;
	private byte[] uFile = null;
	private String cType = null;
	private String fNameContrato;
	private byte[] uFileContrato = null;
	private String cTypeContrato = null;
	private Documentos docDecreto;
	private Documentos docActoAdmin;
	private UploadItem fileActoAdmin;
	private UploadItem fileContrato;
	private DatosEspecificos tipoIngreso = null;

	private Long idDEpermanente;
	private Long idDEcontratado;
	private Long idDEtipoRegistro;
	private Long idDEtipoDocContrato;
	private Integer idEstadoReservado;
	private Integer idEstadoUsado;
	private String obsEmpleadoPuesto;
	private Long idEDtipoDocContrato;
	private Integer idRefIngresado;

	private ConcursoPuestoAgrExc concursoPuestoAgrExc;
	private Long idExcepcion;

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}

	public void init() {
		validarInit();
		estadoOcupado = traerEstadoCab();
		initIDS();
		updateNombramiento();
		if (estadoOcupado != null) {
			cargarPostulantes();
			cargarPuestos(false);
			direFisicaAdDocu = generarDireFisicaAdDocu();
			direFisicaAdContrato = generarDireFisicaAdContrato();
			mostrarContratado = calcContratado();
		}
		cargarIngreso();
	}

	private void cargarCPAEXC() {
		if (idExcepcion != null) {
			Query q =
				em.createQuery("select ConcursoPuestoAgrExc from ConcursoPuestoAgrExc ConcursoPuestoAgrExc "
					+ " where ConcursoPuestoAgrExc.activo is true and ConcursoPuestoAgrExc.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
					+ " and  ConcursoPuestoAgrExc.excepcion.idExcepcion = :idExcepcion ");
			q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
			q.setParameter("idExcepcion", idExcepcion);
			List<ConcursoPuestoAgrExc> lista = (List<ConcursoPuestoAgrExc>) q.getResultList();
			if (lista.size() > 0) {
				concursoPuestoAgrExc = lista.get(0);
			} else
				concursoPuestoAgrExc = null;

		}
	}

	private void validarInit() {
		cargarCPAEXC();
		if (concursoPuestoAgrExc == null) {
			throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}
		if (seguridadUtilFormController == null)
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String estado =
			"CONTRATADO" + "#" + "FIRMADO DECRETO" + "#" + "FIRMADO NOMBRAMIENTO" + "#"
				+ "PERMANENTE D12";

		seguridadUtilFormController.validarSeleccion2(concursoPuestoAgrExc.getIdConcursoPuestoAgrExc(), estado);
		if (concursoPuestoAgrExc.getEstado().equalsIgnoreCase("PERMANENTE D12")) {
			if (concursoPuestoAgrExc.getNombramiento() == null
				|| !concursoPuestoAgrExc.getNombramiento())
				throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
		}

	}

	public void cambiarDoc() {
		nroDoc = null;
		fechaDoc = null;
		idTipoDoc = null;
		fName = null;
		uFile = null;
		cType = null;
		docDecreto = null;
		fNameMostrar = null;
		fileActoAdmin = null;
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
					+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.valorAlf like 'CON' and d.activo=true order by d.descripcion").getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	private Boolean precondSearch() {
		if (nroDoc == null || fechaDoc == null || idTipoDoc == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}
		return true;
	}

	private Documentos searchDoc() {
		Query q =
			em.createQuery("select Documentos from Documentos Documentos "
				+ " where Documentos.activo is true "
				+ " and Documentos.nroDocumento = :nroDoc and Documentos.fechaDoc = :fechaDoc"
				+ " and Documentos.datosEspecificos.idDatosEspecificos = :idTipoDoc");
		q.setParameter("nroDoc", nroDoc);
		q.setParameter("fechaDoc", fechaDoc);
		q.setParameter("idTipoDoc", idTipoDoc);
		List<Documentos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public void buscarDoc() {
		if (!precondSearch())
			return;
		docDecreto = searchDoc();
		if (docDecreto != null) {
			fNameMostrar = docDecreto.getNombreFisicoDoc();
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			fNameMostrar = null;
			docActoAdmin = null;
			fileActoAdmin = null;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");
		}
	}

	private Boolean precondAdjuntarDoc() {
		if (!precondSearch())
			return false;
		if (uFile == null) {
			statusMessages.add(Severity.ERROR, "Debe completar los campos obligatorios");
			return false;
		}
		// Verificar que ya no exista
		Documentos doc = searchDoc();
		if (doc != null) {
			statusMessages.add(Severity.ERROR, "No se puede adjuntar, ya existe un documento con los mismos parámetros");
			return false;
		}
		return true;
	}

	private Boolean precondAdjuntarContrato() {

		if (uFileContrato == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar un Contrato antes");
			return false;
		}

		return true;
	}

	public void adjuntarDoc() {
		if (!precondAdjuntarDoc())
			return;
		docActoAdmin = docDecreto;
		fileActoAdmin =
			seleccionUtilFormController.crearUploadItem(fName, uFile.length, cType, uFile);

		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

	}

	public void adjuntarDocContrato() {
		if (!precondAdjuntarContrato())
			return;
		fileContrato =
			seleccionUtilFormController.crearUploadItem(fNameContrato, uFileContrato.length, cTypeContrato, uFileContrato);

		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));

	}

	public void adjuntarDocContrato2() {

	}

	public void descargarDoc() throws FileNotFoundException, IOException {
		if (fileActoAdmin != null) {
			admDocAdjuntoFormController.enviarArchivoANavegador(fileActoAdmin.getFileName(), fileActoAdmin.getFile());
		} else {
			statusMessages.add(Severity.ERROR, "No existe documento que descargar");
		}
	}

	private void validarOee() {
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		String estados =
			seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "FIRMADO NOMBRAMIENTO")
				+ "#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "CONTRATADOS")
				+ "#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "FIRMADO DECRETO PRESIDENCIAL");

		seguridadUtilFormController.verificarPerteneceOee(null, grupoPuestosController.getIdConcursoPuestoAgr(), estados, ActividadEnum.INGRESAR_POSTULANTE.getValor());
	}

	public void takeSelectionPostulante() {
		Iterator<Object> iterator = selePostulante2.getKeys();
		while (iterator.hasNext()) {
			Integer key = (Integer) iterator.next();
			selePostulante = lPostulantes.get(key.intValue());

		}
	}

	public void takeSelectionPuesto() {
		Iterator<Object> iterator = selePuesto2.getKeys();
		while (iterator.hasNext()) {
			Integer key = (Integer) iterator.next();
			selePuesto = lPuestos.get(key.intValue());
			idTipoNombramiento = null;

			updateNombramiento();

		}
	}

	private Boolean calcContratado() {
		Query q =
			em.createQuery("select Referencias from Referencias Referencias "
				+ "where Referencias.valorNum = "
				+ grupoPuestosController.getConcursoPuestoAgr().getEstado()
				+ " and Referencias.activo = true");
		List<Referencias> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0).getDescAbrev().equalsIgnoreCase(TIPO_CONTRATADO);
		} else {
			return false;
		}

	}

	public String generarDireFisicaAdContrato() {
		Calendar cal = Calendar.getInstance();
		String cSeparador = "/";
		String respuesta =
			"SICCA"
				+ cSeparador
				+ +cal.get(Calendar.YEAR)
				+ cSeparador
				+ "OEE"
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getIdConcurso()
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr();

		return respuesta;

	}

	public String generarDireFisicaAdDocu() {
		Calendar cal = Calendar.getInstance();
		String cSeparador = "/";
		String respuesta =
			"SICCA"
				+ cSeparador
				+ +cal.get(Calendar.YEAR)
				+ cSeparador
				+ "OEE"
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getConfiguracionUoCab().getIdConfiguracionUo()
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getConcurso().getIdConcurso()
				+ cSeparador
				+ grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr();

		return respuesta;
	}

	public String traerTipoNombramientos(Long idPlantaCargo) {
		String respuesta = "";
		Query q =
			em.createQuery("select detTipoNombramiento from DetTipoNombramiento detTipoNombramiento "
				+ " where detTipoNombramiento.plantaCargoDet.idPlantaCargoDet = " + idPlantaCargo);
		List<DetTipoNombramiento> lista = q.getResultList();
		for (DetTipoNombramiento o : lista) {
			if (o.getTipoNombramiento().getDescripcion() != null) {
				respuesta += "," + o.getTipoNombramiento().getDescripcion();
			}
		}
		System.out.println("LA RESPUESTA : " + respuesta);
		if (!respuesta.isEmpty()) {
			respuesta = respuesta.replaceFirst(",", "");
		} else {
			respuesta = "-";
		}
		return respuesta;
	}

	public String traerCodPuesto(PlantaCargoDet planta) {
		String respuesta = "-";
		respuesta = grupoPuestosController.obtenerCodigoPuesto(planta);
		return respuesta;
	}

	public String traerModalidad(PlantaCargoDet planta) {
		String respuesta = "-";
		if (planta.getPermanente()) {
			respuesta = "Permanente";
		} else if (planta.getContratado()) {
			respuesta = "Contratado";
		}
		return respuesta;
	}

	public void updateNombramiento() {
		List<DetTipoNombramiento> lTipoNombramiento = getNombramiento();
		tipoNombramientoSelecItem = new ArrayList<SelectItem>();
		buildNombramientoSelectItem(lTipoNombramiento);

	}

	private List<DetTipoNombramiento> getNombramiento() {
		List<DetTipoNombramiento> lTipoNombramiento = new ArrayList<DetTipoNombramiento>();
		if (selePuesto != null) {
			Long idPlanta = selePuesto.getPlantaCargoDet().getIdPlantaCargoDet();

			Query q =
				em.createQuery("select detTipoNombramiento from DetTipoNombramiento detTipoNombramiento"
					+ " where detTipoNombramiento.plantaCargoDet.idPlantaCargoDet = "
					+ idPlanta
					+ " and " + "detTipoNombramiento.plantaCargoDet.activo = true");
			lTipoNombramiento = q.getResultList();

		}
		return lTipoNombramiento;
	}

	private void buildNombramientoSelectItem(List<DetTipoNombramiento> lista) {
		if (tipoNombramientoSelecItem == null)
			tipoNombramientoSelecItem = new ArrayList<SelectItem>();
		else
			tipoNombramientoSelecItem.clear();
		if (lista.size() > 1)
			tipoNombramientoSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		if (lista.size() == 0)
			tipoNombramientoSelecItem.add(new SelectItem(null, "-"));
		if (lista.size() == 1) {
			idTipoNombramiento = lista.get(0).getIdDetTipoNombramiento();
		} else {
			idTipoNombramiento = null;
		}
		for (DetTipoNombramiento o : lista) {
			tipoNombramientoSelecItem.add(new SelectItem(o.getTipoNombramiento().getIdTipoNombramiento(), o.getTipoNombramiento().getDescripcion()));
		}
	}

	private Boolean precondAgregarIngreso() {
		if (selePostulante == null || selePuesto == null || fechaIngreso == null
			|| idTipoNombramiento == null) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU420_completarDatosRequeridos"));
			return false;
		}
		return true;
	}

	private String traerCategoria() {
		String respuesta = null;
		Query q =
			em.createQuery("select grupoConceptoPago from GrupoConceptoPago  grupoConceptoPago where "
				+ "grupoConceptoPago.concursoPuestoAgr.idConcursoPuestoAgr =  "
				+ grupoPuestosController.getIdConcursoPuestoAgr()
				+ " AND "
				+ "grupoConceptoPago.objCodigo = 111");
		List<GrupoConceptoPago> lista = q.getResultList();
		if (lista.size() == 1) {
			respuesta = lista.get(0).getCategoria();
		}
		return respuesta;
	}

	private void cargarIngreso() {
		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		lIngresos = q.getResultList();
	}

	public EmpleadoPuesto agregarIngreso() {
		if (lIngresos == null) {
			lIngresos = new ArrayList<EmpleadoPuesto>();
		}
		if (precondAgregarIngreso()) {
			EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();

			empleadoPuesto.setActivo(true);
			empleadoPuesto.setIncidenAntiguedad(true);
			empleadoPuesto.setCategoria(traerCategoria());
			empleadoPuesto.setContratado(selePuesto.getPlantaCargoDet().getContratado());
			empleadoPuesto.setPersona(selePostulante.getPostulacion().getPersonaPostulante().getPersona());
			if (selePuesto.getPlantaCargoDet().getContratado()) {
			  
				/*
				 * Si la modalidad de ocupación el puesto marcado es PERMANENTE, no se debe mostrar la sección de contratados. (campo CONTRATADO = FALSE y campo PERMANENTE = TRUE en la tabla
				 * PLANTA_CARGO_DET)
				 */
				empleadoPuesto.setFechaFinContrato(fechaFinContrato);
				empleadoPuesto.setFechaInicio(fechaIngreso);
				empleadoPuesto.setFechaFirmaContrato(fechaFirmaContrato);
				empleadoPuesto.setNroContrato(nroContrato);
				empleadoPuesto.setContratado(true);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idDEcontratado);

			} else {
				empleadoPuesto.setContratado(false);
				empleadoPuesto.setDatosEspecificosByIdDatosEspEstado(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspEstado().setIdDatosEspecificos(idDEpermanente);
			}

			empleadoPuesto.setObservacion(obsEmpleadoPuesto);
			empleadoPuesto.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(tipoIngreso);
			empleadoPuesto.setActual(true);
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(new DatosEspecificos());
			empleadoPuesto.getDatosEspecificosByIdDatosEspTipoRegistro().setIdDatosEspecificos(idDEtipoRegistro);
			empleadoPuesto.setPin(generarPin());
			empleadoPuesto.setFechaInicio(fechaIngreso);
			empleadoPuesto.setTipoNombramiento(em.find(TipoNombramiento.class, idTipoNombramiento));
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setPlantaCargoDet(selePuesto.getPlantaCargoDet());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());

			// Cambiar el estado del Puesto
			selePuesto.getPlantaCargoDet().setEstadoCab(estadoOcupado);
			selePuesto.setPlantaCargoDet(em.merge(selePuesto.getPlantaCargoDet()));
			em.persist(empleadoPuesto);
			/* Incidencia 0001604 */
			selePuesto.setEstadoDet(new EstadoDet());
			selePuesto.getEstadoDet().setIdEstadoDet(idEDtipoDocContrato);

			/*******************/
			empleadoPuesto.setPlantaCargoDet(cambiarEstadoPlanta(empleadoPuesto));
			insertarHistorico(empleadoPuesto.getPlantaCargoDet());
			return empleadoPuesto;

		}
		return null;
	}

	private void initIDS() {
		idDEpermanente =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "PERMANENTE").getIdDatosEspecificos();
		idDEcontratado =
			seleccionUtilFormController.traerDatosEspecificos("ESTADO EMPLEADO PUESTO", "CONTRATADO").getIdDatosEspecificos();
		idDEtipoRegistro =
			seleccionUtilFormController.traerDatosEspecificos("TIPOS DE REGISTRO INGRESOS Y MOVILIDAD", "INGRESO").getIdDatosEspecificos();
		idEstadoReservado =
			seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_CATEGORIA", "RESERVADO");
		idEstadoUsado =
			seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_CATEGORIA", "USADO");
		idDEtipoDocContrato =
			datosEspecificosHome.getDatosIngresoContrato().getIdDatosEspecificos();
		idEDtipoDocContrato =
			seleccionUtilFormController.buscarEstadoDet("CONCURSO", "INGRESADO").getIdEstadoDet();
		idRefIngresado =
			seleccionUtilFormController.getIdEstadosReferencia("ESTADOS_GRUPO", "INGRESADO");

	}

	private String generarPin() {
		return seleccionUtilFormController.generarPIN();
	}

	private void limpiar() {
		if (selePostulante2 != null)
			selePostulante2.clear();
		if (selePuesto2 != null)
			selePuesto2.clear();
		selePostulante = null;
		selePuesto = null;
		updateNombramiento();
		fechaIngreso = null;
		obsEmpleadoPuesto = null;
		tipoIngreso = null;
	}

	private void limpiar2() {
		if (selePostulante2 != null)
			selePostulante2.clear();
		if (selePuesto2 != null)
			selePuesto2.clear();
		selePostulante = null;
		selePuesto = null;
		updateNombramiento();
		fechaIngreso = null;
		obsEmpleadoPuesto = null;
		tipoIngreso = null;
		nroDoc = null;
		fechaDoc = null;
		idTipoDoc = null;
		observacion = null;
		fechaFinContrato = null;
		fechaFirmaContrato = null;
		fechaIngreso = null;
		fechaDoc = null;
	}

	private Boolean precondSgteTarea() {
		if (lIngresos == null || lIngresos.size() == 0) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU420_nohayDatos")
				+ " en Ingresos");
			return false;
		}
		if ((lPostulantes.size() - lPuestos.size()) > 0) {
			statusMessages.add(Severity.ERROR, "Debe gestionarse exclusión de postulantes y el paso de los mismos a Lista de Elegibles");
			return false;
		} else if ((lPostulantes.size() - lPuestos.size()) < 0) {
			statusMessages.add(Severity.ERROR, "Debe gestionarse excepción para cubrir puestos disponible");
			return false;
		}
		return true;
	}

	private void inactivar() {
		Iterator iter = null;
		for (EmpleadoPuesto o : lIngresos) {
			iter = o.getPlantaCargoDet().getDetCondicionTrabajos().iterator();
			while (iter.hasNext()) {
				// Inactivacion
				DetCondicionTrabajo entityCopia = new DetCondicionTrabajo();
				DetCondicionTrabajo entity = (DetCondicionTrabajo) iter.next();
				entity.setActivo(false);
				em.merge(entity);
			}
			iter = o.getPlantaCargoDet().getDetCondicionSegurs().iterator();
			while (iter.hasNext()) {
				DetCondicionSegur entity = (DetCondicionSegur) iter.next();
				entity.setActivo(false);
				em.merge(entity);
			}
			iter = o.getPlantaCargoDet().getDetCondicionTrabajoEspecifs().iterator();
			while (iter.hasNext()) {
				DetCondicionTrabajoEspecif entity = (DetCondicionTrabajoEspecif) iter.next();
				entity.setActivo(false);
				em.merge(entity);
			}
			iter = o.getPlantaCargoDet().getDetContenidoFuncionals().iterator();
			while (iter.hasNext()) {
				DetContenidoFuncional entity = (DetContenidoFuncional) iter.next();
				for (DetDescripcionContFuncional p : entity.getDetDescripcionContFuncionals()) {
					p.setActivo(false);
				}
				entity.setActivo(false);
				em.merge(entity);
			}
			iter = o.getPlantaCargoDet().getDetReqMins().iterator();
			while (iter.hasNext()) {
				DetReqMin entity = (DetReqMin) iter.next();
				for (DetMinimosRequeridos p : entity.getDetMinimosRequeridoses()) {
					p.setActivo(false);
				}
				for (DetOpcionesConvenientes p : entity.getDetOpcionesConvenienteses()) {
					p.setActivo(false);
				}
				entity.setActivo(false);
				em.merge(entity);
			}
			iter = o.getPlantaCargoDet().getPuestoConceptoPagos().iterator();
			while (iter.hasNext()) {
				PuestoConceptoPago entity = (PuestoConceptoPago) iter.next();
				entity.setActivo(false);
				em.merge(entity);
			}
		}
	}

	private void inactivarYcopiarDets() {
		inactivar();
		/* Cargar Lista de Grupos a copiar */
		Query q = null;
		q =
			em.createQuery("select DetCondicionTrabajo from DetCondicionTrabajo DetCondicionTrabajo "
				+ "where DetCondicionTrabajo.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetCondicionTrabajo> lCondTrabajoCopy = q.getResultList();
		q =
			em.createQuery("select DetContenidoFuncional from DetContenidoFuncional DetContenidoFuncional "
				+ "where DetContenidoFuncional.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetContenidoFuncional> lContFuncionalCopy = q.getResultList();

		q =
			em.createQuery("select DetCondicionTrabajoEspecif from DetCondicionTrabajoEspecif DetCondicionTrabajoEspecif "
				+ "where DetCondicionTrabajoEspecif.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetCondicionTrabajoEspecif> lCondTrabEspeCopy = q.getResultList();

		q =
			em.createQuery("select DetCondicionSegur from DetCondicionSegur DetCondicionSegur "
				+ "where DetCondicionSegur.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetCondicionSegur> lCondSegurCopy = q.getResultList();

		q =
			em.createQuery("select DetReqMin from DetReqMin DetReqMin "
				+ "where DetReqMin.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ grupoPuestosController.getIdConcursoPuestoAgr());
		List<DetReqMin> lReqMinCopy = q.getResultList();

		q =
			em.createQuery("select PuestoConceptoPago from PuestoConceptoPago PuestoConceptoPago "
				+ " where  PuestoConceptoPago.activo is true and  PuestoConceptoPago.estado =  "
				+ idEstadoReservado);
		List<PuestoConceptoPago> lPuestoConceptoPagoCopy = q.getResultList();

		// Creacion de nuevos registros
		for (EmpleadoPuesto o : lIngresos) {
			for (DetCondicionTrabajo p : lCondTrabajoCopy) {
				DetCondicionTrabajo entity = new DetCondicionTrabajo();
				entity.setActivo(true);
				entity.setPlantaCargoDet(o.getPlantaCargoDet());
				entity.setCondicionTrabajo(p.getCondicionTrabajo());
				entity.setCpt(p.getCpt());
				entity.setPuntajeCondTrab(p.getPuntajeCondTrab());
				entity.setTipo(TIPO_PUESTO);
				em.persist(entity);
			}
			for (DetCondicionSegur p : lCondSegurCopy) {
				DetCondicionSegur entity = new DetCondicionSegur();
				entity.setActivo(true);
				entity.setAjustes(p.getAjustes());
				entity.setPlantaCargoDet(o.getPlantaCargoDet());
				entity.setCondicionSegur(p.getCondicionSegur());
				entity.setCpt(p.getCpt());
				entity.setJustificacion(p.getJustificacion());
				entity.setPuntajeCondSegur(p.getPuntajeCondSegur());
				entity.setTipo(TIPO_PUESTO);
				em.persist(entity);
			}
			for (DetCondicionTrabajoEspecif p : lCondTrabEspeCopy) {
				DetCondicionTrabajoEspecif entity = new DetCondicionTrabajoEspecif();
				entity.setActivo(true);
				entity.setAjustes(p.getAjustes());
				entity.setPlantaCargoDet(o.getPlantaCargoDet());
				entity.setCondicionTrabajoEspecif(p.getCondicionTrabajoEspecif());
				entity.setCpt(p.getCpt());
				entity.setJustificacion(p.getJustificacion());
				entity.setPuntajeCondTrabEspecif(p.getPuntajeCondTrabEspecif());
				entity.setTipo(TIPO_PUESTO);
				em.persist(entity);
			}
			for (DetContenidoFuncional p : lContFuncionalCopy) {
				DetContenidoFuncional entity = new DetContenidoFuncional();
				entity.setActivo(true);
				entity.setCpt(p.getCpt());
				entity.setPlantaCargoDet(o.getPlantaCargoDet());
				entity.setContenidoFuncional(p.getContenidoFuncional());
				entity.setHomologacionPerfilMatriz(p.getHomologacionPerfilMatriz());
				entity.setPuntaje(p.getPuntaje());
				entity.setTipo(TIPO_PUESTO);
				em.persist(entity);
				for (DetDescripcionContFuncional w : p.getDetDescripcionContFuncionals()) {
					DetDescripcionContFuncional entity2 = new DetDescripcionContFuncional();
					entity2.setActivo(true);
					entity2.setDescripcion(w.getDescripcion());
					entity2.setDetContenidoFuncional(entity);
					em.persist(entity2);
				}
			}
			for (DetReqMin p : lReqMinCopy) {
				DetReqMin entity = new DetReqMin();
				entity.setActivo(true);
				entity.setPlantaCargoDet(o.getPlantaCargoDet());
				entity.setCpt(p.getCpt());
				entity.setHomologacionPerfilMatriz(p.getHomologacionPerfilMatriz());
				entity.setPuntajeReqMin(p.getPuntajeReqMin());
				entity.setRequisitoMinimoCpt(p.getRequisitoMinimoCpt());
				entity.setTipo(TIPO_PUESTO);
				em.persist(entity);
				for (DetMinimosRequeridos w : p.getDetMinimosRequeridoses()) {
					DetMinimosRequeridos entity2 = new DetMinimosRequeridos();
					entity2.setActivo(true);
					entity2.setDetReqMin(entity);
					entity2.setMinimosRequeridos(w.getMinimosRequeridos());
				}
				for (DetOpcionesConvenientes w : p.getDetOpcionesConvenienteses()) {
					DetOpcionesConvenientes entity2 = new DetOpcionesConvenientes();
					entity2.setActivo(true);
					entity2.setDetReqMin(entity);
					entity2.setOpcConvenientes(w.getOpcConvenientes());
				}

			}
			for (PuestoConceptoPago p : lPuestoConceptoPagoCopy) {

				EmpleadoConceptoPago entity2 = new EmpleadoConceptoPago();
				entity2.setAnho(p.getAnho());
				entity2.setCategoria(p.getCategoria());
				entity2.setEmpleadoPuesto(o);
				entity2.setFechaAlta(new Date());
				entity2.setMonto((p.getMonto() == null ? 0 : p.getMonto()));
				entity2.setObjCodigo(p.getObjCodigo());
				entity2.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(entity2);
				p.setEstado(idEstadoUsado);
				p = em.merge(p);
			}
		}
	}

	private EstadoCab traerEstadoCab() {
		Query q =
			em.createQuery("select EstadoCab from EstadoCab EstadoCab"
				+ " where EstadoCab.descripcion = '" + ESTADO_OCUPADO
				+ "' and EstadoCab.activo = true");

		List<EstadoCab> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0);
		}
		return null;

	}

	private void insertarHistorico(PlantaCargoDet elPuesto) {
		HistoricosEstado historico = new HistoricosEstado();
		historico.setEstadoCab(estadoOcupado);
		historico.setFechaMod(new Date());
		historico.setPlantaCargoDet(elPuesto);
		historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.persist(historico);
	}

	private PlantaCargoDet cambiarEstadoPlanta(EmpleadoPuesto o) {
		o.getPlantaCargoDet().setEstadoCab(estadoOcupado);
		o.setPlantaCargoDet(em.merge(o.getPlantaCargoDet()));
		return o.getPlantaCargoDet();
	}

	private Boolean precondSave() {
		// Testar....
		
		ConfiguracionUoCab oee =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Persona persona = selePostulante.getPostulacion().getPersonaPostulante().getPersona();
		PlantaCargoDet puesto = selePuesto.getPlantaCargoDet();
		String grupoValidacion = null;
		DatosEspecificos tipoConcurso =
			grupoPuestosController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc();
		if (tipoConcurso.getDescripcion().equalsIgnoreCase("CONCURSO PUBLICO DE OPOSICION")) {
			tipoConcurso =
				seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR CONCURSO PUBLICO DE OPOSICION");
			if (traerModalidad(puesto).equalsIgnoreCase("PERMANENTE")) {
				grupoValidacion = "ICPOPER";
			} else {
				grupoValidacion = "ICPOCO";
			}
		} else if (tipoConcurso.getDescripcion().equalsIgnoreCase("CONCURSO DE MERITOS")) {
			tipoConcurso =
				seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR CONCURSO DE MERITOS");
			grupoValidacion = "ICMER";
		} else if (tipoConcurso.getDescripcion().equalsIgnoreCase("CONCURSO SIMPLIFICADO")) {
			tipoConcurso =
				seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR CONCURSO SIMPLIFICADO");
			grupoValidacion = "ICSIMP";
		}

		if(tipoConcurso != null)
			tipoIngreso = tipoConcurso;
		ValidadorDTO validadorDTO =
			validadorController.validarCfg("PERSONA", grupoValidacion, persona, puesto, null, oee);

		if (validadorDTO.isValido()) {
			validadorDTO =
				validadorController.validarCfg("PUESTO", grupoValidacion, persona, puesto, null, oee);
			if (validadorDTO.isValido())
				return true;
			else {
				statusMessages.add(Severity.ERROR, validadorDTO.getMensaje());
				return false;
			}
		} else {
			statusMessages.add(Severity.ERROR, validadorDTO.getMensaje());
			return false;
		}
	}

	private void crearLegajo(Persona persona) {
		Query q =
			em.createQuery("select Legajos from Legajos Legajos where legajos.persona.idPersona = :idPersona ");
		q.setParameter("idPersona", persona.getIdPersona());
		List<Legajos> lista = q.getResultList();
		if (lista.size() == 0) {
			Long id = persona.getIdPersona();
			Legajos legajo = new Legajos();
			legajo.setPersona(em.find(Persona.class, id));
			legajo.setFechaIngreso(fechaIngreso);
			//legajo.setDatosEspecificosTipoIngreso(new DatosEspecificos());
			legajo.setDatosEspecificosTipoIngreso(tipoIngreso);
			em.persist(legajo);
		}
	}

	public String save() {
		if (!precondSave())
			return null;
		try {
			EmpleadoPuesto empleadoPuesto = agregarIngreso();
			if (empleadoPuesto != null) {
				inactivarYcopiarDets();
				updateUnicoEmpleadoPuesto();
				adjuntarDocumento2(empleadoPuesto.getIdEmpleadoPuesto());
				adjuntarContrato(empleadoPuesto.getIdEmpleadoPuesto());
				crearLegajo(selePostulante.getPostulacion().getPersonaPostulante().getPersona());
				em.flush();
				statusMessages.clear();
				statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				limpiar2();
				return "OK";
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}

	private void adjuntarContrato(Long idEmpleadoPuesto) {
		try {
			Long idDoc = null;
			if (fileContrato != null) {
				idDoc =
					admDocAdjuntoFormController.guardarDoc(fileContrato, direFisicaAdContrato, "INGRESO_EXC’", idDEtipoDocContrato, "EmpleadoPuesto", null);
				if (idDoc != null) {
					// em.flush();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setNroContrato(nroContrato);
					doc.setFechaFinContrato(fechaFinContrato);
					doc.setFechaFirmaContrato(fechaFirmaContrato);
					doc = em.merge(doc);
					//
					ReferenciaAdjuntos refAdj = new ReferenciaAdjuntos();
					refAdj.setUsuAlta(usuarioLogueado.getCodigoUsuario());
					refAdj.setFechaAlta(new Date());
					refAdj.setPersona(selePostulante.getPostulacion().getPersonaPostulante().getPersona());
					refAdj.setDocumentos(new Documentos());
					refAdj.getDocumentos().setIdDocumento(idDoc);
					refAdj.setIdRegistroTabla(idEmpleadoPuesto);
					em.persist(refAdj);
					// em.flush();
					limpiarDatosContrato();
					// statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
	}

	private void limpiarDatosContrato() {
		nroContrato = null;
		fechaFinContrato = null;
		fechaFirmaContrato = null;

	}

	private void adjuntarDocumento2(Long idEmpleadoPuesto) {
		try {
			Long idDoc = null;
			if (docDecreto == null) {
				idDoc =
					admDocAdjuntoFormController.guardarDoc(fileActoAdmin, direFisicaAdDocu, "INGRESO_EXC", idTipoDoc, "EmpleadoPuesto", null);
				if (idDoc != null) {
					em.flush();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
					docDecreto = em.find(Documentos.class, idDoc);
					docDecreto.setFechaDoc(fechaDoc);
					docDecreto.setNroDocumento(nroDoc);
					docDecreto.setAnhoDocumento(Integer.parseInt(sdf.format(fechaDoc)));
					docDecreto = em.merge(docDecreto);
					em.flush();
					statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));
				}
			} else {
				idDoc = docDecreto.getIdDocumento();
			}
			if (idDoc != null) {
				ReferenciaAdjuntos refAdj = new ReferenciaAdjuntos();
				refAdj.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				refAdj.setFechaAlta(new Date());
				refAdj.setPersona(selePostulante.getPostulacion().getPersonaPostulante().getPersona());
				refAdj.setDocumentos(new Documentos());
				refAdj.getDocumentos().setIdDocumento(idDoc);
				refAdj.setIdRegistroTabla(idEmpleadoPuesto);
				em.persist(refAdj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
	}

	private void updateUnicoEmpleadoPuesto() {

		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.activo is true and EmpleadoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		List lista = q.getResultList();
		if (lista.size() == 1) {
			q =
				em.createQuery("select ListaElegiblesCab from ListaElegiblesCab ListaElegiblesCab "
					+ " where ListaElegiblesCab.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo");
			q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
			List<ListaElegiblesCab> lista2 = q.getResultList();
			if (lista2.size() > 0) {
				ListaElegiblesCab entity = lista2.get(0);
				entity.setVtoValidezLista(new Date());
				entity = em.merge(entity);
			}
		}
	}

	private void insertarHistorial() {

		HistorialExcepcion he = new HistorialExcepcion();
		he.setFechaAlta(new Date());
		he.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		he.setConcursoGrupoAgr(new ConcursoPuestoAgr());
		he.getConcursoGrupoAgr().setIdConcursoPuestoAgr(grupoPuestosController.getIdConcursoPuestoAgr());
		he.setExcepcion(new Excepcion());
		he.getExcepcion().setIdExcepcion(idExcepcion);
		he.setObservacion(observacion);
		he.setEstado("INGRESADO");
		em.persist(he);

	}

	public String fin() {
		// Controles particulares
		if (!precondSgteTarea()) {
			return "FAIL";
		}
		try {
			concursoPuestoAgrExc.setEstado("INGRESADO");
			concursoPuestoAgrExc.setFechaMod(new Date());
			concursoPuestoAgrExc.setUsuMod(usuarioLogueado.getCodigoUsuario());
			em.merge(concursoPuestoAgrExc);

			insertarHistorial();
			// Actualizar el estado de la Excepcion
			Excepcion ex = em.find(Excepcion.class, idExcepcion);
			ex.setEstado("INGRESADO");
			ex.setFechaMod(new Date());
			ex.setUsuMod(usuarioLogueado.getCodigoUsuario());
			ex = em.merge(ex);
			em.flush();
			return "OK";

		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}

	public void eliminarIngreso(Integer elIndice) {
		if (lIngresos.size() > elIndice) {
			lIngresos.remove(elIndice.intValue());
			cargarPostulantes();
			cargarPuestos(false);
		}
	}

	private String noIncluirPostulantes() {
		String respuesta = null;
		if (lIngresos != null && lIngresos.size() > 0) {
			respuesta = "";
			for (EmpleadoPuesto o : lIngresos) {
				respuesta += "," + o.getPersona().getIdPersona();
			}
		}
		if (respuesta != null) {
			respuesta = respuesta.replaceFirst(",", "");
			respuesta = "(" + respuesta + ")";
		}
		return respuesta;

	}

	private String noIncluirPuestos() {
		String respuesta = null;
		if (lIngresos != null && lIngresos.size() > 0) {
			respuesta = "";
			for (EmpleadoPuesto o : lIngresos) {
				respuesta += "," + o.getPlantaCargoDet().getIdPlantaCargoDet();
			}
		}
		if (respuesta != null) {
			respuesta = respuesta.replaceFirst(",", "");
			respuesta = "(" + respuesta + ")";
		}
		return respuesta;
	}

	private void cargarPostulantes() {
		String noIncluir = noIncluirPostulantes();
		String elWhere = "";
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT  ");
		SQL.append("     evalrefere0_.* ");
		SQL.append(" FROM ");
		SQL.append("     seleccion.eval_referencial_postulante evalrefere0_, ");
		SQL.append("     seleccion.postulacion postulacio1_, ");
		SQL.append("     seleccion.persona_postulante personapos3_ ");
		SQL.append(" WHERE ");
		SQL.append("     evalrefere0_.id_postulacion=postulacio1_.id_postulacion ");
		SQL.append(" AND postulacio1_.id_persona_postulante=personapos3_.id_persona_postulante ");
		SQL.append(" AND postulacio1_.id_concurso_puesto_agr= :idGrupo");
		SQL.append(" AND evalrefere0_.activo=true ");
		SQL.append(" AND evalrefere0_.seleccionado=true ");
		SQL.append(" AND evalrefere0_.lista_corta=true ");
		SQL.append(" AND (evalrefere0_.excluido=false or evalrefere0_.excluido is null)");
		SQL.append(" AND evalrefere0_.incluido=true");
		SQL.append(" AND evalrefere0_.id_excepcion= :idExcepcion");

		// Lo nuevo
		SQL.append(" AND personapos3_.id_persona NOT IN ");
		SQL.append("     ( ");
		SQL.append("         SELECT ");
		SQL.append("             empleado_puesto.id_persona  ");
		SQL.append("         FROM ");
		SQL.append("             general.empleado_puesto ");
		SQL.append("         WHERE ");
		SQL.append("             empleado_puesto.id_planta_cargo_det  IN ");
		SQL.append("             ( ");
		SQL.append("                 SELECT ");
		SQL.append("                     concursopu0_.id_planta_cargo_det AS id10_125_ ");
		SQL.append("                 FROM ");
		SQL.append("                     seleccion.concurso_puesto_det concursopu0_, ");
		SQL.append("                     planificacion.planta_cargo_det plantacarg1_ ");
		SQL.append("                 WHERE ");
		SQL.append("                     concursopu0_.id_planta_cargo_det=plantacarg1_.id_planta_cargo_det ");
		SQL.append("                 AND concursopu0_.id_concurso_puesto_agr= :idGrupo");

		SQL.append("                 AND concursopu0_.activo=true ");
		SQL.append("                 AND plantacarg1_.id_estado_cab= :idEstadoCab");
		SQL.append("                ");
		SQL.append("             ) ");
		SQL.append("     ) ");
		if (noIncluir != null) {
			elWhere = " AND personapos3_.id_persona  not in " + noIncluir;
		}
		SQL.append(elWhere);
		Query q = em.createNativeQuery(SQL.toString(), EvalReferencialPostulante.class);
		q.setParameter("idExcepcion", idExcepcion);
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		q.setParameter("idEstadoCab", estadoOcupado.getIdEstadoCab());
		lPostulantes = q.getResultList();
	}

	/**
	 * Trae los puestos ocupados o no si es que asi se lo indica la variable ocupados
	 * 
	 * @param ocupados
	 */
	private void cargarPuestos(Boolean ocupados) {
		String noIncluir = noIncluirPuestos();
		String elWhere = "";
		String sql = null;
		if (ocupados) {
			sql =
				"select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet"
					+ " where concursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo"
					+ "  and concursoPuestoDet.excepcion.idExcepcion = :idExcepcion "
					+ " and concursoPuestoDet.activo = true and  concursoPuestoDet.plantaCargoDet.estadoCab.idEstadoCab = :idEstadoCab ";
		} else {
			sql =
				"select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet"
					+ " where concursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo "
					+ "  and concursoPuestoDet.excepcion.idExcepcion = :idExcepcion "
					+ " and concursoPuestoDet.activo = true and  concursoPuestoDet.plantaCargoDet.estadoCab.idEstadoCab != :idEstadoCab";

		}

		if (noIncluir != null) {
			elWhere = " AND plantaCargoDet.idPlantaCargoDet not in " + noIncluir;
		}
		sql = sql + elWhere;
		Query q = em.createQuery(sql);
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		q.setParameter("idEstadoCab", estadoOcupado.getIdEstadoCab());
		q.setParameter("idExcepcion", idExcepcion);
		q.getResultList();
		lPuestos = q.getResultList();
	}

	public List<EvalReferencialPostulante> getlPostulantes() {
		return lPostulantes;
	}

	public void setlPostulantes(List<EvalReferencialPostulante> lPostulantes) {
		this.lPostulantes = lPostulantes;
	}

	public List<ConcursoPuestoDet> getlPuestos() {
		return lPuestos;
	}

	public void setlPuestos(List<ConcursoPuestoDet> lPuestos) {
		this.lPuestos = lPuestos;
	}

	public List<EmpleadoPuesto> getlIngresos() {
		return lIngresos;
	}

	public void setlIngresos(List<EmpleadoPuesto> lIngresos) {
		this.lIngresos = lIngresos;
	}

	public List<SelectItem> getTipoNombramientoSelecItem() {
		return tipoNombramientoSelecItem;
	}

	public void setTipoNombramientoSelecItem(List<SelectItem> tipoNombramientoSelecItem) {
		this.tipoNombramientoSelecItem = tipoNombramientoSelecItem;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Date getFechaFirmaContrato() {
		return fechaFirmaContrato;
	}

	public void setFechaFirmaContrato(Date fechaFirmaContrato) {
		this.fechaFirmaContrato = fechaFirmaContrato;
	}

	public Date getFechaFinContrato() {
		return fechaFinContrato;
	}

	public void setFechaFinContrato(Date fechaFinContrato) {
		this.fechaFinContrato = fechaFinContrato;
	}

	public Integer getNroContrato() {
		return nroContrato;
	}

	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}

	public Long getIdTipoNombramiento() {
		return idTipoNombramiento;
	}

	public void setIdTipoNombramiento(Long idTipoNombramiento) {
		this.idTipoNombramiento = idTipoNombramiento;
	}

	public EvalReferencialPostulante getSelePostulante() {
		return selePostulante;
	}

	public void setSelePostulante(EvalReferencialPostulante selePostulante) {
		this.selePostulante = selePostulante;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDireFisicaAdDocu() {
		return direFisicaAdDocu;
	}

	public void setDireFisicaAdDocu(String direFisicaAdDocu) {
		this.direFisicaAdDocu = direFisicaAdDocu;
	}

	public String getDireFisicaAdContrato() {
		return direFisicaAdContrato;
	}

	public void setDireFisicaAdContrato(String direFisicaAdContrato) {
		this.direFisicaAdContrato = direFisicaAdContrato;
	}

	public Boolean getMostrarContratado() {
		return mostrarContratado;
	}

	public void setMostrarContratado(Boolean mostrarContratado) {
		this.mostrarContratado = mostrarContratado;
	}

	public SimpleSelection getSelePuesto2() {
		return selePuesto2;
	}

	public void setSelePuesto2(SimpleSelection selePuesto2) {
		this.selePuesto2 = selePuesto2;
	}

	public SimpleSelection getSelePostulante2() {
		return selePostulante2;
	}

	public void setSelePostulante2(SimpleSelection selePostulante2) {
		this.selePostulante2 = selePostulante2;
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

	public String getObsEmpleadoPuesto() {
		return obsEmpleadoPuesto;
	}

	public void setObsEmpleadoPuesto(String obsEmpleadoPuesto) {
		this.obsEmpleadoPuesto = obsEmpleadoPuesto;
	}

	public String getfNameContrato() {
		return fNameContrato;
	}

	public void setfNameContrato(String fNameContrato) {
		this.fNameContrato = fNameContrato;
	}

	public byte[] getuFileContrato() {
		return uFileContrato;
	}

	public void setuFileContrato(byte[] uFileContrato) {
		this.uFileContrato = uFileContrato;
	}

	public String getcTypeContrato() {
		return cTypeContrato;
	}

	public void setcTypeContrato(String cTypeContrato) {
		this.cTypeContrato = cTypeContrato;
	}

}
