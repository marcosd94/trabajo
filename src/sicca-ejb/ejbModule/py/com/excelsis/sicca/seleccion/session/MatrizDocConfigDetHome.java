package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.session.DatosEspecificosHome;

@Name("matrizDocConfigDetHome")
public class MatrizDocConfigDetHome extends EntityHome<MatrizDocConfigDet> {

	@In(create = true)
	MatrizDocConfigEncHome matrizDocConfigEncHome;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setMatrizDocConfigDetIdMatrizDocConfigDet(Long id) {
		setId(id);
	}

	public Long getMatrizDocConfigDetIdMatrizDocConfigDet() {
		return (Long) getId();
	}

	@Override
	protected MatrizDocConfigDet createInstance() {
		MatrizDocConfigDet matrizDocConfigDet = new MatrizDocConfigDet();
		return matrizDocConfigDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		MatrizDocConfigEnc matrizDocConfigEnc = matrizDocConfigEncHome
				.getDefinedInstance();
		if (matrizDocConfigEnc != null) {
			getInstance().setMatrizDocConfigEnc(matrizDocConfigEnc);
		}
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
//		if (getInstance().getMatrizDocConfigEnc() == null)
//			return false;
//		if (getInstance().getDatosEspecificos() == null)
//			return false;
		return true;
	}

	public MatrizDocConfigDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
