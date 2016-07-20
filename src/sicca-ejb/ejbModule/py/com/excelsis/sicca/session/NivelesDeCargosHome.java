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

@Name("nivelesDeCargoHome")
public class NivelesDeCargosHome extends EntityHome<NivelesDeCargos> {
	
	@In
	StatusMessages statusMessages;

	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	public void setNivelesDeCargosIdNivelesDeCargo(Long id) {
		setId(id);
	}
	
	public Long getNivelesDeCargosIdNivelesDeCargo() {
		return  (Long) getId();
	}

	@Override
	protected NivelesDeCargos createInstance() {
		NivelesDeCargos nivelesDeCargo = new NivelesDeCargos();
		return nivelesDeCargo;
	}
	
	public static final String CONTEXT_VAR_NAME = "nivelesDeCargos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<NivelesDeCargos> getNivelesDeCargos(){
		try{
			System.out.print("HOLA.getNivelesDeCargos");
			List<NivelesDeCargos> a = getEntityManager().createQuery(" SELECT o FROM NivelesDeCargos o ORDER BY o.tipo ASC").getResultList();
					System.out.println("IMPRIMIR LISTA TAMANHO: "+a.size()+"\n NIVEL: "+a.get(0).getDescripcion());
			System.out.print("HOLA.getNivelesDeCargos"+a.size());
			return a;
		}catch(Exception ex){
			return new Vector<NivelesDeCargos>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getNivelesDeCargosSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		System.out.print("HOLA.getNivelesDeCargosSelectItems");
		
		//si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		
		for(NivelesDeCargos o : getNivelesDeCargos())
			si.add(new SelectItem(o.getIdNivelesDeCargo(),"" + o.getTipo() +" - " +o.getDescripcion()));
		return si;
	}
	
	
	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		
	}

	public boolean isWired() {
		return true;
	}

	public NivelesDeCargos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setTipo(getInstance().getTipo().trim().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM NivelesDeCargos o WHERE lower(o.descripcion) = :desc";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idNivelesDeCargos != " + getInstance().getIdNivelesDeCargo().longValue();
		}
		List<NivelesDeCargos> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
