package py.com.excelsis.sicca.juridicos.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.EmpleadoPuestoHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("accionInconstCabHome")
public class AccionInconstCabHome extends EntityHome<AccionInconstCab> {

	@In(create = true)
	EmpleadoPuestoHome empleadoPuestoHome;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	public static final String CONTEXT_VAR_NAME = "accionesInconstCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setAccionInconstCabIdAccionCab(Long id) {
		setId(id);
	}

	public Long getAccionInconstCabIdAccionCab() {
		return (Long) getId();
	}

	@Override
	protected AccionInconstCab createInstance() {
		AccionInconstCab accionInconstCab = new AccionInconstCab();
		return accionInconstCab;
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

	public AccionInconstCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<AccionInconstDet> getAccionInconstDets() {
		return getInstance() == null ? null : new ArrayList<AccionInconstDet>(
				getInstance().getAccionInconstDets());
	}
	
	@Override
	public String persist() {
		
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

}
