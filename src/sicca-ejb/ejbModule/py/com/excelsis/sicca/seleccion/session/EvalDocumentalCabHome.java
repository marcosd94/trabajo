package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;
import py.com.excelsis.sicca.session.EmpresaPersonaHome;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("evalDocumentalCabHome")
public class EvalDocumentalCabHome extends EntityHome<EvalDocumentalCab> {

	@In(create = true)
	ComisionSeleccionDetHome comisionSeleccionDetHome;
	@In(create = true)
	EmpresaPersonaHome empresaPersonaHome;
	@In(create = true)
	PostulacionHome postulacionHome;
	
	@In(required = false)
	Usuario usuarioLogueado;
	
	public static final String CONTEXT_VAR_NAME = "evalDocumentalCabs";
	public static final String[] CONTEXT_VAR_NAMES = {CONTEXT_VAR_NAME, CONTEXT_VAR_NAME + "SelectItems"};
	

	public void setEvalDocumentalCabIdEvalDocumentalCab(Long id) {
		setId(id);
	}

	public Long getEvalDocumentalCabIdEvalDocumentalCab() {
		return (Long) getId();
	}

	@Override
	protected EvalDocumentalCab createInstance() {
		EvalDocumentalCab evalDocumentalCab = new EvalDocumentalCab();
		return evalDocumentalCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		ComisionSeleccionDet comisionSeleccionDet = comisionSeleccionDetHome
				.getDefinedInstance();
		
		Postulacion postulacion = postulacionHome.getDefinedInstance();
		if (postulacion != null) {
			getInstance().setPostulacion(postulacion);
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
		if (getInstance().getPostulacion() == null)
			return false;
		return true;
	}

	public EvalDocumentalCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<EvalDocumentalDet> getEvalDocumentalDets() {
		return getInstance() == null ? null : new ArrayList<EvalDocumentalDet>(
				getInstance().getEvalDocumentalDets());
	}

}
