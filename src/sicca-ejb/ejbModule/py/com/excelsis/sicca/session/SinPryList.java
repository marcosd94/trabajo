package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.SinPry;

import java.util.Arrays;

@Name("sinPryList")
public class SinPryList extends EntityQuery<SinPry> {

	private static final String EJBQL = "select sinPry from SinPry sinPry";

	private static final String[] RESTRICTIONS = {
			"lower(sinPry.pryNombre) like lower(concat(#{sinPryList.sinPry.pryNombre},'%'))",
			"lower(sinPry.pryNomabr) like lower(concat(#{sinPryList.sinPry.pryNomabr},'%'))",
			"lower(sinPry.pryDescrip) like lower(concat(#{sinPryList.sinPry.pryDescrip},'%'))", };

	private SinPry sinPry = new SinPry();

	public SinPryList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SinPry getSinPry() {
		return sinPry;
	}
}
