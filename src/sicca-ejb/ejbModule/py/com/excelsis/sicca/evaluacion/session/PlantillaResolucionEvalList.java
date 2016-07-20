package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.PlantillaResolucionEval;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("plantillaResolucionEvalList")
public class PlantillaResolucionEvalList extends
		EntityQuery<PlantillaResolucionEval> {

	private static final String EJBQL = "select plantillaResolucionEval from PlantillaResolucionEval plantillaResolucionEval";

	private static final String[] RESTRICTIONS = {
			"lower(plantillaResolucionEval.descripcion) like concat('%',lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.descripcion},'%')))",
			"lower(plantillaResolucionEval.titulo) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.titulo},'%'))",
			"lower(plantillaResolucionEval.visto) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.visto},'%'))",
			"lower(plantillaResolucionEval.considerando) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.considerando},'%'))",
			"lower(plantillaResolucionEval.porTanto) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.porTanto},'%'))",
			"lower(plantillaResolucionEval.resuelve) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.resuelve},'%'))",
			"lower(plantillaResolucionEval.firma) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.firma},'%'))",
			"lower(plantillaResolucionEval.cargo) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.cargo},'%'))",
			"lower(plantillaResolucionEval.institucion) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.institucion},'%'))",
			"lower(plantillaResolucionEval.usuAlta) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.usuAlta},'%'))",
			"lower(plantillaResolucionEval.usuMod) like lower(concat(#{plantillaResolucionEvalList.plantillaResolucionEval.usuMod},'%'))", 
			"plantillaResolucionEval.activo=#{plantillaResolucionEvalList.estado.valor}",
	};

	private PlantillaResolucionEval plantillaResolucionEval = new PlantillaResolucionEval();
	private static final String ORDER = "plantillaResolucionEval.descripcion";
	private Estado estado= Estado.ACTIVO;
	public PlantillaResolucionEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantillaResolucionEval> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<PlantillaResolucionEval> limpiar() {
		plantillaResolucionEval= new PlantillaResolucionEval();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public PlantillaResolucionEval getPlantillaResolucionEval() {
		return plantillaResolucionEval;
	}


	public Estado getEstado() {
		return estado;
	}


	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
}
