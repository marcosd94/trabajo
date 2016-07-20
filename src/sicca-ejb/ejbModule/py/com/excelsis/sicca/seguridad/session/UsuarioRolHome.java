package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.seguridad.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("usuarioRolHome")
public class UsuarioRolHome extends EntityHome<UsuarioRol> {

	@In(create = true)
	RolHome rolHome;
	@In(create = true)
	UsuarioHome usuarioHome;

	public void setUsuarioRolIdUsuarioRol(Long id) {
		setId(id);
	}

	public Long getUsuarioRolIdUsuarioRol() {
		return (Long) getId();
	}

	@Override
	protected UsuarioRol createInstance() {
		UsuarioRol usuarioRol = new UsuarioRol();
		return usuarioRol;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Rol rol = rolHome.getDefinedInstance();
		if (rol != null) {
			getInstance().setRol(rol);
		}
		Usuario usuario = usuarioHome.getDefinedInstance();
		if (usuario != null) {
			getInstance().setUsuario(usuario);
		}
	}

	public boolean isWired() {
		if (getInstance().getRol() == null)
			return false;
		if (getInstance().getUsuario() == null)
			return false;
		return true;
	}

	public UsuarioRol getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
