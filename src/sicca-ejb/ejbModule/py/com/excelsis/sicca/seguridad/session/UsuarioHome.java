package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.seguridad.entity.UsuarioRol;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.seguridad.entity.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("usuarioHome")
public class UsuarioHome extends EntityHome<Usuario> {
	
	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "usuario";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Usuario> getUsuarios(){
		try{
			return getEntityManager().createQuery(" select o from " + Usuario.class.getName() + " o where activo = TRUE order by codigoUsuario").getResultList();
		}catch(Exception ex){
			return new Vector<Usuario>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getUsuarioSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"SELECCIONAR.."));
		for(Usuario o : getUsuarios())
			si.add(new SelectItem(o.getIdUsuario(),"" + o.getCodigoUsuario()));
		return si;
	}

	public void setUsuarioIdUsuario(Long id) {
		setId(id);
	}

	public Long getUsuarioIdUsuario() {
		return (Long) getId();
	}

	@Override
	protected Usuario createInstance() {
		Usuario usuario = new Usuario();
		return usuario;
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

	public Usuario getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<UsuarioRol> getUsuarioRols() {
		return getInstance() == null ? null : new ArrayList<UsuarioRol>(
				getInstance().getUsuarioRols());
	}
	
	@Override
	public String persist() {
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public String save()  
    {
		if(!checkData())
			return null;
		
		getInstance().setCodigoUsuario(getInstance().getCodigoUsuario().toUpperCase().trim());

        if(isIdDefined() || getInstance().getIdUsuario() != null)
        {
            getInstance().setFechaMod(Calendar.getInstance().getTime());
            getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
        	return update();
        }
        
        getInstance().setFechaAlta(Calendar.getInstance().getTime());
        getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());
        return persist();
    }
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM Usuario o WHERE lower(o.codigoUsuario) = '"+getInstance().getCodigoUsuario().trim().toLowerCase()+"' ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idUsuario != " + getInstance().getIdUsuario().longValue();
		}
		List<Usuario> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = getInstance().getIdUsuario() != null ? "update" : "persist";
		if(getInstance().getCodigoUsuario().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}

}
