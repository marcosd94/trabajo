package py.com.excelsis.sicca.legajo.session.form;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import enums.TiposDatos;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptPersonalEstratoEdadCU738")
public class RptPersonalEstratoEdadCU738 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1884627578450952174L;
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
	private Long idTipoPersona;
	private Long idSexo;
	private Long idDiscapacidad;
	private Integer edadDesde;
	private Integer edadHasta;
	private Integer femPermanente;
	private Integer mascPermanente;
	private Integer femContratado;
	private Integer mascContratado;
	private Integer femDiscapacidad;
	private Integer mascDiscapacidad;

	private List<SelectItem> tipoPersonaSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> sexoSelectItems = new ArrayList<SelectItem>();
	private List<SelectItem> discapacidadSelectItems = new ArrayList<SelectItem>();

	public void init() {

		if (primeraEntrada) {
			cargarCabecera();
			// limpiarLista();
			primeraEntrada = false;
		}
		nivelEntidadOeeUtil.init();
		cargarTipoPersonaSelectItems();
		cargarSexoSelectItems();
		cargarDiscapacidadSelectItems();
	}

	private void cargarTipoPersonaSelectItems() {
		tipoPersonaSelectItems = new ArrayList<SelectItem>();
		tipoPersonaSelectItems.add(new SelectItem(null, "Todos"));
		tipoPersonaSelectItems.add(new SelectItem(1, "Contratado"));
		tipoPersonaSelectItems.add(new SelectItem(2, "Permanente"));
	}

	private void cargarSexoSelectItems() {
		sexoSelectItems = new ArrayList<SelectItem>();
		sexoSelectItems.add(new SelectItem(null, "Todos"));
		sexoSelectItems.add(new SelectItem(1, "Femenino"));
		sexoSelectItems.add(new SelectItem(2, "Masculino"));
	}

	private void cargarDiscapacidadSelectItems() {
		discapacidadSelectItems = new ArrayList<SelectItem>();
		discapacidadSelectItems.add(new SelectItem(null, "Todos"));
		discapacidadSelectItems.add(new SelectItem(1, "Si"));
		discapacidadSelectItems.add(new SelectItem(2, "No"));
	}

	public void cambiarOee() {
		nivelEntidadOeeUtil.findUnidadOrganizativa();

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


	public void imprimir(String formato) throws Exception {
		
		if (edadDesde == null || edadHasta == null) {
			statusMessages
					.add(Severity.ERROR,
							"Ingrese los campos obligatorios antes de realizar esta acci\u00F3n");
			return;
		}
		if (edadDesde.intValue() > edadHasta.intValue()) {
			statusMessages.add(Severity.ERROR,
					"La edad desde no puede ser mayor a la edad hasta");
			return;
		}
		
		
		try {
			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param=obtenerMapaParametros();
			if(param==null)
				return;
			reportUtilFormController.setParametros(param);
			reportUtilFormController.setNombreReporte("RPT_CU738_Personal_Estrato_Edad");
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
		param.put("edad_hasta", "" + edadHasta);
		param.put("edad_desde", "" + edadDesde);
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
			param.put("oee_filtro", nivelEntidadOeeUtil.getOrdenUnidOrg()
					+ " - " + nivelEntidadOeeUtil.getDenominacionUnidad());
		else
			param.put("oee_filtro", "Todos");
		if (nivelEntidadOeeUtil.getCodigoUnidOrgDep() != null
				&& !nivelEntidadOeeUtil.getCodigoUnidOrgDep().trim().isEmpty())
			param.put("uo", nivelEntidadOeeUtil.getCodigoUnidOrgDep() + " - "
					+ nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep());
		else
			param.put("uo", "Todos");
		if (idTipoPersona == null)
			param.put("tipo_persona", "Todos");
		if (idTipoPersona != null && idTipoPersona.intValue() == 1)
			param.put("tipo_persona", "Contratado");
		else
			param.put("tipo_persona", "Permanente");
		if (idSexo == null)
			param.put("sexo", "Todos");
		if (idSexo != null && idSexo.intValue() == 1)
			param.put("sexo", "Femenino");
		else
			param.put("sexo", "Masculino");
		if (idDiscapacidad == null)
			param.put("discapacidad", "Todos");
		if (idDiscapacidad != null && idDiscapacidad.intValue() == 1)
			param.put("discapacidad", "Si");
		else
			param.put("discapacidad", "No");
		
		return param;
	}

	
	private String getQuery() {
		String sql = "SELECT DISTINCT OEE.DENOMINACION_UNIDAD AS OEE, "
				+ "SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "
				+ "SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN, "
				+ "UO.DENOMINACION_UNIDAD AS UNIDAD_ORGANIZATIVA, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) as codigo_uo "
				+ ", P.DOCUMENTO_IDENTIDAD AS DOCUMENTO_IDENTIDAD, P.APELLIDOS AS APELLIDOS,  P.NOMBRES AS NOMBRES, case when e.contratado = true then 'CONTRATADO' ELSE 'PERMANENTE' end AS TIPO_PERSONA, "
				+ "case when p.sexo = 'M' then 'Masculino' else 'Femenino' end as sexo, legajo.fnc_discapacidad(e.id_persona) as discapacidad, "
				+ "legajo.fnc_fecha_ingreso_funcion_publica(e.id_persona) as fecha_ingreso, "
				+ "p.fecha_nacimiento AS fecha_nacimiento, SIN_ENTIDAD.ENT_CODIGO AS ENT_CODIGO,OEE.ORDEN AS OEE_ORDEN, "
				+ "SUBSTRING(AGE(NOW(),p.FECHA_NACIMIENTO)::TEXT FROM 1 FOR 2) AS EDAD "
				+ "FROM GENERAL.EMPLEADO_PUESTO E "
				+ "JOIN GENERAL.PERSONA P ON E.ID_PERSONA = P.ID_PERSONA "
				+ "JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_DET UO ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
				+ "JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
				+ "JOIN PLANIFICACION.ENTIDAD ENTIDAD ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "
				+ "JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "
				+ "JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "
				+ "WHERE E.ACTIVO = TRUE AND SUBSTRING(AGE(NOW(),p.FECHA_NACIMIENTO)::TEXT FROM 1 FOR 2)::bigint >= "
				+ edadDesde
				+ " and SUBSTRING(AGE(NOW(),p.FECHA_NACIMIENTO)::TEXT FROM 1 FOR 2)::bigint <= "
				+ edadHasta;
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
		if (idTipoPersona != null && idTipoPersona.intValue() == 1)
			sql += " and e.contratado = true ";
		if (idTipoPersona != null && idTipoPersona.intValue() == 2)
			sql += " and e.contratado = false ";
		if (idSexo != null && idSexo.intValue() == 1)
			sql += " and p.sexo = 'F' ";
		if (idSexo != null && idSexo.intValue() == 2)
			sql += " and p.sexo = 'M' ";

		if (idDiscapacidad != null && idDiscapacidad.intValue() == 1)
			sql += " and legajo.fnc_discapacidad(e.id_persona) = 'Si' ";
		if (idDiscapacidad != null && idDiscapacidad.intValue() == 2)
			sql += " and legajo.fnc_discapacidad(e.id_persona) = 'No' ";

		sql += " order by SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, UO.DENOMINACION_UNIDAD, P.APELLIDOS";
		return sql;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public List<SelectItem> getTipoPersonaSelectItems() {
		return tipoPersonaSelectItems;
	}

	public void setTipoPersonaSelectItems(
			List<SelectItem> tipoPersonaSelectItems) {
		this.tipoPersonaSelectItems = tipoPersonaSelectItems;
	}

	public List<SelectItem> getSexoSelectItems() {
		return sexoSelectItems;
	}

	public void setSexoSelectItems(List<SelectItem> sexoSelectItems) {
		this.sexoSelectItems = sexoSelectItems;
	}

	public List<SelectItem> getDiscapacidadSelectItems() {
		return discapacidadSelectItems;
	}

	public void setDiscapacidadSelectItems(
			List<SelectItem> discapacidadSelectItems) {
		this.discapacidadSelectItems = discapacidadSelectItems;
	}

	public Long getIdTipoPersona() {
		return idTipoPersona;
	}

	public void setIdTipoPersona(Long idTipoPersona) {
		this.idTipoPersona = idTipoPersona;
	}

	public Long getIdSexo() {
		return idSexo;
	}

	public void setIdSexo(Long idSexo) {
		this.idSexo = idSexo;
	}

	public Long getIdDiscapacidad() {
		return idDiscapacidad;
	}

	public void setIdDiscapacidad(Long idDiscapacidad) {
		this.idDiscapacidad = idDiscapacidad;
	}

	public Integer getEdadDesde() {
		return edadDesde;
	}

	public void setEdadDesde(Integer edadDesde) {
		this.edadDesde = edadDesde;
	}

	public Integer getEdadHasta() {
		return edadHasta;
	}

	public void setEdadHasta(Integer edadHasta) {
		this.edadHasta = edadHasta;
	}

	public Integer getFemPermanente() {
		return femPermanente;
	}

	public void setFemPermanente(Integer femPermanente) {
		this.femPermanente = femPermanente;
	}

	public Integer getMascPermanente() {
		return mascPermanente;
	}

	public void setMascPermanente(Integer mascPermanente) {
		this.mascPermanente = mascPermanente;
	}

	public Integer getFemContratado() {
		return femContratado;
	}

	public void setFemContratado(Integer femContratado) {
		this.femContratado = femContratado;
	}

	public Integer getMascContratado() {
		return mascContratado;
	}

	public void setMascContratado(Integer mascContratado) {
		this.mascContratado = mascContratado;
	}

	public Integer getFemDiscapacidad() {
		return femDiscapacidad;
	}

	public void setFemDiscapacidad(Integer femDiscapacidad) {
		this.femDiscapacidad = femDiscapacidad;
	}

	public Integer getMascDiscapacidad() {
		return mascDiscapacidad;
	}

	public void setMascDiscapacidad(Integer mascDiscapacidad) {
		this.mascDiscapacidad = mascDiscapacidad;
	}

}
