package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("evalDocumentalDetHome")
public class EvalDocumentalDetHome extends EntityHome<EvalDocumentalDet> {

	@In(create = true)
	MatrizDocConfigDetHome matrizDocConfigDetHome;
	@In(create = true)
	EvalDocumentalCabHome evalDocumentalCabHome;

	public void setEvalDocumentalDetIdEvalDocumentalDet(Long id) {
		setId(id);
	}

	public Long getEvalDocumentalDetIdEvalDocumentalDet() {
		return (Long) getId();
	}

	@Override
	protected EvalDocumentalDet createInstance() {
		EvalDocumentalDet evalDocumentalDet = new EvalDocumentalDet();
		return evalDocumentalDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		MatrizDocConfigDet matrizDocConfigDet = matrizDocConfigDetHome
				.getDefinedInstance();
		if (matrizDocConfigDet != null) {
			getInstance().setMatrizDocConfigDet(matrizDocConfigDet);
		}
		EvalDocumentalCab evalDocumentalCab = evalDocumentalCabHome
				.getDefinedInstance();
		if (evalDocumentalCab != null) {
			getInstance().setEvalDocumentalCab(evalDocumentalCab);
		}
	}

	public boolean isWired() {
		if (getInstance().getMatrizDocConfigDet() == null)
			return false;
		if (getInstance().getEvalDocumentalCab() == null)
			return false;
		return true;
	}

	public EvalDocumentalDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
