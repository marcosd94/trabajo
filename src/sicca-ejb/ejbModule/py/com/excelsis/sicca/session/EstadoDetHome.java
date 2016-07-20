package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("estadoDetHome")
public class EstadoDetHome extends EntityHome<EstadoDet> {

	@In(create = true)
	EstadoCabHome estadoCabHome;
	@In
	StatusMessages statusMessages;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	
	@In(required=false)
	Usuario usuarioLogueado;
	
	private Long idEstadoCab;
	

	public void setEstadoDetIdEstadoDet(Long id) {
		setId(id);
	}

	public Long getEstadoDetIdEstadoDet() {
		return (Long) getId();
	}

	@Override
	protected EstadoDet createInstance() {
		EstadoDet estadoDet = new EstadoDet();
		return estadoDet;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "estadoDetSalarials";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@SuppressWarnings("unchecked")
	@Factory(value=CONTEXT_VAR_NAME, scope=ScopeType.EVENT)
	public List<EstadoDet> getEstadosDetSalarials(){
		try{
			return getEntityManager().createQuery(" select o from " + EstadoDet.class.getName() + " o " +
					" WHERE o.activo = true order by o.descripcion ").getResultList();
		}catch(Exception ex){
			return new Vector<EstadoDet>();
		}
	}
	
	@Factory(value=CONTEXT_VAR_NAME + "SelectItems", scope=ScopeType.EVENT)
	public List<SelectItem> getEstadosDetSelectItems(){
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for(EstadoDet o : getEstadosDetSalarials())
			si.add(new SelectItem(o.getIdEstadoDet(),"" + o.getDescripcion()));
		return si;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EstadoCab estadoCab = estadoCabHome.getDefinedInstance();
//		if (estadoCab != null) {
//			getInstance().setEstadoCab(estadoCab);
//		}
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}else
			idEstadoCab=getInstance().getEstadoCab().getIdEstadoCab();
	}

	public boolean isWired() {
//		if (getInstance().getEstadoCab() == null)
//			return false;
		return true;
	}
		
	@Override
	public String persist() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setEstadoCab(getEntityManager().find(EstadoCab.class, idEstadoCab));
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());	
	
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		
		getInstance().setDescripcion(getInstance().getDescripcion().trim().toUpperCase());
		getInstance().setEstadoCab(getEntityManager().find(EstadoCab.class, idEstadoCab));
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){ 
		String hql = "SELECT o FROM EstadoDet o WHERE lower(o.descripcion) =:descripcion "+
				" and o.estadoCab.idEstadoCab="+idEstadoCab;
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idEstadoDet != " + getInstance().getIdEstadoDet().longValue();
		}
		List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).setParameter("descripcion", getInstance().getDescripcion().trim().toLowerCase()).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(idEstadoCab == null){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("EstadoDet_msg_idCab"));
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
	public EstadoDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public Long getIdEstadoCab() {
		return idEstadoCab;
	}

	public void setIdEstadoCab(Long idEstadoCab) {
		this.idEstadoCab = idEstadoCab;
	}

}
