package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.Instructores;

@Name("instructoresHome")
public class InstructoresHome extends EntityHome<Instructores> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setInstructoresIdInstructor(Long id) {
		setId(id);
	}

	public Long getInstructoresIdInstructor() {
		return (Long) getId();
	}

	@Override
	protected Instructores createInstance() {
		Instructores instructores = new Instructores();
		return instructores;
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

	public Instructores getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
