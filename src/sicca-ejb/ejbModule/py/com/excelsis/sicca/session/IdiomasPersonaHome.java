package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.IdiomasPersona;

@Name("idiomasPersonaHome")
public class IdiomasPersonaHome extends EntityHome<IdiomasPersona> {

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setIdiomasPersonaIdIdiomaPersona(Long id) {
		setId(id);
	}

	public Long getIdiomasPersonaIdIdiomaPersona() {
		return (Long) getId();
	}

	@Override
	protected IdiomasPersona createInstance() {
		IdiomasPersona idiomasPersona = new IdiomasPersona();
		return idiomasPersona;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public IdiomasPersona getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
