package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.PlantillaMemoHomologacion;
import py.com.excelsis.sicca.entity.PlantillaNotaHomologacion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("plantillaMemoHomologacionList")
public class PlantillaMemoHomologacionList extends EntityQuery<PlantillaMemoHomologacion> {

	private static final String EJBQL =
		"select plantillaMemoHomologacion from PlantillaMemoHomologacion plantillaMemoHomologacion";

	private static final String[] RESTRICTIONS =
		{
			"lower(plantillaMemoHomologacion.descripcion) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.descripcion},'%'))",
			"lower(plantillaMemoHomologacion.titulo) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.titulo},'%'))",			
			"plantillaMemoHomologacion.activo = #{plantillaMemoHomologacionList.estado.valor}",
			"lower(plantillaMemoHomologacion.a) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.a},'%'))",
			"lower(plantillaMemoHomologacion.de) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.de},'%'))",
			"lower(plantillaMemoHomologacion.ref) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.ref},'%'))",
			"lower(plantillaMemoHomologacion.usuMod) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.usuMod},'%'))",
			"lower(plantillaMemoHomologacion.usuAlta) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.usuAlta},'%'))",
			"lower(plantillaMemoHomologacion.texto) like lower(concat(#{plantillaMemoHomologacionList.plantillaMemoHomologacion.texto},'%'))", };

	private PlantillaMemoHomologacion plantillaMemoHomologacion = new PlantillaMemoHomologacion();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "plantillaMemoHomologacion.descripcion";

	public PlantillaMemoHomologacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantillaMemoHomologacion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<PlantillaMemoHomologacion> limpiar() {
		plantillaMemoHomologacion = new PlantillaMemoHomologacion();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public PlantillaMemoHomologacion getPlantillaMemoHomologacion() {
		return plantillaMemoHomologacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setPlantillaMemoHomologacion(PlantillaMemoHomologacion plantillaMemoHomologacion) {
		this.plantillaMemoHomologacion = plantillaMemoHomologacion;
	}

}
