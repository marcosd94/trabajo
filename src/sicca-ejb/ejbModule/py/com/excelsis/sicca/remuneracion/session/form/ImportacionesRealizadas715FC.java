package py.com.excelsis.sicca.remuneracion.session.form;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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

import enums.EstadoImportacion;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;

import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.General;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("importacionesRealizadas715FC")
public class ImportacionesRealizadas715FC {

	
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

	
	private Long anio;
	private Long mes;
	private boolean primeraEntrada=true;
	private String tipo;
	private EstadoImportacion estadoImportacion=EstadoImportacion.todo;
	private List<SelectItem> motivoSelectItems= new ArrayList<SelectItem>();
	private Integer motivo;
	
	private List<SelectItem> capacitacionSelectItems = new ArrayList<SelectItem>();

	public void init() {
		
		if(primeraEntrada){
			cargarMotivo();
			limpiar();
			cargarCabecera();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
	

	}
	public void cargarCabecera() {

		/**
		 *  SE CARGA LA CABECERA OEE,DEL USUARIO LOGUEADO
		 * */
		ConfiguracionUoCab cabUsuario= em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		nivelEntidadOeeUtil.setIdConfigCab(cabUsuario.getIdConfiguracionUo());
		if(usuarioLogueado.getConfiguracionUoDet()!=null)
			nivelEntidadOeeUtil.setIdUnidadOrganizativa(usuarioLogueado.getConfiguracionUoDet().getIdConfiguracionUoDet());
		nivelEntidadOeeUtil.init2();
		/**
		 * **/
	}
	public void limpiar(){
		anio=Long.parseLong(General.anhoActual().toString());
		mes=null;
		nivelEntidadOeeUtil.limpiar();
		tipo="R";
		 estadoImportacion=EstadoImportacion.todo;
		
		
	}

	
	

	
	
	
	public void imprimir() throws Exception {
			
			if(anio==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo Año antes de realizar esta acci\u00F3n");
				return ;
			}
			if(mes==null){
				statusMessages.add(Severity.ERROR,"Debe ingresar el campo Mes antes de realizar esta acci\u00F3n");
				return ;
			}
			
			Connection conn = null;
			try {
	
				HashMap<String, Object> param = new HashMap<String, Object>();
				param=obtenerMapaParametros();
				if(param==null)
					return;
				conn = JpaResourceBean.getConnection();
				param.put("REPORT_CONNECTION", conn);
				
				String nombreReporte;
				if(tipo.equals("R"))
					nombreReporte="RPT_CU715";
				else
					nombreReporte="RPT_CU715_detallado";
				
				JasperReportUtils.respondPDF(
						nombreReporte, param, false,
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
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
	
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		String where= "";
		if(anio!=null ){
			
			if (!sufc.validarInput(anio.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			
			param.put("anio",anio);
		}
		if(mes!=null ){
			
			if (!sufc.validarInput(mes.toString(), TiposDatos.LONG.getValor(), null)) {
				return null;
			}
			Referencias referenciaMes= em.find(Referencias.class, mes);
			param.put("valMes", referenciaMes.getValorNum());
			param.put("mes", referenciaMes.getDescAbrev());
			where+=" WHERE   I.MES="+referenciaMes.getValorNum();
		}
	
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null){
			param.put("codEntidad",nivelEntidadOeeUtil.getIdSinEntidad());
			where+=" AND SIN_ENTIDAD.id_sin_entidad="+nivelEntidadOeeUtil.getIdSinEntidad();
		}
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null){
			param.put("codNivel",nivelEntidadOeeUtil.getIdSinNivelEntidad());
			where+=" AND sne.id_sin_nivel_entidad="+nivelEntidadOeeUtil.getIdSinNivelEntidad();
		}
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null){
			param.put("codOee",nivelEntidadOeeUtil.getIdConfigCab());
			where+=" AND OEE.id_configuracion_uo ="+nivelEntidadOeeUtil.getIdConfigCab();
		}
		
		
		
		
		
		if(estadoImportacion.getValor()!=null)
		{
			param.put("valEstado","%"+estadoImportacion.getValor());
			param.put("estado",estadoImportacion.getValor());
			where+="   AND I.ESTADO_IMPORT ="+estadoImportacion.getValor();
		}else
			param.put("estado","Todos");
		
	
		if(motivo!=null){
			SelectItem mo= motivoSelectItems.get(motivo);
			param.put("motivo", mo.getDescription());
			where+=" AND I.MOTIVO ='"+mo.getDescription()+"'";
		}else
			param.put("motivo", "%%");
		if(tipo.equals("D"))
			where+=" and r.dominio='MESES' ORDER BY  I.id_importacion,ORDEN";
		else{
			where+=" GROUP BY SNE.NEN_CODIGO, SNE.NEN_NOMBRE, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO, "+
					" SIN_ENTIDAD.ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN ,OEE.DENOMINACION_UNIDAD, I.ESTADO_IMPORT, "+
					" CASE WHEN I.MOTIVO IS NULL THEN 'SIN MOTIVO' ELSE I.MOTIVO END " ;
			where+=" ORDER BY I.ESTADO_IMPORT,ORDEN";
		}
			
		
		
		param.put("where",where);
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oeeUsuarioLogueado", cab.getDenominacionUnidad());
		
	
		
		return param;
	}
	
	
	public void cargarMotivo() {
		motivoSelectItems = new Vector<SelectItem>();
		motivoSelectItems.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		List<Object[]> lista = motivo();
		int index=0;
		for (Object obj : lista) {
			motivoSelectItems.add(new SelectItem(index, "" +obj.toString()));
			index++;
		}
		
			
		
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> motivo() {
		try {
			List<Object[]> motivoLists =
				em.createNativeQuery("SELECT "+  
			"	DISTINCT CASE WHEN I.MOTIVO IS NULL THEN 'SIN MOTIVO' ELSE I.MOTIVO END "+
			"	FROM REMUNERACION.IMPORTACION I ").getResultList();

			return motivoLists;
		} catch (Exception ex) {
			return new ArrayList<Object[]>();
		}
	}
	




	public List<SelectItem> getCapacitacionSelectItems() {
		return capacitacionSelectItems;
	}

	public void setCapacitacionSelectItems(List<SelectItem> capacitacionSelectItems) {
		this.capacitacionSelectItems = capacitacionSelectItems;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}
	public Long getAnio() {
		return anio;
	}
	public void setAnio(Long anio) {
		this.anio = anio;
	}
	public Long getMes() {
		return mes;
	}
	public void setMes(Long mes) {
		this.mes = mes;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public EstadoImportacion getEstadoImportacion() {
		return estadoImportacion;
	}
	public void setEstadoImportacion(EstadoImportacion estadoImportacion) {
		this.estadoImportacion = estadoImportacion;
	}
	public List<SelectItem> getMotivoSelectItems() {
		return motivoSelectItems;
	}
	public void setMotivoSelectItems(List<SelectItem> motivoSelectItems) {
		this.motivoSelectItems = motivoSelectItems;
	}
	public Integer getMotivo() {
		return motivo;
	}
	public void setMotivo(Integer motivo) {
		this.motivo = motivo;
	}

	
	
}
