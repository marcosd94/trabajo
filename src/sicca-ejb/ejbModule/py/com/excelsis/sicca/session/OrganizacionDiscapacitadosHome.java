package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("organizacionDiscapacitadosHome")
public class OrganizacionDiscapacitadosHome extends
		EntityHome<OrganizacionDiscapacitados> {

	@In(create = true)
	CiudadHome ciudadHome;

	public void setOrganizacionDiscapacitadosIdOrganizacion(Long id) {
		setId(id);
	}

	public Long getOrganizacionDiscapacitadosIdOrganizacion() {
		return (Long) getId();
	}

	@Override
	protected OrganizacionDiscapacitados createInstance() {
		OrganizacionDiscapacitados organizacionDiscapacitados = new OrganizacionDiscapacitados();
		return organizacionDiscapacitados;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Ciudad ciudad = ciudadHome.getDefinedInstance();
		if (ciudad != null) {
			getInstance().setCiudad(ciudad);
		}
	}

	public boolean isWired() {
		if (getInstance().getCiudad() == null)
			return false;
		return true;
	}

	public OrganizacionDiscapacitados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	

}
