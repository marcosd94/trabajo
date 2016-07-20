package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("alertasEvalDetList")
public class AlertasEvalDetList extends EntityQuery<AlertasEvalDet> {

	private static final String EJBQL = "select alertasEvalDet from AlertasEvalDet alertasEvalDet";

	private static final String[] RESTRICTIONS = {
			"lower(alertasEvalDet.descripcion) like lower(concat(#{alertasEvalDetList.alertasEvalDet.descripcion},'%'))",
			"lower(alertasEvalDet.EMail) like lower(concat(#{alertasEvalDetList.alertasEvalDet.EMail},'%'))",
			"lower(alertasEvalDet.usuAlta) like lower(concat(#{alertasEvalDetList.alertasEvalDet.usuAlta},'%'))",
			"lower(alertasEvalDet.usuMod) like lower(concat(#{alertasEvalDetList.alertasEvalDet.usuMod},'%'))", };

	private AlertasEvalDet alertasEvalDet = new AlertasEvalDet();

	public AlertasEvalDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AlertasEvalDet getAlertasEvalDet() {
		return alertasEvalDet;
	}
}
