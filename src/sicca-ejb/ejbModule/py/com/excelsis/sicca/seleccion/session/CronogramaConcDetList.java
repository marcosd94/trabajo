package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cronogramaConcDetList")
public class CronogramaConcDetList extends EntityQuery<CronogramaConcDet> {

	private static final String EJBQL = "select cronogramaConcDet from CronogramaConcDet cronogramaConcDet";

	private static final String[] RESTRICTIONS = {
			"lower(cronogramaConcDet.lugar) like lower(concat(#{cronogramaConcDetList.cronogramaConcDet.lugar},'%'))",
			"lower(cronogramaConcDet.usuAlta) like lower(concat(#{cronogramaConcDetList.cronogramaConcDet.usuAlta},'%'))",
			"lower(cronogramaConcDet.usuMod) like lower(concat(#{cronogramaConcDetList.cronogramaConcDet.usuMod},'%'))", };

	private CronogramaConcDet cronogramaConcDet = new CronogramaConcDet();

	public CronogramaConcDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CronogramaConcDet getCronogramaConcDet() {
		return cronogramaConcDet;
	}
}
