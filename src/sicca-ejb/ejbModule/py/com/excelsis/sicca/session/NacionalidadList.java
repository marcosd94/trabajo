package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.Nacionalidad;

@Name("nacionalidadList")
public class NacionalidadList extends EntityQuery<Nacionalidad> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Nacionalidad> listaNacionalidads;
	
	private static final String EJBQL = "select nacionalidad from Nacionalidad nacionalidad";

	private static final String[] RESTRICTIONS = {
			"lower(nacionalidad.descripcion) like lower(concat(#{nacionalidadList.nacionalidad.descripcion},'%'))",
			"lower(nacionalidad.usuAlta) like lower(concat(#{nacionalidadList.nacionalidad.usuAlta},'%'))",
			"lower(nacionalidad.usuMod) like lower(concat(#{nacionalidadList.nacionalidad.usuMod},'%'))",
};

	private Nacionalidad nacionalidad = new Nacionalidad();

	public NacionalidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public Nacionalidad getNacionalidad() {
		return nacionalidad;
	}
	
	public List<Nacionalidad> obtenerListaNacionalidads(){
    	listaNacionalidads = getResultList(); 
    	return listaNacionalidads;
    }
	
    public List<Nacionalidad> getListaNacionalidads() {
		return listaNacionalidads;
	}
}
