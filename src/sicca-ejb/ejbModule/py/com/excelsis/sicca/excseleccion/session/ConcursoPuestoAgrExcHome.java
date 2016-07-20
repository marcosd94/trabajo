package py.com.excelsis.sicca.excseleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;

@Name("concursoPuestoAgrExcHome")
public class ConcursoPuestoAgrExcHome extends EntityHome<ConcursoPuestoAgrExc> {

	public void setConcursoPuestoAgrExcIdConcursoPuestoAgrExc(Long id) {
		setId(id);
	}

	public Long getConcursoPuestoAgrExcIdConcursoPuestoAgrExc() {
		return (Long) getId();
	}

	@Override
	protected ConcursoPuestoAgrExc createInstance() {
		ConcursoPuestoAgrExc concursoPuestoAgrExc = new ConcursoPuestoAgrExc();
		return concursoPuestoAgrExc;
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

	public ConcursoPuestoAgrExc getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
