package py.com.excelsis.sicca.capacitacion.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("consultasCapacitacionHome")
public class ConsultasCapacitacionHome extends
		EntityHome<ConsultasCapacitacion> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "consultasCapacitacions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setConsultasCapacitacionIdConsultas(Long id) {
		setId(id);
	}

	public Long getConsultasCapacitacionIdConsultas() {
		return (Long) getId();
	}

	@Override
	protected ConsultasCapacitacion createInstance() {
		ConsultasCapacitacion consultasCapacitacion = new ConsultasCapacitacion();
		return consultasCapacitacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Capacitaciones capacitaciones = capacitacionesHome.getDefinedInstance();
		if (capacitaciones != null) {
			getInstance().setCapacitaciones(capacitaciones);
		}
	}

	public boolean isWired() {
		if (getInstance().getCapacitaciones() == null)
			return false;
		return true;
	}

	public ConsultasCapacitacion getDefinedInstance() {
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
