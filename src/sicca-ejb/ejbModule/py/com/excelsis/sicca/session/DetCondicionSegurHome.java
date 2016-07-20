package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetCondicionSegur;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detCondicionSegurHome")
public class DetCondicionSegurHome extends EntityHome<DetCondicionSegur> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	CondicionSegurHome condicionSegurHome;

	public void setDetCondicionSegurIdDetCondicionSegur(Long id) {
		setId(id);
	}

	public Long getDetCondicionSegurIdDetCondicionSegur() {
		return (Long) getId();
	}

	@Override
	protected DetCondicionSegur createInstance() {
		DetCondicionSegur detCondicionSegur = new DetCondicionSegur();
		return detCondicionSegur;
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
		CondicionSegur condicionSegur = condicionSegurHome.getDefinedInstance();
		if (condicionSegur != null) {
			getInstance().setCondicionSegur(condicionSegur);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getCpt() == null)
			return false;
		if (getInstance().getCondicionSegur() == null)
			return false;
		return true;
	}

	public DetCondicionSegur getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
