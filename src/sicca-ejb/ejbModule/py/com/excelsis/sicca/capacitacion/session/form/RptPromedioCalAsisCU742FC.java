package py.com.excelsis.sicca.capacitacion.session.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptPromedioCalAsisCU742FC")
public class RptPromedioCalAsisCU742FC {
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
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private boolean primeraEntrada = true;
	private Long idTipoCapacitacion;
	private Long idSexo;
	private Long idCapacitacion;
	private Long idNivelAgrupamiento;
	private Date fechaDesde;
	private Date fechaHasta;

	private List<SelectItem> tipoCapacitacionSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> sexoSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> capacitacionSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> nivelAgrupamientoSelectItems = new ArrayList<SelectItem>();

	public void init() {

		if (primeraEntrada) {
			cargarCabecera();
			// limpiarLista();
			primeraEntrada = false;
		}
		nivelEntidadOeeUtil.init();
		cargarTipoCapacitacionSelectItems();
		cargarSexoSelectItems();
		cargarCapacitacionSelectItems();
		cargarNivelAgrupamientoSelectItems();
	}

	public void cargarCabecera() {

		/**
		 * SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario = em.find(ConfiguracionUoCab.class,
				usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());

		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}

	public void cargarTipoCapacitacionSelectItems() {
		tipoCapacitacionSelectItems = new ArrayList<SelectItem>();
		idTipoCapacitacion = null;
		String sql = "SELECT DE.* "
				+ "FROM SELECCION.DATOS_ESPECIFICOS DE "
				+ "JOIN SELECCION.DATOS_GENERALES DG ON DG.ID_DATOS_GENERALES = DE.ID_DATOS_GENERALES "
				+ "WHERE DG.DESCRIPCION = 'TIPOS DE CAPACITACIONES' AND DE.ACTIVO = TRUE  "
				+ "order by DE.DESCRIPCION";
		List<DatosEspecificos> lista = em.createNativeQuery(sql,
				DatosEspecificos.class).getResultList();
		tipoCapacitacionSelectItems.add(new SelectItem(null, "Todos"));
		for (DatosEspecificos d : lista)
			tipoCapacitacionSelectItems.add(new SelectItem(d
					.getIdDatosEspecificos(), d.getDescripcion()));
		cargarCapacitacionSelectItems();
	}

	private void cargarSexoSelectItems() {
		sexoSelectItems = new ArrayList<SelectItem>();
		sexoSelectItems.add(new SelectItem(null, "Todos"));
		sexoSelectItems.add(new SelectItem(1, "Femenino"));
		sexoSelectItems.add(new SelectItem(2, "Masculino"));
	}

	private void cargarNivelAgrupamientoSelectItems() {
		nivelAgrupamientoSelectItems = new ArrayList<SelectItem>();
		nivelAgrupamientoSelectItems.add(new SelectItem(null, "Todos"));
		nivelAgrupamientoSelectItems.add(new SelectItem(1, "Alta Gerencia"));
		nivelAgrupamientoSelectItems.add(new SelectItem(2, "Nivel Operativo"));
		nivelAgrupamientoSelectItems.add(new SelectItem(3, "Ciudadania"));
	}

	public void cargarCapacitacionSelectItems() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		capacitacionSelectItems = new ArrayList<SelectItem>();
		idCapacitacion = null;
		String sql = "SELECT C.* FROM CAPACITACION.CAPACITACIONES C "
				+ "JOIN SELECCION.REFERENCIAS REF ON REF.VALOR_NUM = C.ESTADO "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE ON DE.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP "
				+ "WHERE C.ACTIVO = TRUE "
				+ "AND REF.DOMINIO= 'ESTADOS_CAPACITACION' AND REF.DESC_LARGA = ('FINALIZADA')";
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND C.ID_CONFIGURACION_UO = "
					+ nivelEntidadOeeUtil.getIdConfigCab();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND C.ID_CONFIGURACION_UO_DET = "
					+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		if (idTipoCapacitacion != null)
			sql += " AND DE.ID_DATOS_ESPECIFICOS = " + idTipoCapacitacion;
		if (fechaDesde != null)
			sql += " AND C.FECHA_INICIO <= to_date('" + sdf.format(fechaDesde)
					+ "','DD-MM-YYYY') ";
			
		if (fechaHasta != null)
			sql += " AND C.FECHA_INICIO >= to_date('" + sdf.format(fechaHasta)
					+ "','DD-MM-YYYY') ";
		List<Capacitaciones> lista = em.createNativeQuery(sql,
				Capacitaciones.class).getResultList();
		capacitacionSelectItems.add(new SelectItem(null, "Todos"));
		for (Capacitaciones d : lista)
			capacitacionSelectItems.add(new SelectItem(d.getIdCapacitacion(), d
					.getNombre()));
	}

	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();

	}

	public void imprimir(String formato) throws Exception {

		try {
			reportUtilFormController = (ReportUtilFormController) Component
					.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			reportUtilFormController.setParametros(param);
			reportUtilFormController
					.setNombreReporte("RPT_CU742_Promedio_Calif_Asistencia");
			if ("EXCEL".equals(formato))
				reportUtilFormController.imprimirReporteXLS();
			else
				reportUtilFormController.imprimirReportePdf();

		} catch (Exception e) {
			e.printStackTrace();
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
		param.put("sql", getQuery());
		param.put("oee_uo", usuarioLogueado.getConfiguracionUoCab()
				.getDenominacionUnidad());

		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			param.put("nivel", nivelEntidadOeeUtil.getCodNivelEntidad() + " - "
					+ nivelEntidadOeeUtil.getNombreNivelEntidad());
		else
			param.put("nivel", "Todos");
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			param.put("entidad", nivelEntidadOeeUtil.getCodSinEntidad() + " - "
					+ nivelEntidadOeeUtil.getNombreSinEntidad());
		else
			param.put("entidad", "Todos");
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			param.put("oee", nivelEntidadOeeUtil.getOrdenUnidOrg() + " - "
					+ nivelEntidadOeeUtil.getDenominacionUnidad());
		else
			param.put("oee", "Todos");
		if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
				&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim().isEmpty())
			param.put(
					"unidadOrga",
					nivelEntidadOeeUtil.getCodigoUnidOrgDep()
							+ " - "
							+ nivelEntidadOeeUtil
									.getDenominacionUnidadOrgaDep());
		else
			param.put("unidadOrga", "Todos");
		if (idTipoCapacitacion == null)
			param.put("tipoCapacitacion", "Todos");
		else
			param.put("tipoCapacitacion",
					em.find(DatosEspecificos.class, idTipoCapacitacion)
							.getDescripcion());

		if (idSexo != null && idSexo.intValue() == 1)
			param.put("sexo", "Femenino");
		else if (idSexo != null && idSexo.intValue() == 2)
			param.put("sexo", "Masculino");
		else
			param.put("sexo", "Todos");
		if (idCapacitacion == null)
			param.put("capacitacion", "Todos");
		else
			param.put("capacitacion",
					em.find(Capacitaciones.class, idCapacitacion).getNombre());
		if (idNivelAgrupamiento != null && idNivelAgrupamiento.intValue() == 1)
			param.put("nivel_agrupamiento", "Alta Gerencia");
		else if (idNivelAgrupamiento != null
				&& idNivelAgrupamiento.intValue() == 2)
			param.put("nivel_agrupamiento", "Nivel Operativo");
		else if (idNivelAgrupamiento != null
				&& idNivelAgrupamiento.intValue() == 3)
			param.put("nivel_agrupamiento", "Ciudadanía");
		else
			param.put("nivel_agrupamiento", "Todos");
		if (fechaDesde != null)
			param.put("fecha_desde", fechaDesde);

		if (fechaHasta != null)
			param.put("fecha_hasta", fechaHasta);
		return param;
	}

	private String getQuery() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String query = " SELECT DISTINCT  OEE, NEN_COD, NEN_NOM, ENT_COD, ENT_NOMBRE, ORDEN, UNIDAD_ORGANIZATIVA, "
				+ "CODIGO_UO, SEXO, TIPO_CAPACITACION, CAPACITACION, FECHA_DESDE, FECHA_HASTA,  "
				+ "NIVEL_AGRUP, ID_CAPACITACION, ID_CONFIGURACION_UO, ID_CONFIGURACION_UO_DET,  "
				+ "TRUNC(SUM(((ASISTENCIA *100)/ CANTIDAD_DIAS))/COUNT(*)) ||' %' AS PORCENTAJE_ASISTENCIA,  "
				+ "TRUNC(SUM(CALIFICACION)/COUNT(*)) ||' %' AS PORCENTAJE_CALIFICACION "
				+ "FROM CAPACITACION.PROMEDIO_CALIFICACIONES_ASISTENCIA_DET WHERE 1=1";
		
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			query += " AND NEN_COD = "
					+ nivelEntidadOeeUtil.getCodNivelEntidad();
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			query += " AND ENT_COD = "
					+ nivelEntidadOeeUtil.getCodSinEntidad();
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			query += " AND ORDEN = " + nivelEntidadOeeUtil.getOrdenUnidOrg();
		if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
				&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim().isEmpty())
			query += " AND CODIGO_UO = '"
					+ nivelEntidadOeeUtil.getCodigoUnidOrgDep() + "' ";
		if(fechaDesde != null)
			query += " AND FECHA_DESDE = to_date('" + sdf.format(fechaDesde)
			+ "','DD-MM-YYYY') ";
		if(fechaHasta != null)
			query += " AND FECHA_HASTA = to_date('" + sdf.format(fechaHasta)
			+ "','DD-MM-YYYY') ";
		if(idSexo != null && idSexo.intValue() == 1)
			query += " AND SEXO = 'F'";
		if(idSexo != null && idSexo.intValue() == 2)
			query += " AND SEXO = 'M'";
		if(idTipoCapacitacion != null)
			query += " AND TIPO_CAPACITACION = '"+em.find(DatosEspecificos.class, idTipoCapacitacion).getDescripcion()+"'";
		if(idCapacitacion != null)
			query += " AND CAPACITACION = '"+em.find(Capacitaciones.class, idCapacitacion).getNombre()+"'";
		if(idNivelAgrupamiento != null && idNivelAgrupamiento.intValue() == 1)
			query += " AND NIVEL_AGRUP = 'ALTA GERENCIA'";
		if(idNivelAgrupamiento != null && idNivelAgrupamiento.intValue() == 2)
			query += " AND NIVEL_AGRUP = 'NIVEL OPERATIVO'";
		if(idNivelAgrupamiento != null && idNivelAgrupamiento.intValue() == 3)
			query += " AND NIVEL_AGRUP = 'CIUDADANIA'";
		/*
		 * 
		 * SEXO = sexo AND CAPACITACION = capacitacion AND TIPO_CAPACITACION =
		 * tipo_capacitacion
		 */
		query += "GROUP BY OEE, NEN_COD, NEN_NOM, ENT_COD, ENT_NOMBRE, ORDEN, UNIDAD_ORGANIZATIVA,  "
				+ "CODIGO_UO, SEXO, TIPO_CAPACITACION, CAPACITACION, FECHA_DESDE, FECHA_HASTA, "
				+ "NIVEL_AGRUP, ID_CAPACITACION, ID_CONFIGURACION_UO, ID_CONFIGURACION_UO_DET ORDER BY ID_CAPACITACION";

		return query;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public Long getIdTipoCapacitacion() {
		return idTipoCapacitacion;
	}

	public void setIdTipoCapacitacion(Long idTipoCapacitacion) {
		this.idTipoCapacitacion = idTipoCapacitacion;
	}

	public Long getIdSexo() {
		return idSexo;
	}

	public void setIdSexo(Long idSexo) {
		this.idSexo = idSexo;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public List<SelectItem> getTipoCapacitacionSelectItems() {
		return tipoCapacitacionSelectItems;
	}

	public void setTipoCapacitacionSelectItems(
			List<SelectItem> tipoCapacitacionSelectItems) {
		this.tipoCapacitacionSelectItems = tipoCapacitacionSelectItems;
	}

	public List<SelectItem> getSexoSelectItems() {
		return sexoSelectItems;
	}

	public void setSexoSelectItems(List<SelectItem> sexoSelectItems) {
		this.sexoSelectItems = sexoSelectItems;
	}

	public List<SelectItem> getCapacitacionSelectItems() {
		return capacitacionSelectItems;
	}

	public void setCapacitacionSelectItems(
			List<SelectItem> capacitacionSelectItems) {
		this.capacitacionSelectItems = capacitacionSelectItems;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public List<SelectItem> getNivelAgrupamientoSelectItems() {
		return nivelAgrupamientoSelectItems;
	}

	public void setNivelAgrupamientoSelectItems(
			List<SelectItem> nivelAgrupamientoSelectItems) {
		this.nivelAgrupamientoSelectItems = nivelAgrupamientoSelectItems;
	}

	public Long getIdNivelAgrupamiento() {
		return idNivelAgrupamiento;
	}

	public void setIdNivelAgrupamiento(Long idNivelAgrupamiento) {
		this.idNivelAgrupamiento = idNivelAgrupamiento;
	}

}
