package py.com.excelsis.sicca.general.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("cabValidacionOeeHome")
public class CabValidacionOeeHome extends EntityHome<CabValidacionOee> {

	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	public static final String CONTEXT_VAR_NAME = "cabValidacionOees";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<CabValidacionOee> getCabValidacions(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + CabValidacionOee.class.getName() + " o " +
					"WHERE o.activo = true ORDER BY o.nombreGrupoValidacion").getResultList();
		}catch(Exception ex){
			return new Vector<CabValidacionOee>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getCabValidacionSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(CabValidacionOee o : getCabValidacions())
			si.add(new SelectItem(o.getIdCabValidacionOee(),o.getIdCabValidacionOee()+" - " + o.getNombreGrupoValidacion()));
		return si;
	}

	public void setCabValidacionOeeIdCabValidacionOee(Long id) {
		setId(id);
	}

	public Long getCabValidacionOeeIdCabValidacionOee() {
		return (Long) getId();
	}

	@Override
	protected CabValidacionOee createInstance() {
		CabValidacionOee cabValidacionOee = new CabValidacionOee();
		return cabValidacionOee;
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

	public CabValidacionOee getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DetValidacionOee> getDetValidacionOees() {
		return getInstance() == null ? null : new ArrayList<DetValidacionOee>(
				getInstance().getDetValidacionOees());
	}

}
