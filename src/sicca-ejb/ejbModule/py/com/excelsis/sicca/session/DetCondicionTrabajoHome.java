package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detCondicionTrabajoHome")
public class DetCondicionTrabajoHome extends EntityHome<DetCondicionTrabajo> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	CondicionTrabajoHome condicionTrabajoHome;

	public void setDetCondicionTrabajoIdDetCondiconTrabajo(Long id) {
		setId(id);
	}

	public Long getDetCondicionTrabajoIdDetCondiconTrabajo() {
		return (Long) getId();
	}

	@Override
	protected DetCondicionTrabajo createInstance() {
		DetCondicionTrabajo detCondicionTrabajo = new DetCondicionTrabajo();
		return detCondicionTrabajo;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantaCargoDet plantaCargoDet = plantaCargoDetHome.getDefinedInstance();
		if (plantaCargoDet != null) {
			getInstance().setPlantaCargoDet(plantaCargoDet);
		}
		Cpt cpt = cptHome.getDefinedInstance();
		if (cpt != null) {
			getInstance().setCpt(cpt);
		}
		CondicionTrabajo condicionTrabajo = condicionTrabajoHome
				.getDefinedInstance();
		if (condicionTrabajo != null) {
			getInstance().setCondicionTrabajo(condicionTrabajo);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getCpt() == null)
			return false;
		if (getInstance().getCondicionTrabajo() == null)
			return false;
		return true;
	}

	public DetCondicionTrabajo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
