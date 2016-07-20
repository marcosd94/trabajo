package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.seguridad.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("rolList")
public class RolList extends EntityQuery<Rol> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<Rol> listaRoles;
	private static final String EJBQL = "select rol from Rol rol";
	
	private List<Long> listIdsRoles = new ArrayList<Long>();

	private static final String[] RESTRICTIONS = {
			"lower(rol.descripcion) like lower(concat('%', concat(#{rolList.rol.descripcion},'%')))",
			"lower(rol.usuMod) like lower(concat(#{rolList.rol.usuMod},'%'))",
			"lower(rol.usuAlta) like lower(concat(#{rolList.rol.usuAlta},'%'))",
			"rol.activo=#{rolList.estado.valor}", };

	private static final String[] RESTRICTIONS_NOT_IN = { "rol.idRol NOT IN (#{rolList.listIdsRoles})", };

	private static final String ORDER = "rol.descripcion";
	private Rol rol = new Rol();
	private Estado estado = Estado.ACTIVO;

	public RolList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
//		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Rol> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
//		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setEstado(estado);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Rol> limpiar() {
		rol = new Rol();
		estado = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
//		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public List<Rol> listRolesNotAssigned() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_NOT_IN));
//		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setEstado(estado);
		setOrder("rol.descripcion");
		return getResultList();
	}

	public Rol getRol() {
		return rol;
	}

	public List<Rol> obtenerListaRoles() {
		listaRoles = getResultList();
		return listaRoles;
	}

	public List<Rol> getListaRoles() {
		return listaRoles;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<Long> getListIdsRoles() {
		return listIdsRoles;
	}
	
	public void setListIdsRoles(List<Long> listIdsRoles) {
		this.listIdsRoles = listIdsRoles;
	}

	

}
