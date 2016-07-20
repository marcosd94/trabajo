package py.com.excelsis.sicca.juridicos.session;

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

import py.com.excelsis.sicca.entity.Sancion;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

@Name("sancionHome")
public class SancionHome extends EntityHome<Sancion> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	@Override
	protected Sancion loadInstance() {
		Sancion o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "sancions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Sancion> getSancions(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + Sancion.class.getName() + " o " +
					"WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<Sancion>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getSancionSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(Sancion o : getSancions())
			si.add(new SelectItem(o.getIdSancion(),"" + o.getDescripcion()));
		return si;
	}

	public void setSancionIdSancion(Long id) {
		setId(id);
	}

	public Long getSancionIdSancion() {
		return (Long) getId();
	}

	@Override
	protected Sancion createInstance() {
		Sancion sancion = new Sancion();
		return sancion;
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
			getInstance().setDesvincula(false);
			getInstance().setInhabilita(false);
		}
	}

	public boolean isWired() {
		return true;
	}

	public Sancion getDefinedInstance() {
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
		getInstance().setDescripcion(getInstance().getDescripcion().trim());	
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM Sancion o WHERE lower(o.descripcion) = '"+getInstance().getDescripcion().trim().toLowerCase()+"' ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idSancion != " + getInstance().getIdSancion().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
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

