package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("fusionPuestoList")
public class FusionPuestoList extends EntityQuery<FusionPuesto> {

	private static final String EJBQL = "select fusionPuesto from FusionPuesto fusionPuesto";

	private static final String[] RESTRICTIONS = {};

	private FusionPuesto fusionPuesto = new FusionPuesto();

	public FusionPuestoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public FusionPuesto getFusionPuesto() {
		return fusionPuesto;
	}
}
