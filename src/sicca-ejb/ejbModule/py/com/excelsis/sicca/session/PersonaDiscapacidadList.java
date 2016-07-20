package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("personaDiscapacidadList")
public class PersonaDiscapacidadList extends EntityQuery<PersonaDiscapacidad> {

	private static final String EJBQL = "select personaDiscapacidad from PersonaDiscapacidad personaDiscapacidad";

	private static final String[] RESTRICTIONS = {
			"lower(personaDiscapacidad.causa) like lower(concat(#{personaDiscapacidadList.personaDiscapacidad.causa},'%'))",
			"lower(personaDiscapacidad.dificultadActividad) like lower(concat(#{personaDiscapacidadList.personaDiscapacidad.dificultadActividad},'%'))",
			"lower(personaDiscapacidad.usuAlta) like lower(concat(#{personaDiscapacidadList.personaDiscapacidad.usuAlta},'%'))",
			"lower(personaDiscapacidad.usuMod) like lower(concat(#{personaDiscapacidadList.personaDiscapacidad.usuMod},'%'))", };

	private PersonaDiscapacidad personaDiscapacidad = new PersonaDiscapacidad();

	public PersonaDiscapacidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PersonaDiscapacidad getPersonaDiscapacidad() {
		return personaDiscapacidad;
	}
}
