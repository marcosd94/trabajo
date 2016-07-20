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

import py.com.excelsis.sicca.entity.CategoriaCpt;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("categoriaCptHome")
public class CategoriaCptHome extends EntityHome<CategoriaCpt> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected CategoriaCpt loadInstance() {
		CategoriaCpt o = super.loadInstance();
		this.idCpt = o.getCpt().getIdCpt();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idCpt;
	public static final String CONTEXT_VAR_NAME = "categoriaCpts";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<CategoriaCpt> getCategoriaCpts(){
		try{
			return getEntityManager().createQuery(" select o from " + CategoriaCpt.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<CategoriaCpt>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getCategoriaCptSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(CategoriaCpt o : getCategoriaCpts())
			si.add(new SelectItem(o.getIdCategoriaCpt(),"" + o.getCategoria()));
		return si;
	}

	public void setCategoriaCptIdCategoriaCpt(Long id) {
		setId(id);
	}

	public Long getCategoriaCptIdCategoriaCpt() {
		return (Long) getId();
	}

	@Override
	protected CategoriaCpt createInstance() {
		CategoriaCpt categoriaCpt = new CategoriaCpt();
		return categoriaCpt;
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

	public CategoriaCpt getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setCpt(getEntityManager().find(Cpt.class,this.idCpt));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setCpt(getEntityManager().find(Cpt.class,this.idCpt));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Long getIdCpt(){
		return this.idCpt;
	}
	
	public void setIdCpt(Long idCpt){
		this.idCpt = idCpt;
	}
	
}
