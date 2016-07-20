package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detCondicionSegurList")
public class DetCondicionSegurList extends EntityQuery<DetCondicionSegur> {

	private static final String EJBQL = "select detCondicionSegur from DetCondicionSegur detCondicionSegur";

	private static final String[] RESTRICTIONS = {
			"lower(detCondicionSegur.tipo) like lower(concat(#{detCondicionSegurList.detCondicionSegur.tipo},'%'))",
			"lower(detCondicionSegur.ajustes) like lower(concat(#{detCondicionSegurList.detCondicionSegur.ajustes},'%'))",
			"lower(detCondicionSegur.justificacion) like lower(concat(#{detCondicionSegurList.detCondicionSegur.justificacion},'%'))", };

	private DetCondicionSegur detCondicionSegur = new DetCondicionSegur();

	public DetCondicionSegurList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetCondicionSegur getDetCondicionSegur() {
		return detCondicionSegur;
	}
}
