package py.com.excelsis.sicca.seleccion.session.form;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.beans.metadata.api.annotations.Search;
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
import org.jboss.seam.web.Session;
import org.richfaces.model.UploadItem;
import org.richfaces.model.selection.SimpleSelection;

import py.com.excelsis.sicca.desvinculacion.session.form.DTO547;
import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
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
import py.com.excelsis.sicca.entity.GrupoConceptoPago;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.ListaElegiblesCab;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PersonaPostulante;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.Postulacion;
import py.com.excelsis.sicca.entity.PromocionConcursoAgr;
import py.com.excelsis.sicca.entity.PromocionSalarial;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;
import enums.ActividadEnum;

@Scope(ScopeType.CONVERSATION)
@Name("gestionarIngresosConcursos")
public class GestionarIngresosConcursos {
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

	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	
	List<EvalReferencialPostulante> lPostulantes;
	List<ConcursoPuestoDet> lPuestos;
	List<PromocionConcursoAgr> lPromocionSalarial;
	List<PromocionConcursoAgr> lPromocionSalarialTemp;
	List<EmpleadoPuesto> lIngresos;
	List<EmpleadoPuesto> lIngresosTemp;
	List<EmpleadoConceptoPago> lIngresosPromocionSalarial;
	
	
	List<DatosEspecificos> tipoIngresoList;
	

	private List<SelectItem> tipoNombramientoSelecItem;
	EvalReferencialPostulante selePostulante;
	ConcursoPuestoDet selePuesto;
	PromocionConcursoAgr selePuestoPromocionSalarial;
	SimpleSelection selePuesto2;
	SimpleSelection selePostulante2;

	String observacion;
	Boolean mostrarContratado = false;

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
	EstadoCab estadoOcupado;
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
	DatosEspecificos tipoIngreso = null;
	private Boolean mostrarPanelAdjunto = false;

	Long idDEpermanente;
	Long idDEcontratado;
	Long idDEtipoRegistro;
	Long idDEtipoDocContrato;
	Integer idEstadoReservado;
	Integer idEstadoUsado;
	String obsEmpleadoPuesto;
	Long idEDtipoDocContrato;
	Integer idRefIngresado;
	Long idEstadoCabVacante;
	Long idEstadoCabLibre;
	Long idDEEspSituacion;
	String nombrePantalla = "gestionarIngresosConcursos";

	public void init() {
		validarInit();
		estadoOcupado = traerEstadoCab(ESTADO_OCUPADO);
		initIDS();
		updateNombramiento();
		if (estadoOcupado != null) {
			cargarPostulantes();
			cargarPuestos(false, "");
			direFisicaAdDocu = generarDireFisicaAdDocu();
			direFisicaAdContrato = generarDireFisicaAdContrato();
			mostrarContratado = calcContratado();
		}
		// limpiar2();
		cargarIngreso();
	}
	
	public void initPromocionSalarial() {
		validarInit();
		estadoOcupado = traerEstadoCab(ESTADO_OCUPADO);
		initIDS();
		//updateNombramiento();
		if (estadoOcupado != null) {
			cargarPostulantesPromocionSalarial();
			cargarPromocionSalarial(false, "");
			direFisicaAdDocu = generarDireFisicaAdDocu();
			direFisicaAdContrato = generarDireFisicaAdContrato();
			mostrarContratado = calcContratado();
		}
		// limpiar2();
		cargarIngresoPromocionSalarial();
	}
	
	public List<EmpleadoPuesto> getlIngresosTemp() {
		return lIngresosTemp;
	}

	public void setlIngresosTemp(List<EmpleadoPuesto> lIngresosTemp) {
		this.lIngresosTemp = lIngresosTemp;
	}

	void validarInit() {
		if (seguridadUtilFormController == null)
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String estado =

			seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "CONTRATADO")
				+ "#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "FIRMADO DECRETO PRESIDENCIAL")
				+ "#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "FIRMADO NOMBRAMIENTO")
				+ "#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PERMANENTE D12")
				+ "#"
				+ seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PERMANENTE N12");

		validarInitCore(estado);

	}

	public void validarInitCore(String estado) {
		if (seguridadUtilFormController == null)
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		/* Para que funcione con varios circuitos */
		DatosEspecificos detc =
			grupoPuestosController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc();
		if (detc.getValorAlf().equalsIgnoreCase("CPO")
			|| detc.getValorAlf().equalsIgnoreCase("CME")) {
			seguridadUtilFormController.verificarPerteneceOee(null, grupoPuestosController.getIdConcursoPuestoAgr(), estado, ActividadEnum.INGRESAR_POSTULANTE.getValor());
			if (grupoPuestosController.getConcursoPuestoAgr().getEstado().intValue() == seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PERMANENTE D12")) {
				if (grupoPuestosController.getConcursoPuestoAgr().getNombramiento() == null
					|| !grupoPuestosController.getConcursoPuestoAgr().getNombramiento())
					throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}

		else if (detc.getValorAlf().equalsIgnoreCase("CII")
			|| detc.getValorAlf().equalsIgnoreCase("CIR")) {
			seguridadUtilFormController.verificarPerteneceOeeCIO(null, grupoPuestosController.getIdConcursoPuestoAgr(), estado, ActividadEnum.INGRESAR_POSTULANTE.getValor());
			if (grupoPuestosController.getConcursoPuestoAgr().getEstado().intValue() == seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PERMANENTE D12")) {
				if (grupoPuestosController.getConcursoPuestoAgr().getNombramiento() == null
					|| !grupoPuestosController.getConcursoPuestoAgr().getNombramiento())
					throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}

		else if (detc.getValorAlf().equalsIgnoreCase("CSI")) {
			seguridadUtilFormController.verificarPerteneceOeeCSI(null, grupoPuestosController.getIdConcursoPuestoAgr(), estado, ActividadEnum.INGRESAR_POSTULANTE.getValor());
			if (grupoPuestosController.getConcursoPuestoAgr().getEstado().intValue() == seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "PERMANENTE D12")) {
				if (grupoPuestosController.getConcursoPuestoAgr().getNombramiento() == null
					|| !grupoPuestosController.getConcursoPuestoAgr().getNombramiento())
					throw new AuthorizationException(SeamResourceBundle.getBundle().getString("GENERICO_CAPACITACION_NO_VALIDA"));
			}
		}

		/****************************************/
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
			mostrarPanelAdjunto = false;
			fNameMostrar = docDecreto.getNombreFisicoDoc();
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			mostrarPanelAdjunto = true;
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
		fNameMostrar = fName;
		statusMessages.add(Severity.INFO, "Documento Adjuntado");

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
		if (docDecreto != null && !mostrarPanelAdjunto) {
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
	
	public void takeSelectionPromocionSalarial() {
		Iterator<Object> iterator = selePuesto2.getKeys();
		while (iterator.hasNext()) {
			Integer key = (Integer) iterator.next();
			selePuestoPromocionSalarial = lPromocionSalarial.get(key.intValue());
			idTipoNombramiento = null;

			//updateNombramiento();

		}
	}

	Boolean calcContratado() {
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
		
		if (!respuesta.isEmpty()) {
			respuesta = respuesta.replaceFirst(",", "");
		} else {
			respuesta = "-";
		}
		return respuesta;
	}
	
	public String traerRemuneraciones(ConcursoPuestoAgr concursoPuesoAgr) {
		int respuesta = 0;
		String sql = "select * from seleccion.grupo_concepto_pago where id_concurso_puesto_agr = " + concursoPuesoAgr.getIdConcursoPuestoAgr();
		List <GrupoConceptoPago> listaGrupoConceptoPago  = em.createNativeQuery(sql,GrupoConceptoPago.class).getResultList();
		if(listaGrupoConceptoPago.size()> 0)
			respuesta += listaGrupoConceptoPago.get(0).getMonto();
		return ""+respuesta;
	}
	
	
	public String traerRemuneracionesPromocionSalarial(ConcursoPuestoAgr concursoPuesoAgr) {
		int respuesta = 0;
		String sql = "select * from seleccion.promocion_salarial where id_concurso_puesto_agr = " + concursoPuesoAgr.getIdConcursoPuestoAgr();
		
		List <PromocionSalarial> listaPromocionSalarial  = em.createNativeQuery(sql,PromocionSalarial.class).getResultList();
		if(listaPromocionSalarial.size()> 0)
			respuesta += listaPromocionSalarial.get(0).getMonto();
		return ""+respuesta;
	}
	
	public String traerCategoria(ConcursoPuestoAgr concursoPuesoAgr) {
		String respuesta = "";
		String sql = "select * from seleccion.grupo_concepto_pago where id_concurso_puesto_agr = " + concursoPuesoAgr.getIdConcursoPuestoAgr();
		List <GrupoConceptoPago> listaGrupoConceptoPago  = em.createNativeQuery(sql,GrupoConceptoPago.class).getResultList();
		if(listaGrupoConceptoPago.size()> 0)
			respuesta = listaGrupoConceptoPago.get(0).getCategoria();
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
	
	private Boolean precondAgregarIngresoPromocionSalarial() {
		if (selePostulante == null || selePuestoPromocionSalarial == null || fechaIngreso == null) {
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

	void cargarIngreso() {
		Query q =
			em.createQuery("select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto "
				+ " where EmpleadoPuesto.concursoPuestoAgr.idConcursoPuestoAgr = :idGrupo ");
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		lIngresos = q.getResultList();
	}
	
	void cargarIngresoPromocionSalarial() {
		String sql = "select * from general.empleado_concepto_pago "
				+ " where id_empleado_puesto in ("
				+ "		select id_empleado_puesto from seleccion.promocion_concurso_agr "
				+ "		where id_empleado_puesto is not null "
				+ "		and id_concurso_puesto_agr ="
				+ grupoPuestosController.getIdConcursoPuestoAgr() + ")";
		
		lIngresosPromocionSalarial = em.createNativeQuery(sql,EmpleadoConceptoPago.class).getResultList();
	}
	
	public void agregarIngreso(EmpleadoPuesto empleadoPuesto) {
		// Cambiar el estado del Puesto
		em.merge(empleadoPuesto.getPlantaCargoDet());
		em.persist(empleadoPuesto);
		 //Incidencia 0001604 
		empleadoPuesto.getPlantaCargoDet().setEstadoDet(new EstadoDet());
		empleadoPuesto.getPlantaCargoDet().getEstadoDet().setIdEstadoDet(idEDtipoDocContrato);

		//*******************//*
		empleadoPuesto.setPlantaCargoDet(cambiarEstadoPlanta(empleadoPuesto));
		insertarHistorico(empleadoPuesto.getPlantaCargoDet());
	}
	
	public EmpleadoPuesto agregarIngreso() {
		if (lIngresos == null) {
			lIngresos = new ArrayList<EmpleadoPuesto>();
		}
		if (precondAgregarIngreso()) {
			EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();

			empleadoPuesto.setActivo(true);
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

			if (idDEEspSituacion != null) {
				empleadoPuesto.setDatosEspecificosByIdDatosEspSituacion(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspSituacion().setIdDatosEspecificos(idDEEspSituacion);
			}

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
	

	void initIDS() {
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
		idEstadoCabVacante =
			seleccionUtilFormController.buscarEstadoCab("VACANTE").getIdEstadoCab();
		idEstadoCabLibre = seleccionUtilFormController.buscarEstadoCab("LIBRE").getIdEstadoCab();
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
	
	private void limpiarPromocionSalarial() {
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

	void limpiar2() {
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
		observacion = null;
		fechaFinContrato = null;
		fechaFirmaContrato = null;
		fechaIngreso = null;
		fechaDoc = null;
		fileActoAdmin= null;
		fNameMostrar=null;
	}

	public Boolean precondSgteTarea() {
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
	
	public Boolean precondSgteTareaPromocionSalarial() {
		if (lIngresosPromocionSalarial == null || lIngresosPromocionSalarial.size() == 0) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("CU420_nohayDatos")
				+ " de Asignaciones");
			return false;
		}
		if ((lPostulantes.size() - lPromocionSalarial.size()) > 0) {
			statusMessages.add(Severity.ERROR, "Debe gestionarse exclusión de postulantes y el paso de los mismos a Lista de Elegibles");
			return false;
		} else if ((lPostulantes.size() - lPromocionSalarial.size()) < 0) {
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

	void inactivarYcopiarDets() {
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
				entity2.setMonto(p.getMonto());
				entity2.setObjCodigo(p.getObjCodigo());
				entity2.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(entity2);
				p.setEstado(idEstadoUsado);
				p = em.merge(p);
			}
		}
	}

	EstadoCab traerEstadoCab(String descripcion) {
		Query q =
			em.createQuery("select EstadoCab from EstadoCab EstadoCab"
				+ " where EstadoCab.descripcion = :desc " + " and EstadoCab.activo = true");
		q.setParameter("desc", descripcion);
		List<EstadoCab> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0);
		}
		return null;

	}

	EstadoCab traerEstadoCab() {
		Query q =
			em.createQuery("select EstadoCab from EstadoCab EstadoCab"
				+ " where EstadoCab.descripcion ='" + ESTADO_OCUPADO
				+ "' and EstadoCab.activo = true");

		List<EstadoCab> lista = q.getResultList();
		if (lista.size() == 1) {
			return lista.get(0);
		}
		return null;

	}

	void insertarHistorico(PlantaCargoDet elPuesto) {
		HistoricosEstado historico = new HistoricosEstado();
		historico.setEstadoCab(estadoOcupado);
		historico.setFechaMod(new Date());
		historico.setPlantaCargoDet(elPuesto);
		historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
		em.persist(historico);
	}

	void insertarHistorico(PlantaCargoDet elPuesto, EstadoCab estadoCab) {
		HistoricosEstado historico = new HistoricosEstado();
		historico.setEstadoCab(estadoCab);
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

	private Boolean precondSave(EmpleadoPuesto empleadoPuesto) {
		// Testar....

		ConfiguracionUoCab oee =
			em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Persona persona = empleadoPuesto.getPersona();
		PlantaCargoDet puesto = empleadoPuesto.getPlantaCargoDet();
		String grupoValidacion = null;
		tipoIngreso =
			grupoPuestosController.getConcursoPuestoAgr().getConcurso().getDatosEspecificosTipoConc();
		if (tipoIngreso.getDescripcion().equalsIgnoreCase("CONCURSO PUBLICO DE OPOSICION")) {
			tipoIngreso =
				seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR CONCURSO PUBLICO DE OPOSICION");
			if (traerModalidad(puesto).equalsIgnoreCase("PERMANENTE")) {
				grupoValidacion = "ICPOPER";
			} else {
				grupoValidacion = "ICPOCO";
			}
		} else if (tipoIngreso.getDescripcion().equalsIgnoreCase("CONCURSO DE MERITOS")) {
			tipoIngreso =
				seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR CONCURSO DE MERITOS");
			grupoValidacion = "ICMER";
		} else if (tipoIngreso.getDescripcion().equalsIgnoreCase("CONCURSO SIMPLIFICADO")) {
			tipoIngreso =
				seleccionUtilFormController.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD", "INGRESO POR CONCURSO SIMPLIFICADO");
			grupoValidacion = "ICSIMP";
		}

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
	
	public void crearLegajo(Persona persona) {
		Query q =
			em.createQuery("select Legajos from Legajos Legajos where legajos.persona.idPersona = :idPersona ");
		q.setParameter("idPersona", persona.getIdPersona());
		List<Legajos> lista = q.getResultList();
		if (lista.size() == 0) {
			Legajos legajo = new Legajos();
			legajo.setPersona(persona);
			legajo.setFechaIngreso(fechaIngreso);
			legajo.setDatosEspecificosTipoIngreso(new DatosEspecificos());
			legajo.getDatosEspecificosTipoIngreso().setIdDatosEspecificos(tipoIngreso.getIdDatosEspecificos());
			em.persist(legajo);
		}
	}

	public void crearLegajo(EmpleadoPuesto empleadoPuesto,Integer index) {
		Query q =
			em.createQuery("select Legajos from Legajos Legajos where legajos.persona.idPersona = :idPersona ");
		q.setParameter("idPersona", empleadoPuesto.getPersona().getIdPersona());
		List<Legajos> lista = q.getResultList();
		if (lista.size() == 0) {
			Legajos legajo = new Legajos();
			legajo.setPersona(empleadoPuesto.getPersona());
			legajo.setFechaIngreso(empleadoPuesto.getFechaInicio());
			legajo.setDatosEspecificosTipoIngreso(new DatosEspecificos());
			legajo.getDatosEspecificosTipoIngreso().setIdDatosEspecificos(empleadoPuesto.getDatosEspecificosByIdDatosEspTipoIngresoMovilidad().getIdDatosEspecificos());
			em.persist(legajo);
		}
	}

	/*public String save() {
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
	}*/
	
	public String guardar() {
		
		try {
			for(EmpleadoPuesto empleadoPuesto:lIngresosTemp){
				agregarIngreso(empleadoPuesto);
				inactivarYcopiarDets();
				updateUnicoEmpleadoPuesto();
				adjuntarDocumento2(empleadoPuesto);
				adjuntarContrato(empleadoPuesto.getIdEmpleadoPuesto());
				crearEmpleadoConceptoPago(empleadoPuesto);
				crearLegajo(empleadoPuesto,lIngresosTemp.indexOf(empleadoPuesto));
				em.flush();
			}
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));	
			init();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}
	
	public String guardarPromocionSalarial() {
		
		try {
			
			if(lPromocionSalarialTemp.size() != lPromocionSalarial.size()){
				statusMessages.clear();
				statusMessages.add(Severity.INFO, "Existen Promociones No Asignadas... Verifique");
				return null;
			}
			
			
			for(PromocionConcursoAgr p : lPromocionSalarialTemp){
				
				EmpleadoPuesto ep = new EmpleadoPuesto();
				
				//SE DEBEN GUARDAR LOS REGISTROS DE lPromocionSalarialTemp con los id_empleado_puestos setteados
						
				if(p.getEmpleadoPuesto() != null && p.getEmpleadoPuesto().getIdEmpleadoPuesto() != null){
					em.merge(p);
					ep = em.find(EmpleadoPuesto.class, p.getEmpleadoPuesto().getIdEmpleadoPuesto());
					
				}else{
					statusMessages.clear();
					statusMessages.add(Severity.INFO, "No se han asignado todos los Empleados... Verifique");
					return null;
				}
				
				
				//SE DEBEN INACTIVAR LOS REGISTROS Anteriores DE EmpledoConceptoPago para el EmpleadoPuesto que gano
				String sqlEmpleadoConceptoPago = "select * from general.empleado_concepto_pago "
						+ " where activo = true"
						+ " and actual = true "
						+ " and fecha_fin is null "
						+ " and id_empleado_puesto = "+ ep.getIdEmpleadoPuesto();
				
				List <EmpleadoConceptoPago> ListaEcp = em.createNativeQuery(sqlEmpleadoConceptoPago, EmpleadoConceptoPago.class).getResultList(); 
				
				for (EmpleadoConceptoPago e : ListaEcp){
					e.setActual(false);
					e.setFechaFin(new Date());
					em.merge(e);
				}
				
				//SE DEBEN GENERAR LOS NUEVOS REGISTROS PARA lIngresosPromocionSalarial
				EmpleadoConceptoPago ecp = new EmpleadoConceptoPago();
				ecp.setActivo(true);
				ecp.setActual(true);
				ecp.setFechaInicio(fechaIngreso);
				ecp.setCategoria(p.getPromocionSalarial().getCategoria());
				ecp.setObjCodigo(p.getPromocionSalarial().getObjCodigo().intValue());
				ecp.setMonto(p.getPromocionSalarial().getMonto().intValue());
				ecp.setEmpleadoPuesto(ep);
				ecp.setFechaAlta(new Date());
				ecp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
				ecp.setAnho(Integer.parseInt(sdf.format(new Date())));
				
				em.merge(ecp);
			}
				
			em.flush();
			statusMessages.clear();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle().getString("GENERICO_MSG"));	
			initPromocionSalarial();
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
		return null;
	}
	
	private void crearEmpleadoConceptoPago(EmpleadoPuesto empleadoPuesto) {
		EmpleadoConceptoPago ecp = new EmpleadoConceptoPago();
		ecp.setEmpleadoPuesto(empleadoPuesto);
		
		String sql = "select * from seleccion.grupo_concepto_pago where id_concurso_puesto_agr = " +  grupoPuestosController.getIdConcursoPuestoAgr();
		List <GrupoConceptoPago> listaGrupoConceptoPago  = em.createNativeQuery(sql,GrupoConceptoPago.class).getResultList();
		if(listaGrupoConceptoPago.size()> 0){
			GrupoConceptoPago grupoConceptoPago = listaGrupoConceptoPago.get(0);
			
			ecp.setCategoria(grupoConceptoPago.getCategoria());
			ecp.setObjCodigo(grupoConceptoPago.getObjCodigo());
			ecp.setMonto(grupoConceptoPago.getMonto());
			ecp.setAnho((grupoConceptoPago.getAnho()));
			
		}
		
		ecp.setFechaInicio(this.fechaIngreso);
		ecp.setActivo(true);
		ecp.setActual(true);
		ecp.setUsuAlta("SYSTEM");
		ecp.setFechaAlta(new Date());
		em.persist(ecp);

	}
	
	public String getTotalPuntosFormateado(Float totalPuntos) {
		DecimalFormat myFormatter = new DecimalFormat("##.##");
		
		if (totalPuntos != null)
			return  myFormatter.format(totalPuntos);
		else
			return  "";
		
	}
	
	
	
	
	public void agregarAGrilla() {
		if (lIngresosTemp == null) {
			lIngresosTemp = new ArrayList<EmpleadoPuesto>();
		}else{
			for(EmpleadoPuesto e :lIngresosTemp){
				if(e.getPlantaCargoDet().equals(selePuesto.getPlantaCargoDet())){
					statusMessages.add(Severity.ERROR, "EL PUESTO YA HA SIDO OCUPADO");
					return;
				}
				if(e.getPersona().getIdPersona().equals(selePostulante.getPostulacion().getPersonaPostulante().getPersona().getIdPersona())){
					statusMessages.add(Severity.ERROR, "LA PERSONA YA OCUPA UN PUESTO");
					return;
				}
			}
			
		}
		if(fileActoAdmin == null){
			statusMessages.add(Severity.ERROR, "No se ha adjuntado un documento");
			return;
		}
		if(tipoIngresoList == null){
			tipoIngresoList = new  ArrayList<DatosEspecificos>();
		}
		
		//verifica si los campos son correctos
		if (precondAgregarIngreso()) {
			EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
			empleadoPuesto.setActivo(true);
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
			
			empleadoPuesto.setActual(true);
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoRegistro(new DatosEspecificos());
			empleadoPuesto.getDatosEspecificosByIdDatosEspTipoRegistro().setIdDatosEspecificos(idDEtipoRegistro);
			empleadoPuesto.setPin(generarPin());
			empleadoPuesto.setFechaInicio(fechaIngreso);
			empleadoPuesto.setTipoNombramiento(em.find(TipoNombramiento.class, idTipoNombramiento));
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setPlantaCargoDet(selePuesto.getPlantaCargoDet());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());

			if (idDEEspSituacion != null) {
				empleadoPuesto.setDatosEspecificosByIdDatosEspSituacion(new DatosEspecificos());
				empleadoPuesto.getDatosEspecificosByIdDatosEspSituacion().setIdDatosEspecificos(idDEEspSituacion);
			}
			empleadoPuesto.setPlantaCargoDet(selePuesto.getPlantaCargoDet());
			empleadoPuesto.getPlantaCargoDet().setEstadoCab(estadoOcupado);
			if (!precondSave(empleadoPuesto)){
				return;
			}
			empleadoPuesto.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(tipoIngreso);
			tipoIngresoList.add(tipoIngreso);
			lIngresosTemp.add(empleadoPuesto);
			limpiar();
		}
	}
	
	
	public void agregarAGrillaPromocionSalarial() {
		
		if(!precondAgregarIngresoPromocionSalarial()){
			statusMessages.add(Severity.ERROR, "Verifique los Campos Obligatorios.");
			return;
		}
		
		if(fileActoAdmin == null){
			statusMessages.add(Severity.ERROR, "No se ha adjuntado un documento");
			return;
		}
		
		String sqlEmpleadoPuesto = "select * from general.empleado_puesto "
				+ "	where actual = true "
				+ "	and activo = true "
				+ "	and fecha_fin_contrato is null 	"
				+ "	and id_persona = "+selePostulante.getPostulacion().getPersonaPostulante().getPersona().getIdPersona();
		
		EmpleadoPuesto emp = (EmpleadoPuesto) em.createNativeQuery(sqlEmpleadoPuesto, EmpleadoPuesto.class).getResultList().get(0);
		
		
		if(lPromocionSalarialTemp == null){
			lPromocionSalarialTemp = new ArrayList <PromocionConcursoAgr>();
			
		}else{
			
			
			for(PromocionConcursoAgr e :lPromocionSalarialTemp){
				if(e.getPromocionSalarial().equals(selePuestoPromocionSalarial.getPromocionSalarial())){
					statusMessages.add(Severity.ERROR, "LA PROMOCION YA HA SIDO ASIGNADA");
					return;
				}
				if(e.getEmpleadoPuesto().getPersona().getIdPersona().equals(selePostulante.getPostulacion().getPersonaPostulante().getPersona().getIdPersona())){
					statusMessages.add(Severity.ERROR, "LA PERSONA YA TIENE ASIGNADA UNA PROMOCION");
					return;
				}
			}
			
			
		}
			
		PromocionConcursoAgr pca = selePuestoPromocionSalarial;
		pca.setEmpleadoPuesto(emp);
		lPromocionSalarialTemp.add(pca);
		
		
	}

	public void eliminar(EmpleadoPuesto empleado){

		if (lIngresosTemp.contains(empleado)) {
			Integer index =lIngresosTemp.indexOf(empleado);
			tipoIngresoList.remove(index);
			lIngresosTemp.remove(empleado);
			
			cargarPostulantes();
			cargarPuestos(false, "");
		}
		
	}
	
	public void eliminarPromocionSalarial(PromocionConcursoAgr pca){

		if (lPromocionSalarialTemp.contains(pca)) {
			Integer index =lPromocionSalarialTemp.indexOf(pca);
			
			lPromocionSalarialTemp.remove(pca);
			
			cargarPostulantesPromocionSalarial();
			cargarPromocionSalarial(false, "");
		}
		
	}
	
	void adjuntarContrato(Long idEmpleadoPuesto) {
		try {
			Long idDoc = null;
			if (fileContrato != null) {
				idDoc =
					admDocAdjuntoFormController.guardarDoc(fileContrato, direFisicaAdContrato, "gestionarIngresosConcursos", idDEtipoDocContrato, "EmpleadoPuesto", null);
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
	
	void adjuntarDocumento2(Long idEmpleadoPuesto) {
		try {
			Long idDoc = null;
			if (docDecreto == null) {
				idDoc =
					admDocAdjuntoFormController.guardarDoc(fileActoAdmin, direFisicaAdDocu, nombrePantalla, idTipoDoc, "EmpleadoPuesto", null);
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

	void adjuntarDocumento2(EmpleadoPuesto empleadoPuesto) {
		try {
			Long idDoc = null;
			if (docDecreto == null) {
				idDoc =
					admDocAdjuntoFormController.guardarDoc(fileActoAdmin, direFisicaAdDocu, nombrePantalla, idTipoDoc, "EmpleadoPuesto", null);
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
				refAdj.setPersona(empleadoPuesto.getPersona());
				refAdj.setDocumentos(new Documentos());
				refAdj.getDocumentos().setIdDocumento(idDoc);
				refAdj.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
				em.persist(refAdj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
	}
	
	
	void adjuntarDocumentoPromocionSalarial(EmpleadoPuesto empleadoPuesto) {
		try {
			Long idDoc = null;
			if (docDecreto == null) {
				idDoc =
					admDocAdjuntoFormController.guardarDoc(fileActoAdmin, direFisicaAdDocu, nombrePantalla, idTipoDoc, "EmpleadoPuesto", null);
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
				refAdj.setPersona(empleadoPuesto.getPersona());
				refAdj.setDocumentos(new Documentos());
				refAdj.getDocumentos().setIdDocumento(idDoc);
				refAdj.setIdRegistroTabla(empleadoPuesto.getIdEmpleadoPuesto());
				em.persist(refAdj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("GENERICO_NO_MSG"));
		}
	}

	void updateUnicoEmpleadoPuesto() {

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

	private void guardarObservacionActividad() {
		if (observacion != null && !observacion.trim().isEmpty()) {
			Observacion obs = new Observacion();
			obs.setObservacion(observacion);
			obs.setIdTaskInstance(grupoPuestosController.getConcursoPuestoAgr().getIdProcessInstance());
			obs.setFechaAlta(new Date());
			obs.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			obs.setConcurso(this.grupoPuestosController.getConcurso());
			em.persist(obs);
		}
	}
	
	public boolean tareaCerrada(){	
		Referencias ref = new Referencias();
		ref = referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "INGRESADO");
		ConcursoPuestoAgr aux = em.find(ConcursoPuestoAgr.class, this.grupoPuestosController.getConcursoPuestoAgr().getIdConcursoPuestoAgr());
		em.refresh(aux);
		
		System.out.println("Tarea: ");
		System.out.println(ref.getValorNum());
		System.out.println(aux.getEstado());
		
		return (aux.getEstado().equals(ref.getValorNum()))? true : false;
		
	}
	public String nextTask() {
		// Controles particulares
		if (!precondSgteTarea()) {
			return "FAIL";
		}
		
		if(tareaCerrada())
		{
			statusMessages.add(Severity.INFO,
					"El Grupo ya se encuentra en la Siguiente Actividad. Verifique.");
			return "FAIL";
		}
		
		
		try {
			grupoPuestosController.getConcursoPuestoAgr().setEstado(idRefIngresado);
			grupoPuestosController.setConcursoPuestoAgr(em.merge(grupoPuestosController.getConcursoPuestoAgr()));
			guardarObservacionActividad();
			// Se pasa a la siguiente tarea

			jbpmUtilFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
			Concurso concurso = grupoPuestosController.getConcursoPuestoAgr().getConcurso();
			if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CII")
				|| concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CIR")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.CIO_INGRESAR_POSTULANTE);
				jbpmUtilFormController.setActividadSiguiente(null);
			} else if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CPO")
				|| concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CME")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.INGRESAR_POSTULANTE);
				jbpmUtilFormController.setActividadSiguiente(null);
			} else if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CSI")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_INGRESAR_POSTULANTE);
				jbpmUtilFormController.setActividadSiguiente(null);
			}
			jbpmUtilFormController.setTransition("next");
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			} else {
				return "FAIL";
			}
			return "nextTask";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}
	
	
	
	public String nextTaskPromocionSalrial() {
		// Controles particulares
		if (!precondSgteTareaPromocionSalarial()) {
			return "FAIL";
		}
		
		if(tareaCerrada())
		{
			statusMessages.add(Severity.INFO,
					"El Grupo ya se encuentra en la Siguiente Actividad. Verifique.");
			return "FAIL";
		}
		
		
		try {
			grupoPuestosController.getConcursoPuestoAgr().setEstado(idRefIngresado);
			grupoPuestosController.setConcursoPuestoAgr(em.merge(grupoPuestosController.getConcursoPuestoAgr()));
			guardarObservacionActividad();
			// Se pasa a la siguiente tarea CIPS

			jbpmUtilFormController.setConcursoPuestoAgr(grupoPuestosController.getConcursoPuestoAgr());
			Concurso concurso = grupoPuestosController.getConcursoPuestoAgr().getConcurso();
			if (concurso.getDatosEspecificosTipoConc().getValorAlf().equalsIgnoreCase("CIPS")) {
				jbpmUtilFormController.setActividadActual(ActividadEnum.ASIGNAR_PROMOCION_SALARIAL);
				jbpmUtilFormController.setActividadSiguiente(null);
			}
			jbpmUtilFormController.setTransition("next");
			if (jbpmUtilFormController.nextTask()) {
				em.flush();
			} else {
				return "FAIL";
			}
			return "nextTask";
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}

	}

	public void eliminarIngreso(Integer elIndice) {
		if (lIngresos.size() > elIndice) {
			lIngresos.remove(elIndice.intValue());
			cargarPostulantes();
			cargarPuestos(false, "");
		}
	}
	
	public String noIncluirPostulantesPromocionSalarial() {
		String respuesta = null;
		if (lIngresosPromocionSalarial != null && lIngresosPromocionSalarial.size() > 0) {
			respuesta = "";
			for (EmpleadoConceptoPago o : lIngresosPromocionSalarial) {
				respuesta += "," + o.getEmpleadoPuesto().getPersona().getIdPersona();
			}
		}
		if (respuesta != null) {
			respuesta = respuesta.replaceFirst(",", "");
			respuesta = "(" + respuesta + ")";
		}
		return respuesta;

	}

	public String noIncluirPostulantes() {
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

	
	private String noIncluirPuestosPromocionSalarial() {
		String respuesta = null;
		
		String sql = "select * from seleccion.promocion_concurso_agr "
				+ " where id_empleado_puesto is not null "
				+ " and id_concurso_puesto_agr =  " + this.grupoPuestosController.getIdConcursoPuestoAgr();
		
		List <PromocionConcursoAgr> lista = em.createNativeQuery(sql,PromocionConcursoAgr.class).getResultList();
		
		if (lista != null && lista.size() > 0) {
			respuesta = "";
			
			for (PromocionConcursoAgr o : lista) {
					
				respuesta += "," + o.getPromocionSalarial().getIdPromocionSalarial();
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

	public void cargarPostulantes(String cWhere) {
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
		SQL.append(" AND postulacio1_.id_concurso_puesto_agr= :idGrupo ");
		SQL.append(cWhere);
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
		SQL.append("                 AND plantacarg1_.id_estado_cab= :idEstadoCab ");
		SQL.append("                ");
		SQL.append("             ) ");
		SQL.append("     ) ");
		if (noIncluir != null) {
			elWhere = " AND personapos3_.id_persona  not in " + noIncluir;
		}
		SQL.append(elWhere);
		Query q = em.createNativeQuery(SQL.toString(), EvalReferencialPostulante.class);
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		q.setParameter("idEstadoCab", grupoPuestosController.getIdConcursoPuestoAgr());
		lPostulantes = q.getResultList();
	}

	void cargarPostulantes() {
		estadoOcupado = traerEstadoCab(ESTADO_OCUPADO);
		String noIncluir = noIncluirPostulantes();
		String elWhere = "";
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT  ");
		SQL.append("     evalrefere0_.* ");
		SQL.append(" FROM ");
		SQL.append("     seleccion.eval_referencial_postulante evalrefere0_, ");
		SQL.append("     seleccion.postulacion postulacio1_, ");
		SQL.append("     seleccion.persona_postulante personapos3_ ,");
		SQL.append("     seleccion.eval_documental_cab eval_documental ");
		SQL.append(" WHERE ");
		SQL.append("     evalrefere0_.id_postulacion=postulacio1_.id_postulacion ");
		SQL.append(" AND postulacio1_.id_persona_postulante=personapos3_.id_persona_postulante ");
		SQL.append(" AND eval_documental.id_postulacion = postulacio1_.id_postulacion and eval_documental.tipo like 'EVALUACION ADJUDICADOS' and eval_documental.aprobado = true ");
		SQL.append(" AND postulacio1_.id_concurso_puesto_agr= "	+ grupoPuestosController.getIdConcursoPuestoAgr());
		SQL.append(" AND evalrefere0_.activo=true ");
		//SQL.append(" AND evalrefere0_.seleccionado=true ");
		//SQL.append(" AND evalrefere0_.lista_corta=true ");
		SQL.append(" AND (evalrefere0_.excluido=false or evalrefere0_.excluido is null) ");

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
		SQL.append("                 AND concursopu0_.id_concurso_puesto_agr= "
			+ grupoPuestosController.getIdConcursoPuestoAgr());
		SQL.append("                 AND concursopu0_.activo=true ");
		SQL.append("                 AND plantacarg1_.id_estado_cab= "
			+ estadoOcupado.getIdEstadoCab());
		SQL.append("                ");
		SQL.append("             ) ");
		SQL.append("     ) ");
		if (noIncluir != null) {
			elWhere = " AND personapos3_.id_persona  not in " + noIncluir;
		}
		SQL.append(elWhere);
		Query q = em.createNativeQuery(SQL.toString(), EvalReferencialPostulante.class);
		lPostulantes = q.getResultList();
	}
	
	
	void cargarPostulantesPromocionSalarial() {
		estadoOcupado = traerEstadoCab(ESTADO_OCUPADO);
		String noIncluir = noIncluirPostulantesPromocionSalarial();
		String elWhere = "";
		StringBuffer SQL = new StringBuffer();
		SQL.append(" SELECT  ");
		SQL.append("     evalrefere0_.* ");
		SQL.append(" FROM ");
		SQL.append("     seleccion.eval_referencial_postulante evalrefere0_, ");
		SQL.append("     seleccion.postulacion postulacio1_, ");
		SQL.append("     seleccion.persona_postulante personapos3_ ");
//		SQL.append("     ,seleccion.eval_documental_cab eval_documental ");
		SQL.append(" WHERE ");
		SQL.append("     evalrefere0_.id_postulacion=postulacio1_.id_postulacion ");
		SQL.append(" AND postulacio1_.id_persona_postulante=personapos3_.id_persona_postulante ");
//		SQL.append(" AND eval_documental.id_postulacion = postulacio1_.id_postulacion and eval_documental.tipo like 'EVALUACION ADJUDICADOS' and eval_documental.aprobado = true ");
		SQL.append(" AND postulacio1_.id_concurso_puesto_agr= "	+ grupoPuestosController.getIdConcursoPuestoAgr());
		SQL.append(" AND evalrefere0_.activo=true ");
		SQL.append(" AND evalrefere0_.seleccionado_replica=true ");
		//SQL.append(" AND evalrefere0_.lista_corta=true ");
		SQL.append(" AND (evalrefere0_.excluido=false or evalrefere0_.excluido is null) ");

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
		SQL.append("                 AND concursopu0_.id_concurso_puesto_agr= "
			+ grupoPuestosController.getIdConcursoPuestoAgr());
		SQL.append("                 AND concursopu0_.activo=true ");
		SQL.append("                 AND plantacarg1_.id_estado_cab= "
			+ estadoOcupado.getIdEstadoCab());
		SQL.append("                ");
		SQL.append("             ) ");
		SQL.append("     ) ");
		if (noIncluir != null) {
			elWhere = " AND personapos3_.id_persona  not in " + noIncluir;
		}
		SQL.append(elWhere);
		Query q = em.createNativeQuery(SQL.toString(), EvalReferencialPostulante.class);
		lPostulantes = q.getResultList();
	}

	/**
	 * Trae los puestos ocupados o no si es que asi se lo indica la variable ocupados
	 * 
	 * @param ocupados
	 */
	void cargarPuestos(Boolean ocupados, String cWhere) {
		String noIncluir = noIncluirPuestos();
		String elWhereBase =
			" where concursoPuestoDet.concursoPuestoAgr.idConcursoPuestoAgr  =:idGrupo and concursoPuestoDet.activo is true ";
		String sql = null;
		sql = "select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet";
		if (ocupados) {
			elWhereBase +=
				" and concursoPuestoDet.plantaCargoDet.estadoCab.idEstadoCab = "
					+ this.estadoOcupado.getIdEstadoCab();
		} else {
			elWhereBase +=
				" and concursoPuestoDet.plantaCargoDet.estadoCab.idEstadoCab != "
					+ this.estadoOcupado.getIdEstadoCab();
		}

		if (noIncluir != null) {
			elWhereBase += " AND plantaCargoDet.idPlantaCargoDet not in " + noIncluir;
		}
		sql = sql + elWhereBase + cWhere;
		Query q = em.createQuery(sql);
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		lPuestos = q.getResultList();
	}
	
	
	void cargarPromocionSalarial(Boolean ocupados, String cWhere) {
		String noIncluir = noIncluirPuestosPromocionSalarial();
		String elWhereBase =
			" where promocionConcursoAgr.concursoPuestoAgr.idConcursoPuestoAgr  =:idGrupo and promocionConcursoAgr.activo is true ";
		String sql = null;
		sql = "select promocionConcursoAgr from PromocionConcursoAgr promocionConcursoAgr";
		if (ocupados) {
			elWhereBase +=
				" and promocionConcursoAgr.promocionSalarial.estadoCab.idEstadoCab = "
					+ this.estadoOcupado.getIdEstadoCab();
		} else {
			elWhereBase +=
				" and promocionConcursoAgr.promocionSalarial.estadoCab.idEstadoCab != "
					+ this.estadoOcupado.getIdEstadoCab();
		}

		if (noIncluir != null) {
			elWhereBase += " AND promocionSalarial.idPromocionSalarial not in " + noIncluir;
		}
		sql = sql + elWhereBase + cWhere;
		Query q = em.createQuery(sql);
		q.setParameter("idGrupo", grupoPuestosController.getIdConcursoPuestoAgr());
		lPromocionSalarial = q.getResultList();
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

	public Boolean getMostrarPanelAdjunto() {
		return mostrarPanelAdjunto;
	}

	public void setMostrarPanelAdjunto(Boolean mostrarPanelAdjunto) {
		this.mostrarPanelAdjunto = mostrarPanelAdjunto;
	}

	public DatosEspecificos getTipoIngreso() {
		return tipoIngreso;
	}

	public void setTipoIngreso(DatosEspecificos tipoIngreso) {
		this.tipoIngreso = tipoIngreso;
	}

	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	public EstadoCab getEstadoOcupado() {
		return estadoOcupado;
	}

	public void setEstadoOcupado(EstadoCab estadoOcupado) {
		this.estadoOcupado = estadoOcupado;
	}

	public Long getIdDEtipoRegistro() {
		return idDEtipoRegistro;
	}

	public void setIdDEtipoRegistro(Long idDEtipoRegistro) {
		this.idDEtipoRegistro = idDEtipoRegistro;
	}

	public Long getIdDEEspSituacion() {
		return idDEEspSituacion;
	}

	public void setIdDEEspSituacion(Long idDEEspSituacion) {
		this.idDEEspSituacion = idDEEspSituacion;
	}

	public List<PromocionConcursoAgr> getlPromocionSalarial() {
		return lPromocionSalarial;
	}

	public void setlPromocionSalarial(List<PromocionConcursoAgr> lPromocionSalarial) {
		this.lPromocionSalarial = lPromocionSalarial;
	}

	public List<EmpleadoConceptoPago> getlIngresosPromocionSalarial() {
		return lIngresosPromocionSalarial;
	}

	public void setlIngresosPromocionSalarial(
			List<EmpleadoConceptoPago> lIngresosPromocionSalarial) {
		this.lIngresosPromocionSalarial = lIngresosPromocionSalarial;
	}

	public List<PromocionConcursoAgr> getlPromocionSalarialTemp() {
		return lPromocionSalarialTemp;
	}

	public void setlPromocionSalarialTemp(
			List<PromocionConcursoAgr> lPromocionSalarialTemp) {
		this.lPromocionSalarialTemp = lPromocionSalarialTemp;
	}

	

}
