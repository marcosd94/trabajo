package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.SinPry;

@Name("sinPryHome")
public class SinPryHome extends EntityHome<SinPry> {

	public void setSinPryIdPry(Integer id) {
		setId(id);
	}

	public Integer getSinPryIdPry() {
		return (Integer) getId();
	}

	@Override
	protected SinPry createInstance() {
		SinPry sinPry = new SinPry();
		return sinPry;
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

	public SinPry getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
