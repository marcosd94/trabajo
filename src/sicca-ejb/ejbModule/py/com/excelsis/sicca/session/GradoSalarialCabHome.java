package py.com.excelsis.sicca.session;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.entity.GradoSalarialCab;
import py.com.excelsis.sicca.entity.TipoCce;

@Name("gradoSalarialCabHome")
public class GradoSalarialCabHome extends EntityHome<GradoSalarialCab> {

	@In(create = true)
	TipoCceHome tipoCceHome;
	
	public void setGradoSalarialCabIdGradoSalarialCab(Long id) {
		setId(id);
	}

	public Long getGradoSalarialCabIdGradoSalarialCab() {
		return (Long) getId();
	}

	@Override
	protected GradoSalarialCab createInstance() {
		GradoSalarialCab gradoSalarialCab = new GradoSalarialCab();
		return gradoSalarialCab;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
//		TipoCce tipoCce = tipoCceHome.getDefinedInstance();
//		if (tipoCce != null) {
//			getInstance().setTipoCce(tipoCce);
//		}
	}

	public boolean isWired() {
//		if (getInstance().getTipoCce() == null)
//			return false;
		return true;
	}

	public GradoSalarialCab getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<GradoSalarial> getGradoSalarials() {
		return getInstance() == null ? null : new ArrayList<GradoSalarial>(
				getInstance().getGradoSalarials());
	}

}
