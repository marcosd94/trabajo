package py.com.excelsis.sicca.capacitacion.session.form;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.PlantillaConfigurada;
import py.com.excelsis.sicca.entity.PlantillaEncuesta;
import py.com.excelsis.sicca.entity.PreguntaConfigurada;
import py.com.excelsis.sicca.entity.Preguntas;
import py.com.excelsis.sicca.entity.RespuestaMultiple;
import py.com.excelsis.sicca.entity.RespuestaMultipleConfigurada;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.JbpmUtilFormController;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("configurarPlantillaEvalFC")
public class ConfigurarPlantillaEvalFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	@In(create = true)
	JbpmUtilFormController jbpmUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Capacitaciones capacitacion = new Capacitaciones();
	private PlantillaEncuesta plantillaEncuesta = new PlantillaEncuesta();
	private PlantillaConfigurada plantillaConfig = new PlantillaConfigurada();
	private Long idCapacitacion;
	private Long idPlantilla;
	private Long valorRecibido;

	private Boolean estaPlantilla = false;
	private String nombrePlantilla;
	private String cu;

	private List<SelectItem> plantillaSelecItem = new ArrayList<SelectItem>();

	/**
	 * Paso 1: inicializa los parámetros
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (idCapacitacion != null) {
			if (!sufc.validarInput(idCapacitacion.toString(),
					TiposDatos.LONG.getValor(), null))
				return;
			capacitacion = em.find(Capacitaciones.class, idCapacitacion);
			seguridadUtilFormController
					.validarCapacitacionPerteneceUO(idCapacitacion);
			cargarDatosNivel();
		}
		plantillaConfiguradaCorrespondiente();
		nombrePlantilla = plantillaConfig.getNombre();
		cargarComboPlantilla();

	}

	/**
	 * Obtiene la plantilla configurada correspondiente a la capacitacion
	 * recibida como parametro, método llamado desde el paso 1
	 * 
	 * @return
	 */
	private void plantillaConfiguradaCorrespondiente() {
		String sql = "SELECT P.* FROM CAPACITACION.PLANTILLA_CONFIGURADA P WHERE P.ACTIVO IS TRUE AND P.ID_CAPACITACION = "
				+ idCapacitacion +" AND P.VALOR = "+valorRecibido;
		List<PlantillaConfigurada> lista = new ArrayList<PlantillaConfigurada>();
		lista = em.createNativeQuery(sql, PlantillaConfigurada.class)
				.getResultList();
		if (lista != null && lista.size() > 0) {
			estaPlantilla = true;
			plantillaConfig = lista.get(0);
			return;
		}
		estaPlantilla = false;
		return;
	}

	/**
	 * Carga datos del nivel, método llamado desde el paso 1
	 */
	private void cargarDatosNivel() {
		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			if (capacitacion.getConfiguracionUoDet() != null)
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(capacitacion
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}
		cargarCabecera();
	}

	/**
	 * Carga datos de la cabecera, método llamado desde el paso 1
	 */
	public void cargarCabecera() {

		// Nivel
		Long idSinNivelEntidad = nivelEntidadOeeUtil
				.getIdSinNivelEntidad(capacitacion.getConfiguracionUoCab()
						.getEntidad().getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(capacitacion
				.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(capacitacion.getConfiguracionUoCab()
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	/**
	 * Carga el combo Nombre de Plantilla, se carga en el paso 1
	 */
	private void cargarComboPlantilla() {
		String sql = "SELECT P.* FROM CAPACITACION.PLANTILLA_ENCUESTA P "
				+ "WHERE P.ID_CONFIGURACION_UO = "
				+ nivelEntidadOeeUtil.getIdConfigCab()
				+ " AND P.ACTIVO IS TRUE";
		List<PlantillaEncuesta> lista = new ArrayList<PlantillaEncuesta>();
		lista = em.createNativeQuery(sql, PlantillaEncuesta.class)
				.getResultList();
		plantillaSelecItem = new ArrayList<SelectItem>();

		plantillaSelecItem.add(new SelectItem(null, SeamResourceBundle
				.getBundle().getString("opt_select_one")));
		for (PlantillaEncuesta p : lista) {
			plantillaSelecItem.add(new SelectItem(p.getIdPlantilla(), p
					.getNombre()));
			if (p.getNombre().equals(nombrePlantilla))
				idPlantilla = p.getIdPlantilla();
		}
	}
	
	private Boolean cuentaConPreguntas(){
		String sql = "SELECT P.* " +
				"FROM CAPACITACION.PREGUNTAS P " +
				"WHERE P.ACTIVO IS TRUE " +
				"AND P.ID_PLANTILLA = "+idPlantilla;
		List<Preguntas> lista = em.createNativeQuery(sql, Preguntas.class).getResultList();
		if(lista.isEmpty())
			return false;
		return true;
	}

	/**
	 * Paso 2: Guarda/Actualiza datos en las tablas citadas en el ETC
	 * 
	 * @return
	 */
	public String guardar() {
		
		if (idPlantilla == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe completar el campo Nombre de Plantilla antes de realizar esta acción");
			return null;
		}
		if (!cuentaConPreguntas()) {
			statusMessages
					.add(Severity.ERROR,
							"La plantilla seleccionada no posee preguntas. Verifique");
			return null;
		}
		try {
			PlantillaEncuesta plantillaEncuesta = new PlantillaEncuesta();
			plantillaEncuesta = em.find(PlantillaEncuesta.class, idPlantilla);
			// Si es un registro nuevo
			if (!estaPlantilla) {

				PlantillaConfigurada plantillaConfigurada = new PlantillaConfigurada();
				plantillaConfigurada.setActivo(true);
				plantillaConfigurada.setCapacitaciones(capacitacion);
				plantillaConfigurada.setFechaAlta(new Date());
				plantillaConfigurada.setNombre(plantillaEncuesta.getNombre());
				plantillaConfigurada.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				if(cu.equalsIgnoreCase("CU534") && valorRecibido != null)
					plantillaConfigurada.setValor(valorRecibido);
				em.persist(plantillaConfigurada);
				guardarPreguntaConfigurada(plantillaConfigurada);
				em.flush();
			}
			// si es una actualizacion
			if (estaPlantilla) {
				String actual = em.find(PlantillaEncuesta.class, idPlantilla)
						.getNombre();

				if (!nombrePlantilla.equals(actual)) {
					eliminarRelacionados();
					em.remove(plantillaConfig);

					PlantillaConfigurada plantillaConfigurada = new PlantillaConfigurada();
					plantillaConfigurada.setActivo(true);
					plantillaConfigurada.setCapacitaciones(capacitacion);
					plantillaConfigurada.setFechaAlta(new Date());
					plantillaConfigurada.setNombre(plantillaEncuesta
							.getNombre());
					plantillaConfigurada.setUsuAlta(usuarioLogueado
							.getCodigoUsuario());
					em.persist(plantillaConfigurada);
					guardarPreguntaConfigurada(plantillaConfigurada);
					em.flush();
				}
			
			}
		} catch (Exception e) {
			return null;
		}
		statusMessages.add(Severity.INFO, SeamResourceBundle.getBundle()
				.getString("GENERICO_MSG"));
		return "ok";
	}
	
	private String obtenerSql(){
		String sql = "SELECT C.NOMBRE as nombre, DEC.DESCRIPCION as tipo, OEE_C.DENOMINACION_UNIDAD OEE_CAPACITACION, " +
				"DE.DESCRIPCION GRUPO, DE.ID_DATOS_ESPECIFICOS AS ID_GRUPO, PREG.ORDEN as orden, PREG.PREGUNTA as pregunta, " +
				"PREG.id_pregunta_conf as id_pregunta, PREG.tipo_pregunta as tipo_pregunta, " +
				"capacitacion.fnc_resultado_opcion_multiple_reporte(PREG.id_pregunta_conf) as respuesta " +
				"FROM CAPACITACION.PREGUNTA_CONFIGURADA PREG " +
				"JOIN SELECCION.DATOS_ESPECIFICOS DE ON DE.ID_DATOS_ESPECIFICOS = PREG.ID_DATOS_ESPECIFICOS_GRUPO " +
				"LEFT JOIN CAPACITACION.RESPUESTA_MULTIPLE_CONFIGURADA RPTA ON RPTA.ID_PREGUNTA_CONF = PREG.ID_PREGUNTA_CONF " +
				"JOIN CAPACITACION.PLANTILLA_CONFIGURADA PLANT ON PLANT.ID_PLANTILLA_CONF = PREG.ID_PLANTILLA_CONF " +
				"JOIN CAPACITACION.CAPACITACIONES C ON C.ID_CAPACITACION = PLANT.ID_CAPACITACION " +
				"JOIN PLANIFICACION.CONFIGURACION_UO_DET UO_C ON UO_C.ID_CONFIGURACION_UO_DET = C.ID_CONFIGURACION_UO_DET " +
				"JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE_C ON OEE_C.ID_CONFIGURACION_UO = C.ID_CONFIGURACION_UO " +
				"JOIN SELECCION.DATOS_ESPECIFICOS DEC ON DEC.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP " +
				"WHERE PREG.ACTIVO = TRUE AND PLANT.ID_CAPACITACION = "+idCapacitacion
				+" ORDER BY DE.DESCRIPCION, PREG.ORDEN";
		return sql;
	}
	public void pdf() throws Exception {
		Connection conn = JpaResourceBean.getConnection();
		try {

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			if (idCapacitacion != null)
				param.put("sql", obtenerSql());
			
			JasperReportUtils.respondPDF(
					"RPT_CU492_Plantilla_Evaluacion", param, false, conn,
					usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}

	/**
	 * Elimina los registros relacionados en caso de que sea actualización,
	 * método llamado desde el paso 2
	 */
	private void eliminarRelacionados() {
		for (PreguntaConfigurada pc : obtenerPreguntasConfiguradasRelacionadas(plantillaConfig
				.getIdPlantillaConf())) {

			for (RespuestaMultipleConfigurada opcM : pc
					.getRespuestaMultipleConfiguradas())
				em.remove(opcM);
			em.remove(pc);

		}
	}

	/**
	 * Guarda los registros en las tablas relacionadas pregunta_configurada y
	 * respuesta_multiple_configurada
	 * 
	 * @param pl
	 */
	private void guardarPreguntaConfigurada(PlantillaConfigurada pl) {
		for (Preguntas pr : obtenerPreguntasRelacionadas()) {
			boolean tieneRptaMultiple = false;
			PreguntaConfigurada preguntaConfigurada = new PreguntaConfigurada();
			preguntaConfigurada.setActivo(true);
			preguntaConfigurada.setDatosEspecificosGrupo(pr
					.getDatosEspecificosGrupo());
			preguntaConfigurada.setFechaAlta(new Date());
			preguntaConfigurada.setObligatorio(pr.getObligatorio());
			preguntaConfigurada.setOrden(pr.getOrden());
			preguntaConfigurada.setPlantillaConfigurada(pl);
			preguntaConfigurada.setPregunta(pr.getPregunta());
			preguntaConfigurada.setTipoPregunta(pr.getTipoPregunta());
			preguntaConfigurada.setUsuAlta(usuarioLogueado.getCodigoUsuario());
			em.persist(preguntaConfigurada);
			if(pr.getTipoPregunta().equals("M"))
				tieneRptaMultiple = true;
			else
				tieneRptaMultiple = false;
			for (RespuestaMultiple rpta : obtenerRptaMultipleRelacionadas(pr
					.getIdPregunta())) {
			
				RespuestaMultipleConfigurada rptaMultipleConfigurada = new RespuestaMultipleConfigurada();
				rptaMultipleConfigurada.setActivo(true);
				rptaMultipleConfigurada.setFechaAlta(new Date());
				rptaMultipleConfigurada
						.setPreguntaConfigurada(preguntaConfigurada);
				rptaMultipleConfigurada.setRespuestaOpc(rpta.getRespuestaOpc());
				rptaMultipleConfigurada.setUsuAlta(usuarioLogueado
						.getCodigoUsuario());
				em.persist(rptaMultipleConfigurada);
			}
			if(tieneRptaMultiple){
			RespuestaMultipleConfigurada rptaMultipleConfigurada = new RespuestaMultipleConfigurada();
			rptaMultipleConfigurada.setActivo(true);
			rptaMultipleConfigurada.setFechaAlta(new Date());
			rptaMultipleConfigurada.setUsuAlta(usuarioLogueado
					.getCodigoUsuario());
			rptaMultipleConfigurada.setPreguntaConfigurada(preguntaConfigurada);
			rptaMultipleConfigurada.setRespuestaOpc("No respondio");
			em.persist(rptaMultipleConfigurada);
			}
		}
	}

	private List<RespuestaMultiple> obtenerRptaMultipleRelacionadas(Long id) {
		String sql = "SELECT RPTA.* "
				+ "FROM CAPACITACION.RESPUESTA_MULTIPLE RPTA "
				+ "WHERE RPTA.ACTIVO IS TRUE " + "AND RPTA.ID_PREGUNTA = " + id;
		return em.createNativeQuery(sql, RespuestaMultiple.class)
				.getResultList();
	}

	private List<Preguntas> obtenerPreguntasRelacionadas() {
		String sql = "SELECT P.* FROM CAPACITACION.PREGUNTAS P "
				+ "WHERE P.ACTIVO IS TRUE AND P.ID_PLANTILLA = " + idPlantilla;
		return em.createNativeQuery(sql, Preguntas.class).getResultList();
	}

	private List<PreguntaConfigurada> obtenerPreguntasConfiguradasRelacionadas(
			Long id) {
		String sql = "SELECT CONF.* "
				+ "FROM CAPACITACION.PREGUNTA_CONFIGURADA CONF "
				+ "WHERE CONF.ID_PLANTILLA_CONF = " + id;
		return em.createNativeQuery(sql, PreguntaConfigurada.class)
				.getResultList();
	}

	public Capacitaciones getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(Capacitaciones capacitacion) {
		this.capacitacion = capacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public List<SelectItem> getPlantillaSelecItem() {
		return plantillaSelecItem;
	}

	public void setPlantillaSelecItem(List<SelectItem> plantillaSelecItem) {
		this.plantillaSelecItem = plantillaSelecItem;
	}

	public PlantillaEncuesta getPlantillaEncuesta() {
		return plantillaEncuesta;
	}

	public void setPlantillaEncuesta(PlantillaEncuesta plantillaEncuesta) {
		this.plantillaEncuesta = plantillaEncuesta;
	}

	public Long getIdPlantilla() {
		return idPlantilla;
	}

	public void setIdPlantilla(Long idPlantilla) {
		this.idPlantilla = idPlantilla;
	}

	public Boolean getEstaPlantilla() {
		return estaPlantilla;
	}

	public void setEstaPlantilla(Boolean estaPlantilla) {
		this.estaPlantilla = estaPlantilla;
	}

	public PlantillaConfigurada getPlantillaConfig() {
		return plantillaConfig;
	}

	public void setPlantillaConfig(PlantillaConfigurada plantillaConfig) {
		this.plantillaConfig = plantillaConfig;
	}

	public String getNombrePlantilla() {
		return nombrePlantilla;
	}

	public void setNombrePlantilla(String nombrePlantilla) {
		this.nombrePlantilla = nombrePlantilla;
	}

	public String getCu() {
		return cu;
	}

	public void setCu(String cu) {
		this.cu = cu;
	}

	public Long getValorRecibido() {
		return valorRecibido;
	}

	public void setValorRecibido(Long valorRecibido) {
		this.valorRecibido = valorRecibido;
	}
	
	

	
}
