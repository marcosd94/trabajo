package py.com.excelsis.sicca.session;

import java.util.Date;
import java.util.List;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("calendarioSfpHome")
public class CalendarioSfpHome extends EntityHome<CalendarioSfp> {

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "calendarioSfps";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	
	@Override
	protected CalendarioSfp loadInstance() {
		CalendarioSfp o = super.loadInstance();
		return o;
	}
	@Override
	public String persist() {
		if(!checkData())
			return null;
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		if(!checkData())
			return null;
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	@SuppressWarnings("unchecked")
	private boolean checkDuplicate(String operation){
		String hql = "SELECT o FROM CalendarioSfp o WHERE o.anho = "+getInstance().getAnho();
		if(operation.equalsIgnoreCase("update")){
			hql += " AND o.idCalendarioSfp != " + getInstance().getIdCalendarioSfp().longValue();
		}
		List<CalendarioSfp> list = getEntityManager().createQuery(hql).getResultList();
		return list.isEmpty();
	}
	
	private boolean checkData(){
		String operation = isIdDefined() ? "update" : "persist";
		if(getInstance().getAnho() == null){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_descripcion_invalida"));
			return false;
		}
		if(!checkDuplicate(operation)){
			statusMessages.add(Severity.ERROR, SeamResourceBundle.getBundle().getString("msg_registro_duplicado"));
			return false;
		}
		return true;
	}
	
	public void setCalendarioSfpIdCalendarioSfp(Long id) {
		setId(id);
	}

	public Long getCalendarioSfpIdCalendarioSfp() {
		return (Long) getId();
	}

	@Override
	protected CalendarioSfp createInstance() {
		CalendarioSfp calendarioSfp = new CalendarioSfp();
		return calendarioSfp;
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

	public CalendarioSfp getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
