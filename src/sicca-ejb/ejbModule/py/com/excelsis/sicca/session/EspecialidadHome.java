package py.com.excelsis.sicca.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.Especialidad;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.TituloAcademico;

@Name("especialidadHome")
public class EspecialidadHome extends EntityHome<Especialidad> {

	public void setEspecialidadIdEspecialidad(Long id) {
		setId(id);
	}

	public Long getEspecialidadIdEspecialidad() {
		return (Long) getId();
	}

	@Override
	protected Especialidad createInstance() {
		Especialidad especialidad = new Especialidad();
		return especialidad;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "especialidadSelec";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Especialidad> getEspecialidadSelec(){
		try{
			return getEntityManager().createQuery(" select o from " + Especialidad.class.getName() + " o where o.activo = true order by o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<Especialidad>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEspecialidadSelecSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Especialidad e : getEspecialidadSelec())
			si.add(new SelectItem(e.getIdEspecialidad(),"" + e.getDescripcion()));
		return si;
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

	public Especialidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	

}
