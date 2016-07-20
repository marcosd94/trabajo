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

@Name("publicacionConcursoCabHome")
public class PublicacionConcursoCabHome extends
		EntityHome<PublicacionConcursoCab> {

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	
	@Override
	protected PublicacionConcursoCab loadInstance() {
		PublicacionConcursoCab o = super.loadInstance();
		return o;
	}
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "publicacionConcursoCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setPublicacionConcursoCabIdPublicacionConc(Long id) {
		setId(id);
	}

	public Long getPublicacionConcursoCabIdPublicacionConc() {
		return (Long) getId();
	}

	@Override
	protected PublicacionConcursoCab createInstance() {
		PublicacionConcursoCab publicacionConcursoCab = new PublicacionConcursoCab();
		return publicacionConcursoCab;
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
	}

	public boolean isWired() {
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		return true;
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
	

	public PublicacionConcursoCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PublicacionConcursoDet> getPublicacionConcursoDets() {
		return getInstance() == null ? null
				: new ArrayList<PublicacionConcursoDet>(getInstance()
						.getPublicacionConcursoDets());
	}

}
