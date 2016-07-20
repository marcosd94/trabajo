package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("listaHome")
public class ListaHome extends EntityHome<Lista> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setListaIdLista(Long id) {
		setId(id);
	}

	public Long getListaIdLista() {
		return (Long) getId();
	}

	@Override
	protected Lista createInstance() {
		Lista lista = new Lista();
		return lista;
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

	public Lista getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
