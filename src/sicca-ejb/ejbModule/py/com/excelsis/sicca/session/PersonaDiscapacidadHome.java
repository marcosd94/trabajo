package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PersonaDiscapacidad;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("personaDiscapacidadHome")
public class PersonaDiscapacidadHome extends EntityHome<PersonaDiscapacidad> {

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setPersonaDiscapacidadIdPersonaDiscapacidad(Long id) {
		setId(id);
	}

	public Long getPersonaDiscapacidadIdPersonaDiscapacidad() {
		return (Long) getId();
	}

	@Override
	protected PersonaDiscapacidad createInstance() {
		PersonaDiscapacidad personaDiscapacidad = new PersonaDiscapacidad();
		return personaDiscapacidad;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificosByIdDatosEspecificosDiscapac = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificosByIdDatosEspecificosDiscapac != null) {
			getInstance().setDatosEspecificosByIdDatosEspecificosDiscapac(
					datosEspecificosByIdDatosEspecificosDiscapac);
		}
		DatosEspecificos datosEspecificosByIdDatosEspecificosGradoAutonom = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificosByIdDatosEspecificosGradoAutonom != null) {
			getInstance().setDatosEspecificosByIdDatosEspecificosGradoAutonom(
					datosEspecificosByIdDatosEspecificosGradoAutonom);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificosByIdDatosEspecificosDiscapac() == null)
			return false;
		if (getInstance().getDatosEspecificosByIdDatosEspecificosGradoAutonom() == null)
			return false;
		return true;
	}

	public PersonaDiscapacidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
