package py.com.excelsis.sicca.session;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.JasperReportUtils;
import py.com.excelsis.sicca.util.JpaResourceBean;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("gradoSalarialList")
public class GradoSalarialList extends EntityQuery<GradoSalarial> {

	@In(create = true)
	StatusMessages statusMessages;
	
	@In(value = "entityManager")
	EntityManager em;
	
	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<GradoSalarial> listaGradoSalarials;
	
	private static final String EJBQL = "select gradoSalarial from GradoSalarial gradoSalarial";

	private static final String[] RESTRICTIONS = {
			"lower(gradoSalarial.usuAlta) like lower(concat(#{gradoSalarialList.gradoSalarial.usuAlta},'%'))",
			"lower(gradoSalarial.usuMod) like lower(concat(#{gradoSalarialList.gradoSalarial.usuMod},'%'))",
			"lower(gradoSalarial.agrupamientoCce.descripcion)  like concat('%',lower(concat(#{gradoSalarialList.descripcion},'%'))) ",
			"gradoSalarial.numero=#{gradoSalarialList.gradoSalarial.numero}",
			"gradoSalarial.activo=#{gradoSalarialList.estado.valor}", 
			"gradoSalarial.agrupamientoCce.idAgrupamientoCce = #{gradoSalarialList.idNivelGradoSalarial}", 
			"gradoSalarial.agrupamientoCce.tipoCce.idTipoCce = #{gradoSalarialList.idTipoCce}",
			
	};

	private GradoSalarial gradoSalarial = new GradoSalarial();
	private Estado estado= Estado.ACTIVO;
	private String descripcion;
	private static final String ORDER = "gradoSalarial.agrupamientoCce.descripcion";
	private Long idNivelGradoSalarial;
	private Long idTipoCce;
	

	public GradoSalarialList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<GradoSalarial> listaResultados() {
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
	public List<GradoSalarial> limpiar() {
		gradoSalarial= new GradoSalarial();
		estado = Estado.ACTIVO;
		descripcion= null;
		idNivelGradoSalarial=null;
		idTipoCce=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	

	
	public void print() {
		generarReporte();
	}
	
//	METODOS PRIVADOS
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void generarReporte() {
		try{
			
			ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			
			Connection conn = JpaResourceBean.getConnection();

			StringBuffer whereClause = new StringBuffer("WHERE 1 = 1");
			
			if(gradoSalarial.getNumero() != null)
				whereClause.append(" AND gs.numero = " + gradoSalarial.getNumero());
			if(descripcion != null && !descripcion.isEmpty())
				whereClause.append(" AND lower(ngs.descripcion) like '" + descripcion + "%'");
			if(estado != null && estado.getValor() != null)
				whereClause.append(" AND gs.activo = " + estado.getValor());
			
			HashMap param = new HashMap();	
			param.put("usuario", "ADMIN");
			param.put("SUBREPORT_DIR", new File(servletContext.getRealPath("/reports/jasper/")).getAbsolutePath() + "/");
			param.put("whereClause", whereClause.toString());
			
			JasperReportUtils.respondPDF("RptUC144_ListarGradoSalarial", param, false, conn, new Usuario());

			conn.close();
		   }catch(Exception e){
			   //System.out.println(e);
		   }
	}
	
	
//	GETTERS Y SETTERS
	public GradoSalarial getGradoSalarial() {
		return gradoSalarial;
	}
	
	public List<GradoSalarial> obtenerListaGradoSalarials(){
    	listaGradoSalarials = getResultList(); 
    	return listaGradoSalarials;
    }
	
    public List<GradoSalarial> getListaGradoSalarials() {
		return listaGradoSalarials;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getIdNivelGradoSalarial() {
		return idNivelGradoSalarial;
	}

	public void setIdNivelGradoSalarial(Long idNivelGradoSalarial) {
		this.idNivelGradoSalarial = idNivelGradoSalarial;
	}

	public Long getIdTipoCce() {
		return idTipoCce;
	}

	public void setIdTipoCce(Long idTipoCce) {
		this.idTipoCce = idTipoCce;
	}

    
}
