package py.com.excelsis.sicca.juridicos.session;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Sancion;
import py.com.excelsis.sicca.entity.SumarioCab;
import py.com.excelsis.sicca.entity.SumarioDet;
import py.com.excelsis.sicca.session.EmpleadoPuestoHome;

@Name("sumarioCabHome")
public class SumarioCabHome extends EntityHome<SumarioCab> {

	@In(create = true)
	EmpleadoPuestoHome empleadoPuestoHome;
	@In(create = true)
	SancionHome sancionHome;

	public void setSumarioCabIdSumarioCab(Long id) {
		setId(id);
	}

	public Long getSumarioCabIdSumarioCab() {
		return (Long) getId();
	}

	@Override
	protected SumarioCab createInstance() {
		SumarioCab sumarioCab = new SumarioCab();
		return sumarioCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EmpleadoPuesto empleadoPuesto = empleadoPuestoHome.getDefinedInstance();
		if (empleadoPuesto != null) {
			getInstance().setEmpleadoPuesto(empleadoPuesto);
		}
		Sancion sancionByIdSancionM = sancionHome.getDefinedInstance();
		if (sancionByIdSancionM != null) {
			getInstance().setSancionMai(sancionByIdSancionM);
		}
		Sancion sancionByIdSancionJ = sancionHome.getDefinedInstance();
		if (sancionByIdSancionJ != null) {
			getInstance().setSancionJuez(sancionByIdSancionJ);
		}
	}

	public boolean isWired() {
		if (getInstance().getEmpleadoPuesto() == null)
			return false;
		return true;
	}

	public SumarioCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<SumarioDet> getSumarioDets() {
		return getInstance() == null ? null : new ArrayList<SumarioDet>(
				getInstance().getSumarioDets());
	}

}
