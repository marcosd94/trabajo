package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("historicoEvaluacionHome")
public class HistoricoEvaluacionHome extends EntityHome<HistoricoEvaluacion> {

	public void setHistoricoEvaluacion(HistoricoEvaluacion id) {
		setId(id);
	}

	public HistoricoEvaluacion getHistoricoEvaluacionId() {
		return (HistoricoEvaluacion) getId();
	}

	public HistoricoEvaluacionHome() {
		setHistoricoEvaluacion(new HistoricoEvaluacion());
	}

	@Override
	public boolean isIdDefined() {
		
		return true;
	}

	@Override
	protected HistoricoEvaluacion createInstance() {
		HistoricoEvaluacion historicoEvaluacion = new HistoricoEvaluacion();
		return historicoEvaluacion;
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

	public HistoricoEvaluacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
