package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detDescripcionContFuncionalList")
public class DetDescripcionContFuncionalList extends
		EntityQuery<DetDescripcionContFuncional> {

	private static final String EJBQL = "select detDescripcionContFuncional from DetDescripcionContFuncional detDescripcionContFuncional";

	private static final String[] RESTRICTIONS = { "lower(detDescripcionContFuncional.descripcion) like lower(concat(#{detDescripcionContFuncionalList.detDescripcionContFuncional.descripcion},'%'))", };

	private DetDescripcionContFuncional detDescripcionContFuncional = new DetDescripcionContFuncional();

	public DetDescripcionContFuncionalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetDescripcionContFuncional getDetDescripcionContFuncional() {
		return detDescripcionContFuncional;
	}
}
