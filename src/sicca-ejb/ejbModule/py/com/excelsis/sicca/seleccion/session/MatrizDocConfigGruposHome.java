package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.entity.MatrizDocConfigGrupos;
import py.com.excelsis.sicca.session.DatosEspecificosHome;

@Name("matrizDocConfigGruposHome")
public class MatrizDocConfigGruposHome extends EntityHome<MatrizDocConfigGrupos> {

	@In(create = true)
	MatrizDocConfigEncHome matrizDocConfigEncHome;
	

	public void setMatrizDocConfigGruposIdMatrizDocConfigDet(Long id) {
		setId(id);
	}

	public Long getMatrizDocConfigGruposIdMatrizDocConfigDet() {
		return (Long) getId();
	}

	@Override
	protected MatrizDocConfigGrupos createInstance() {
		MatrizDocConfigGrupos matrizDocConfigGrupos = new MatrizDocConfigGrupos();
		return matrizDocConfigGrupos;
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
		
	}

	public boolean isWired() {
//		if (getInstance().getMatrizDocConfigEnc() == null)
//			return false;
//		if (getInstance().getDatosEspecificos() == null)
//			return false;
		return true;
	}

	public MatrizDocConfigGrupos getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
