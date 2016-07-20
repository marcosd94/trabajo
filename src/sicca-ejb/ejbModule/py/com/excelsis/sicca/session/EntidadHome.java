package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("entidadHome")
public class EntidadHome extends EntityHome<Entidad> {

	@In(create = true)
	SinEntidadHome sinEntidadHome;
	@In(create = true)
	OficinaHome oficinaHome;

	public void setEntidadIdEntidad(Long id) {
		setId(id);
	}

	public Long getEntidadIdEntidad() {
		return (Long) getId();
	}

	@Override
	protected Entidad createInstance() {
		Entidad entidad = new Entidad();
		return entidad;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SinEntidad sinEntidad = sinEntidadHome.getDefinedInstance();
		if (sinEntidad != null) {
			getInstance().setSinEntidad(sinEntidad);
		}
		Oficina oficina = oficinaHome.getDefinedInstance();
		if (oficina != null) {
			getInstance().setOficina(oficina);
		}
	}

	public boolean isWired() {
		return true;
	}

	public Entidad getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<ConfiguracionUoCab> getConfiguracionUoCabs() {
		return getInstance() == null ? null
				: new ArrayList<ConfiguracionUoCab>(getInstance()
						.getConfiguracionUoCabs());
	}

}
