package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detOpcionesConvenientesHome")
public class DetOpcionesConvenientesHome extends
		EntityHome<DetOpcionesConvenientes> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	RequisitoMinimoCptHome requisitoMinimoCptHome;

	public void setDetOpcionesConvenientesIdDetOpcionesConvenientes(Long id) {
		setId(id);
	}

	public Long getDetOpcionesConvenientesIdDetOpcionesConvenientes() {
		return (Long) getId();
	}

	@Override
	protected DetOpcionesConvenientes createInstance() {
		DetOpcionesConvenientes detOpcionesConvenientes = new DetOpcionesConvenientes();
		return detOpcionesConvenientes;
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

	public DetOpcionesConvenientes getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
