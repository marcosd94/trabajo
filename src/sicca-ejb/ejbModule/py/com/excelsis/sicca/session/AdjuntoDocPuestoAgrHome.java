package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.AdjuntoDocPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("adjuntoDocPuestoAgrHome")
public class AdjuntoDocPuestoAgrHome extends EntityHome<AdjuntoDocPuestoAgr> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setAdjuntoDocPuestoAgrIdAdjuntoDocPuestoAgr(Long id) {
		setId(id);
	}

	public Long getAdjuntoDocPuestoAgrIdAdjuntoDocPuestoAgr() {
		return (Long) getId();
	}

	@Override
	protected AdjuntoDocPuestoAgr createInstance() {
		AdjuntoDocPuestoAgr adjuntoDocPuestoAgr = new AdjuntoDocPuestoAgr();
		return adjuntoDocPuestoAgr;
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

	public AdjuntoDocPuestoAgr getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
