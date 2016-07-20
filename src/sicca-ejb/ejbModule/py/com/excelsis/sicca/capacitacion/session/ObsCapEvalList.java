package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ObsCapEval;

import java.util.Arrays;

@Name("obsCapEvalList")
public class ObsCapEvalList extends EntityQuery<ObsCapEval> {

	private static final String EJBQL = "select obsCapEval from ObsCapEval obsCapEval";

	private static final String[] RESTRICTIONS = {
			"lower(obsCapEval.observacion) like lower(concat(#{obsCapEvalList.obsCapEval.observacion},'%'))",
			"lower(obsCapEval.usuAlta) like lower(concat(#{obsCapEvalList.obsCapEval.usuAlta},'%'))",
			"lower(obsCapEval.usuMod) like lower(concat(#{obsCapEvalList.obsCapEval.usuMod},'%'))", };

	private ObsCapEval obsCapEval = new ObsCapEval();

	public ObsCapEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ObsCapEval getObsCapEval() {
		return obsCapEval;
	}
}
