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

import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;

@Name("condicionTrabajoEspecifHome")
public class CondicionTrabajoEspecifHome extends EntityHome<CondicionTrabajoEspecif> {
	
	@In
	FacesMessages facesMessages;
	@In
	StatusMessages statusMessages;
	
	@In(required=false)
	Usuario usuarioLogueado;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	@Override
	protected CondicionTrabajoEspecif loadInstance() {
		CondicionTrabajoEspecif o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "condicionTrabajoEspecifs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<CondicionTrabajoEspecif> getCondicionTrabajoEspecifs(){
		try{
			return getEntityManager().createQuery(" select o from " + CondicionTrabajoEspecif.class.getName() + " o where activo = TRUE order by tipo").getResultList();
		}catch(Exception ex){
			return new Vector<CondicionTrabajoEspecif>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getCondicionTrabajoEspecifSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null,"SELECCIONAR.."));
		for(CondicionTrabajoEspecif o : getCondicionTrabajoEspecifs())
			si.add(new SelectItem(o.getIdCondicionesTrabajoEspecif(),"" + o.getTipo()));
		return si;
	}

	public void setCondicionTrabajoEspecifIdCondicionesTrabajoEspecif(Long id) {
		setId(id);
	}

	public Long getCondicionTrabajoEspecifIdCondicionesTrabajoEspecif() {
		return (Long) getId();
	}

	@Override
	protected CondicionTrabajoEspecif createInstance() {
		CondicionTrabajoEspecif condicionTrabajoEspecif = new CondicionTrabajoEspecif();
		return condicionTrabajoEspecif;
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

	public CondicionTrabajoEspecif getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setTipo(getInstance().getTipo().trim().toUpperCase());
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
	
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setTipo(getInstance().getTipo().trim().toUpperCase());
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM CondicionTrabajoEspecif o WHERE lower(o.descripcion) =:descripcion";
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idCondicionesTrabajoEspecif != " + getInstance().getIdCondicionesTrabajoEspecif().longValue();
		}
		List<CondicionTrabajoEspecif> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion()).getResultList();
		return list.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicateOrden(String operation) {
		String hql = "SELECT o FROM CondicionTrabajoEspecif o WHERE o.orden = "+getInstance().getOrden();
		if (operation.equalsIgnoreCase("update")) {
			hql += " AND o.idCondicionesTrabajoEspecif != "
					+ getInstance().getIdCondicionesTrabajoEspecif().longValue();
		}
		List<CondicionTrabajo> list = getEntityManager()
				.createQuery(hql).getResultList();
		return list.isEmpty();
	}

	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getDescripcion().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(getInstance().getTipo().trim().isEmpty()){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_tipo_invalido"));
			return false;
		}
		if(seguridadUtilFormController.contieneCaracter(getInstance().getTipo().trim())){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_caracter"));
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
		if (!checkDuplicateOrden(operation)) {
			statusMessages.add(Severity.ERROR, "Ya existe un registro con el mismo número de orden");
			return false;
		}
		if(getInstance().getOrden() < 0){
			statusMessages.add(Severity.ERROR, "El orden debe ser mayor a cero");
			return false;
		}
		
		return true;
	}
	
	//Public getters and setters if exists
	
}
