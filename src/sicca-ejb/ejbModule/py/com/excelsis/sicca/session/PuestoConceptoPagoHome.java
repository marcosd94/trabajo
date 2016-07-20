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
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PuestoConceptoPago;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("puestoConceptoPagoHome")
public class PuestoConceptoPagoHome extends EntityHome<PuestoConceptoPago> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@Override
	protected PuestoConceptoPago loadInstance() {
		PuestoConceptoPago o = super.loadInstance();
		this.idPlantaCargoDet = o.getPlantaCargoDet().getIdPlantaCargoDet();
		
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idPlantaCargoDet;
	private Long idConceptoPago;
	public static final String CONTEXT_VAR_NAME = "puestoConceptoPagos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<PuestoConceptoPago> getPuestoConceptoPagos(){
		try{
			return getEntityManager().createQuery(" select o from " + PuestoConceptoPago.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<PuestoConceptoPago>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getPuestoConceptoPagoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(PuestoConceptoPago o : getPuestoConceptoPagos())
			si.add(new SelectItem(o.getIdPuestoConceptoPago(),"" + o.getUsuAlta()));
		return si;
	}

	public void setPuestoConceptoPagoIdPuestoConceptoPago(Long id) {
		setId(id);
	}

	public Long getPuestoConceptoPagoIdPuestoConceptoPago() {
		return (Long) getId();
	}

	@Override
	protected PuestoConceptoPago createInstance() {
		PuestoConceptoPago puestoConceptoPago = new PuestoConceptoPago();
		return puestoConceptoPago;
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

	public PuestoConceptoPago getDefinedInstance() {
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
    public void setInstance(PuestoConceptoPago instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	//Public getters and setters if exists
	
	public Long getIdPlantaCargoDet(){
		return this.idPlantaCargoDet;
	}
	
	public void setIdPlantaCargoDet(Long idPlantaCargoDet){
		this.idPlantaCargoDet = idPlantaCargoDet;
	}
	
	public Long getIdConceptoPago(){
		return this.idConceptoPago;
	}
	
	public void setIdConceptoPago(Long idConceptoPago){
		this.idConceptoPago = idConceptoPago;
	}
	
}
