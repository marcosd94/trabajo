package py.com.excelsis.sicca.seguridad.session;


import py.com.excelsis.sicca.seguridad.entity.Rol;
import py.com.excelsis.sicca.seguridad.entity.RolFuncion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.seguridad.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Scope(ScopeType.CONVERSATION)
@Name("rolHome")
public class RolHome extends EntityHome<Rol> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7333209930336822562L;

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "roles";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	


	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Rol> getRoles(){
		try{
			return getEntityManager().createQuery(" select o from " + Rol.class.getName() + " o where activo = TRUE order by descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<Rol>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getRolSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"SELECCIONAR..."));
		for(Rol o : getRoles())
			si.add(new SelectItem(o.getIdRol(),"" + o.getDescripcion()));
		return si;
	}
	
	
	public List<Rol> getRolesCOMS(){
		try{
			return getEntityManager().createQuery(" select o from " + Rol.class.getName() + " o where o.activo = TRUE and o.tipo like 'COMS' order by descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<Rol>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "ByComsSelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getRolSelectCOMSItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"SELECCIONAR..."));
		for(Rol o : getRolesCOMS())
			si.add(new SelectItem(o.getIdRol(),"" + o.getDescripcion()));
		return si;
	}

	public void setRolIdRol(Long id) {
		setId(id);
	}

	public Long getRolIdRol() {
		return (Long) getId();
	}

	@Override
	protected Rol createInstance() {
		Rol rol = new Rol();
		return rol;
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

	public Rol getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<RolFuncion> getRolFuncions() {
		return getInstance() == null ? null : new ArrayList<RolFuncion>(
				getInstance().getRolFuncions());
	}

	public List<UsuarioRol> getUsuarioRols() {
		return getInstance() == null ? null : new ArrayList<UsuarioRol>(
				getInstance().getUsuarioRols());
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM Rol o WHERE lower(o.descripcion) = '"+getInstance().getDescripcion().trim().toLowerCase()+"' ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idRol != " + getInstance().getIdRol().longValue();
		}
		List<Rol> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	@Override
    public void setInstance(Rol instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }


}
