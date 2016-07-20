package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("datosGrupoPuestoHome")
public class DatosGrupoPuestoHome extends EntityHome<DatosGrupoPuesto> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setDatosGrupoPuestoIdDatosGrupoPuesto(Long id) {
		setId(id);
	}

	public Long getDatosGrupoPuestoIdDatosGrupoPuesto() {
		return (Long) getId();
	}

	@Override
	protected DatosGrupoPuesto createInstance() {
		DatosGrupoPuesto datosGrupoPuesto = new DatosGrupoPuesto();
		return datosGrupoPuesto;
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

	public DatosGrupoPuesto getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
