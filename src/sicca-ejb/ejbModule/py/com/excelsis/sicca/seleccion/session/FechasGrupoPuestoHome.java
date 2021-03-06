package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("fechasGrupoPuestoHome")
public class FechasGrupoPuestoHome extends EntityHome<FechasGrupoPuesto> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setFechasGrupoPuestoIdFechas(Long id) {
		setId(id);
	}

	public Long getFechasGrupoPuestoIdFechas() {
		return (Long) getId();
	}

	@Override
	protected FechasGrupoPuesto createInstance() {
		FechasGrupoPuesto fechasGrupoPuesto = new FechasGrupoPuesto();
		return fechasGrupoPuesto;
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

	public FechasGrupoPuesto getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
