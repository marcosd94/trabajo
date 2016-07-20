package py.com.excelsis.sicca.seleccion.session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

import py.com.excelsis.sicca.entity.MemoHomologacion;
import py.com.excelsis.sicca.entity.PlantillaMemoHomologacion;
import py.com.excelsis.sicca.entity.Tags;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.AppHelper;

@Name("plantillaMemoHomologacionHome")
public class PlantillaMemoHomologacionHome extends EntityHome<PlantillaMemoHomologacion> {
	@In(required = false)
	Usuario usuarioLogueado;

	public static final String CONTEXT_VAR_NAME = "plantillaMemoHomologacions";
	public static final String[] CONTEXT_VAR_NAMES = { CONTEXT_VAR_NAME,
		CONTEXT_VAR_NAME + "SelectItems" };
	private List<Tags> tagList = new ArrayList<Tags>();
	private String fechaActual;

	public void setPlantillaMemoHomologacionIdPlantillaMemoHomologacion(Long id) {
		setId(id);
	}

	public Long getPlantillaMemoHomologacionIdPlantillaMemoHomologacion() {
		return (Long) getId();
	}

	@Override
	protected PlantillaMemoHomologacion createInstance() {
		PlantillaMemoHomologacion plantillaMemoHomologacion = new PlantillaMemoHomologacion();
		return plantillaMemoHomologacion;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}

	}

	public void wire() {
		if (!isIdDefined()) {
			getInstance().setActivo(true);
		}
		findTags();
	}

	public boolean isWired() {
		return true;
	}

	public PlantillaMemoHomologacion getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<MemoHomologacion> getMemoHomologacions() {
		return getInstance() == null ? null
			: new ArrayList<MemoHomologacion>(getInstance().getMemoHomologacions());
	}

	@SuppressWarnings("unchecked")
	private void findTags() {
		tagList =
			getEntityManager().createQuery("Select t from Tags t " + " order by t.descripcion ").getResultList();

	}

	// getter setter
	public List<Tags> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tags> tagList) {
		this.tagList = tagList;
	}

	public String getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(String fechaActual) {
		this.fechaActual = fechaActual;
	}

	@Override
	public String persist() {
		getInstance().setDescripcion(getInstance().getDescripcion().trim());
		getInstance().setFechaAlta(new Date());
		getInstance().setUsuAlta(usuarioLogueado.getCodigoUsuario().toUpperCase());
		return AppHelper.removeFromContext("persist", super.persist(), CONTEXT_VAR_NAMES, getEventContext());
	}

	@Override
	public String update() {
		getInstance().setDescripcion(getInstance().getDescripcion().trim());
		getInstance().setFechaMod(new Date());
		getInstance().setUsuMod(usuarioLogueado.getCodigoUsuario().toUpperCase());
		return AppHelper.removeFromContext("updated", super.update(), CONTEXT_VAR_NAMES, getEventContext());
	}
}
