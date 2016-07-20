package py.com.excelsis.sicca.general.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("detValidacionHome")
public class DetValidacionHome extends EntityHome<DetValidacion> {

	@In(create = true)
	CabValidacionHome cabValidacionHome;
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	public static final String CONTEXT_VAR_NAME = "detValidaciones";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setDetValidacionIdDetValidacion(Long id) {
		setId(id);
	}

	public Long getDetValidacionIdDetValidacion() {
		return (Long) getId();
	}

	@Override
	protected DetValidacion createInstance() {
		DetValidacion detValidacion = new DetValidacion();
		return detValidacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		CabValidacion cabValidacion = cabValidacionHome.getDefinedInstance();
		if (cabValidacion != null) {
			getInstance().setCabValidacion(cabValidacion);
		}
	}

	public boolean isWired() {
		if (getInstance().getCabValidacion() == null)
			return false;
		return true;
	}

	public DetValidacion getDefinedInstance() {
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
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
}
