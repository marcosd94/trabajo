package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;

import java.util.Arrays;

@Name("reprPersonaDiscapacidadList")
public class ReprPersonaDiscapacidadList extends
		EntityQuery<ReprPersonaDiscapacidad> {

	private static final String EJBQL = "select reprPersonaDiscapacidad from ReprPersonaDiscapacidad reprPersonaDiscapacidad";

	private static final String[] RESTRICTIONS = {
			"lower(reprPersonaDiscapacidad.observacion) like lower(concat(#{reprPersonaDiscapacidadList.reprPersonaDiscapacidad.observacion},'%'))",
			"lower(reprPersonaDiscapacidad.usuAlta) like lower(concat(#{reprPersonaDiscapacidadList.reprPersonaDiscapacidad.usuAlta},'%'))",
			"lower(reprPersonaDiscapacidad.usuMod) like lower(concat(#{reprPersonaDiscapacidadList.reprPersonaDiscapacidad.usuMod},'%'))", };

	private ReprPersonaDiscapacidad reprPersonaDiscapacidad = new ReprPersonaDiscapacidad();

	public ReprPersonaDiscapacidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ReprPersonaDiscapacidad getReprPersonaDiscapacidad() {
		return reprPersonaDiscapacidad;
	}
}
