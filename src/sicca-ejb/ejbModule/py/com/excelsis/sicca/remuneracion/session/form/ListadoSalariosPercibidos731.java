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

import enums.NivelAgrupacionEnum;
import enums.NivelCptEnum;

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


@Scope(ScopeType.CONVERSATION)
@Name("listadoSalariosPercibidos731")
public class ListadoSalariosPercibidos731 {
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

	private NivelAgrupacionEnum nivelAgrupacion= NivelAgrupacionEnum.Todos;
	private NivelCptEnum desde;
	private NivelCptEnum hasta;
	private String tipo="D";
	private boolean primeraVez=true;
	private boolean habPanel=false;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		nivelEntidadOeeUtil.init();
		cargarCabecera();
		
	}

	
	private void cargarCabecera(){
		if(primeraVez){
			if(usuarioLogueado.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
			}
				
			primeraVez=false;
		}
	}


	
	private boolean validar(){
		em.clear();
		if(hasta != null && desde != null)
			{
				if(desde.getId().intValue()<hasta.getId().intValue()){
					statusMessages.add(Severity.ERROR,"El valor desde no puede ser mayor al valor hasta");
					return false;
				}
			}
		
		return true;
	}

	public void imprimir() throws Exception {
		validar();
		Connection conn = null;
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param = obtenerMapaParametros();
			if (param == null)
				return;
			conn = JpaResourceBean.getConnection();
			param.put("REPORT_CONNECTION", conn);
			
			JasperReportUtils.respondPDF(
						"RPT_CU731", param, false,
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
		if(desde!=null && desde.getId()!=0)
			param.put("desde", desde.getId());
		if(hasta!=null && hasta.getId()!=0)
			param.put("hasta", hasta.getId());
		param.put("nivelEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
		param.put("sinEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
		param.put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
		param.put("sql",consulta() );
		//param.put("sql",sql2);
	

		return param;
	}

	
	private String consulta(){
		String q="";
		String genericoJoin;
			q="	SELECT DISTINCT  "+
				"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD,SIN_ENTIDAD.ENT_CODIGO as cod_ent,OEE.ORDEN as oee_orden, "+
				"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "+
				"	PERSONA.DOCUMENTO_IDENTIDAD CEDULA, PAIS.DESCRIPCION, "+
				"	PERSONA.NOMBRES||' '||PERSONA.APELLIDOS as funcionario, cpt.denominacion CPT, cpt.nivel nivel, "+
				"	PUESTO.DESCRIPCION puesto, SUM(R.PRESUPUESTADO) VALOR_ECONOMICO, CASE WHEN PUESTO.JEFATURA = TRUE " +
				"	THEN 'ALTA GERENCIA' WHEN PUESTO.JEFATURA = FALSE  THEN 'OPERATIVO' END AS NIVEL_AGRUPAMIENTO, "+
				"	pais.descripcion as pais ";
			genericoJoin="	FROM REMUNERACION.REMUNERACIONES R "+
				"	JOIN GENERAL.EMPLEADO_PUESTO E "+
				"	on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO "+
				"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
				"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET "+
				"	JOIN GENERAL.PERSONA PERSONA "+
				"	ON PERSONA.ID_PERSONA = E.ID_PERSONA "+
				"	JOIN GENERAL.PAIS "+
				"	ON PERSONA.ID_PAIS_EXPEDICION_DOC = PAIS.ID_PAIS "+
				"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO"+ 
				"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
				"	LEFT JOIN PLANIFICACION.CPT CPT "+
				"	ON CPT.ID_CPT =  PUESTO.ID_CPT "+
				"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "+
				"	ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
				"	JOIN PLANIFICACION.ENTIDAD ENTIDAD "+
				"	ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
				"	  JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
				"	  JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO)";
								
		
		
	
		
		String where=" where 1=1";
		
		if(!nivelAgrupacion.getValor().equals("T")){
			if(nivelAgrupacion.getValor().equals("J"))
			{
				where+=" and puesto.jefatura= true";
			}else{
				
				where+=" and puesto.jefatura= false";
				if(desde.getId()!=0){
					where+="  and cpt.nivel >="+desde.getId();
				}
				if(desde.getId()!=0){
					where+=" and cpt.nivel <="+hasta.getValor();
				}
			}
				
				
		}
		
		
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
			where+="	AND  SNE.NEN_CODIGO =  "+nivelEntidadOeeUtil.getCodNivelEntidad();
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
			where+="	AND  SIN_ENTIDAD.ENT_CODIGO = "+nivelEntidadOeeUtil.getCodSinEntidad();
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
			where+="	AND OEE.ID_CONFIGURACION_UO = "+nivelEntidadOeeUtil.getIdConfigCab();
		
		where+="	group by SNE.NEN_CODIGO , SNE.NEN_NOMBRE , SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO , "+
			"	SIN_ENTIDAD.ENT_NOMBRE ,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN ,OEE.DENOMINACION_UNIDAD, "+
			"	PERSONA.DOCUMENTO_IDENTIDAD, PAIS.DESCRIPCION,SIN_ENTIDAD.ENT_CODIGO, "+
			"	PERSONA.NOMBRES, PERSONA.APELLIDOS, cpt.denominacion, cpt.nivel, PUESTO.DESCRIPCION,  puesto.jefatura,OEE.ORDEN "+
			"	order by SNE.NEN_CODIGO, ENT_COD, ORDEN,   NIVEL_AGRUPAMIENTO, cpt.nivel";

		q+=genericoJoin+where;
	
		return q;
	}
	
	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		desde = null;
		hasta = null;
		tipo = "D";
		primeraVez=true;
		cargarCabecera();
		nivelAgrupacion=NivelAgrupacionEnum.Todos;
		habPanel=false;

	}


	public void changeCb(){
		if(nivelAgrupacion.getValor().equals("O"))
			habPanel=true;
		else
			habPanel=false;
	}


	public NivelCptEnum getDesde() {
		return desde;
	}






	public void setDesde(NivelCptEnum desde) {
		this.desde = desde;
	}






	public NivelCptEnum getHasta() {
		return hasta;
	}






	public void setHasta(NivelCptEnum hasta) {
		this.hasta = hasta;
	}






	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}






	public NivelAgrupacionEnum getNivelAgrupacion() {
		return nivelAgrupacion;
	}






	public void setNivelAgrupacion(NivelAgrupacionEnum nivelAgrupacion) {
		this.nivelAgrupacion = nivelAgrupacion;
	}


	public boolean isHabPanel() {
		return habPanel;
	}


	public void setHabPanel(boolean habPanel) {
		this.habPanel = habPanel;
	}
	
	

}
