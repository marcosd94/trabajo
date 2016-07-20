package py.com.excelsis.sicca.capacitacion.session.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import py.com.excelsis.sicca.entity.MovUsuariosTarea;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptPersonalCapacitacionesOEECU743")
public class RptPersonalCapacitacionesOEECU743 {
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
	private String ci;

	private List<SelectItem> tipoCapacitacionSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> sexoSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> capacitacionSelectItems = new ArrayList<SelectItem>();

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

	private void cargarTipoCapacitacionSelectItems() {
		tipoCapacitacionSelectItems = new ArrayList<SelectItem>();
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
	}

	public void cargarCapacitacionSelectItems() {
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
		List<Capacitaciones> lista = em.createNativeQuery(sql,
				Capacitaciones.class).getResultList();
		capacitacionSelectItems.add(new SelectItem(null, "Todos"));
		for (Capacitaciones d : lista)
			capacitacionSelectItems.add(new SelectItem(d.getIdCapacitacion(), d
					.getNombre()));
	}

	private void cargarSexoSelectItems() {
		sexoSelectItems = new ArrayList<SelectItem>();
		sexoSelectItems.add(new SelectItem(null, "Todos"));
		sexoSelectItems.add(new SelectItem(1, "Femenino"));
		sexoSelectItems.add(new SelectItem(2, "Masculino"));
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
					.setNombreReporte("RPT_CU743_Personal_Capacitaciones");
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
		param.put("oee_uo", usuarioLogueado.getConfiguracionUoCab().getDenominacionUnidad());

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
		else if(idSexo != null && idSexo.intValue() == 2)
			param.put("sexo", "Masculino");
		else
			param.put("sexo", "Todos");
		if (idCapacitacion == null)
			param.put("capacitacion", "Todos");
		else
			param.put("capacitacion",
					em.find(Capacitaciones.class, idCapacitacion).getNombre());
		if (ci != null && !ci.trim().isEmpty())
			param.put("ci", ci.trim());

		return param;
	}

	private String getQuery() {
		String sql = "SELECT distinct OEE.DENOMINACION_UNIDAD AS OEE, SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "
				+ "SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,OEE.ORDEN AS ORDEN, UO.DENOMINACION_UNIDAD UNIDAD_ORGANIZATIVA, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) as codigo_uo, "
				+ "p.documento_identidad, p.NOMBRES, P.APELLIDOS, case when p.sexo = 'M' then 'Masculino' else 'Femenino' end as sexo, "
				+ "p.FECHA_NACIMIENTO, SUBSTRING(AGE(NOW(),p.FECHA_NACIMIENTO)::TEXT FROM 1 FOR 2) EDAD, OEE_CAP.DENOMINACION_UNIDAD oee_capacitacion, "
				+ "DE.DESCRIPCION as tipo_capacitacion, C.NOMBRE as capacitacion, date(c.fecha_inicio) as fecha_desde, "
				+ "date(c.fecha_fin) as fecha_hasta, c.carga_horaria, e.calificacion, p.id_persona as id "
				+ "FROM CAPACITACION.LISTA_DET DET JOIN CAPACITACION.LISTA_CAB CAB ON CAB.ID_LISTA = DET.ID_LISTA "
				+ "JOIN CAPACITACION.CAPACITACIONES C ON C.ID_CAPACITACION = CAB.ID_CAPACITACION "
				+ "JOIN CAPACITACION.POSTULACION_CAP POST ON POST.ID_POSTULACION = DET.ID_POSTULACION "
				+ "JOIN GENERAL.PERSONA P ON P.ID_PERSONA = POST.ID_PERSONA "
				+ "JOIN GENERAL.PAIS ON PAIS.ID_PAIS = P.ID_PAIS_EXPEDICION_DOC "
				+ "JOIN GENERAL.EMPLEADO_PUESTO EMP ON EMP.ID_EMPLEADO_PUESTO=  POST.ID_EMPLEADO_PUESTO "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET =  EMP.ID_PLANTA_CARGO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET =  PUESTO.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON OEE.ID_CONFIGURACION_UO =  UO.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
				+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
				+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE_CAP ON OEE_CAP.ID_CONFIGURACION_UO =  C.ID_CONFIGURACION_UO "
				+ "JOIN SELECCION.DATOS_ESPECIFICOS DE ON DE.ID_DATOS_ESPECIFICOS = C.ID_DATOS_ESPECIFICOS_TIPO_CAP "
				+ "LEFT JOIN CAPACITACION.EVALUACION_PART E ON E.ID_POSTULACION = POST.ID_POSTULACION "
				+ "WHERE DET.TIPO = 'P'";
		if (nivelEntidadOeeUtil.getIdSinNivelEntidad() != null)
			sql += " AND SNE.NEN_CODIGO = "
					+ nivelEntidadOeeUtil.getCodNivelEntidad();
		if (nivelEntidadOeeUtil.getIdSinEntidad() != null)
			sql += " AND SIN_ENTIDAD.ENT_CODIGO = "
					+ nivelEntidadOeeUtil.getCodSinEntidad();
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND OEE.ORDEN = " + nivelEntidadOeeUtil.getOrdenUnidOrg();
		if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
				&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim().isEmpty())
			sql += " AND (SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET)) = '"
					+ nivelEntidadOeeUtil.getCodigoUnidOrgDep() + "' ";
		if (idSexo != null && idSexo.intValue() == 1)
			sql += " and p.sexo = 'F' ";
		if (idSexo != null && idSexo.intValue() == 2)
			sql += " and p.sexo = 'M' ";
		if(ci != null && !ci.trim().isEmpty())
			sql += " and P.DOCUMENTO_IDENTIDAD = '"+ci.trim()+"'";
		if(idCapacitacion != null)
			sql += " and c.id_capacitacion = "+idCapacitacion;
		if(idTipoCapacitacion != null)
			sql += " and DE.ID_DATOS_ESPECIFICOS = "+idTipoCapacitacion;
		
		sql += "ORDER BY SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, UO.DENOMINACION_UNIDAD, P.APELLIDOS, fecha_desde";

		return sql;
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

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
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

}
