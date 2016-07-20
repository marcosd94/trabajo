package py.com.excelsis.sicca.general.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

import java.util.ArrayList;
import java.util.Date;
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

@Name("cabValidacionHome")
public class CabValidacionHome extends EntityHome<CabValidacion> {
	@In(required = false)
	Usuario usuarioLogueado;
	@In
	StatusMessages statusMessages;
	public static final String CONTEXT_VAR_NAME = "cabValidaciones";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<CabValidacion> getCabValidacions(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + CabValidacion.class.getName() + " o " +
					"WHERE o.activo = true ORDER BY o.nombreGrupoValidacion").getResultList();
		}catch(Exception ex){
			return new Vector<CabValidacion>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getCabValidacionSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(CabValidacion o : getCabValidacions())
			si.add(new SelectItem(o.getIdCabValidacion(),"" + o.getNombreGrupoValidacion()));
		return si;
	}

	public void setCabValidacionIdCabValidacion(Long id) {
		setId(id);
	}

	public Long getCabValidacionIdCabValidacion() {
		return (Long) getId();
	}

	@Override
	protected CabValidacion createInstance() {
		CabValidacion cabValidacion = new CabValidacion();
		return cabValidacion;
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

	public CabValidacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DetValidacion> getDetValidacions() {
		return getInstance() == null ? null : new ArrayList<DetValidacion>(
				getInstance().getDetValidacions());
	}
	
	@Override
	public String persist() {
		
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}


}
