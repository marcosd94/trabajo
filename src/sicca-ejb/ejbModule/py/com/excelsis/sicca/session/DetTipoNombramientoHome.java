package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detTipoNombramientoHome")
public class DetTipoNombramientoHome extends EntityHome<DetTipoNombramiento> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	TipoNombramientoHome tipoNombramientoHome;
	@In(create = true)
	CptHome cptHome;

	public void setDetTipoNombramientoIdDetTipoNombramiento(Long id) {
		setId(id);
	}

	public Long getDetTipoNombramientoIdDetTipoNombramiento() {
		return (Long) getId();
	}

	@Override
	protected DetTipoNombramiento createInstance() {
		DetTipoNombramiento detTipoNombramiento = new DetTipoNombramiento();
		return detTipoNombramiento;
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
		TipoNombramiento tipoNombramiento = tipoNombramientoHome
				.getDefinedInstance();
		if (tipoNombramiento != null) {
			getInstance().setTipoNombramiento(tipoNombramiento);
		}
		Cpt cpt = cptHome.getDefinedInstance();
		if (cpt != null) {
			getInstance().setCpt(cpt);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getTipoNombramiento() == null)
			return false;
		if (getInstance().getCpt() == null)
			return false;
		return true;
	}

	public DetTipoNombramiento getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
