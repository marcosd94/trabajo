package py.com.excelsis.sicca.desvinculacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("jubiladosHome")
public class JubiladosHome extends EntityHome<Jubilados> {

	@In(create = true)
	EmpleadoPuestoHome empleadoPuestoHome;

	public void setJubiladosIdJubilado(Long id) {
		setId(id);
	}

	public Long getJubiladosIdJubilado() {
		return (Long) getId();
	}

	@Override
	protected Jubilados createInstance() {
		Jubilados jubilados = new Jubilados();
		return jubilados;
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

	public Jubilados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
