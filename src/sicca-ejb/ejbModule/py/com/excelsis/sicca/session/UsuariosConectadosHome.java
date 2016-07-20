package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.UsuariosConectados;

@Name("usuariosConectadosHome")
public class UsuariosConectadosHome extends EntityHome<UsuariosConectados> {

	public void setUsuariosConectadosIdUsuariosConectados(Long id) {
		setId(id);
	}

	public Long getUsuariosConectadosIdUsuariosConectados() {
		return (Long) getId();
	}

	@Override
	protected UsuariosConectados createInstance() {
		UsuariosConectados usuariosConectados = new UsuariosConectados();
		return usuariosConectados;
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

	public UsuariosConectados getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
