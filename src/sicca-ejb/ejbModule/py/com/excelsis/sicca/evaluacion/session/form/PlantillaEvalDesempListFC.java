package py.com.excelsis.sicca.evaluacion.session.form;

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

import py.com.excelsis.sicca.entity.PlantillaEval;
import py.com.excelsis.sicca.evaluacion.session.PlantillaEvalList;
import py.com.excelsis.sicca.seguridad.entity.Usuario;

import py.com.excelsis.sicca.session.util.NivelEntidadOeeUtil;
import py.com.excelsis.sicca.session.util.ReferenciasUtilFormController;
import py.com.excelsis.sicca.session.util.SeguridadUtilFormController;
import py.com.excelsis.sicca.session.util.SeleccionUtilFormController;
import py.com.excelsis.sicca.util.GrupoPuestosController;
import py.com.excelsis.sicca.util.JpaResourceBean;

@Scope(ScopeType.CONVERSATION)
@Name("plantillaEvalDesempListFC")
public class PlantillaEvalDesempListFC {

	@In
	StatusMessages statusMessages;
	@In(create = true)
	JpaResourceBean jpaResourceBean;
	@In(value = "entityManager")
	EntityManager em;
	@In(required = false)
	Usuario usuarioLogueado;
	@In(create = true)
	PlantillaEvalList plantillaEvalList;
	@In(create = true)
	SeleccionUtilFormController seleccionUtilFormController;
	@In(create = true)
	ReferenciasUtilFormController referenciasUtilFormController;
	@In(required = false, create = true)
	GrupoPuestosController grupoPuestosController;
	@In(required = false, create = true)
	NivelEntidadOeeUtil nivelEntidadOeeUtil;
	@In(create = true)
	SeguridadUtilFormController seguridadUtilFormController;

	private Long idMetodologia;
	private PlantillaEval plantillaEval = new PlantillaEval();
	private Estado estado = Estado.ACTIVO;
	
	private List<PlantillaEval> listaPlantillaEval = new ArrayList<PlantillaEval>();

	public void init() {
		limpiar();
		search();

	}
	
	private void limpiar(){
		estado = Estado.ACTIVO;
		idMetodologia = null;
		plantillaEval = new PlantillaEval();
	}
	
	public void search() {
		
		if (plantillaEval.getNombre() != null
				&& !plantillaEval.getNombre().trim().isEmpty())
			plantillaEvalList.getPlantillaEval().setNombre(
					plantillaEval.getNombre());
		if (estado != null && estado.getId() != null)
			plantillaEvalList.setEstado(estado);
		if(idMetodologia != null)
			plantillaEvalList.setIdMetodologia(idMetodologia);
		listaPlantillaEval = plantillaEvalList.listaResultados();
	}
	
	public void searchAll(){
		limpiar();
		listaPlantillaEval = plantillaEvalList.limpiar();
	}

	public Long getIdMetodologia() {
		return idMetodologia;
	}

	public void setIdMetodologia(Long idMetodologia) {
		this.idMetodologia = idMetodologia;
	}

	public PlantillaEval getPlantillaEval() {
		return plantillaEval;
	}

	public void setPlantillaEval(PlantillaEval plantillaEval) {
		this.plantillaEval = plantillaEval;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public List<PlantillaEval> getListaPlantillaEval() {
		return listaPlantillaEval;
	}

	public void setListaPlantillaEval(List<PlantillaEval> listaPlantillaEval) {
		this.listaPlantillaEval = listaPlantillaEval;
	}
	
	

}
