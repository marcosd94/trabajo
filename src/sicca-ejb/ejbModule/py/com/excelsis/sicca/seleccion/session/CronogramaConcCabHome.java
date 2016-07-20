package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("cronogramaConcCabHome")
public class CronogramaConcCabHome extends EntityHome<CronogramaConcCab> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setCronogramaConcCabIdCronogramaConcCab(Long id) {
		setId(id);
	}

	public Long getCronogramaConcCabIdCronogramaConcCab() {
		return (Long) getId();
	}

	@Override
	protected CronogramaConcCab createInstance() {
		CronogramaConcCab cronogramaConcCab = new CronogramaConcCab();
		return cronogramaConcCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		return true;
	}

	public CronogramaConcCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<CronogramaConcDet> getCronogramaConcDets() {
		return getInstance() == null ? null : new ArrayList<CronogramaConcDet>(
				getInstance().getCronogramaConcDets());
	}

}
