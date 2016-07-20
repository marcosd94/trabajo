package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.TblPuestoCategoria;

@Name("tblPuestoCategoriaList")
public class TblPuestoCategoriaList extends EntityQuery<TblPuestoCategoria> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<TblPuestoCategoria> listaTblPuestoCategorias;
	
	private static final String EJBQL = "select tblPuestoCategoria from TblPuestoCategoria tblPuestoCategoria";

	private static final String[] RESTRICTIONS = {
			"lower(tblPuestoCategoria.codigoCategoria) like lower(concat(#{tblPuestoCategoriaList.tblPuestoCategoria.codigoCategoria},'%'))",
			"lower(tblPuestoCategoria.descripcion) like lower(concat(#{tblPuestoCategoriaList.tblPuestoCategoria.descripcion},'%'))",
};

	private TblPuestoCategoria tblPuestoCategoria = new TblPuestoCategoria();

	public TblPuestoCategoriaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public TblPuestoCategoria getTblPuestoCategoria() {
		return tblPuestoCategoria;
	}
	
	public List<TblPuestoCategoria> obtenerListaTblPuestoCategorias(){
    	listaTblPuestoCategorias = getResultList(); 
    	return listaTblPuestoCategorias;
    }
	
    public List<TblPuestoCategoria> getListaTblPuestoCategorias() {
		return listaTblPuestoCategorias;
	}
}
