package py.com.excelsis.sicca.session;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ProcesoGestion;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("procesoGestionHome")
public class ProcesoGestionHome extends EntityHome<ProcesoGestion> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	@Override
	protected ProcesoGestion loadInstance() {
		ProcesoGestion o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idConfiguracionUoDet;
	public static final String CONTEXT_VAR_NAME = "procesoGestions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ProcesoGestion> getProcesoGestions(){
		try{
			return getEntityManager().createQuery(" select o from " + ProcesoGestion.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<ProcesoGestion>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getProcesoGestionSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(ProcesoGestion o : getProcesoGestions())
			si.add(new SelectItem(o.getIdProcesoGestion(),"" + o.getDescripcion()));
		return si;
	}

	public void setProcesoGestionIdProcesoGestion(Long id) {
		setId(id);
	}

	public Long getProcesoGestionIdProcesoGestion() {
		return (Long) getId();
	}

	@Override
	protected ProcesoGestion createInstance() {
		ProcesoGestion procesoGestion = new ProcesoGestion();
		return procesoGestion;
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

	public ProcesoGestion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());			
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());				
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	//Public getters and setters if exists
	
	public Long getIdConfiguracionUoDet(){
		return this.idConfiguracionUoDet;
	}
	
	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet){
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}
	
}
