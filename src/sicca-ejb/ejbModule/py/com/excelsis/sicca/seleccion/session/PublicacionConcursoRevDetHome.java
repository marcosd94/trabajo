package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("publicacionConcursoRevDetHome")
public class PublicacionConcursoRevDetHome extends
		EntityHome<PublicacionConcursoRevDet> {

	@In(create = true)
	PublicacionConcursoHome publicacionConcursoHome;

	public void setPublicacionConcursoRevDetIdPublicacionConcRevDet(Long id) {
		setId(id);
	}

	public Long getPublicacionConcursoRevDetIdPublicacionConcRevDet() {
		return (Long) getId();
	}

	@Override
	protected PublicacionConcursoRevDet createInstance() {
		PublicacionConcursoRevDet publicacionConcursoRevDet = new PublicacionConcursoRevDet();
		return publicacionConcursoRevDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PublicacionConcurso publicacionConcurso = publicacionConcursoHome
				.getDefinedInstance();
		if (publicacionConcurso != null) {
			getInstance().setPublicacionConcurso(publicacionConcurso);
		}
	}

	public boolean isWired() {
		if (getInstance().getPublicacionConcurso() == null)
			return false;
		return true;
	}

	public PublicacionConcursoRevDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
