package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.CptObs;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("cptObsHome")
public class CptObsHome extends EntityHome<CptObs>
{
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@Override
	protected CptObs loadInstance() {
		CptObs o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idCpt;
	
	
	public static final String CONTEXT_VAR_NAME = "cptObs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<CptObs> getCptObs(){
		try{
			return getEntityManager().createQuery(" select o from " + CptObs.class.getName() + " o WHERE o.activo = true ").getResultList();
		}catch(Exception ex){
			return new Vector<CptObs>();
		}
	}
	
	

	public void setCptIdCpt(Long id) {
		setId(id);
	}

	public Long getCptIdCpt() {
		return (Long) getId();
	}

	@Override
	protected CptObs createInstance() {
		CptObs cptobs = new CptObs();
		return cptobs;
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

	public CptObs getDefinedInstance() {
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
    public void setInstance(CptObs instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	//Public getters and setters if exists
	
	public Long getIdCpt(){
		return this.idCpt;
	}
	
	public void setIdTipoCpt(Long idCpt){
		this.idCpt = idCpt;
	}
	
	
	
	

}
