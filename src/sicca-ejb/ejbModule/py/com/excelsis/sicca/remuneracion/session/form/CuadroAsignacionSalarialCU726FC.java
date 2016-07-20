package py.com.excelsis.sicca.remuneracion.session.form;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinAnxOriginal;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cuadroAsignacionSalarialCU726FC")
public class CuadroAsignacionSalarialCU726FC {
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
	private String ci;
	private String puesto;
	private String objCodigos;

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
		// OEE
		if (usuarioLogueado.getConfiguracionUoDet() != null) {
			Long id = usuarioLogueado.getConfiguracionUoDet()
					.getIdConfiguracionUoDet();
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());
		}

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

	private static boolean isNumeric(String cadena) {
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	private boolean chequearDatos() {
		if (objCodigos != null && !objCodigos.trim().isEmpty()) {
			String[] campos = objCodigos.split(";");
			if (campos.length == 0)
				return isNumeric(objCodigos.trim());
			else {
				for (int i = 0; i < campos.length; i++) {
					if (!isNumeric(campos[i].trim()))
						return false;
				}
			}
		}
		return true;
	}

	public void imprimir() throws Exception {
		if(!chequearDatos()){
			statusMessages.add(Severity.ERROR,
					"Ingrese valores válidos en el campo Objeto de Gasto");
			return;
		}
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);

			JasperReportUtils.respondPDF("RPT_Asignacion_Salarial_CU726",
					param, false, conn, usuarioLogueado);
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
		param.put("sql", obtenerSql());

		return param;
	}

	private String obtenerSql() {
		String sql = "SELECT DISTINCT SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "
				+ "SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "
				+ "SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) UO_CODIGO, UO.DENOMINACION_UNIDAD, "
				+ "PER.DOCUMENTO_IDENTIDAD CEDULA, PER.NOMBRES, PER.APELLIDOS, PUESTO.DESCRIPCION PUESTO, CASE WHEN E.CONTRATADO = TRUE THEN 'CONTRATADO' ELSE 'PERMANENTE' END MODALIDAD_OCUPACION, "
				+ "R.obj_codigo, R.DESCRIP_CONCEPTO, R.CATEGORIA, r.PRESUPUESTADO, r.DEVENGADO, r.fecha_alta, r.anho||'/'||r.mes as anho_mes "
				+ "FROM REMUNERACION.REMUNERACIONES R "
				+ "JOIN GENERAL.EMPLEADO_PUESTO E on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "
				+ "JOIN GENERAL.PERSONA PER ON PER.ID_PERSONA = E.ID_PERSONA "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD  ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
				+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
				+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "
				+ "WHERE 1=1 ";
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
		if (nivelEntidadOeeUtil.getIdConfigCab() != null) {
			ConfiguracionUoCab oee = new ConfiguracionUoCab();
			oee = em.find(ConfiguracionUoCab.class,
					nivelEntidadOeeUtil.getIdConfigCab());
			sql += " AND OEE.ORDEN = " + oee.getOrden();
		}
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND (SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET))= '"
					+ nivelEntidadOeeUtil.getCodigoUnidOrgDep() + "' ";

		if (ci != null && !ci.trim().isEmpty())
			sql += " AND PER.DOCUMENTO_IDENTIDAD = '" + ci.trim() + "' ";
		if(puesto != null && !puesto.trim().isEmpty())
			sql += " AND lower(PUESTO.DESCRIPCION) LIKE lower('%"+puesto+"%') " ;
		if (objCodigos != null && !objCodigos.trim().isEmpty()) {
			String[] campos = objCodigos.split(";");
			if(campos.length == 0)
				sql += " AND R.obj_codigo = "+objCodigos.trim();
			else{
				sql += " AND R.obj_codigo IN ('";
				for (int i = 0; i < campos.length; i++) {
					sql += campos[i].trim();
					if (i < campos.length - 1)
						sql += "', '";
					else
						sql += "')";
				}
			}
		}

		sql += " ORDER BY NEN_COD, ENT_COD, ORDEN, UO_CODIGO, PER.DOCUMENTO_IDENTIDAD";
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
		ci = null;
		cargarCabecera();

	}

	public void cambiarUO() {
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();

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

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getObjCodigos() {
		return objCodigos;
	}

	public void setObjCodigos(String objCodigos) {
		this.objCodigos = objCodigos;
	}

}
