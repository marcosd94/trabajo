package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.OrgDiscapacitadosPersona;
import py.com.excelsis.sicca.entity.OrganizacionDiscapacitados;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.session.OrganizacionDiscapacitadosHome;
import py.com.excelsis.sicca.session.PersonaHome;

@Name("orgDiscapacitadosPersonaHome")
public class OrgDiscapacitadosPersonaHome extends
		EntityHome<OrgDiscapacitadosPersona> {

	@In(create = true)
	OrganizacionDiscapacitadosHome organizacionDiscapacitadosHome;
	@In(create = true)
	PersonaHome personaHome;

	public void setOrgDiscapacitadosPersonaIdOrgDiscapacitadosPersona(Long id) {
		setId(id);
	}

	public Long getOrgDiscapacitadosPersonaIdOrgDiscapacitadosPersona() {
		return (Long) getId();
	}

	@Override
	protected OrgDiscapacitadosPersona createInstance() {
		OrgDiscapacitadosPersona orgDiscapacitadosPersona = new OrgDiscapacitadosPersona();
		return orgDiscapacitadosPersona;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		OrganizacionDiscapacitados organizacionDiscapacitados = organizacionDiscapacitadosHome
				.getDefinedInstance();
		if (organizacionDiscapacitados != null) {
			getInstance().setOrganizacionDiscapacitados(
					organizacionDiscapacitados);
		}
		Persona persona = personaHome.getDefinedInstance();
		if (persona != null) {
			getInstance().setPersona(persona);
		}
	}

	public boolean isWired() {
		if (getInstance().getOrganizacionDiscapacitados() == null)
			return false;
		if (getInstance().getPersona() == null)
			return false;
		return true;
	}

	public OrgDiscapacitadosPersona getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
