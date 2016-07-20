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

import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("contenidoFuncionalHome")
public class ContenidoFuncionalHome extends EntityHome<ContenidoFuncional> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@Override
	protected ContenidoFuncional loadInstance() {
		ContenidoFuncional o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "contenidoFuncionals";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<ContenidoFuncional> getContenidoFuncionals(){
		try{
			return getEntityManager().createQuery(" select o from " + ContenidoFuncional.class.getName() + " o where o.activo=true  ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<ContenidoFuncional>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getContenidoFuncionalSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(ContenidoFuncional o : getContenidoFuncionals())
			si.add(new SelectItem(o.getIdContenidoFuncional(),"" + o.getDescripcion()));
		return si;
	}

	public void setContenidoFuncionalIdContenidoFuncional(Long id) {
		setId(id);
	}

	public Long getContenidoFuncionalIdContenidoFuncional() {
		return (Long) getId();
	}

	@Override
	protected ContenidoFuncional createInstance() {
		ContenidoFuncional contenidoFuncional = new ContenidoFuncional();
		return contenidoFuncional;
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

	public ContenidoFuncional getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
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
		if(getInstance().getOrden().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El Orden debe ser mayor a cero. Verifique");
			return false;
		}
		if(!checkDuplicateNroOrden(operation)){
			statusMessages.add(Severity.ERROR, "Ya existe el Orden. Verifique");
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM ContenidoFuncional o WHERE lower(o.descripcion) =:desc ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idContenidoFuncional != " + getInstance().getIdContenidoFuncional().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).setParameter("desc",getInstance().getDescripcion().trim().toLowerCase() ).getResultList();
		return list.isEmpty();
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicateNroOrden(String operation){
		String hql = "SELECT o FROM ContenidoFuncional o WHERE  o.orden="+getInstance().getOrden();
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idContenidoFuncional != " + getInstance().getIdContenidoFuncional().longValue();
		}
		List<ValorNivelOrg> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	
	
	//Public getters and setters if exists
	
}
