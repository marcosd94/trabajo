package py.com.excelsis.sicca.session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.PersistenceException;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.EscalaCondTrab;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.ValorNivelOrg;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("escalaCondTrabHome")
public class EscalaCondTrabHome extends EntityHome<EscalaCondTrab> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2422511889529692047L;

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	@Override
	protected EscalaCondTrab loadInstance() {
		EscalaCondTrab o = super.loadInstance();
		this.idCondicionTrabajo = o.getCondicionTrabajo().getIdCondicionTrabajo();
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idCondicionTrabajo;
	public static final String CONTEXT_VAR_NAME = "escalaCondTrabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EscalaCondTrab> getEscalaCondTrabs(){
		try{
			return getEntityManager().createQuery(" select o from " + EscalaCondTrab.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<EscalaCondTrab>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEscalaCondTrabSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(EscalaCondTrab o : getEscalaCondTrabs())
			si.add(new SelectItem(o.getIdEscalaCondTrab(),"" + o.getDescripcion()));
		return si;
	}

	public void setEscalaCondTrabIdEscalaCondTrab(Long id) {
		setId(id);
	}

	public Long getEscalaCondTrabIdEscalaCondTrab() {
		return (Long) getId();
	}

	@Override
	protected EscalaCondTrab createInstance() {
		EscalaCondTrab escalaCondTrab = new EscalaCondTrab();
		return escalaCondTrab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(getInstance().getIdEscalaCondTrab() == null)
			getInstance().setActivo(true);
	}

	public boolean isWired() {
		return true;
	}

	public EscalaCondTrab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(getInstance().getHasta() < getInstance().getDesde()){
			statusMessages.add(Severity.ERROR, "Desde no puede ser mayor a Hasta");
			return null;
		}
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setCondicionTrabajo(getEntityManager().find(CondicionTrabajo.class,this.idCondicionTrabajo));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(getInstance().getHasta() < getInstance().getDesde()){
			statusMessages.add(Severity.ERROR, "Desde no puede ser mayor a Hasta");
			return null;
		}
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());		
			getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setCondicionTrabajo(getEntityManager().find(CondicionTrabajo.class,this.idCondicionTrabajo));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM EscalaCondTrab o WHERE lower(o.descripcion) =:descripcion";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaCondTrab != " + getInstance().getIdEscalaCondTrab().longValue();
		}
		List<EscalaCondTrab> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		if(getInstance().getDesde() < 0){
			statusMessages.add(Severity.ERROR, "El campo Desde debe ser mayor a cero");
			return false;
		}
		if(getInstance().getHasta() < 0){
			statusMessages.add(Severity.ERROR, "El campo Hasta debe ser mayor a cero");
			return false;
		}
		if(getInstance().getHasta() < getInstance().getDesde()){
			statusMessages.add(Severity.ERROR, "El campo Hasta no debe ser mayor al campo Desde");
			return false;
		}
		if(!estaValor(operation, getInstance().getDesde()) && !estaValor(operation, getInstance().getHasta())){
			statusMessages.add(Severity.ERROR, "El rango ingresado ya existe");
			return false;
		}
		
		return true;
	}
	
	private boolean estaValor(String operation, Integer valor){
		String hql = "SELECT o FROM EscalaCondTrab o join o.condicionTrabajo c " +
				"WHERE (:valor between o.desde and o.hasta) and c.idCondicionTrabajo = :tipo ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaCondTrab != " + getInstance().getIdEscalaCondTrab().longValue();
		}
		javax.persistence.Query q = getEntityManager().createQuery(hql);
		q.setParameter("valor", valor);
		q.setParameter("tipo", idCondicionTrabajo);
		List<EscalaCondTrab> list = q.getResultList();
		return list.isEmpty();
	}
	
	@Override
    public void setInstance(EscalaCondTrab instance)
    {
        if (instance != null)
        {
            super.setId(instance.getId());
        }
        super.setInstance(instance);
    }
	
	//Public getters and setters if exists
	
	public Long getIdCondicionTrabajo(){
		return this.idCondicionTrabajo;
	}
	
	public void setIdCondicionTrabajo(Long idCondicionTrabajo){
		this.idCondicionTrabajo = idCondicionTrabajo;
	}
	
}
