package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;
import py.com.excelsis.sicca.entity.Concurso;
import py.com.excelsis.sicca.session.ConcursoHome;

@Name("comisionSeleccionCabHome")
public class ComisionSeleccionCabHome extends EntityHome<ComisionSeleccionCab> {

	@In(create = true)
	ConcursoHome concursoHome;

	public void setComisionSeleccionCabIdComisionSel(Long id) {
		setId(id);
	}

	public Long getComisionSeleccionCabIdComisionSel() {
		return (Long) getId();
	}

	@Override
	protected ComisionSeleccionCab createInstance() {
		ComisionSeleccionCab comisionSeleccionCab = new ComisionSeleccionCab();
		return comisionSeleccionCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		Concurso concurso = concursoHome.getDefinedInstance();
		if (concurso != null) {
			getInstance().setConcurso(concurso);
		}
	}

	public boolean isWired() {
		if (getInstance().getConcurso() == null)
			return false;
		return true;
	}

	public ComisionSeleccionCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ComisionSeleccionDet> getComisionSeleccionDets() {
		return getInstance() == null ? null
				: new ArrayList<ComisionSeleccionDet>(getInstance()
						.getComisionSeleccionDets());
	}

}
