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

import py.com.excelsis.sicca.entity.Empleado;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("empleadoHome")
public class EmpleadoHome extends EntityHome<Empleado> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected Empleado loadInstance() {
		Empleado o = super.loadInstance();
		this.idPersona = o.getPersona().getIdPersona();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idPersona;
	public static final String CONTEXT_VAR_NAME = "empleados";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Empleado> getEmpleados(){
		try{
			return getEntityManager().createQuery(" select o from " + Empleado.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Empleado>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEmpleadoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Empleado o : getEmpleados())
			si.add(new SelectItem(o.getIdEmpleado(),"" + o.getTipoEmpleado()));
		return si;
	}

	public void setEmpleadoIdEmpleado(Long id) {
		setId(id);
	}

	public Long getEmpleadoIdEmpleado() {
		return (Long) getId();
	}

	@Override
	protected Empleado createInstance() {
		Empleado empleado = new Empleado();
		return empleado;
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

	public Empleado getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setPersona(getEntityManager().find(Persona.class,this.idPersona));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setPersona(getEntityManager().find(Persona.class,this.idPersona));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Long getIdPersona(){
		return this.idPersona;
	}
	
	public void setIdPersona(Long idPersona){
		this.idPersona = idPersona;
	}
	
}
