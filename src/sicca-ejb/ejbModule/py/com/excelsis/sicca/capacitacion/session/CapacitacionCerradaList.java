package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.CapacitacionCerrada;

import java.util.Arrays;

@Name("capacitacionCerradaList")
public class CapacitacionCerradaList extends EntityQuery<CapacitacionCerrada> {

	private static final String EJBQL = "select capacitacionCerrada from CapacitacionCerrada capacitacionCerrada";

	private static final String[] RESTRICTIONS = {
			"lower(capacitacionCerrada.usuAlta) like lower(concat(#{capacitacionCerradaList.capacitacionCerrada.usuAlta},'%'))",
			"lower(capacitacionCerrada.usuMod) like lower(concat(#{capacitacionCerradaList.capacitacionCerrada.usuMod},'%'))", };

	private CapacitacionCerrada capacitacionCerrada = new CapacitacionCerrada();

	public CapacitacionCerradaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CapacitacionCerrada getCapacitacionCerrada() {
		return capacitacionCerrada;
	}
}
