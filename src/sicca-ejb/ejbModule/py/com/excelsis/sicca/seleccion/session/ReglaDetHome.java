package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("reglaDetHome")
public class ReglaDetHome extends EntityHome<ReglaDet> {

	@In(create = true)
	ReglaCabHome reglaCabHome;

	public void setReglaDetIdReglaDet(Long id) {
		setId(id);
	}

	public Long getReglaDetIdReglaDet() {
		return (Long) getId();
	}

	@Override
	protected ReglaDet createInstance() {
		ReglaDet reglaDet = new ReglaDet();
		return reglaDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ReglaCab reglaCab = reglaCabHome.getDefinedInstance();
		if (reglaCab != null) {
			getInstance().setReglaCab(reglaCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getReglaCab() == null)
			return false;
		return true;
	}

	public ReglaDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
