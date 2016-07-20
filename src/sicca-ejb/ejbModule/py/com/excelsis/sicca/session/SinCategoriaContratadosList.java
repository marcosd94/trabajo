package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.SinCategoriaContratados;

@Name("sinCategoriaContratadosList")
public class SinCategoriaContratadosList extends EntityQuery<SinCategoriaContratados> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<SinCategoriaContratados> listaSinCategoriaContratadoss;
	
	private static final String EJBQL = "select sinCategoriaContratados from SinCategoriaContratados sinCategoriaContratados";

	private static final String[] RESTRICTIONS = {
			"lower(sinCategoriaContratados.conCtg) like lower(concat(#{sinCategoriaContratadosList.sinCategoriaContratados.conCtg},'%'))",
			"lower(sinCategoriaContratados.ctgDescrip) like lower(concat(#{sinCategoriaContratadosList.sinCategoriaContratados.ctgDescrip},'%'))",
};

	private SinCategoriaContratados sinCategoriaContratados = new SinCategoriaContratados();

	public SinCategoriaContratadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public SinCategoriaContratados getSinCategoriaContratados() {
		return sinCategoriaContratados;
	}
	
	public List<SinCategoriaContratados> obtenerListaSinCategoriaContratadoss(){
    	listaSinCategoriaContratadoss = getResultList(); 
    	return listaSinCategoriaContratadoss;
    }
	
    public List<SinCategoriaContratados> getListaSinCategoriaContratadoss() {
		return listaSinCategoriaContratadoss;
	}
}
