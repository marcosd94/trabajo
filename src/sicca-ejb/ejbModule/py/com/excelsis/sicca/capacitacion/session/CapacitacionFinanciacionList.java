package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;

import java.util.Arrays;

@Name("capacitacionFinanciacionList")
public class CapacitacionFinanciacionList extends
		EntityQuery<CapacitacionFinanciacion> {

	private static final String EJBQL = "select capacitacionFinanciacion from CapacitacionFinanciacion capacitacionFinanciacion";

	private static final String[] RESTRICTIONS = {
			"lower(capacitacionFinanciacion.usuAlta) like lower(concat(#{capacitacionFinanciacionList.capacitacionFinanciacion.usuAlta},'%'))",
			"lower(capacitacionFinanciacion.usuMod) like lower(concat(#{capacitacionFinanciacionList.capacitacionFinanciacion.usuMod},'%'))", };

	private CapacitacionFinanciacion capacitacionFinanciacion = new CapacitacionFinanciacion();

	public CapacitacionFinanciacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CapacitacionFinanciacion getCapacitacionFinanciacion() {
		return capacitacionFinanciacion;
	}
}
