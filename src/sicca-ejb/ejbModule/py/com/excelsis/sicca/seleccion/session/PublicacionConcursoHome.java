package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.ConcursoHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("publicacionConcursoHome")
public class PublicacionConcursoHome extends EntityHome<PublicacionConcurso> {

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	
	@Override
	protected PublicacionConcurso loadInstance() {
		PublicacionConcurso o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "publicacionConcursos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	@In(create = true)
	ConcursoHome concursoHome;

	public void setPublicacionConcursoIdPublicacionConcurso(Long id) {
		setId(id);
	}

	public Long getPublicacionConcursoIdPublicacionConcurso() {
		return (Long) getId();
	}

	@Override
	protected PublicacionConcurso createInstance() {
		PublicacionConcurso publicacionConcurso = new PublicacionConcurso();
		return publicacionConcurso;
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
		
		getInstance().setFechaSolicitud(new Date());
			getInstance().setUsuSolicitud(usuarioLogueado.getCodigoUsuario());		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
	

	public boolean isWired() {
		if (getInstance().getConcurso() == null)
			return false;
		return true;
	}

	public PublicacionConcurso getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PublicacionConcursoCab> getPublicacionConcursoCabs() {
		return getInstance() == null ? null
				: new ArrayList<PublicacionConcursoCab>(getInstance()
						.getPublicacionConcursoCabs());
	}

}
