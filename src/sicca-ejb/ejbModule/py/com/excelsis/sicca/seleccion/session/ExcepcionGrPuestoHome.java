package py.com.excelsis.sicca.seleccion.session;


import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.session.ConcursoPuestoAgrHome;
import py.com.excelsis.sicca.session.PlantaCargoDetHome;

@Name("excepcionGrPuestoHome")
public class ExcepcionGrPuestoHome extends EntityHome<ExcepcionGrPuesto> {

	@In(create = true)
	PlantaCargoDetHome plantaCargoDetHome;
	@In(create = true)
	ConcursoPuestoAgrHome concursoPuestoAgrHome;
	@In(create = true)
	ExcepcionHome excepcionHome;

	public void setExcepcionGrPuestoIdExcepcionGrPuesto(Long id) {
		setId(id);
	}

	public Long getExcepcionGrPuestoIdExcepcionGrPuesto() {
		return (Long) getId();
	}

	@Override
	protected ExcepcionGrPuesto createInstance() {
		ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();
		return excepcionGrPuesto;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		PlantaCargoDet plantaCargoDet = plantaCargoDetHome.getDefinedInstance();
		if (plantaCargoDet != null) {
			getInstance().setPlantaCargoDet(plantaCargoDet);
		}
		ConcursoPuestoAgr concursoPuestoAgr = concursoPuestoAgrHome
				.getDefinedInstance();
		if (concursoPuestoAgr != null) {
			getInstance().setConcursoPuestoAgr(concursoPuestoAgr);
		}
		Excepcion excepcion = excepcionHome.getDefinedInstance();
		if (excepcion != null) {
			getInstance().setExcepcion(excepcion);
		}
	}

	public boolean isWired() {
		if (getInstance().getExcepcion() == null)
			return false;
		return true;
	}

	public ExcepcionGrPuesto getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
