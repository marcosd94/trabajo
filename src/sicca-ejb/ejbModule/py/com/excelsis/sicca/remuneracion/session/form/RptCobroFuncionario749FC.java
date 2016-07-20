package py.com.excelsis.sicca.remuneracion.session.form;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import enums.NivelAgrupacionEnum;
import enums.NivelCptEnum;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;


@Scope(ScopeType.CONVERSATION)
@Name("rptCobroFuncionario749FC")
public class RptCobroFuncionario749FC {
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
	@In(create = true)
	SinarhUtiles sinarhUtiles;
	@In(required = false)
	ReportUtilFormController reportUtilFormController;

	private NivelAgrupacionEnum nivelAgrupacion= NivelAgrupacionEnum.Todos;
	private String anio;
		
	
	private String tipo="D";
	private boolean primeraVez=true;
	private List<SelectItem> desde=  new Vector<SelectItem>();
	private List<SelectItem> hasta =  new Vector<SelectItem>();
	private Long valDesde;
	private Long valHasta;
	private boolean habPanel=false;
	private Integer codigoObj;
	private Long idCategoria;	
	private Long monto1;
	private Long monto2;
	private String puesto;

	/**
	 * Método que inicia los parametros
	 */
	public void init() {

		nivelEntidadOeeUtil.init();
		cargarCabecera();
		
	}
	
	public void initRptRemMes831() {

		nivelEntidadOeeUtil.init();
		//cargarCabecera();
		
	}

	
	private void cargarCabecera(){
		if(primeraVez){
			if(usuarioLogueado.getConfiguracionUoCab()!=null){
				nivelEntidadOeeUtil.setIdConfigCab(usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
				nivelEntidadOeeUtil.init2();
			}
			upDesdeHasta();	
			primeraVez=false;
		}
	}

	public List<SelectItem> buildCategoriasSI() {
		
		List<SelectItem> resultado = new ArrayList<SelectItem>();
		List<SinAnx> lista = traerCategorias();
		

		resultado.add(new SelectItem(null, "Todos"));
		for (SinAnx o : lista) {
			resultado.add(new SelectItem(o.getIdAnx(), o.getCtgCodigo()));
		}
		return resultado;
	}
	private List<SinAnx> traerCategorias(){
		if(nivelEntidadOeeUtil.getIdConfigCab()==null || codigoObj==null){
			 return new ArrayList<SinAnx>();
		}
		ConfiguracionUoCab oee= em.find(ConfiguracionUoCab.class, nivelEntidadOeeUtil.getIdConfigCab());
		return sinarhUtiles.obtenerListaSinAnx(null, null, codigoObj, null,null,oee.getCodigoSinarh() );
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
			reportUtilFormController.setNombreReporte("RPT_CU749");
			if ("EXCEL".equals(formato))
				reportUtilFormController.imprimirReporteXLS();
			else
				reportUtilFormController.imprimirReportePdf();
			

		} catch (Exception e) {
			e.printStackTrace();
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
		param.put("oeeUsuarioLogueado", em.find(ConfiguracionUoCab.class, id)
				.getDenominacionUnidad());
	
		param.put("nivelEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
		param.put("sinEntidadCod", nivelEntidadOeeUtil.getCodNivelEntidad());
		param.put("idOEE", nivelEntidadOeeUtil.getIdConfigCab());
		param.put("sql",consulta() );
		//param.put("sql",sql2);
	

		return param;
	}

	
	private String consulta(){
		String q="";
	
			q="	SELECT DISTINCT  "+
				"	SNE.NEN_CODIGO AS NEN_COD, SNE.NEN_NOMBRE AS NEN_NOM, SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO AS ENT_COD, "+
				"	SIN_ENTIDAD.ENT_NOMBRE AS ENT_NOMBRE,SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN AS ORDEN,OEE.DENOMINACION_UNIDAD AS OEE, "+
				"	SNE.NEN_CODIGO||'.'||SIN_ENTIDAD.ENT_CODIGO||'.'||OEE.ORDEN  || DESVINCULACION.GETDEPENDIENTES(UO.ID_CONFIGURACION_UO_DET) UO_CODIGO, UO.DENOMINACION_UNIDAD, "+
				"	PER.DOCUMENTO_IDENTIDAD CEDULA, "+
				"	PER.NOMBRES, PER.APELLIDOS, PUESTO.DESCRIPCION PUESTO, CASE WHEN E.CONTRATADO = TRUE THEN 'CONTRATADO' ELSE 'PERMANENTE' END MODALIDAD_OCUPACION, "+
				"	remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO,SIN_ENTIDAD.ENT_CODIGO, oee.orden, per.id_persona) as remuneracion, "+
				"	r.mes ||'/'|| r.anho as mes_anho, "+
				"	R.obj_codigo as objeto_codigo,  "+
				"	R.DESCRIP_CONCEPTO as descripcion_concepto, R.CATEGORIA, r.PRESUPUESTADO, "+ 
				"	r.DEVENGADO, date(r.fecha_alta) as fecha_alta, cpt.nivel as nivel, per.id_persona as id, R.id_remuneracion as id_remuneracion "+
				"	FROM REMUNERACION.REMUNERACIONES R "+
				"	JOIN GENERAL.EMPLEADO_PUESTO E "+
				"	on e.ID_EMPLEADO_PUESTO = R.ID_EMPLEADO_PUESTO "+
				"	JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO "+
				"	ON PUESTO.ID_PLANTA_CARGO_DET = E.ID_PLANTA_CARGO_DET " +
				"   JOIN PLANIFICACION.CPT AS CPT ON CPT.ID_CPT=PUESTO.ID_CPT "+
				"	JOIN GENERAL.PERSONA PER "+
				"	ON PER.ID_PERSONA = E.ID_PERSONA "+
				"	JOIN PLANIFICACION.CONFIGURACION_UO_DET UO "+
				"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
				"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE "+
				"	ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "+
				"	JOIN PLANIFICACION.ENTIDAD ENTIDAD  "+
				"	ON ENTIDAD.ID_ENTIDAD= OEE.ID_ENTIDAD "+
				"	  JOIN SINARH.SIN_ENTIDAD SIN_ENTIDAD ON SIN_ENTIDAD.ID_SIN_ENTIDAD=ENTIDAD.ID_SIN_ENTIDAD "+
				"	  JOIN SINARH.SIN_NIVEL_ENTIDAD SNE ON (SIN_ENTIDAD.ANI_ANIOPRE = SNE.ANI_ANIOPRE AND SIN_ENTIDAD.NEN_CODIGO = SNE.NEN_CODIGO) "; 
								
		
		
	
		
		String where=" where 1=1";
		
		if(!nivelAgrupacion.getValor().equals("T")){
			if(nivelAgrupacion.getValor().equals("J"))
			{
				where+=" and puesto.jefatura= true";
			}else{
				
				where+=" and puesto.jefatura= false";
				
			}
				
				
		}
		if(valDesde!=null){
			where+="  and cpt.nivel >="+valDesde;
		}
		if(valDesde!=null){
			where+=" and cpt.nivel <="+valDesde;
		}
		
		
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()!=null)
			where+="	AND  SNE.NEN_CODIGO =  "+nivelEntidadOeeUtil.getCodNivelEntidad();
		if(nivelEntidadOeeUtil.getIdSinEntidad()!=null)
			where+="	AND  SIN_ENTIDAD.ENT_CODIGO = "+nivelEntidadOeeUtil.getCodSinEntidad();
		if(nivelEntidadOeeUtil.getIdConfigCab()!=null)
			where+="	AND OEE.ID_CONFIGURACION_UO = "+nivelEntidadOeeUtil.getIdConfigCab();
		if(codigoObj!=null)
			where+=" and R.obj_codigo= "+codigoObj;
		if(idCategoria!=null)
		{
			SinAnx anx=em.find(SinAnx.class, idCategoria);
				where+= " and R.CATEGORIA =  '"+anx.getCtgCodigo()+"'";
		}
		if(puesto==null && puesto.trim().equals(""))
			where+=" and lower(PUESTO.DESCRIPCION) LIKE '%"+puesto.toLowerCase()+"%'";
		if(monto1!=null)
			where+=" AND remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO,SIN_ENTIDAD.ENT_CODIGO, oee.orden, per.id_persona) >= "+monto1;
		if(monto2!=null)
			where+=" AND remuneracion.valor_economico_remuneracion(SNE.NEN_CODIGO,SIN_ENTIDAD.ENT_CODIGO, oee.orden, per.id_persona) >= "+monto2;
		where+="	ORDER BY NEN_COD, ENT_COD, ORDEN, UO_CODIGO, PER.DOCUMENTO_IDENTIDAD ";
		
		return q+where;
	}
	
	public void limpiar() {
		nivelEntidadOeeUtil.limpiar();
		desde = null;
		hasta = null;
		tipo = "D";
		puesto=null;
		primeraVez=true;
		cargarCabecera();
		nivelAgrupacion=NivelAgrupacionEnum.Todos;
		habPanel=false;
		upDesdeHasta();
		codigoObj=null;
		buildCategoriasSI();

	}
	
	private void upDesdeHasta(){
		desdeSelectItem();
		hastaSelectItem();
			
	}

	public void changeCb(){
	
		if(nivelAgrupacion.getValor().equals("T"))
			habPanel=false;
		else
			habPanel=true;
		
		upDesdeHasta();
	}
	

	public List<SelectItem> desdeSelectItem() {
		desde=  new Vector<SelectItem>();
		desde.add(new SelectItem(null, "Todos"));
		if(nivelAgrupacion.getValor().equals("J")){// 	Si el usuario selecciona la opción Alta Gerencia
			for (int i = 7; i < 10; i++) {
				desde.add(new SelectItem(new Long(i+1), i+1+""));
			}
		}else if(nivelAgrupacion.getValor().equals("O")){// 	Si el usuario selecciona la opción Nivel Operativo: 
			for (int i = 0; i < 7; i++) {
				desde.add(new SelectItem(new Long(i+1), i+1+""));
			}
		}
		return desde;
	}
	public List<SelectItem> hastaSelectItem() {
		hasta=  new Vector<SelectItem>();
		
		hasta.add(new SelectItem(null, "Todos"));
		if(nivelAgrupacion.getValor().equals("J")){// 	Si el usuario selecciona la opción Alta Gerencia
			for (int i = 7; i < 10; i++) {
				hasta.add(new SelectItem(new Long(i+1), i+1+""));
			}
		}else if(nivelAgrupacion.getValor().equals("O")){// 	Si el usuario selecciona la opción Nivel Operativo: 
			for (int i = 0; i < 7; i++) {
				hasta.add(new SelectItem(new Long(i+1), i+1+""));
			}
		}
		return hasta;
	}
	
	public void seleccionReporteAltasYBajas() {
		if(nivelEntidadOeeUtil.getCodNivelEntidad() != null){
			imprimirReporteAltasYBajas();
		}
		else{
			imprimirReporteAltasYBajasSinNivel();
		}
	}
	
	
	
	public void imprimirReporteAltasYBajas() {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
				
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("anio", Integer.parseInt(this.anio));
		param.put("nnivel", nivelEntidadOeeUtil.getCodNivelEntidad().intValue());
		JasperReportUtils.respondPDF("remun_mes05d",	param, false, conn, usuarioLogueado);
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
public void imprimirReporteAltasYBajasSinNivel() {
		
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		Connection conn = JpaResourceBean.getConnection();
				
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("SUBREPORT_DIR",
				servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("anio", Integer.parseInt(this.anio));
		JasperReportUtils.respondPDF("remun_mes05d_sinNivel",	param, false, conn, usuarioLogueado);
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public List<SelectItem> getAnios() {
		List<SelectItem> si = new Vector<SelectItem>();
				
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		int anioHasta = cal.get(Calendar.YEAR);
		
		for (int i = anioHasta; i >= 2013 ; i--)
			si.add(new SelectItem(i));
		
		return si;
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

	public boolean isPrimeraVez() {
		return primeraVez;
	}


	public void setPrimeraVez(boolean primeraVez) {
		this.primeraVez = primeraVez;
	}


	public Long getValDesde() {
		return valDesde;
	}


	public void setValDesde(Long valDesde) {
		this.valDesde = valDesde;
	}


	public Long getValHasta() {
		return valHasta;
	}


	public void setValHasta(Long valHasta) {
		this.valHasta = valHasta;
	}


	public boolean isHabPanel() {
		return habPanel;
	}


	public void setHabPanel(boolean habPanel) {
		this.habPanel = habPanel;
	}


	public void setDesde(List<SelectItem> desde) {
		this.desde = desde;
	}


	public void setHasta(List<SelectItem> hasta) {
		this.hasta = hasta;
	}


	public Integer getCodigoObj() {
		return codigoObj;
	}


	public void setCodigoObj(Integer codigoObj) {
		this.codigoObj = codigoObj;
	}


	public Long getIdCategoria() {
		return idCategoria;
	}


	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}


	public Long getMonto1() {
		return monto1;
	}


	public void setMonto1(Long monto1) {
		this.monto1 = monto1;
	}


	public Long getMonto2() {
		return monto2;
	}


	public void setMonto2(Long monto2) {
		this.monto2 = monto2;
	}


	public String getPuesto() {
		return puesto;
	}


	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}


	public List<SelectItem> getDesde() {
		return desde;
	}


	public List<SelectItem> getHasta() {
		return hasta;
	}
	
	public String getAnio() {
		return anio;
	}


	public void setAnio(String anio) {
		this.anio = anio;
	}

	


	
	

}
