package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("alertasEvalDetHome")
public class AlertasEvalDetHome extends EntityHome<AlertasEvalDet> {

	@In(create = true)
	AlertasEvalHome alertasEvalHome;

	public void setAlertasEvalDetIdAlertasEvalDet(Long id) {
		setId(id);
	}

	public Long getAlertasEvalDetIdAlertasEvalDet() {
		return (Long) getId();
	}

	@Override
	protected AlertasEvalDet createInstance() {
		AlertasEvalDet alertasEvalDet = new AlertasEvalDet();
		return alertasEvalDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		AlertasEval alertasEval = alertasEvalHome.getDefinedInstance();
		if (alertasEval != null) {
			getInstance().setAlertasEval(alertasEval);
		}
	}

	public boolean isWired() {
		if (getInstance().getAlertasEval() == null)
			return false;
		return true;
	}

	public AlertasEvalDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
