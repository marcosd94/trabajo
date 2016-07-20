package py.com.excelsis.sicca.reportes.form;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.EvaluacionDesempeno;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptResultadoEvaluacionFC")
public class RPTResultadoEvaluacionFC implements Serializable {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Date fechaDesde;
	private Date fechaHasta;
	private Long idEvaluacion;
	private Long idPlantilla;

	private List<SelectItem> evaluacionSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> plantillaSelectItems = new ArrayList<SelectItem>();

	public void init() {

		if (nivelEntidadOeeUtil == null
				|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil
						.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil = (NivelEntidadOeeUtil) Component.getInstance(
					NivelEntidadOeeUtil.class, true);

			if (usuarioLogueado.getConfiguracionUoDet() != null) {
				Long id = usuarioLogueado.getConfiguracionUoDet()
						.getIdConfiguracionUoDet();
				nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
						.getConfiguracionUoDet().getIdConfiguracionUoDet());
			}
			cargarCabecera();
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			fechaDesde = calendar.getTime();
			fechaHasta = new Date();

		}
		cargarEvaluacionSelectItem();
		cargarPlantillaSelectItem();
	}

	public void cargarEvaluacionSelectItem() {
		List<EvaluacionDesempeno> lista = new ArrayList<EvaluacionDesempeno>();
		String sql = obtenerSql();
		lista = em.createNativeQuery(sql, EvaluacionDesempeno.class)
				.getResultList();
		evaluacionSelectItems.clear();
		evaluacionSelectItems.add(new SelectItem(null, "Todos"));
		for (EvaluacionDesempeno c : lista) {
			evaluacionSelectItems.add(new SelectItem(c
					.getIdEvaluacionDesempeno(), c.getTitulo()));
		}
	}

	private String obtenerSql() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT  E.* "
				+ "FROM EVAL_DESEMPENO.EVALUACION_DESEMPENO E "
				+ "JOIN SELECCION.REFERENCIAS REF ON REF.VALOR_NUM = E.ESTADO "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "
				+ "ON UO.ID_CONFIGURACION_UO_DET = E.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+ "ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD  "
				+ "ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
				+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
				+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "
				+ "WHERE REF.DOMINIO = 'ESTADOS_EVALUACION_DESEMPENO' AND REF.DESC_LARGA  ='FINALIZADA' "
				+ "AND E.ACTIVO = TRUE ";
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			sql += "AND SNE.ID_SIN_NIVEL_ENTIDAD = "
					+ nivelEntidadOeeUtil.getIdSinNivelEntidad();
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND OEE.ID_CONFIGURACION_UO = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
//		Se deshabilita UO; Werner.
//		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
//			sql += " AND UO.ID_CONFIGURACION_UO_DET = "
//					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (fechaDesde != null)
			sql += " AND E.FECHA_ALTA >= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " AND E.FECHA_ALTA <= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";
		return sql;
	}

	public void cargarPlantillaSelectItem() {
		List<GrupoMetodologia> lista = new ArrayList<GrupoMetodologia>();
		String sql = obtenerSqlPlantilla();
		if (idEvaluacion != null)
			lista = em.createNativeQuery(sql, GrupoMetodologia.class)
					.getResultList();
		plantillaSelectItems.clear();
		plantillaSelectItems.add(new SelectItem(null, "Todos"));
		for (GrupoMetodologia c : lista) {
			plantillaSelectItems.add(new SelectItem(c.getIdGrupoMetodologia(),
					c.getGruposEvaluacion().getGrupo() + " - "
							+ c.getDatosEspecifMetod().getDescripcion()));
		}
	}

	private String obtenerSqlPlantilla() {
		String sql = "SELECT GM.* "
				+ "FROM EVAL_DESEMPENO.GRUPO_METODOLOGIA GM "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE "
				+ "ON DE.ID_DATOS_ESPECIFICOS = GM.ID_DATOS_ESPECIF_METOD "
				+ "JOIN EVAL_DESEMPENO.GRUPOS_EVALUACION GE "
				+ "ON GE.ID_GRUPOS_EVALUACION = GM.ID_GRUPOS_EVALUACION  "
				+ "JOIN EVAL_DESEMPENO.EVALUACION_DESEMPENO E "
				+ "ON E.ID_EVALUACION_DESEMPENO = GE.ID_EVALUACION_DESEMPENO "
				+ "where ge.ID_EVALUACION_DESEMPENO = " + idEvaluacion
				+ " order by GE.GRUPO";
		return sql;
	}

	public void cargarCabecera() {

		// Nivel

		ConfiguracionUoCab oee = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		Entidad enti = em.find(Entidad.class, oee.getEntidad().getIdEntidad());
		SinEntidad sinEntidad = em.find(SinEntidad.class, enti.getSinEntidad()
				.getIdSinEntidad());
		Long idSinNivelEntidad = nivelEntidadOeeUtil.getIdSinNivelEntidad(enti
				.getSinEntidad().getNenCodigo());
		nivelEntidadOeeUtil.setIdSinNivelEntidad(idSinNivelEntidad);

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		nivelEntidadOeeUtil.setIdConfigCab(oee.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();

	}

	public void pdf() throws SQLException {
		if (fechaDesde == null || fechaHasta == null) {
			statusMessages.add(Severity.ERROR,
					"Debe completar los campos de fechas, son obligatorios");
			return;
		}
		Connection conn = null;
		try {
			HashMap<String, Object> mapa = obtenerMapaParametros();
			if (mapa == null) {
				return;
			} else
				statusMessages.clear();
			conn = JpaResourceBean.getConnection();
			JasperReportUtils.respondPDF("RPT_CU577_Resultado_Evaluaciones",
					mapa, false, conn, usuarioLogueado);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		param.put("sql", getQueryReport());
		param.put("oee", nivelEntidadOeeUtil.getDenominacionUnidad());
		if (nivelEntidadOeeUtil.getCodNivelEntidad() != null)
			param.put("nivel", nivelEntidadOeeUtil.getNombreNivelEntidad());
		if (nivelEntidadOeeUtil.getCodSinEntidad() != null)
			param.put("entidad", nivelEntidadOeeUtil.getNombreSinEntidad());
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			param.put("unidad_cab", nivelEntidadOeeUtil.getDenominacionUnidad());
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			param.put("uo", nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep());
		if (idEvaluacion != null)
			param.put("evaluacion",
					em.find(EvaluacionDesempeno.class, idEvaluacion)
							.getTitulo());
		if(idPlantilla != null)
			param.put("grupo",
					em.find(GrupoMetodologia.class, idPlantilla)
							.getGruposEvaluacion().getGrupo());
		param.put("fecha_desde", fechaDesde);
		param.put("fecha_hasta", fechaHasta);
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		return param;
	}

	private String getQueryReport() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String query = "select eval.*  "
				+ "FROM eval_desempeno.evaluaciones_funcionario eval "
				+ "where eval.fecha_eval_desde >= to_date('"
				+ sdf.format(fechaDesde) + "','DD-MM-YYYY') ";
		query += " and eval.fecha_eval_desde <= to_date('"
				+ sdf.format(fechaHasta) + "','DD-MM-YYYY') ";
		if(idEvaluacion != null)
			query += " and eval.id_evaluacion_desempeno = "+idEvaluacion;
		if(idPlantilla != null)
			query += " and eval.id_grupo_metodologia = "+idPlantilla;
		if(nivelEntidadOeeUtil.getCodNivelEntidad() != null)
			query += " and eval.nen_cod = "+nivelEntidadOeeUtil.getCodNivelEntidad();
		
		if(nivelEntidadOeeUtil.getIdConfigCab() != null)
			query += " and eval.id_configuracion_uo = "+nivelEntidadOeeUtil.getIdConfigCab();
		if(nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null)
			query += " and eval.uo_codigo = '"+nivelEntidadOeeUtil.getCodigoUnidOrgDep()+"'";
		return query;

	}
	
	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
		cargarEvaluacionSelectItem();
		cargarPlantillaSelectItem();
	}
	
	public void cambiarUO() {
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
		cargarEvaluacionSelectItem();
		cargarPlantillaSelectItem();
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

	public Long getIdEvaluacion() {
		return idEvaluacion;
	}

	public void setIdEvaluacion(Long idEvaluacion) {
		this.idEvaluacion = idEvaluacion;
	}

	public Long getIdPlantilla() {
		return idPlantilla;
	}

	public void setIdPlantilla(Long idPlantilla) {
		this.idPlantilla = idPlantilla;
	}

	public List<SelectItem> getEvaluacionSelectItems() {
		return evaluacionSelectItems;
	}

	public void setEvaluacionSelectItems(List<SelectItem> evaluacionSelectItems) {
		this.evaluacionSelectItems = evaluacionSelectItems;
	}

	public List<SelectItem> getPlantillaSelectItems() {
		return plantillaSelectItems;
	}

	public void setPlantillaSelectItems(List<SelectItem> plantillaSelectItems) {
		this.plantillaSelectItems = plantillaSelectItems;
	}

}
