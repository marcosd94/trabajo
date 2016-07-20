package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("concursoPuestoDetList")
public class ConcursoPuestoDetList extends EntityQuery<ConcursoPuestoDet> {

	private static final String EJBQL = "select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet";

	private static final String[] RESTRICTIONS = {
			"lower(concursoPuestoDet.usuAlta) like lower(concat(#{concursoPuestoDetList.concursoPuestoDet.usuAlta},'%'))",
			"lower(concursoPuestoDet.usuMod) like lower(concat(#{concursoPuestoDetList.concursoPuestoDet.usuMod},'%'))", };

	private ConcursoPuestoDet concursoPuestoDet = new ConcursoPuestoDet();

	public ConcursoPuestoDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConcursoPuestoDet getConcursoPuestoDet() {
		return concursoPuestoDet;
	}
}
