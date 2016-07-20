package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("gruposEvaluacionHome")
public class GruposEvaluacionHome extends EntityHome<GruposEvaluacion> {

	@In(create = true)
	ComisionEvalHome comisionEvalHome;
	@In(create = true)
	EvaluacionDesempenoHome evaluacionDesempenoHome;

	public void setGruposEvaluacionIdGruposEvaluacion(Long id) {
		setId(id);
	}

	public Long getGruposEvaluacionIdGruposEvaluacion() {
		return (Long) getId();
	}

	@Override
	protected GruposEvaluacion createInstance() {
		GruposEvaluacion gruposEvaluacion = new GruposEvaluacion();
		return gruposEvaluacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ComisionEval comisionEval = comisionEvalHome.getDefinedInstance();
		if (comisionEval != null) {
			getInstance().setComisionEval(comisionEval);
		}
		EvaluacionDesempeno evaluacionDesempeno = evaluacionDesempenoHome
				.getDefinedInstance();
		if (evaluacionDesempeno != null) {
			getInstance().setEvaluacionDesempeno(evaluacionDesempeno);
		}
	}

	public boolean isWired() {
		if (getInstance().getComisionEval() == null)
			return false;
		if (getInstance().getEvaluacionDesempeno() == null)
			return false;
		return true;
	}

	public GruposEvaluacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	
	public List<GruposSujetos> getGruposSujetoses() {
		return getInstance() == null ? null : new ArrayList<GruposSujetos>(
				getInstance().getGruposSujetoses());
	}

}
