package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.PreguntasFrecuentes;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("preguntasFrecuentesList")
public class PreguntasFrecuentesList extends EntityQuery<PreguntasFrecuentes> {

	private static final String EJBQL = "select preguntasFrecuentes from PreguntasFrecuentes preguntasFrecuentes";

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<PreguntasFrecuentes> listaPreguntasFrecuentes;
	
	private static final String[] RESTRICTIONS = {
			"lower(preguntasFrecuentes.preguntaFrecuente) like lower(concat('%', concat(#{preguntasFrecuentesList.preguntasFrecuentes.preguntaFrecuente},'%')))",
			"lower(preguntasFrecuentes.respuestaPregunta) like lower(concat(#{preguntasFrecuentesList.preguntasFrecuentes.respuestaPregunta},'%'))",
			"lower(preguntasFrecuentes.usuAlta) like lower(concat(#{preguntasFrecuentesList.preguntasFrecuentes.usuAlta},'%'))",
			"lower(preguntasFrecuentes.usuMod) like lower(concat(#{preguntasFrecuentesList.preguntasFrecuentes.usuMod},'%'))",
			"preguntasFrecuentes.activo=#{preguntasFrecuentesList.estado.valor}", 
			"preguntasFrecuentes.datosEspecificos.idDatosEspecificos=#{preguntasFrecuentesList.idPreguntasFrecuentes}", 
			};

	private static final String ORDER = "preguntasFrecuentes.nroOrden";
	private PreguntasFrecuentes preguntasFrecuentes = new PreguntasFrecuentes();
	private Long idPreguntasFrecuentes;
	private Estado estado = Estado.ACTIVO;

	public PreguntasFrecuentesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PreguntasFrecuentes> listaResultados() {
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
	public List<PreguntasFrecuentes> limpiar() {
		preguntasFrecuentes = new PreguntasFrecuentes();
		estado = Estado.ACTIVO;
		idPreguntasFrecuentes = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public PreguntasFrecuentes getPreguntasFrecuentes() {
		return preguntasFrecuentes;
	}

	public Long getIdPreguntasFrecuentes() {
		return idPreguntasFrecuentes;
	}

	public void setIdPreguntasFrecuentes(Long idPreguntasFrecuentes) {
		this.idPreguntasFrecuentes = idPreguntasFrecuentes;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public List<PreguntasFrecuentes> obtenerListaPreguntasFrecuentes() {
		listaPreguntasFrecuentes = getResultList();
		return listaPreguntasFrecuentes;
	}
	
}
