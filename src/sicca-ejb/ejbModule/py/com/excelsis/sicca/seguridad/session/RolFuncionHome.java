package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("rolFuncionHome")
public class RolFuncionHome extends EntityHome<RolFuncion> {

	@In(create = true)
	FuncionHome funcionHome;
	@In(create = true)
	RolHome rolHome;

	public void setRolFuncionIdRolFuncion(Long id) {
		setId(id);
	}

	public Long getRolFuncionIdRolFuncion() {
		return (Long) getId();
	}

	@Override
	protected RolFuncion createInstance() {
		RolFuncion rolFuncion = new RolFuncion();
		return rolFuncion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Funcion funcion = funcionHome.getDefinedInstance();
		if (funcion != null) {
			getInstance().setFuncion(funcion);
		}
		Rol rol = rolHome.getDefinedInstance();
		if (rol != null) {
			getInstance().setRol(rol);
		}
	}

	public boolean isWired() {
		if (getInstance().getFuncion() == null)
			return false;
		if (getInstance().getRol() == null)
			return false;
		return true;
	}

	public RolFuncion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
