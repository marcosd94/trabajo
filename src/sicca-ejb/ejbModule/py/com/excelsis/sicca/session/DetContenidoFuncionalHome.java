package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detContenidoFuncionalHome")
public class DetContenidoFuncionalHome extends
		EntityHome<DetContenidoFuncional> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	ContenidoFuncionalHome contenidoFuncionalHome;
	
	public static final String CONTEXT_VAR_NAME = "detContenidosFuncionales";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setDetContenidoFuncionalIdContenidoFuncional(Long id) {
		setId(id);
	}

	public Long getDetContenidoFuncionalIdContenidoFuncional() {
		return (Long) getId();
	}

	@Override
	protected DetContenidoFuncional createInstance() {
		DetContenidoFuncional detContenidoFuncional = new DetContenidoFuncional();
		return detContenidoFuncional;
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
		ContenidoFuncional contenidoFuncional = contenidoFuncionalHome
				.getDefinedInstance();
		if (contenidoFuncional != null) {
			getInstance().setContenidoFuncional(contenidoFuncional);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getCpt() == null)
			return false;
		if (getInstance().getContenidoFuncional() == null)
			return false;
		return true;
	}
	
	@Override
	public String persist() {
			
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	

	public DetContenidoFuncional getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DetDescripcionContFuncional> getDetDescripcionContFuncionals() {
		return getInstance() == null ? null
				: new ArrayList<DetDescripcionContFuncional>(getInstance()
						.getDetDescripcionContFuncionals());
	}

}
