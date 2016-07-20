package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;

@Name("capacitacionFinanciacionHome")
public class CapacitacionFinanciacionHome extends
		EntityHome<CapacitacionFinanciacion> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setCapacitacionFinanciacionIdCapacitacionFinanciacion(Long id) {
		setId(id);
	}

	public Long getCapacitacionFinanciacionIdCapacitacionFinanciacion() {
		return (Long) getId();
	}

	@Override
	protected CapacitacionFinanciacion createInstance() {
		CapacitacionFinanciacion capacitacionFinanciacion = new CapacitacionFinanciacion();
		return capacitacionFinanciacion;
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

	public CapacitacionFinanciacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
