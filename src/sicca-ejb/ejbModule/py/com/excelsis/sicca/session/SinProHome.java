package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.SinPro;

@Name("sinProHome")
public class SinProHome extends EntityHome<SinPro> {

	public void setSinProIdPro(Integer id) {
		setId(id);
	}

	public Integer getSinProIdPro() {
		return (Integer) getId();
	}

	@Override
	protected SinPro createInstance() {
		SinPro sinPro = new SinPro();
		return sinPro;
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

	public SinPro getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
