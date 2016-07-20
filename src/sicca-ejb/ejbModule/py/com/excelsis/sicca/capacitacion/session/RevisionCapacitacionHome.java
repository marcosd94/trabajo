package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("revisionCapacitacionHome")
public class RevisionCapacitacionHome extends EntityHome<RevisionCapacitacion> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setRevisionCapacitacionIdRevision(Long id) {
		setId(id);
	}

	public Long getRevisionCapacitacionIdRevision() {
		return (Long) getId();
	}

	@Override
	protected RevisionCapacitacion createInstance() {
		RevisionCapacitacion revisionCapacitacion = new RevisionCapacitacion();
		return revisionCapacitacion;
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

	public RevisionCapacitacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
