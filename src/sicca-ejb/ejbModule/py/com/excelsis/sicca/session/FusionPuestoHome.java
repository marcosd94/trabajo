package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("fusionPuestoHome")
public class FusionPuestoHome extends EntityHome<FusionPuesto> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;

	public void setFusionPuestoIdFusionPuesto(Long id) {
		setId(id);
	}

	public Long getFusionPuestoIdFusionPuesto() {
		return (Long) getId();
	}

	@Override
	protected FusionPuesto createInstance() {
		FusionPuesto fusionPuesto = new FusionPuesto();
		return fusionPuesto;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantaCargoDet plantaCargoDetByPuestoFusionado = plantaCargoDetHome
				.getDefinedInstance();
		if (plantaCargoDetByPuestoFusionado != null) {
			getInstance().setPlantaCargoDetByPuestoFusionado(
					plantaCargoDetByPuestoFusionado);
		}
		PlantaCargoDet plantaCargoDetByIdPlantaCargoDet = plantaCargoDetHome
				.getDefinedInstance();
		if (plantaCargoDetByIdPlantaCargoDet != null) {
			getInstance().setPlantaCargoDetByIdPlantaCargoDet(
					plantaCargoDetByIdPlantaCargoDet);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDetByPuestoFusionado() == null)
			return false;
		if (getInstance().getPlantaCargoDetByIdPlantaCargoDet() == null)
			return false;
		return true;
	}

	public FusionPuesto getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
