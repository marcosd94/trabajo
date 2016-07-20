package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.AdjuntoDocPortal;

@Name("adjuntoDocPortalHome")
public class AdjuntoDocPortalHome extends EntityHome<AdjuntoDocPortal> {

	public void setAdjuntoDocPortalIdAdjuntoDocPortal(Long id) {
		setId(id);
	}

	public Long getAdjuntoDocPortalIdAdjuntoDocPortal() {
		return (Long) getId();
	}

	@Override
	protected AdjuntoDocPortal createInstance() {
		AdjuntoDocPortal adjuntoDocPortal = new AdjuntoDocPortal();
		return adjuntoDocPortal;
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

	public AdjuntoDocPortal getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
