package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.DatosGenerales;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@Scope(ScopeType.CONVERSATION)
@Name("datosEspecificosList")
public class DatosEspecificosList extends EntityQuery<DatosEspecificos> {

	private static final String EJBQL = "select datosEspecificos from DatosEspecificos datosEspecificos";

	@In(required = false)
	Usuario usuarioLogueado;
	
	private static final String[] RESTRICTIONS = {
			"lower(datosEspecificos.descripcion) like concat('%',lower(concat(#{datosEspecificosList.datosEspecificos.descripcion},'%')))",
			"lower(datosEspecificos.valorAlf) like lower(concat(#{datosEspecificosList.datosEspecificos.valorAlf},'%'))",
			"lower(datosEspecificos.datosGenerales.descripcion) = lower(#{datosEspecificosList.datosGenerales.descripcion})",
			"lower(datosEspecificos.usuAlta) like lower(concat(#{datosEspecificosList.datosEspecificos.usuAlta},'%'))",
			"lower(datosEspecificos.usuMod) like lower(concat(#{datosEspecificosList.datosEspecificos.usuMod},'%'))", 
			"datosEspecificos.datosGenerales.idDatosGenerales =#{datosEspecificosList.idDatosGenerales} ",
			"datosEspecificos.activo=#{datosEspecificosList.estado.valor}",
	};

	private DatosEspecificos datosEspecificos = new DatosEspecificos();
	private DatosGenerales datosGenerales = new DatosGenerales();
	
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "datosEspecificos.descripcion";
	private Long idDatosGenerales;
	
	public DatosEspecificosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<DatosEspecificos> listaResultados() {
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
	public List<DatosEspecificos> limpiar() {
		datosEspecificos= new DatosEspecificos();
		estado = Estado.ACTIVO;
		idDatosGenerales=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	 public void pdf() {
		 try {
			 
				Connection conn = JpaResourceBean.getConnection();
				JasperReportUtils.respondPDF("RPT_CU133_datos_especificos", obtenerMapaParametros(), false, conn, usuarioLogueado);
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	        
	 }
	
	
	 private HashMap<String, Object> obtenerMapaParametros() {
	        HashMap<String, Object> param = new HashMap<String, Object>();
	        param.put("usuario", usuarioLogueado.getCodigoUsuario());
	      
	        StringBuffer sql = new StringBuffer(); 
			
			sql.append(" select de.descripcion as descripcion,   ");
			sql.append(" case when de.activo = 'f'   then 'NO' else 'SI' end as activo ,   ");
			sql.append("  dg.descripcion as general ");
			sql.append(" from seleccion.datos_especificos de  ");
			sql.append(" join seleccion.datos_generales dg on dg.id_datos_generales=de.id_datos_generales   ");
			sql.append("  where 1=1   ");
			if(datosEspecificos.getDescripcion()!=null && !datosEspecificos.getDescripcion().trim().isEmpty()){
				sql.append("  and  lower(de.descripcion) like '"+datosEspecificos.getDescripcion().toLowerCase()+"%'");
			}
			if(idDatosGenerales!=null){
				sql.append("  and  dg.id_datos_generales ="+idDatosGenerales);
			}
			if(estado.getValor()!=null){
				sql.append("  and  de.activo = "+estado.getValor());
			}
			sql.append("   group by de.descripcion, de.activo,dg.descripcion   ");
			sql.append("   order by dg.descripcion   ");
	        param.put("sql", sql.toString());
	        if(datosEspecificos.getDescripcion()!=null)
	        	param.put("descripcion",!datosEspecificos.getDescripcion().equals("")? datosEspecificos.getDescripcion():"TODOS");
	        param.put("estado",estado.getDescripcion());
	    	ServletContext servletContext = (ServletContext) FacesContext
			.getCurrentInstance().getExternalContext().getContext();

	        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
	        return param;
	    }

//	GETTERS Y SETTERS
	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}
	public DatosGenerales getDatosGenerales() {
		return datosGenerales;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Long getIdDatosGenerales() {
		return idDatosGenerales;
	}
	public void setIdDatosGenerales(Long idDatosGenerales) {
		this.idDatosGenerales = idDatosGenerales;
	}
	
	
	
}
