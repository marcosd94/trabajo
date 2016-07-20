package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("postulacionHome")
public class PostulacionHome extends EntityHome<Postulacion> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	PersonaPostulanteHome personaPostulanteHome;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "postulaciones";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	

	public void setPostulacionIdPostulacion(Long id) {
		setId(id);
	}

	public Long getPostulacionIdPostulacion() {
		return (Long) getId();
	}

	@Override
	protected Postulacion createInstance() {
		Postulacion postulacion = new Postulacion();
		return postulacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
		PersonaPostulante personaPostulante = personaPostulanteHome
				.getDefinedInstance();
		if (personaPostulante != null) {
			getInstance().setPersonaPostulante(personaPostulante);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		if (getInstance().getPersonaPostulante() == null)
			return false;
		return true;
	}

	
	@Override
	public String persist() {
		getInstance().setFechaPostulacion(new Date());
		getInstance().setUsuPostulacion(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	public Postulacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PostulacionAdjuntos> getPostulacionAdjuntoses() {
		return getInstance() == null ? null
				: new ArrayList<PostulacionAdjuntos>(getInstance()
						.getPostulacionAdjuntoses());
	}

	public List<EvalDocumentalCab> getEvalDocumentalCabs() {
		return getInstance() == null ? null : new ArrayList<EvalDocumentalCab>(
				getInstance().getEvalDocumentalCabs());
	}

}
