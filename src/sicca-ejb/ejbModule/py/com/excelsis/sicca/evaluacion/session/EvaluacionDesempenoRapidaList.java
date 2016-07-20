package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("evaluacionDesempenoRapidaList")
public class EvaluacionDesempenoRapidaList extends EntityQuery<EvaluacionDesempeno> {

	private static final String EJBQL = "select evaluacionDesempeno from EvaluacionDesempeno evaluacionDesempeno";

	private static final String[] RESTRICTIONS = {
		"lower(evaluacionDesempeno.titulo) like concat('%',lower(concat(#{evaluacionDesempenoRapidaList.evaluacionDesempeno.titulo},'%')))",
		"evaluacionDesempeno.estado=#{evaluacionDesempenoRapidaList.evaluacionDesempeno.estado}",
		"evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=#{evaluacionDesempenoRapidaList.configuracionUoCab.idConfiguracionUo}",
//		"evaluacionDesempeno.configuracionUoDet.idConfiguracionUoDet=#{evaluacionDesempenoRapidaList.configuracionUoDet.idConfiguracionUoDet}",no hace falta que el usuario pertenezca al mismo UO; Werner
		"evaluacionDesempeno.activo=#{evaluacionDesempenoRapidaList.activo}",
		"evaluacionDesempeno.esEvaluacionRapida=#{evaluacionDesempenoRapidaList.esEvaluacionRapida}",
		 };
	private static final String[] RESTRICTIONS569 = {
		"lower(evaluacionDesempeno.titulo) like concat('%',lower(concat(#{evaluacionDesempenoRapidaList.evaluacionDesempeno.titulo},'%')))",
		"evaluacionDesempeno.estado in (#{evaluacionDesempenoRapidaList.estadosFinalizados})",
		"evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=#{evaluacionDesempenoRapidaList.configuracionUoCab.idConfiguracionUo}",
//		"evaluacionDesempeno.configuracionUoDet.idConfiguracionUoDet=#{evaluacionDesempenoRapidaList.configuracionUoDet.idConfiguracionUoDet}",
		"evaluacionDesempeno.activo=#{evaluacionDesempenoRapidaList.activo}",
		"evaluacionDesempeno.esEvaluacionRapida=#{evaluacionDesempenoRapidaList.esEvaluacionRapida}",
		 };

	private EvaluacionDesempeno evaluacionDesempeno = new EvaluacionDesempeno();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
//	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private Boolean activo = true;
	private Boolean esEvaluacionRapida = true;
	private static final String ORDER = "evaluacionDesempeno.configuracionUoCab.denominacionUnidad";
	private List<Integer> estadosFinalizados= new ArrayList<Integer>();
	
	public EvaluacionDesempenoRapidaList() {
		setEjbql(EJBQL);
		setOrder(ORDER);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EvaluacionDesempeno> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EvaluacionDesempeno> listaResultados569() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS569));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	
	public List<EvaluacionDesempeno> limpiar() {
		evaluacionDesempeno = new EvaluacionDesempeno();
//		configuracionUoCab = new ConfiguracionUoCab();
//		configuracionUoDet = new ConfiguracionUoDet();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public EvaluacionDesempeno getEvaluacionDesempeno() {
		return evaluacionDesempeno;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

//	public ConfiguracionUoDet getConfiguracionUoDet() {
//		return configuracionUoDet;
//	}
//
//	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
//		this.configuracionUoDet = configuracionUoDet;
//	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	public Boolean getEsEvaluacionRapida() {
		return esEvaluacionRapida;
	}

	public void setEsEvaluacionRapida(Boolean esEvaluacionRapida) {
		this.esEvaluacionRapida = esEvaluacionRapida;
	}

	public List<Integer> getEstadosFinalizados() {
		return estadosFinalizados;
	}

	public void setEstadosFinalizados(List<Integer> estadosFinalizados) {
		this.estadosFinalizados = estadosFinalizados;
	}
	
	
}
