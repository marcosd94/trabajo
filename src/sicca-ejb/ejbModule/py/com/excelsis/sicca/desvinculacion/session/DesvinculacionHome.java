package py.com.excelsis.sicca.desvinculacion.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("desvinculacionHome")
public class DesvinculacionHome extends EntityHome<Desvinculacion> {

	@In(create = true)
	EmpleadoPuestoHome empleadoPuestoHome;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	public static final String CONTEXT_VAR_NAME = "desvinculaciones";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setDesvinculacionIdDesvinculacion(Long id) {
		setId(id);
	}

	public Long getDesvinculacionIdDesvinculacion() {
		return (Long) getId();
	}

	@Override
	protected Desvinculacion createInstance() {
		Desvinculacion desvinculacion = new Desvinculacion();
		return desvinculacion;
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

	public Desvinculacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

}
