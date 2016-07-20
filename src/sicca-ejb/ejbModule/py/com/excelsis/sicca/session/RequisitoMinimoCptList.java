package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.ValorNivelOrg;

@Name("requisitoMinimoCptList")
public class RequisitoMinimoCptList extends EntityQuery<RequisitoMinimoCpt> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<RequisitoMinimoCpt> listaRequisitoMinimoCpts;
	
	private static final String EJBQL = "select requisitoMinimoCpt from RequisitoMinimoCpt requisitoMinimoCpt";

	private static final String[] RESTRICTIONS = {
			"lower(requisitoMinimoCpt.descripcion) like lower(concat('%',concat(#{requisitoMinimoCptList.requisitoMinimoCpt.descripcion},'%')))",
			"requisitoMinimoCpt.activo = #{requisitoMinimoCptList.activo}",
	};
	private static final String ORDER = "requisitoMinimoCpt.orden";
	
	private RequisitoMinimoCpt requisitoMinimoCpt = new RequisitoMinimoCpt();
	private Boolean activo = true;

	public RequisitoMinimoCptList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder(ORDER);
	}
	
	public List<RequisitoMinimoCpt> buscarResultados(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder(ORDER);
		return getResultList(); 
	}

	public List<RequisitoMinimoCpt> limpiarResultados(){
		requisitoMinimoCpt = new RequisitoMinimoCpt();
		activo = true;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder(ORDER);
		return getResultList(); 
	}

//	GETTERS Y SETTERS
	public RequisitoMinimoCpt getRequisitoMinimoCpt() {
		return requisitoMinimoCpt;
	}
	
	public List<RequisitoMinimoCpt> obtenerListaRequisitoMinimoCpts(){
    	listaRequisitoMinimoCpts = getResultList(); 
    	return listaRequisitoMinimoCpts;
    }
	
    public List<RequisitoMinimoCpt> getListaRequisitoMinimoCpts() {
		return listaRequisitoMinimoCpts;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return activo;
	}
}
