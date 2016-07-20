package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("postulacionAdjuntosHome")
public class PostulacionAdjuntosHome extends EntityHome<PostulacionAdjuntos> {

	@In(create = true)
	PostulacionHome postulacionHome;

	public void setPostulacionAdjuntosIdPostulacionAdjuntos(Long id) {
		setId(id);
	}

	public Long getPostulacionAdjuntosIdPostulacionAdjuntos() {
		return (Long) getId();
	}

	@Override
	protected PostulacionAdjuntos createInstance() {
		PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();
		return postulacionAdjuntos;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Postulacion postulacion = postulacionHome.getDefinedInstance();
		if (postulacion != null) {
			getInstance().setPostulacion(postulacion);
		}
	}

	public boolean isWired() {
		if (getInstance().getPostulacion() == null)
			return false;
		return true;
	}

	public PostulacionAdjuntos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
