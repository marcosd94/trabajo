package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("reclamoSugerenciaHome")
public class ReclamoSugerenciaHome extends EntityHome<ReclamoSugerencia> {

	@In(required = false)
	Usuario usuarioLogueado;
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "reclamosSugerencias";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ReclamoSugerencia> getReclamosSugerencias(){
		try{
			return getEntityManager().createQuery(" select o from " + ReclamoSugerencia.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<ReclamoSugerencia>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getReclamosSugerenciasSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(ReclamoSugerencia o : getReclamosSugerencias())
			si.add(new SelectItem(o.getIdReclamoSugerencia(),"" + o.getDescripcion()));
		return si;
	}
	
	@Override
	public String persist() {
		
		getInstance().setFechaAlta(new Date());
		if (usuarioLogueado != null)
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());
		else
			getInstance().setUsuAlta("PORTAL");
		
		getStatusMessages().clear();
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		getInstance().setFechaMod(new Date());
		if (usuarioLogueado != null)
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		else
			getInstance().setUsuAlta("PORTAL");
			
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public void setReclamoSugerenciaIdReclamoSugerencia(Long id) {
		setId(id);
	}

	public Long getReclamoSugerenciaIdReclamoSugerencia() {
		return (Long) getId();
	}

	@Override
	protected ReclamoSugerencia createInstance() {
		ReclamoSugerencia reclamoSugerencia = new ReclamoSugerencia();
		return reclamoSugerencia;
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

	public ReclamoSugerencia getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	
	@Override
    public void setInstance(ReclamoSugerencia instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
}
