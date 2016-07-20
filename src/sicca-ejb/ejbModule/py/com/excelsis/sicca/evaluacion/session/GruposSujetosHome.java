package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("gruposSujetosHome")
public class GruposSujetosHome extends EntityHome<GruposSujetos> {

	@In(create = true)
	SujetosHome sujetosHome;
	@In(create = true)
	GruposEvaluacionHome gruposEvaluacionHome;

	public void setGruposSujetosIdGrupoSujeto(Long id) {
		setId(id);
	}

	public Long getGruposSujetosIdGrupoSujeto() {
		return (Long) getId();
	}

	@Override
	protected GruposSujetos createInstance() {
		GruposSujetos gruposSujetos = new GruposSujetos();
		return gruposSujetos;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Sujetos sujetos = sujetosHome.getDefinedInstance();
		if (sujetos != null) {
			getInstance().setSujetos(sujetos);
		}
		GruposEvaluacion gruposEvaluacion = gruposEvaluacionHome
				.getDefinedInstance();
		if (gruposEvaluacion != null) {
			getInstance().setGruposEvaluacion(gruposEvaluacion);
		}
	}

	public boolean isWired() {
		if (getInstance().getSujetos() == null)
			return false;
		if (getInstance().getGruposEvaluacion() == null)
			return false;
		return true;
	}

	public GruposSujetos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
