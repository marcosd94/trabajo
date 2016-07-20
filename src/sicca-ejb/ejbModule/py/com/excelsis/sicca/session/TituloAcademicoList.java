package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.Component;
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

@Name("tituloAcademicoList")
public class TituloAcademicoList extends EntityQuery<TituloAcademico> {

	private static final String EJBQL = "select tituloAcademico from TituloAcademico tituloAcademico";

	private static final String[] RESTRICTIONS = {
			"lower(tituloAcademico.descripcion) like concat('%',lower(concat(#{tituloAcademicoList.tituloAcademico.descripcion},'%')))",
			"lower(tituloAcademico.usuAlta) like lower(concat(#{tituloAcademicoList.tituloAcademico.usuAlta},'%'))",
			"lower(tituloAcademico.usuMod) like lower(concat(#{tituloAcademicoList.tituloAcademico.usuMod},'%'))", 
			"tituloAcademico.activo=#{tituloAcademicoList.estado.valor}",		
			"tituloAcademico.especialidad.idEspecialidad=#{tituloAcademicoList.idEspecialidad}",	
			"tituloAcademico.nivelEstudio.idNivelEstudio=#{tituloAcademicoList.idNivelEstudio}",	
	};

	private TituloAcademico tituloAcademico = new TituloAcademico();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "tituloAcademico.descripcion";
	private Long idNivelEstudio;
	private Long idEspecialidad;
	
	public TituloAcademicoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<TituloAcademico> listaResultados() {
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
	public List<TituloAcademico> limpiar() {
		tituloAcademico= new TituloAcademico();
		estado = Estado.ACTIVO;
		idEspecialidad=null;
		idNivelEstudio=null;
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
					JasperReportUtils.respondPDF("RPT_CU260_titulo_academico", mapa, false, conn, new Usuario());
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
	        param.put("usuario", "ADMIN");
	      
	        StringBuffer sql = new StringBuffer(); 
			
			sql.append("  select ta.descripcion as tituloA, ");
			sql.append("  es.descripcion as especialidad, ");
			sql.append("  ne.descripcion as nivelEs,  ");
			sql.append(" case when ta.activo = 'f'   then 'NO' else 'SI' end as activo    ");
			sql.append(" From seleccion.titulo_academico ta ");
			sql.append(" left  join seleccion.especialidad es on es.id_especialidad=ta.id_especialidad  ");
			sql.append(" left  join seleccion.nivel_estudio ne on ne.id_nivel_estudio=ta.id_nivel_estudio ");
			sql.append("  where 1=1   ");
			if(tituloAcademico.getDescripcion()!=null && !tituloAcademico.getDescripcion().trim().isEmpty()){
				if (!sufc.validarInput(tituloAcademico.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
					return null;
				}
				sql.append("  and  lower(ta.descripcion) like '%"+sufc.caracterInvalido(tituloAcademico.getDescripcion()).toLowerCase()+"%'");
			}
			if(estado.getValor()!=null){
				if (!sufc.validarInput(estado.getValor().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
					return null;
				}
				sql.append("  and  ta.activo = "+estado.getValor());
			}
			if(idEspecialidad!=null){
				if (!sufc.validarInput(idEspecialidad.toString(), TiposDatos.LONG.getValor(), null)) {
					return null;
				}
				sql.append("  and   es.id_especialidad = "+idEspecialidad);
			}
			if(idNivelEstudio!=null){
				if (!sufc.validarInput(idNivelEstudio.toString(), TiposDatos.LONG.getValor(), null)) {
					return null;
				}
				sql.append("  and  ne.id_nivel_estudio = "+idNivelEstudio);
			}
			sql.append("   order by ta.descripcion  ");
	        param.put("sql", sql.toString());
	        if(tituloAcademico.getDescripcion()!=null)
	        	param.put("descripcion",!tituloAcademico.getDescripcion().equals("")? tituloAcademico.getDescripcion():"TODOS");
	        param.put("estado",estado.getDescripcion());
	        ServletContext servletContext = (ServletContext) FacesContext
			.getCurrentInstance().getExternalContext().getContext();

	        param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
	        return param;
	    }

//	GETTERS Y SETTERS
	public TituloAcademico getTituloAcademico() {
		return tituloAcademico;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public Long getIdNivelEstudio() {
		return idNivelEstudio;
	}
	public void setIdNivelEstudio(Long idNivelEstudio) {
		this.idNivelEstudio = idNivelEstudio;
	}
	public Long getIdEspecialidad() {
		return idEspecialidad;
	}
	public void setIdEspecialidad(Long idEspecialidad) {
		this.idEspecialidad = idEspecialidad;
	}
	
	
}
