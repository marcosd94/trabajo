package py.com.excelsis.sicca.juridicos.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.SumarioDet;

import java.util.Arrays;

@Name("sumarioDetList")
public class SumarioDetList extends EntityQuery<SumarioDet> {

	private static final String EJBQL = "select sumarioDet from SumarioDet sumarioDet";

	private static final String[] RESTRICTIONS = {
			"lower(sumarioDet.usuAlta) like lower(concat(#{sumarioDetList.sumarioDet.usuAlta},'%'))",
			"lower(sumarioDet.usuMod) like lower(concat(#{sumarioDetList.sumarioDet.usuMod},'%'))", };

	private SumarioDet sumarioDet = new SumarioDet();

	public SumarioDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SumarioDet getSumarioDet() {
		return sumarioDet;
	}
}
