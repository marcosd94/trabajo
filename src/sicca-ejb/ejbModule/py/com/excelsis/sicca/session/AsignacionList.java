package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.Asignacion;

@Name("asignacionList")
public class AsignacionList extends EntityQuery<Asignacion> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Asignacion> listaAsignacions;
	
	private static final String EJBQL = "select asignacion from Asignacion asignacion";

	private static final String[] RESTRICTIONS = {
			"lower(asignacion.usuAlta) like lower(concat(#{asignacionList.asignacion.usuAlta},'%'))",
			"lower(asignacion.usuMod) like lower(concat(#{asignacionList.asignacion.usuMod},'%'))",
};

	private Asignacion asignacion = new Asignacion();

	public AsignacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public Asignacion getAsignacion() {
		return asignacion;
	}
	
	public List<Asignacion> obtenerListaAsignacions(){
    	listaAsignacions = getResultList(); 
    	return listaAsignacions;
    }
	
    public List<Asignacion> getListaAsignacions() {
		return listaAsignacions;
	}
}
