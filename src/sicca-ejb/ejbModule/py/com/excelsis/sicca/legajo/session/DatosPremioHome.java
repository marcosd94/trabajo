package py.com.excelsis.sicca.legajo.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.DatosPremio;

@Name("datosPremioHome")
public class DatosPremioHome extends EntityHome<DatosPremio> {

	public void setDatosPremioIdDatosPremio(Long id) {
		setId(id);
	}

	public Long getDatosPremioIdDatosPremio() {
		return (Long) getId();
	}

	@Override
	protected DatosPremio createInstance() {
		DatosPremio datosPremio = new DatosPremio();
		return datosPremio;
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

	public DatosPremio getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
