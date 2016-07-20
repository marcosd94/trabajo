package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("evalDocumentalComisList")
public class EvalDocumentalComisList extends EntityQuery<EvalDocumentalComis> {

	private static final String EJBQL = "select evalDocumentalComis from EvalDocumentalComis evalDocumentalComis";

	private static final String[] RESTRICTIONS = {
			"lower(evalDocumentalComis.usuAlta) like lower(concat(#{evalDocumentalComisList.evalDocumentalComis.usuAlta},'%'))",
			"lower(evalDocumentalComis.usuMod) like lower(concat(#{evalDocumentalComisList.evalDocumentalComis.usuMod},'%'))", };

	private EvalDocumentalComis evalDocumentalComis = new EvalDocumentalComis();

	public EvalDocumentalComisList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EvalDocumentalComis getEvalDocumentalComis() {
		return evalDocumentalComis;
	}
}
