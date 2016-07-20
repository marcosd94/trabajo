package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.CapacitacionCerrada;
import py.com.excelsis.sicca.entity.Capacitaciones;

@Name("capacitacionCerradaHome")
public class CapacitacionCerradaHome extends EntityHome<CapacitacionCerrada> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setCapacitacionCerradaIdCapacitacionCerrada(Long id) {
		setId(id);
	}

	public Long getCapacitacionCerradaIdCapacitacionCerrada() {
		return (Long) getId();
	}

	@Override
	protected CapacitacionCerrada createInstance() {
		CapacitacionCerrada capacitacionCerrada = new CapacitacionCerrada();
		return capacitacionCerrada;
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

	public CapacitacionCerrada getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
