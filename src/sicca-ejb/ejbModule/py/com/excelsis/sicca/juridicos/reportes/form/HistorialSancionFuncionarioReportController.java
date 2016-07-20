package py.com.excelsis.sicca.juridicos.reportes.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("historialSancionFuncionarioReportController")
public class HistorialSancionFuncionarioReportController implements
		Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;

	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private SinNivelEntidad nivelEntidadUsu = new SinNivelEntidad();
	private SinEntidad sinEntidadUsu = new SinEntidad();
	private Entidad entidadUsu = new Entidad();
	private ConfiguracionUoCab configuracionUoCabUsu = new ConfiguracionUoCab();

	private Persona persona = new Persona();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Long idPais;
	private Integer anhoActual;
	private Date fechaDesde;
	private Date fechaHasta;

	private List<EmpleadoPuesto> listaEmpleados;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		Date fechaActual = new Date();
		anhoActual = fechaActual.getYear() + 1900;
		idPais = idParaguay();
		listaEmpleados = new ArrayList<EmpleadoPuesto>();
		buscarDatosAsociadosUsuario();
		if (idSinNivelEntidad != null)
			nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad != null)
			sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);

	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			configuracionUoCabUsu = configuracionUoCab;
			if (configuracionUoCab.getEntidad() != null) {
				entidad = configuracionUoCab.getEntidad();
				entidadUsu = entidad;
			}
			sinEntidad = entidad.getSinEntidad();
			sinEntidadUsu = sinEntidad;
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
			nivelEntidadUsu = nivelEntidad;
		}
	}

	@SuppressWarnings("unchecked")
	private Long idParaguay() {
		List<Pais> p = em.createQuery(
				" Select p from Pais p"
						+ " where lower(p.descripcion) like 'paraguay'")
				.getResultList();
		if (!p.isEmpty())
			return p.get(0).getIdPais();

		return null;
	}

	public void limpiar() {
		persona = new Persona();
		listaEmpleados = new ArrayList<EmpleadoPuesto>();
		idConfigCab = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
		nivelEntidad = new SinNivelEntidad();
		sinEntidad = new SinEntidad();
		configuracionUoCab = new ConfiguracionUoCab();
		idPais = idParaguay();
	}

	@SuppressWarnings("unchecked")
	public void search() {
		listaEmpleados = new ArrayList<EmpleadoPuesto>();

		listaEmpleados = em.createNativeQuery(obtenerSql(),
				EmpleadoPuesto.class).getResultList();

	}

	private String obtenerSql() {
		String sql = "select distinct(e.*) "
				+ "from sinarh.sin_nivel_entidad nivel, general.empleado_puesto e "
				+ "join general.persona p " + "on p.id_persona = e.id_persona "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_planta_cargo_det = e.id_planta_cargo_det ";
		String where = " where 1=1";
		if (idSinNivelEntidad != null) {
			sql += " join planificacion.configuracion_uo_det uo_det "
					+ "on uo_det.id_configuracion_uo_det = puesto.id_configuracion_uo_det "
					+ "join planificacion.configuracion_uo_cab oee "
					+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo "
					+ "join planificacion.entidad ent "
					+ "on ent.id_entidad = oee.id_entidad "
					+ "join sinarh.sin_entidad sin_ent "
					+ "on sin_ent.id_sin_entidad = ent.id_sin_entidad";
			where += " and sin_ent.id_sin_entidad = " + idSinEntidad
					+ " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo()
					+ " and nivel.nen_codigo = sin_ent.nen_codigo "
					+ "and nivel.ani_aniopre = sin_ent.ani_aniopre "
					+ "and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();
		}

		if (persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty())
			where += " and p.documento_identidad = '"
					+ persona.getDocumentoIdentidad() + "'";

		return sql + where;
	}

	private Boolean esAdministrador() {
		String sentencia = "select r.* from seguridad.rol r "
				+ "join seguridad.usuario_rol ur " + "on ur.id_rol = r.id_rol "
				+ "join seguridad.usuario u "
				+ "on u.id_usuario = ur.id_usuario" + " where u.id_usuario = "
				+ usuarioLogueado.getIdUsuario();
		List<Rol> lista = em.createNativeQuery(sentencia, Rol.class)
				.getResultList();
		for (Rol o : lista) {
			if (o.getHomologador())
				return true;
		}
		return false;
	}

	private Boolean validoImpresion() {
		if (nivelEntidad == null
				|| !nivelEntidad.getIdSinNivelEntidad().equals(
						nivelEntidadUsu.getIdSinNivelEntidad())
				|| sinEntidad == null
				|| !sinEntidad.getIdSinEntidad().equals(
						sinEntidadUsu.getIdSinEntidad())
				|| configuracionUoCab == null
				|| !configuracionUoCabUsu.getIdConfiguracionUo().equals(
						configuracionUoCab.getIdConfiguracionUo()))
			return false;
		return true;
	}

	/**
	 * Es llamado desde el boton Imprimir
	 * 
	 * @throws Exception
	 */
	public void print() throws Exception {

		if (!esAdministrador() && !validoImpresion()) {
			buscarDatosAsociadosUsuario();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_WARN,
									"El OEE especificado no corresponde al usuario logueado...",
									"No hay datos..."));
			return;
		}
		if (fechaDesde != null && fechaHasta != null
				&& fechaDesde.after(fechaHasta)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Las fechas ingresadas no son v\u00E1lidas");

		}
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		if (persona != null && persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty()) {
			if (!sufc.validarInput(persona.getDocumentoIdentidad().toString(),
					TiposDatos.STRING.getValor(), null))
				return;
		}

		List<Object[]> lista = consulta();
		if (lista == null || lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
		Connection conn = JpaResourceBean.getConnection();
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("id_sin_entidad", new Long(obj[0].toString()));
			if (obj[1] != null)
				map.put("cod_nivel", new Long(obj[1].toString()));
			if (obj[2] != null)
				map.put("nivel", obj[2].toString());
			if (obj[3] != null)
				map.put("cod_entidad", obj[3].toString());
			if (obj[4] != null)
				map.put("entidad", obj[4].toString());

			if (obj[5] != null)
				map.put("cod_oee", obj[5].toString());
			if (obj[6] != null)
				map.put("desc_oee", obj[6].toString());
			if (obj[7] != null)
				map.put("ci", obj[7].toString());
			if (obj[8] != null)
				map.put("nombres", obj[8].toString());

			if (obj[9] != null)
				map.put("pais", obj[9].toString());

			if (obj[10] != null)
				map.put("id", new Long(obj[10].toString()));
			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU457_SANCIONES_FUNCIONARIO", false,
				listaDatosReporte, param);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * Es llamado desde el boton Imprimir
	 */
	public void printLink(Integer row) {

		EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
		empleadoPuesto = listaEmpleados.get(row);
		List<Object[]> lista = consultaLink(empleadoPuesto
				.getIdEmpleadoPuesto());
		if (lista == null || lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No existen datos...", "No hay datos..."));
			return;
		}
		Connection conn = JpaResourceBean.getConnection();
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);

		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("id_sin_entidad", new Long(obj[0].toString()));
			if (obj[1] != null)
				map.put("cod_nivel", new Long(obj[1].toString()));
			if (obj[2] != null)
				map.put("nivel", obj[2].toString());
			if (obj[3] != null)
				map.put("cod_entidad", obj[3].toString());
			if (obj[4] != null)
				map.put("entidad", obj[4].toString());

			if (obj[5] != null)
				map.put("cod_oee", obj[5].toString());
			if (obj[6] != null)
				map.put("desc_oee", obj[6].toString());

			if (obj[7] != null)
				map.put("ci", obj[7].toString());
			if (obj[8] != null)
				map.put("nombres", obj[8].toString());
			if (obj[9] != null)
				map.put("pais", obj[9].toString());

			if (obj[10] != null)
				map.put("id", new Long(obj[10].toString()));

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU457_SANCIONES_FUNCIONARIO", false,
				listaDatosReporte, param);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consulta() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String cad = "SELECT distinct(sin_entidad.id_sin_entidad) as id_sin_entidad, nivel.nen_codigo as cod_nivel,"
				+ " nivel.nen_nombre as nivel,"
				+ " nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "sin_entidad.ent_nombre as entidad, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee.orden as cod_oee, "
				+ "OEE.DENOMINACION_UNIDAD AS DESC_OEE, "
				+ "PERSONA.DOCUMENTO_IDENTIDAD AS CI, "
				+ "PERSONA.NOMBRES || ' ' || "
				+ "PERSONA.APELLIDOS AS NOMBRES, "
				+ "PAIS.DESCRIPCION AS PAIS, PERSONA.ID_PERSONA AS ID "
				+ "FROM sinarh.sin_nivel_entidad nivel, "
				+ "JURIDICOS.SUMARIO_CAB SUM "
				+ "JOIN GENERAL.PERSONA PERSONA "
				+ "ON PERSONA.ID_PERSONA = SUM.ID_PERSONA "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE_SUM "
				+ "ON OEE_sum.ID_CONFIGURACION_UO = SUM.ID_CONFIGURACION_UO "
				+ "JOIN JURIDICOS.SANCION SA ON SA.ID_SANCION = SUM.ID_SANCION_M "
				+ "JOIN GENERAL.PAIS ON PAIS.ID_PAIS = PERSONA.ID_PAIS_EXPEDICION_DOC "
				+ "JOIN GENERAL.EMPLEADO_PUESTO EMP ON PERSONA.ID_PERSONA = EMP.ID_PERSONA "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET P ON "
				+ "P.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "
				+ "ON UO.ID_CONFIGURACION_UO_DET = P.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+ "ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "left join planificacion.entidad entidad "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "join sinarh.sin_entidad sin_entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre ";

		if (persona.getDocumentoIdentidad() != null
				&& !persona.getDocumentoIdentidad().trim().isEmpty())
			cad += " and persona.documento_identidad = '"
					+ seguridadUtilFormController.caracterInvalido(persona
							.getDocumentoIdentidad().toLowerCase()) + "'";
		if (idSinEntidad != null)
			cad += " and sin_entidad.id_sin_entidad = " + idSinEntidad;
		if (configuracionUoCab.getIdConfiguracionUo() != null)
			cad += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();
		if (nivelEntidad.getIdSinNivelEntidad() != null)
			cad += " and nivel.id_sin_nivel_entidad = "
					+ nivelEntidad.getIdSinNivelEntidad();

		if (idPais != null)
			cad += " and PAIS.ID_PAIS = " + idPais;
		if (fechaDesde != null && fechaHasta != null) {

			cad += " and date(SUM.fecha_alta) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";

			cad += " and date(SUM.fecha_alta) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		}
		cad += " order by nivel.nen_codigo, cod_entidad, cod_oee, desc_oee";

		try {

			List<Object[]> config = em.createNativeQuery(cad).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * busca los datos para ser enviados en el reporte
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Object[]> consultaLink(Long id) {
		String cad = "SELECT distinct(sin_entidad.id_sin_entidad) as id_sin_entidad, nivel.nen_codigo as cod_nivel, "
				+ "nivel.nen_nombre as nivel, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad,  "
				+ "sin_entidad.ent_nombre as entidad, oee.id_configuracion_uo as id_oee, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee.orden as cod_oee,  "
				+ "oee.denominacion_unidad as desc_oee, PERSONA.ID_PERSONA as id,"
				+ "CASE WHEN EMP.ACTUAL IS TRUE THEN 'SI' ELSE 'NO' END as actual, "
				+ "PERSONA.DOCUMENTO_IDENTIDAD as ci, PERSONA.NOMBRES || ' ' || PERSONA.APELLIDOS as nombres, "
				+ "PUESTO.DESCRIPCION as puesto, "
				+ "UO.DENOMINACION_UNIDAD as unidad_organizativa, UO.ID_CONFIGURACION_UO_DET as id_uo "
				+ "FROM sinarh.sin_nivel_entidad nivel, "
				+ "GENERAL.EMPLEADO_PUESTO EMP "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "
				+ "ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET  "
				+ "JOIN GENERAL.PERSONA PERSONA "
				+ "ON EMP.ID_PERSONA = PERSONA.ID_PERSONA "
				+ "JOIN  PLANIFICACION.CONFIGURACION_UO_DET UO "
				+ "ON  UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+ "ON OEE.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO "
				+ "join planificacion.entidad entidad "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "join sinarh.sin_entidad sin_entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre "
				+ "and emp.id_empleado_puesto = " + id;

		cad += " order by nivel.nen_codigo, cod_entidad, cod_oee, desc_oee";

		try {

			List<Object[]> config = em.createNativeQuery(cad).getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/**
	 * Método que obtiene el codigo del puesto
	 */
	public String obtenerCodigoUnidad(Long id) {
		ConfiguracionUoDet uoDet = new ConfiguracionUoDet();
		uoDet = em.find(ConfiguracionUoDet.class, id);
		String code = "";
		code += findCodNivelEntidad(uoDet.getConfiguracionUoCab().getEntidad()
				.getSinEntidad().getNenCodigo())
				+ ".";
		code += uoDet.getConfiguracionUoCab().getEntidad().getSinEntidad()
				.getEntCodigo()
				+ ".";
		code += uoDet.getConfiguracionUoCab().getOrden();
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += "." + codigo;
		}

		return code;
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public String findCodNivelEntidad(BigDecimal nencod) {
		SinNivelEntidad nivel = new SinNivelEntidad();
		if (nencod != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(nencod);
			nivel = sinNivelEntidadList.nivelEntidadMaxAnho();
		} else
			return "";
		return nivel.getNenCodigo() + "";
	}

	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Método que busca la entidad correspondiente al codigo ingresado y el
	 * nivel
	 */
	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null
				&& sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setEntCodigo(
					sinEntidad.getEntCodigo());
			sinEntidadList.getSinEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			sinEntidad = sinEntidadList.entidadMaxAnho();

			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
				idSinEntidad = sinEntidad.getIdSinEntidad();
				entidadList.getSinEntidad().setIdSinEntidad(
						sinEntidad.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
			} else {
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		}
	}

	/**
	 * Método que obtiene el codigo correspondiente a una Unidad Organizativa
	 * 
	 * @param uoDet
	 * @return
	 */
	public String obtenerCodigo(ConfiguracionUoDet uoDet) {
		String code = "";
		List<Integer> listCodes = obtenerListaCodigos(uoDet, null);
		for (Integer codigo : listCodes) {
			code += codigo + ".";
		}
		code = code.substring(0, code.length() - 1);
		return code;
	}

	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo
	 * ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {
		if (configuracionUoCab != null && configuracionUoCab.getOrden() != null) {

			configuracionUoCab = obtenerOeeCorrespondiente();
			if (configuracionUoCab != null)
				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
			else {
				configuracionUoCab = new ConfiguracionUoCab();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else
			configuracionUoCab = new ConfiguracionUoCab();
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoCab obtenerOeeCorrespondiente() {
		String sql = "select uo_cab.*  "
				+ "from planificacion.configuracion_uo_cab uo_cab "
				+ "join planificacion.entidad entidad "
				+ "on entidad.id_entidad = uo_cab.id_entidad "
				+ "join sinarh.sin_entidad sin_ent "
				+ "on sin_ent.id_sin_entidad = entidad.id_sin_entidad "
				+ "where  sin_ent.id_sin_entidad = "
				+ sinEntidad.getIdSinEntidad() + " and uo_cab.orden = "
				+ configuracionUoCab.getOrden() + " and sin_ent.ani_aniopre = "
				+ anhoActual;
		List<ConfiguracionUoCab> lista = new ArrayList<ConfiguracionUoCab>();
		lista = em.createNativeQuery(sql, ConfiguracionUoCab.class)
				.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoDet padre,
			Integer orden) {
		if (padre != null) {
			String cad = "select det.* from planificacion.configuracion_uo_det det "
					+ " where det.id_configuracion_uo_det_padre = "
					+ padre.getIdConfiguracionUoDet()
					+ " and det.orden = "
					+ orden;
			List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
			lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
					.getResultList();
			ConfiguracionUoDet actual = new ConfiguracionUoDet();
			if (lista.size() > 0) {
				actual = lista.get(0);
				return actual;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoCab padre,
			Integer orden) {
		String cad = "select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo = "
				+ padre.getIdConfiguracionUo()
				+ " and det.orden = "
				+ orden
				+ " and det.id_configuracion_uo_det_padre is null";
		List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
		lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
				.getResultList();
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (lista.size() > 0) {
			actual = lista.get(0);
			return actual;
		}
		return null;
	}

	/**
	 * Metodo que llama al buscador de entidades
	 * 
	 * @return
	 */
	public String getUrlToPageEntidad() {

		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=juridicos/reportes/EmpleadoPuestoList&codigoNivel="
				+ nivelEntidad.getNenCodigo();
	}

	/**
	 * Método que llama al buscador de OEE's
	 * 
	 * @return
	 */
	public String getUrlToPageOee() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=juridicos/reportes/EmpleadoPuestoList&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		retorno += "&anho=" + anhoActual;
		return retorno;
	}

	/**
	 * Método que llama al buscador de Unidades Organizativas Dependientes
	 * 
	 * @return
	 */
	public String getUrlToPageDependencia() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);

		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (configuracionUoCab == null
				|| configuracionUoCab.getIdConfiguracionUo() == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
			return null;
		}
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=juridicos/reportes/EmpleadoPuestoList&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
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

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	public Integer getAnhoActual() {
		return anhoActual;
	}

	public void setAnhoActual(Integer anhoActual) {
		this.anhoActual = anhoActual;
	}

	public List<EmpleadoPuesto> getListaEmpleados() {
		return listaEmpleados;
	}

	public void setListaEmpleados(List<EmpleadoPuesto> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public SinNivelEntidad getNivelEntidadUsu() {
		return nivelEntidadUsu;
	}

	public void setNivelEntidadUsu(SinNivelEntidad nivelEntidadUsu) {
		this.nivelEntidadUsu = nivelEntidadUsu;
	}

	public SinEntidad getSinEntidadUsu() {
		return sinEntidadUsu;
	}

	public void setSinEntidadUsu(SinEntidad sinEntidadUsu) {
		this.sinEntidadUsu = sinEntidadUsu;
	}

	public Entidad getEntidadUsu() {
		return entidadUsu;
	}

	public void setEntidadUsu(Entidad entidadUsu) {
		this.entidadUsu = entidadUsu;
	}

	public ConfiguracionUoCab getConfiguracionUoCabUsu() {
		return configuracionUoCabUsu;
	}

	public void setConfiguracionUoCabUsu(
			ConfiguracionUoCab configuracionUoCabUsu) {
		this.configuracionUoCabUsu = configuracionUoCabUsu;
	}

}
