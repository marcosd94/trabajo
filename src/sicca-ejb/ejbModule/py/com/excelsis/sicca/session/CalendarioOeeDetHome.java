package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("calendarioOeeDetHome")
public class CalendarioOeeDetHome extends EntityHome<CalendarioOeeDet> {

	@In(create = true)
	CalendarioOeeCabHome calendarioOeeCabHome;

	public void setCalendarioOeeDetIdCalendarioOeeDet(Long id) {
		setId(id);
	}

	public Long getCalendarioOeeDetIdCalendarioOeeDet() {
		return (Long) getId();
	}

	@Override
	protected CalendarioOeeDet createInstance() {
		CalendarioOeeDet calendarioOeeDet = new CalendarioOeeDet();
		return calendarioOeeDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		CalendarioOeeCab calendarioOeeCab = calendarioOeeCabHome
				.getDefinedInstance();
		if (calendarioOeeCab != null) {
			getInstance().setCalendarioOeeCab(calendarioOeeCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getCalendarioOeeCab() == null)
			return false;
		return true;
	}

	public CalendarioOeeDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
