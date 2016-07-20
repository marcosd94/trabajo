package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.DatosEspecificosHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("personaPostulanteHome")
public class PersonaPostulanteHome extends EntityHome<PersonaPostulante> {

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(required = false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "personasPostulantes";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	

	public void setPersonaPostulanteIdPersonaPostulante(Long id) {
		setId(id);
	}

	public Long getPersonaPostulanteIdPersonaPostulante() {
		return (Long) getId();
	}

	@Override
	protected PersonaPostulante createInstance() {
		PersonaPostulante personaPostulante = new PersonaPostulante();
		return personaPostulante;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	

	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	public boolean isWired() {
		return true;
	}

	public PersonaPostulante getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PersonaDiscapacidad> getPersonaDiscapacidads() {
		return getInstance() == null ? null
				: new ArrayList<PersonaDiscapacidad>(getInstance()
						.getPersonaDiscapacidads());
	}

	public List<Postulacion> getPostulacions() {
		return getInstance() == null ? null : new ArrayList<Postulacion>(
				getInstance().getPostulacions());
	}


	public List<ExperienciaLaboral> getExperienciaLaborals() {
		return getInstance() == null ? null
				: new ArrayList<ExperienciaLaboral>(getInstance()
						.getExperienciaLaborals());
	}

	public List<EstudiosRealizados> getEstudiosRealizadoses() {
		return getInstance() == null ? null
				: new ArrayList<EstudiosRealizados>(getInstance()
						.getEstudiosRealizadoses());
	}

}
