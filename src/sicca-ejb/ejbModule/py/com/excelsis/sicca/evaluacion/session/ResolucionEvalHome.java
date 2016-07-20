package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("resolucionEvalHome")
public class ResolucionEvalHome extends EntityHome<ResolucionEval> {

	public void setResolucionEvalIdResolucionEval(Long id) {
		setId(id);
	}

	public Long getResolucionEvalIdResolucionEval() {
		return (Long) getId();
	}

	@Override
	protected ResolucionEval createInstance() {
		ResolucionEval resolucionEval = new ResolucionEval();
		return resolucionEval;
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

	public ResolucionEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
