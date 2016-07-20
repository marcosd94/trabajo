package py.com.excelsis.sicca.session;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.ExperienciaLaboral;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("experienciaLaboralHome")
public class ExperienciaLaboralHome extends EntityHome<ExperienciaLaboral> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	PersonaHome personaHome;

	public static final String CONTEXT_VAR_NAME = "experienciaLaboral";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	public void setExperienciaLaboralIdExperienciaLab(Long id) {
		setId(id);
	}

	public Long getExperienciaLaboralIdExperienciaLab() {
		return (Long) getId();
	}

	@Override
	protected ExperienciaLaboral createInstance() {
		ExperienciaLaboral experienciaLaboral = new ExperienciaLaboral();
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

	public ExperienciaLaboral getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setEmpresa(getInstance().getEmpresa().trim());
		getInstance().setFuncionesRealizadas(getInstance().getFuncionesRealizadas().trim());
		getInstance().setReferenciasLaborales(getInstance().getReferenciasLaborales().trim());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setEmpresa(getInstance().getEmpresa().trim());
		getInstance().setFuncionesRealizadas(getInstance().getFuncionesRealizadas().trim());
		getInstance().setReferenciasLaborales(getInstance().getReferenciasLaborales().trim());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public String save(){
		if(getInstance().getIdExperienciaLab() == null){
			return persist();
		}
		return update();
	}

}
