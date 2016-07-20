package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("concursoPuestoDetHome")
public class ConcursoPuestoDetHome extends EntityHome<ConcursoPuestoDet> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	EstadoDetHome estadoDetHome;

	public void setConcursoPuestoDetIdConcursoPuestoDet(Long id) {
		setId(id);
	}

	public Long getConcursoPuestoDetIdConcursoPuestoDet() {
		return (Long) getId();
	}

	@Override
	protected ConcursoPuestoDet createInstance() {
		ConcursoPuestoDet concursoPuestoDet = new ConcursoPuestoDet();
		return concursoPuestoDet;
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
		
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
		EstadoDet estadoDet = estadoDetHome.getDefinedInstance();
		if (estadoDet != null) {
			getInstance().setEstadoDet(estadoDet);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		
		if (getInstance().getEstadoDet() == null)
			return false;
		return true;
	}

	public ConcursoPuestoDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
