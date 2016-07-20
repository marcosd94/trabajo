package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("detDescripcionContFuncionalHome")
public class DetDescripcionContFuncionalHome extends
		EntityHome<DetDescripcionContFuncional> {

	@In(create = true)
	DetContenidoFuncionalHome detContenidoFuncionalHome;

	public void setDetDescripcionContFuncionalIdDetDescripcionContFuncional(
			Long id) {
		setId(id);
	}

	public Long getDetDescripcionContFuncionalIdDetDescripcionContFuncional() {
		return (Long) getId();
	}

	@Override
	protected DetDescripcionContFuncional createInstance() {
		DetDescripcionContFuncional detDescripcionContFuncional = new DetDescripcionContFuncional();
		return detDescripcionContFuncional;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DetContenidoFuncional detContenidoFuncional = detContenidoFuncionalHome
				.getDefinedInstance();
		if (detContenidoFuncional != null) {
			getInstance().setDetContenidoFuncional(detContenidoFuncional);
		}
	}

	public boolean isWired() {
		if (getInstance().getDetContenidoFuncional() == null)
			return false;
		return true;
	}

	public DetDescripcionContFuncional getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
