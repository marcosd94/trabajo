package py.com.excelsis.sicca.juridicos.reportes.form;

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

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import antlr.collections.impl.LList;

import enums.PorInstitucion;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.ConfiguracionUoCabList;
import py.com.excelsis.sicca.session.EntidadList;
import py.com.excelsis.sicca.session.SinEntidadList;
import py.com.excelsis.sicca.session.SinNivelEntidadList;
import py.com.excelsis.sicca.session.util.ReportUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCAAppHelper;

@Scope(ScopeType.CONVERSATION)
@Name("sumariosIngresadosReportController")
public class SumariosIngresadosReportController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3965785215837021353L;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	
	@In(create = true)
	SinNivelEntidadList sinNivelEntidadList;
	private SinNivelEntidad nivelEntidad = new SinNivelEntidad();
	private SinEntidad sinEntidad = new SinEntidad();
	@In(create = true)
	SinEntidadList sinEntidadList;
	@In(create = true)
	EntidadList entidadList;
	@In(create = true)
	ConfiguracionUoCabList configuracionUoCabList;
	@In(required = false)
	Usuario usuarioLogueado;
	

	private Entidad entidad = new Entidad();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private String puesto;
	private String codigoUnidOrgDep;
	private Integer ordenUnidOrg;
	private Long idConfigCab;
	private Long idSinNivelEntidad;
	private Long idSinEntidad;
	private Integer anioActual;
	private Integer anho;
	private PorInstitucion porInstitucion;
	private Integer totalDetalle;
	private boolean habOee;
	

	/**
	 * Método que inicia los parametros
	 */
	public void init() {
		if(idSinNivelEntidad!=null){
			nivelEntidad= em.find(SinNivelEntidad.class, idSinNivelEntidad);
		}
		
		if (idSinEntidad != null) {
			sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		}
			
		if (idConfigCab != null) {
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);
			ordenUnidOrg = configuracionUoCab.getOrden();
		}
		
		if(idSinNivelEntidad==null && idSinEntidad==null&& idConfigCab==null ){
			nivelEntidad= new SinNivelEntidad();
			sinEntidad= new SinEntidad();
			configuracionUoCab= new ConfiguracionUoCab();
			ordenUnidOrg= null;
			cargarAnhoActual();
			anho=anioActual;
			porInstitucion=PorInstitucion.POR;
			habOee=true;
		}
			
	
		
	
	}

	

	

	public String getUrlToPageEntidad() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		return "/planificacion/searchForms/FindNivelEntidad.xhtml?from=juridicos/reportes/SumariosIngresados&codigoNivel="
				+ nivelEntidad.getNenCodigo();
	}

	public String getUrlToPageOee() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);
		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}

		sinEntidad = em.find(SinEntidad.class, idSinEntidad);

		String retorno = "/planificacion/searchForms/FindDependencias.xhtml?from=juridicos/reportes/SumariosIngresados&sinNivelEntidadIdSinNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad();
		if (idSinEntidad != null)
			retorno = retorno + "&sinEntidadIdSinEntidad="
					+ sinEntidad.getIdSinEntidad();
		
		retorno += "&anho="+anioActual;
		return retorno;
	}

	

	public String getUrlToPageDependencia() {
		if (idSinNivelEntidad == null) {
			statusMessages
					.add(Severity.ERROR, SICCAAppHelper
							.getBundleMessage("SinEntidad_msg_sin_nivel"));
			return null;
		}
		nivelEntidad = em.find(SinNivelEntidad.class, idSinNivelEntidad);

		if (idSinEntidad == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Entidad_msg_entidad"));
			return null;
		}
		sinEntidad = em.find(SinEntidad.class, idSinEntidad);
		if (configuracionUoCab == null
				|| configuracionUoCab.getIdConfiguracionUo() == null) {
			statusMessages.add(Severity.ERROR,
					SICCAAppHelper.getBundleMessage("Cabecera_msg_oee"));
			return null;
		}
		if (idConfigCab != null)
			configuracionUoCab = em.find(ConfiguracionUoCab.class, idConfigCab);

		return "/planificacion/configuracionUoDet/ListarConfiguracionUoDet.xhtml?from=juridicos/reportes/SumariosIngresados&idNivelEntidad="
				+ nivelEntidad.getIdSinNivelEntidad()
				+ "&sinEntidadIdSinEntidad="
				+ sinEntidad.getIdSinEntidad()
				+ "&idConfiguracionUoCab="
				+ configuracionUoCab.getIdConfiguracionUo();
	}

	public void searchAll() {
		configuracionUoCab= new ConfiguracionUoCab();
		nivelEntidad= new SinNivelEntidad();
		sinEntidad= new SinEntidad();
		codigoUnidOrgDep=null;
		ordenUnidOrg=null;
		entidad= new Entidad();
		idConfigCab=null;
		idSinEntidad=null;
		idSinNivelEntidad=null;
		anho=anioActual;
		porInstitucion=PorInstitucion.POR;
		habOee=true;
	}


	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	public void findNivelEntidad() {
		if (nivelEntidad.getNenCodigo() != null) {
			sinNivelEntidadList.getSinNivelEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			nivelEntidad = sinNivelEntidadList.nivelEntidadMaxAnho();
			if (nivelEntidad != null)
				idSinNivelEntidad = nivelEntidad.getIdSinNivelEntidad();
			else {
				nivelEntidad= new SinNivelEntidad();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}
		} else
			nivelEntidad = new SinNivelEntidad();
	}

	/**
	 * Método que busca la entidad correspondiente al codigo ingresado y el
	 * nivel
	 */
	public void findEntidad() {
		if (nivelEntidad.getNenCodigo() != null
				&& sinEntidad.getEntCodigo() != null) {
			sinEntidadList.getSinEntidad().setEntCodigo(
					sinEntidad.getEntCodigo());
			sinEntidadList.getSinEntidad().setNenCodigo(
					nivelEntidad.getNenCodigo());
			sinEntidad = sinEntidadList.entidadMaxAnho();

			if (sinEntidad != null && sinEntidad.getIdSinEntidad() != null) {
				idSinEntidad = sinEntidad.getIdSinEntidad();
				entidadList.getSinEntidad().setIdSinEntidad(
						sinEntidad.getIdSinEntidad());
				entidad = entidadList.getEntidadBySinEntidad();
			} else {
				entidad= new Entidad();
				sinEntidad= new SinEntidad();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		}
	}

	/**
	 * Método que busca la unidad organizativa correspondiente al codigo
	 * ingresado, a la entidad y al nivel
	 */
	public void findUnidadOrganizativa() {

		if (ordenUnidOrg != null) {
			configuracionUoCabList.getConfiguracionUoCab().setOrden(
					ordenUnidOrg);
			if (entidad != null && entidad.getIdEntidad()!=null) {
				Long id = entidad.getIdEntidad();
				configuracionUoCabList.setIdEntidad(id);
			}
			
			configuracionUoCab = configuracionUoCabList.searchUnidad();
			if (configuracionUoCab != null)
				idConfigCab = configuracionUoCab.getIdConfiguracionUo();
			else {
				configuracionUoCab= new ConfiguracionUoCab();
				statusMessages.add(Severity.ERROR,
						SICCAAppHelper.getBundleMessage("nivel_msg_1"));
				return;
			}

		} else
			configuracionUoCab = new ConfiguracionUoCab();
	}

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad
	 * organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */

	/**
	 * Método que busca la unidad organizativa dependiente de la unidad
	 * organizativa cabeza, la entidad el nivel y el codigo ingresado
	 */


	
	/**
	 * Método que busca el nivel correspondiente al codigo ingresado
	 */
	@SuppressWarnings("unchecked")
	public String findCodNivelEntidad(BigDecimal nencod) {
		SinNivelEntidad nivel = new SinNivelEntidad(); 
		if (nencod != null) {
			List<SinNivelEntidad> nivelEntidads= em.createQuery("Select n from SinNivelEntidad n " +
					" where n.nenCodigo= "+nencod +" and n.activo=true order by n.aniAniopre desc").getResultList();
			if(nivelEntidads.isEmpty())
				return "";
			else
				nivel = nivelEntidads.get(0);
		} else
			return "";
		return nivel.getNenCodigo() + "";
	}
	private List<Integer> obtenerListaCodigos(ConfiguracionUoDet uoDet,
			List<Integer> listCodigos) {
		uoDet.getDenominacionUnidad();
		if (listCodigos == null)
			listCodigos = new ArrayList<Integer>();

		listCodigos.add(0, uoDet.getOrden());
		if (uoDet.getConfiguracionUoDet() != null)
			obtenerListaCodigos(uoDet.getConfiguracionUoDet(), listCodigos);

		return listCodigos;
	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoCab padre,
			Integer orden) {
		String cad = "select det.* from planificacion.configuracion_uo_det det "
				+ " where det.id_configuracion_uo = "
				+ padre.getIdConfiguracionUo()
				+ " and det.orden = "
				+ orden
				+ " and det.id_configuracion_uo_det_padre is null";
		List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
		lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
				.getResultList();
		ConfiguracionUoDet actual = new ConfiguracionUoDet();
		if (lista.size() > 0) {
			actual = lista.get(0);
			return actual;
		}

		return null;

	}

	@SuppressWarnings("unchecked")
	private ConfiguracionUoDet buscarDetalle(ConfiguracionUoDet padre,
			Integer orden) {
		if (padre != null) {
			String cad = "select det.* from planificacion.configuracion_uo_det det "
					+ " where det.id_configuracion_uo_det_padre = "
					+ padre.getIdConfiguracionUoDet()
					+ " and det.orden = "
					+ orden;
			List<ConfiguracionUoDet> lista = new ArrayList<ConfiguracionUoDet>();
			lista = em.createNativeQuery(cad, ConfiguracionUoDet.class)
					.getResultList();
			ConfiguracionUoDet actual = new ConfiguracionUoDet();
			if (lista.size() > 0) {
				actual = lista.get(0);
				return actual;
			}
		}
		return null;

	}

	


	private void cargarAnhoActual() {
		Calendar cal = Calendar.getInstance();
		anioActual = cal.get(Calendar.YEAR);
	}

	

	

	public void imprimir() {
		try {
			if(anho==null){
				statusMessages.add("Debe ingresar el año, verifíque");
				return;
			}
			if(porInstitucion.getValor()&& idConfigCab==null){
				statusMessages.add("Debe especificar el OEE, verifique ");
				return;
			}
		
			HashMap<String, Object> param = new HashMap<String, Object>();
			Connection conn = JpaResourceBean.getConnection();
			ServletContext servletContext = (ServletContext) FacesContext
					.getCurrentInstance().getExternalContext().getContext();

			List<Map<String, Object>> listaReporte = new ArrayList<Map<String, Object>>();
			listaReporte.add(cargarDetalleCntExpedientes());
			
			param.put("SUBREPORT_DIR",
					servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("REPORT_CONNECTION", conn);
			param.put("anho", anho);
			
			String whereRecom=" AND    EXTRACT(YEAR FROM SU.FECHA_ALTA) = "+anho;;
			if(idConfigCab!=null)
				whereRecom+=" AND  UO.ID_CONFIGURACION_UO =  "+idConfigCab;
			
			whereRecom+=" GROUP BY SU.ESTADO ";
			
			param.put("whereRecom", whereRecom);
			
			String whereOee="	AND	EXTRACT(YEAR FROM SU.FECHA_ALTA) =  "+anho;
		
			if(idConfigCab!=null)
				whereOee+=" AND	UO.ID_CONFIGURACION_UO =  "+idConfigCab;
			
			whereOee+=" GROUP BY  OEE.DENOMINACION_UNIDAD ";
			
			param.put("whereOee", whereOee);
			
			
			param.put("total", totalDetalle);
			
			JasperReportUtils.respondPDF("RPT_CU459", false,

					listaReporte, param);

		
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void selecInst(){
		if(!porInstitucion.getValor()){
			habOee=false;
			idConfigCab=null;
			idSinEntidad=null;
			idSinNivelEntidad=null;
			configuracionUoCab= new ConfiguracionUoCab();
			nivelEntidad= new SinNivelEntidad();
			sinEntidad= new SinEntidad();
			codigoUnidOrgDep=null;
			ordenUnidOrg=null;
			entidad= new Entidad();
		}else
			habOee=true;
		
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> cargarDetalleCntExpedientes(){
		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapaReporte = new HashMap<String, Object>();
		String query= " SELECT   CASE EXTRACT (MONTH  FROM SU.FECHA_ALTA) " 
				+"		WHEN 1 THEN 'ENERO' "
				+"	WHEN 2 THEN 'FEBRERO'  "
				+"	WHEN 3 THEN 'MARZO'  "
				+"	WHEN 4 THEN 'ABRIL' "
				+"	WHEN 5 THEN 'MAYO'  "
				+"	WHEN 6 THEN 'JUNIO'  "
				+"	WHEN 7 THEN 'JULIO' "
				+"	WHEN 8 THEN 'AGOSTO' " 
				+"	WHEN 9 THEN 'SETIEMBRE' "
				+"	WHEN 10 THEN 'OCTUBRE'  "
				+"	WHEN 11 THEN 'NOVIEMRE' "
				+"	WHEN 12 THEN 'DICIEMBRE'  "
				+"	END as mes, "
				+"  COUNT (*) as total "
				+"	FROM    JURIDICOS.SUMARIO_CAB SU "
				+"	JOIN    GENERAL.EMPLEADO_PUESTO EMP "
				+"	ON      EMP.ID_EMPLEADO_PUESTO = SU.ID_EMPLEADO_PUESTO "
				+"	JOIN	PLANIFICACION.PLANTA_CARGO_DET PUESTO "
				+"	ON      PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "
				+"	JOIN 	PLANIFICACION.CONFIGURACION_UO_DET UO "
				+"	ON      UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
				+"	JOIN 	PLANIFICACION.CONFIGURACION_UO_CAB OEE "
				+"	ON      UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO" 
				+"  WHERE   SU.ESTADO IN ('AR', 'SO', 'SA') "
				+"  AND     EXTRACT(YEAR FROM SU.FECHA_ALTA) = "+anho;
		
		if(idConfigCab!=null){
			query+=" AND     UO.ID_CONFIGURACION_UO = "+idConfigCab;
		}
		query+=" GROUP BY EXTRACT (MONTH  FROM SU.FECHA_ALTA)";	
		totalDetalle=0;
		List<Object[]> lista =em.createNativeQuery(query).getResultList();
		if(lista.size()<12){
			   String [][] meses=new String[12][2];
			   meses[0][0]="ENERO";
			   meses[1][0]="FEBRERO";
			   meses[2][0]="MARZO";
			   meses[3][0]="ABRIL";
			   meses[4][0]="MAYO";
			   meses[5][0]="JUNIO";
			   meses[6][0]="JULIO";
			   meses[7][0]="AGOSTO";
			   meses[8][0]="SETIEMBRE";
			   meses[9][0]="OCTUBRE";
			   meses[10][0]="NOVIEMBRE";
			   meses[11][0]="DICIEMBRE";
			  
			   for (int i = 0; i < lista.size(); i++) {
				   Object[] lis= lista.get(i);
				   if(lis[1]!=null)
					   totalDetalle+=new Integer(lis[1].toString());
				   
				   if(lis[0].toString().equals("ENERO")){
					   if(lis[1]!=null)
						   meses[0][1]=lis[1]+"";
					   else
						   meses[0][1]="-";
				   }
				   if(lis[0].toString().equals("FEBRERO")){
					   if(lis[1]!=null)
						   meses[1][1]=lis[1]+"";
					   else
						   meses[1][1]="-";
				   }
				   if(lis[0].toString().equals("MARZO")){
					   if(lis[1]!=null)
						   meses[2][1]=lis[1]+"";
					   else
						   meses[2][1]="-";
				   }
				   if(lis[0].toString().equals("ABRIL")){
					   if(lis[1]!=null)
						   meses[3][1]=lis[1]+"";
					   else
						   meses[3][1]="-";
				   }
				   if(lis[0].toString().equals("MAYO")){
					   if(lis[1]!=null)
						   meses[4][1]=lis[1]+"";
					   else
						   meses[4][1]="-";
				   }
				   if(lis[0].toString().equals("JUNIO")){
					   if(lis[1]!=null)
						   meses[5][1]=lis[1]+"";
					   else
						   meses[5][1]="-";
				   }
				   if(lis[0].toString().equals("JULIO")){
					   if(lis[1]!=null)
						   meses[6][1]=lis[1]+"";
					   else
						   meses[6][1]="-";
				   }
				   if(lis[0].toString().equals("AGOSTO")){
					   if(lis[1]!=null)
						   meses[7][1]=lis[1]+"";
					   else
						   meses[7][1]="-";
				   }
				   if(lis[0].toString().equals("SETIEMBRE")){
					   if(lis[1]!=null)
						   meses[8][1]=lis[1]+"";
					   else
						   meses[8][1]="-";
				   }
				   if(lis[0].toString().equals("OCTUBRE")){
					   if(lis[1]!=null)
						   meses[9][1]=lis[1]+"";
					   else
						   meses[9][1]="-";
				   }
				   if(lis[0].toString().equals("NOVIEMBRE")){
					   if(lis[1]!=null)
						   meses[10][1]=lis[1]+"";
					   else
						   meses[10][1]="-";
				   }
				   if(lis[0].toString().equals("DICIEMBRE")){
					   if(lis[1]!=null)
						   meses[11][1]=lis[1]+"";
					   else
						   meses[11][1]="-";
				   }
				  
					   
			}
			for (int i = 0; i <12; i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("descripcion", new String(meses[i][0]));
				if(meses[i][1]!=null)
					map.put("cnt",new String(meses[i][1]));
				else
					map.put("cnt","-");
				
				listaDatosReporte.add(map);
				
			}
			   
		}else{

			for (Object[] obj : lista) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				if (obj[0] != null)
					map.put("descripcion", new String(obj[0].toString()));
				if (obj[1] != null)
					map.put("cnt", obj[1].toString());
				else
					map.put("cnt", "-");
				
				listaDatosReporte.add(map);
			}

		}
	
		
		mapaReporte.put("detalle", new JRMapCollectionDataSource(listaDatosReporte));
		return mapaReporte;
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> cargarDetalleRecomendaciones(){
		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapaReporte = new HashMap<String, Object>();
		String query= " SELECT CASE  WHEN SU.ESTADO = 'SA' THEN 'SANCION' "+
					" WHEN SU.ESTADO = 'SO' THEN 'SOBRESEIMIENTO' "+
					" WHEN SU.ESTADO = 'AR' THEN 'ARCHIVO' END, COUNT (*) as total "+
					" FROM JURIDICOS.SUMARIO_CAB SU "+
					" JOIN	GENERAL.EMPLEADO_PUESTO EMP ON EMP.ID_EMPLEADO_PUESTO = SU.ID_EMPLEADO_PUESTO "+
					" JOIN PLANIFICACION.PLANTA_CARGO_DET PUESTO ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "+
					" JOIN PLANIFICACION.CONFIGURACION_UO_DET UO  ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "+
					" JOIN PLANIFICACION.CONFIGURACION_UO_CAB OEE  ON UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO " +
					" WHERE  SU.ESTADO IN ('AR', 'SO', 'SA')  " +
					" AND    EXTRACT(YEAR FROM SU.FECHA_ALTA) = "+anho;
			if(idConfigCab!=null)
				query+=" AND  UO.ID_CONFIGURACION_UO =  "+idConfigCab;
			
			query+=" GROUP BY SU.ESTADO ";
			
							
		List<Object[]> lista =em.createNativeQuery(query).getResultList();
		
		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("descripcion", new String(obj[0].toString()));
			if (obj[1] != null)
				map.put("total", new Long(obj[1].toString()));
			
			listaDatosReporte.add(map);
		}
		mapaReporte.put("detalleRecomendaciones", new JRMapCollectionDataSource(listaDatosReporte));
		return mapaReporte;
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> cargarDetalleOrganizadores(){
		List<Map<String, Object>> listaDatosReporte = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapaReporte = new HashMap<String, Object>();
		String query= "SELECT OEE.DENOMINACION_UNIDAD as oee, COUNT (*) as total "
					+"	FROM  JURIDICOS.SUMARIO_CAB SU "
					+"	JOIN  GENERAL.EMPLEADO_PUESTO EMP "
					+"	ON    EMP.ID_EMPLEADO_PUESTO = SU.ID_EMPLEADO_PUESTO "
					+"	JOIN	PLANIFICACION.PLANTA_CARGO_DET PUESTO "
					+"	ON PUESTO.ID_PLANTA_CARGO_DET = EMP.ID_PLANTA_CARGO_DET "
					+"	JOIN	PLANIFICACION.CONFIGURACION_UO_DET UO "
					+"	ON UO.ID_CONFIGURACION_UO_DET = PUESTO.ID_CONFIGURACION_UO_DET "
					+"	JOIN	PLANIFICACION.CONFIGURACION_UO_CAB OEE "
					+"	ON	UO.ID_CONFIGURACION_UO = OEE.ID_CONFIGURACION_UO "
					+"	WHERE 	SU.ESTADO IN ('AR', 'SO', 'SA') "
					+"	AND	EXTRACT(YEAR FROM SU.FECHA_ALTA) =  "+anho;
			if(idConfigCab!=null)
				query+=" AND	UO.ID_CONFIGURACION_UO =  "+idConfigCab;
			
			query+=" GROUP BY  OEE.DENOMINACION_UNIDAD ";
			
							
		List<Object[]> lista =em.createNativeQuery(query).getResultList();
		
		for (Object[] obj : lista) {

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (obj[0] != null)
				map.put("descripicion", new String(obj[0].toString()));
			if (obj[1] != null)
				map.put("cantidad", new Long(obj[1].toString()));
			
			listaDatosReporte.add(map);
		}
		mapaReporte.put("detalleOrganizadores", new JRMapCollectionDataSource(listaDatosReporte));
		return mapaReporte;
	}
	
	
	
	
	
	

	/**
	 * Getters y Setters
	 * 
	 * @return
	 */

	public SinNivelEntidad getNivelEntidad() {
		return nivelEntidad;
	}

	public void setNivelEntidad(SinNivelEntidad nivelEntidad) {
		this.nivelEntidad = nivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	public String getCodigoUnidOrgDep() {
		return codigoUnidOrgDep;
	}

	public void setCodigoUnidOrgDep(String codigoUnidOrgDep) {
		this.codigoUnidOrgDep = codigoUnidOrgDep;
	}

	public Integer getOrdenUnidOrg() {
		return ordenUnidOrg;
	}

	public void setOrdenUnidOrg(Integer ordenUnidOrg) {
		this.ordenUnidOrg = ordenUnidOrg;
	}

	public Long getIdConfigCab() {
		return idConfigCab;
	}

	public void setIdConfigCab(Long idConfigCab) {
		this.idConfigCab = idConfigCab;
	}

	
	

	

	public Long getIdSinNivelEntidad() {
		return idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}





	public Integer getAnioActual() {
		return anioActual;
	}





	public void setAnioActual(Integer anioActual) {
		this.anioActual = anioActual;
	}





	public Integer getAnho() {
		return anho;
	}





	public void setAnho(Integer anho) {
		this.anho = anho;
	}





	public PorInstitucion getPorInstitucion() {
		return porInstitucion;
	}





	public void setPorInstitucion(PorInstitucion porInstitucion) {
		this.porInstitucion = porInstitucion;
	}





	public boolean isHabOee() {
		return habOee;
	}





	public void setHabOee(boolean habOee) {
		this.habOee = habOee;
	}


	

}
