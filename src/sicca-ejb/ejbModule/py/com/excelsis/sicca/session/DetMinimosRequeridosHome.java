package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detMinimosRequeridosHome")
public class DetMinimosRequeridosHome extends EntityHome<DetMinimosRequeridos> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	RequisitoMinimoCptHome requisitoMinimoCptHome;

	public void setDetMinimosRequeridosIdMinimosRequeridos(Long id) {
		setId(id);
	}

	public Long getDetMinimosRequeridosIdMinimosRequeridos() {
		return (Long) getId();
	}

	@Override
	protected DetMinimosRequeridos createInstance() {
		DetMinimosRequeridos detMinimosRequeridos = new DetMinimosRequeridos();
		return detMinimosRequeridos;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantaCargoDet plantaCargoDet = plantaCargoDetHome.getDefinedInstance();
		
		Cpt cpt = cptHome.getDefinedInstance();
		
		RequisitoMinimoCpt requisitoMinimoCpt = requisitoMinimoCptHome
				.getDefinedInstance();
		
	}

	public boolean isWired() {
		
		return true;
	}

	public DetMinimosRequeridos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
