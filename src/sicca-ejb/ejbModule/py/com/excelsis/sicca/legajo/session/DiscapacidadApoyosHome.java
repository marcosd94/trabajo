package py.com.excelsis.sicca.legajo.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.DiscapacidadApoyos;

@Name("discapacidadApoyosHome")
public class DiscapacidadApoyosHome extends EntityHome<DiscapacidadApoyos> {

	public void setDiscapacidadApoyosIdDiscapacidadApoyos(Long id) {
		setId(id);
	}

	public Long getDiscapacidadApoyosIdDiscapacidadApoyos() {
		return (Long) getId();
	}

	@Override
	protected DiscapacidadApoyos createInstance() {
		DiscapacidadApoyos discapacidadApoyos = new DiscapacidadApoyos();
		return discapacidadApoyos;
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

	public DiscapacidadApoyos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
