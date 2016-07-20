package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("actividadProcesoHome")
public class ActividadProcesoHome extends EntityHome<ActividadProceso> {

	@In(create = true)
	ProcesoHome procesoHome;
	@In(create = true)
	ActividadHome actividadHome;

	public void setActividadProcesoIdActividadProceso(Long id) {
		setId(id);
	}

	public Long getActividadProcesoIdActividadProceso() {
		return (Long) getId();
	}

	@Override
	protected ActividadProceso createInstance() {
		ActividadProceso actividadProceso = new ActividadProceso();
		return actividadProceso;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Proceso proceso = procesoHome.getDefinedInstance();
		if (proceso != null) {
			getInstance().setProceso(proceso);
		}
		Actividad actividad = actividadHome.getDefinedInstance();
		if (actividad != null) {
			getInstance().setActividad(actividad);
		}
	}

	public boolean isWired() {
		if (getInstance().getProceso() == null)
			return false;
		if (getInstance().getActividad() == null)
			return false;
		return true;
	}

	public ActividadProceso getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ProcActividadRol> getProcActividadRols() {
		return getInstance() == null ? null : new ArrayList<ProcActividadRol>(
				getInstance().getProcActividadRols());
	}

}
