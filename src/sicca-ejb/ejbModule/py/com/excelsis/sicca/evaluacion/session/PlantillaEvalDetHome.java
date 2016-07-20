package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("plantillaEvalDetHome")
public class PlantillaEvalDetHome extends EntityHome<PlantillaEvalDet> {

	@In(create = true)
	PlantillaEvalHome plantillaEvalHome;

	public void setPlantillaEvalDetIdPlantillaEvalDet(Long id) {
		setId(id);
	}

	public Long getPlantillaEvalDetIdPlantillaEvalDet() {
		return (Long) getId();
	}

	@Override
	protected PlantillaEvalDet createInstance() {
		PlantillaEvalDet plantillaEvalDet = new PlantillaEvalDet();
		return plantillaEvalDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantillaEval plantillaEval = plantillaEvalHome.getDefinedInstance();
		if (plantillaEval != null) {
			getInstance().setPlantillaEval(plantillaEval);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantillaEval() == null)
			return false;
		return true;
	}

	public PlantillaEvalDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
