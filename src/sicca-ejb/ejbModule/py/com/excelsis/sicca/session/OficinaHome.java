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

import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("oficinaHome")
public class OficinaHome extends EntityHome<Oficina> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected Oficina loadInstance() {
		Oficina o = super.loadInstance();
		this.idCiudad = o.getCiudad().getIdCiudad();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idCiudad;
	public static final String CONTEXT_VAR_NAME = "oficinas";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Oficina> getOficinas(){
		try{
			return getEntityManager().createQuery(" select o from " + Oficina.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Oficina>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getOficinaSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Oficina o : getOficinas())
			si.add(new SelectItem(o.getIdOficina(),"" + o.getDescripcion()));
		return si;
	}

	public void setOficinaIdOficina(Long id) {
		setId(id);
	}

	public Long getOficinaIdOficina() {
		return (Long) getId();
	}

	@Override
	protected Oficina createInstance() {
		Oficina oficina = new Oficina();
		return oficina;
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

	public Oficina getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setCiudad(getEntityManager().find(Ciudad.class,this.idCiudad));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setCiudad(getEntityManager().find(Ciudad.class,this.idCiudad));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Long getIdCiudad(){
		return this.idCiudad;
	}
	
	public void setIdCiudad(Long idCiudad){
		this.idCiudad = idCiudad;
	}
	
}
