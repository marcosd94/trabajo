package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;

@Name("comisionSeleccionDetHome")
public class ComisionSeleccionDetHome extends EntityHome<ComisionSeleccionDet> {

	@In(create = true)
	ComisionSeleccionCabHome comisionSeleccionCabHome;

	public void setComisionSeleccionDetIdComisionSelDet(Long id) {
		setId(id);
	}

	public Long getComisionSeleccionDetIdComisionSelDet() {
		return (Long) getId();
	}

	@Override
	protected ComisionSeleccionDet createInstance() {
		ComisionSeleccionDet comisionSeleccionDet = new ComisionSeleccionDet();
		return comisionSeleccionDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ComisionSeleccionCab comisionSeleccionCab = comisionSeleccionCabHome
				.getDefinedInstance();
		if (comisionSeleccionCab != null) {
			getInstance().setComisionSeleccionCab(comisionSeleccionCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getComisionSeleccionCab() == null)
			return false;
		return true;
	}

	public ComisionSeleccionDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
