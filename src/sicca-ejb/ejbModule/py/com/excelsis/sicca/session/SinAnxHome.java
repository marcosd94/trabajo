package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.SinAnx;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("sinAnxHome")
public class SinAnxHome extends EntityHome<SinAnx> {

	public void setSinAnxIdAnx(Long id) {
		setId(id);
	}

	public Long getSinAnxIdAnx() {
		return (Long) getId();
	}

	@Override
	protected SinAnx createInstance() {
		SinAnx sinAnx = new SinAnx();
		return sinAnx;
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

	public SinAnx getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
