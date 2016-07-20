package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.EmpresaPersonaHome;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("evalDocumentalComisHome")
public class EvalDocumentalComisHome extends EntityHome<EvalDocumentalComis> {

	@In(create = true)
	ComisionSeleccionDetHome comisionSeleccionDetHome;
	@In(create = true)
	EmpresaPersonaHome empresaPersonaHome;
	@In(create = true)
	EvalDocumentalCabHome evalDocumentalCabHome;

	public void setEvalDocumentalComisIdEvalDocumentalComis(Long id) {
		setId(id);
	}

	public Long getEvalDocumentalComisIdEvalDocumentalComis() {
		return (Long) getId();
	}

	@Override
	protected EvalDocumentalComis createInstance() {
		EvalDocumentalComis evalDocumentalComis = new EvalDocumentalComis();
		return evalDocumentalComis;
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
		if (comisionSeleccionDet != null) {
			getInstance().setComisionSeleccionDet(comisionSeleccionDet);
		}
		EmpresaPersona empresaPersona = empresaPersonaHome.getDefinedInstance();
		if (empresaPersona != null) {
			getInstance().setEmpresaPersona(empresaPersona);
		}
		EvalDocumentalCab evalDocumentalCab = evalDocumentalCabHome
				.getDefinedInstance();
		if (evalDocumentalCab != null) {
			getInstance().setEvalDocumentalCab(evalDocumentalCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getEvalDocumentalCab() == null)
			return false;
		return true;
	}

	public EvalDocumentalComis getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
