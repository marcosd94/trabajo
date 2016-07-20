package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("antecedenteGrupoHome")
public class AntecedenteGrupoHome extends EntityHome<AntecedenteGrupo> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setAntecedenteGrupoIdAntecedenteGrupo(Long id) {
		setId(id);
	}

	public Long getAntecedenteGrupoIdAntecedenteGrupo() {
		return (Long) getId();
	}

	@Override
	protected AntecedenteGrupo createInstance() {
		AntecedenteGrupo antecedenteGrupo = new AntecedenteGrupo();
		return antecedenteGrupo;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		return true;
	}

	public AntecedenteGrupo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
