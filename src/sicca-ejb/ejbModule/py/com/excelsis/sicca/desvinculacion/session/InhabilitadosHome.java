package py.com.excelsis.sicca.desvinculacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("inhabilitadosHome")
public class InhabilitadosHome extends EntityHome<Inhabilitados> {

	@In(create = true)
	EmpleadoPuestoHome empleadoPuestoHome;

	public void setInhabilitadosIdInhabilitado(Long id) {
		setId(id);
	}

	public Long getInhabilitadosIdInhabilitado() {
		return (Long) getId();
	}

	@Override
	protected Inhabilitados createInstance() {
		Inhabilitados inhabilitados = new Inhabilitados();
		return inhabilitados;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EmpleadoPuesto empleadoPuesto = empleadoPuestoHome.getDefinedInstance();
		if (empleadoPuesto != null) {
			getInstance().setEmpleadoPuesto(empleadoPuesto);
		}
	}

	public boolean isWired() {
		if (getInstance().getEmpleadoPuesto() == null)
			return false;
		return true;
	}

	public Inhabilitados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
