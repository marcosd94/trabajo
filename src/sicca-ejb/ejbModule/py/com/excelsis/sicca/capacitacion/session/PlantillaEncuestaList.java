package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("plantillaEncuestaList")
public class PlantillaEncuestaList extends EntityQuery<PlantillaEncuesta> {

	private static final String EJBQL = "select plantillaEncuesta from PlantillaEncuesta plantillaEncuesta";

	private static final String[] RESTRICTIONS = {
		"lower(plantillaEncuesta.nombre) like concat('%',lower(concat(#{plantillaEncuestaList.plantillaEncuesta.nombre},'%')))",
			"plantillaEncuesta.activo =#{plantillaEncuestaList.estado.valor}",
			"plantillaEncuesta.configuracionUoCab.idConfiguracionUo =#{plantillaEncuestaList.idOee}", };

	private PlantillaEncuesta plantillaEncuesta = new PlantillaEncuesta();
	private Estado estado = Estado.ACTIVO;
	private Long idOee;
	private static final String ORDER = "plantillaEncuesta.nombre";

	public PlantillaEncuestaList() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlantillaEncuesta> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<PlantillaEncuesta> limpiar() {
		plantillaEncuesta = new PlantillaEncuesta();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public PlantillaEncuesta getPlantillaEncuesta() {
		return plantillaEncuesta;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdOee() {
		return idOee;
	}

	public void setIdOee(Long idOee) {
		this.idOee = idOee;
	}
	
	
	
}

