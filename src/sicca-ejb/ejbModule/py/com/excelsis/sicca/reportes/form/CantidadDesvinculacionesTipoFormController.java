package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
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
import javax.faces.model.SelectItem;
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
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cantidadDesvinculacionesTipoFormController")
public class CantidadDesvinculacionesTipoFormController implements Serializable {

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
	private Long idMotivo;

	private String opcionImpresion;
	private String tipoInforme;

	private Date fechaDesde;
	private Date fechaHasta;

	private List<SelectItem> motivoSelecItem = new ArrayList<SelectItem>();

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
		updatedMotivoSelectItem();
		tipoInforme = "r";
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
				entidad = new Entidad();
				sinEntidad = new SinEntidad();
				idSinEntidad = null;
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else {
			entidad = new Entidad();
			sinEntidad = new SinEntidad();
			idSinEntidad = null;
		}
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
				+ configuracionUoCab.getOrden();
		List<ConfiguracionUoCab> lista = new ArrayList<ConfiguracionUoCab>();
		lista = em.createNativeQuery(sql, ConfiguracionUoCab.class)
				.getResultList();
		if (lista.size() > 0)
			return lista.get(0);
		else
			return null;
	}

	/**
	 * Métodos que cargan los combos a ser desplegados
	 */
	// Combo motivo desvincualcion
	@SuppressWarnings("unchecked")
	public void updatedMotivoSelectItem() {
		String cadena = " SELECT DE.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG "
				+ "ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'MOTIVOS DE DESVINCULACION' "
				+ "AND DE.ACTIVO = TRUE  " + "ORDER BY DE.DESCRIPCION";

		List<DatosEspecificos> lista = new ArrayList<DatosEspecificos>();
		lista = em.createNativeQuery(cadena, DatosEspecificos.class)
				.getResultList();
		motivoSelecItem = new ArrayList<SelectItem>();
		motivoSelecItem.add(new SelectItem(null, "Todos..."));
		if (lista.size() > 0) {

			for (DatosEspecificos datos : lista) {
				motivoSelecItem.add(new SelectItem(datos
						.getIdDatosEspecificos(), datos.getDescripcion()));
			}
		}
	}

	/**
	 * Es llamado desde el imprimir Grafico
	 */
	public void printGrafico() {

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null || nivelEntidadOeeUtil.getIdSinEntidad() == null
				|| nivelEntidadOeeUtil.getIdConfigCab() == null
				|| fechaDesde == null || fechaHasta == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Ingrese los datos obligatorios para el filtro...",
							"No hay datos..."));
			return;
		}
		if (fechaDesde.after(fechaHasta)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Las fechas ingresadas no son v\u00E1lidas");

		}
		List<Object[]> lista = sql();
		if (lista == null || lista.size() == 0) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"No hay datos que mostrar...", "No hay datos..."));
			return;
		}

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		Connection conn = JpaResourceBean.getConnection();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			param.put(
					"entidad",
					nivelEntidad.getNenCodigo() + "."
							+ sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNombre());

		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			param.put(
					"oee",
					nivelEntidad.getNenCodigo() + "."
							+ sinEntidad.getEntCodigo() + "."
							+ configuracionUoCab.getOrden() + " - "
							+ configuracionUoCab.getDenominacionUnidad());
		if (idMotivo != null) {
			DatosEspecificos dato = new DatosEspecificos();
			dato = em.find(DatosEspecificos.class, idMotivo);
			param.put("motivo", dato.getDescripcion());
		}
		if (fechaDesde != null)
			param.put("fecha_desde", fechaDesde);
		if (fechaHasta != null)
			param.put("fecha_hasta", fechaHasta);
		param.put("oee_usuario", usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad().toUpperCase());

		for (Object[] obj : lista) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("descripcion", obj[0].toString());
			if (obj[1] != null)
				map.put("cantidad", new Long(obj[1].toString()));

			listaDatosReporte.add(map);
		}
		JasperReportUtils.respondPDF("RPT_CU444_Cant_Desvinculacion_Tipo",
				false, listaDatosReporte, param);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> sql() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String select = "select de.descripcion as descripcion, count (*) as cantidad  "
				+ "from desvinculacion.desvinculacion des "
				+ "join seleccion.datos_especificos de "
				+ "on de.id_datos_especificos = des.id_datos_especif_motivo "
				+ "join general.empleado_puesto emp "
				+ "on emp.id_empleado_puesto = des.id_empleado_puesto "
				+ "join planificacion.planta_cargo_det cargo "
				+ "on cargo.id_planta_cargo_det = emp.id_planta_cargo_det "
				+ "join planificacion.configuracion_uo_det uo_det "
				+ "on uo_det.id_configuracion_uo_det = cargo.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_configuracion_uo = uo_det.id_configuracion_uo ";
		if (fechaDesde != null)
			select += " where date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			select += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		if (idMotivo != null)
			select += " and de.id_datos_especificos = " + idMotivo;
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			select += " and oee.id_configuracion_uo = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		String order = " group by de.descripcion " + " order by de.descripcion";
		try {

			List<Object[]> config = em.createNativeQuery(select + order)
					.getResultList();
			if (config == null || config.size() == 0) {
				return null;
			}
			return config;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void print() {
		if (fechaDesde == null || fechaHasta == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"El rango de fechas es obligatorio ...",
							"No hay datos..."));
			return;
		}
		
		if (opcionImpresion == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Seleccione el Formato de Salida...",
							"No hay datos..."));
			return;
		}

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		HashMap<String, Object> param = new HashMap<String, Object>();
		Connection conn = JpaResourceBean.getConnection();

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("REPORT_CONNECTION", conn);
		param.put("oee", usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad().toUpperCase());
		if (nivelEntidad != null && nivelEntidad.getIdSinNivelEntidad() != null)
			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());
		if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null)
			param.put(
					"entidad",
					nivelEntidad.getNenCodigo() + "."
							+ sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNombre());

		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			param.put(
					"oee",
					nivelEntidad.getNenCodigo() + "."
							+ sinEntidad.getEntCodigo() + "."
							+ configuracionUoCab.getOrden() + " - "
							+ configuracionUoCab.getDenominacionUnidad());
		if (idMotivo != null) {
			DatosEspecificos dato = new DatosEspecificos();
			dato = em.find(DatosEspecificos.class, idMotivo);
			param.put("motivo", dato.getDescripcion());
			param.put("id_motivo", idMotivo);
		}
		if (idMotivo == null)
			param.put("motivo", "Todos");
		if (fechaDesde != null)
			param.put("fecha_desde", fechaDesde);
		if (fechaHasta != null)
			param.put("fecha_hasta", fechaHasta);
		if (tipoInforme.equals("r")) {

			param.put("sql", sqlPrincipalResumido());

			param.put("sql_secundario", sqlSecundarioResumido());
			if (opcionImpresion.equals("pdf"))
				JasperReportUtils.respondPDF(
						"RPTO_CU444_Cant_Desvinculacion_Tipo_Resumido", param,
						false, conn, usuarioLogueado);
			if (opcionImpresion.equals("xls"))
				JasperReportUtils.respondXLS(
						"RPTO_CU444_Cant_Desvinculacion_Tipo_Resumido", param,
						conn, usuarioLogueado);

		}
		if (tipoInforme.equals("d")) {
			param.put("sql", sqlPrincipalDetallado());
			param.put("sql_secundario", sqlSecundarioDetallado());
			if (opcionImpresion.equals("pdf"))
				JasperReportUtils.respondPDF(
						"RPT_CU444_Cant_Desvinculacion_Tipo_Detallado", param,
						false, conn, usuarioLogueado);
			if (opcionImpresion.equals("xls"))
				JasperReportUtils.respondXLS(
						"RPT_CU444_Cant_Desvinculacion_Tipo_Detallado", param,
						conn, usuarioLogueado);
		}

	}

	private String sqlPrincipalResumido() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String sql = "select distinct (sin_entidad.id_sin_entidad) as id, nivel.nen_codigo as cod_nivel, "
				+ "nivel.nen_nombre as nivel, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "sin_entidad.ent_nombre as entidad "
				+ "FROM sinarh.sin_nivel_entidad nivel, sinarh.sin_entidad sin_entidad "
				+ "join planificacion.entidad entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "join planificacion.configuracion_uo_det uo "
				+ "on uo.id_configuracion_uo = oee.id_configuracion_uo "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_configuracion_uo_det = uo.id_configuracion_uo_det "
				+ "join general.empleado_puesto emp "
				+ "on emp.id_planta_cargo_det = puesto.id_planta_cargo_det "
				+ "join desvinculacion.desvinculacion des "
				+ "on des.id_empleado_puesto = emp.id_empleado_puesto "
				+ "join seleccion.datos_especificos de "
				+ "on de.id_datos_especificos = des.id_datos_especif_motivo "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre ";
		if (idMotivo != null)
			sql += " and de.id_datos_especificos = " + idMotivo;
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			nivelEntidad = em.find(SinNivelEntidad.class,
					nivelEntidadOeeUtil.getIdSinNivelEntidad());
			sql += " and nivel.nen_codigo = " + nivelEntidad.getNenCodigo();
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class,
					nivelEntidadOeeUtil.getIdSinEntidad());
			sql += " and sin_entidad.ent_codigo = " + sinEntidad.getEntCodigo();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " and oee.id_configuracion_uo = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (fechaDesde != null)
			sql += " and date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		String order = " order by  nivel.nen_codigo, cod_entidad";
		sql += order;
		return sql;
	}

	private String sqlSecundarioResumido() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String sqlSecundario = "select nivel.nen_codigo as cod_nivel, nivel.nen_nombre as nivel, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "sin_entidad.ent_nombre as entidad, count(*) as cantidad, de.descripcion as descripcion, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee.orden as cod_oee, "
				+ "oee.denominacion_unidad as desc_oee "
				+ "FROM sinarh.sin_nivel_entidad nivel, sinarh.sin_entidad sin_entidad "
				+ "join planificacion.entidad entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "join planificacion.configuracion_uo_det uo "
				+ "on uo.id_configuracion_uo = oee.id_configuracion_uo "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_configuracion_uo_det = uo.id_configuracion_uo_det "
				+ "join general.empleado_puesto emp "
				+ "on emp.id_planta_cargo_det = puesto.id_planta_cargo_det "
				+ "join desvinculacion.desvinculacion des "
				+ "on des.id_empleado_puesto = emp.id_empleado_puesto "
				+ "join seleccion.datos_especificos de "
				+ "on de.id_datos_especificos = des.id_datos_especif_motivo "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre "
				+ "and sin_entidad.id_sin_entidad = $P{id_entidad} ";
		if (idMotivo != null)
			sqlSecundario += " and de.id_datos_especificos = " + idMotivo;
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			nivelEntidad = em.find(SinNivelEntidad.class,
					nivelEntidadOeeUtil.getIdSinNivelEntidad());
			sqlSecundario += " and nivel.nen_codigo = "
					+ nivelEntidad.getNenCodigo();
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class,
					nivelEntidadOeeUtil.getIdSinEntidad());
			sqlSecundario += " and sin_entidad.ent_codigo = "
					+ sinEntidad.getEntCodigo();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sqlSecundario += " and oee.id_configuracion_uo = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (fechaDesde != null)
			sqlSecundario += " and date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sqlSecundario += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		String order = " group by nivel.nen_nombre, nivel.nen_codigo, sin_entidad.ent_codigo, sin_entidad.ent_nombre, sin_entidad.id_sin_entidad, de.descripcion, "
				+ "oee.orden, oee.denominacion_unidad "
				+ "order by nivel.nen_codigo, sin_entidad.ent_codigo, oee.orden";
		sqlSecundario += order;
		return sqlSecundario;
	}

	private String sqlPrincipalDetallado() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String sql = "select distinct (oee.id_configuracion_uo) as id, nivel.nen_codigo as cod_nivel, "
				+ "nivel.nen_nombre as nivel, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "sin_entidad.ent_nombre as entidad, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee.orden as cod_oee, "
				+ "oee.denominacion_unidad as desc_oee "
				+ "from sinarh.sin_nivel_entidad nivel, sinarh.sin_entidad sin_entidad "
				+ "join planificacion.entidad entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "join planificacion.configuracion_uo_cab oee "
				+ "on oee.id_entidad = entidad.id_entidad "
				+ "join planificacion.configuracion_uo_det uo "
				+ "on uo.id_configuracion_uo = oee.id_configuracion_uo "
				+ "join planificacion.planta_cargo_det puesto "
				+ "on puesto.id_configuracion_uo_det = uo.id_configuracion_uo_det "
				+ "join general.empleado_puesto emp "
				+ "on emp.id_planta_cargo_det = puesto.id_planta_cargo_det "
				+ "join desvinculacion.desvinculacion des "
				+ "on des.id_empleado_puesto = emp.id_empleado_puesto "
				+ "join seleccion.datos_especificos de "
				+ "on de.id_datos_especificos = des.id_datos_especif_motivo "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre";

		if (idMotivo != null)
			sql += " and de.id_datos_especificos = " + idMotivo;
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			nivelEntidad = em.find(SinNivelEntidad.class,
					nivelEntidadOeeUtil.getIdSinNivelEntidad());
			sql += " and nivel.nen_codigo = " + nivelEntidad.getNenCodigo();
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class,
					nivelEntidadOeeUtil.getIdSinEntidad());
			sql += " and sin_entidad.ent_codigo = " + sinEntidad.getEntCodigo();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " and oee.id_configuracion_uo = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (fechaDesde != null)
			sql += " and date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		return sql;
	}

	private String sqlSecundarioDetallado() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String sql = "select persona.nombres ||' '||persona.apellidos as nombres, persona.documento_identidad as ci, "
				+ "des.fecha_desvinculacion as fecha_desvinculacion, de.descripcion as motivo,  "
				+ "case when count(*) > 0 then 'X' end as valor "
				+ "from sinarh.sin_nivel_entidad nivel, "
				+ "desvinculacion.desvinculacion des "
				+ "join seleccion.datos_especificos de on des.id_datos_especif_motivo = de.id_datos_especificos "
				+ "join general.empleado_puesto emp on emp.id_empleado_puesto = des.id_empleado_puesto "
				+ "join planificacion.planta_cargo_det puesto on puesto.id_planta_cargo_det = emp.id_planta_cargo_det "
				+ "join general.persona persona on persona.id_persona = emp.id_persona "
				+ "join planificacion.configuracion_uo_det uo on uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det "
				+ "join planificacion.configuracion_uo_cab oee on oee.id_configuracion_uo = uo.id_configuracion_uo "
				+ "join planificacion.entidad entidad on entidad.id_entidad = oee.id_entidad "
				+ "join sinarh.sin_entidad sin_entidad on sin_entidad.id_sin_entidad = entidad.id_sin_entidad "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo and nivel.ani_aniopre = sin_entidad.ani_aniopre  "
				+ "and oee.id_configuracion_uo = $P{id} ";
		if (idMotivo != null)
			sql += " and de.id_datos_especificos = " + idMotivo;
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null) {
			nivelEntidad = em.find(SinNivelEntidad.class,
					nivelEntidadOeeUtil.getIdSinNivelEntidad());
			sql += " and nivel.nen_codigo = " + nivelEntidad.getNenCodigo();
		}
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null) {
			sinEntidad = em.find(SinEntidad.class,
					nivelEntidadOeeUtil.getIdSinEntidad());
			sql += " and sin_entidad.ent_codigo = " + sinEntidad.getEntCodigo();
		}
		if (fechaDesde != null)
			sql += " and date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		sql += " group by persona.nombres, persona.apellidos, persona.documento_identidad, fecha_desvinculacion, de.descripcion "
				+ " order by persona.documento_identidad, persona.apellidos, persona.apellidos";
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
		cargarCabecera();
		idMotivo = null;
		fechaDesde = null;
		fechaHasta = null;
		updatedMotivoSelectItem();
	}

	public void prepararImpresion() {

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
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=reports/reportes/CantidadDesvinculacionesTipo&codigoNivel="
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
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=reports/reportes/CantidadDesvinculacionesTipo&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		return retorno;
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

	public String getOpcionImpresion() {
		return opcionImpresion;
	}

	public void setOpcionImpresion(String opcionImpresion) {
		this.opcionImpresion = opcionImpresion;
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

	public List<SelectItem> getMotivoSelecItem() {
		return motivoSelecItem;
	}

	public void setMotivoSelecItem(List<SelectItem> motivoSelecItem) {
		this.motivoSelecItem = motivoSelecItem;
	}

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}

	public String getTipoInforme() {
		return tipoInforme;
	}

	public void setTipoInforme(String tipoInforme) {
		this.tipoInforme = tipoInforme;
	}

}
