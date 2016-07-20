package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.ClasificadorUoRequisito;

@Name("clasificadorUoRequisitoList")
public class ClasificadorUoRequisitoList extends EntityQuery<ClasificadorUoRequisito> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<ClasificadorUoRequisito> listaClasificadorUoRequisitos;
	
	private static final String EJBQL = "select clasificadorUoRequisito from ClasificadorUoRequisito clasificadorUoRequisito";

	private static final String[] RESTRICTIONS = {
			"lower(clasificadorUoRequisito.descripcion) like lower(concat(#{clasificadorUoRequisitoList.clasificadorUoRequisito.descripcion},'%'))",
			"lower(clasificadorUoRequisito.usuAlta) like lower(concat(#{clasificadorUoRequisitoList.clasificadorUoRequisito.usuAlta},'%'))",
			"lower(clasificadorUoRequisito.usuMod) like lower(concat(#{clasificadorUoRequisitoList.clasificadorUoRequisito.usuMod},'%'))",
};

	private ClasificadorUoRequisito clasificadorUoRequisito = new ClasificadorUoRequisito();

	public ClasificadorUoRequisitoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public ClasificadorUoRequisito getClasificadorUoRequisito() {
		return clasificadorUoRequisito;
	}
	
	public List<ClasificadorUoRequisito> obtenerListaClasificadorUoRequisitos(){
    	listaClasificadorUoRequisitos = getResultList(); 
    	return listaClasificadorUoRequisitos;
    }
	
    public List<ClasificadorUoRequisito> getListaClasificadorUoRequisitos() {
		return listaClasificadorUoRequisitos;
	}
}
