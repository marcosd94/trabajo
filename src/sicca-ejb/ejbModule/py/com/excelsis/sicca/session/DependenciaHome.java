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

import py.com.excelsis.sicca.entity.Dependencia;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("dependenciaHome")
public class DependenciaHome extends EntityHome<Dependencia> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected Dependencia loadInstance() {
		Dependencia o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "dependencias";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Dependencia> getDependencias(){
		try{
			return getEntityManager().createQuery(" select o from " + Dependencia.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Dependencia>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getDependenciaSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Dependencia o : getDependencias())
			si.add(new SelectItem(o.getIdDependencia(),"" + o.getNombre()));
		return si;
	}

	public void setDependenciaIdDependencia(Long id) {
		setId(id);
	}

	public Long getDependenciaIdDependencia() {
		return (Long) getId();
	}

	@Override
	protected Dependencia createInstance() {
		Dependencia dependencia = new Dependencia();
		return dependencia;
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

	public Dependencia getDefinedInstance() {
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
