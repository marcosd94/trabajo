package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("cronogramaConcCabList")
public class CronogramaConcCabList extends EntityQuery<CronogramaConcCab> {

	private static final String EJBQL = "select cronogramaConcCab from CronogramaConcCab cronogramaConcCab";

	private static final String[] RESTRICTIONS = {
			"lower(cronogramaConcCab.descripcion) like lower(concat(#{cronogramaConcCabList.cronogramaConcCab.descripcion},'%'))",
			"lower(cronogramaConcCab.usuAlta) like lower(concat(#{cronogramaConcCabList.cronogramaConcCab.usuAlta},'%'))",
			"lower(cronogramaConcCab.usuMod) like lower(concat(#{cronogramaConcCabList.cronogramaConcCab.usuMod},'%'))", };

	private CronogramaConcCab cronogramaConcCab = new CronogramaConcCab();

	public CronogramaConcCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CronogramaConcCab getCronogramaConcCab() {
		return cronogramaConcCab;
	}
}
