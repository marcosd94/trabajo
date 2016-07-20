package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("evalReferencialPostulanteHome")
public class EvalReferencialPostulanteHome extends
		EntityHome<EvalReferencialPostulante> {

	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	PostulacionHome postulacionHome;

	public void setEvalReferencialPostulanteIdEvalReferencialPostulante(Long id) {
		setId(id);
	}

	public Long getEvalReferencialPostulanteIdEvalReferencialPostulante() {
		return (Long) getId();
	}

	@Override
	protected EvalReferencialPostulante createInstance() {
		EvalReferencialPostulante evalReferencialPostulante = new EvalReferencialPostulante();
		return evalReferencialPostulante;
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
		Postulacion postulacion = postulacionHome.getDefinedInstance();
		if (postulacion != null) {
			getInstance().setPostulacion(postulacion);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		if (getInstance().getPostulacion() == null)
			return false;
		return true;
	}

	public EvalReferencialPostulante getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
