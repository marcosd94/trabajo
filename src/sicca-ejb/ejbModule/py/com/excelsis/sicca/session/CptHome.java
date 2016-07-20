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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.entity.TipoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("cptHome")
public class CptHome extends EntityHome<Cpt> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@Override
	protected Cpt loadInstance() {
		Cpt o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idTipoCpt;
	private Long idGradoSalarialMin;
	private Long idGradoSalarialMax;
	public static final String CONTEXT_VAR_NAME = "cpts";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Cpt> getCpts(){
		try{
			return getEntityManager().createQuery(" select o from " + Cpt.class.getName() + " o WHERE o.activo = true ORDER BY o.denominacion").getResultList();
		}catch(Exception ex){
			return new Vector<Cpt>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getCptSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Cpt o : getCpts())
			si.add(new SelectItem(o.getIdCpt(),"" + o.getDenominacion()));
		return si;
	}

	public void setCptIdCpt(Long id) {
		setId(id);
	}

	public Long getCptIdCpt() {
		return (Long) getId();
	}

	@Override
	protected Cpt createInstance() {
		Cpt cpt = new Cpt();
		return cpt;
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

	public Cpt getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	
	@Override
    public void setInstance(Cpt instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	//Public getters and setters if exists
	
	public Long getIdTipoCpt(){
		return this.idTipoCpt;
	}
	
	public void setIdTipoCpt(Long idTipoCpt){
		this.idTipoCpt = idTipoCpt;
	}
	
	public Long getIdGradoSalarialMin(){
		return this.idGradoSalarialMin;
	}
	
	public void setIdGradoSalarialMin(Long idGradoSalarialMin){
		this.idGradoSalarialMin = idGradoSalarialMin;
	}
	
	public Long getIdGradoSalarialMax(){
		return this.idGradoSalarialMax;
	}
	
	public void setIdGradoSalarialMax(Long idGradoSalarialMax){
		this.idGradoSalarialMax = idGradoSalarialMax;
	}
	
}
