package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Parentesco;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("parentescoHome")
public class ParentescoHome extends EntityHome<Parentesco> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	PersonaHome personaHome;
	
	public static final String CONTEXT_VAR_NAME = "parentesco";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	public void setParentescoIdParentesco(Long id) {
		setId(id);
	}

	public Long getParentescoIdParentesco() {
		return (Long) getId();
	}

	@Override
	protected Parentesco createInstance() {
		Parentesco experienciaLaboral = new Parentesco();
		return experienciaLaboral;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Persona persona = personaHome.getDefinedInstance();
		if (persona != null) {
			getInstance().setPersona(persona);
		}
	}

	public boolean isWired() {
		if (getInstance().getPersona() == null)
			return false;
		return true;
	}

	public Parentesco getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setNombres(getInstance().getNombres().trim());
		getInstance().setApellidos(getInstance().getApellidos().trim());
		getInstance().setDocumentoIdentidad(getInstance().getDocumentoIdentidad().trim());
		getInstance().setInstitucion(getInstance().getInstitucion().trim());
				return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setNombres(getInstance().getNombres().trim());
		getInstance().setApellidos(getInstance().getApellidos().trim());
		getInstance().setDocumentoIdentidad(getInstance().getDocumentoIdentidad().trim());
		getInstance().setInstitucion(getInstance().getInstitucion().trim());
				return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public String save(){
		if(getInstance().getIdParentesco() == null){
			return persist();
		}
		return update();
	}

}