package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("bandejaCapacitacionHome")
public class BandejaCapacitacionHome extends EntityHome<BandejaCapacitacion> {

	public void setBandejaCapacitacionIdBandejaCapacitacion(Long id) {
		setId(id);
	}

	public Long getBandejaCapacitacionIdBandejaCapacitacion() {
		return (Long) getId();
	}

	@Override
	protected BandejaCapacitacion createInstance() {
		BandejaCapacitacion bandejaCapacitacion = new BandejaCapacitacion();
		return bandejaCapacitacion;
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

	public BandejaCapacitacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	

}
