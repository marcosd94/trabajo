package py.com.excelsis.sicca.remuneracion.session.form;

import java.sql.Connection;
import java.util.HashMap;

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

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("rangoSalarialModOcupacionCU729FC")
public class RangoSalarialModOcupacionCU729FC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();

	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Long idConfigCab;
	private Integer desde;
	private Integer hasta;
	private String tipo;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);
			cargarCabecera();
		}
		tipo = "D";
	}

	public void cargarCabecera() {
		nivelEntidadOeeUtil.limpiar();
		buscarDatosAsociadosUsuario();
		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(nivelEntidad
				.getIdSinNivelEntidad());
		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());
		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab
				.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}

	private void buscarDatosAsociadosUsuario() {
		if (usuarioLogueado.getConfiguracionUoCab() != null) {

			configuracionUoCab = new ConfiguracionUoCab();
			Long id = usuarioLogueado.getConfiguracionUoCab()
					.getIdConfiguracionUo();
			configuracionUoCab = em.find(ConfiguracionUoCab.class, id);
			if (configuracionUoCab.getOrden() != null)

				if (configuracionUoCab.getEntidad() != null)
					entidad = configuracionUoCab.getEntidad();
			sinEntidad = entidad.getSinEntidad();
			nivelEntidad.setNenCodigo(sinEntidad.getNenCodigo());
			findNivelEntidad();
		}
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
				idSinNivelEntidad = null;
				return;
			}
		} else {
			nivelEntidad = new SinNivelEntidad();
			idSinNivelEntidad = null;
		}
	}
	
	 boolean validar(){
		if(hasta == null || desde == null){
			statusMessages.add(Severity.ERROR,"Ingrese los datos obligatorios");
			return false;
		}
		if(desde.intValue() > hasta.intValue()){
			statusMessages.add(Severity.ERROR,"El rango salarial desde no puede ser mayor al rango salarial hasta");
			return false;
		}
		return true;
	}

	public void imprimir() throws Exception {
		if(!validar())
			return;
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			if (tipo.equalsIgnoreCase("D"))
				JasperReportUtils.respondPDF(
						"RPT_CU729_Rango_Salarial_Detallado", param, false,
						conn, usuarioLogueado);

			if (tipo.equalsIgnoreCase("R"))
				JasperReportUtils.respondPDF(
						"RPT_CU729_Rango_Salarial_Resumido", param, false,
						conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		Long id = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		param.put("oee", em.find(ConfiguracionUoCab.class, id)
				.getDenominacionUnidad());
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("desde", desde);
		param.put("hasta", hasta);
		param.put("sql", obtenerSql());

		return param;
	}

	private String obtenerSql() {
		String sql = "";
		if (tipo.equalsIgnoreCase("D")) {
			sql = "SELECT DISTINCT SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "
					+ "SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "
					+ "PERSONA.DOCUMENTO_IDENTIDAD CEDULA, PAIS.DESCRIPCION, PERSONA.NOMBRES, PERSONA.APELLIDOS, "
					+ "PUESTO.DESCRIPCION puesto, CASE WHEN E.CONTRATADO = TRUE THEN 'CONTRATADO' ELSE 'PERMANENTE' END MODALIDAD_OCUPACION, "
					+ "remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, PERSONA.ID_PERSONA ) VALOR_ECONOMICO, "
					+ "SIN_ENTIDAD.ENT_CODIGO AS COD_ENT, OEE.ORDEN AS OEE_ORDEN "
					+ "FROM REMUNERACION.REMUNERACIONES R "
					+ "JOIN GENERAL.EMPLEADO_PUESTO E on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO "
					+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "
					+ "JOIN GENERAL.PERSONA PERSONA ON PERSONA.ID_PERSONA = E.ID_PERSONA "
					+ "JOIN GENERAL.PAIS ON PERSONA.ID_PAIS_EXPEDICION_DOC = PAIS.ID_PAIS "
					+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
					+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
					+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
					+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
					+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "
					+ "where 1=1";
			if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
				sql += " AND SNE.NEN_CODIGO = "
						+ em.find(SinNivelEntidad.class,
								nivelEntidadOeeUtil.getIdSinNivelEntidad())
								.getNenCodigo();
			if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
				sql += " AND SIN_ENTIDAD.ENT_CODIGO = "
						+ em.find(SinEntidad.class,
								nivelEntidadOeeUtil.getIdSinEntidad())
								.getEntCodigo();
			if (nivelEntidadOeeUtil.getIdConfigCab() != null)
				sql += " AND OEE.ORDEN = "
						+ em.find(ConfiguracionUoCab.class,
								nivelEntidadOeeUtil.getIdConfigCab())
								.getOrden();
			sql += " group by SNE.NEN_CODIGO , SNE.NEN_NOMBRE , SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO , "
					+ "SIN_ENTIDAD.ENT_NOMBRE ,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN ,OEE.DENOMINACION_UNIDAD, "
					+ "PERSONA.DOCUMENTO_IDENTIDAD, PAIS.DESCRIPCION, "
					+ "PERSONA.NOMBRES, PERSONA.APELLIDOS, PUESTO.DESCRIPCION, E.CONTRATADO, SIN_ENTIDAD.ENT_CODIGO,  "
					+ "OEE.ORDEN, PERSONA.ID_PERSONA";
		}
		if (tipo.equalsIgnoreCase("R")) {
			sql += "SELECT DISTINCT SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "
					+ "SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "
					+ "SIN_ENTIDAD.ENT_CODIGO AS COD_ENT, OEE.ORDEN AS OEE_ORDEN, remuneracion.total_contratado_permanente(SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, true, $P{desde}, $P{hasta}) as contratado, "
					+ "remuneracion.total_contratado_permanente(SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, false, $P{desde}, $P{hasta}) as permanente "
					+ "FROM REMUNERACION.REMUNERACIONES R "
					+ "JOIN GENERAL.EMPLEADO_PUESTO E on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO "
					+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "
					+ "JOIN GENERAL.PERSONA PERSONA ON PERSONA.ID_PERSONA = E.ID_PERSONA "
					+ "JOIN GENERAL.PAIS ON PERSONA.ID_PAIS_EXPEDICION_DOC = PAIS.ID_PAIS "
					+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
					+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
					+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
					+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
					+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO)";
			if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
				sql += " AND SNE.NEN_CODIGO = "
						+ em.find(SinNivelEntidad.class,
								nivelEntidadOeeUtil.getIdSinNivelEntidad())
								.getNenCodigo();
			if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
				sql += " AND SIN_ENTIDAD.ENT_CODIGO = "
						+ em.find(SinEntidad.class,
								nivelEntidadOeeUtil.getIdSinEntidad())
								.getEntCodigo();
			if (nivelEntidadOeeUtil.getIdConfigCab() != null)
				sql += " AND OEE.ORDEN = "
						+ em.find(ConfiguracionUoCab.class,
								nivelEntidadOeeUtil.getIdConfigCab())
								.getOrden();
		}
		return sql;
	}
	
	public void limpiar() {
		sinEntidad = new SinEntidad();
		nivelEntidad = new SinNivelEntidad();
		entidad = new Entidad();
		configuracionUoCab = new ConfiguracionUoCab();
		idConfigCab = null;
		idSinEntidad = null;
		idSinNivelEntidad = null;
		desde = null;
		hasta = null;
		tipo = "D";
		cargarCabecera();

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

	public Integer getDesde() {
		return desde;
	}

	public void setDesde(Integer desde) {
		this.desde = desde;
	}

	public Integer getHasta() {
		return hasta;
	}

	public void setHasta(Integer hasta) {
		this.hasta = hasta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
