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
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("requisitoMinimoCptHome")
public class RequisitoMinimoCptHome extends EntityHome<RequisitoMinimoCpt> {
	
	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;
		
	@In(required=false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@Override
	protected RequisitoMinimoCpt loadInstance() {
		RequisitoMinimoCpt o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "requisitoMinimoCpts";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<RequisitoMinimoCpt> getRequisitoMinimoCpts(){
		try{
			return getEntityManager().createQuery(" select o from " + RequisitoMinimoCpt.class.getName() + " o where activo = TRUE" + " order by descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<RequisitoMinimoCpt>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getRequisitoMinimoCptSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"SELECCIONAR.."));
		for(RequisitoMinimoCpt o : getRequisitoMinimoCpts())
			si.add(new SelectItem(o.getIdRequisitoMinimoCpt(),"" + o.getDescripcion()));
		return si;
	}

	public void setRequisitoMinimoCptIdRequisitoMinimoCpt(Long id) {
		setId(id);
	}

	public Long getRequisitoMinimoCptIdRequisitoMinimoCpt() {
		return (Long) getId();
	}

	@Override
	protected RequisitoMinimoCpt createInstance() {
		RequisitoMinimoCpt requisitoMinimoCpt = new RequisitoMinimoCpt();
		return requisitoMinimoCpt;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}
	}

	public boolean isWired() {
		return true;
	}

	public RequisitoMinimoCpt getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
	
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM RequisitoMinimoCpt o WHERE lower(o.descripcion) =:descripcion ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idRequisitoMinimoCpt != " + getInstance().getIdRequisitoMinimoCpt().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicateOrden(String operation){
		String hql = "SELECT o FROM RequisitoMinimoCpt o WHERE o.orden =:orden ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idRequisitoMinimoCpt != " + getInstance().getIdRequisitoMinimoCpt().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("orden", getInstance().getOrden()).getResultList();
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
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_inactivo"));
			return false;
		}
		if(getInstance().getOrden() < 0){
			statusMessages.add(Severity.ERROR, "El Orden no puede ser un número negativo");
			return false;
		}
		if(!checkDuplicateOrden(operation)){
			statusMessages.add(Severity.ERROR, "El Orden ingresado ya existe");
			return false;
		}
		
		return true;
	}
	
	//Public getters and setters if exists
	
}
