package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.SinCategoria;

@Name("sinCategoriaList")
public class SinCategoriaList extends EntityQuery<SinCategoria> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<SinCategoria> listaSinCategorias;
	
	private static final String EJBQL = "select sinCategoria from SinCategoria sinCategoria";

	private static final String[] RESTRICTIONS = {
			"lower(sinCategoria.ctgCodigo) like lower(concat(#{sinCategoriaList.sinCategoria.ctgCodigo},'%'))",
			"lower(sinCategoria.ctgDenominacion) like lower(concat(#{sinCategoriaList.sinCategoria.ctgDenominacion},'%'))",
			"lower(sinCategoria.vrsCodigo) like lower(concat(#{sinCategoriaList.sinCategoria.vrsCodigo},'%'))",
};

	private SinCategoria sinCategoria = new SinCategoria();

	public SinCategoriaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public SinCategoria getSinCategoria() {
		return sinCategoria;
	}
	
	public List<SinCategoria> obtenerListaSinCategorias(){
    	listaSinCategorias = getResultList(); 
    	return listaSinCategorias;
    }
	
    public List<SinCategoria> getListaSinCategorias() {
		return listaSinCategorias;
	}
}
