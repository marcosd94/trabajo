package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.AdjuntoDocConcurso;
import py.com.excelsis.sicca.entity.Concurso;

@Name("adjuntoDocConcursoHome")
public class AdjuntoDocConcursoHome extends EntityHome<AdjuntoDocConcurso> {

	@In(create = true)
	ConcursoHome concursoHome;

	public void setAdjuntoDocConcursoIdAdjuntoDocConcurso(Long id) {
		setId(id);
	}

	public Long getAdjuntoDocConcursoIdAdjuntoDocConcurso() {
		return (Long) getId();
	}

	@Override
	protected AdjuntoDocConcurso createInstance() {
		AdjuntoDocConcurso adjuntoDocConcurso = new AdjuntoDocConcurso();
		return adjuntoDocConcurso;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Concurso concurso = concursoHome.getDefinedInstance();
		if (concurso != null) {
			getInstance().setConcurso(concurso);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcurso() == null)
			return false;
		return true;
	}

	public AdjuntoDocConcurso getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
