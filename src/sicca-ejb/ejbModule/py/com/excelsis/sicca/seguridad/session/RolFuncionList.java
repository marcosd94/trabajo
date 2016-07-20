package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("rolFuncionList")
public class RolFuncionList extends EntityQuery<RolFuncion> {

	private static final String EJBQL = "select rolFuncion from RolFuncion rolFuncion";

	private static final String[] RESTRICTIONS = {};

	private RolFuncion rolFuncion = new RolFuncion();

	public RolFuncionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RolFuncion getRolFuncion() {
		return rolFuncion;
	}
}
