package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.EstudiosRealizados;

@Name("estudiosRealizadosHome")
public class EstudiosRealizadosHome extends EntityHome<EstudiosRealizados> {

	@In(create = true)
	InstitucionEducativaHome institucionEducativaHome;
	

	public void setEstudiosRealizadosIdEstudiosRealizados(Long id) {
		setId(id);
	}

	public Long getEstudiosRealizadosIdEstudiosRealizados() {
		return (Long) getId();
	}

	@Override
	protected EstudiosRealizados createInstance() {
		EstudiosRealizados estudiosRealizados = new EstudiosRealizados();
		return estudiosRealizados;
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
		if (getInstance().getInstitucionEducativa() == null)
			return false;
		return true;
	}

	public EstudiosRealizados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
