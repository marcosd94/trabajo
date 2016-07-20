package py.com.excelsis.sicca.session;
import java.util.List;
import java.util.Vector;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;


@Name("cptNivelesDeCargosHome")
public class CptNivelesDeCargosHome extends EntityHome<CptNivelesCargos> {
	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;
	
	public void setCptNivelesDeCargosIdNivelesDeCargo(Long id) {
		setId(id);
	}
	
	public Long getCptNivelesDeCargosIdNivelesDeCargo() {
		return  (Long) getId();
	}

	@Override
	protected CptNivelesCargos createInstance() {
		CptNivelesCargos cptNivelesCargos  = new CptNivelesCargos();
		return cptNivelesCargos;
	}
	
	public static final String CONTEXT_VAR_NAME = "cptNivelesCargos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<CptNivelesCargos> getCptNivelesCargos(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + CptNivelesCargos.class.getName() + " o ORDER BY o.tipo").getResultList();
		}catch(Exception ex){
			return new Vector<CptNivelesCargos>();
		}
	}
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setIdCpt(getInstance().getIdCpt());
		getInstance().setIdNivelesCargos(getInstance().getIdNivelesCargos());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}
	private boolean checkData(){
		return (getInstance().getIdCpt()==null || getInstance().getIdNivelesCargos()==null)? false : true;
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
	public CptNivelesCargos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	

}
