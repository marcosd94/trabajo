package py.com.excelsis.sicca.legajo.session;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.ExperienciaLaboralLegajo;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.PersonaHome;

@Name("experienciaLaboralLegajoHome")
public class ExperienciaLaboralLegajoHome extends EntityHome<ExperienciaLaboralLegajo> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@In(create = true)
	PersonaHome personaHome;

	public static final String CONTEXT_VAR_NAME = "experienciaLaboralLegajo";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	public void setExperienciaLaboralLegajoIdExperienciaLab(Long id) {
		setId(id);
	}

	public Long getExperienciaLaborallLegajoIdExperienciaLab() {
		return (Long) getId();
	}

	@Override
	protected ExperienciaLaboralLegajo createInstance() {
		ExperienciaLaboralLegajo experienciaLaboral = new ExperienciaLaboralLegajo();
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

	public ExperienciaLaboralLegajo getDefinedInstance() {
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
