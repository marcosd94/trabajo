package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.seguridad.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.util.Arrays;
import java.util.List;

@Name("usuarioRolList")
public class UsuarioRolList extends EntityQuery<UsuarioRol> {

	private static final String EJBQL = "select usuarioRol from UsuarioRol usuarioRol";

	private static final String[] RESTRICTIONS = {
		"usuarioRol.usuario.idUsuario = #{usuarioRolList.usuario.idUsuario}",
	};
	
	private UsuarioRol usuarioRol = new UsuarioRol();
	private Usuario usuario = new Usuario();
	
	public UsuarioRolList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public List<UsuarioRol> listByUser(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder("usuarioRol.rol.descripcion");
		return getResultList();
	}

//	GETTERS Y SETTERS
	public UsuarioRol getUsuarioRol() {
		return usuarioRol;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	
}
