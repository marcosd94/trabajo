package py.com.excelsis.sicca.seleccion.session.form;


import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.CronogramaConcCab;
import py.com.excelsis.sicca.entity.FechasGrupoPuesto;
import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.entity.SolicProrrogaDet;
import py.com.excelsis.sicca.seleccion.session.CronogramaConcCabHome;
import py.com.excelsis.sicca.seleccion.session.FechasGrupoPuestoHome;

@Name("solicProrrogaCabHome")
public class SolicProrrogaCabHome extends EntityHome<SolicProrrogaCab> {

	@In(create = true)
	CronogramaConcCabHome cronogramaConcCabHome;
	@In(create = true)
	FechasGrupoPuestoHome fechasGrupoPuestoHome;

	public void setSolicProrrogaCabIdSolicCab(Long id) {
		setId(id);
	}

	public Long getSolicProrrogaCabIdSolicCab() {
		return (Long) getId();
	}

	@Override
	protected SolicProrrogaCab createInstance() {
		SolicProrrogaCab solicProrrogaCab = new SolicProrrogaCab();
		return solicProrrogaCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		CronogramaConcCab cronogramaConcCab = cronogramaConcCabHome
				.getDefinedInstance();
		if (cronogramaConcCab != null) {
			getInstance().setCronogramaConcCab(cronogramaConcCab);
		}
		FechasGrupoPuesto fechasGrupoPuesto = fechasGrupoPuestoHome
				.getDefinedInstance();
		if (fechasGrupoPuesto != null) {
			getInstance().setFechasGrupoPuesto(fechasGrupoPuesto);
		}
	}

	public boolean isWired() {
		if (getInstance().getFechasGrupoPuesto() == null)
			return false;
		return true;
	}

	public SolicProrrogaCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<SolicProrrogaDet> getSolicProrrogaDets() {
		return getInstance() == null ? null : new ArrayList<SolicProrrogaDet>(
				getInstance().getSolicProrrogaDets());
	}

}
