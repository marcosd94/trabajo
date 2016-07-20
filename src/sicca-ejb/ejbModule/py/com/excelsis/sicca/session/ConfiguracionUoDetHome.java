package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("configuracionUoDetHome")
public class ConfiguracionUoDetHome extends EntityHome<ConfiguracionUoDet> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@Override
	protected ConfiguracionUoDet loadInstance() {
		ConfiguracionUoDet o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "configuracionUoDets";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ConfiguracionUoDet> getConfiguracionUoDets(){
		try{
			return getEntityManager().createQuery(" select o from " + ConfiguracionUoDet.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<ConfiguracionUoDet>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getConfiguracionUoDetSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(ConfiguracionUoDet o : getConfiguracionUoDets())
			si.add(new SelectItem(o.getIdConfiguracionUoDet(),"" + o.getDescripcionCorta()));
		return si;
	}

	public void setConfiguracionUoDetIdConfiguracionUoDet(Long id) {
		setId(id);
	}

	public Long getConfiguracionUoDetIdConfiguracionUoDet() {
		return (Long) getId();
	}

	@Override
	protected ConfiguracionUoDet createInstance() {
		ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
		return configuracionUoDet;
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

	public ConfiguracionUoDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		todoMayus();
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		todoMayus();
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	
//	METODOS PRIVADOS
	private void todoMayus(){
		getInstance().setDenominacionUnidad(getInstance().getDenominacionUnidad().trim().toUpperCase());
		getInstance().setDescripcionCorta(getInstance().getDescripcionCorta() != null ?
				getInstance().getDescripcionCorta().trim().toUpperCase() : null);
		getInstance().setDescripcionFinalidad(getInstance().getDescripcionFinalidad() != null ? 
				getInstance().getDescripcionFinalidad().trim().toUpperCase() : null);
		getInstance().setDireccion(getInstance().getDireccion() != null ? 
				getInstance().getDireccion().trim().toUpperCase() : null);
		getInstance().setTelefono(getInstance().getTelefono() != null ?
				getInstance().getTelefono().trim() : null);
	}
	
	//Public getters and setters if exists

	
}
