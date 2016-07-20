package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.PlantillaNotaHomologacion;
import py.com.excelsis.sicca.entity.PlantillaResolucion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("plantillaNotaHomologacionList")
public class PlantillaNotaHomologacionList extends
		EntityQuery<PlantillaNotaHomologacion> {

	private static final String EJBQL = "select plantillaNotaHomologacion from PlantillaNotaHomologacion plantillaNotaHomologacion";

	private static final String[] RESTRICTIONS = {
			"lower(plantillaNotaHomologacion.descripcion) like lower(concat('%', concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.descripcion},'%')))", 
			"plantillaNotaHomologacion.activo =#{plantillaNotaHomologacionList.estado.valor}",
			"lower(plantillaNotaHomologacion.referencia) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.referencia},'%'))",
			"lower(plantillaNotaHomologacion.titulo1) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.titulo1},'%'))",
			"lower(plantillaNotaHomologacion.titulo2) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.titulo2},'%'))",
			"lower(plantillaNotaHomologacion.cuerpo) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.cuerpo},'%'))",
			"lower(plantillaNotaHomologacion.firma) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.firma},'%'))",
			"lower(plantillaNotaHomologacion.cargo) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.cargo},'%'))",
			"lower(plantillaNotaHomologacion.institucionFirma) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.institucionFirma},'%'))",
			"lower(plantillaNotaHomologacion.a) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.a},'%'))",
			"lower(plantillaNotaHomologacion.don) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.don},'%'))",
			"lower(plantillaNotaHomologacion.itemFinal) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.itemFinal},'%'))",
			"lower(plantillaNotaHomologacion.institucion) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.institucion},'%'))",
			"lower(plantillaNotaHomologacion.usuAlta) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.usuAlta},'%'))",
			"lower(plantillaNotaHomologacion.usuMod) like lower(concat(#{plantillaNotaHomologacionList.plantillaNotaHomologacion.usuMod},'%'))", };

	private PlantillaNotaHomologacion plantillaNotaHomologacion = new PlantillaNotaHomologacion();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "plantillaNotaHomologacion.descripcion";
	public PlantillaNotaHomologacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantillaNotaHomologacion> listaResultados() {
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
	public List<PlantillaNotaHomologacion> limpiar() {
		plantillaNotaHomologacion= new PlantillaNotaHomologacion();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public PlantillaNotaHomologacion getPlantillaNotaHomologacion() {
		return plantillaNotaHomologacion;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
	
}
