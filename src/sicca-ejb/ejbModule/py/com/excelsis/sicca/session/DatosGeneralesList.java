package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;
import enums.TiposDatos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@Name("datosGeneralesList")
public class DatosGeneralesList extends EntityQuery<DatosGenerales> {

	private static final String EJBQL = "select datosGenerales from DatosGenerales datosGenerales";

	@In(required = false)
	Usuario usuarioLogueado;
	private static final String[] RESTRICTIONS = {
			"lower(datosGenerales.descripcion) like concat('%',lower(concat(#{datosGeneralesList.datosGenerales.descripcion},'%')))",
			"lower(datosGenerales.usuAlta) like lower(concat(#{datosGeneralesList.datosGenerales.usuAlta},'%'))",
			"lower(datosGenerales.usuMod) like lower(concat(#{datosGeneralesList.datosGenerales.usuMod},'%'))",
			"datosGenerales.activo =#{datosGeneralesList.estado.valor}",
			"datosGenerales.idDatosGenerales =#{datosGeneralesList.datosGenerales.idDatosGenerales}",		
	};

	private DatosGenerales datosGenerales = new DatosGenerales();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "datosGenerales.descripcion";
	public DatosGeneralesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public DatosGenerales datosGeneralesFind(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(1);
		List<DatosGenerales> listResult = getResultList(); 
		return listResult.isEmpty() ? null : listResult.get(0);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<DatosGenerales> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<DatosGenerales> limpiar() {
		datosGenerales= new DatosGenerales();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	 public void pdf() throws SQLException {
		 Connection conn =null; 
			try {
				HashMap<String, Object> mapa = obtenerMapaParametros();
				if (mapa == null) {
					throw new Exception(SeamResourceBundle.getBundle().getString("GENERICO_ERR_REPORTE_PARAM"));
				} else {
					conn = JpaResourceBean.getConnection();
					JasperReportUtils.respondPDF("RPT_CU55_Datos_Generales", obtenerMapaParametros(), false, conn, usuarioLogueado);
					conn.close();
				}
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.close();
		}
	        
	 }
	      
	 private HashMap<String, Object> obtenerMapaParametros() throws Exception {
		 SeguridadUtilFormController sufc =
				(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
		        HashMap<String, Object> param = new HashMap<String, Object>();
		        param.put("usuario", usuarioLogueado.getCodigoUsuario());
		      
		        StringBuffer sql = new StringBuffer(); 
				

				sql.append("  select  g.descripcion as descripcion,   ");
				sql.append(" case when g.activo = 'f' ");
				sql.append("  then 'NO' else 'SI' end as activo  ");
				sql.append(" from seleccion.datos_generales g   ");
				sql.append("  where 1=1   ");
				
		        
		        if(datosGenerales.getDescripcion()!=null && !datosGenerales.getDescripcion().trim().equals("")){
		        	if (!sufc.validarInput(datosGenerales.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
						return null;
					}
		        	param.put("descripcion",datosGenerales.getDescripcion());
		        	sql.append("   and  lower(g.descripcion) like  '%"+sufc.caracterInvalido(datosGenerales.getDescripcion().toLowerCase())+"%'");
		        }else
		        	param.put("descripcion","TODOS");
		        if(estado.getValor()!=null){
		        	if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
						return null;
					}
					sql.append("  and  g.activo = "+estado.getValor());
				}
		        	
		        sql.append("   order by g.descripcion   ");
		        param.put("sql", sql.toString());
		        param.put("estado",estado.getDescripcion());
		        
		        ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		        
		        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
				param.put("path_logo", servletContext.getRealPath("/img/"));
		        return param;
		    }

	//GETTER && SETTER
	public DatosGenerales getDatosGenerales() {
		return datosGenerales;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
}
