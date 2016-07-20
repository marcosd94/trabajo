package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("listaCabHome")
public class ListaCabHome extends EntityHome<ListaCab> {

	@In(create = true)
	CapacitacionesHome capacitacionesHome;

	public void setListaCabIdLista(Long id) {
		setId(id);
	}

	public Long getListaCabIdLista() {
		return (Long) getId();
	}

	@Override
	protected ListaCab createInstance() {
		ListaCab listaCab = new ListaCab();
		return listaCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Capacitaciones capacitaciones = capacitacionesHome.getDefinedInstance();
		if (capacitaciones != null) {
			getInstance().setCapacitaciones(capacitaciones);
		}
	}

	public boolean isWired() {
		if (getInstance().getCapacitaciones() == null)
			return false;
		return true;
	}

	public ListaCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ListaDet> getListaDets() {
		return getInstance() == null ? null : new ArrayList<ListaDet>(
				getInstance().getListaDets());
	}

}
