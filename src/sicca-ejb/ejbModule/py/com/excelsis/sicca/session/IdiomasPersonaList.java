package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.IdiomasPersona;

import java.util.Arrays;

@Name("idiomasPersonaList")
public class IdiomasPersonaList extends EntityQuery<IdiomasPersona> {

	private static final String EJBQL = "select idiomasPersona from IdiomasPersona idiomasPersona";

	private static final String[] RESTRICTIONS = {
			"lower(idiomasPersona.habla) like lower(concat(#{idiomasPersonaList.idiomasPersona.habla},'%'))",
			"lower(idiomasPersona.escribe) like lower(concat(#{idiomasPersonaList.idiomasPersona.escribe},'%'))",
			"lower(idiomasPersona.lee) like lower(concat(#{idiomasPersonaList.idiomasPersona.lee},'%'))",
			"lower(idiomasPersona.usuAlta) like lower(concat(#{idiomasPersonaList.idiomasPersona.usuAlta},'%'))", };

	private IdiomasPersona idiomasPersona = new IdiomasPersona();

	public IdiomasPersonaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public IdiomasPersona getIdiomasPersona() {
		return idiomasPersona;
	}
}
