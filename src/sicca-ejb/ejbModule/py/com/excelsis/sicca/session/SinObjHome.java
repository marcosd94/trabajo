package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.SinObj;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("sinObjHome")
public class SinObjHome extends EntityHome<SinObj> {

	public void setSinObjIdObj(Integer id) {
		setId(id);
	}

	public Integer getSinObjIdObj() {
		return (Integer) getId();
	}

	@Override
	protected SinObj createInstance() {
		SinObj sinObj = new SinObj();
		return sinObj;
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

	public SinObj getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
