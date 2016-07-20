package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Referencias;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
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

@Name("referenciasList")
public class ReferenciasList extends EntityQuery<Referencias> {
	
	@In(required=false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;

	private static final String EJBQL = "select referencias from Referencias referencias";

	private static final String[] RESTRICTIONS = {
			"lower(referencias.dominio) like lower(concat('%',concat(#{referenciasList.referencias.dominio},'%')))",
			"referencias.descAbrev like #{referenciasList.referencias.descAbrev}",
			"lower(referencias.descLarga) like lower(concat('%',concat(#{referenciasList.referencias.descLarga},'%')))",
			"lower(referencias.valorAlf) like lower(concat('%',concat(#{referenciasList.referencias.valorAlf},'%')))",
			"referencias.activo = #{referenciasList.referencias.activo}",
	};
	private static final String ORDER = "referencias.dominio,referencias.descLarga";

	private Referencias referencias = new Referencias();

	public ReferenciasList() {
		referencias.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	public Referencias referencia(){
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(1);
		List<Referencias> listResult = getResultList(); 
		return listResult.isEmpty() ? null : listResult.get(0);
	}
	
	
	public List<Referencias> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}

	public List<Referencias> limpiarResultados(){
		referencias = new Referencias();
		referencias.setActivo(Estado.ACTIVO.getValor());
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER);
		return getResultList(); 
	}
	
	public void print() throws SQLException{
		Connection conn =null; 
		SeguridadUtilFormController sufc =
			(SeguridadUtilFormController) Component.getInstance(SeguridadUtilFormController.class, true);
    	try {
    		Map<String, Object> param = new HashMap<String, Object>();
    		String estado = "Todos";
    		
    		StringBuffer whereClause = new StringBuffer();
    		if(referencias.getDominio() != null && !referencias.getDominio().isEmpty()){
    			if (!sufc.validarInput(referencias.getDominio(), TiposDatos.STRING.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "lower(r.dominio) like '%" + sufc.caracterInvalido(referencias.getDominio()).trim().toLowerCase() + "%' ");
    		}
    		if(referencias.getDescLarga() != null && !referencias.getDescLarga().isEmpty()){
    			if (!sufc.validarInput(referencias.getDescLarga(), TiposDatos.STRING.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "lower(r.desc_larga) like '%" + sufc.caracterInvalido(referencias.getDescLarga()).trim().toLowerCase() + "%' ");
    		}
    		if(referencias.getActivo() != null){
    			if (!sufc.validarInput(referencias.getActivo().toString(), TiposDatos.BOOLEAN.getValor(), null)) {
    				return ;
    			}
    			whereClause.append((whereClause.toString().contains("WHERE") 
    					? " AND " : " WHERE ") + "r.activo = "+referencias.getActivo());
    			estado = referencias.getActivo() ? "ACTIVO" : "INACTIVO";
    		}
			
			ServletContext servletContext = (ServletContext) FacesContext
									.getCurrentInstance().getExternalContext().getContext();

			
			param.put("SUBREPORT_DIR", servletContext.getRealPath("/reports/jasper/"));
			param.put("path_logo", servletContext.getRealPath("/img/"));
			param.put("whereClause", whereClause.toString());
			param.put("usuario", usuarioLogueado.getCodigoUsuario());
			param.put("estado", estado);
			
			 conn = JpaResourceBean.getConnection();
			
			JasperReportUtils.respondPDF("ListarReferenciasUC151", param, false, conn, usuarioLogueado);
			
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
	public Referencias getReferencias() {
		return referencias;
	}
}
