package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import py.com.excelsis.sicca.entity.Empleado;

@Name("empleadoList")
public class EmpleadoList extends EntityQuery<Empleado> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Empleado> listaEmpleados;
	
	private static final String EJBQL = "select empleado from Empleado empleado";

	private static final String[] RESTRICTIONS = {
			"lower(empleado.tipoEmpleado) like lower(concat(#{empleadoList.empleado.tipoEmpleado},'%'))",
			"lower(empleado.categoria) like lower(concat(#{empleadoList.empleado.categoria},'%'))",
};

	private Empleado empleado = new Empleado();

	public EmpleadoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}

	public Empleado getEmpleado() {
		return empleado;
	}
	
	public List<Empleado> obtenerListaEmpleados(){
    	listaEmpleados = getResultList(); 
    	return listaEmpleados;
    }
	
    public List<Empleado> getListaEmpleados() {
		return listaEmpleados;
	}
}
