package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ReprPersonaDiscapacidad;

@Name("reprPersonaDiscapacidadHome")
public class ReprPersonaDiscapacidadHome extends
		EntityHome<ReprPersonaDiscapacidad> {

	public void setReprPersonaDiscapacidadIdReprPersonaDiscapacidad(Integer id) {
		setId(id);
	}

	public Integer getReprPersonaDiscapacidadIdReprPersonaDiscapacidad() {
		return (Integer) getId();
	}

	@Override
	protected ReprPersonaDiscapacidad createInstance() {
		ReprPersonaDiscapacidad reprPersonaDiscapacidad = new ReprPersonaDiscapacidad();
		return reprPersonaDiscapacidad;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public ReprPersonaDiscapacidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
