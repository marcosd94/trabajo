package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.SinTip;

import java.util.Arrays;

@Name("sinTipList")
public class SinTipList extends EntityQuery<SinTip> {

	private static final String EJBQL = "select sinTip from SinTip sinTip";

	private static final String[] RESTRICTIONS = {
			"lower(sinTip.tipNombre) like lower(concat(#{sinTipList.sinTip.tipNombre},'%'))",
			"lower(sinTip.tipNomabr) like lower(concat(#{sinTipList.sinTip.tipNomabr},'%'))", };

	private SinTip sinTip = new SinTip();

	public SinTipList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SinTip getSinTip() {
		return sinTip;
	}
}
