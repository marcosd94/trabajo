package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.InstitucionEducativa;
import py.com.excelsis.sicca.entity.Pais;
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
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;
import enums.TiposDatos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

@Name("institucionEducativaList")
public class InstitucionEducativaList extends EntityQuery<InstitucionEducativa> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	private static final String EJBQL = "select institucionEducativa from InstitucionEducativa institucionEducativa";

	private static final String[] RESTRICTIONS = {
			"lower(institucionEducativa.descripcion) like lower(concat('%',concat(#{institucionEducativaList.institucionEducativa.descripcion},'%')))",
			"institucionEducativa.pais.idPais = #{institucionEducativaList.pais.idPais}",
			"institucionEducativa.activo = #{institucionEducativaList.institucionEducativa.activo}", };
	
	private static final String ORDER = "institucionEducativa.descripcion";

	private InstitucionEducativa institucionEducativa = new InstitucionEducativa();
	private Pais pais = new Pais();

	public InstitucionEducativaList() {
		institucionEducativa.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	
	public List<InstitucionEducativa> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<InstitucionEducativa> limpiarResultados(){
		institucionEducativa = new InstitucionEducativa();
		pais = new Pais();
		institucionEducativa.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	public void print() throws SQLException{
		Connection conn = null;
    	try {
    		SeguridadUtilFormController sufc =
    			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
    		Map<String, Object> param = new HashMap<String, Object>();
    		String estado = "Todos";
    		
    		StringBuffer whereClause = new StringBuffer();
    		if(institucionEducativa.getDescripcion() != null && !institucionEducativa.getDescripcion().isEmpty()){
    			if (!sufc.validarInput(institucionEducativa.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "lower(ie.descripcion) like '%" + sufc.caracterInvalido(institucionEducativa.getDescripcion().trim().toLowerCase()) + "%' ");
    		}
    		if(pais.getIdPais() != null){
    			if (!sufc.validarInput(pais.getIdPais().toString(), TiposDatos.LONG.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "p.id_pais = "+pais.getIdPais());
    		}    		
    		if(institucionEducativa.getActivo() != null){
    			if (!sufc.validarInput(institucionEducativa.getActivo().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "ie.activo = "+institucionEducativa.getActivo());
    			estado = institucionEducativa.getActivo() ? "ACTIVO" : "INACTIVO";
    		}
			
			ServletContext servletContext = (ServletContext) FacesContext
									.getCurrentInstance().getExternalContext().getContext();

			
			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("estado", estado);
			
			 conn = JpaResourceBean.getConnection();
			
			JasperReportUtils.respondPDF("ListarInstitucionEducativaUC378", param, false, conn, usuarioLogueado);
			
			JpaResourceBean.closeConnection(conn);
			
		}catch (Exception e) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("seam_error_inesperado"));
			e.printStackTrace();
			return;
		}finally {
			if (conn != null)
				conn.close();
		}
	}

//	GETTERS Y SETTERS
	public InstitucionEducativa getInstitucionEducativa() {
		return institucionEducativa;
	}

	public Pais getPais() {
		return pais;
	}
	
}
