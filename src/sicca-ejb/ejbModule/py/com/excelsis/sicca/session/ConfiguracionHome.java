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

import py.com.excelsis.sicca.entity.Configuracion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("configuracionHome")
public class ConfiguracionHome extends EntityHome<Configuracion> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected Configuracion loadInstance() {
		Configuracion o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "configuracions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Configuracion> getConfiguracions(){
		try{
			return getEntityManager().createQuery(" select o from " + Configuracion.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Configuracion>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getConfiguracionSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Configuracion o : getConfiguracions())
			si.add(new SelectItem(o.getIdConfiguracion(),"" + o.getDescIdentVac()));
		return si;
	}

	public void setConfiguracionIdConfiguracion(Integer id) {
		setId(id);
	}

	public Integer getConfiguracionIdConfiguracion() {
		return (Integer) getId();
	}

	@Override
	protected Configuracion createInstance() {
		Configuracion configuracion = new Configuracion();
		return configuracion;
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

	public Configuracion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
}
