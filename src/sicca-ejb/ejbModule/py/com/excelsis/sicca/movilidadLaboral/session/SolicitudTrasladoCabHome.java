package py.com.excelsis.sicca.movilidadLaboral.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("solicitudTrasladoCabHome")
public class SolicitudTrasladoCabHome extends EntityHome<SolicitudTrasladoCab> {

	public void setSolicitudTrasladoCabIdSolicitudTrasladoCab(Long id) {
		setId(id);
	}

	public Long getSolicitudTrasladoCabIdSolicitudTrasladoCab() {
		return (Long) getId();
	}

	@Override
	protected SolicitudTrasladoCab createInstance() {
		SolicitudTrasladoCab solicitudTrasladoCab = new SolicitudTrasladoCab();
		return solicitudTrasladoCab;
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

	public SolicitudTrasladoCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
