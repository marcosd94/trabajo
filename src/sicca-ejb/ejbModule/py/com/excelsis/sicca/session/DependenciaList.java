package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.Dependencia;

@Name("dependenciaList")
public class DependenciaList extends EntityQuery<Dependencia> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Dependencia> listaDependencias;
	
	private static final String EJBQL = "select dependencia from Dependencia dependencia";

	private static final String[] RESTRICTIONS = {
			"lower(dependencia.nombre) like lower(concat(#{dependenciaList.dependencia.nombre},'%'))",
			"lower(dependencia.nombreAbr) like lower(concat(#{dependenciaList.dependencia.nombreAbr},'%'))",
			"lower(dependencia.sigla) like lower(concat(#{dependenciaList.dependencia.sigla},'%'))",
			"lower(dependencia.entRuc) like lower(concat(#{dependenciaList.dependencia.entRuc},'%'))",
};

	private Dependencia dependencia = new Dependencia();

	public DependenciaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public Dependencia getDependencia() {
		return dependencia;
	}
	
	public List<Dependencia> obtenerListaDependencias(){
    	listaDependencias = getResultList(); 
    	return listaDependencias;
    }
	
    public List<Dependencia> getListaDependencias() {
		return listaDependencias;
	}
}
