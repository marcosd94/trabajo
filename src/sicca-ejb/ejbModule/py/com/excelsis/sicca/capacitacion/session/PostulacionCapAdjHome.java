package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.PostulacionCap;
import py.com.excelsis.sicca.entity.PostulacionCapAdj;

@Name("postulacionCapAdjHome")
public class PostulacionCapAdjHome extends EntityHome<PostulacionCapAdj> {

	@In(create = true)
	PostulacionCapHome postulacionCapHome;

	public void setPostulacionCapAdjIdPostulacionAdj(Long id) {
		setId(id);
	}

	public Long getPostulacionCapAdjIdPostulacionAdj() {
		return (Long) getId();
	}

	@Override
	protected PostulacionCapAdj createInstance() {
		PostulacionCapAdj postulacionCapAdj = new PostulacionCapAdj();
		return postulacionCapAdj;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PostulacionCap postulacionCap = postulacionCapHome.getDefinedInstance();
		if (postulacionCap != null) {
			getInstance().setPostulacionCap(postulacionCap);
		}
	}

	public boolean isWired() {
		if (getInstance().getPostulacionCap() == null)
			return false;
		return true;
	}

	public PostulacionCapAdj getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
