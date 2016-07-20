package py.com.excelsis.sicca.juridicos.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Excepcionados;

@Name("excepcionadosHome")
public class ExcepcionadosHome extends EntityHome<Excepcionados> {

	public void setExcepcionadosIdExcepcionado(Long id) {
		setId(id);
	}

	public Long getExcepcionadosIdExcepcionado() {
		return (Long) getId();
	}

	@Override
	protected Excepcionados createInstance() {
		Excepcionados excepcionados = new Excepcionados();
		return excepcionados;
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

	public Excepcionados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
