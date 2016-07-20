package py.com.excelsis.sicca.session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.PersistenceException;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.Especialidades;
import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

@Name("plantaCargoDetHome")
public class PlantaCargoDetHome extends EntityHome<PlantaCargoDet> {

	@In
	FacesMessages facesMessages;

	@In(required = false)
	Usuario usuarioLogueado;

	@Override
	protected PlantaCargoDet loadInstance() {
		PlantaCargoDet o = super.loadInstance();
		this.idConfiguracionUoDet = o.getConfiguracionUoDet()
				.getIdConfiguracionUoDet();
		this.idCpt = o.getCpt().getIdCpt();
		this.idOficina = o.getOficina() != null ? o.getOficina().getIdOficina()
				: null;
		return o;
	}

	// Value holders for selectItems if exists
	private Long idConfiguracionUoDet;
	private Long idGradoSalarialActual;
	private Long idGradoSalarial;
	private Long idTipoPuesto;// ahora es idEspecialidades
	private Long idTipoCarrera;
	private Long idCpt;
	private Long idOficina;
	public static final String CONTEXT_VAR_NAME = "plantaCargoDets";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
			CONTEXT_VAR_NAME + "SelectItems" };

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME, scope = ScopeType.EVENT)
	public List<PlantaCargoDet> getPlantaCargoDets() {
		try {
			return getEntityManager().createQuery(
					" select o from " + PlantaCargoDet.class.getName() + " o")
					.getResultList();
		} catch (Exception ex) {
			return new Vector<PlantaCargoDet>();
		}
	}

	@SuppressWarnings("unchecked")
	@Factory(value = CONTEXT_VAR_NAME + "SelectItems", scope = ScopeType.EVENT)
	public List<SelectItem> getPlantaCargoDetSelectItems() {
		List<SelectItem> si = new Vector<SelectItem>();
		for (PlantaCargoDet o : getPlantaCargoDets())
			si.add(new SelectItem(o.getIdPlantaCargoDet(), ""
					+ o.getDescripcion()));
		return si;
	}

	public void setPlantaCargoDetIdPlantaCargoDet(Long id) {
		setId(id);
	}

	public Long getPlantaCargoDetIdPlantaCargoDet() {
		return (Long) getId();
	}

	@Override
	protected PlantaCargoDet createInstance() {
		PlantaCargoDet plantaCargoDet = new PlantaCargoDet();
		return plantaCargoDet;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		try {
			getInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public boolean isWired() {
		return true;
	}

	public PlantaCargoDet getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	@Override
	public String persist() {
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario());
		return AppHelper.removeFromContext("persist", super.persist(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario());

		return AppHelper.removeFromContext("updated", super.update(),
				CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public void setInstance(PlantaCargoDet instance) {
		if (instance != null) {
			super.setId(instance.getId());
		}
		super.setInstance(instance);
	}

	// Public getters and setters if exists

	public Long getIdConfiguracionUoDet() {
		return this.idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
	}

	public Long getIdGradoSalarialActual() {
		return this.idGradoSalarialActual;
	}

	public void setIdGradoSalarialActual(Long idGradoSalarialActual) {
		this.idGradoSalarialActual = idGradoSalarialActual;
	}

	public Long getIdGradoSalarial() {
		return this.idGradoSalarial;
	}

	public void setIdGradoSalarial(Long idGradoSalarial) {
		this.idGradoSalarial = idGradoSalarial;
	}

	public Long getIdTipoPuesto() {
		return this.idTipoPuesto;
	}

	public void setIdTipoPuesto(Long idTipoPuesto) {
		this.idTipoPuesto = idTipoPuesto;
	}

	public Long getIdTipoCarrera() {
		return this.idTipoCarrera;
	}

	public void setIdTipoCarrera(Long idTipoCarrera) {
		this.idTipoCarrera = idTipoCarrera;
	}

	public Long getIdCpt() {
		return this.idCpt;
	}

	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}

	public Long getIdOficina() {
		return this.idOficina;
	}

	public void setIdOficina(Long idOficina) {
		this.idOficina = idOficina;
	}

}
