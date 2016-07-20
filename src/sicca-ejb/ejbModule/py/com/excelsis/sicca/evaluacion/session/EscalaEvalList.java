package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.EscalaEval;

import java.util.Arrays;

@Name("escalaEvalList")
public class EscalaEvalList extends EntityQuery<EscalaEval> {

	private static final String EJBQL = "select escalaEval from EscalaEval escalaEval";

	private static final String[] RESTRICTIONS = {
			"lower(escalaEval.descripcion) like lower(concat(#{escalaEvalList.escalaEval.descripcion},'%'))",
			"lower(escalaEval.observacion) like lower(concat(#{escalaEvalList.escalaEval.observacion},'%'))",
			"lower(escalaEval.usuAlta) like lower(concat(#{escalaEvalList.escalaEval.usuAlta},'%'))",
			"lower(escalaEval.usuMod) like lower(concat(#{escalaEvalList.escalaEval.usuMod},'%'))", };

	private EscalaEval escalaEval = new EscalaEval();

	public EscalaEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EscalaEval getEscalaEval() {
		return escalaEval;
	}
}
