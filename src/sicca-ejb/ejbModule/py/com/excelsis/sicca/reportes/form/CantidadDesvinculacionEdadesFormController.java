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
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("cantidadDesvinculacionEdadesFormController")
public class CantidadDesvinculacionEdadesFormController implements Serializable {

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

	/**
	 * Es llamado desde el imprimir Grafico
	 */
	public void printGrafico() throws Exception {

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() == null
				|| nivelEntidadOeeUtil.getIdSinEntidad() == null
				|| nivelEntidadOeeUtil.getIdConfigCab() == null
				|| fechaDesde == null || fechaHasta == null) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Ingrese los datos obligatorios para el filtro...",
							"No hay datos..."));
			return;
		}
		nivelEntidad = em.find(SinNivelEntidad.class,
				nivelEntidadOeeUtil.getIdSinNivelEntidad());
		sinEntidad = em.find(SinEntidad.class,
				nivelEntidadOeeUtil.getIdSinEntidad());
		configuracionUoCab = em.find(ConfiguracionUoCab.class,
				nivelEntidadOeeUtil.getIdConfigCab());
		if (fechaDesde.after(fechaHasta)) {
			statusMessages.clear();
			statusMessages.add(Severity.ERROR,
					"Las fechas ingresadas no son v\u00E1lidas");

		}

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();

		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> param = new HashMap<String, Object>();
		Connection conn = JpaResourceBean.getConnection();

		try {
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("REPORT_CONNECTION", conn);
			param.put("oee_usuario", usuarioLogueado.getConfiguracionUoCab()
					.getDenominacionUnidad());

			param.put("nivel", nivelEntidad.getNenCodigo() + " - "
					+ nivelEntidad.getNenNombre());

			param.put(
					"entidad",
					nivelEntidad.getNenCodigo() + "."
							+ sinEntidad.getEntCodigo() + " - "
							+ sinEntidad.getEntNombre());

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

			param.put("fecha_desde", fechaDesde);

			param.put("fecha_hasta", fechaHasta);
			param.put("where", where("grafico"));

			JasperReportUtils.respondPDF(
					"RPT_CU446_Grafico_Cant_Desvinculacion_Edad", param, false,
					conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	public void print() throws Exception{
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
		try {
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("REPORT_CONNECTION", conn);
			param.put("oee_usuario", usuarioLogueado.getConfiguracionUoCab()
					.getDenominacionUnidad());

			if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
				nivelEntidad = em.find(SinNivelEntidad.class,
						nivelEntidadOeeUtil.getIdSinNivelEntidad());
			else
				nivelEntidad = new SinNivelEntidad();
			if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
				sinEntidad = em.find(SinEntidad.class,
						nivelEntidadOeeUtil.getIdSinEntidad());
			else
				sinEntidad = new SinEntidad();
			if (nivelEntidadOeeUtil.getIdConfigCab() != null)
				configuracionUoCab = em.find(ConfiguracionUoCab.class,
						nivelEntidadOeeUtil.getIdConfigCab());
			else
				configuracionUoCab = new ConfiguracionUoCab();
			if (nivelEntidad != null
					&& nivelEntidad.getIdSinNivelEntidad() != null)
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

			param.put("sql", where("informe"));
			param.put("sql_secundario", whereSecundario());
			if (opcionImpresion.equals("pdf"))
				JasperReportUtils.respondPDF(
						"RPT_CU446_Cant_Desvinculacion_Edades", param, false,
						conn, usuarioLogueado);
			if (opcionImpresion.equals("xls"))
				JasperReportUtils.respondXLS(
						"RPT_CU446_Cant_Desvinculacion_Edades", param, conn,
						usuarioLogueado);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private String where(String desde) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String where = " WHERE nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre ";
		if (idMotivo != null)
			where += " and de.id_datos_especificos = " + idMotivo;

		if (fechaDesde != null)
			where += " and date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			where += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			where += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();
		if (desde.equals("grafico"))
			where += " GROUP BY GRUPOS, nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad, oee.id_configuracion_uo, persona.id_persona ";
		if (desde.equals("informe"))
			where += " GROUP BY nivel.nen_nombre, sin_entidad.id_sin_entidad, nivel.nen_codigo, sin_entidad.ent_codigo, sin_entidad.ent_nombre ";
		if (desde.equals("grafico"))
			where += " ORDER BY GRUPOS";
		if (desde.equals("informe"))
			where += " ORDER BY cod_nivel, cod_entidad";
		return where;
	}

	private String whereSecundario() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String where = " WHERE nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre ";

		where += " and sin_entidad.id_sin_entidad = $P{id_entidad} ";
		if (idMotivo != null)
			where += " and de.id_datos_especificos = " + idMotivo;

		if (fechaDesde != null)
			where += " and date(des.fecha_desvinculacion) >= to_date('"
					+ formatoDeFecha.format(fechaDesde) + "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			where += " and date(des.fecha_desvinculacion) <= to_date('"
					+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";
		if (configuracionUoCab != null
				&& configuracionUoCab.getIdConfiguracionUo() != null)
			where += " and oee.id_configuracion_uo = "
					+ configuracionUoCab.getIdConfiguracionUo();

		where += " GROUP BY GRUPOS, nivel.id_sin_nivel_entidad, sin_entidad.id_sin_entidad, oee.id_configuracion_uo, nivel.nen_codigo, sin_entidad.ent_codigo, oee.orden, oee.denominacion_unidad, persona.id_persona ";
		where += " ORDER BY GRUPOS";
		return where;
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
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=reports/reportes/CantidadDesvinculacionesEdades&codigoNivel="
				+ nivelEntidad.getNenCodigo();
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
		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=reports/reportes/CantidadDesvinculacionesEdades&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		return retorno;
	}

	public void prepararImpresion() {

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

	public Long getIdMotivo() {
		return idMotivo;
	}

	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
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

}
