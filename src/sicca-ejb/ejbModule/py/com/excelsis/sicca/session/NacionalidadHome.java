package py.com.excelsis.sicca.session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.PersistenceException;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Nacionalidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("nacionalidadHome")
public class NacionalidadHome extends EntityHome<Nacionalidad> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected Nacionalidad loadInstance() {
		Nacionalidad o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "nacionalidads";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Nacionalidad> getNacionalidads(){
		try{
			return getEntityManager().createQuery(" select o from " + Nacionalidad.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Nacionalidad>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getNacionalidadSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Nacionalidad o : getNacionalidads())
			si.add(new SelectItem(o.getIdNacionalidad(),"" + o.getDescripcion()));
		return si;
	}

	public void setNacionalidadIdNacionalidad(Long id) {
		setId(id);
	}

	public Long getNacionalidadIdNacionalidad() {
		return (Long) getId();
	}

	@Override
	protected Nacionalidad createInstance() {
		Nacionalidad nacionalidad = new Nacionalidad();
		return nacionalidad;
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

	public Nacionalidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
}
