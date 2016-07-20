package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.DatosEspecificosHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("cronogramaConcDetHome")
public class CronogramaConcDetHome extends EntityHome<CronogramaConcDet> {

	@In(create = true)
	CronogramaConcCabHome cronogramaConcCabHome;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setCronogramaConcDetIdCronogramaConcDet(Long id) {
		setId(id);
	}

	public Long getCronogramaConcDetIdCronogramaConcDet() {
		return (Long) getId();
	}

	@Override
	protected CronogramaConcDet createInstance() {
		CronogramaConcDet cronogramaConcDet = new CronogramaConcDet();
		return cronogramaConcDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		CronogramaConcCab cronogramaConcCab = cronogramaConcCabHome
				.getDefinedInstance();
		if (cronogramaConcCab != null) {
			getInstance().setCronogramaConcCab(cronogramaConcCab);
		}
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getCronogramaConcCab() == null)
			return false;
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public CronogramaConcDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
