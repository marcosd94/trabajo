package py.com.excelsis.sicca.movilidadLaboral.session.form;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import enums.PermanenteContratadoEnums;
import enums.SexoEnums;
import enums.SiNoTodosEnums;
import enums.TiposDatos;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SinarhUtiles;

@Scope(ScopeType.CONVERSATION)
@Name("rptEntidadCategoria671FC")
public class RptEntidadCategoria671FC {

	
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
	
	@In(create = true)
	SinarhUtiles sinarhUtiles;



	private boolean primeraEntrada=true;
	private Date desde;
	private Date hasta;
	private Long idTipoMovilidad;
	private Integer codigoObj;
	private Long idCategoria;	
	private List<SelectItem> tipoMovilidadSI;
	private Long monto1;
	private Long monto2;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
 	public void init() {

		
		if(primeraEntrada){
			cargarCabecera();
			limpiar();
			primeraEntrada=false;
		}
		nivelEntidadOeeUtil.init();
	

	}

	public void limpiar(){
		desde=null;
		hasta=null;
		idTipoMovilidad=null;
		monto1=null;
		monto2=null;
		codigoObj=null;
		idCategoria=null;
	}
	
	private boolean validaciones(){
		if(nivelEntidadOeeUtil.getIdSinNivelEntidad()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Nivel");
			return false;
		}
		if(nivelEntidadOeeUtil.getIdSinEntidad()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar la Entidad");
			return false;
		}
		if(nivelEntidadOeeUtil.getIdConfigCab()==null){
			statusMessages.add(Severity.ERROR,"Debe Seleccionar el Oee");
			return false;
		}
		if(desde==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar a fecha desde");
			return false;
		}
		if(hasta==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar a fecha hasta");
			return false;
		}
		if(desde.after(hasta)){
			statusMessages.add(Severity.ERROR,"La fecha desde no puede ser mayo a la fecha hasta");
			return false;
		}
		
		if(codigoObj==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar el objeto gasto");
			return false;
		}
		if(monto1==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar el monto desde");
			return false;
		}
		if(monto2==null){
			statusMessages.add(Severity.ERROR,"Debe ingresar el monto hasta");
			return false;
		}
		return true;
		
	}

	private String obtenerSql1() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String sql = " SELECT DISTINCT 	sne.nen_codigo AS nen_cod,  "+
					"	sne.nen_nombre AS nen_nom, "+
					"	sne.nen_codigo||'.'||sin_entidad.ent_codigo AS ent_cod, "+
					"	sin_entidad.ent_nombre AS ent_nombre, "+
					"	sne.nen_codigo||'.'||sin_entidad.ent_codigo||'.'||oee.orden AS orden, "+
					"	oee.denominacion_unidad AS oee, "+
					"	persona.nombres, persona.apellidos, "+ 
					"	puesto.descripcion puesto,  "+
					"	de.descripcion, "+
					"	SUM(r.monto) monto "+
					"	FROM GENERAL.EMPLEADO_CONCEPTO_PAGO r "+
					"	JOIN GENERAL.EMPLEADO_PUESTO e on e.id_empleado_puesto = r.id_empleado_puesto "+
					"	JOIN SELECCION.DATOS_ESPECIFICOS de on (e.id_datos_esp_tipo_ingreso_movilidad = de.id_datos_especificos) "+
					"	JOIN SELECCION.DATOS_GENERALES dg on (de.id_datos_generales = dg.id_datos_generales) "+
					"	JOIN PLANIFICACION.PLANTA_CARGO_DET puesto on puesto.id_planta_cargo_det = e.id_planta_cargo_det "+
					"	JOIN GENERAL.PERSONA persona on persona.id_persona = e.id_persona "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_DET uo on uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB oee on uo.id_configuracion_uo = oee.id_configuracion_uo "+
					"	JOIN PLANIFICACION.ENTIDAD entidad on entidad.id_entidad= oee.id_entidad "+
					"	JOIN SINARH.SIN_ENTIDAD sin_entidad on sin_entidad.id_sin_entidad=entidad.id_sin_entidad "+
					"	JOIN SINARH.SIN_NIVEL_ENTIDAD sne on (sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo) "+
					"	WHERE  date(E.fecha_inicio) >= to_date('" + sdf.format(desde)
					+ "','DD-MM-YYYY') "+
					"	AND  date(E.fecha_inicio) <= to_date('" + sdf.format(hasta)
					+ "','DD-MM-YYYY') "+
					"	AND  r.obj_codigo =  "+codigoObj;
					if(idCategoria!=null){
						SinAnx anx=em.find(SinAnx.class, idCategoria);
						sql+="	AND  r.categoria = '"+anx.getCtgCodigo()+"'";
					}
					if(idTipoMovilidad!=null){
						sql+=" and  e.id_datos_esp_tipo_ingreso_movilidad="+idTipoMovilidad;
					}
			
					
					sql+="	AND sne.nen_codigo = "+nivelEntidadOeeUtil.getCodNivelEntidad()+
					"	AND sin_entidad.ent_codigo ="+nivelEntidadOeeUtil.getCodSinEntidad()+
					"	AND oee.orden   =  "+nivelEntidadOeeUtil.getOrdenUnidOrg()+
					"	AND  dg.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' "+ 
					"	AND de.valor_alf = 'MOV'  "+
					"	GROUP BY sne.nen_codigo , sne.nen_nombre , sne.nen_codigo||'.'||sin_entidad.ent_codigo , "+
					"	sin_entidad.ent_nombre, sne.nen_codigo||'.'||sin_entidad.ent_codigo||'.'||oee.orden, oee.denominacion_unidad, "+
					"	persona.nombres, persona.apellidos, puesto.descripcion, de.descripcion 	"; 
		
		return sql;
	}
	private String obtenerSql2() throws Exception {
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		String sql = " SELECT DISTINCT  "+
					"	sne.nen_codigo AS nen_cod, "+ 
					"	sne.nen_nombre AS nen_nom,  "+
					"	sne.nen_codigo||'.'||sin_entidad.ent_codigo AS ent_cod,  "+
					"	sin_entidad.ent_nombre AS ent_nombre,  "+
					"	sne.nen_codigo||'.'||sin_entidad.ent_codigo||'.'||oee.orden AS orden, "+
					"	oee.denominacion_unidad AS oee, "+
					"	persona.nombres, persona.apellidos, "+ 
					"	puesto.descripcion puesto,  "+
					"	de.descripcion, "+
					"	SUM(r.monto) montoTotal "+
					"	FROM GENERAL.EMPLEADO_CONCEPTO_PAGO r "+
					"	JOIN GENERAL.EMPLEADO_PUESTO e on e.id_empleado_puesto = r.id_empleado_puesto "+
					"	JOIN SELECCION.DATOS_ESPECIFICOS de on (e.id_datos_esp_tipo_ingreso_movilidad = de.id_datos_especificos) "+
					"	JOIN SELECCION.DATOS_GENERALES dg on (de.id_datos_generales = dg.id_datos_generales) "+
					"	JOIN PLANIFICACION.PLANTA_CARGO_DET puesto on puesto.id_planta_cargo_det = e.id_planta_cargo_det "+
					"	JOIN GENERAL.PERSONA persona on persona.id_persona = e.id_persona "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_DET uo on uo.id_configuracion_uo_det = puesto.id_configuracion_uo_det "+
					"	JOIN PLANIFICACION.CONFIGURACION_UO_CAB oee on uo.id_configuracion_uo = oee.id_configuracion_uo "+
					"	JOIN PLANIFICACION.ENTIDAD entidad on entidad.id_entidad= oee.id_entidad "+
					"	JOIN SINARH.SIN_ENTIDAD sin_entidad on sin_entidad.id_sin_entidad=entidad.id_sin_entidad "+
					"	JOIN SINARH.SIN_NIVEL_ENTIDAD sne on (sin_entidad.ani_aniopre = sne.ani_aniopre AND sin_entidad.nen_codigo = sne.nen_codigo) "+
					"	WHERE  date(E.fecha_inicio) >= to_date('" + sdf.format(desde)
					+ "','DD-MM-YYYY') "+
					"	AND  date(E.fecha_inicio) <= to_date('" + sdf.format(hasta)
					+ "','DD-MM-YYYY') "+
					"	AND  r.obj_codigo =  "+codigoObj;
					if(idCategoria!=null){
						SinAnx anx=em.find(SinAnx.class, idCategoria);
						sql+="	AND  r.categoria = '"+anx.getCtgCodigo()+"'";
					}
					if(idTipoMovilidad!=null){
						sql+=" and  e.id_datos_esp_tipo_ingreso_movilidad="+idTipoMovilidad;
					}
			
					
					sql+="	AND sne.nen_codigo = "+nivelEntidadOeeUtil.getCodNivelEntidad()+
					"	AND sin_entidad.ent_codigo ="+nivelEntidadOeeUtil.getCodSinEntidad()+
					"	AND oee.orden   =  "+nivelEntidadOeeUtil.getOrdenUnidOrg()+
					"	AND  dg.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' "+ 
					"	AND de.valor_alf = 'MOV'  "+
					"	GROUP BY sne.nen_codigo , sne.nen_nombre , sne.nen_codigo||'.'||sin_entidad.ent_codigo , "+
					"	sin_entidad.ent_nombre, sne.nen_codigo||'.'||sin_entidad.ent_codigo||'.'||oee.orden, oee.denominacion_unidad, "+
					"	persona.nombres, persona.apellidos, puesto.descripcion, de.descripcion 	"; 
		
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
	
	
	
	public void imprimirPorCategoria() throws Exception {
			if(!validaciones())
				return ;
			
			
			try {
				reportUtilFormController =
					(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
				reportUtilFormController.init();
				HashMap<String, Object> param = new HashMap<String, Object>();
				param=obtenerMapaParametros("1");
				if(param==null)
					return;
				reportUtilFormController.setParametros(param);
				reportUtilFormController.setNombreReporte("RPT_CU671");
				reportUtilFormController.imprimirReportePdf();
				

			} catch (Exception e) {
				e.printStackTrace();
			} 
	
		}
	public void imprimirPorRango() throws Exception {
		if(!validaciones())
			return ;
		
		
		try {
			reportUtilFormController =
				(ReportUtilFormController) Component.getInstance(ReportUtilFormController.class, true);
			reportUtilFormController.init();
			HashMap<String, Object> param = new HashMap<String, Object>();
			param=obtenerMapaParametros("2");
			if(param==null)
				return;
			reportUtilFormController.setParametros(param);
			reportUtilFormController.setNombreReporte("RPT_CU671_2");
			reportUtilFormController.imprimirReportePdf();
			

		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	private HashMap<String, Object> obtenerMapaParametros(String tipo) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		ServletContext servletContext =
			(ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("usuario", usuarioLogueado.getCodigoUsuario());
	
		param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
		param.put("path_logo", servletContext.getRealPath("/img/"));
		param.put("desde",desde);
		param.put("hasta",hasta);
	
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
			
		if(idCategoria!=null){
			SinAnx anx=em.find(SinAnx.class, idCategoria);
			param.put("categoria", anx.getCtgCodigo());
		}
		
		param.put("objeto", codigoObj);
		param.put("desde", monto1);
		param.put("hasta", monto2);
		
		
		ConfiguracionUoCab cab=em.find(ConfiguracionUoCab.class, usuarioLogueado.getConfiguracionUoCab().getIdConfiguracionUo());
		param.put("oeeUsuarioLogueado", cab.getDenominacionUnidad());
		String sql=null;
		if(tipo.equals("1"))
			sql=obtenerSql1();
		else
			sql=obtenerSql2();
		if(sql==null)
			return null;
		param.put("sql",sql);
		
		return param;
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
	
	
	public List<SelectItem> buildTipoMovilidadSI() {
		List<DatosEspecificos> lista = traerTipoMovilidad();
		if (tipoMovilidadSI == null)
			tipoMovilidadSI = new ArrayList<SelectItem>();
		else
			tipoMovilidadSI.clear();

		tipoMovilidadSI.add(new SelectItem(null, "Todos"));
		for (DatosEspecificos o : lista) {
			tipoMovilidadSI.add(new SelectItem(o.getIdDatosEspecificos(), o.getDescripcion()));
		}
		return tipoMovilidadSI;
	}
	private List<DatosEspecificos> traerTipoMovilidad() {
		Query q =
			em.createQuery("select DatosEspecificos from DatosEspecificos DatosEspecificos "
				+ " where DatosEspecificos.valorAlf ='MOV' and DatosEspecificos.activo is true "
				+ " and DatosEspecificos.datosGenerales.descripcion = 'TIPOS DE INGRESOS Y MOVILIDAD' ");
		return q.getResultList();
	}
	
	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public boolean isPrimeraEntrada() {
		return primeraEntrada;
	}

	public void setPrimeraEntrada(boolean primeraEntrada) {
		this.primeraEntrada = primeraEntrada;
	}

	public List<SelectItem> getTipoMovilidadSI() {
		return tipoMovilidadSI;
	}

	public void setTipoMovilidadSI(List<SelectItem> tipoMovilidadSI) {
		this.tipoMovilidadSI = tipoMovilidadSI;
	}

	public Long getIdTipoMovilidad() {
		return idTipoMovilidad;
	}

	public void setIdTipoMovilidad(Long idTipoMovilidad) {
		this.idTipoMovilidad = idTipoMovilidad;
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

}
