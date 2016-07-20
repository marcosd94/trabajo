package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ObsCapEval;

@Name("obsCapEvalHome")
public class ObsCapEvalHome extends EntityHome<ObsCapEval> {

	public void setObsCapEvalIdObservacion(Long id) {
		setId(id);
	}

	public Long getObsCapEvalIdObservacion() {
		return (Long) getId();
	}

	@Override
	protected ObsCapEval createInstance() {
		ObsCapEval obsCapEval = new ObsCapEval();
		return obsCapEval;
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

	public ObsCapEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
