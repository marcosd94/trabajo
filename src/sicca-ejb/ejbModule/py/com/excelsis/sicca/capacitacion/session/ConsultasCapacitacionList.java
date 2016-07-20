package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("consultasCapacitacionList")
public class ConsultasCapacitacionList extends
		EntityQuery<ConsultasCapacitacion> {

	private static final String EJBQL = "select consultasCapacitacion from ConsultasCapacitacion consultasCapacitacion";

	private static final String[] RESTRICTIONS = {
			"lower(consultasCapacitacion.lugar) like lower(concat(#{consultasCapacitacionList.consultasCapacitacion.lugar},'%'))",
			"lower(consultasCapacitacion.direccion) like lower(concat(#{consultasCapacitacionList.consultasCapacitacion.direccion},'%'))",
			"lower(consultasCapacitacion.telefono) like lower(concat(#{consultasCapacitacionList.consultasCapacitacion.telefono},'%'))",
			"lower(consultasCapacitacion.email) like lower(concat(#{consultasCapacitacionList.consultasCapacitacion.email},'%'))",
			"lower(consultasCapacitacion.usuAlta) like lower(concat(#{consultasCapacitacionList.consultasCapacitacion.usuAlta},'%'))",
			"lower(consultasCapacitacion.usuMod) like lower(concat(#{consultasCapacitacionList.consultasCapacitacion.usuMod},'%'))", };

	private ConsultasCapacitacion consultasCapacitacion = new ConsultasCapacitacion();

	public ConsultasCapacitacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ConsultasCapacitacion getConsultasCapacitacion() {
		return consultasCapacitacion;
	}
}
