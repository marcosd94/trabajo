package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("publicacionCapacitacionHome")
public class PublicacionCapacitacionHome extends
		EntityHome<PublicacionCapacitacion> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setPublicacionCapacitacionIdPublicacion(Long id) {
		setId(id);
	}

	public Long getPublicacionCapacitacionIdPublicacion() {
		return (Long) getId();
	}

	@Override
	protected PublicacionCapacitacion createInstance() {
		PublicacionCapacitacion publicacionCapacitacion = new PublicacionCapacitacion();
		return publicacionCapacitacion;
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

	public PublicacionCapacitacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
