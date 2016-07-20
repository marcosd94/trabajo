package py.com.excelsis.sicca.legajo.session.form;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import enums.SiNo;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptMigracionCU753FC")
public class RptMigracionCU753FC {

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
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;

	private Date fechaDesde;
	private Date fechaHasta;
	private boolean primeraEntrada = true;
	private Long tipoPersona;
	private List<SelectItem> listaTipoPersona = new ArrayList<SelectItem>();

	public void init() {

		if (primeraEntrada) {
			cargarCabecera();
			limpiarLista();
			primeraEntrada = false;
			listaTipoPersona = referenciasUtilFormController
					.getSelectItemComboTodos("TIPO_PERSONA");
		}
		nivelEntidadOeeUtil.init();

	}

	public void cargarCabecera() {

		/**
		 * SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		if (usuarioLogueado.getConfiguracionUoDet() != null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado
					.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}

	private void limpiarLista() {
		fechaDesde = null;
		fechaHasta = null;
		tipoPersona = null;
	}

	public void imprimir() throws Exception {
		if (nivelEntidadOeeUtil.getCodNivelEntidad() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar el campo Nivel antes de realizar la acci\u00F3n");
			return;
		}

		if (nivelEntidadOeeUtil.getCodSinEntidad() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar el campo Entidad antes de realizar la acci\u00F3n");
			return;
		}

		if (nivelEntidadOeeUtil.getOrdenUnidOrg() == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar el campo OEE antes de realizar la acci\u00F3n");
			return;
		}
		
		if (fechaDesde == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar el campo Fecha desde antes de realizar la acci\u00F3n");
			return;
		}
		if (fechaHasta == null) {
			statusMessages
					.add(Severity.ERROR,
							"Debe ingresar el campo Fecha hasta antes de realizar la acci\u00F3n");
			return;
		}
		if (fechaDesde.after(fechaHasta)) {
			statusMessages
					.add(Severity.ERROR,
							"La Fecha Desde no puede ser mayor a la Fecha Hasta, verifique");
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
			JasperReportUtils.respondPDF("RPT_CU753_Migracion", param, false,
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SeguridadUtilFormController sufc = (SeguridadUtilFormController) Component
				.getInstance(SeguridadUtilFormController.class, true);
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		String where = " where 1=1 ";

		where += obtenerWhere();
	
		where += " ORDER BY ORDEN, M.DOCUMENTO_IDENTIDAD";
		param.put(
				"oee",
				em.find(ConfiguracionUoCab.class,
						nivelEntidadOeeUtil.getIdConfigCab())
						.getDenominacionUnidad());
		param.put("where", where);
		param.put("sql", obtenerSql());
		
		param.put("cant_migrados", obtenerCantExito());
		param.put("cant_fracasos", obtenerCantFracaso());
		return param;
	}
	
	private String obtenerSql(){
		String sql = "SELECT DISTINCT SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, " +
				"SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, " +
				"SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN, " +
				"OEE.DENOMINACION_UNIDAD AS OEE, M.DOCUMENTO_IDENTIDAD CEDULA, M.NOMBRE, M.APELLIDO, " +
				"case when UPPER(M.tipo_persona) = 'C' then 'CONTRATADO' else 'PERMANENTE' END TIPO_PERSONA, " +
				"R.DESC_LARGA MENSAJE " +
				"FROM legajo.migraciones M " +
				"JOIN SELECCION.REFERENCIAS R ON R.VALOR_NUM = M.MENSAJE " +
				"JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON M.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO " +
				"JOIN PLANIFICACION.ENTIDAD ENTIDAD ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD " +
				"JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD " +
				"JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) " +
				"WHERE M.TABLA_SICCA IS NULL " +
				"AND R.DOMINIO = 'MSJ MIGRACION' "+obtenerWhere();
		sql += " ORDER BY ORDEN, M.DOCUMENTO_IDENTIDAD";
		return sql;
	}
	
	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
	}

	private String obtenerWhere() {
		String where = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		if (nivelEntidadOeeUtil.getCodNivelEntidad() != null)
			where += " and SNE.NEN_CODIGO = "
					+ nivelEntidadOeeUtil.getCodNivelEntidad();
		if (nivelEntidadOeeUtil.getCodSinEntidad() != null)
			where += " and SIN_ENTIDAD.ENT_CODIGO = "
					+ nivelEntidadOeeUtil.getCodSinEntidad();
		if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null)
			where += " and OEE.ORDEN  = "
					+ nivelEntidadOeeUtil.getOrdenUnidOrg();
		if(tipoPersona != null){
			Referencias ref = em.find(Referencias.class, tipoPersona);
			where += " AND UPPER(m.tipo_persona) = '"+ref.getDescAbrev()+"' ";	
		}
		if (fechaDesde != null)
			where += " AND date(M.FECHA_ALTA) >= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			where += " AND date(M.FECHA_ALTA) <= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";
		return where;
	}

	private Integer obtenerCantExito() {
		String sql = "SELECT DISTINCT COUNT (M.*) "
				+ "FROM legajo.migraciones m "
				+ "join general.persona p "
				+ "on p.id_persona = m.id_persona "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+ "ON m.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD "
				+ "ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
				+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
				+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE "
				+ "ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) WHERE 1= 1"
				+ obtenerWhere();
		try {

			Object[] config = em.createNativeQuery(sql).getResultList()
					.toArray();
			if (config == null || config.length == 0) {
				return 0;
			}
			return new Integer(config[0].toString());
		} catch (Exception e) {
			return 0;
		}
	}

	private Integer obtenerCantFracaso() {
		String sql = "SELECT DISTINCT count (m.*) "
				+ "FROM legajo.migraciones m "
				+ "JOIN SELECCION.REFERENCIAS R ON R.VALOR_NUM = m.MENSAJE "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON m.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
				+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
				+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "
				+ "WHERE m.TABLA_SICCA IS NULL "
				+ "AND R.DOMINIO = 'MSJ MIGRACION'" + obtenerWhere();

		try {

			Object[] config = em.createNativeQuery(sql).getResultList()
					.toArray();
			if (config == null || config.length == 0) {
				return 0;
			}
			return new Integer(config[0].toString());
		} catch (Exception e) {
			return 0;
		}
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

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public List<SelectItem> getListaTipoPersona() {
		return listaTipoPersona;
	}

	public void setListaTipoPersona(List<SelectItem> listaTipoPersona) {
		this.listaTipoPersona = listaTipoPersona;
	}

	public Long getTipoPersona() {
		return tipoPersona;
	}

	public void setTipoPersona(Long tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

}
