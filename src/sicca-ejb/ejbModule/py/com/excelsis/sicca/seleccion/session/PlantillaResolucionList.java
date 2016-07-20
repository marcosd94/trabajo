package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.entity.Resolucion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("plantillaResolucionHomologacionList")
public class PlantillaResolucionList extends
		EntityQuery<PlantillaResolucion> {

	private static final String EJBQL = "select plantillaResolucionHomologacion from PlantillaResolucion plantillaResolucionHomologacion";

	private static final String[] RESTRICTIONS = {
			"lower(plantillaResolucionHomologacion.descripcion) like concat('%',lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.descripcion},'%')))",
			"lower(plantillaResolucionHomologacion.titulo) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.titulo},'%'))",
			"plantillaResolucionHomologacion.activo=#{plantillaResolucionHomologacionList.estado.valor}",
			"lower(plantillaResolucionHomologacion.visto) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.visto},'%'))",
			"lower(plantillaResolucionHomologacion.considerando) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.considerando},'%'))",
			"lower(plantillaResolucionHomologacion.porTanto) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.porTanto},'%'))",
			"lower(plantillaResolucionHomologacion.resuelve) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.resuelve},'%'))",
			"lower(plantillaResolucionHomologacion.firma) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.firma},'%'))",
			"lower(plantillaResolucionHomologacion.cargo) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.cargo},'%'))",
			"lower(plantillaResolucionHomologacion.institucion) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.institucion},'%'))",
			"lower(plantillaResolucionHomologacion.usuAlta) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.usuAlta},'%'))",
			"lower(plantillaResolucionHomologacion.usuMod) like lower(concat(#{plantillaResolucionHomologacionList.plantillaResolucionHomologacion.usuMod},'%'))", };

	private PlantillaResolucion plantillaResolucionHomologacion = new PlantillaResolucion();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "plantillaResolucionHomologacion.descripcion";
	public PlantillaResolucionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public PlantillaResolucion getPlantillaResolucionHomologacion() {
		return plantillaResolucionHomologacion;
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantillaResolucion> listaResultados() {
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
	public List<PlantillaResolucion> limpiar() {
		plantillaResolucionHomologacion= new PlantillaResolucion();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
