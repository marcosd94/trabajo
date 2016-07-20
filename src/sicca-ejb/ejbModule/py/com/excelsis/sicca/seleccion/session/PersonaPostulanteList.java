package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("personaPostulanteList")
public class PersonaPostulanteList extends EntityQuery<PersonaPostulante> {

	private static final String EJBQL = "select personaPostulante from PersonaPostulante personaPostulante";

	private static final String[] RESTRICTIONS = {
			"lower(personaPostulante.nombres) like lower(concat(#{personaPostulanteList.personaPostulante.nombres},'%'))",
			"lower(personaPostulante.apellidos) like lower(concat(#{personaPostulanteList.personaPostulante.apellidos},'%'))",
			"lower(personaPostulante.documentoIdentidad) like lower(concat(#{personaPostulanteList.personaPostulante.documentoIdentidad},'%'))",
			"lower(personaPostulante.EMail) like lower(concat(#{personaPostulanteList.personaPostulante.EMail},'%'))",
			"lower(personaPostulante.sexo) like lower(concat(#{personaPostulanteList.personaPostulante.sexo},'%'))",
			"lower(personaPostulante.estadoCivil) like lower(concat(#{personaPostulanteList.personaPostulante.estadoCivil},'%'))",
			"lower(personaPostulante.callePrincipal) like lower(concat(#{personaPostulanteList.personaPostulante.callePrincipal},'%'))",
			"lower(personaPostulante.primeraLateral) like lower(concat(#{personaPostulanteList.personaPostulante.primeraLateral},'%'))",
			"lower(personaPostulante.segundaLateral) like lower(concat(#{personaPostulanteList.personaPostulante.segundaLateral},'%'))",
			"lower(personaPostulante.departamentoNro) like lower(concat(#{personaPostulanteList.personaPostulante.departamentoNro},'%'))",
			"lower(personaPostulante.piso) like lower(concat(#{personaPostulanteList.personaPostulante.piso},'%'))",
			"lower(personaPostulante.observacion) like lower(concat(#{personaPostulanteList.personaPostulante.observacion},'%'))",
			"lower(personaPostulante.direccionLaboral) like lower(concat(#{personaPostulanteList.personaPostulante.direccionLaboral},'%'))",
			"lower(personaPostulante.otrasDirecciones) like lower(concat(#{personaPostulanteList.personaPostulante.otrasDirecciones},'%'))",
			"lower(personaPostulante.telefonos) like lower(concat(#{personaPostulanteList.personaPostulante.telefonos},'%'))",
			"lower(personaPostulante.usuAlta) like lower(concat(#{personaPostulanteList.personaPostulante.usuAlta},'%'))",
			"lower(personaPostulante.usuMod) like lower(concat(#{personaPostulanteList.personaPostulante.usuMod},'%'))", };

	private PersonaPostulante personaPostulante = new PersonaPostulante();

	public PersonaPostulanteList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}
}
