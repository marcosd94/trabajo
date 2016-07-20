package py.com.excelsis.sicca.remuneracion.session.form;

import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;
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
@Name("listaentidades730FC")
public class Listaentidades730FC {
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


	private Integer desde;
	private Integer hasta;
	private String tipo="D";
	private boolean primeraEntrada=true;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		cargarCabecera();
		nivelEntidadOeeUtil.init();
		
	}

	private void cargarCabecera(){
		if(primeraEntrada){
			if(usuarioLogueado.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
			}
				
			primeraEntrada=false;
		}
	}




	
	private boolean validar(){
		if(hasta == null || desde == null){
			statusMessages.add(Severity.ERROR,"Ingrese los datos obligatorios");
			return false;
		}
		if(hasta.intValue() <desde.intValue() ){
			statusMessages.add(Severity.ERROR,"El valor desde no puede ser mayor al valor hasta verifique");
			return false;
		}
		return true;
	}

	public void imprimir() throws Exception {
		if(!validar())
			return;
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			if (tipo.equalsIgnoreCase("D"))
				JasperReportUtils.respondPDF(
						"RPT_CU730_detallado", param, false,
						conn, usuarioLogueado);

			if (tipo.equalsIgnoreCase("R"))
				JasperReportUtils.respondPDF(
						"RPT_CU730_resumido", param, false,
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

		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());

		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		Long id = usuarioLogueado.getConfiguracionUoCab()
				.getIdConfiguracionUo();
		param.put("oee", em.find(ConfiguracionUoCab.class, id)
				.getDenominacionUnidad());
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
		param.put("desde", desde);
		param.put("hasta", hasta);
		param.put("nivelEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
		param.put("sinEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
		param.put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
		param.put("sql",consulta() );
	

		return param;
	}

	
	private String consulta(){
		String q="";
		if(tipo.equals("D")){
			q="	SELECT DISTINCT "+
			"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SIN_ENTIDAD.ENT_CODIGO AS COD_ENT, "+
			"	SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
			"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,oee.orden as oee_orden, "+
			"	SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "+
			"	PERSONA.DOCUMENTO_IDENTIDAD CEDULA, PAIS.DESCRIPCION, "+
			"	PERSONA.NOMBRES ||' '||PERSONA.APELLIDOS as funcionario, "+
			"	PUESTO.DESCRIPCION puesto,  remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, persona.id_persona) as VALOR_ECONOMICO, pais.descripcion as pais ,PERSONA.NOMBRES"+
			"	FROM REMUNERACION.REMUNERACIONES R "+
			"	JOIN GENERAL.EMPLEADO_PUESTO E "+
			"	on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO "+
			"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
			"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "+
			"	JOIN GENERAL.PERSONA PERSONA "+
			"	ON PERSONA.ID_PERSONA = E.ID_PERSONA "+
			"	JOIN GENERAL.PAIS "+
			"	ON PERSONA.ID_PAIS_EXPEDICION_DOC = PAIS.ID_PAIS "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
			"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "+
			"	ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
			"	JOIN PLANIFICACION.ENTIDAD ENTIDAD "+
			"	ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
			"	  JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
			"	  JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) ";
			
		
		}else{
			q="	SELECT  DISTINCT "+
			"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SIN_ENTIDAD.ENT_CODIGO AS COD_ENT, "+
			"	SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
			"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,oee.orden as oee_orden, "+
			"	SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "+
			" remuneracion.total_730(SNE.NEN_CODIGO,SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN ,"+desde+","+hasta+" ) as total "+
			"	 FROM REMUNERACION.REMUNERACIONES R      "+
			"	JOIN GENERAL.EMPLEADO_PUESTO E     on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO    "+
			"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
			"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET     JOIN GENERAL.PERSONA PERSONA  "+
			"	ON PERSONA.ID_PERSONA = E.ID_PERSONA     JOIN GENERAL.PAIS     ON PERSONA.ID_PAIS_EXPEDICION_DOC = PAIS.ID_PAIS  "+
			"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO     ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET  "+    
			"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE     ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO     "+ 
			"	JOIN PLANIFICACION.ENTIDAD ENTIDAD     ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD        "+
			"	JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD  "+
			"	JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) ";
				
		}
		q+=" where remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, persona.id_persona) >="+desde;
		q+=" and remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO, SIN_ENTIDAD.ENT_CODIGO, OEE.ORDEN, persona.id_persona) <= "+hasta;
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
			q+="	AND  SNE.NEN_CODIGO =  "+nivelEntidadOeeUtil.getCodNivelEntidad();
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
			q+="	AND  SIN_ENTIDAD.ENT_CODIGO = "+nivelEntidadOeeUtil.getCodSinEntidad();
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
			q+="	AND OEE.ID_CONFIGURACION_UO = "+nivelEntidadOeeUtil.getIdConfigCab();
		
		q+="	group by SNE.NEN_CODIGO , SNE.NEN_NOMBRE , SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO ,SIN_ENTIDAD.ENT_CODIGO, "+ 
		"	SIN_ENTIDAD.ENT_NOMBRE ,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN ,OEE.DENOMINACION_UNIDAD, 	 "+
		" PERSONA.DOCUMENTO_IDENTIDAD, PAIS.DESCRIPCION, 	PERSONA.NOMBRES, PERSONA.APELLIDOS, "+
		" PUESTO.DESCRIPCION, OEE.ORDEN  " ;
		if(tipo.equals("D"))
			q+=" , persona.id_persona";
		q+=" order by SNE.NEN_CODIGO,SIN_ENTIDAD.ENT_CODIGO,OEE.ORDEN ";
		
				
		return q;
	}
	
	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		desde = null;
		hasta = null;
		tipo = "D";
		primeraEntrada=true;
		cargarCabecera();

	}



	public Integer getDesde() {
		return desde;
	}

	public void setDesde(Integer desde) {
		this.desde = desde;
	}

	public Integer getHasta() {
		return hasta;
	}

	public void setHasta(Integer hasta) {
		this.hasta = hasta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
