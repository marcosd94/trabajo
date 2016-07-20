package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;

@Name("plantillaEvalHome")
public class PlantillaEvalHome extends EntityHome<PlantillaEval> {

	@In(value = "entityManager")
	EntityManager em;
	public static final String CONTEXT_VAR_NAME = "plantillaEvals";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	
	
	@SuppressWarnings("unchecked")
	public List<PlantillaEval> getPlantillaEvals() {
		try {
			return getEntityManager().createQuery(" SELECT o FROM "
				+ PlantillaEval.class.getName() + " o "
				+ "WHERE o.activo = true ORDER BY o.nombre").getResultList();
		} catch (Exception ex) {
			return new Vector<PlantillaEval>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getPlantillaEvalSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (PlantillaEval o : getPlantillaEvals())
			si.add(new SelectItem(o.getIdPlantillaEval(), "" + o.getNombre()));
		return si;
	}
	
	public void setPlantillaEvalIdPlantillaEval(Long id) {
		setId(id);
	}

	public Long getPlantillaEvalIdPlantillaEval() {
		return (Long) getId();
	}

	@Override
	protected PlantillaEval createInstance() {
		PlantillaEval plantillaEval = new PlantillaEval();
		return plantillaEval;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public PlantillaEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PlantillaEvalDet> getPlantillaEvalDets() {
		return getInstance() == null ? null : new ArrayList<PlantillaEvalDet>(
				getInstance().getPlantillaEvalDets());
	}

}
