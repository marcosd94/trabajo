package py.com.excelsis.sicca.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetReqMin;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detReqMinHome")
public class DetReqMinHome extends EntityHome<DetReqMin> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	RequisitoMinimoCptHome requisitoMinimoCptHome;
	
	public static final String CONTEXT_VAR_NAME = "detReqMinimos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setDetReqMinIdDetReqMin(Long id) {
		setId(id);
	}

	public Long getDetReqMinIdDetReqMin() {
		return (Long) getId();
	}

	@Override
	protected DetReqMin createInstance() {
		DetReqMin detReqMin = new DetReqMin();
		return detReqMin;
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
		RequisitoMinimoCpt requisitoMinimoCpt = requisitoMinimoCptHome
				.getDefinedInstance();
		if (requisitoMinimoCpt != null) {
			getInstance().setRequisitoMinimoCpt(requisitoMinimoCpt);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getCpt() == null)
			return false;
		if (getInstance().getRequisitoMinimoCpt() == null)
			return false;
		return true;
	}

	public DetReqMin getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
			
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@Override
    public void setInstance(DetReqMin instance)
    {
        if (instance != null)
        {
            super.setId(instance.getIdDetReqMin());
        }
        super.setInstance(instance);
    }


}
