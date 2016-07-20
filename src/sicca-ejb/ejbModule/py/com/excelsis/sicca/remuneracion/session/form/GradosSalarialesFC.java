package py.com.excelsis.sicca.remuneracion.session.form;

import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("gradosSalarialesFC")
public class GradosSalarialesFC {
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(required = false, create = true)
	RangoSalarialModOcupacionCU729FC rangoSalarialModOcupacionCU729FC;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;

	public void init() {
		if (rangoSalarialModOcupacionCU729FC == null) {
			rangoSalarialModOcupacionCU729FC =
				(RangoSalarialModOcupacionCU729FC) org.jboss.seam.Component.getInstance(RangoSalarialModOcupacionCU729FC.class, true);
		}
		rangoSalarialModOcupacionCU729FC.init();
	}

	private HashMap<String, Object> obtenerMapaParametros() throws Exception {

		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		Long id = usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo();
		param.put("oee", em.find(ConfiguracionUoCab.class, id).getDenominacionUnidad());
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("desde", rangoSalarialModOcupacionCU729FC.getDesde());
		param.put("hasta", rangoSalarialModOcupacionCU729FC.getHasta());
		param.put("sql", obtenerSql());

		return param;
	}

	private String obtenerSql() {
		String sql = "";
	 
			StringBuffer var1 = new StringBuffer();
			var1.append("SELECT DISTINCT SNE.nen_codigo AS NEN_COD, SNE.nen_nombre AS NEN_NOM, SNE.nen_codigo ");
			var1.append(" || '.' || sin_entidad.ent_codigo AS ENT_COD, sin_entidad.ent_nombre AS  ");
			var1.append("ENT_NOMBRE, SNE.nen_codigo || '.' || sin_entidad.ent_codigo || '.' || OEE.orden ");
			var1.append(" AS ORDEN, OEE.denominacion_unidad AS OEE FROM remuneracion.remuneraciones R  ");
			var1.append(" JOIN general.empleado_puesto E ON e.id_empleado_puesto = R.id_empleado_puesto  ");
			var1.append(" JOIN planificacion.planta_cargo_det PUESTO ON PUESTO.id_planta_cargo_det = E.id_planta_cargo_det ");
			var1.append(" JOIN general.persona PERSONA ON persona.id_persona = E.id_persona JOIN  ");
			var1.append("planificacion.configuracion_uo_det UO ON UO.id_configuracion_uo_det = PUESTO.id_configuracion_uo_det ");
			var1.append(" JOIN planificacion.configuracion_uo_cab OEE ON UO.id_configuracion_uo = OEE.id_configuracion_uo ");
			var1.append(" JOIN planificacion.entidad ENTIDAD ON entidad.id_entidad = OEE.id_entidad  ");
			var1.append("JOIN sinarh.sin_entidad SIN_ENTIDAD ON sin_entidad.id_sin_entidad = entidad.id_sin_entidad ");
			var1.append(" JOIN sinarh.sin_nivel_entidad SNE ON ( sin_entidad.ani_aniopre = SNE.ani_aniopre ");
			var1.append(" AND sin_entidad.nen_codigo = SNE.nen_codigo )   ");

			var1.append("  WHERE 1=1    ");

			if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
				var1.append(" AND  SNE.nen_codigo = "
					+ em.find(SinNivelEntidad.class, nivelEntidadOeeUtil.getIdSinNivelEntidad()).getNenCodigo());

			if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
				var1.append(" AND SIN_ENTIDAD.ENT_CODIGO =    "
					+ em.find(SinEntidad.class, nivelEntidadOeeUtil.getIdSinEntidad()).getEntCodigo());

			if (nivelEntidadOeeUtil.getIdConfigCab() != null)
				var1.append(" AND OEE.ORDEN = "
					+ em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab()).getOrden());

			var1.append("  GROUP BY SNE.nen_codigo, SNE.nen_nombre, ");
			var1.append(" SNE.nen_codigo || '.' || sin_entidad.ent_codigo,  ");
			var1.append("sin_entidad.ent_nombre, SNE.nen_codigo || '.' || sin_entidad.ent_codigo || '.' ");
			var1.append(" || OEE.orden, OEE.denominacion_unidad ");

			var1.append("  ORDER BY nen_cod, nen_nom, ent_cod,ent_nombre, orden, oee   ");
			sql = var1.toString();

	 
		return sql;
	}

	public void imprimir728() throws Exception {
		if (!rangoSalarialModOcupacionCU729FC.validar())
			return;
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			if (rangoSalarialModOcupacionCU729FC.getTipo().equalsIgnoreCase("D"))
				JasperReportUtils.respondPDF("RPT_CU728_Rango_Salarial_Detallado", param, false, conn, usuarioLogueado);

			if (rangoSalarialModOcupacionCU729FC.getTipo().equalsIgnoreCase("R"))
				JasperReportUtils.respondPDF("RPT_CU728_Rango_Salarial_Resumido", param, false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
	public void imprimir() throws Exception {
		if (!rangoSalarialModOcupacionCU729FC.validar())
			return;
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			if (rangoSalarialModOcupacionCU729FC.getTipo().equalsIgnoreCase("D"))
				JasperReportUtils.respondPDF("RPT_CU727_Rango_Salarial_Detallado", param, false, conn, usuarioLogueado);

			if (rangoSalarialModOcupacionCU729FC.getTipo().equalsIgnoreCase("R"))
				JasperReportUtils.respondPDF("RPT_CU727_Rango_Salarial_Resumido", param, false, conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)

				conn.close();
		}

	}
}
