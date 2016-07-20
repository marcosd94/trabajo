package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.CategoriaCpt;

@Name("categoriaCptList")
public class CategoriaCptList extends EntityQuery<CategoriaCpt> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<CategoriaCpt> listaCategoriaCpts;
	
	private static final String EJBQL = "select categoriaCpt from CategoriaCpt categoriaCpt";

	private static final String[] RESTRICTIONS = {
			"lower(categoriaCpt.categoria) like lower(concat(#{categoriaCptList.categoriaCpt.categoria},'%'))",
			"lower(categoriaCpt.usuAlta) like lower(concat(#{categoriaCptList.categoriaCpt.usuAlta},'%'))",
			"lower(categoriaCpt.usuMod) like lower(concat(#{categoriaCptList.categoriaCpt.usuMod},'%'))",
};

	private CategoriaCpt categoriaCpt = new CategoriaCpt();

	public CategoriaCptList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public CategoriaCpt getCategoriaCpt() {
		return categoriaCpt;
	}
	
	public List<CategoriaCpt> obtenerListaCategoriaCpts(){
    	listaCategoriaCpts = getResultList(); 
    	return listaCategoriaCpts;
    }
	
    public List<CategoriaCpt> getListaCategoriaCpts() {
		return listaCategoriaCpts;
	}
}
