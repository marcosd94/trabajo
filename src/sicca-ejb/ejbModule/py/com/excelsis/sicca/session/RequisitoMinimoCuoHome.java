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

import py.com.excelsis.sicca.entity.RequisitoMinimoCuo;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("requisitoMinimoCuoHome")
public class RequisitoMinimoCuoHome extends EntityHome<RequisitoMinimoCuo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2402969927656146696L;

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	@Override
	protected RequisitoMinimoCuo loadInstance() {
		RequisitoMinimoCuo o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "requisitoMinimoCuos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<RequisitoMinimoCuo> getRequisitoMinimoCuos(){
		try{
			return getEntityManager().createQuery(" select o from " + RequisitoMinimoCuo.class.getName() + " o" +" order by descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<RequisitoMinimoCuo>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getRequisitoMinimoCuoSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(RequisitoMinimoCuo o : getRequisitoMinimoCuos())
			si.add(new SelectItem(o.getIdRequisitoMinimoCuo(),"" + o.getDescripcion()));
		return si;
	}

	public void setRequisitoMinimoCuoIdRequisitoMinimoCuo(Long id) {
		setId(id);
	}

	public Long getRequisitoMinimoCuoIdRequisitoMinimoCuo() {
		return (Long) getId();
	}

	@Override
	protected RequisitoMinimoCuo createInstance() {
		RequisitoMinimoCuo requisitoMinimoCuo = new RequisitoMinimoCuo();
		return requisitoMinimoCuo;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(getInstance().getIdRequisitoMinimoCuo() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public RequisitoMinimoCuo getDefinedInstance() {
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
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM RequisitoMinimoCuo o WHERE lower(o.descripcion) =:descripcion";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idRequisitoMinimoCuo != " + getInstance().getIdRequisitoMinimoCuo().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicateOrden(String operation){
		String hql = "SELECT o FROM RequisitoMinimoCuo o WHERE o.orden =:orden";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idRequisitoMinimoCuo != " + getInstance().getIdRequisitoMinimoCuo().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("orden", getInstance().getOrden()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(getInstance().getDescripcion().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		if(!checkDuplicateOrden(operation)){
			statusMessages.add(Severity.ERROR, "El orden ingresado ya existe");
			return false;
		}
		if(getInstance().getOrden() < 0){
			statusMessages.add(Severity.ERROR, "El campo Orden debe ser mayor a cero");
			return false;
		}
		return true;
	}
	
	//Public getters and setters if exists
	
}
