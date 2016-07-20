package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("bandejaEvaluacionHome")
public class BandejaEvaluacionHome extends EntityHome<BandejaEvaluacion> {

	public void setBandejaEvaluacionIdBandejaEvaluacion(Long id) {
		setId(id);
	}

	public Long getBandejaEvaluacionIdBandejaEvaluacion() {
		return (Long) getId();
	}
	@Override
	protected BandejaEvaluacion createInstance() {
		BandejaEvaluacion bandejaEvaluacion = new BandejaEvaluacion();
		return bandejaEvaluacion;
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

	public BandejaEvaluacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	

}
