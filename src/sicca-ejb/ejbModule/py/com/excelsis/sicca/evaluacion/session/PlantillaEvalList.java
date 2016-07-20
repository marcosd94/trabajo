package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("plantillaEvalList")
public class PlantillaEvalList extends EntityQuery<PlantillaEval> {

	private static final String EJBQL = "select plantillaEval from PlantillaEval plantillaEval";

	private static final String[] RESTRICTIONS = {
		"lower(plantillaEval.nombre) like concat('%',lower(concat(#{plantillaEvalList.plantillaEval.nombre},'%')))",
			"plantillaEval.activo=#{plantillaEvalList.estado.valor}",
			"plantillaEval.datosEspecifMetod.idDatosEspecificos=#{plantillaEvalList.idMetodologia}",};

	private PlantillaEval plantillaEval = new PlantillaEval();
	private Estado estado = Estado.ACTIVO;
	private Long idMetodologia;
	private static final String ORDER = "plantillaEval.nombre";

	public PlantillaEvalList() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantillaEval> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<PlantillaEval> limpiar() {
		plantillaEval = new PlantillaEval();
		estado = Estado.ACTIVO;
		idMetodologia = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public PlantillaEval getPlantillaEval() {
		return plantillaEval;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdMetodologia() {
		return idMetodologia;
	}

	public void setIdMetodologia(Long idMetodologia) {
		this.idMetodologia = idMetodologia;
	}
	
	
}
