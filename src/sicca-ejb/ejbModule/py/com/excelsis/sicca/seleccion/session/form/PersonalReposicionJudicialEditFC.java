package py.com.excelsis.sicca.seleccion.session.form;

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
import javax.persistence.EntityManager;
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
import py.com.excelsis.sicca.session.form.AdministrarVacanciasListFormController;
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
@Name("personalReposicionJudicialEditFC")
public class PersonalReposicionJudicialEditFC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3243890324148819874L;
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
	@In(create = true)
	AdministrarVacanciasListFormController administrarVacanciasListFormController;

	
	private Long idPaisExp = null;
	private Long idPersona = null;
	private Long idEmpleadoPuesto;
	private Long idPlantaCargoDet;
	private Long idPais;
	private Integer monto;
	private Integer nroDocOficio;
	private Integer anhoOficio;
	private int selectedRow = -1;
	private int selectedRowPuesto = -1;
	private Boolean descargarOficio;
	private Boolean adjuntarOficio;
	private boolean habUpdate = false;
	private boolean habPersona = false;
	private boolean habPaisExp = true;
	private boolean habBtnAddPersons = false;
	private Boolean primeraEntrada = true;
	private String docIdentidad = null;
	private String fNameMostrar;
	private Date fechaIngreso;
	private PersonaDTO personaDTO;
	private Persona persona = new Persona();
	private PlantaCargoDet plantaCargoDetView = new PlantaCargoDet();
	private EmpleadoPuesto empleadoPuesto;
	private Documentos docOficio;
	private byte[] uFile = null;
	private String cType = null;
	private String fName;
	private UploadItem fileOficio;
	SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
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

	private void obtenerDatosView() {
		em.clear();
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

	public String toFindPersona() {
		idPersona = null;
		return "/seleccion/persona/PersonaEdit.xhtml?personaFrom=/seleccion/ingresoDirecto/porReposicionJudicial/PersonalReposicionJudicialEdit";
	}

	public String toFindPersonaView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/seleccion/ingresoDirecto/porReposicionJudicial/PersonalReposicionJudicialEdit&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
	}
	public String toFindPersonaToView() {
		if (persona.getIdPersona() == null) {
			statusMessages.add(Severity.ERROR, "Debe seleccionar la Persona");
			return null;
		}
		return "/seleccion/persona/Persona.xhtml?personaFrom=/seleccion/ingresoDirecto/porReposicionJudicial/PersonalReposicionJudicialView&personaIdPersona="
				+ persona.getIdPersona() + "&conversationPropagation=join";
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
				.setParameter("idDet", configuracionUoDetList.get(index).getIdConfiguracionUoDet()).getResultList();
		setSelectedRow(index);
		setSelectedRowPuesto(-1);
	}

	private Long getIdEstadoCabVacante() {
		EstadoCab cab = seleccionUtilFormController.buscarEstadoCab("VACANTE");
		return cab.getIdEstadoCab();
	}

	private Boolean precondSearch() {
		if (nroDocOficio == null || anhoOficio == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}
		return true;
	}

	public void buscarDoc() {
		if (!precondSearch())
			return;
		docOficio = searchDoc();
		if (docOficio != null) {
			fNameMostrar = docOficio.getNombreFisicoDoc();
			descargarOficio = true;
			adjuntarOficio = false;
			statusMessages.add(Severity.INFO, "Documento encontrado!");
		} else {
			fNameMostrar = null;
			adjuntarOficio = true;
			descargarOficio = false;
			// fileActoAdmin = null;
			statusMessages.add(Severity.ERROR, "Documento no encontrado");
		}
	}

	private Documentos searchDoc() {
		Query q = em
				.createQuery("select Documentos from Documentos Documentos "
						+ " where Documentos.activo is true "
						+ " and Documentos.nroDocumento = :nroDoc and Documentos.anhoDocumento = :anhio"
						+ " and Documentos.datosEspecificos.descripcion = 'OFICIO JUDICIAL' "
						+ "and Documentos.datosEspecificos.valorAlf = 'CON'");
		q.setParameter("nroDoc", nroDocOficio);
		q.setParameter("anhio", anhoOficio);

		List<Documentos> lista = q.getResultList();
		if (lista.size() > 0) {
			fNameMostrar = lista.get(0).getNombreFisicoDoc();
			return lista.get(0);
		}
		return null;
	}

	private DatosEspecificos searchTipoOficio() {
		Query q = em.createQuery("select esp from DatosEspecificos esp "
				+ " where esp.activo is true "
				+ " and esp.descripcion = 'OFICIO JUDICIAL' "
				+ "and esp.valorAlf = 'CON'");
		return (DatosEspecificos) q.getSingleResult();
	}

	public void adjuntarDoc() {
		if (!precondAdjuntarDoc())
			return;

		fileOficio = seleccionUtilFormController.crearUploadItem(fName,
				uFile.length, cType, uFile);
		fNameMostrar = fName;

		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));

	}

	private Boolean precondAdjuntarDoc() {
		if (!precondSearch())
			return false;
		if (uFile == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos obligatorios");
			return false;
		}
		// Verificar que ya no exista
		Documentos doc = searchDoc();
		if (doc != null) {
			statusMessages
					.add(Severity.ERROR,
							"No se puede adjuntar, ya existe un documento con los mismos parámetros");
			return false;
		}
		return true;
	}

	public void limpiarDatosPersona() {
		persona = new Persona();
		idPersona = null;
		persona = new Persona();
		docIdentidad = null;
		habBtnAddPersons = false;
	}

	public void agregar() {
		if (seleccionUtilFormController.getCodigoObj() == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Cod. Objeto Gasto");
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
		empleadoConceptoPagos.remove(index);
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
	}

	public String obtenerCodigo(PlantaCargoDet planta) {
		return administrarVacanciasListFormController.obtenerCodigo(planta);
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
							.getDatosIngresoReposicionJudicial());
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
				emp.setAnho(Integer.parseInt(sdfAnio
						.format(fechaIngreso)));
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
			Long idDocOficio = null;

			if (fileOficio != null) {
				if (docOficio == null) {
					DatosEspecificos datosEspecificos = searchTipoOficio();
					idDocOficio = guardarAdjuntos(fName,
							fileOficio.getFileSize(),
							fileOficio.getContentType(), fileOficio,
							datosEspecificos.getIdDatosEspecificos(), null);
					if (idDocOficio == null)
						return null;
					Documentos docOficio = em.find(Documentos.class,
							idDocOficio);
					docOficio.setNroDocumento(nroDocOficio);
					docOficio.setAnhoDocumento(anhoOficio);
					em.merge(docOficio);
				}
			}

			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */
			if (idDocOficio == null)
				idDocOficio = docOficio.getIdDocumento();
			insertarRerAdjunto(idDocOficio);

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
								"INGRESO DIRECTO PERSONAL REPOSICION JUDICIAL"));
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

	@SuppressWarnings("unchecked")
	private boolean exiteLegajo(Long idPersona) {
		List<Legajos> legajos = em
				.createQuery(
						"Select l from Legajos l "
								+ " where l.persona.idPersona=:idPersona")
				.setParameter("idPersona", idPersona).getResultList();
		return !legajos.isEmpty();
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "personalReposicionJudicial_edit";
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
		if (nroDocOficio == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Número de Oficio Judicial");
			return false;
		}
		if (anhoOficio == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar el Año de Oficio Judicial");
			return false;
		}
		if (fileOficio == null) {
			statusMessages.add(Severity.ERROR,
					"Debe Ingresar un oficio, verifique");
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

		String grupoValidador = "IDREPJUD";

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

	public void descargarDocBD() throws FileNotFoundException, IOException {
		
		
		if (docOficio != null &&  docOficio.getIdDocumento() != null) {
			String resp =	AdmDocAdjuntoFormController.abrirDocumentoFromCU(docOficio.getIdDocumento(), usuarioLogueado.getIdUsuario());
			if(!resp.equalsIgnoreCase("OK")){
				statusMessages.add(Severity.ERROR,resp);
			}
			} else if (fileOficio != null) {
				admDocAdjuntoFormController.enviarArchivoANavegador(fileOficio.getFileName(), fileOficio.getFile());
			} else {
				statusMessages.add(Severity.ERROR, "No existe documento que descargar");
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
	 * Getter's y Setter's
	 * 
	 * @return
	 */
	

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

	public Integer getMonto() {
		return monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
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

	public Integer getNroDocOficio() {
		return nroDocOficio;
	}

	public void setNroDocOficio(Integer nroDocOficio) {
		this.nroDocOficio = nroDocOficio;
	}

	public Integer getAnhoOficio() {
		return anhoOficio;
	}

	public void setAnhoOficio(Integer anhoOficio) {
		this.anhoOficio = anhoOficio;
	}

	public Boolean getDescargarOficio() {
		return descargarOficio;
	}

	public void setDescargarOficio(Boolean descargarOficio) {
		this.descargarOficio = descargarOficio;
	}

	public Boolean getAdjuntarOficio() {
		return adjuntarOficio;
	}

	public void setAdjuntarOficio(Boolean adjuntarOficio) {
		this.adjuntarOficio = adjuntarOficio;
	}

	public Documentos getDocOficio() {
		return docOficio;
	}

	public void setDocOficio(Documentos docOficio) {
		this.docOficio = docOficio;
	}

	public String getfNameMostrar() {
		return fNameMostrar;
	}

	public void setfNameMostrar(String fNameMostrar) {
		this.fNameMostrar = fNameMostrar;
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

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public UploadItem getFileOficio() {
		return fileOficio;
	}

	public void setFileOficio(UploadItem fileOficio) {
		this.fileOficio = fileOficio;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public List<Documentos> getListaDocumentos() {
		return listaDocumentos;
	}

	public void setListaDocumentos(List<Documentos> listaDocumentos) {
		this.listaDocumentos = listaDocumentos;
	}

	public int getSelectedRowPuesto() {
		return selectedRowPuesto;
	}

	public void setSelectedRowPuesto(int selectedRowPuesto) {
		this.selectedRowPuesto = selectedRowPuesto;
	}
	
	

}
