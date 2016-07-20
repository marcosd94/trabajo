package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("calendarioOeeCabHome")
public class CalendarioOeeCabHome extends EntityHome<CalendarioOeeCab> {

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "calendarioOeeCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	@Override
	protected CalendarioOeeCab loadInstance() {
		CalendarioOeeCab o = super.loadInstance();
		return o;
	}
	@Override
	public String persist() {
		
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	
	public void setCalendarioOeeCabIdCalendarioOeeCab(Long id) {
		setId(id);
	}

	public Long getCalendarioOeeCabIdCalendarioOeeCab() {
		return (Long) getId();
	}

	@Override
	protected CalendarioOeeCab createInstance() {
		CalendarioOeeCab calendarioOeeCab = new CalendarioOeeCab();
		return calendarioOeeCab;
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

	public CalendarioOeeCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<CalendarioOeeDet> getCalendarioOeeDets() {
		return getInstance() == null ? null : new ArrayList<CalendarioOeeDet>(
				getInstance().getCalendarioOeeDets());
	}

}
