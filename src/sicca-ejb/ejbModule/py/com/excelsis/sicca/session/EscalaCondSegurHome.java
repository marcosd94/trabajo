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
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.EscalaCondSegur;
import py.com.excelsis.sicca.entity.EscalaCondTrabEspecif;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("escalaCondSegurHome")
public class EscalaCondSegurHome extends EntityHome<EscalaCondSegur> {
	
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	@In
	StatusMessages statusMessages;
	
	@Override
	protected EscalaCondSegur loadInstance() {
		EscalaCondSegur o = super.loadInstance();
		
		return o;
	}
	
	//Value holders for selectItems if exists
	private Long idCondicionSegur;
	public static final String CONTEXT_VAR_NAME = "escalaCondSegurs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EscalaCondSegur> getEscalaCondSegurs(){
		try{
			return getEntityManager().createQuery(" select o from " + EscalaCondSegur.class.getName() + " o").getResultList();
		}catch(Exception ex){
			return new Vector<EscalaCondSegur>();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEscalaCondSegurSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		for(EscalaCondSegur o : getEscalaCondSegurs())
			si.add(new SelectItem(o.getIdEscalaCondSegur(),"" + o.getDescripcion()));
		return si;
	}

	public void setEscalaCondSegurIdEscalaCondSegur(Long id) {
		setId(id);
	}

	public Long getEscalaCondSegurIdEscalaCondSegur() {
		return (Long) getId();
	}

	@Override
	protected EscalaCondSegur createInstance() {
		EscalaCondSegur escalaCondSegur = new EscalaCondSegur();
		return escalaCondSegur;
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

	public EscalaCondSegur getDefinedInstance() {
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
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM EscalaCondSegur o WHERE lower(o.descripcion) =:desc ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaCondSegur != " + getInstance().getIdEscalaCondSegur().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		if(getInstance().getDesde().intValue()<0){
			statusMessages.add(Severity.ERROR,"El valor DESDE no puede ser menor a cero, verifique");
			return false;
		}
		if(getInstance().getHasta().intValue()<0){
			statusMessages.add(Severity.ERROR,"El valor HASTA no puede ser menor a cero, verifique");
			return false;
		}
		if(getInstance().getDesde().intValue()>getInstance().getHasta().intValue()){
			statusMessages.add(Severity.ERROR,"El valor DESDE no puede ser mayor al el valor HASTA, verifique");
			return false;
		}
		if(!estaValor(operation, getInstance().getDesde()) || !estaValor(operation, getInstance().getHasta())){
			statusMessages.add(Severity.ERROR, "El rango ingresado ya existe");
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	private boolean estaValor(String operation, Integer valor){
		String hql = "SELECT o FROM EscalaCondSegur o WHERE :valor between o.desde and o.hasta ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEscalaCondSegur != " + getInstance().getIdEscalaCondSegur().longValue();
		}
		List<EscalaCondTrabEspecif> list = getEntityManager().createQuery(hql).setParameter("valor", valor).getResultList();
		return list.isEmpty();
	}
	
	
	//Public getters and setters if exists
	
	public Long getIdCondicionSegur(){
		return this.idCondicionSegur;
	}
	
	public void setIdCondicionSegur(Long idCondicionSegur){
		this.idCondicionSegur = idCondicionSegur;
	}
	
}
