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

import py.com.excelsis.sicca.entity.Asignacion;
import py.com.excelsis.sicca.entity.TblPuestoCategoria;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("asignacionHome")
public class AsignacionHome extends EntityHome<Asignacion> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected Asignacion loadInstance() {
		Asignacion o = super.loadInstance();
		this.idPuestoCategoria = o.getTblPuestoCategoria().getIdPuestoCategoria();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Integer idPuestoCategoria;
	public static final String CONTEXT_VAR_NAME = "asignacions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Asignacion> getAsignacions(){
		try{
			return getEntityManager().createQuery(" select o from " + Asignacion.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Asignacion>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getAsignacionSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Asignacion o : getAsignacions())
			si.add(new SelectItem(o.getIdAsignacion(),"" + o.getUsuAlta()));
		return si;
	}

	public void setAsignacionIdAsignacion(Long id) {
		setId(id);
	}

	public Long getAsignacionIdAsignacion() {
		return (Long) getId();
	}

	@Override
	protected Asignacion createInstance() {
		Asignacion asignacion = new Asignacion();
		return asignacion;
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

	public Asignacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setTblPuestoCategoria(getEntityManager().find(TblPuestoCategoria.class,this.idPuestoCategoria));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setTblPuestoCategoria(getEntityManager().find(TblPuestoCategoria.class,this.idPuestoCategoria));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Integer getIdPuestoCategoria(){
		return this.idPuestoCategoria;
	}
	
	public void setIdPuestoCategoria(Integer idPuestoCategoria){
		this.idPuestoCategoria = idPuestoCategoria;
	}
	
}
