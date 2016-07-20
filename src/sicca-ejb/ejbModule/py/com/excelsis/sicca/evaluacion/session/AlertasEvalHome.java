package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("alertasEvalHome")
public class AlertasEvalHome extends EntityHome<AlertasEval> {

	public void setAlertasEvalIdAlertasEval(Long id) {
		setId(id);
	}

	public Long getAlertasEvalIdAlertasEval() {
		return (Long) getId();
	}

	@Override
	protected AlertasEval createInstance() {
		AlertasEval alertasEval = new AlertasEval();
		return alertasEval;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public AlertasEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<AlertasEvalDet> getAlertasEvalDets() {
		return getInstance() == null ? null : new ArrayList<AlertasEvalDet>(
				getInstance().getAlertasEvalDets());
	}

}
