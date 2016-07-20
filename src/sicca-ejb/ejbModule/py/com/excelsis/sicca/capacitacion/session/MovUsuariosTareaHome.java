package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("movUsuariosTareaHome")
public class MovUsuariosTareaHome extends EntityHome<MovUsuariosTarea> {

	public void setMovUsuariosTareaIdMov(Long id) {
		setId(id);
	}

	public Long getMovUsuariosTareaIdMov() {
		return (Long) getId();
	}

	@Override
	protected MovUsuariosTarea createInstance() {
		MovUsuariosTarea movUsuariosTarea = new MovUsuariosTarea();
		return movUsuariosTarea;
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

	public MovUsuariosTarea getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
