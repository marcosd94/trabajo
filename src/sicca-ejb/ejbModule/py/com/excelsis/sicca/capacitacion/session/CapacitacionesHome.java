package py.com.excelsis.sicca.capacitacion.session;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.CapacitacionFinanciacion;
import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ComisionCapacEval;
import py.com.excelsis.sicca.entity.Instructores;

@Name("capacitacionesHome")
public class CapacitacionesHome extends EntityHome<Capacitaciones> {

	public void setCapacitacionesIdCapacitacion(Long id) {
		setId(id);
	}

	public Long getCapacitacionesIdCapacitacion() {
		return (Long) getId();
	}

	@Override
	protected Capacitaciones createInstance() {
		Capacitaciones capacitaciones = new Capacitaciones();
		return capacitaciones;
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

	public Capacitaciones getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<CapacitacionFinanciacion> getCapacitacionFinanciacions() {
		return getInstance() == null ? null
				: new ArrayList<CapacitacionFinanciacion>(getInstance()
						.getCapacitacionFinanciacions());
	}

	public List<CapacitacionCerrada> getCapacitacionCerradas() {
		return getInstance() == null ? null
				: new ArrayList<CapacitacionCerrada>(getInstance()
						.getCapacitacionCerradas());
	}

	public List<ComisionCapacEval> getComisionCapacEvals() {
		return getInstance() == null ? null : new ArrayList<ComisionCapacEval>(
				getInstance().getComisionCapacEvals());
	}

	public List<Instructores> getInstructoreses() {
		return getInstance() == null ? null : new ArrayList<Instructores>(
				getInstance().getInstructoreses());
	}

}
