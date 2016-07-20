package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("historicosEstadoHome")
public class HistoricosEstadoHome extends EntityHome<HistoricosEstado> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	EstadoCabHome estadoCabHome;

	public void setHistoricosEstadoIdHistoricoEstado(Long id) {
		setId(id);
	}

	public Long getHistoricosEstadoIdHistoricoEstado() {
		return (Long) getId();
	}

	@Override
	protected HistoricosEstado createInstance() {
		HistoricosEstado historicosEstado = new HistoricosEstado();
		return historicosEstado;
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
		EstadoCab estadoCab = estadoCabHome.getDefinedInstance();
		if (estadoCab != null) {
			getInstance().setEstadoCab(estadoCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getPlantaCargoDet() == null)
			return false;
		if (getInstance().getEstadoCab() == null)
			return false;
		return true;
	}

	public HistoricosEstado getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
