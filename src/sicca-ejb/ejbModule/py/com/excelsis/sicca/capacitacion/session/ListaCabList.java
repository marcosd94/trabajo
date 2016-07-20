package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("listaCabList")
public class ListaCabList extends EntityQuery<ListaCab> {

	private static final String EJBQL = "select listaCab from ListaCab listaCab";

	private static final String[] RESTRICTIONS = {
			"lower(listaCab.usuAlta) like lower(concat(#{listaCabList.listaCab.usuAlta},'%'))",
			"lower(listaCab.usuMod) like lower(concat(#{listaCabList.listaCab.usuMod},'%'))", };

	private ListaCab listaCab = new ListaCab();

	public ListaCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ListaCab getListaCab() {
		return listaCab;
	}
}
