package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("calendarioOeeDetList")
public class CalendarioOeeDetList extends EntityQuery<CalendarioOeeDet> {

	private static final String EJBQL = "select calendarioOeeDet from CalendarioOeeDet calendarioOeeDet";

	private static final String[] RESTRICTIONS = {
			"lower(calendarioOeeDet.descripcion) like lower(concat(#{calendarioOeeDetList.calendarioOeeDet.descripcion},'%'))",
			"lower(calendarioOeeDet.usuAlta) like lower(concat(#{calendarioOeeDetList.calendarioOeeDet.usuAlta},'%'))",
			"lower(calendarioOeeDet.usuMod) like lower(concat(#{calendarioOeeDetList.calendarioOeeDet.usuMod},'%'))", };

	private CalendarioOeeDet calendarioOeeDet = new CalendarioOeeDet();

	public CalendarioOeeDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CalendarioOeeDet getCalendarioOeeDet() {
		return calendarioOeeDet;
	}
}
