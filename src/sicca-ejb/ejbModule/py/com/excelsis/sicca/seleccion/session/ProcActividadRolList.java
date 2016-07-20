package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("procActividadRolList")
public class ProcActividadRolList extends EntityQuery<ProcActividadRol> {

	private static final String EJBQL = "select procActividadRol from ProcActividadRol procActividadRol";

	private static final String[] RESTRICTIONS = {
			"lower(procActividadRol.usuAlta) like lower(concat(#{procActividadRolList.procActividadRol.usuAlta},'%'))",
			"lower(procActividadRol.usuMod) like lower(concat(#{procActividadRolList.procActividadRol.usuMod},'%'))", };

	private ProcActividadRol procActividadRol = new ProcActividadRol();

	public ProcActividadRolList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ProcActividadRol getProcActividadRol() {
		return procActividadRol;
	}
}
