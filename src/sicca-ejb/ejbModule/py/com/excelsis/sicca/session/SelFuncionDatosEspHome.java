package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.SelFuncionDatosEsp;
import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.session.FuncionHome;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("selFuncionDatosEspHome")
public class SelFuncionDatosEspHome extends EntityHome<SelFuncionDatosEsp> {

	@In(create = true)
	FuncionHome funcionHome;
	@In(create = true)
	DatosEspecificosHome datosEspecificosHome;

	public void setSelFuncionDatosEspIdFuncionDatosEsp(Long id) {
		setId(id);
	}

	public Long getSelFuncionDatosEspIdFuncionDatosEsp() {
		return (Long) getId();
	}

	@Override
	protected SelFuncionDatosEsp createInstance() {
		SelFuncionDatosEsp selFuncionDatosEsp = new SelFuncionDatosEsp();
		return selFuncionDatosEsp;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Funcion funcion = funcionHome.getDefinedInstance();
		if (funcion != null) {
			getInstance().setFuncion(funcion);
		}
		DatosEspecificos datosEspecificos = datosEspecificosHome
				.getDefinedInstance();
		if (datosEspecificos != null) {
			getInstance().setDatosEspecificos(datosEspecificos);
		}
	}

	public boolean isWired() {
		if (getInstance().getFuncion() == null)
			return false;
		if (getInstance().getDatosEspecificos() == null)
			return false;
		return true;
	}

	public SelFuncionDatosEsp getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
