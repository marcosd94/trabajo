package py.com.excelsis.sicca.juridicos.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("accionInconstDetHome")
public class AccionInconstDetHome extends EntityHome<AccionInconstDet> {

	@In(create = true)
	AccionInconstCabHome accionInconstCabHome;

	public void setAccionInconstDetIdAccionDet(Long id) {
		setId(id);
	}

	public Long getAccionInconstDetIdAccionDet() {
		return (Long) getId();
	}

	@Override
	protected AccionInconstDet createInstance() {
		AccionInconstDet accionInconstDet = new AccionInconstDet();
		return accionInconstDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		AccionInconstCab accionInconstCab = accionInconstCabHome
				.getDefinedInstance();
		if (accionInconstCab != null) {
			getInstance().setAccionInconstCab(accionInconstCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getAccionInconstCab() == null)
			return false;
		return true;
	}

	public AccionInconstDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
