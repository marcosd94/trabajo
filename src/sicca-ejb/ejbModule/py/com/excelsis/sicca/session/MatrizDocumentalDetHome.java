package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocumentalDet;
import py.com.excelsis.sicca.entity.MatrizDocumentalEnc;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("matrizDocumentalDetHome")
public class MatrizDocumentalDetHome extends EntityHome<MatrizDocumentalDet> {

	@In(create = true)
	MatrizDocumentalEncHome matrizDocumentalEncHome;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setMatrizDocumentalDetIdMatrizDocumentalDet(Long id) {
		setId(id);
	}

	public Long getMatrizDocumentalDetIdMatrizDocumentalDet() {
		return (Long) getId();
	}

	@Override
	protected MatrizDocumentalDet createInstance() {
		MatrizDocumentalDet matrizDocumentalDet = new MatrizDocumentalDet();
		return matrizDocumentalDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		MatrizDocumentalEnc matrizDocumentalEnc = matrizDocumentalEncHome
				.getDefinedInstance();
		if (matrizDocumentalEnc != null) {
			getInstance().setMatrizDocumentalEnc(matrizDocumentalEnc);
		}
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getMatrizDocumentalEnc() == null)
			return false;
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public MatrizDocumentalDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
