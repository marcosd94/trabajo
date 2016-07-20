package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("evaluacionDesempenoHome")
public class EvaluacionDesempenoHome extends EntityHome<EvaluacionDesempeno> {

	public void setEvaluacionDesempenoIdEvaluacionDesempeno(Long id) {
		setId(id);
	}

	public Long getEvaluacionDesempenoIdEvaluacionDesempeno() {
		return (Long) getId();
	}

	@Override
	protected EvaluacionDesempeno createInstance() {
		EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
		return evaluacionDesempeno;
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

	public EvaluacionDesempeno getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<Sujetos> getSujetoses() {
		return getInstance() == null ? null : new ArrayList<Sujetos>(
				getInstance().getSujetoses());
	}

	public List<GruposEvaluacion> getGruposEvaluacions() {
		return getInstance() == null ? null : new ArrayList<GruposEvaluacion>(
				getInstance().getGruposEvaluacions());
	}

	public List<ComisionEval> getComisionEvals() {
		return getInstance() == null ? null : new ArrayList<ComisionEval>(
				getInstance().getComisionEvals());
	}

}
