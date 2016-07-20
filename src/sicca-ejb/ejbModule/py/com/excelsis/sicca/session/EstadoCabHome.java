package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.TransicionEstado;
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

@Name("estadoCabHome")
public class EstadoCabHome extends EntityHome<EstadoCab> {

	

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "estadosCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EstadoCab> getEstadosCabs(){
		try{
			return getEntityManager().createQuery(" SELECT o FROM " + EstadoCab.class.getName() + " o " +
					"WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<EstadoCab>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEstadosCabSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(EstadoCab o : getEstadosCabs())
			si.add(new SelectItem(o.getIdEstadoCab(),"" + o.getDescripcion()));
		return si;
	}
	
	
	public void setEstadoCabIdEstadoCab(Long id) {
		setId(id);
	}

	public Long getEstadoCabIdEstadoCab() {
		return (Long) getId();
	}

	@Override
	protected EstadoCab createInstance() {
		EstadoCab estadoCab = new EstadoCab();
		return estadoCab;
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

	public EstadoCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<TransicionEstado> getTransicionEstadosForPosibleEstado() {
		return getInstance() == null ? null : new ArrayList<TransicionEstado>(
				getInstance().getTransicionEstadosForPosibleEstado());
	}

	public List<TransicionEstado> getTransicionEstadosForIdEstadoCab() {
		return getInstance() == null ? null : new ArrayList<TransicionEstado>(
				getInstance().getTransicionEstadosForIdEstadoCab());
	}

	public List<PlantaCargoDet> getPlantaCargoDets() {
		return getInstance() == null ? null : new ArrayList<PlantaCargoDet>(
				getInstance().getPlantaCargoDets());
	}

	public List<HistoricosEstado> getHistoricosEstados() {
		return getInstance() == null ? null : new ArrayList<HistoricosEstado>(
				getInstance().getHistoricosEstados());
	}

	public List<EstadoDet> getEstadoDets() {
		return getInstance() == null ? null : new ArrayList<EstadoDet>(
				getInstance().getEstadoDets());
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
		String hql = "SELECT o FROM EstadoCab o WHERE lower(o.descripcion) =:desc ";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEstadoCab != " + getInstance().getIdEstadoCab().longValue();
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
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	

}
