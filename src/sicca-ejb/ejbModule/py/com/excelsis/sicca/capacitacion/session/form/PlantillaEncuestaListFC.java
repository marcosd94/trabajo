package py.com.excelsis.sicca.capacitacion.session.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import py.com.excelsis.sicca.capacitacion.session.PlantillaEncuestaList;
import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.PlantillaEncuesta;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("plantillaEncuestaListFC")
public class PlantillaEncuestaListFC implements Serializable {
	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(create = true)
	PlantillaEncuestaList plantillaEncuestaList;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Estado estado;
	private PlantillaEncuesta plantillaEncuesta = new PlantillaEncuesta();
	private List<PlantillaEncuesta> listaPlantilla;

	public void init() {
		searchAll();

	}

	private void limpiar() {
		estado = Estado.ACTIVO;
		plantillaEncuesta = new PlantillaEncuesta();
	}

	public void searchAll() {
		limpiar();
		search();
	}
	
	public void setear(){
		listaPlantilla = new ArrayList<PlantillaEncuesta>();
		limpiar();
		listaPlantilla = plantillaEncuestaList.limpiar();
	}

	public void search() {
		listaPlantilla = new ArrayList<PlantillaEncuesta>();
		plantillaEncuestaList.setEstado(estado);
		if (nivelEntidadOeeUtil.getIdConfigCab() != null)
			plantillaEncuestaList
					.setIdOee(nivelEntidadOeeUtil.getIdConfigCab());
		if (plantillaEncuesta.getNombre() != null
				&& !plantillaEncuesta.getNombre().trim().isEmpty())
			plantillaEncuestaList.getPlantillaEncuesta().setNombre(
					plantillaEncuesta.getNombre());
		listaPlantilla = plantillaEncuestaList.listaResultados();
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<PlantillaEncuesta> getListaPlantilla() {
		return listaPlantilla;
	}

	public void setListaPlantilla(List<PlantillaEncuesta> listaPlantilla) {
		this.listaPlantilla = listaPlantilla;
	}

	public PlantillaEncuesta getPlantillaEncuesta() {
		return plantillaEncuesta;
	}

	public void setPlantillaEncuesta(PlantillaEncuesta plantillaEncuesta) {
		this.plantillaEncuesta = plantillaEncuesta;
	}

}
