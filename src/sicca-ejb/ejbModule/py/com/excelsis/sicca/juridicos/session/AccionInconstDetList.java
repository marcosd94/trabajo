package py.com.excelsis.sicca.juridicos.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("accionInconstDetList")
public class AccionInconstDetList extends EntityQuery<AccionInconstDet> {

	private static final String EJBQL = "select accionInconstDet from AccionInconstDet accionInconstDet";

	private static final String[] RESTRICTIONS = {
			"lower(accionInconstDet.otraLey) like lower(concat(#{accionInconstDetList.accionInconstDet.otraLey},'%'))",
			"lower(accionInconstDet.artEspecif) like lower(concat(#{accionInconstDetList.accionInconstDet.artEspecif},'%'))",
			"lower(accionInconstDet.usuAlta) like lower(concat(#{accionInconstDetList.accionInconstDet.usuAlta},'%'))", };

	private AccionInconstDet accionInconstDet = new AccionInconstDet();

	public AccionInconstDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AccionInconstDet getAccionInconstDet() {
		return accionInconstDet;
	}
}
