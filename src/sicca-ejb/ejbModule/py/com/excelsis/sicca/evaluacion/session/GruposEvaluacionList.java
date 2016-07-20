package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("gruposEvaluacionList")
public class GruposEvaluacionList extends EntityQuery<GruposEvaluacion> {

	private static final String EJBQL = "select gruposEvaluacion from GruposEvaluacion gruposEvaluacion";

	private static final String[] RESTRICTIONS = {
			"lower(gruposEvaluacion.grupo) like concat('%',lower(concat(#{gruposEvaluacionList.gruposEvaluacion.grupo},'%')))",
			"gruposEvaluacion.activo = #{gruposEvaluacionList.gruposEvaluacion.activo} ",
			"gruposEvaluacion.evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=#{gruposEvaluacionList.configuracionUoCab.idConfiguracionUo}",
			"gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=#{gruposEvaluacionList.idEvaluacionDesempeno}", };

	private static final String[] RESTRICTIONS_552_BAJA = {
		"lower(gruposEvaluacion.grupo) like concat('%',lower(concat(#{gruposEvaluacionList.gruposEvaluacion.grupo},'%')))",
		"gruposEvaluacion.evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=#{gruposEvaluacionList.configuracionUoCab.idConfiguracionUo}" +
		"gruposEvaluacion.motivoCancelacion is not null ",
		"gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=#{gruposEvaluacionList.idEvaluacionDesempeno}", };
	private static final String[] RESTRICTIONS_552 = {
		"lower(gruposEvaluacion.grupo) like concat('%',lower(concat(#{gruposEvaluacionList.gruposEvaluacion.grupo},'%')))",
		"gruposEvaluacion.evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=#{gruposEvaluacionList.configuracionUoCab.idConfiguracionUo}",
		"gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno=#{gruposEvaluacionList.idEvaluacionDesempeno}", };


	
	private GruposEvaluacion gruposEvaluacion = new GruposEvaluacion();
	private static final String ORDER = "gruposEvaluacion.grupo";
	private ConfiguracionUoCab configuracionUoCab= new ConfiguracionUoCab();
	private Long idEvaluacionDesempeno=null;

	public GruposEvaluacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public GruposEvaluacion getGruposEvaluacion() {
		return gruposEvaluacion;
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<GruposEvaluacion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public List<GruposEvaluacion> listaResultadosCU522BajaVer() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_552_BAJA));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public List<GruposEvaluacion> listaResultadosCU522Baja() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_552));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	
}
