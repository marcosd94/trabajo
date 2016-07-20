package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;

@Name("procesoHome")
public class ProcesoHome extends EntityHome<Proceso> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8381016799886479035L;

	public void setProcesoIdProceso(Long id) {
		setId(id);
	}

	public Long getProcesoIdProceso() {
		return (Long) getId();
	}

	@Override
	protected Proceso createInstance() {
		Proceso proceso = new Proceso();
		return proceso;
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

	public Proceso getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ActividadProceso> getActividadProcesos() {
		return getInstance() == null ? null : new ArrayList<ActividadProceso>(
				getInstance().getActividadProcesos());
	}
	
	public static final String CONTEXT_VAR_NAME = "proceso";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Proceso> getProcesos(){
		try{
			List<Proceso> lista = getEntityManager().createQuery(" SELECT o FROM " + Proceso.class.getName() 
					+ " o WHERE o.activo = true ORDER BY o.descripcion").getResultList();
			
			return lista;
		}catch(Exception ex){
			return new Vector<Proceso>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getProcesoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Proceso o : getProcesos())
			si.add(new SelectItem(o.getIdProceso(),"" + o.getDescripcion()));
		return si;
	}
	
}
