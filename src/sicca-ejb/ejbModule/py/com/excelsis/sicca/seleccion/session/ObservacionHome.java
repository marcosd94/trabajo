package py.com.excelsis.sicca.seleccion.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.ConcursoHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("observacionHome")
public class ObservacionHome extends EntityHome<Observacion> {

	@In(create = true)
	ConcursoHome concursoHome;
	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "Observacions";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setObservacionIdObservacion(Long id) {
		setId(id);
	}

	public Long getObservacionIdObservacion() {
		return (Long) getId();
	}

	@Override
	protected Observacion createInstance() {
		Observacion observacion = new Observacion();
		return observacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Concurso concurso = concursoHome.getDefinedInstance();
		if (concurso != null) {
			getInstance().setConcurso(concurso);
		}
	}
	
	@Override
	public String persist() {
		
		getInstance().setFechaAlta(new Date());
			getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());	
			
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	

	public boolean isWired() {
		if (getInstance().getConcurso() == null)
			return false;
		return true;
	}

	public Observacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
