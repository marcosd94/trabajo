package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.PlanCapacitacionDet;

import java.util.Arrays;

@Name("planCapacitacionDetList")
public class PlanCapacitacionDetList extends EntityQuery<PlanCapacitacionDet> {

	private static final String EJBQL = "select planCapacitacionDet from PlanCapacitacionDet planCapacitacionDet";

	private static final String[] RESTRICTIONS = {
			"lower(planCapacitacionDet.descripcion) like lower(concat(#{planCapacitacionDetList.planCapacitacionDet.descripcion},'%'))",
			"lower(planCapacitacionDet.usuAlta) like lower(concat(#{planCapacitacionDetList.planCapacitacionDet.usuAlta},'%'))",
			"lower(planCapacitacionDet.usuMod) like lower(concat(#{planCapacitacionDetList.planCapacitacionDet.usuMod},'%'))", };

	private PlanCapacitacionDet planCapacitacionDet = new PlanCapacitacionDet();

	public PlanCapacitacionDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PlanCapacitacionDet getPlanCapacitacionDet() {
		return planCapacitacionDet;
	}
}
