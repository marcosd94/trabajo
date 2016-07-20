package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detContenidoFuncionalList")
public class DetContenidoFuncionalList extends
		EntityQuery<DetContenidoFuncional> {

	private static final String EJBQL = "select detContenidoFuncional from DetContenidoFuncional detContenidoFuncional";

	private static final String[] RESTRICTIONS = { "lower(detContenidoFuncional.tipo) like lower(concat(#{detContenidoFuncionalList.detContenidoFuncional.tipo},'%'))", };

	private DetContenidoFuncional detContenidoFuncional = new DetContenidoFuncional();

	public DetContenidoFuncionalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetContenidoFuncional getDetContenidoFuncional() {
		return detContenidoFuncional;
	}
}
