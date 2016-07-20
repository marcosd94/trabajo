package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.MatrizDocConfigDet;
import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.DatosEspecificosHome;

@Name("matrizDocConfigEncHome")
public class MatrizDocConfigEncHome extends EntityHome<MatrizDocConfigEnc> {

	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;

	public void setMatrizDocConfigEncIdMatrizDocConfigEnc(Long id) {
		setId(id);
	}

	public Long getMatrizDocConfigEncIdMatrizDocConfigEnc() {
		return (Long) getId();
	}

	@Override
	protected MatrizDocConfigEnc createInstance() {
		MatrizDocConfigEnc matrizDocConfigEnc = new MatrizDocConfigEnc();
		return matrizDocConfigEnc;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
	}

	public boolean isWired() {
		if (getInstance().getDatosEspecificos() == null)
			return false;
		if (getInstance().getConcursoPuestoAgr() == null)
			return false;
		return true;
	}

	public MatrizDocConfigEnc getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<MatrizDocConfigDet> getMatrizDocConfigDets() {
		return getInstance() == null ? null
				: new ArrayList<MatrizDocConfigDet>(getInstance()
						.getMatrizDocConfigDets());
	}

}
