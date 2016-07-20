package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.session.RolHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("procActividadRolHome")
public class ProcActividadRolHome extends EntityHome<ProcActividadRol> {

	@In(create = true)
	RolHome rolHome;
	@In(create = true)
	ActividadProcesoHome actividadProcesoHome;

	public void setProcActividadRolIdProcActividadRol(Long id) {
		setId(id);
	}

	public Long getProcActividadRolIdProcActividadRol() {
		return (Long) getId();
	}

	@Override
	protected ProcActividadRol createInstance() {
		ProcActividadRol procActividadRol = new ProcActividadRol();
		return procActividadRol;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Rol rol = rolHome.getDefinedInstance();
		if (rol != null) {
			getInstance().setRol(rol);
		}
		ActividadProceso actividadProceso = actividadProcesoHome
				.getDefinedInstance();
		if (actividadProceso != null) {
			getInstance().setActividadProceso(actividadProceso);
		}
	}

	public boolean isWired() {
		if (getInstance().getRol() == null)
			return false;
		if (getInstance().getActividadProceso() == null)
			return false;
		return true;
	}

	public ProcActividadRol getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
