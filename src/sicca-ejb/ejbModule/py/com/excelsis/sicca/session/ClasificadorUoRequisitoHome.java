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

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.ClasificadorUoRequisito;
import py.com.excelsis.sicca.entity.RequisitoMinimoCuo;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("clasificadorUoRequisitoHome")
public class ClasificadorUoRequisitoHome extends EntityHome<ClasificadorUoRequisito> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected ClasificadorUoRequisito loadInstance() {
		ClasificadorUoRequisito o = super.loadInstance();
		this.idRequisitoMinimoCuo = o.getRequisitoMinimoCuo().getIdRequisitoMinimoCuo();
		this.idClasificadorUo = o.getClasificadorUo().getIdClasificadorUo();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idRequisitoMinimoCuo;
	private Long idClasificadorUo;
	public static final String CONTEXT_VAR_NAME = "clasificadorUoRequisitos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ClasificadorUoRequisito> getClasificadorUoRequisitos(){
		try{
			return getEntityManager().createQuery(" select o from " + ClasificadorUoRequisito.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<ClasificadorUoRequisito>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getClasificadorUoRequisitoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(ClasificadorUoRequisito o : getClasificadorUoRequisitos())
			si.add(new SelectItem(o.getIdClasificadorUoRequisito(),"" + o.getDescripcion()));
		return si;
	}

	public void setClasificadorUoRequisitoIdClasificadorUoRequisito(Long id) {
		setId(id);
	}

	public Long getClasificadorUoRequisitoIdClasificadorUoRequisito() {
		return (Long) getId();
	}

	@Override
	protected ClasificadorUoRequisito createInstance() {
		ClasificadorUoRequisito clasificadorUoRequisito = new ClasificadorUoRequisito();
		return clasificadorUoRequisito;
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

	public ClasificadorUoRequisito getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setRequisitoMinimoCuo(getEntityManager().find(RequisitoMinimoCuo.class,this.idRequisitoMinimoCuo));
		getInstance().setClasificadorUo(getEntityManager().find(ClasificadorUo.class,this.idClasificadorUo));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
		getInstance().setRequisitoMinimoCuo(getEntityManager().find(RequisitoMinimoCuo.class,this.idRequisitoMinimoCuo));
		getInstance().setClasificadorUo(getEntityManager().find(ClasificadorUo.class,this.idClasificadorUo));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@Override
    public void setInstance(ClasificadorUoRequisito instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }

	
	//Public getters and setters if exists
	
	public Long getIdRequisitoMinimoCuo(){
		return this.idRequisitoMinimoCuo;
	}
	
	public void setIdRequisitoMinimoCuo(Long idRequisitoMinimoCuo){
		this.idRequisitoMinimoCuo = idRequisitoMinimoCuo;
	}
	
	public Long getIdClasificadorUo(){
		return this.idClasificadorUo;
	}
	
	public void setIdClasificadorUo(Long idClasificadorUo){
		this.idClasificadorUo = idClasificadorUo;
	}
	
}
