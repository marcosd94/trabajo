package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.AgrupamientoCce;
import py.com.excelsis.sicca.entity.TipoCce;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import enums.Estado;

@Name("tipoCceHome")
public class TipoCceHome extends EntityHome<TipoCce> {
	
	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	public void setTipoCceIdTipoCce(Long id) {
		setId(id);
	}

	public Long getTipoCceIdTipoCce() {
		return (Long) getId();
	}

	@Override
	protected TipoCce createInstance() {
		TipoCce tipoCce = new TipoCce();
		return tipoCce;
	}
	
	public static final String CONTEXT_VAR_NAME = "tipoCces";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<TipoCce> getTipoCces(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + TipoCce.class.getName() 
											+ " o WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<TipoCce>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getTipoCceSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(TipoCce o : getTipoCces())
			si.add(new SelectItem(o.getIdTipoCce(),"" + o.getDescripcion()));
		return si;
	}
	
	
	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(!isIdDefined()){
			getInstance().setActivo(Estado.ACTIVO.getValor());
		}
	}

	public boolean isWired() {
		return true;
	}

	public TipoCce getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<AgrupamientoCce> getNivelGradoSalarials() {
		return getInstance() == null ? null
				: new ArrayList<AgrupamientoCce>(getInstance()
						.getAgrupamientoCces());
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
		String hql = "SELECT o FROM TipoCce o WHERE lower(o.descripcion) = :desc";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idTipoCce != " + getInstance().getIdTipoCce().longValue();
		}
		List<TipoCce> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		return true;
	}

}
