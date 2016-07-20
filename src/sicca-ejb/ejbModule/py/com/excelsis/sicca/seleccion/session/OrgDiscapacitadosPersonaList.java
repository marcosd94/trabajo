package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.OrgDiscapacitadosPersona;

import java.util.Arrays;

@Name("orgDiscapacitadosPersonaList")
public class OrgDiscapacitadosPersonaList extends
		EntityQuery<OrgDiscapacitadosPersona> {

	private static final String EJBQL = "select orgDiscapacitadosPersona from OrgDiscapacitadosPersona orgDiscapacitadosPersona";

	private static final String[] RESTRICTIONS = {
			"lower(orgDiscapacitadosPersona.cargo) like lower(concat(#{orgDiscapacitadosPersonaList.orgDiscapacitadosPersona.cargo},'%'))",
			"lower(orgDiscapacitadosPersona.usuAlta) like lower(concat(#{orgDiscapacitadosPersonaList.orgDiscapacitadosPersona.usuAlta},'%'))",
			"lower(orgDiscapacitadosPersona.usuMod) like lower(concat(#{orgDiscapacitadosPersonaList.orgDiscapacitadosPersona.usuMod},'%'))", };

	private OrgDiscapacitadosPersona orgDiscapacitadosPersona = new OrgDiscapacitadosPersona();

	public OrgDiscapacitadosPersonaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public OrgDiscapacitadosPersona getOrgDiscapacitadosPersona() {
		return orgDiscapacitadosPersona;
	}
}
