package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityHome;

@Name("comisionEvalHome")
public class ComisionEvalHome extends EntityHome<ComisionEval> {

	@In(create = true)
	EvaluacionDesempenoHome evaluacionDesempenoHome;

	// Value holders for selectItems if exists
	public static final String CONTEXT_VAR_NAME = "comisionEvals";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	
	

	@SuppressWarnings("unchecked")
	public List<ComisionEval> getComisionEvals() {
		try {
			return getEntityManager().createQuery(" SELECT o FROM "
				+ ComisionEval.class.getName() + " o "
				+ "WHERE o.activo = true ORDER BY o.nombre").getResultList();
		} catch (Exception ex) {
			return new Vector<ComisionEval>();
		}
	}

	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getComisionEvalSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		si.add(new SelectItem(null, SeamResourceBundle.getBundle().getString("opt_select_one")));
		for (ComisionEval o : getComisionEvals())
			si.add(new SelectItem(o.getIdComisionEval(), "" + o.getNombre()));
		return si;
	}
	public void setComisionEvalIdComisionEval(Long id) {
		setId(id);
	}

	public Long getComisionEvalIdComisionEval() {
		return (Long) getId();
	}

	@Override
	protected ComisionEval createInstance() {
		ComisionEval comisionEval = new ComisionEval();
		return comisionEval;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		EvaluacionDesempeno evaluacionDesempeno = evaluacionDesempenoHome
				.getDefinedInstance();
		if (evaluacionDesempeno != null) {
			getInstance().setEvaluacionDesempeno(evaluacionDesempeno);
		}
	}

	public boolean isWired() {
		if (getInstance().getEvaluacionDesempeno() == null)
			return false;
		return true;
	}

	public ComisionEval getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<GruposEvaluacion> getGruposEvaluacions() {
		return getInstance() == null ? null : new ArrayList<GruposEvaluacion>(
				getInstance().getGruposEvaluacions());
	}

}
