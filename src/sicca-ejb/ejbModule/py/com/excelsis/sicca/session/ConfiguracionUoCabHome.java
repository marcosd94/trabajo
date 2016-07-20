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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("configuracionUoCabHome")
public class ConfiguracionUoCabHome extends EntityHome<ConfiguracionUoCab> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected ConfiguracionUoCab loadInstance() {
		ConfiguracionUoCab o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "configuracionUoCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ConfiguracionUoCab> getConfiguracionUoCabs(){
		try{
			return getEntityManager().createQuery(" select o from " + ConfiguracionUoCab.class.getName() + " o where activo = TRUE order by denominacionUnidad ").getResultList();
		}catch(Exception ex){
			return new Vector<ConfiguracionUoCab>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getConfiguracionUoCabSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(ConfiguracionUoCab o : getConfiguracionUoCabs())
			si.add(new SelectItem(o.getIdConfiguracionUo(),"" + o.getDenominacionUnidad()));
		return si;
	}
	
	

	public void setConfiguracionUoCabIdConfiguracionUo(Long id) {
		setId(id);
	}

	public Long getConfiguracionUoCabIdConfiguracionUo() {
		return (Long) getId();
	}

	@Override
	protected ConfiguracionUoCab createInstance() {
		ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
		return configuracionUoCab;
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

	public ConfiguracionUoCab getDefinedInstance() {
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
