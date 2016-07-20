package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("publicacionConcursoDetHome")
public class PublicacionConcursoDetHome extends
		EntityHome<PublicacionConcursoDet> {

	@In(create = true)
	PublicacionConcursoCabHome publicacionConcursoCabHome;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setPublicacionConcursoDetIdPublicacionDet(Long id) {
		setId(id);
	}

	public Long getPublicacionConcursoDetIdPublicacionDet() {
		return (Long) getId();
	}

	@Override
	protected PublicacionConcursoDet createInstance() {
		PublicacionConcursoDet publicacionConcursoDet = new PublicacionConcursoDet();
		return publicacionConcursoDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PublicacionConcursoCab publicacionConcursoCab = publicacionConcursoCabHome
				.getDefinedInstance();
		if (publicacionConcursoCab != null) {
			getInstance().setPublicacionConcursoCab(publicacionConcursoCab);
		}
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getPublicacionConcursoCab() == null)
			return false;
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public PublicacionConcursoDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
