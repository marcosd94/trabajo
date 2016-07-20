package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.FuncionarioOee;

import java.util.Arrays;

@Name("funcionarioOeeList")
public class FuncionarioOeeList extends EntityQuery<FuncionarioOee> {

	private static final String EJBQL = "select funcionarioOee from FuncionarioOee funcionarioOee";

	private static final String[] RESTRICTIONS = {
			"lower(funcionarioOee.usuAlta) like lower(concat(#{funcionarioOeeList.funcionarioOee.usuAlta},'%'))",
			"lower(funcionarioOee.usuMod) like lower(concat(#{funcionarioOeeList.funcionarioOee.usuMod},'%'))", };

	private FuncionarioOee funcionarioOee = new FuncionarioOee();

	public FuncionarioOeeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public FuncionarioOee getFuncionarioOee() {
		return funcionarioOee;
	}
}
