package py.com.excelsis.sicca.session;

import java.util.Date;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessages;

@Name("grupoConceptoPagoHome")
public class GrupoConceptoPagoHome extends EntityHome<GrupoConceptoPago> {

	@In
	FacesMessages facesMessages;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	@In
	StatusMessages statusMessages;
	
	//Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "grupoConceptoPagos";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	
	
	@Override
	protected GrupoConceptoPago loadInstance() {
		GrupoConceptoPago o = super.loadInstance();
		return o;
	}
	
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setGrupoConceptoPagoIdGrupoConceptoPago(Long id) {
		setId(id);
	}

	public Long getGrupoConceptoPagoIdGrupoConceptoPago() {
		return (Long) getId();
	}

	@Override
	protected GrupoConceptoPago createInstance() {
		GrupoConceptoPago grupoConceptoPago = new GrupoConceptoPago();
		return grupoConceptoPago;
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

	public GrupoConceptoPago getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
