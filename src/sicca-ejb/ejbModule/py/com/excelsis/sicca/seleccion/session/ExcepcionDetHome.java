package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionDet;

@Name("excepcionDetHome")
public class ExcepcionDetHome extends EntityHome<ExcepcionDet> {

	@In(create = true)
	ExcepcionHome excepcionHome;

	public void setExcepcionDetIdExcepcionDet(Long id) {
		setId(id);
	}

	public Long getExcepcionDetIdExcepcionDet() {
		return (Long) getId();
	}

	@Override
	protected ExcepcionDet createInstance() {
		ExcepcionDet excepcionDet = new ExcepcionDet();
		return excepcionDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Excepcion excepcion = excepcionHome.getDefinedInstance();
		if (excepcion != null) {
			getInstance().setExcepcion(excepcion);
		}
	}

	public boolean isWired() {
		if (getInstance().getExcepcion() == null)
			return false;
		return true;
	}

	public ExcepcionDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
