package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.PresentAclaracDoc;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

@Name("presentAclaracDocHome")
public class PresentAclaracDocHome extends EntityHome<PresentAclaracDoc> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setPresentAclaracDocIdPresentAclaracDoc(Long id) {
		setId(id);
	}

	public Long getPresentAclaracDocIdPresentAclaracDoc() {
		return (Long) getId();
	}

	@Override
	protected PresentAclaracDoc createInstance() {
		PresentAclaracDoc presentAclaracDoc = new PresentAclaracDoc();
		return presentAclaracDoc;
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

	public PresentAclaracDoc getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
