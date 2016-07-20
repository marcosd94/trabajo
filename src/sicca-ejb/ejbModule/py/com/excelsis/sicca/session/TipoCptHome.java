package py.com.excelsis.sicca.session;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.TipoCpt;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("tipoCptHome")
public class TipoCptHome extends EntityHome<TipoCpt> {
	
	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	
	@Override
	protected TipoCpt loadInstance() {
		TipoCpt o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "tipoCpts";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<TipoCpt> getTipoCpts(){
		try{
			return getEntityManager().createQuery(" select o from " + TipoCpt.class.getName() + " o where activo = TRUE order by descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<TipoCpt>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ConfiguracionUoCab> getConfiguracionUoCabs(){
		try{
			return getEntityManager().createQuery(" select o from " + ConfiguracionUoCab.class.getName() + " o where activo = TRUE order by denominacionUnidad").getResultList();
		}catch(Exception ex){
			return new Vector<ConfiguracionUoCab>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getTipoCptSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(TipoCpt o : getTipoCpts())
			si.add(new SelectItem(o.getIdTipoCpt(),"" + o.getDescripcion()));
		return si;
	}
	

	public void setTipoCptIdTipoCpt(Long id) {
		setId(id);
	}

	public Long getTipoCptIdTipoCpt() {
		return (Long) getId();
	}

	@Override
	protected TipoCpt createInstance() {
		TipoCpt tipoCpt = new TipoCpt();
		return tipoCpt;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(getInstance().getIdTipoCpt() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public TipoCpt getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
			
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@Override
    public void setInstance(TipoCpt instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM TipoCpt o WHERE lower(o.descripcion) =:descripcion ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idTipoCpt != " + getInstance().getIdTipoCpt().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if (seguridadUtilFormController.contieneCaracter(getInstance()
				.getDescripcion().trim())) {
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("msg_caracter"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	//Public getters and setters if exists
	
}
