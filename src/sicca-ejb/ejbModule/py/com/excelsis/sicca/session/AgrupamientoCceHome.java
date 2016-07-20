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

import py.com.excelsis.sicca.entity.AgrupamientoCce;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.TipoCce;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("agrupamientoCceHome")
public class AgrupamientoCceHome extends EntityHome<AgrupamientoCce> {
	
	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	
	@In(required=false)
	Usuario usuarioLogueado;
	
	private Long codTipoCce;
	
	@Override
	protected AgrupamientoCce loadInstance() {
		AgrupamientoCce o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "nivelGradoSalarials";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<AgrupamientoCce> getNivelGradoSalarials(){
		try{
			return getEntityManager().createQuery(" select o from " + AgrupamientoCce.class.getName() + " o " +
					" WHERE o.activo = true order by o.descripcion ").getResultList();
		}catch(Exception ex){
			return new Vector<AgrupamientoCce>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getNivelGradoSalarialSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(AgrupamientoCce o : getNivelGradoSalarials())
			si.add(new SelectItem(o.getIdAgrupamientoCce(),"" + o.getDescripcion()));
		return si;
	}

	public void setNivelGradoSalarialIdNivelGradoSalarial(Long id) {
		setId(id);
	}

	public Long getNivelGradoSalarialIdNivelGradoSalarial() {
		return (Long) getId();
	}

	@Override
	protected AgrupamientoCce createInstance() {
		AgrupamientoCce agrupamientoCce = new AgrupamientoCce();
		return agrupamientoCce;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		codTipoCce = getInstance().getTipoCce() != null ? getInstance().getTipoCce().getIdTipoCce() : null;
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}
	}

	public boolean isWired() {
		return true;
	}

	public AgrupamientoCce getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData("persist"))
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setTipoCce(getEntityManager().find(TipoCce.class, codTipoCce));
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());	
	
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData("update"))
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setTipoCce(getEntityManager().find(TipoCce.class, codTipoCce));
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "" +
				" SELECT o FROM AgrupamientoCce o " +
				" WHERE (lower(o.descripcion) =:desc "+
				" 			and  o.numero = " + getInstance().getNumero() + ") " +
				" and o.tipoCce.idTipoCce="+codTipoCce;
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idAgrupamientoCce != " + getInstance().getIdAgrupamientoCce().longValue();
		}
		
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(String operation){
		
		if(codTipoCce == null){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("NivelGradoSalarial_msg_sin_tipoCce"));
			return false;
		}
		if(getInstance().getNumero().intValue()<=0){
			statusMessages.add(Severity.ERROR,"El campo número debe ser mayor a 0. Verifique	");
			return false;
		}
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

	//Public getters and setters if exists
	public void setCodTipoCce(Long codTipoCce) {
		this.codTipoCce = codTipoCce;
	}

	public Long getCodTipoCce() {
		return codTipoCce;
	}
	
}
