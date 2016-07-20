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

import py.com.excelsis.sicca.entity.AgrupamientoCce;
import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("gradoSalarialHome")
public class GradoSalarialHome extends EntityHome<GradoSalarial> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6477961146931747346L;

	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario user;
	
	@Override
	protected GradoSalarial loadInstance() {
		GradoSalarial o = super.loadInstance();
		this.idNivelGradoSalarial = o.getAgrupamientoCce().getIdAgrupamientoCce();//se cambio lo qeu era nivel grado salarial
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idNivelGradoSalarial;
	public static final String CONTEXT_VAR_NAME = "gradoSalarials";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<GradoSalarial> getGradoSalarials(){
		try{
			return getEntityManager().createQuery(" select o from " + GradoSalarial.class.getName() + " o order by numero ").getResultList();
		}catch(Exception ex){
			return new Vector<GradoSalarial>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getGradoSalarialSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"SELECCIONAR.."));
		for(GradoSalarial o : getGradoSalarials())
			si.add(new SelectItem(o.getIdGradoSalarial(),"" + o.getNumero()));
		return si;
	}

	public void setGradoSalarialIdGradoSalarial(Long id) {
		setId(id);
	}

	public Long getGradoSalarialIdGradoSalarial() {
		return (Long) getId();
	}

	@Override
	protected GradoSalarial createInstance() {
		GradoSalarial gradoSalarial = new GradoSalarial();
		return gradoSalarial;
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

	public GradoSalarial getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta("AMDIN");		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
	//	getInstance().setNivelGradoSalarial(getEntityManager().find(NivelGradoSalarial.class,this.idNivelGradoSalarial));
		getInstance().setAgrupamientoCce(getEntityManager().find(AgrupamientoCce.class,this.idNivelGradoSalarial));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod("AMDIN");		
			//getInstance().setNivelGradoSalarial(getEntityManager().find(NivelGradoSalarial.class,this.idNivelGradoSalarial));
			getInstance().setAgrupamientoCce(getEntityManager().find(AgrupamientoCce.class,this.idNivelGradoSalarial));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@Override
    public void setInstance(GradoSalarial instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	//Public getters and setters if exists
	
	public Long getIdNivelGradoSalarial(){
		return this.idNivelGradoSalarial;
	}
	
	public void setIdNivelGradoSalarial(Long idNivelGradoSalarial){
		this.idNivelGradoSalarial = idNivelGradoSalarial;
	}
	
}
