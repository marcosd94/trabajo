package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("homologacionPerfilMatrizDetHome")
public class HomologacionPerfilMatrizDetHome extends
		EntityHome<HomologacionPerfilMatrizDet> {

	@In(create = true)
	HomologacionPerfilMatrizHome homologacionPerfilMatrizHome;

	public void setHomologacionPerfilMatrizDetIdHomologacionDet(Long id) {
		setId(id);
	}

	public Long getHomologacionPerfilMatrizDetIdHomologacionDet() {
		return (Long) getId();
	}

	@Override
	protected HomologacionPerfilMatrizDet createInstance() {
		HomologacionPerfilMatrizDet homologacionPerfilMatrizDet = new HomologacionPerfilMatrizDet();
		return homologacionPerfilMatrizDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		HomologacionPerfilMatriz homologacionPerfilMatriz = homologacionPerfilMatrizHome
				.getDefinedInstance();
		if (homologacionPerfilMatriz != null) {
			getInstance().setHomologacionPerfilMatriz(homologacionPerfilMatriz);
		}
	}

	public boolean isWired() {
		if (getInstance().getHomologacionPerfilMatriz() == null)
			return false;
		return true;
	}

	public HomologacionPerfilMatrizDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
