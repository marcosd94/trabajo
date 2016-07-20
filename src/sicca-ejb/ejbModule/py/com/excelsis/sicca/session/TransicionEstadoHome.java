package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;
import py.com.excelsis.sicca.entity.TransicionEstado;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("transicionEstadoHome")
public class TransicionEstadoHome extends EntityHome<TransicionEstado> {

	@In(create = true)
	EstadoCabHome estadoCabHome;

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "transicionEstadoSegurs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	private Long idEstadoPosible;
	private Long idEstadoCab;
	
	

	public void setTransicionEstadoIdTrancisionEstado(Long id) {
		setId(id);
	}

	public Long getTransicionEstadoIdTrancisionEstado() {
		return (Long) getId();
	}

	@Override
	protected TransicionEstado createInstance() {
		TransicionEstado transicionEstado = new TransicionEstado();
		return transicionEstado;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
//		EstadoCab estadoCabByIdEstadoCab = estadoCabHome.getDefinedInstance();
//		if (estadoCabByIdEstadoCab != null) {
//			getInstance().setEstadoCabByIdEstadoCab(estadoCabByIdEstadoCab);
//		}
//		EstadoCab estadoCabByPosibleEstado = estadoCabHome.getDefinedInstance();
//		if (estadoCabByPosibleEstado != null) {
//			getInstance().setEstadoCabByPosibleEstado(estadoCabByPosibleEstado);
//		}
		if(isIdDefined()){
			idEstadoCab=getInstance().getEstadoCabByIdEstadoCab().getIdEstadoCab();
			idEstadoPosible=getInstance().getEstadoCabByPosibleEstado().getIdEstadoCab();
		}else
		{
			idEstadoCab=null;
			idEstadoPosible=null;
		}
	}

	public boolean isWired() {
//		if (getInstance().getEstadoCabByIdEstadoCab() == null)
//			return false;
//		if (getInstance().getEstadoCabByPosibleEstado() == null)
//			return false;
		return true;
	}

	public TransicionEstado getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		getInstance().setEstadoCabByPosibleEstado(getEntityManager().find(EstadoCab.class, idEstadoPosible));
		getInstance().setEstadoCabByIdEstadoCab(getEntityManager().find(EstadoCab.class,idEstadoCab));
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());	
		getInstance().setEstadoCabByPosibleEstado(getEntityManager().find(EstadoCab.class, idEstadoPosible));
		getInstance().setEstadoCabByIdEstadoCab(getEntityManager().find(EstadoCab.class,idEstadoCab));
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	
	 @Override
	    public String remove()
	    {
	        try {
	            return AppHelper.removeFromContext("removed",super.remove(),  CONTEXT_VAR_NAME, getEventContext());
	    } catch (PersistenceException e) {
	                    if (e.getCause().toString().contains("ConstraintViolationException"))
	                            statusMessages.add(Severity.ERROR,SeamResourceBundle.getBundle().getString("msg_constraint_violation"));
	                    else
	                            statusMessages.add(Severity.ERROR,"Error Inesperado");

	                    return null;
	            }
	    }
	
	 @SuppressWarnings("unchecked")
		private boolean checkDuplicate(String operation){
			String hql = "SELECT o FROM TransicionEstado o WHERE o.estadoCabByIdEstadoCab.idEstadoCab = "+idEstadoCab+" "
			+" AND o.estadoCabByPosibleEstado.idEstadoCab ="+idEstadoPosible;
			if(operation.equalsIgnoreCase("update")){
				hql += " AND o.idTrancisionEstado != " + getInstance().getIdTrancisionEstado().longValue();
			}
			List<RequisitoMinimoCpt> list = getEntityManager().createQuery(hql).getResultList();
			return list.isEmpty();
		}
		
		private boolean checkData(){
			String operation = isIdDefined() ? "update" : "persist";
			
			if(!checkDuplicate(operation)){
				statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
				return false;
			}
			return true;
		}
	public Long getIdEstadoPosible() {
		return idEstadoPosible;
	}

	public void setIdEstadoPosible(Long idEstadoPosible) {
		this.idEstadoPosible = idEstadoPosible;
	}

	public Long getIdEstadoCab() {
		return idEstadoCab;
	}

	public void setIdEstadoCab(Long idEstadoCab) {
		this.idEstadoCab = idEstadoCab;
	}

	
}
