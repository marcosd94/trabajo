package py.com.excelsis.sicca.session;


import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.Barrio;
import py.com.excelsis.sicca.entity.Ciudad;
import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Scope(ScopeType.CONVERSATION)
@Name("barrioHome")
public class BarrioHome extends EntityHome<Barrio> {

	@In(create = true)
	CiudadHome ciudadHome;

	
	@In
	StatusMessages statusMessages;
	
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	@In(required = false)
	Usuario usuarioLogueado;
	
	//Value holders for selectItems if exists
	private Long idCiudad;
	public static final String CONTEXT_VAR_NAME = "barrios";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<Barrio> getBarrios(){
		try{
			return getEntityManager().createQuery(" select o from " + Barrio.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<Barrio>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getBarrioSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(Barrio o : getBarrios())
			si.add(new SelectItem(o.getIdBarrio(),"" + o.getDescripcion()));
		return si;
	}
	
	public void setBarrioIdBarrio(Long id) {
		setId(id);
	}

	public Long getBarrioIdBarrio() {
		return (Long) getId();
	}

	@Override
	protected Barrio createInstance() {
		Barrio barrio = new Barrio();
		return barrio;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Ciudad ciudad = ciudadHome.getDefinedInstance();
		if (ciudad != null) {
			getInstance().setCiudad(ciudad);
		}
	}

	public boolean isWired() {
		if (getInstance().getCiudad() == null)
			return false;
		return true;
	}

	public Barrio getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	public String save(){
		if(!isIdDefined()){
			return persist();
		}
		return update();
	}
	@Override
	public String persist() {
		if(!checkData("persist"))
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setCiudad(getEntityManager().find(Ciudad.class, idCiudad));
			
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData("update"))
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setCiudad(getEntityManager().find(Ciudad.class, idCiudad));
			
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM Barrio o WHERE lower(o.descripcion) =:descr "+
				" and o.ciudad.idCiudad="+idCiudad;
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idBarrio != " + getInstance().getIdBarrio().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("descr",getInstance().getDescripcion().trim().toLowerCase() ).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(String operation){
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
		return true;
	}
	//Public getters and setters
	public Long getIdCiudad() {
		return idCiudad;
	}

	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}
	
	
	

}
