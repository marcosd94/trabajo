package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("resolucionEvalList")
public class ResolucionEvalList extends EntityQuery<ResolucionEval> {

	private static final String EJBQL = "select resolucionEval from ResolucionEval resolucionEval";

	private static final String[] RESTRICTIONS = {
			"lower(resolucionEval.titulo) like lower(concat(#{resolucionEvalList.resolucionEval.titulo},'%'))",
			"lower(resolucionEval.lugar) like lower(concat(#{resolucionEvalList.resolucionEval.lugar},'%'))",
			"lower(resolucionEval.fecha) like lower(concat(#{resolucionEvalList.resolucionEval.fecha},'%'))",
			"lower(resolucionEval.visto) like lower(concat(#{resolucionEvalList.resolucionEval.visto},'%'))",
			"lower(resolucionEval.considerando) like lower(concat(#{resolucionEvalList.resolucionEval.considerando},'%'))",
			"lower(resolucionEval.porTanto) like lower(concat(#{resolucionEvalList.resolucionEval.porTanto},'%'))",
			"lower(resolucionEval.resuelve) like lower(concat(#{resolucionEvalList.resolucionEval.resuelve},'%'))",
			"lower(resolucionEval.firma) like lower(concat(#{resolucionEvalList.resolucionEval.firma},'%'))",
			"lower(resolucionEval.cargo) like lower(concat(#{resolucionEvalList.resolucionEval.cargo},'%'))",
			"lower(resolucionEval.institucion) like lower(concat(#{resolucionEvalList.resolucionEval.institucion},'%'))",
			"lower(resolucionEval.usuAlta) like lower(concat(#{resolucionEvalList.resolucionEval.usuAlta},'%'))",
			"lower(resolucionEval.usuMod) like lower(concat(#{resolucionEvalList.resolucionEval.usuMod},'%'))", };

	private ResolucionEval resolucionEval = new ResolucionEval();

	public ResolucionEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ResolucionEval getResolucionEval() {
		return resolucionEval;
	}
}
