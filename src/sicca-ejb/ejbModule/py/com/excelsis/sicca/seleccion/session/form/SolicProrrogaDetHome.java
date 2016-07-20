package py.com.excelsis.sicca.seleccion.session.form;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;

@Name("solicProrrogaDetHome")
public class SolicProrrogaDetHome extends EntityHome<SolicProrrogaDet> {

	@In(create = true)
	SolicProrrogaCabHome solicProrrogaCabHome;

	public void setSolicProrrogaDetIdSolicDet(Long id) {
		setId(id);
	}

	public Long getSolicProrrogaDetIdSolicDet() {
		return (Long) getId();
	}

	@Override
	protected SolicProrrogaDet createInstance() {
		SolicProrrogaDet solicProrrogaDet = new SolicProrrogaDet();
		return solicProrrogaDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SolicProrrogaCab solicProrrogaCab = solicProrrogaCabHome
				.getDefinedInstance();
		if (solicProrrogaCab != null) {
			getInstance().setSolicProrrogaCab(solicProrrogaCab);
		}
	}

	public boolean isWired() {
//		if (getInstance().getSolicProrrogaCab() == null)
//			return false;
		return true;
	}

	public SolicProrrogaDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
