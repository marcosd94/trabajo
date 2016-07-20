package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.CptHome;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("homologacionPerfilMatrizHome")
public class HomologacionPerfilMatrizHome extends
		EntityHome<HomologacionPerfilMatriz> {

	@In(create = true)
	CptHome cptHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setHomologacionPerfilMatrizIdHomologacion(Long id) {
		setId(id);
	}

	public Long getHomologacionPerfilMatrizIdHomologacion() {
		return (Long) getId();
	}

	@Override
	protected HomologacionPerfilMatriz createInstance() {
		HomologacionPerfilMatriz homologacionPerfilMatriz = new HomologacionPerfilMatriz();
		return homologacionPerfilMatriz;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Cpt cpt = cptHome.getDefinedInstance();
		if (cpt != null) {
			getInstance().setCpt(cpt);
		}
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
	}

	public boolean isWired() {
		if (getInstance().getCpt() == null)
			return false;
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		return true;
	}

	public HomologacionPerfilMatriz getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<HomologacionPerfilMatrizDet> getHomologacionPerfilMatrizDets() {
		return getInstance() == null ? null
				: new ArrayList<HomologacionPerfilMatrizDet>(getInstance()
						.getHomologacionPerfilMatrizDets());
	}

}
