package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.SinPro;

import java.util.Arrays;

@Name("sinProList")
public class SinProList extends EntityQuery<SinPro> {

	private static final String EJBQL = "select sinPro from SinPro sinPro";

	private static final String[] RESTRICTIONS = {
			"lower(sinPro.funCodigo) like lower(concat(#{sinProList.sinPro.funCodigo},'%'))",
			"lower(sinPro.proNombre) like lower(concat(#{sinProList.sinPro.proNombre},'%'))",
			"lower(sinPro.proNomabr) like lower(concat(#{sinProList.sinPro.proNomabr},'%'))",
			"lower(sinPro.proDescrip) like lower(concat(#{sinProList.sinPro.proDescrip},'%'))", };

	private SinPro sinPro = new SinPro();

	public SinProList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SinPro getSinPro() {
		return sinPro;
	}
}
