package py.com.excelsis.sicca.seleccion.session.form;

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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.model.UploadItem;

import enums.TiposDatos;

import py.com.excelsis.sicca.dto.ValidadorDTO;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.Legajos;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.DatosEspecificosHome;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.PersonaDTO;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ValidadorController;

@Scope(ScopeType.CONVERSATION)
@Name("ingresoPorLeyDeEmergenciaFC")
public class IngresoPorLeyDeEmergenciaFC {

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
	ValidadorController validadorController;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	BuscadorDocsFC buscadorDocsFC;

	private Long idPaisExp = null;
	private Long idPersona = null;
	private Long idEmpleadoPuesto;
	private Long idPlantaCargoDet;
	private Long idPais;
	private Integer monto;
	private Integer nroDocLey;
	private Integer anhoLey;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private Boolean descargarLey;
	private Boolean adjuntarLey;
	private boolean habUpdate = false;
	private boolean habPersona = false;
	private boolean habPaisExp = true;
	private boolean habBtnAddPersons = false;
	private Boolean primeraEntrada = true;
	private String docIdentidad = null;
	private String fNameLeyMostrar;
	private Date fechaIngreso;
	private Date fechaInicioContrato;
	private Date fechaFinContrato;
	private Integer nroContrato;
	private String contratoAdjunto;
	private String nombreContrato;
	private Documentos documentoCotrato;
	private boolean habDesContrato;
	private boolean habAdjContrato;
	private boolean habAdjLey;
	private PersonaDTO personaDTO;
	private Persona persona = new Persona();
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private EmpleadoPuesto empleadoPuesto;
	private Documentos docLey;
	private byte[] uLeyFile = null;
	private String cLeyType = null;
	private String fLeyName;
	private UploadItem fileLey;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
	private byte[] uploadedFileContrato = null;
	private String contentTypeContrato = null;
	private String fileNameContrato = null;
	private UploadItem fileContrato;
	private List<ConfiguracionUoDet> configuracionUoDetList = new ArrayList<ConfiguracionUoDet>();
	private List<PlantaCargoDet> plantaCargoDetList = new ArrayList<PlantaCargoDet>();
	private List<EmpleadoConceptoPago> empleadoConceptoPagos = new ArrayList<EmpleadoConceptoPago>();
	private List<Documentos> listaDocumentos = new ArrayList<Documentos>();

	public void init() throws Exception {

		if (idPersona != null) {
			if (!seguridadUtilFormController.validarInput(idPersona.toString(),
					TiposDatos.LONG.getValor(), null)) {
				return;
			}
			persona = em.find(Persona.class, idPersona);
			completarDatosPersona(persona, true);
			habBtnAddPersons = false;
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
			completarDatosPersona(persona, true);
			habBtnAddPersons = false;
		}
		idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		cargarNiveentidadOee();
		obtenerDatosView();

		fechaIngreso = empleadoPuesto.getFechaInicio();
	}

	private void obtenerDatosView() {

		persona = em.find(Persona.class, empleadoPuesto.getPersona()
				.getIdPersona());
		completarDatosPersona(persona, true);
		List<ReferenciaAdjuntos> r = em.createQuery(
				"Select r from ReferenciaAdjuntos r "
						+ " where r.idRegistroTabla=" + idEmpleadoPuesto)
				.getResultList();
		listaDocumentos = new ArrayList<Documentos>();
		for (ReferenciaAdjuntos ad : r)
			listaDocumentos.add(ad.getDocumentos());

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

	private void completarDatosPersona(Persona persona, Boolean completo) {
		idPaisExp = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
		docIdentidad = persona.getDocumentoIdentidad();
	}

	@SuppressWarnings("unchecked")
	private void cargarNiveentidadOee() {
		nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		configuracionUoDetList = em
				.createQuery(
						"Select det from ConfiguracionUoDet det "
								+ " where det.configuracionUoCab.idConfiguracionUo=:idOEE ")
				.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab())
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	private void obtenerDatos() {

		persona = em.find(Persona.class, empleadoPuesto.getPersona()
				.getIdPersona());
		completarDatosPersona(persona, true);
		List<ReferenciaAdjuntos> r = em.createQuery(
				"Select r from ReferenciaAdjuntos r "
						+ " where r.idRegistroTabla=" + idEmpleadoPuesto)
				.getResultList();
		if (!r.isEmpty()) {
			buscadorDocsFC.setDocDecreto(r.get(0).getDocumentos());
			buscadorDocsFC.cargarDatosDocumento();
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

	private void setearDatos() {
		persona = new Persona();
		idPaisExp = General.getIdParaguay();
		habPaisExp = true;
		habPersona = false;
		plantaCargoDetList = new ArrayList<PlantaCargoDet>();
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
									+ " where d.datosGenerales.descripcion like 'TIPOS DE DOCUMENTOS' and d.valorAlf like 'CON' and d.activo=true order by d.descripcion")
					.getResultList();

			return datosEspecificosLists;
		} catch (Exception ex) {
			return new Vector<DatosEspecificos>();
		}
	}

	public void buscarDocLey() {
		if (!precondLeySearch())
			return;
		docLey = searchDoc(nroDocLey, anhoLey);

		if (docLey != null) {
			fLeyName = docLey.getNombreFisicoDoc();
			fNameLeyMostrar = docLey.getNombreFisicoDoc();
			descargarLey = true;
			adjuntarLey = false;
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			fNameLeyMostrar = null;
			adjuntarLey = true;
			descargarLey = false;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");
		}
	}

	private Documentos searchDoc(Integer nro, Integer anho) {
		Query q = em
				.createQuery("select Documentos from Documentos Documentos "
						+ " where Documentos.activo is true "
						+ " and Documentos.nroDocumento = :nroDoc and Documentos.anhoDocumento = :anhio"
						+ " and Documentos.datosEspecificos.descripcion = 'LEY DE EMERGENCIA' "
						+ "and Documentos.datosEspecificos.valorAlf = 'CON'");
		q.setParameter("nroDoc", nro);
		q.setParameter("anhio", anho);

		List<Documentos> lista = q.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		return null;
	}

	private Boolean precondLeySearch() {
		if (nroDocLey == null || anhoLey == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}
		return true;
	}

	public void adjuntarLey() {
		if (!precondAdjuntarLey())
			return;

		fileLey = seleccionUtilFormController.crearUploadItem(fLeyName,
				uLeyFile.length, cLeyType, uLeyFile);
		fNameLeyMostrar = fLeyName;

		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

	}

	private Boolean precondAdjuntarLey() {
		if (!precondLeySearch())
			return false;
		if (uLeyFile == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}
		// Verificar que ya no exista
		Documentos doc = searchDoc(nroDocLey, anhoLey);
		if (doc != null) {
			statusMessages
					.add(Severity.ERROR,
							"No se puede adjuntar, ya existe un documento con los mismos parámetros");
			return false;
		}
		return true;
	}

	public void descargarLeyDocBD() throws FileNotFoundException, IOException {
		if (docLey.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(
					docLey.getIdDocumento(), usuarioLogueado.getIdUsuario());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	public void limpiarDatosPersona() {
		persona = new Persona();
		idPersona = null;
		persona = new Persona();
		docIdentidad = null;
		habBtnAddPersons = false;
	}

	public void buscarPersona() throws Exception {

		/* Validaciones */
		if (idPais == null
				|| !seguridadUtilFormController.validarInput(idPais.toString(),
						TiposDatos.LONG.getValor(), null)) {
			return;
		}
		if (docIdentidad == null
				|| !seguridadUtilFormController.validarInput(
						docIdentidad.toString(), TiposDatos.STRING.getValor(),
						null)) {
			return;
		}
		Pais pais = em.find(Pais.class, idPais);
		if (pais == null)
			return;
		/* fin validaciones */
		personaDTO = seleccionUtilFormController.buscarPersona(docIdentidad,
				pais.getDescripcion());
		if (personaDTO.getHabilitarBtn() == null) {
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
			habBtnAddPersons = false;
			persona = new Persona();
		} else if (personaDTO.getHabilitarBtn()) {
			habBtnAddPersons = true;
			persona = new Persona();
			statusMessages.add(Severity.ERROR, personaDTO.getMensaje());
		} else {
			habBtnAddPersons = false;
			completarDatosPersona(personaDTO.getPersona().getIdPersona(), false);
		}
	}

	private void completarDatosPersona(Long idPersona, Boolean completo) {
		persona = em.find(Persona.class, idPersona);
		if (completo) {
			idPais = persona.getPaisByIdPaisExpedicionDoc().getIdPais();
			docIdentidad = persona.getDocumentoIdentidad();
		}
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

	public void agregar() {
		if (seleccionUtilFormController.getCodigoObj() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Cod. Objeto Gasto");
			return;
		}
		if (seleccionUtilFormController.getCodigoObj().intValue() == 111) {
			statusMessages.add(Severity.ERROR,
					"No se permite codigo de objeto 111");
			return;
		}
		if (seleccionUtilFormController.getCodigoObj().intValue() < 140) {
			statusMessages.add(Severity.ERROR,
					"No se permite codigo de objeto menor a 140");
			return;
		}

		if (seleccionUtilFormController.getCodigoObj().intValue() > 149) {
			statusMessages.add(Severity.ERROR,
					"No se permite codigo de objeto mayor a 149");
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

	public void eliminar(int index) {
		empleadoConceptoPagos.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	public void seleccionarPuesto(Long idPuesto, Integer index) {
		ConfiguracionUoCab cab = em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtil.getIdConfigCab());
		seleccionUtilFormController.setPlantaCargoDet(em.find(
				PlantaCargoDet.class, idPuesto));
		seleccionUtilFormController.setCodigoSinarh(cab.getCodigoSinarh());
		idPlantaCargoDet = idPuesto;
		setearConPago();
		setSelectedRowPuesto(index);
	}

	public void buscarContratos() {
		try {
			if (fechaFinContrato == null || fechaInicioContrato == null
					|| nroContrato == null) {
				statusMessages.add(Severity.ERROR,
						"Debe completar los campos obligatorios");
				return;
			}
			documentoCotrato = (Documentos) em
					.createQuery(
							"Select documentos from Documentos documentos"
									+ " where date(documentos.fechaFirmaContrato)=:fechaIni "
									+ " and date(documentos.fechaFinContrato)=:fechaFin "
									+ " and documentos.configuracionUoCab.idConfiguracionUo=:idOEE")
					.setParameter("fechaIni", fechaInicioContrato)
					.setParameter("fechaFin", fechaFinContrato)
					.setParameter("idOEE", nivelEntidadOeeUtil.getIdConfigCab())
					.getSingleResult();
			habAdjContrato = false;
			habDesContrato = true;
			nombreContrato = documentoCotrato.getNombreFisicoDoc();
			statusMessages.add("Documento encontrado!");
		} catch (NoResultException e) {
			habDesContrato = false;
			habAdjContrato = true;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");

		}

	}

	public void descargarContrato() throws FileNotFoundException, IOException {
		if (documentoCotrato != null) {
			String resp = AdmDocAdjuntoFormController.abrirDocumentoFromCU(
					documentoCotrato.getIdDocumento(),
					usuarioLogueado.getIdUsuario());
			if (!resp.equalsIgnoreCase("OK")) {
				statusMessages.add(Severity.ERROR, resp);
			}
		} else if (fileContrato != null) {
			admDocAdjuntoFormController.enviarArchivoANavegador(
					fileContrato.getFileName(), fileContrato.getFile());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	public void adjuntarDocContrato() {
		if (uploadedFileContrato == null) {
			statusMessages.add(Severity.ERROR,
					"Debe seleccionar un Contrato antes");
			return;
		}
		fileContrato = seleccionUtilFormController.crearUploadItem(
				fileNameContrato, uploadedFileContrato.length,
				contentTypeContrato, uploadedFileContrato);
		nombreContrato = fileNameContrato;

		statusMessages.add(Severity.INFO, "Documento Adjuntado ");

	}

	public void descargarLey() throws FileNotFoundException, IOException {
		if (docLey != null) {
			String resp = AdmDocAdjuntoFormController.abrirDocumentoFromCU(
					docLey.getIdDocumento(), usuarioLogueado.getIdUsuario());
			if (!resp.equalsIgnoreCase("OK")) {
				statusMessages.add(Severity.ERROR, resp);
			}
		} else if (fileContrato != null) {
			admDocAdjuntoFormController.enviarArchivoANavegador(
					fileLey.getFileName(), fileLey.getFile());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	private void setearConPago() {
		seleccionUtilFormController.setearValoresObjetosGasto();
	}

	private Long getIdEstadoCabVacante() {
		EstadoCab cab = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		return cab.getIdEstadoCab();
	}

	public String toFindPersona() {
		idPersona = null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/ingresoDirecto/porLeyDeEmergencia/IngresoDirectoPorLeyEmergenciaEdit";
	}

	public String toFindPersonaView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/seleccion/ingresoDirecto/porLeyDeEmergencia/IngresoDirectoPorLeyEmergenciaEdit&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
	}

	public String toFindPersonaToView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/seleccion/ingresoDirecto/porLeyDeEmergencia/IngresoDirectoPorLeyEmergenciaView&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
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

	private boolean chkDatos() {
		if (buscadorDocsFC.getNroDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Numero de Documento");
			return false;
		}
		if (buscadorDocsFC.getFechaDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la Fecha de Documento");
			return false;
		}
		if (buscadorDocsFC.getIdTipoDoc() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Seleccionar el Tipo de Documento");
			return false;
		}
		if (buscadorDocsFC.getDocDecreto() == null
				&& buscadorDocsFC.getFileActoAdmin() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar un archivo, verifique");
			return false;
		}
		if (fechaIngreso == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar campo  Fecha de Ingreso");
			return false;
		}
		if (nroDocLey == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Número de Ley de Emergencia");
			return false;
		}
		if (anhoLey == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Año de Ley de Emergencia");
			return false;
		}
		if (fileLey == null && docLey == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Adjuntar una Ley de Emergencia, verifique");
			return false;
		}

		if (nroContrato == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Número de Contrato");
			return false;
		}
		if (fechaInicioContrato == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la fecha de Inicio del Contrato");
			return false;
		}
		if (fechaFinContrato == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar la fecha de Fin del Contrato");
			return false;
		}
		if (fileContrato == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Adjuntar un Contrato, verifique");
			return false;
		}
		if (persona.getIdPersona() == null) {
			statusMessages
					.add(Severity.ERROR,
							"No se ingresó el dato correspondiente al campo obligatorio: Persona");
			return false;
		}
		if (empleadoConceptoPagos.isEmpty()) {
			statusMessages.add(Severity.ERROR,
					"Debe agregar almenos un Objeto de Gasto y Monto");
			return false;
		}

		String grupoValidador = "IDCEMERG";

		PlantaCargoDet pDet = em.find(PlantaCargoDet.class, idPlantaCargoDet);
		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtil.getIdConfigCab());
		ValidadorDTO validadorDTOPersona = validadorController.validarCfg(
				"PERSONA", grupoValidador, persona, pDet, null, oee);
		if (!validadorDTOPersona.isValido()) {
			statusMessages.add(validadorDTOPersona.getMensaje());
			return false;
		}
		ValidadorDTO validadorDTOPuesto = validadorController.validarCfg(
				"PUESTO", grupoValidador, persona, pDet, null, oee);
		if (!validadorDTOPuesto.isValido()) {
			statusMessages.add(validadorDTOPersona.getMensaje());
			return false;
		}

		return true;
	}

	public String save() {
		try {
			if (!chkDatos())
				return null;
			PlantaCargoDet puestoSelec = em.find(PlantaCargoDet.class,
					idPlantaCargoDet);
			/**
			 * Guardan en la tabla EMPLEADO_PUESTO
			 * */
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(datosEspecificosHome
							.getDatosIngresoPorLeyEmergencia());
			empleadoPuesto.setFechaInicio(fechaIngreso);
			empleadoPuesto.setActual(true);
			empleadoPuesto.setPin(seleccionUtilFormController.generarPIN());
			empleadoPuesto.setContratado(false);
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspEstado(datosEspecificosHome
							.getDatosPermanenteEstadoEmpleado());
			empleadoPuesto
					.setDatosEspecificosByIdDatosEspTipoRegistro(datosEspecificosHome
							.getTipoRegistroIngreso());
			empleadoPuesto.setFechaAlta(new Date());
			empleadoPuesto.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			empleadoPuesto.setPersona(persona);
			empleadoPuesto.setPlantaCargoDet(em.find(PlantaCargoDet.class,
					idPlantaCargoDet));

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
			 * Guarda un registro en la tabla de historicos de estado
			 * */
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			historico.setFechaMod(new Date());
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historico.setPlantaCargoDet(puestoSelec);

			em.persist(historico);

			/**
			 * Se insertan datos de categorías/remuneraciones:
			 * */
			for (EmpleadoConceptoPago conceptoPago : empleadoConceptoPagos) {
				EmpleadoConceptoPago emp = new EmpleadoConceptoPago();
				emp.setCategoria(conceptoPago.getCategoria());
				emp.setMonto(conceptoPago.getMonto());
				emp.setObjCodigo(conceptoPago.getObjCodigo());
				emp.setEmpleadoPuesto(empleadoPuesto);
				emp.setFechaAlta(new Date());
				emp.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				emp.setAnho(Integer.parseInt(sdfAnio.format(fechaIngreso)));

				em.persist(emp);

				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 * */
				if (seleccionUtilFormController.getCategoria() != null
						&& seleccionUtilFormController.getSinAnx() != null) {
					SinAnx anx = em.find(SinAnx.class,
							seleccionUtilFormController.getSinAnx().getIdAnx());
					anx.setCantDisponible(anx.getCantDisponible() - 1);
					em.merge(anx);
				}
			}

			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarDocumento) seteada en botón Buscar = ok
			 * Insertar registro en Documentos y el archivo pdf
			 * 
			 * *
			 */
			Long idDoc = null;
			if (buscadorDocsFC.getFileActoAdmin() != null) {
				if (buscadorDocsFC.getDocDecreto() == null) {
					idDoc = guardarAdjuntos(buscadorDocsFC.getfName(),
							buscadorDocsFC.getFileActoAdmin().getFileSize(),
							buscadorDocsFC.getFileActoAdmin().getContentType(),
							buscadorDocsFC.getFileActoAdmin(),
							buscadorDocsFC.getIdTipoDoc(), null);
					if (idDoc == null)
						return null;
					Documentos doc = em.find(Documentos.class, idDoc);
					doc.setNroDocumento(buscadorDocsFC.getNroDoc());
					doc.setAnhoDocumento(Integer.parseInt(sdfAnio
							.format(buscadorDocsFC.getFechaDoc())));
					doc.setFechaDoc(buscadorDocsFC.getFechaDoc());
					em.merge(doc);
				}
			}

			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */
			if (idDoc == null)
				idDoc = buscadorDocsFC.getDocDecreto().getIdDocumento();
			insertarRerAdjunto(idDoc);

			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarLey) seteada en botón Buscar = ok Insertar
			 * registro en Documentos y el archivo pdf
			 * 
			 * *
			 */
			Long idDocContrato = null;

			if (fileContrato != null) {
				if (documentoCotrato == null) {
					DatosEspecificos datosEspecificos = searchTipoContrato();
					idDocContrato = guardarAdjuntos(fileNameContrato,
							fileContrato.getFileSize(),
							fileContrato.getContentType(), fileContrato,
							datosEspecificos.getIdDatosEspecificos(), null);
					if (idDocContrato == null)
						return null;
					documentoCotrato = em.find(Documentos.class, idDocContrato);
					documentoCotrato.setNroDocumento(nroContrato);
					em.merge(documentoCotrato);
				}
			}

			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */
			if (idDocContrato == null)
				idDocContrato = documentoCotrato.getIdDocumento();
			insertarRerAdjunto(idDocContrato);

			/**
			 * Insertar registros de documentos adjuntos *
			 */
			/**
			 * Si variable (AdjuntarLey) seteada en botón Buscar = ok Insertar
			 * registro en Documentos y el archivo pdf
			 * 
			 * *
			 */
			Long idDocLey = null;

			if (fileLey != null) {
				if (docLey == null) {
					DatosEspecificos datosEspecificos = searchTipoLey();
					idDocLey = guardarAdjuntos(fLeyName, fileLey.getFileSize(),
							fileLey.getContentType(), fileLey,
							datosEspecificos.getIdDatosEspecificos(), null);
					if (idDocLey == null)
						return null;
					docLey = em.find(Documentos.class, idDocLey);
					docLey.setNroDocumento(nroDocLey);
					docLey.setAnhoDocumento(anhoLey);
					em.merge(docLey);
				}
			}

			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */
			if (idDocLey == null)
				idDocLey = docLey.getIdDocumento();
			insertarRerAdjunto(idDocLey);

			/**
			 * El sistema genera un registro en la tabla LEGAJOS , si la persona
			 * aun no existe la persona en la tabla Legajos.
			 * */
			if (!exiteLegajo(persona.getIdPersona())) {
				Legajos legajos = new Legajos();
				legajos.setPersona(persona);
				legajos.setFechaIngreso(fechaIngreso);
				legajos.setDatosEspecificosTipoIngreso(seleccionUtilFormController
						.traerDatosEspecificos("TIPOS DE INGRESOS Y MOVILIDAD",
								"INGRESO DIRECTO DE PERSONAL POR LEY DE EMERGENCIA"));
				em.persist(legajos);
			}
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

	public void descargarDocBD(Integer row) throws FileNotFoundException,
			IOException {
		Documentos doc = listaDocumentos.get(row);
		if (doc.getIdDocumento() != null) {
			admDocAdjuntoFormController.abrirDocumentoFromCU(
					doc.getIdDocumento(), usuarioLogueado.getIdUsuario());
		} else {
			statusMessages.add(Severity.ERROR,
					"No existe documento que descargar");
		}
	}

	@SuppressWarnings("unchecked")
	private boolean exiteLegajo(Long idPersona) {
		List<Legajos> legajos = em
				.createQuery(
						"Select l from Legajos l "
								+ " where l.persona.idPersona=:idPersona")
				.setParameter("idPersona", idPersona).getResultList();
		return !legajos.isEmpty();
	}

	private DatosEspecificos searchTipoContrato() {
		Query q = em.createQuery("select esp from DatosEspecificos esp "
				+ " where esp.activo is true "
				+ " and esp.descripcion = 'CONTRATO' "
				+ "and esp.valorAlf = 'CON'");
		return (DatosEspecificos) q.getSingleResult();
	}

	private DatosEspecificos searchTipoLey() {
		Query q = em.createQuery("select esp from DatosEspecificos esp "
				+ " where esp.activo is true "
				+ " and esp.descripcion = 'LEY DE EMERGENCIA' "
				+ "and esp.valorAlf = 'CON'");
		return (DatosEspecificos) q.getSingleResult();
	}

	/**
	 * Insertar registro en tabla REFERENCIA_ADJUNTOS para el acto
	 * administrativo
	 * */
	private void insertarRerAdjunto(Long idDocumento) {
		ReferenciaAdjuntos referenciaAdjuntos = new ReferenciaAdjuntos();
		referenciaAdjuntos.setPersona(persona);
		referenciaAdjuntos.setDocumentos(new Documentos());
		referenciaAdjuntos.getDocumentos().setIdDocumento(idDocumento);
		referenciaAdjuntos.setIdRegistroTabla(empleadoPuesto
				.getIdEmpleadoPuesto());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "ingresoPorLeyEmergencia_edit";// cambiar
			String direccionFisica = "";
			String sf = File.separator;
			direccionFisica = sf + "SICCA" + sf + anio + sf + "OEE" + sf
					+ nivelEntidadOeeUtil.getIdConfigCab();

			idDocuGenerado = admDocAdjuntoFormController.guardarDoc(file,
					direccionFisica, nombrePantalla, idTipoDoc, nombreTabla,
					idDocumento);
			return idDocuGenerado;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/** GETTER'S Y SETTER'S **/
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

	public Integer getNroDocLey() {
		return nroDocLey;
	}

	public void setNroDocLey(Integer nroDocLey) {
		this.nroDocLey = nroDocLey;
	}

	public Integer getAnhoLey() {
		return anhoLey;
	}

	public void setAnhoLey(Integer anhoLey) {
		this.anhoLey = anhoLey;
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

	public Boolean getDescargarLey() {
		return descargarLey;
	}

	public void setDescargarLey(Boolean descargarLey) {
		this.descargarLey = descargarLey;
	}

	public Boolean getAdjuntarLey() {
		return adjuntarLey;
	}

	public void setAdjuntarLey(Boolean adjuntarLey) {
		this.adjuntarLey = adjuntarLey;
	}

	public boolean isHabUpdate() {
		return habUpdate;
	}

	public void setHabUpdate(boolean habUpdate) {
		this.habUpdate = habUpdate;
	}

	public boolean isHabPersona() {
		return habPersona;
	}

	public void setHabPersona(boolean habPersona) {
		this.habPersona = habPersona;
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

	public String getfNameLeyMostrar() {
		return fNameLeyMostrar;
	}

	public void setfNameLeyMostrar(String fNameLeyMostrar) {
		this.fNameLeyMostrar = fNameLeyMostrar;
	}

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public PersonaDTO getPersonaDTO() {
		return personaDTO;
	}

	public void setPersonaDTO(PersonaDTO personaDTO) {
		this.personaDTO = personaDTO;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
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

	public Documentos getDocLey() {
		return docLey;
	}

	public void setDocLey(Documentos docLey) {
		this.docLey = docLey;
	}

	public byte[] getuLeyFile() {
		return uLeyFile;
	}

	public void setuLeyFile(byte[] uLeyFile) {
		this.uLeyFile = uLeyFile;
	}

	public String getcLeyType() {
		return cLeyType;
	}

	public void setcLeyType(String cLeyType) {
		this.cLeyType = cLeyType;
	}

	public String getfLeyName() {
		return fLeyName;
	}

	public void setfLeyName(String fLeyName) {
		this.fLeyName = fLeyName;
	}

	public UploadItem getFileLey() {
		return fileLey;
	}

	public void setFileLey(UploadItem fileLey) {
		this.fileLey = fileLey;
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

	public List<Documentos> getListaDocumentos() {
		return listaDocumentos;
	}

	public void setListaDocumentos(List<Documentos> listaDocumentos) {
		this.listaDocumentos = listaDocumentos;
	}

	public Date getFechaInicioContrato() {
		return fechaInicioContrato;
	}

	public void setFechaInicioContrato(Date fechaInicioContrato) {
		this.fechaInicioContrato = fechaInicioContrato;
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

	public String getContratoAdjunto() {
		return contratoAdjunto;
	}

	public void setContratoAdjunto(String contratoAdjunto) {
		this.contratoAdjunto = contratoAdjunto;
	}

	public Documentos getDocumentoCotrato() {
		return documentoCotrato;
	}

	public void setDocumentoCotrato(Documentos documentoCotrato) {
		this.documentoCotrato = documentoCotrato;
	}

	public boolean isHabDesContrato() {
		return habDesContrato;
	}

	public void setHabDesContrato(boolean habDesContrato) {
		this.habDesContrato = habDesContrato;
	}

	public boolean isHabAdjContrato() {
		return habAdjContrato;
	}

	public void setHabAdjContrato(boolean habAdjContrato) {
		this.habAdjContrato = habAdjContrato;
	}

	public String getNombreContrato() {
		return nombreContrato;
	}

	public void setNombreContrato(String nombreContrato) {
		this.nombreContrato = nombreContrato;
	}

	public byte[] getUploadedFileContrato() {
		return uploadedFileContrato;
	}

	public void setUploadedFileContrato(byte[] uploadedFileContrato) {
		this.uploadedFileContrato = uploadedFileContrato;
	}

	public String getContentTypeContrato() {
		return contentTypeContrato;
	}

	public void setContentTypeContrato(String contentTypeContrato) {
		this.contentTypeContrato = contentTypeContrato;
	}

	public String getFileNameContrato() {
		return fileNameContrato;
	}

	public void setFileNameContrato(String fileNameContrato) {
		this.fileNameContrato = fileNameContrato;
	}

	public UploadItem getFileContrato() {
		return fileContrato;
	}

	public void setFileContrato(UploadItem fileContrato) {
		this.fileContrato = fileContrato;
	}

	public boolean isHabAdjLey() {
		return habAdjLey;
	}

	public void setHabAdjLey(boolean habAdjLey) {
		this.habAdjLey = habAdjLey;
	}

}
