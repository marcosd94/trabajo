package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("evalDocumentalDetList")
public class EvalDocumentalDetList extends EntityQuery<EvalDocumentalDet> {

	private static final String EJBQL = "select evalDocumentalDet from EvalDocumentalDet evalDocumentalDet";

	private static final String[] RESTRICTIONS = {
			"lower(evalDocumentalDet.usuAlta) like lower(concat(#{evalDocumentalDetList.evalDocumentalDet.usuAlta},'%'))",
			"lower(evalDocumentalDet.usuMod) like lower(concat(#{evalDocumentalDetList.evalDocumentalDet.usuMod},'%'))", };

	private EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();

	public EvalDocumentalDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EvalDocumentalDet getEvalDocumentalDet() {
		return evalDocumentalDet;
	}
}
