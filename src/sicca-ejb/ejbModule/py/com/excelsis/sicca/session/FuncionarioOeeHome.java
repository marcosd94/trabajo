package py.com.excelsis.sicca.session;

import java.util.Date;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.FuncionarioOee;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("funcionarioOeeHome")
public class FuncionarioOeeHome extends EntityHome<FuncionarioOee> {

	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "funcionarioOee";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@In(create = true)
	TipoNombramientoHome tipoNombramientoHome;
	@In(create = true)
	ConfiguracionUoDetHome configuracionUoDetHome;
	@In(create = true)
	PersonaHome personaHome;

	public void setFuncionarioOeeIdFuncionarioOee(Long id) {
		setId(id);
	}

	public Long getFuncionarioOeeIdFuncionarioOee() {
		return (Long) getId();
	}

	@Override
	protected FuncionarioOee createInstance() {
		FuncionarioOee funcionarioOee = new FuncionarioOee();
		return funcionarioOee;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		TipoNombramiento tipoNombramiento = tipoNombramientoHome
				.getDefinedInstance();
		if (tipoNombramiento != null) {
			getInstance().setTipoNombramiento(tipoNombramiento);
		}
		Persona persona = personaHome.getDefinedInstance();
		if (persona != null) {
			getInstance().setPersona(persona);
		}
	}

	public boolean isWired() {
		if (getInstance().getTipoNombramiento() == null)
			return false;
		if (getInstance().getPersona() == null)
			return false;
		return true;
	}

	public FuncionarioOee getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public String save(){
		if(!isIdDefined()){
			return persist();
		}
		return update();
	}

}
