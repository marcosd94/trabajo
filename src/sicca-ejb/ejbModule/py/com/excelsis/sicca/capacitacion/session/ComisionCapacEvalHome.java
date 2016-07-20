package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Capacitaciones;
import py.com.excelsis.sicca.entity.ComisionCapacEval;

@Name("comisionCapacEvalHome")
public class ComisionCapacEvalHome extends EntityHome<ComisionCapacEval> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setComisionCapacEvalIdComision(Long id) {
		setId(id);
	}

	public Long getComisionCapacEvalIdComision() {
		return (Long) getId();
	}

	@Override
	protected ComisionCapacEval createInstance() {
		ComisionCapacEval comisionCapacEval = new ComisionCapacEval();
		return comisionCapacEval;
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

	public ComisionCapacEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
