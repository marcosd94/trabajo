package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detReqMinList")
public class DetReqMinList extends EntityQuery<DetReqMin> {

	private static final String EJBQL = "select detReqMin from DetReqMin detReqMin";

	private static final String[] RESTRICTIONS = { "lower(detReqMin.tipo) like lower(concat(#{detReqMinList.detReqMin.tipo},'%'))", };

	private DetReqMin detReqMin = new DetReqMin();

	public DetReqMinList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetReqMin getDetReqMin() {
		return detReqMin;
	}
}
