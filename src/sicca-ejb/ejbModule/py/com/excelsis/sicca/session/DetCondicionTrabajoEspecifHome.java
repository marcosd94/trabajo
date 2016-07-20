package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detCondicionTrabajoEspecifHome")
public class DetCondicionTrabajoEspecifHome extends
		EntityHome<DetCondicionTrabajoEspecif> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	CondicionTrabajoEspecifHome condicionTrabajoEspecifHome;

	public void setDetCondicionTrabajoEspecifIdDetCondicionTrabajoEspecif(
			Long id) {
		setId(id);
	}

	public Long getDetCondicionTrabajoEspecifIdDetCondicionTrabajoEspecif() {
		return (Long) getId();
	}

	@Override
	protected DetCondicionTrabajoEspecif createInstance() {
		DetCondicionTrabajoEspecif detCondicionTrabajoEspecif = new DetCondicionTrabajoEspecif();
		return detCondicionTrabajoEspecif;
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
		CondicionTrabajoEspecif condicionTrabajoEspecif = condicionTrabajoEspecifHome
				.getDefinedInstance();
		if (condicionTrabajoEspecif != null) {
			getInstance().setCondicionTrabajoEspecif(condicionTrabajoEspecif);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getCpt() == null)
			return false;
		if (getInstance().getCondicionTrabajoEspecif() == null)
			return false;
		return true;
	}

	public DetCondicionTrabajoEspecif getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
