package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

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

@Name("nivelEstudioHome")
public class NivelEstudioHome extends EntityHome<NivelEstudio> {
	
	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;
	
	public static final String CONTEXT_VAR_NAME = "nivelEstudio";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<NivelEstudio> getNivelesEstudio(){
		try{
			return getEntityManager().createQuery(" select o from " + NivelEstudio.class.getName() + " o" +
					" WHERE o.activo = true ORDER BY o.descripcion").getResultList();
		}catch(Exception ex){
			return new Vector<NivelEstudio>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getNivelesEstudioSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(NivelEstudio o : getNivelesEstudio())
			si.add(new SelectItem(o.getIdNivelEstudio(), o.getDescripcion()));
		return si;
	}
	@Factory(value=CONTEXT_VAR_NAME + "TodosSelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getNivelesEstudioTodosSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"Todos"));
		for(NivelEstudio o : getNivelesEstudio())
			si.add(new SelectItem(o.getIdNivelEstudio(), o.getDescripcion()));
		return si;
	}


	public void setNivelEstudioIdNivelEstudio(Long id) {
		setId(id);
	}

	public Long getNivelEstudioIdNivelEstudio() {
		return (Long) getId();
	}

	@Override
	protected NivelEstudio createInstance() {
		NivelEstudio nivelEstudio = new NivelEstudio();
		return nivelEstudio;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		if(!isIdDefined()){
			getInstance().setActivo(true);
		}
	}

	public boolean isWired() {
		return true;
	}

	public NivelEstudio getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<TituloAcademico> getTituloAcademicos() {
		return getInstance() == null ? null : new ArrayList<TituloAcademico>(
				getInstance().getTituloAcademicos());
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());

		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM NivelEstudio o WHERE lower(o.descripcion) = :desc";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idNivelEstudio != " + getInstance().getIdNivelEstudio().longValue();
		}
		List<CondicionTrabajoEspecif> list = getEntityManager().createQuery(hql).setParameter("desc", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
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
		if(!getInstance().getActivo()){
			if(!getInstance().getTituloAcademicos().isEmpty()){
				statusMessages.add("No se puede inactivar el registro porque está en uso");
				return false;
			}
		}
			
			
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	
}
