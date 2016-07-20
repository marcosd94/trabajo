package py.com.excelsis.sicca.reportes.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("empleadosInhabilitadosFC")
public class EmpleadosInhabilitadosFC {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;
	@In(required = false)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private Date fechaDesde;
	private Date fechaHasta;

	private String formato = "PDF";
	private List<SelectItem> formatoSelectItem;

	private String tipo = "R";
	private List<SelectItem> tipoSelectItem;

	private String detalle = "T";
	private List<SelectItem> detalleSelectItem;

	private String reporte;
	private final String TIPO_REPORTE_EMPLEADOS_INHABILITADOS = "EI";
	private final String TIPO_REPORTE_EMPLEADOS_JUBILADOS = "EJ";

	private final String TIPO_INHABILITADO_POR_DESVINCULACION = "D";
	private final String TIPO_INHABILITADO_POR_PROCESO_JURIDICO = "J";
	private String inhabilitadoPor;

	private String from;

	private boolean habOee;

	public void init() {

		if (nivelEntidadOeeUtil == null
			|| (nivelEntidadOeeUtil.getCodSinEntidad() == null && nivelEntidadOeeUtil.getNombreNivelEntidad() == null)) {
			nivelEntidadOeeUtil =
				(NivelEntidadOeeUtil) Component.getInstance(NivelEntidadOeeUtil.class, true);
			cargarCabecera();
			if (esRolHomologar())
				habOee = true;
			else
				habOee = false;
		}

		if (inhabilitadoPor == null) {
			if ("D".equals(from))
				inhabilitadoPor = TIPO_INHABILITADO_POR_DESVINCULACION;
			else
				inhabilitadoPor = TIPO_INHABILITADO_POR_PROCESO_JURIDICO;
		}

	}

	@SuppressWarnings("unchecked")
	private boolean esRolHomologar() {
		List<UsuarioRol> uRols =
			em.createQuery("Select d FROM UsuarioRol d " + " WHERE d.usuario.idUsuario="
				+ usuarioLogueado.getIdUsuario() + " AND d.rol.homologador=TRUE ").getResultList();
		if (uRols.isEmpty())
			return false;
		return true;
	}

	public void cargarCabecera() {
		grupoPuestosController =
			(GrupoPuestosController) Component.getInstance(GrupoPuestosController.class, true);
		grupoPuestosController.initCabecera();
		nivelEntidadOeeUtil.limpiar();

		SinNivelEntidad sinNivelEntidad = grupoPuestosController.getSinNivelEntidad();
		SinEntidad sinEntidad = grupoPuestosController.getSinEntidad();
		ConfiguracionUoCab configuracionUoCab = grupoPuestosController.getConfiguracionUoCab();

		// Nivel
		nivelEntidadOeeUtil.setIdSinNivelEntidad(sinNivelEntidad.getIdSinNivelEntidad());

		// Entidad
		nivelEntidadOeeUtil.setIdSinEntidad(sinEntidad.getIdSinEntidad());

		// OEE
		
		nivelEntidadOeeUtil.setIdConfigCab(configuracionUoCab.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init();
	}
	private String query1(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql=" SELECT sne.nen_codigo as nivel_codigo, sne.nen_nombre as nivel_nombre, sne.id_sin_nivel_entidad, "+
			  "  se.ent_codigo as entidad_codigo, se.ent_nombre as entidad_nombre, se.id_sin_entidad, "+
			  "  cuc.orden as oee_codigo, cuc.denominacion_unidad as oee_nombre, cuc.id_configuracion_uo, "+
			  "  sne.nen_codigo || '.' || se.ent_codigo || '.' || cuc.orden as cod_oee, "+
			 "   desvinculacion.cant_empleados_inhab (cuc.id_configuracion_uo, 'M', true,INH.tipo) as cant_inhab_masc, "+
			 "   desvinculacion.cant_empleados_inhab (cuc.id_configuracion_uo, 'F', true,INH.tipo) as cant_inhab_fem, "+
			  "  desvinculacion.cant_empleados_inhab (cuc.id_configuracion_uo, 'M', false,INH.tipo) as cant_hab_masc, "+
			  "  desvinculacion.cant_empleados_inhab (cuc.id_configuracion_uo, 'F', false,INH.tipo) as cant_hab_fem "+
			"	FROM DESVINCULACION.INHABILITADOS INH "+
			"	JOIN GENERAL.EMPLEADO_PUESTO EMP "+
			"	ON EMP.ID_EMPLEADO_PUESTO = INH.ID_EMPLEADO_PUESTO "+
			"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
			"	ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
			"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB cuc "+
			"	ON cuc.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO "+
			"	JOIN GENERAL.PERSONA P 	ON EMP.ID_PERSONA = P.ID_PERSONA "+
			"	JOIN planificacion.entidad e 	on(cuc.id_entidad = e.id_entidad) "+
			"      JOIN sinarh.sin_entidad se	on(se.id_sin_entidad= e.id_sin_entidad) "+
			 "	JOIN sinarh.sin_nivel_entidad sne 	on (se.ani_aniopre = sne.ani_aniopre and se.nen_codigo = sne.nen_codigo) "+
			"	where cuc.activo is true 	and se.activo is true 	and sne.activo is true ";
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
				sql+= "	and sne.id_sin_nivel_entidad= "+nivelEntidadOeeUtil.getIdSinNivelEntidad();
			if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
				sql+="	and se.id_sin_entidad = "+nivelEntidadOeeUtil.getIdSinEntidad();
			if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
				sql+="	and cuc.id_configuracion_uo = "+nivelEntidadOeeUtil.getIdConfigCab();
			
			sql += " AND cast(INH.FECHA_ALTA as date) >= to_date('" + sdf.format(fechaDesde)
			+ "','DD-MM-YYYY') and cast(INH.FECHA_ALTA as date)  <=  to_date('" + sdf.format(fechaHasta)
			+ "','DD-MM-YYYY')  ";
			
			sql+=" and inh.tipo like '%"+inhabilitadoPor+"%'";
			sql+="	group by sne.nen_codigo, sne.nen_nombre, sne.id_sin_nivel_entidad, "+
			"	    se.ent_codigo, se.ent_nombre, se.id_sin_entidad, "+
			"	    cuc.orden, cuc.denominacion_unidad, cuc.id_configuracion_uo, cod_oee,INH.tipo "+
			"	order by nivel_codigo, nivel_nombre, sne.id_sin_nivel_entidad, entidad_codigo, "+
			"	    entidad_nombre, se.id_sin_entidad, oee_codigo, oee_nombre, cuc.id_configuracion_uo";
			return sql;
	}
	
	private String query2(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql=" SELECT sne.nen_codigo as nivel_codigo, sne.nen_nombre as nivel_nombre, sne.id_sin_nivel_entidad, "+
			  "  se.ent_codigo as entidad_codigo, se.ent_nombre as entidad_nombre, se.id_sin_entidad, "+
			  "  cuc.orden as oee_codigo, cuc.denominacion_unidad as oee_nombre, cuc.id_configuracion_uo, "+
			  "  sne.nen_codigo || '.' || se.ent_codigo || '.' || cuc.orden as cod_oee, "+
			 "   desvinculacion.cant_empleados_inhab2 (cuc.id_configuracion_uo, 'M', true) as cant_inhab_masc, "+
			 "   desvinculacion.cant_empleados_inhab2 (cuc.id_configuracion_uo, 'F', true) as cant_inhab_fem, "+
			  "  desvinculacion.cant_empleados_inhab2 (cuc.id_configuracion_uo, 'M', false) as cant_hab_masc, "+
			  "  desvinculacion.cant_empleados_inhab2 (cuc.id_configuracion_uo, 'F', false) as cant_hab_fem "+
			"	FROM DESVINCULACION.INHABILITADOS INH "+
			"	JOIN GENERAL.EMPLEADO_PUESTO EMP "+
			"	ON EMP.ID_EMPLEADO_PUESTO = INH.ID_EMPLEADO_PUESTO "+
			"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
			"	ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
			"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB cuc "+
			"	ON cuc.ID_CONFIGURACION_UO = UO.ID_CONFIGURACION_UO "+
			"	JOIN GENERAL.PERSONA P 	ON EMP.ID_PERSONA = P.ID_PERSONA "+
			"	JOIN planificacion.entidad e 	on(cuc.id_entidad = e.id_entidad) "+
			"      JOIN sinarh.sin_entidad se	on(se.id_sin_entidad= e.id_sin_entidad) "+
			 "	JOIN sinarh.sin_nivel_entidad sne 	on (se.ani_aniopre = sne.ani_aniopre and se.nen_codigo = sne.nen_codigo) "+
			"	where cuc.activo is true 	and se.activo is true 	and sne.activo is true ";
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
				sql+= "	and sne.id_sin_nivel_entidad= "+nivelEntidadOeeUtil.getIdSinNivelEntidad();
			if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
				sql+="	and se.id_sin_entidad = "+nivelEntidadOeeUtil.getIdSinEntidad();
			if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
				sql+="	and cuc.id_configuracion_uo = "+nivelEntidadOeeUtil.getIdConfigCab();
			
			sql += " AND cast(INH.FECHA_ALTA as date) >= to_date('" + sdf.format(fechaDesde)
			+ "','DD-MM-YYYY') and cast(INH.FECHA_ALTA as date)  <=  to_date('" + sdf.format(fechaHasta)
			+ "','DD-MM-YYYY')  ";
			
		
			sql+="	group by sne.nen_codigo, sne.nen_nombre, sne.id_sin_nivel_entidad, "+
			"	    se.ent_codigo, se.ent_nombre, se.id_sin_entidad, "+
			"	    cuc.orden, cuc.denominacion_unidad, cuc.id_configuracion_uo, cod_oee "+
			"	order by nivel_codigo, nivel_nombre, sne.id_sin_nivel_entidad, entidad_codigo, "+
			"	    entidad_nombre, se.id_sin_entidad, oee_codigo, oee_nombre, cuc.id_configuracion_uo";
			return sql;
	}
	

	public void limpiar() {
		cargarCabecera();
		fechaDesde = null;
		fechaHasta = null;
		formato = "PDF";
		tipo = "R";
		detalle = "T";
	}

	private void cargarParametros() {
		try {
			ConfiguracionUoCab uo = new ConfiguracionUoCab();
			uo =
				em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
			reportUtilFormController.getParametros().put("oee_uo", uo.getDenominacionUnidad());
			nivelEntidadOeeUtil.init();

			reportUtilFormController.getParametros().put("idSinNivelEntidad", nivelEntidadOeeUtil.getIdSinNivelEntidad());
			reportUtilFormController.getParametros().put("idSinEntidad", nivelEntidadOeeUtil.getIdSinEntidad());
			reportUtilFormController.getParametros().put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
			reportUtilFormController.getParametros().put("desde", fechaDesde);
			reportUtilFormController.getParametros().put("hasta", fechaHasta);

			// if("I".equals(detalle))
			// reportUtilFormController.getParametros().put("inhabilitado", true);
			// else if("R".equals(detalle))
			// reportUtilFormController.getParametros().put("inhabilitado", false);
			// else
			// reportUtilFormController.getParametros().put("inhabilitado", null);

			if ("T".equals(inhabilitadoPor))
			{
				reportUtilFormController.getParametros().put("inhabilitadoPor", "%%");
				reportUtilFormController.getParametros().put("sql", query2());
			}
			else
			{
				reportUtilFormController.getParametros().put("inhabilitadoPor", "%"+ inhabilitadoPor + "%");
				reportUtilFormController.getParametros().put("sql", query1());
			}
					

			if (TIPO_INHABILITADO_POR_DESVINCULACION.equals(inhabilitadoPor))
				reportUtilFormController.getParametros().put("inha", "DESVINCULACION");
			else if(TIPO_INHABILITADO_POR_PROCESO_JURIDICO.equals(inhabilitadoPor))
				reportUtilFormController.getParametros().put("inha", "PROCESOS JURIDICOS");
			else 
				reportUtilFormController.getParametros().put("inha", "TODOS");
			if (nivelEntidadOeeUtil.getCodNivelEntidad() != null) {
				String codNivel = nivelEntidadOeeUtil.getCodNivelEntidad() + "";
				reportUtilFormController.getParametros().put("nivel", codNivel + "-"
					+ nivelEntidadOeeUtil.getNombreNivelEntidad());

				if (nivelEntidadOeeUtil.getCodSinEntidad() != null) {
					reportUtilFormController.getParametros().put("entidad", codNivel + "."
						+ nivelEntidadOeeUtil.getCodSinEntidad() + "-"
						+ nivelEntidadOeeUtil.getNombreSinEntidad());

					if (nivelEntidadOeeUtil.getOrdenUnidOrg() != null) {
						reportUtilFormController.getParametros().put("oee", codNivel + "."
							+ nivelEntidadOeeUtil.getCodSinEntidad() + "."
							+ nivelEntidadOeeUtil.getOrdenUnidOrg() + "-"
							+ nivelEntidadOeeUtil.getDenominacionUnidad());
					}
				}

			}

			reportUtilFormController.getParametros().put("isAdmin", habOee);

			// inhabilitado
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean validar(Boolean controlarCab) {

		if (fechaDesde == null) {
			statusMessages.add(Severity.ERROR, "Fecha Desde es un campo requerido. Verifique.");
			return false;
		} else if (fechaHasta == null) {
			statusMessages.add(Severity.ERROR, "Fecha Hasta es un campo requerido. Verifique.");
			return false;
		}

		if (fechaDesde.after(fechaHasta)) {
			statusMessages.add(Severity.ERROR, "La Fecha Desde no puede ser posterior a la Fecha Hasta. Verifique.");
			return false;
		}

		return true;
	}

	public void imprimir() {

		boolean validarCabecera = false;
		if ("D".equals(tipo))
			validarCabecera = true;

		if (!validar(validarCabecera))
			return;

		reportUtilFormController =
			(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
		reportUtilFormController.init();

		if (TIPO_REPORTE_EMPLEADOS_JUBILADOS.equals(reporte)) {

			if ("R".equalsIgnoreCase(tipo)) {
				if (habOee) {// si es usuario administrador
					reportUtilFormController.setNombreReporte("RPT_CU448_empleados_jubilados");
				} else
					reportUtilFormController.setNombreReporte("RPT_CU448_empleados_jubilados_no_admin");
			} else
				reportUtilFormController.setNombreReporte("RPT_CU448_empleados_jubilados_detallado");

		} else {
			if ("R".equalsIgnoreCase(tipo))
				reportUtilFormController.setNombreReporte("RPT_CU447_empleados_inhabilitados");
			else
				reportUtilFormController.setNombreReporte("RPT_CU447_empleados_inhabilitados_detallado");
		}

		cargarParametros();

		if ("EXCEL".equals(formato))
			reportUtilFormController.imprimirReporteXLS();
		else
			reportUtilFormController.imprimirReportePdf();
	}

	private void buildFormatoSelectItem() {
		formatoSelectItem = new ArrayList<SelectItem>();
		formatoSelectItem.add(new SelectItem("PDF", "PDF"));
		formatoSelectItem.add(new SelectItem("EXCEL", "EXCEL"));
	}

	private void buildDetalleSelectItem() {
		detalleSelectItem = new ArrayList<SelectItem>();
		detalleSelectItem.add(new SelectItem("T", "Todos"));
		detalleSelectItem.add(new SelectItem("I", "Inhabilitados"));
		detalleSelectItem.add(new SelectItem("R", "Restablecidos"));
	}

	public List<SelectItem> getInhabilitadoPorSelectItem() {
		List<SelectItem> inhabilitadoPorSelectItem = new ArrayList<SelectItem>();
		inhabilitadoPorSelectItem.add(new SelectItem("T", "TODOS"));
		inhabilitadoPorSelectItem.add(new SelectItem(TIPO_INHABILITADO_POR_DESVINCULACION, "DESVINCULACION"));
		inhabilitadoPorSelectItem.add(new SelectItem(TIPO_INHABILITADO_POR_PROCESO_JURIDICO, "PROCESOS JURIDICOS"));
		return inhabilitadoPorSelectItem;
	}

	private void buildTipoSelectItem() {
		tipoSelectItem = new ArrayList<SelectItem>();
		tipoSelectItem.add(new SelectItem("R", "Resumido"));
		tipoSelectItem.add(new SelectItem("D", "Detallado"));
	}

	public String getTitulo() {
		if (TIPO_REPORTE_EMPLEADOS_INHABILITADOS.equals(reporte))
			return SeamResourceBundle.getBundle().getString("CU447_tittle");

		return SeamResourceBundle.getBundle().getString("CU448_tittle");
	}

	public boolean mostrarInhabilitadoPor() {
		if (TIPO_REPORTE_EMPLEADOS_JUBILADOS.equals(reporte))
			return false;

		return true;
	}

	public void initSet() {

	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormatoSelectItem(List<SelectItem> formatoSelectItem) {
		this.formatoSelectItem = formatoSelectItem;
	}

	public List<SelectItem> getFormatoSelectItem() {
		if (formatoSelectItem == null)
			buildFormatoSelectItem();
		return formatoSelectItem;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipoSelectItem(List<SelectItem> tipoSelectItem) {
		this.tipoSelectItem = tipoSelectItem;
	}

	public List<SelectItem> getTipoSelectItem() {
		if (tipoSelectItem == null)
			buildTipoSelectItem();
		return tipoSelectItem;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalleSelectItem(List<SelectItem> detalleSelectItem) {
		this.detalleSelectItem = detalleSelectItem;
	}

	public List<SelectItem> getDetalleSelectItem() {
		if (detalleSelectItem == null)
			buildDetalleSelectItem();
		return detalleSelectItem;
	}

	public void setReporte(String reporte) {
		this.reporte = reporte;
	}

	public String getReporte() {
		return reporte;
	}

	public void setInhabilitadoPor(String inhabilitadoPor) {
		this.inhabilitadoPor = inhabilitadoPor;
	}

	public String getInhabilitadoPor() {
		return inhabilitadoPor;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public boolean isHabOee() {
		return habOee;
	}

	public void setHabOee(boolean habOee) {
		this.habOee = habOee;
	}

}
