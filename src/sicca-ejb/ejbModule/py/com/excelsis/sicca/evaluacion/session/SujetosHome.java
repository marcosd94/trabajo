package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("sujetosHome")
public class SujetosHome extends EntityHome<Sujetos> {

	@In(create = true)
	EvaluacionDesempenoHome evaluacionDesempenoHome;

	public void setSujetosIdSujetos(Long id) {
		setId(id);
	}

	public Long getSujetosIdSujetos() {
		return (Long) getId();
	}

	@Override
	protected Sujetos createInstance() {
		Sujetos sujetos = new Sujetos();
		return sujetos;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EvaluacionDesempeno evaluacionDesempeno = evaluacionDesempenoHome
				.getDefinedInstance();
		if (evaluacionDesempeno != null) {
			getInstance().setEvaluacionDesempeno(evaluacionDesempeno);
		}
	}

	public boolean isWired() {
		if (getInstance().getEvaluacionDesempeno() == null)
			return false;
		return true;
	}

	public Sujetos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<GruposSujetos> getGruposSujetoses() {
		return getInstance() == null ? null : new ArrayList<GruposSujetos>(
				getInstance().getGruposSujetoses());
	}

	
}
