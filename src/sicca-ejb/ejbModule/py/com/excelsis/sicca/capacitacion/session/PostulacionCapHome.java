package py.com.excelsis.sicca.capacitacion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

@Name("postulacionCapHome")
public class PostulacionCapHome extends EntityHome<PostulacionCap> {
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "postulacionCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};

	public void setPostulacionCapIdPostulacion(Long id) {
		setId(id);
	}

	public Long getPostulacionCapIdPostulacion() {
		return (Long) getId();
	}

	@Override
	protected PostulacionCap createInstance() {
		PostulacionCap postulacionCap = new PostulacionCap();
		return postulacionCap;
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

	public PostulacionCap getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PostulacionCapAdj> getPostulacionCapAdjs() {
		return getInstance() == null ? null : new ArrayList<PostulacionCapAdj>(
				getInstance().getPostulacionCapAdjs());
	}
	
	@Override
	public String persist() {
		
		getInstance().setFechaFicha(new Date());
			getInstance().setUsuFicha(usuarioLogueado.getCodigoUsuario());		
		
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
			getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());	
		
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}

}
