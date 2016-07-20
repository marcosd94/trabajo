package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("valorNivelOrgList")
public class ValorNivelOrgList extends EntityQuery<ValorNivelOrg> {

	
	
	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<ValorNivelOrg> listaValorNivelOrgs;
	
	private static final String EJBQL = "select valorNivelOrg from ValorNivelOrg valorNivelOrg";

	private static final String[] RESTRICTIONS = {
			"lower(valorNivelOrg.descripcion) like concat('%',lower(concat(#{valorNivelOrgList.valorNivelOrg.descripcion},'%')))",
			"valorNivelOrg.activo = #{valorNivelOrgList.estado.valor}",
			"valorNivelOrg.contenidoFuncional.idContenidoFuncional = #{valorNivelOrgList.contenidoFuncional.idContenidoFuncional}",
	};

	private ValorNivelOrg valorNivelOrg = new ValorNivelOrg();
	private ContenidoFuncional contenidoFuncional = new ContenidoFuncional();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = " valorNivelOrg.contenidoFuncional.descripcion desc,valorNivelOrg.desde,valorNivelOrg.hasta asc ";
	
	

	public ValorNivelOrgList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<ValorNivelOrg> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		
		return getResultList(); 
	}

	public List<ValorNivelOrg> limpiarResultados(){
		valorNivelOrg = new ValorNivelOrg();
		contenidoFuncional = new ContenidoFuncional();
		estado=Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList(); 
	}
	
	public ValorNivelOrg getValorNivelOrg() {
		return valorNivelOrg;
	}
	
	public List<ValorNivelOrg> obtenerListaValorNivelOrgs(){
    	listaValorNivelOrgs = getResultList(); 
    	return listaValorNivelOrgs;
    }
	

//	GETTERS Y SETTERS
	public List<ValorNivelOrg> getListaValorNivelOrgs() {
		return listaValorNivelOrgs;
	}
	public ContenidoFuncional getContenidoFuncional() {
		return contenidoFuncional;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
}
