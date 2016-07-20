package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("listaDetHome")
public class ListaDetHome extends EntityHome<ListaDet> {

	@In(create = true)
	ListaCabHome listaCabHome;
	@In(create = true)
	PostulacionCapHome postulacionCapHome;

	public void setListaDetIdListaDet(Long id) {
		setId(id);
	}

	public Long getListaDetIdListaDet() {
		return (Long) getId();
	}

	@Override
	protected ListaDet createInstance() {
		ListaDet listaDet = new ListaDet();
		return listaDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ListaCab listaCab = listaCabHome.getDefinedInstance();
		if (listaCab != null) {
			getInstance().setListaCab(listaCab);
		}
		PostulacionCap postulacionCap = postulacionCapHome.getDefinedInstance();
		if (postulacionCap != null) {
			getInstance().setPostulacionCap(postulacionCap);
		}
	}

	public boolean isWired() {
		if (getInstance().getListaCab() == null)
			return false;
		if (getInstance().getPostulacionCap() == null)
			return false;
		return true;
	}

	public ListaDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
