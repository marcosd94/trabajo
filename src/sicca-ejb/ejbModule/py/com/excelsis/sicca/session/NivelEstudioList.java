package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.NivelEstudio;
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

@Name("nivelEstudioList")
public class NivelEstudioList extends EntityQuery<NivelEstudio> {
	
	@In(required=false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	private static final String EJBQL = "select nivelEstudio from NivelEstudio nivelEstudio";

	private static final String[] RESTRICTIONS = {
		"lower(nivelEstudio.descripcion) like lower(concat('%', concat(#{nivelEstudioList.nivelEstudio.descripcion},'%')))",
		"nivelEstudio.activo = #{nivelEstudioList.nivelEstudio.activo}",
	};
	private static final String ORDER = "nivelEstudio.descripcion";
	private Estado estado= Estado.ACTIVO;

	private NivelEstudio nivelEstudio = new NivelEstudio();

	public NivelEstudioList() {
		nivelEstudio.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
	}
	
	public List<NivelEstudio> buscarResultados(){
		nivelEstudio.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<NivelEstudio> limpiarResultados(){
		nivelEstudio = new NivelEstudio();
		estado = Estado.ACTIVO;
		nivelEstudio.setActivo(estado.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	public void print() throws SQLException{
		Connection conn=null;
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
    	try {
    		Map<String, Object> param = new HashMap<String, Object>();
    		String estado = "Todos";
    		
    		StringBuffer whereClause = new StringBuffer();
    		if(nivelEstudio.getDescripcion() != null && !nivelEstudio.getDescripcion().isEmpty()){
    			if (!sufc.validarInput(nivelEstudio.getDescripcion(), TiposDatos.STRING.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "lower(ne.descripcion) like '%" + sufc.caracterInvalido(nivelEstudio.getDescripcion().trim().toLowerCase()) + "%' ");
    		}
    		if(nivelEstudio.getActivo() != null){
    			if (!sufc.validarInput(nivelEstudio.getActivo().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "ne.activo = "+nivelEstudio.getActivo());
    			estado = nivelEstudio.getActivo() ? "ACTIVO" : "INACTIVO";
    		}
			
			ServletContext servletContext = (ServletContext) FacesContext
									.getCurrentInstance().getExternalContext().getContext();

			
			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("estado", estado);
			
			conn = JpaResourceBean.getConnection();
			
			JasperReportUtils.respondPDF("ListarNivelEstudioUC374", param, false, conn, usuarioLogueado);
			
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

	public NivelEstudio getNivelEstudio() {
		return nivelEstudio;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
}
