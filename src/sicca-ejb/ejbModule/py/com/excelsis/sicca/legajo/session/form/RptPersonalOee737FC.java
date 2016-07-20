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

import enums.PermanenteContratadoEnums;
import enums.SexoEnums;
import enums.SiNo;
import enums.SiNoTodosEnums;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Pais;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("rptPersonalOee737FC")
public class RptPersonalOee737FC {

	
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


	private boolean primeraEntrada=true;
	private SiNoTodosEnums siNoTodosEnums=SiNoTodosEnums.TODOS;
	private PermanenteContratadoEnums permanenteContratado=PermanenteContratadoEnums.TODOS;
	private SexoEnums sexo=SexoEnums.TODOS;
	
	public void init() {

		
		if(primeraEntrada){
			cargarCabecera();
			limpiarLista();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
	

	}

	private void limpiarLista(){
		siNoTodosEnums=SiNoTodosEnums.TODOS;
		permanenteContratado=PermanenteContratadoEnums.TODOS;
		sexo=SexoEnums.TODOS;
		
	}
	

	private String obtenerSql() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String sql = "SELECT DISTINCT 	OEE.DENOMINACION_UNIDAD AS OEE, SIN_ENTIDAD.ENT_CODIGO,OEE.ORDEN,"+
					"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
					"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN, "+
					"	UO.DENOMINACION_UNIDAD UNIDAD_ORGANIZATIVA, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) as codigo_uo "+
					"	, P.DOCUMENTO_IDENTIDAD, P.APELLIDOS,  P.NOMBRES, case when e.contratado = true then 'CONTRATADO' ELSE 'PERMANENTE' end AS TIPO_PERSONA,  "+
					"	case when p.sexo = 'M' then 'Masculino' else 'Femenino' end as sexo, legajo.fnc_discapacidad(e.id_persona) as discapacidad, p.fecha_nacimiento, "+ 
					"	SUBSTRING(AGE(NOW(),p.FECHA_NACIMIENTO)::TEXT FROM 1 FOR 2) EDAD, legajo.fnc_fecha_ingreso_funcion_publica(e.id_persona) as fecha_ingreso ,legajo.fnc_discapacidad(e.id_persona) as discapacidad "+
					"	FROM GENERAL.EMPLEADO_PUESTO E "+
					"	JOIN GENERAL.PERSONA P "+
					"	ON E.ID_PERSONA = P.ID_PERSONA "+
					"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
					"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
					"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "+
					"	ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
					"	JOIN PLANIFICACION.ENTIDAD ENTIDAD  "+
					"	ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
					"	  JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
					"	  JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "+
					"	WHERE E.ACTIVO = TRUE ";
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null){
			sql+=" AND SNE.NEN_CODIGO = "+nivelEntidadOeeUtil.getCodNivelEntidad();
		}
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null){
			sql+="AND SIN_ENTIDAD.ENT_CODIGO =  "+nivelEntidadOeeUtil.getCodSinEntidad();
		}
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			sql += " AND  OEE.ORDEN = "+ nivelEntidadOeeUtil.getOrdenUnidOrg();
		if (nivelEntidadOeeUtil.getIdUnidadOrganizativa() != null)
			sql += " AND UO.ID_CONFIGURACION_UO_DET = "+ nivelEntidadOeeUtil.getIdUnidadOrganizativa();
		
		if (sexo.getValor() != null)
			sql += " and p.sexo =  '" + sexo.getValor()+"'";
		if (permanenteContratado.getValor() != null)
			sql += " and e.contratado = " +permanenteContratado.getValor();
		if(siNoTodosEnums.getValor()!=null){
			if(siNoTodosEnums.getValor())
				sql+=" and legajo.fnc_discapacidad(e.id_persona) = 'Si'";
			else
				sql+=" and legajo.fnc_discapacidad(e.id_persona) = 'No'";
		}
		sql+=" order by SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, "+
			" OEE.ORDEN,	UO.DENOMINACION_UNIDAD,	P.APELLIDOS	";
		return sql;
	}
	

	public void cargarCabecera() {

		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}
	
	
	
	public void imprimir(String formato) throws Exception {
			
			
			
			
			try {
				reportUtilFormController =
					(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
				reportUtilFormController.init();
				HashMap<String, Object> param = new HashMap<String, Object>();
				param=obtenerMapaParametros();
				if(param==null)
					return;
				reportUtilFormController.setParametros(param);
				reportUtilFormController.setNombreReporte("RPT_CU737");
				if ("EXCEL".equals(formato))
					reportUtilFormController.imprimirReporteXLS();
				else
					reportUtilFormController.imprimirReportePdf();
				
//				JasperReportUtils.respondPDF("RPT_CU737", param, false,
//						conn, usuarioLogueado);
			//	conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
	
		}
	private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
	
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		
	
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null){
			Map<String, String> diccDscNEO = nivelEntidadOeeUtil.descNivelEntidadOee();
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			param.put("nivel", diccDscNEO.get("NIVEL"));
			param.put("entidad", diccDscNEO.get("ENTIDAD"));
			param.put("oee", diccDscNEO.get("OEE"));
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null ){
				
				if (!sufc.validarInput(nivelEntidadOeeUtil.getIdUnidadOrganizativa().toString(), TiposDatos.LONG.getValor(), null)) {
					return null;
				}
				
				param.put("unidadOrga", diccDscNEO.get("UND_ORG"));
			}
			
		}else {
			if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
				param.put("nivel",nivelEntidadOeeUtil.getCodNivelEntidad()+" "+nivelEntidadOeeUtil.getNombreNivelEntidad());
			if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
				param.put("entidad",nivelEntidadOeeUtil.getCodSinEntidad()+" "+nivelEntidadOeeUtil.getNombreSinEntidad());
		}
			
		
		
		param.put("sexo", sexo.getDescripcion());
		param.put("tipoPersona", permanenteContratado.getDescripcion());
		
		
		
		
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oee_uo", cab.getDenominacionUnidad());
		String sql=null;
		sql=obtenerSql();
		if(sql==null)
			return null;
		param.put("sql",sql);
		
		return param;
	}
	
	
	



	
	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public SiNoTodosEnums getSiNoTodosEnums() {
		return siNoTodosEnums;
	}

	public void setSiNoTodosEnums(SiNoTodosEnums siNoTodosEnums) {
		this.siNoTodosEnums = siNoTodosEnums;
	}

	public PermanenteContratadoEnums getPermanenteContratado() {
		return permanenteContratado;
	}

	public void setPermanenteContratado(
			PermanenteContratadoEnums permanenteContratado) {
		this.permanenteContratado = permanenteContratado;
	}

	public SexoEnums getSexo() {
		return sexo;
	}

	public void setSexo(SexoEnums sexo) {
		this.sexo = sexo;
	}
	
	
}
