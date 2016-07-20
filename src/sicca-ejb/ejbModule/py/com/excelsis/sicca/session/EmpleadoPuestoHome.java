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
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("empleadoPuestoHome")
public class EmpleadoPuestoHome extends EntityHome<EmpleadoPuesto> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected EmpleadoPuesto loadInstance() {
		EmpleadoPuesto o = super.loadInstance();
		this.idPlantaCargoDet = o.getPlantaCargoDet().getIdPlantaCargoDet();
		//this.idEmpleado = o.getEmpleado().getIdEmpleado();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idPlantaCargoDet;
	private Long idEmpleado;
	public static final String CONTEXT_VAR_NAME = "empleadoPuestos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EmpleadoPuesto> getEmpleadoPuestos(){
		try{
			return getEntityManager().createQuery(" select o from " + EmpleadoPuesto.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<EmpleadoPuesto>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEmpleadoPuestoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
//		for(EmpleadoPuesto o : getEmpleadoPuestos())
//			si.add(new SelectItem(o.getIdEmpleadoPuesto(),"" + o.getActual()));
		return si;
	}

	public void setEmpleadoPuestoIdEmpleadoPuesto(Long id) {
		setId(id);
	}

	public Long getEmpleadoPuestoIdEmpleadoPuesto() {
		return (Long) getId();
	}

	@Override
	protected EmpleadoPuesto createInstance() {
		EmpleadoPuesto empleadoPuesto = new EmpleadoPuesto();
		return empleadoPuesto;
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

	public EmpleadoPuesto getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setPlantaCargoDet(getEntityManager().find(PlantaCargoDet.class,this.idPlantaCargoDet));
		//getInstance().setEmpleado(getEntityManager().find(Empleado.class,this.idEmpleado));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setPlantaCargoDet(getEntityManager().find(PlantaCargoDet.class,this.idPlantaCargoDet));
		//getInstance().setEmpleado(getEntityManager().find(Empleado.class,this.idEmpleado));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Long getIdPlantaCargoDet(){
		return this.idPlantaCargoDet;
	}
	
	public void setIdPlantaCargoDet(Long idPlantaCargoDet){
		this.idPlantaCargoDet = idPlantaCargoDet;
	}
	
	public Long getIdEmpleado(){
		return this.idEmpleado;
	}
	
	public void setIdEmpleado(Long idEmpleado){
		this.idEmpleado = idEmpleado;
	}
	
}
