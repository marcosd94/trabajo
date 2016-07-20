package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Documentos;

@Name("documentosHome")
public class DocumentosHome extends EntityHome<Documentos> {

	public void setDocumentosIdDocumento(Long id) {
		setId(id);
	}

	public Long getDocumentosIdDocumento() {
		return (Long) getId();
	}

	@Override
	protected Documentos createInstance() {
		Documentos documentos = new Documentos();
		return documentos;
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

	public Documentos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
