package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("reglaDetList")
public class ReglaDetList extends EntityQuery<ReglaDet> {

	private static final String EJBQL = "select reglaDet from ReglaDet reglaDet";

	private static final String[] RESTRICTIONS = {
			"lower(reglaDet.observacion) like lower(concat(#{reglaDetList.reglaDet.observacion},'%'))",
			"lower(reglaDet.usuAlta) like lower(concat(#{reglaDetList.reglaDet.usuAlta},'%'))",
			"lower(reglaDet.usuMod) like lower(concat(#{reglaDetList.reglaDet.usuMod},'%'))", };

	private ReglaDet reglaDet = new ReglaDet();

	public ReglaDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReglaDet getReglaDet() {
		return reglaDet;
	}
}
