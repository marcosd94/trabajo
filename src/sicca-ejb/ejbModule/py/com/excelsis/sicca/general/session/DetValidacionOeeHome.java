package py.com.excelsis.sicca.general.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detValidacionOeeHome")
public class DetValidacionOeeHome extends EntityHome<DetValidacionOee> {

	@In(create = true)
	CabValidacionOeeHome cabValidacionOeeHome;

	public void setDetValidacionOeeIdDetValidacionOee(Long id) {
		setId(id);
	}

	public Long getDetValidacionOeeIdDetValidacionOee() {
		return (Long) getId();
	}

	@Override
	protected DetValidacionOee createInstance() {
		DetValidacionOee detValidacionOee = new DetValidacionOee();
		return detValidacionOee;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		CabValidacionOee cabValidacionOee = cabValidacionOeeHome
				.getDefinedInstance();
		if (cabValidacionOee != null) {
			getInstance().setCabValidacionOee(cabValidacionOee);
		}
	}

	public boolean isWired() {
		if (getInstance().getCabValidacionOee() == null)
			return false;
		return true;
	}

	public DetValidacionOee getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
