package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("comisionGrupoHome")
public class ComisionGrupoHome extends EntityHome<ComisionGrupo> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	ComisionSeleccionCabHome comisionSeleccionCabHome;

	public void setComisionGrupoIdComisionGrupo(Long id) {
		setId(id);
	}

	public Long getComisionGrupoIdComisionGrupo() {
		return (Long) getId();
	}

	@Override
	protected ComisionGrupo createInstance() {
		ComisionGrupo comisionGrupo = new ComisionGrupo();
		return comisionGrupo;
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
		ComisionSeleccionCab comisionSeleccionCab = comisionSeleccionCabHome
				.getDefinedInstance();
		if (comisionSeleccionCab != null) {
			getInstance().setComisionSeleccionCab(comisionSeleccionCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		if (getInstance().getComisionSeleccionCab() == null)
			return false;
		return true;
	}

	public ComisionGrupo getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
