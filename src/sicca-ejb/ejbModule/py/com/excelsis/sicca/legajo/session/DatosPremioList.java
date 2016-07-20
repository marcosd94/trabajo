package py.com.excelsis.sicca.legajo.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.DatosPremio;

import java.util.Arrays;

@Name("datosPremioList")
public class DatosPremioList extends EntityQuery<DatosPremio> {

	private static final String EJBQL = "select datosPremio from DatosPremio datosPremio";

	private static final String[] RESTRICTIONS = {
			"lower(datosPremio.obsPremio) like lower(concat(#{datosPremioList.datosPremio.obsPremio},'%'))",
			"lower(datosPremio.usuAlta) like lower(concat(#{datosPremioList.datosPremio.usuAlta},'%'))",
			"lower(datosPremio.usuMod) like lower(concat(#{datosPremioList.datosPremio.usuMod},'%'))", };

	private DatosPremio datosPremio = new DatosPremio();

	public DatosPremioList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DatosPremio getDatosPremio() {
		return datosPremio;
	}
}
