package py.com.excelsis.sicca.seleccion.session.form;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import enums.Estado;
import enums.TiposDatos;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.PlantaCargoDetList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("informeCntPuestoEstado")
public class InformeCntPuestoEstado implements Serializable {

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
	
	@In(create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;

	private String puesto;
	private Long idEstado;
	private Integer anioActual=General.anhoActual();
	private boolean primeraEntrada=true;
	

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		nivelEntidadOeeUtil.init();
	}

	

	

	
	

	

	

	public void searchAll() {
	
		nivelEntidadOeeUtil.limpiar();
		
	}



	public void imprimir() {
		try {
			reportUtilFormController = (ReportUtilFormController) Component
			.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();
			
			
			reportUtilFormController.setNombreReporte("RPT_CU432");
			if(!cargarParametros())
				return ;
			Connection conn = JpaResourceBean.getConnection();
			reportUtilFormController.getParametros().put("REPORT_CONNECTION", conn);
			reportUtilFormController.imprimirReportePdf();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private boolean cargarParametros() throws Exception{
		Map<String,String> oeeMap=null;
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		
		String where=" where  sin_entidad.nen_codigo = nivel.nen_codigo "+
					" and  sin_entidad.ani_aniopre = nivel.ani_aniopre " ;
				
		reportUtilFormController.getParametros().put("nivel", "TODOS" );
		reportUtilFormController.getParametros().put("entidad", "TODOS" );
		reportUtilFormController.getParametros().put("oee", "TODOS" );
		reportUtilFormController.getParametros().put("uniOrg", "TODOS");
		
		if( nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null){
			if (!sufc.validarInput(nivelEntidadOeeUtil.getIdSinNivelEntidad().toString(), TiposDatos.LONG.getValor(), null)) {
				return false;
			}
			reportUtilFormController.getParametros().put("nroNivel",nivelEntidadOeeUtil.getCodNivelEntidad()+"" );
			reportUtilFormController.getParametros().put("nivel", nivelEntidadOeeUtil.getNombreNivelEntidad());
			where+=" and   nivel.nen_codigo ="+nivelEntidadOeeUtil.getCodNivelEntidad();
			
			
			if(nivelEntidadOeeUtil.getIdSinEntidad()!=null){
				if (!sufc.validarInput(nivelEntidadOeeUtil.getIdSinEntidad().toString(), TiposDatos.LONG.getValor(), null)) {
					return false;
				}
				reportUtilFormController.getParametros().put("nroEntidad",nivelEntidadOeeUtil.getCodNivelEntidad()+"."+ nivelEntidadOeeUtil.getCodSinEntidad() );
				reportUtilFormController.getParametros().put("entidad", nivelEntidadOeeUtil.getNombreSinEntidad() );
				where+=" and   entidad.id_sin_entidad = "+nivelEntidadOeeUtil.getIdSinEntidad();
				
				if(nivelEntidadOeeUtil.getIdConfigCab()!=null){
					if (!sufc.validarInput(nivelEntidadOeeUtil.getIdConfigCab().toString(), TiposDatos.LONG.getValor(), null)) {
						return false;
					}
					oeeMap=nivelEntidadOeeUtil.descNivelEntidadOee();
					reportUtilFormController.getParametros().put("nroOee", oeeMap.get("OEE_COD"));
					reportUtilFormController.getParametros().put("oee",nivelEntidadOeeUtil.getDenominacionUnidad()  );
					where+=" and  oee.id_configuracion_uo ="+nivelEntidadOeeUtil.getIdConfigCab();
				}
			}
				
			
			if(nivelEntidadOeeUtil.getIdUnidadOrganizativa()!=null) {
				if (!sufc.validarInput(nivelEntidadOeeUtil.getIdUnidadOrganizativa().toString(), TiposDatos.LONG.getValor(), null)) {
					return false;
				}
				reportUtilFormController.getParametros().put("nroUo",oeeMap.get("UND_ORG_COD"));
				reportUtilFormController.getParametros().put("uniOrg", nivelEntidadOeeUtil.getDenominacionUnidadOrgaDep() );
				where+=" and  uo.id_configuracion_uo_det ="+nivelEntidadOeeUtil.getIdUnidadOrganizativa();
			}
		}
		String contratado=where+" and  puesto.contratado = 'true'  GROUP BY estado.descripcion ";
		String permanente=where+"  and puesto.permanente  = 'true' GROUP BY estado.descripcion";
		reportUtilFormController.getParametros().put("whereContrado", contratado );
		reportUtilFormController.getParametros().put("wherePermanente", permanente);	
		
		return true;
	}
	
	
	
	
	
	

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */



	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public Long getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Long idEstado) {
		this.idEstado = idEstado;
	}

	public Integer getAnioActual() {
		return anioActual;
	}

	public void setAnioActual(Integer anioActual) {
		this.anioActual = anioActual;
	}

	public NivelEntidadOeeUtil getNivelEntidadOeeUtil() {
		return nivelEntidadOeeUtil;
	}

	public void setNivelEntidadOeeUtil(NivelEntidadOeeUtil nivelEntidadOeeUtil) {
		this.nivelEntidadOeeUtil = nivelEntidadOeeUtil;
	}

}
