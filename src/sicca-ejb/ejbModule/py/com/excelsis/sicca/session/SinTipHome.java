package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.SinTip;

@Name("sinTipHome")
public class SinTipHome extends EntityHome<SinTip> {

	public void setSinTipIdTip(Integer id) {
		setId(id);
	}

	public Integer getSinTipIdTip() {
		return (Integer) getId();
	}

	@Override
	protected SinTip createInstance() {
		SinTip sinTip = new SinTip();
		return sinTip;
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

	public SinTip getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
