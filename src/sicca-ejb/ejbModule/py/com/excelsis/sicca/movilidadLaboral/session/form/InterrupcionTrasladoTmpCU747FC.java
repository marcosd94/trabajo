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

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Documentos;
import py.com.excelsis.sicca.entity.EmpleadoConceptoPago;
import py.com.excelsis.sicca.entity.EmpleadoMovilidadCab;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.ReferenciaAdjuntos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.form.AdmDocAdjuntoFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.BuscadorDocsFC;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.ReponerCategoriasController;

@Name("interrupcionTrasladoTmpCU747FC")
@Scope(ScopeType.CONVERSATION)
public class InterrupcionTrasladoTmpCU747FC {
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(create = true)
	BuscadorDocsFC buscadorDocsFC;
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	ReponerCategoriasController reponerCategoriasController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	AdmDocAdjuntoFormController admDocAdjuntoFormController;

	private Long idEmpleadoPuesto;
	private int selectedRow = -1;
	private Date fechaRetorno;
	private String observacion;
	private BigDecimal codNivelEntidad;
	private String nombreNivelEntidad;
	private BigDecimal entCodigo;
	private String entidad;
	private Integer orden;
	private String oee;
	private String codUnigOrg;
	private String unidadOrg;
	private Boolean comisionamiento;
	private Boolean ocupa;
	private EmpleadoPuesto empleadoPuestoAnterior = new EmpleadoPuesto();
	private EmpleadoPuesto empleadoPuestoNuevo = new EmpleadoPuesto();
	private EmpleadoPuesto empleadoPuestoVirtual = new EmpleadoPuesto();
	private EmpleadoMovilidadCab empleadoMovilidadCab = new EmpleadoMovilidadCab();
	private NivelEntidadOeeUtil entidadOeeUtilAnterior;
	private List<EmpleadoMovilidadCab> empleadoMovilidadCabList = new ArrayList<EmpleadoMovilidadCab>();
	private List<EmpleadoConceptoPago> ListaEmpleadoConceptoPagoNew = new ArrayList<EmpleadoConceptoPago>();
	private List<EmpleadoConceptoPago> ListaEmpleadoConceptoPagoOld = new ArrayList<EmpleadoConceptoPago>();
	private List<ReferenciaAdjuntos> adjuntos = new ArrayList<ReferenciaAdjuntos>();

	public void init() {
		searchEmpleadoMovCab();
		selectedRow = -1;
	}

	public void initVer() {
		if (idEmpleadoPuesto != null) {
			empleadoPuestoNuevo = em.find(EmpleadoPuesto.class,
					idEmpleadoPuesto);
			adjuntos = em
					.createQuery(
							"Select r from ReferenciaAdjuntos r "
									+ " where r.idRegistroTabla=:idRegTabla")
					.setParameter("idRegTabla", idEmpleadoPuesto)
					.getResultList();
			searchMovilidaCab();
			empleadoPuestoAnterior = empleadoMovilidadCab
					.getEmpleadoPuestoAnterior();
			obtenerNivelEntidadOeeVer();
			obtenerEmpleadoConceptoPagoAnterior();
			obtenerEmpleadoConceptoPagoNew();
		}
	}

	private void searchMovilidaCab() {
		List<EmpleadoMovilidadCab> lista = em
				.createQuery(
						"Select e from EmpleadoMovilidadCab e "
								+ " where e.empleadoPuestoNuevo.idEmpleadoPuesto=:idRegTabla")
				.setParameter("idRegTabla", idEmpleadoPuesto).getResultList();
		if (!lista.isEmpty()) {
			empleadoMovilidadCab = lista.get(0);
			observacion = empleadoMovilidadCab.getObservacion();
			fechaRetorno = empleadoMovilidadCab.getFechaInicio();
		}
	}

	private void searchEmpleadoMovCab() {
		String sql = "SELECT DISTINCT em.* "
				+ "FROM general.empleado_puesto e "
				+ "JOIN movilidad.empleado_movilidad_cab em on em.id_empleado_puesto_nuevo = e.id_empleado_puesto "
				//+ "JOIN GENERAL.EMPLEADO_PUESTO VIRTUAL ON VIRTUAL.ID_EMPLEADO_PUESTO = EM.id_empleado_puesto_virtual " MP 10/11/2015
				+ "JOIN seleccion.datos_especificos de on de.id_datos_especificos = e.id_datos_esp_tipo_ingreso_movilidad "
				+ "JOIN seleccion.datos_generales dg on dg.id_datos_generales = de.id_datos_generales  "
				+ "JOIN planificacion.planta_cargo_det puesto on puesto.id_planta_cargo_det = e.id_planta_cargo_det "
				+ "JOIN general.persona persona on persona.id_persona = e.id_persona "
				+ "JOIN planificacion.configuracion_uo_det uo on uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det "
				+ "JOIN planificacion.configuracion_uo_cab oee on uo.id_configuracion_uo = oee.id_configuracion_uo "
				+ "JOIN planificacion.entidad entidad on entidad.id_entidad= oee.id_entidad "
				+ "JOIN sinarh.sin_entidad sin_entidad on sin_entidad.id_sin_entidad=entidad.id_sin_entidad "
				+ "JOIN sinarh.sin_nivel_entidad sne on (sin_entidad.ani_aniopre = sne.ani_aniopre and sin_entidad.nen_codigo = sne.nen_codigo) "
				+ "WHERE dg.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' AND  "
				+ "de.descripcion = 'TRASLADO TEMPORAL (COMISIONAMIENTO)' AND e.actual = TRUE order by em.id_empleado_movilidad_cab ";
		empleadoMovilidadCabList = em.createNativeQuery(sql,
				EmpleadoMovilidadCab.class).getResultList();
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

	public void obtenerDatosFuncionario(int index) {

		empleadoMovilidadCab = empleadoMovilidadCabList.get(index);
		empleadoPuestoAnterior = empleadoMovilidadCab
				.getEmpleadoPuestoAnterior();
		empleadoPuestoNuevo = empleadoMovilidadCab.getEmpleadoPuestoNuevo();
		empleadoPuestoVirtual = empleadoMovilidadCab.getEmpleadoPuestoVirtual();
		ocupa = empleadoPuestoNuevo.isActual();
		if (empleadoPuestoNuevo.getDatosEspecificosByIdDatosEspSituacion()
				.getDescripcion().equalsIgnoreCase("COMISIONAMIENTO INTERNO"))
			comisionamiento = true;
		else
			comisionamiento = false;
		obtenerNivelEntidadOeeNew();
		obtenerEmpleadoConceptoPagoNew();
		obtenerEmpleadoConceptoPagoAnterior();
		setSelectedRow(index);

	}

	private void obtenerNivelEntidadOeeNew() {
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(empleadoPuestoNuevo
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilAnterior.setIdUnidadOrganizativa(empleadoPuestoNuevo
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getIdConfiguracionUoDet());
		entidadOeeUtilAnterior.init2();
		nombreNivelEntidad = entidadOeeUtilAnterior.getNombreNivelEntidad();
		codNivelEntidad = entidadOeeUtilAnterior.getCodNivelEntidad();
		entCodigo = entidadOeeUtilAnterior.getCodSinEntidad();
		entidad = entidadOeeUtilAnterior.getNombreSinEntidad();
		orden = entidadOeeUtilAnterior.getOrdenUnidOrg();
		oee = entidadOeeUtilAnterior.getDenominacionUnidad();
		if (nivelEntidadOeeUtil == null)
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
		nivelEntidadOeeUtil.setEm(em);
		nivelEntidadOeeUtil.setIdConfigCab(empleadoPuestoAnterior
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(empleadoPuestoAnterior
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();

	}

	private void obtenerNivelEntidadOeeVer() {
		if (entidadOeeUtilAnterior == null)
			entidadOeeUtilAnterior = (NivelEntidadOeeUtil) Component
					.getInstance(NivelEntidadOeeUtil.class, true);
		entidadOeeUtilAnterior.setEm(em);
		entidadOeeUtilAnterior.setIdConfigCab(empleadoPuestoAnterior
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getConfiguracionUoCab().getIdConfiguracionUo());
		entidadOeeUtilAnterior.setIdUnidadOrganizativa(empleadoPuestoAnterior
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getIdConfiguracionUoDet());
		entidadOeeUtilAnterior.init2();
		nombreNivelEntidad = entidadOeeUtilAnterior.getNombreNivelEntidad();
		codNivelEntidad = entidadOeeUtilAnterior.getCodNivelEntidad();
		entCodigo = entidadOeeUtilAnterior.getCodSinEntidad();
		entidad = entidadOeeUtilAnterior.getNombreSinEntidad();
		orden = entidadOeeUtilAnterior.getOrdenUnidOrg();
		oee = entidadOeeUtilAnterior.getDenominacionUnidad();
		codUnigOrg = entidadOeeUtilAnterior.getCodigoUnidOrgDep();
		unidadOrg = entidadOeeUtilAnterior.getDenominacionUnidadOrgaDep();
		if (nivelEntidadOeeUtil == null)
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
		nivelEntidadOeeUtil.setEm(em);
		nivelEntidadOeeUtil.setIdConfigCab(empleadoPuestoNuevo
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdUnidadOrganizativa(empleadoPuestoNuevo
				.getPlantaCargoDet().getConfiguracionUoDet()
				.getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();

	}

	public String toFindPersonaView() {

		String from = "";
		if (idEmpleadoPuesto == null)
			from = "/movilidadLaboral/interrupcionTrasladoTmp/InterrupcionTrasladoTmpVerCU474";
		else
			from = "/movilidadLaboral/interrupcionTrasladoTmp/InterrupcionTrasladoTmpVerCU474";
		return "/seleccion/persona/Persona.xhtml?personaFrom=" + from
				+ "&personaIdPersona="
				+ empleadoPuestoAnterior.getPersona().getIdPersona()
				+ "&conversationPropagation=join";
	}

	private void obtenerEmpleadoConceptoPagoNew() {
		try {
			ListaEmpleadoConceptoPagoNew = em.createQuery(
					"Select e from EmpleadoConceptoPago e "
							+ " where e.empleadoPuesto.idEmpleadoPuesto = "
							+ empleadoPuestoNuevo.getIdEmpleadoPuesto())
					.getResultList();

		} catch (Exception ex) {

		}
	}

	private void obtenerEmpleadoConceptoPagoAnterior() {
		try {
			ListaEmpleadoConceptoPagoOld = em.createQuery(
					"Select e from EmpleadoConceptoPago e "
							+ " where e.empleadoPuesto.idEmpleadoPuesto = "
							+ empleadoPuestoAnterior.getIdEmpleadoPuesto())
					.getResultList();

		} catch (Exception ex) {

		}
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
					"Debe Inrgesar un archivo, verifique");
			return false;
		}

		if (fechaRetorno == null) {
			statusMessages.add(Severity.ERROR,
					"Debe ingresar la Fecha de Retorno");
			return false;
		}
		if (observacion == null || observacion.trim().isEmpty()) {
			statusMessages.add(Severity.ERROR, "Debe ingresar la Observación");
			return false;
		}

		return true;
	}

	public DatosEspecificos traerDatosEspecificos(String dg, String de) {
		Query q = em
				.createQuery("select DatosEspecificos from  DatosEspecificos DatosEspecificos"
						+ " where datosGenerales.descripcion = :dg and DatosEspecificos.descripcion = :de and DatosEspecificos.activo is true ");
		q.setParameter("de", de);
		q.setParameter("dg", dg);
		List<DatosEspecificos> lista = q.getResultList();
		if (lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public String save() {
		try {
			if (!chkDatos())
				return null;
			SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
			if (reponerCategoriasController == null)
				reponerCategoriasController = (ReponerCategoriasController) Component
						.getInstance(ReponerCategoriasController.class, true);
			/**
			 * Actualiza los datos del puesto ocupado actualmente
			 */
			empleadoPuestoNuevo.setActual(false);
			empleadoPuestoNuevo.setFechaFin(new Date());
			empleadoPuestoNuevo.setUsuMod(usuarioLogueado.getCodigoUsuario());
			empleadoPuestoNuevo.setFechaMod(new Date());
			em.merge(empleadoPuestoNuevo);

			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al puesto
			 * ocupado actualmente
			 * */
			PlantaCargoDet plantaCargoDet = em.find(PlantaCargoDet.class,
					empleadoPuestoNuevo.getPlantaCargoDet()
							.getIdPlantaCargoDet());
			plantaCargoDet.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("VACANTE"));
			plantaCargoDet.setUsuMod(usuarioLogueado.getCodigoUsuario());
			plantaCargoDet.setFechaMod(new Date());
			plantaCargoDet.setEstadoDet(null);
			em.merge(plantaCargoDet);

			/***
			 * • Registra el histórico de cambios de estados del Puesto nuevo en
			 * la tabla HISTORICOS_ESTADO , registrando el estado actual
			 * */
			HistoricosEstado historico = new HistoricosEstado();
			historico.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("VACANTE"));
			historico.setFechaMod(new Date());
			historico.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historico.setPlantaCargoDet(plantaCargoDet);
			em.persist(historico);

			/**
			 * • Gestionar objetos y categorías para el puesto
			 * 
			 * */

			for (EmpleadoConceptoPago conceptoPago : ListaEmpleadoConceptoPagoNew) {

				/**
				 * PARA EL CASO QUE TENGA CATEGORIA
				 * */
				if (conceptoPago.getCategoria() != null
						&& conceptoPago.getObjCodigo() != null)
					reponerCategoriasController.actualizarDisponibles(
							conceptoPago.getCategoria(),
							conceptoPago.getObjCodigo());
			}

			/**
			 * Actualizar el puesto virtual
			 */
			Long idDato = traerDatosEspecificos(
					"TIPOS DE INGRESOS Y MOVILIDAD",
					"INTERRUPCION TRASLADO TEMPORAL").getIdDatosEspecificos();
			empleadoPuestoVirtual
					.setDatosEspecificosByIdDatosEspTipoIngresoMovilidad(em
							.find(DatosEspecificos.class, idDato));

			empleadoPuestoVirtual.setFechaInicio(fechaRetorno);
			empleadoPuestoVirtual
					.setDatosEspecificosByIdDatosEspEstado(seleccionUtilFormController
							.traerDatosEspecificos("ESTADO EMPLEADO PUESTO",
									"PERMANENTE"));
			empleadoPuestoVirtual
					.setDatosEspecificosByIdDatosEspSituacion(new DatosEspecificos());
			empleadoPuestoVirtual.setObservacion(observacion);
			em.merge(empleadoPuestoVirtual);
			/**
			 * Actualiza la tabla PLANTA_CARGO_DET correspondiente al puesto
			 * virtual
			 * */
			PlantaCargoDet plantaCargoDetVirtual = em.find(
					PlantaCargoDet.class, empleadoPuestoVirtual
							.getPlantaCargoDet().getIdPlantaCargoDet());
			plantaCargoDetVirtual.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			plantaCargoDetVirtual.setUsuMod(usuarioLogueado.getCodigoUsuario());
			plantaCargoDetVirtual.setFechaMod(new Date());
			plantaCargoDetVirtual.setEstadoDet(null);
			em.merge(plantaCargoDetVirtual);

			/***
			 * • Registra el histórico de cambios de estados del Puesto virtual
			 * en la tabla HISTORICOS_ESTADO , registrando el estado actual
			 * */
			HistoricosEstado historicoVirtual = new HistoricosEstado();
			historicoVirtual.setEstadoCab(seleccionUtilFormController
					.buscarEstadoCab("OCUPADO"));
			historicoVirtual.setFechaMod(new Date());
			historicoVirtual.setUsuMod(usuarioLogueado.getCodigoUsuario());
			historicoVirtual.setPlantaCargoDet(plantaCargoDetVirtual);
			em.persist(historicoVirtual);
			/**
			 * • Gestionar objetos y categorías para el puesto OCUPADO
			 * 
			 * */
			Integer anio = new Date().getYear() + 1900;
			for (EmpleadoConceptoPago concepto : ListaEmpleadoConceptoPagoOld) {
				EmpleadoConceptoPago p = new EmpleadoConceptoPago();
				p.setAnho(anio);
				p.setCategoria(concepto.getCategoria());
				p.setEmpleadoPuesto(empleadoPuestoVirtual);
				p.setFechaAlta(new Date());
				p.setMonto(concepto.getMonto());
				p.setObjCodigo(concepto.getObjCodigo());
				p.setUsuAlta(usuarioLogueado.getCodigoUsuario());
				em.persist(p);

			}

			EmpleadoMovilidadCab movilidad = new EmpleadoMovilidadCab();
			movilidad.setActivo(true);
			movilidad.setEmpleadoPuestoAnterior(empleadoPuestoNuevo);
			movilidad.setEmpleadoPuestoNuevo(empleadoPuestoVirtual);
			movilidad.setFechaAlta(new Date());
			movilidad.setFechaInicio(fechaRetorno);
			movilidad.setObservacion(observacion);
			movilidad.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(movilidad);

			// Por Acto administrativo de Entidad Destino
			Long idDocDestino = null;
			if (buscadorDocsFC.getFileActoAdmin() != null) {

				idDocDestino = guardarAdjuntos(buscadorDocsFC.getfName(),
						buscadorDocsFC.getFileActoAdmin().getFileSize(),
						buscadorDocsFC.getFileActoAdmin().getContentType(),
						buscadorDocsFC.getFileActoAdmin(),
						buscadorDocsFC.getIdTipoDoc(), null);
				if (idDocDestino == null)
					return null;
				Documentos doc = em.find(Documentos.class, idDocDestino);
				doc.setNroDocumento(buscadorDocsFC.getNroDoc());
				doc.setAnhoDocumento(Integer.parseInt(sdfAnio
						.format(buscadorDocsFC.getFechaDoc())));
				doc.setFechaDoc(buscadorDocsFC.getFechaDoc());

				em.merge(doc);

			}
			/**
			 * Si no se inserta en tabla documentos y solo se referencia
			 * */

			insertarAdjunto(idDocDestino);

			em.flush();
			statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
					.getString("GENERICO_MSG"));

			return "persisted";

		} catch (Exception e) {
			statusMessages.add(Severity.ERROR, e.getMessage());
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
		referenciaAdjuntos.setPersona(empleadoPuestoAnterior.getPersona());
		referenciaAdjuntos
				.setDocumentos(em.find(Documentos.class, idDocumento));
		referenciaAdjuntos.setIdRegistroTabla(empleadoPuestoAnterior
				.getIdEmpleadoPuesto());
		referenciaAdjuntos.setUsuAlta(usuarioLogueado.getCodigoUsuario());
		referenciaAdjuntos.setFechaAlta(new Date());
		em.persist(referenciaAdjuntos);
	}

	private Long guardarAdjuntos(String fileName, int fileSize,
			String contentType, UploadItem file, Long idTipoDoc,
			Long idDocumento) {
		SimpleDateFormat sdfAnio = new SimpleDateFormat("yyyy");
		try {

			String anio = sdfAnio.format(new Date());
			Long idDocuGenerado;
			String nombreTabla = "EmpleadoPuesto";
			String nombrePantalla = "interrupcion_traslado_tmp_edit";
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

	public String getRowClass(int currentRow) {
		if (selectedRow == currentRow) {
			return "selectedRow";
		}
		return "notSelectedRow";
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

	public Long getIdEmpleadoPuesto() {
		return idEmpleadoPuesto;
	}

	public void setIdEmpleadoPuesto(Long idEmpleadoPuesto) {
		this.idEmpleadoPuesto = idEmpleadoPuesto;
	}

	public EmpleadoPuesto getEmpleadoPuestoAnterior() {
		return empleadoPuestoAnterior;
	}

	public void setEmpleadoPuestoAnterior(EmpleadoPuesto empleadoPuestoAnterior) {
		this.empleadoPuestoAnterior = empleadoPuestoAnterior;
	}

	public EmpleadoPuesto getEmpleadoPuestoNuevo() {
		return empleadoPuestoNuevo;
	}

	public void setEmpleadoPuestoNuevo(EmpleadoPuesto empleadoPuestoNuevo) {
		this.empleadoPuestoNuevo = empleadoPuestoNuevo;
	}

	public List<EmpleadoMovilidadCab> getEmpleadoMovilidadCabList() {
		return empleadoMovilidadCabList;
	}

	public void setEmpleadoMovilidadCabList(
			List<EmpleadoMovilidadCab> empleadoMovilidadCabList) {
		this.empleadoMovilidadCabList = empleadoMovilidadCabList;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public Date getFechaRetorno() {
		return fechaRetorno;
	}

	public void setFechaRetorno(Date fechaRetorno) {
		this.fechaRetorno = fechaRetorno;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
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

	public BigDecimal getEntCodigo() {
		return entCodigo;
	}

	public void setEntCodigo(BigDecimal entCodigo) {
		this.entCodigo = entCodigo;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getOee() {
		return oee;
	}

	public void setOee(String oee) {
		this.oee = oee;
	}

	public EmpleadoMovilidadCab getEmpleadoMovilidadCab() {
		return empleadoMovilidadCab;
	}

	public void setEmpleadoMovilidadCab(
			EmpleadoMovilidadCab empleadoMovilidadCab) {
		this.empleadoMovilidadCab = empleadoMovilidadCab;
	}

	public Boolean getComisionamiento() {
		return comisionamiento;
	}

	public void setComisionamiento(Boolean comisionamiento) {
		this.comisionamiento = comisionamiento;
	}

	public Boolean getOcupa() {
		return ocupa;
	}

	public void setOcupa(Boolean ocupa) {
		this.ocupa = ocupa;
	}

	public List<EmpleadoConceptoPago> getListaEmpleadoConceptoPagoNew() {
		return ListaEmpleadoConceptoPagoNew;
	}

	public void setListaEmpleadoConceptoPagoNew(
			List<EmpleadoConceptoPago> listaEmpleadoConceptoPagoNew) {
		ListaEmpleadoConceptoPagoNew = listaEmpleadoConceptoPagoNew;
	}

	public NivelEntidadOeeUtil getEntidadOeeUtilAnterior() {
		return entidadOeeUtilAnterior;
	}

	public void setEntidadOeeUtilAnterior(
			NivelEntidadOeeUtil entidadOeeUtilAnterior) {
		this.entidadOeeUtilAnterior = entidadOeeUtilAnterior;
	}

	public List<EmpleadoConceptoPago> getListaEmpleadoConceptoPagoOld() {
		return ListaEmpleadoConceptoPagoOld;
	}

	public void setListaEmpleadoConceptoPagoOld(
			List<EmpleadoConceptoPago> listaEmpleadoConceptoPagoOld) {
		ListaEmpleadoConceptoPagoOld = listaEmpleadoConceptoPagoOld;
	}

	public EmpleadoPuesto getEmpleadoPuestoVirtual() {
		return empleadoPuestoVirtual;
	}

	public void setEmpleadoPuestoVirtual(EmpleadoPuesto empleadoPuestoVirtual) {
		this.empleadoPuestoVirtual = empleadoPuestoVirtual;
	}

	public List<ReferenciaAdjuntos> getAdjuntos() {
		return adjuntos;
	}

	public void setAdjuntos(List<ReferenciaAdjuntos> adjuntos) {
		this.adjuntos = adjuntos;
	}

	public String getCodUnigOrg() {
		return codUnigOrg;
	}

	public void setCodUnigOrg(String codUnigOrg) {
		this.codUnigOrg = codUnigOrg;
	}

	public String getUnidadOrg() {
		return unidadOrg;
	}

	public void setUnidadOrg(String unidadOrg) {
		this.unidadOrg = unidadOrg;
	}

}
