package py.com.excelsis.sicca.circuitoCSI.session.form;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.MatrizRefConf;
import py.com.excelsis.sicca.entity.Observacion;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.BarrioList;
import py.com.excelsis.sicca.session.CiudadList;
import py.com.excelsis.sicca.session.DepartamentoList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.Utiles;
import enums.ActividadEnum;
import enums.ModalidadOcupacion;

@Scope(ScopeType.CONVERSATION)
@Name("cargaDeGruposCU683FC")
public class CargaDeGruposCU683FC {
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In
	StatusMessages statusMessages;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	CiudadList ciudadList;
	@In(create = true)
	DepartamentoList departamentoList;
	@In(create = true)
	BarrioList barrioList;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	@In(scope = ScopeType.SESSION, required = false)
	@Out(scope = ScopeType.SESSION, required = false)
	String roles;

	private static String ACTIVIDAD_ESTADO = "2";
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private PlantaCargoDet plantaCargoDet;
	private List<ConcursoPuestoDet> listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
	private List<SelectItem> departamentosSelecItem;
	private List<SelectItem> ciudadSelecItem;
	private List<SelectItem> barrioSelecItem;
	private Long idDpto;
	private Long idCiudad;
	private Long idBarrio;
	private Long idConcursoPuestoAgr;
	private ModalidadOcupacion modalidadOcupacion;
	private String valorRadioButton;
	private String observaciones;
	private String ubicacionFisica;
	private Boolean muestraBtnHomologados = false;

	/**
	 * Método que inicializa los datos
	 */
	public void init() {

		concursoPuestoAgr = new ConcursoPuestoAgr();
		if (idConcursoPuestoAgr != null) {
			concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
			concurso = new Concurso();
			configuracionUoCab = new ConfiguracionUoCab();
			sinEntidad = new SinEntidad();
			nivelEntidad = new SinNivelEntidad();
			concurso = concursoPuestoAgr.getConcurso();
			listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
			buscarLista();
			muestraBancoPerfiles();
			if (concurso != null) {
				/* Incidencia 1014 */
				validarOee();
				/**/
				configuracionUoCab = concurso.getConfiguracionUoCab();
				if (configuracionUoCab != null)
					sinEntidad = configuracionUoCab.getEntidad().getSinEntidad();
				if (sinEntidad != null)
					nivelEntidad = buscarNivel(sinEntidad.getNenCodigo());
			}
		}
		Date fechaActual = new Date();
		Integer anho = fechaActual.getYear() + 1900;
		String separador = File.separator;
		ubicacionFisica =
			separador
				+ "SICCA"
				+ separador
				+ anho
				+ separador
				+ "OEE"
				+ separador
				+ configuracionUoCab.getIdConfiguracionUo()
				+ separador
				+ concursoPuestoAgr.getConcurso().getDatosEspecificosTipoConc().getIdDatosEspecificos()
				+ separador + concursoPuestoAgr.getConcurso().getIdConcurso() + separador
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		updateDepartamento();
		updateCiudad();
		updateBarrio();
	}
	
	private void muestraBancoPerfiles(){
		
		Query q = em.createQuery("select m from MatrizRefConf m "
				+ "  where m.activo is true and m.concursoPuestoAgr.idConcursoPuestoAgr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr());
		List<MatrizRefConf> l = q.getResultList();
		if(l.isEmpty())
			muestraBtnHomologados = true;
		else
			muestraBtnHomologados = false;
	}

	private void validarOee() {
		/* Incidencia 1014 */
		if (seguridadUtilFormController == null) {
			seguridadUtilFormController =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		}
		seguridadUtilFormController.verificarPerteneceOeeCSI(concurso.getConfiguracionUoCab().getIdConfiguracionUo(), concursoPuestoAgr.getIdConcursoPuestoAgr(), seguridadUtilFormController.estadoActividades("ESTADOS_GRUPO", "INICIADO CIRCUITO")+"", ActividadEnum.CSI_CARGA_GRUPO.getValor());

	}

	@SuppressWarnings("unchecked")
	private void buscarLista() {
		String sql =
			"select det.* " + "from seleccion.concurso_puesto_det det "
				+ "where det.activo is true " + "and det.id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		listaConcursoPuestoDet = em.createNativeQuery(sql, ConcursoPuestoDet.class).getResultList();
	}

	private SinNivelEntidad buscarNivel(BigDecimal nenCodigo) {

		sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nenCodigo);
		nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
		return nivelEntidad;
	}

	/**
	 * Combo Departamento
	 */
	public void updateDepartamento() {
		List<Departamento> dptoList = getDepartamentos();
		departamentosSelecItem = new ArrayList<SelectItem>();
		buildDepartamentoSelectItem(dptoList);
		idDpto = null;
		idCiudad = null;
		idBarrio = null;
	}

	private List<Departamento> getDepartamentos() {
		Long idPaisDir = buscarIdPais();
		if (idPaisDir != null) {
			departamentoList.getPais().setIdPais(idPaisDir);
			departamentoList.setOrder("descripcion");
			departamentoList.setMaxResults(null);
			return departamentoList.getResultList();
		}
		return new ArrayList<Departamento>();
	}

	/**
	 * Busca el id correspondiente a Paraguay
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Long buscarIdPais() {
		String cad = "select p.* from general.pais p where lower(p.descripcion) = 'paraguay'";
		List<Pais> lista = new ArrayList<Pais>();
		lista = em.createNativeQuery(cad, Pais.class).getResultList();
		if (lista.size() > 0)
			return lista.get(0).getIdPais();
		else
			return null;
	}

	private void buildDepartamentoSelectItem(List<Departamento> dptoList) {
		if (departamentosSelecItem == null)
			departamentosSelecItem = new ArrayList<SelectItem>();
		else
			departamentosSelecItem.clear();

		departamentosSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Departamento dep : dptoList) {
			departamentosSelecItem.add(new SelectItem(dep.getIdDepartamento(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo ciudad
	 */
	public void updateCiudad() {
		List<Ciudad> ciuList = getCiudadByDpto();
		ciudadSelecItem = new ArrayList<SelectItem>();
		buildCiudadSelectItem(ciuList);
		idCiudad = null;
		idBarrio = null;
	}

	private List<Ciudad> getCiudadByDpto() {
		if (idDpto != null) {
			ciudadList.getDepartamento().setIdDepartamento(idDpto);
			ciudadList.setMaxResults(null);
			return ciudadList.getResultList();
		}
		return new ArrayList<Ciudad>();
	}

	private void buildCiudadSelectItem(List<Ciudad> ciudadList) {
		if (ciudadSelecItem == null)
			ciudadSelecItem = new ArrayList<SelectItem>();
		else
			ciudadSelecItem.clear();

		ciudadSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Ciudad dep : ciudadList) {
			ciudadSelecItem.add(new SelectItem(dep.getIdCiudad(), dep.getDescripcion()));
		}
	}

	/**
	 * Combo Barrio
	 */
	public void updateBarrio() {
		List<Barrio> barList = getBarriosByCiudad();
		barrioSelecItem = new ArrayList<SelectItem>();
		buildBarrioSelectItem(barList);
		idBarrio = null;
	}

	private List<Barrio> getBarriosByCiudad() {
		if (idCiudad != null) {
			barrioList.setIdCiudad(idCiudad);
			barrioList.setOrder("descripcion");
			barrioList.setMaxResults(null);
			return barrioList.getResultList();
		}
		return new ArrayList<Barrio>();
	}

	private void buildBarrioSelectItem(List<Barrio> barrioList) {
		if (barrioSelecItem == null)
			barrioSelecItem = new ArrayList<SelectItem>();
		else
			barrioSelecItem.clear();

		barrioSelecItem.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (Barrio bar : barrioList) {
			barrioSelecItem.add(new SelectItem(bar.getIdBarrio(), bar.getDescripcion()));
		}
	}

	/**
	 * Método que obtiene el codigo del puesto, concatenado con la unidad organizativa cabeza unidad organizativa dependiente, entidad, nivel, orden
	 * 
	 * @param det
	 * @return
	 */
	public String obtenerCodigo(PlantaCargoDet det) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = det.getConfiguracionUoDet();
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";

		}
		if (det.getContratado())
			code = code + "C";
		if (det.getPermanente())
			code = code + "P";
		code = code + det.getOrden();
		return code;
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet, List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);
		else {
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getOrden());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getEntCodigo().intValue());
			listCodigos.add(0, uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad().getNenCodigo().intValue());
		}
		return listCodigos;
	}

	@SuppressWarnings("unchecked")
	public String obtenerCategoria(PlantaCargoDet det) {
		if (det != null) {
			String cad =
				"select pago.* from planificacion.puesto_concepto_pago pago "
					+ "where pago.obj_codigo = 111 " + "and pago.id_planta_cargo_det = "
					+ det.getIdPlantaCargoDet();

			List<PuestoConceptoPago> lista =
				em.createNativeQuery(cad, PuestoConceptoPago.class).getResultList();
			if (lista.size() > 0)
				return lista.get(0).getCategoria();
		}
		return "Sin Categoria";
	}

	@SuppressWarnings("unchecked")
	public void search() {
		String sql =
			"select * from seleccion.concurso_puesto_det det "
				+ "join seleccion.concurso_puesto_agr agr  "
				+ "on agr.id_concurso_puesto_agr = det.id_concurso_puesto_agr "
				+ "join planificacion.planta_cargo_det cargo_det "
				+ "on cargo_det.id_planta_cargo_det = det.id_planta_cargo_det ";
		String where = " where agr.id_concurso_puesto_agr = " + idConcursoPuestoAgr;
		if (idBarrio != null || idCiudad != null || idDpto != null)
			sql =
				sql + "join planificacion.oficina oficina  "
					+ "on oficina.id_oficina = cargo_det.id_oficina ";
		if (modalidadOcupacion != null && modalidadOcupacion.getValor() != null) {
			if (modalidadOcupacion.getValor().equals("CONTRATADO"))
				where = where + " and cargo_det.contratado is true ";
			if (modalidadOcupacion.getValor().equals("PERMANENTE"))
				where = where + " and cargo_det.permanente is true ";
		}

		if (idDpto != null) {
			sql =
				sql
					+ " join general.ciudad ciudad on ciudad.id_ciudad = oficina.id_ciudad "
					+ "join general.departamento dpto on dpto.id_departamento = ciudad.id_departamento ";
			where = where + " and dpto.id_departamento = " + idDpto;
		}
		if (idCiudad != null)
			where = where + " and ciudad.id_ciudad = " + idCiudad;
		if (idBarrio != null) {
			sql = sql + " join general.barrio barrio on barrio.id_barrio = oficina.id_barrio ";
			where = where + " and barrio.id_barrio = " + idBarrio;
		}

		listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
		listaConcursoPuestoDet =
			em.createNativeQuery(sql + where, ConcursoPuestoDet.class).getResultList();
	}

	public void searchAll() {
		listaConcursoPuestoDet = new ArrayList<ConcursoPuestoDet>();
		listaConcursoPuestoDet = concursoPuestoAgr.getConcursoPuestoDets();
		idBarrio = null;
		idCiudad = null;
		idDpto = null;
		modalidadOcupacion = null;
	}

	public String nextTask() {

		ConcursoPuestoAgr concursoPuestoAgr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);

		if (!validate(concursoPuestoAgr)) {
			return null;
		}

		// Se actualiza el estado
		Referencias referencias =
			referenciasUtilFormController.getReferencia("ESTADOS_GRUPO", "FINALIZADO CARGA");
		concursoPuestoAgr.setEstado(referencias.getValorNum());
		em.merge(concursoPuestoAgr);

		// Se actualiza los estados de las plantas
		EstadoDet estadoDet =
			seleccionUtilFormController.buscarEstadoDet("CONCURSO", "FINALIZADO CARGA");
		List<ConcursoPuestoDet> lista = concursoPuestoAgr.getConcursoPuestoDets();
		if (lista != null && lista.size() > 0) {
			for (ConcursoPuestoDet concursoPuestoDet : lista) {
				PlantaCargoDet plantaCargoDet = concursoPuestoDet.getPlantaCargoDet();
				plantaCargoDet.setEstadoDet(estadoDet);
				em.merge(plantaCargoDet);

				concursoPuestoDet.setEstadoDet(estadoDet);
				em.merge(concursoPuestoDet);
			}
		}

		// Se pasa a la siguiente tarea
		jbpmUtilFormController.setConcursoPuestoAgr(concursoPuestoAgr);
		jbpmUtilFormController.setActividadActual(ActividadEnum.CSI_CARGA_GRUPO);

		if (concursoPuestoAgr.getHomologar() == null || concursoPuestoAgr.getHomologar()) { // Se asume que por defecto se envie a homologacion si no hay nada cargado
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_ENVIAR_HOMOLOGACION);
			jbpmUtilFormController.setTransition("next");
		} else {
			jbpmUtilFormController.setActividadSiguiente(ActividadEnum.CSI_MODIFICAR_DATOS_CONCURSO);
			jbpmUtilFormController.setTransition("next2");
		}

		jbpmUtilFormController.nextTask();

		ConcursoPuestoAgr agr = new ConcursoPuestoAgr();
		agr = em.find(ConcursoPuestoAgr.class, idConcursoPuestoAgr);
		Observacion obs = new Observacion();
		obs.setConcurso(agr.getConcurso());
		obs.setFechaAlta(new Date());
		obs.setUsuAlta(usuarioLogueado.getCodigoUsuario());

		Long idTaskInstance =
			jbpmUtilFormController.getIdTaskInstanceActual(agr.getIdProcessInstance());
		obs.setIdTaskInstance(idTaskInstance);

		if (observaciones != null && !observaciones.trim().isEmpty())
			obs.setObservacion(observaciones);
		try {
			em.persist(obs);
			em.flush();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return "nextTask";
	}

	/**
	 * Validacion
	 * 
	 * @return
	 */
	private boolean validate(ConcursoPuestoAgr concursoPuestoAgr) {

		String mensaje = "";

		if (!existeDatosGrupoPuestos(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Datos del Grupo de Puestos.";
		} else if (!existeFunciones(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo() + "' no tiene Funciones.";
		} else if (!existeRequisitos(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo() + "' no tiene Requisitos.";
		} else if (!existeConceptoPagos(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo() + "' no tiene Concepto de Pago.";
		} else if (!existeCondicionTrabajoGral(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Condiciones de Trabajo Generales.";
		} else if (!existeCondicionTrabajoEspecifico(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Condiciones de Trabajo Específicas.";
		} else if (!existeCondicionSeguridad(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo()
					+ "' no tiene Condiciones de Seguridad.";
		} else if (!existeMatrizReferencial(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo() + "' no tiene Matríz Referencial.";
		} else if (!existeMatrizDocumental(concursoPuestoAgr)) {
			mensaje =
				"No puede pasar a la Siguiente Tarea porque el Grupo '"
					+ concursoPuestoAgr.getDescripcionGrupo() + "' no tiene Matríz Documental.";
		} else {
			Concurso c = em.find(Concurso.class, concursoPuestoAgr.getConcurso().getIdConcurso());
			if (c.getPcd() != null && !c.getPcd()) {
				if (existeDatosGrupoPuestosNullPCD(concursoPuestoAgr)) {
					mensaje =
						"No puede pasar a la Siguiente Tarea porque el Grupo '"
							+ concursoPuestoAgr.getDescripcionGrupo()
							+ "' no tiene Preferencia Personas con Discapacidad.";
				}
			}
		}

		if (!Utiles.vacio(mensaje)) {
			// SEAM ERROR
			statusMessages.clear();
			statusMessages.add(Severity.ERROR, mensaje);
			return false;
		}

		return true;
	}

	/**
	 * Verifica que existan datos del grupo de puestos para el grupo
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeDatosGrupoPuestos(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			"SELECT * FROM seleccion.datos_grupo_puesto where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan funciones para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeFunciones(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM planificacion.det_contenido_funcional "
				+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan requisitos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeRequisitos(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM planificacion.det_req_min " + " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan concepto de pagos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeConceptoPagos(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM seleccion.grupo_concepto_pago " + " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan condiciones de trabajos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeCondicionTrabajoGral(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM planificacion.det_condicion_trabajo "
				+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan condiciones de trabajos especificos para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeCondicionTrabajoEspecifico(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM planificacion.det_condicion_trabajo_especif "
				+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que existan condiciones de seguridad para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeCondicionSeguridad(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM planificacion.det_condicion_segur "
				+ " where id_concurso_puesto_agr = " + concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que exista matriz referencial para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeMatrizReferencial(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM seleccion.matriz_ref_conf " + " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr() + " and tipo = 'GRUPO' ";
		return seleccionUtilFormController.existeNative(query);
	}

	/**
	 * Verifica que exista matriz documental para el grupo de puestos
	 * 
	 * @param concursoPuestoAgr
	 * @return
	 */
	private boolean existeMatrizDocumental(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			" SELECT * FROM seleccion.matriz_doc_config_enc " + " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr();
		return seleccionUtilFormController.existeNative(query);
	}

	private boolean existeDatosGrupoPuestosNullPCD(ConcursoPuestoAgr concursoPuestoAgr) {
		String query =
			"SELECT * FROM seleccion.datos_grupo_puesto " + " where id_concurso_puesto_agr = "
				+ concursoPuestoAgr.getIdConcursoPuestoAgr()
				+ " and preferencia_pers_discapacidad is null";
		return seleccionUtilFormController.existeNative(query);
	}

	public String usarBancoHomologados() {
		String resultado = null;
		try {

			if (valorRadioButton != null) {
				if (valorRadioButton.equals("si")) {
					concursoPuestoAgr.setHomologar(true);
					resultado = "ir";
				}
				if (valorRadioButton.equals("no")) {
					concursoPuestoAgr.setHomologar(false);
					resultado = "ir";
				}
				em.merge(concursoPuestoAgr);
				em.flush();

			}
		} catch (Exception e) {
			return null;
		}
		return resultado;
	}

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	public Long getIdDpto() {
		return idDpto;
	}

	public void setIdDpto(Long idDpto) {
		this.idDpto = idDpto;
	}

	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}

	public Long getIdBarrio() {
		return idBarrio;
	}

	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}

	public ModalidadOcupacion getModalidadOcupacion() {
		return modalidadOcupacion;
	}

	public void setModalidadOcupacion(ModalidadOcupacion modalidadOcupacion) {
		this.modalidadOcupacion = modalidadOcupacion;
	}

	public Long getIdConcursoPuestoAgr() {
		return idConcursoPuestoAgr;
	}

	public void setIdConcursoPuestoAgr(Long idConcursoPuestoAgr) {
		this.idConcursoPuestoAgr = idConcursoPuestoAgr;
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	public List<ConcursoPuestoDet> getListaConcursoPuestoDet() {
		return listaConcursoPuestoDet;
	}

	public void setListaConcursoPuestoDet(List<ConcursoPuestoDet> listaConcursoPuestoDet) {
		this.listaConcursoPuestoDet = listaConcursoPuestoDet;
	}

	public List<SelectItem> getDepartamentosSelecItem() {
		return departamentosSelecItem;
	}

	public void setDepartamentosSelecItem(List<SelectItem> departamentosSelecItem) {
		this.departamentosSelecItem = departamentosSelecItem;
	}

	public List<SelectItem> getCiudadSelecItem() {
		return ciudadSelecItem;
	}

	public void setCiudadSelecItem(List<SelectItem> ciudadSelecItem) {
		this.ciudadSelecItem = ciudadSelecItem;
	}

	public List<SelectItem> getBarrioSelecItem() {
		return barrioSelecItem;
	}

	public void setBarrioSelecItem(List<SelectItem> barrioSelecItem) {
		this.barrioSelecItem = barrioSelecItem;
	}

	public String getValorRadioButton() {
		return valorRadioButton;
	}

	public void setValorRadioButton(String valorRadioButton) {
		this.valorRadioButton = valorRadioButton;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getUbicacionFisica() {
		return ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	public Boolean getMuestraBtnHomologados() {
		return muestraBtnHomologados;
	}

	public void setMuestraBtnHomologados(Boolean muestraBtnHomologados) {
		this.muestraBtnHomologados = muestraBtnHomologados;
	}
}
