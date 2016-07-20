package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.sql.Connection;
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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.RevisionCapacitacion;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptParticipantesPorCursoFC")
public class RPTParticipantesPorCursoFC implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4219668130390697342L;
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
	private Long idTipoCapacitacion;
	private Long idCapacitacion;
	/**
	 * Incidencia 0001542
	 * */
	private String valorNivelAgrup;

	private List<SelectItem> capacitacionSelectItems = new ArrayList<SelectItem>();

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
			

		}
		if(fechaDesde == null && fechaHasta == null){
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_YEAR, 1);
			fechaDesde = calendar.getTime();
			fechaHasta = new Date();
		}
		cargarSelectItem();

	}

	public void cargarSelectItem() {
		List<Capacitaciones> lista = new ArrayList<Capacitaciones>();
		String sql = obtenerSql();
		lista = em.createNativeQuery(sql, Capacitaciones.class).getResultList();
		capacitacionSelectItems.clear();
		capacitacionSelectItems.add(new SelectItem(null, "Todos"));
		for (Capacitaciones c : lista) {
			capacitacionSelectItems.add(new SelectItem(c.getIdCapacitacion(), c
					.getNombre()));
		}
	}

	private String obtenerSql() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT C.* as resultado FROM CAPACITACION.CAPACITACIONES C "
				+ "JOIN SELECCION.REFERENCIAS REF "
				+ "ON REF.VALOR_NUM = C.ESTADO "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE "
				+ "ON DE.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP "
				+ "WHERE C.ACTIVO = TRUE "
				+ "AND REF.DOMINIO= 'ESTADOS_CAPACITACION' "
				+ "AND REF.DESC_LARGA = 'FINALIZADA' ";
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND C.ID_CONFIGURACION_UO = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND C.ID_CONFIGURACION_UO_DET = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (idTipoCapacitacion != null)
			sql += " AND DE.DESCRIPCION = '"
					+ em.find(DatosEspecificos.class, idTipoCapacitacion)
							.getDescripcion() + "'";
		if (fechaDesde != null)
			sql += " AND C.FECHA_ALTA >= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
		if (fechaHasta != null)
			sql += " AND C.FECHA_ALTA <= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";
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

	public void pdf() throws Exception {
		if (fechaDesde == null || fechaHasta == null) {
			statusMessages.add(Severity.ERROR,
					"Ingrese los datos obligatorios para realizar la acción");
			return;
		}
		Connection conn = JpaResourceBean.getConnection();
		try {

			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("sql", obtenerSqlReporte());
			JasperReportUtils.respondPDF(
					"RPT_CU503_Participantes_por_curso_y_tipo", param, false,
					conn, usuarioLogueado);
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				conn.close();
		}

	}

	private String obtenerSqlReporte() {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		String sql = "SELECT 	distinct(sin_entidad.id_sin_entidad) as id_sin_entidad, "
				+ "nivel.id_sin_nivel_entidad as id_nivel, nivel.nen_codigo as cod_nivel, "
				+ "nivel.nen_nombre as nivel, nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo as cod_entidad, "
				+ "sin_entidad.ent_nombre as entidad, oee_cap.id_configuracion_uo as id_oee, "
				+ "nivel.nen_codigo ||'.'|| sin_entidad.ent_codigo ||'.'||oee_cap.orden as cod_oee, "
				+ "c.id_capacitacion as id_capacitacion, C.NOMBRE as capacitacion, "
				+ "oee_cap.denominacion_unidad as desc_oee, DE.DESCRIPCION AS tipo, p.id_persona as id_persona, "
				+ "p.documento_identidad as ci,PAIS.DESCRIPCION PAIS, "
				+ "p.NOMBRES|| ' ' ||P.APELLIDOS nombres, OEE.DENOMINACION_UNIDAD OEE, "
				+ "P.E_MAIL EMAIL, P.TELEFONOS tel ,"
				+ "CASE WHEN POST.ID_EMPLEADO_PUESTO IS NULL THEN (SELECT REF.DESC_LARGA " 
				+ "FROM SELECCION.REFERENCIAS REF WHERE REF.DOMINIO = 'CAP_CU503' and REF.VALOR_ALF ='C') "
				+ "WHEN PUESTO.JEFATURA = TRUE THEN (SELECT REF.DESC_LARGA  "
				+ "FROM SELECCION.REFERENCIAS REF WHERE REF.DOMINIO = 'CAP_CU503' and REF.VALOR_ALF ='J') "
				+ "WHEN PUESTO.JEFATURA = FALSE  THEN (SELECT REF.DESC_LARGA  "
				+ "FROM SELECCION.REFERENCIAS REF WHERE REF.DOMINIO = 'CAP_CU503' and REF.VALOR_ALF ='O') END AS NIVEL_AGRUP "
				+ "FROM sinarh.sin_nivel_entidad nivel, CAPACITACION.LISTA_DET DET "
				+ "JOIN CAPACITACION.LISTA_CAB CAB ON CAB.ID_LISTA = DET.ID_LISTA "
				+ "JOIN CAPACITACION.CAPACITACIONES C ON C.ID_CAPACITACION = CAB.ID_CAPACITACION "
				+ "JOIN CAPACITACION.POSTULACION_CAP POST ON POST.ID_POSTULACION = DET.ID_POSTULACION "
				+ "JOIN GENERAL.PERSONA P ON P.ID_PERSONA = POST.ID_PERSONA "
				+ "JOIN GENERAL.PAIS ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC "
				+ "LEFT JOIN GENERAL.EMPLEADO_PUESTO EMP ON EMP.ID_EMPLEADO_PUESTO=  POST.ID_EMPLEADO_PUESTO "
				+ "LEFT JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET =  EMP.ID_PLANTA_CARGO_DET "
				+ "LEFT JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET =  PUESTO.ID_CONFIGURACION_UO_DET "
				+ "LEFT JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON OEE.ID_CONFIGURACION_UO =  UO.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE_CAP ON OEE_CAP.ID_CONFIGURACION_UO =  C.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO_CAP ON UO_CAP.ID_CONFIGURACION_UO_DET =  C.ID_CONFIGURACION_UO_DET "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE ON DE.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP "
				+ "Join planificacion.entidad entidad "
				+ "on OEE_CAP.id_entidad = entidad.id_entidad "
				+ "join sinarh.sin_entidad sin_entidad "
				+ "on entidad.id_sin_entidad = sin_entidad.id_sin_entidad "
				+ "where nivel.nen_codigo = sin_entidad.nen_codigo "
				+ "and nivel.ani_aniopre = sin_entidad.ani_aniopre "
				+ "AND DET.TIPO = 'P' "
				+ "and date(C.fecha_alta) >= to_date('"
				+ formatoDeFecha.format(fechaDesde)
				+ "','DD-MM-YYYY') "
				+ "and date(C.fecha_alta) <= to_date('"
				+ formatoDeFecha.format(fechaHasta) + "','DD-MM-YYYY') ";

		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND C.ID_CONFIGURACION_UO = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND C.ID_CONFIGURACION_UO_DET = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (idTipoCapacitacion != null)
			sql += " AND DE.DESCRIPCION = '"
					+ em.find(DatosEspecificos.class, idTipoCapacitacion)
							.getDescripcion() + "'";
		if (idCapacitacion != null)
			sql += " AND C.id_capacitacion = " + idCapacitacion;
		/**
		 * Incidencia 0001542
		 * */
		if(valorNivelAgrup!=null){
			if(valorNivelAgrup.equals("J"))
				sql+=" and  PUESTO.JEFATURA = TRUE ";
			if(valorNivelAgrup.equals("O"))
				sql+=" and PUESTO.JEFATURA = FALSE ";
			
			if(valorNivelAgrup.equals("C"))
				sql+=" and POST.ID_EMPLEADO_PUESTO IS NULL ";
				
		}
		
		sql+=" order by tipo,capacitacion, NIVEL_AGRUP ";
		return sql;
	}

	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();
		cargarSelectItem();
	}

	public void cambiarUO() {
		nivelEntidadOeeUtil.obtenerUnidadOrganizativaDep();
		cargarSelectItem();
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

	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}

	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public List<SelectItem> getCapacitacionSelectItems() {
		return capacitacionSelectItems;
	}

	public void setCapacitacionSelectItems(
			List<SelectItem> capacitacionSelectItems) {
		this.capacitacionSelectItems = capacitacionSelectItems;
	}

	public String getValorNivelAgrup() {
		return valorNivelAgrup;
	}

	public void setValorNivelAgrup(String valorNivelAgrup) {
		this.valorNivelAgrup = valorNivelAgrup;
	}

	
	

}
