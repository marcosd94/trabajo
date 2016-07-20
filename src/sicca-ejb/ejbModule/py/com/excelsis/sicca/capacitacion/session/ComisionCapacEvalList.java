package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ComisionCapacEval;

import java.util.Arrays;

@Name("comisionCapacEvalList")
public class ComisionCapacEvalList extends EntityQuery<ComisionCapacEval> {

	private static final String EJBQL = "select comisionCapacEval from ComisionCapacEval comisionCapacEval";

	private static final String[] RESTRICTIONS = {
			"lower(comisionCapacEval.tipo) like lower(concat(#{comisionCapacEvalList.comisionCapacEval.tipo},'%'))",
			"lower(comisionCapacEval.equipoTecnico) like lower(concat(#{comisionCapacEvalList.comisionCapacEval.equipoTecnico},'%'))",
			"lower(comisionCapacEval.titularSuplente) like lower(concat(#{comisionCapacEvalList.comisionCapacEval.titularSuplente},'%'))",
			"lower(comisionCapacEval.usuAlta) like lower(concat(#{comisionCapacEvalList.comisionCapacEval.usuAlta},'%'))",
			"lower(comisionCapacEval.usuMod) like lower(concat(#{comisionCapacEvalList.comisionCapacEval.usuMod},'%'))", };

	private ComisionCapacEval comisionCapacEval = new ComisionCapacEval();

	public ComisionCapacEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ComisionCapacEval getComisionCapacEval() {
		return comisionCapacEval;
	}
}
