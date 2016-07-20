package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Name("alertasEvalList")
public class AlertasEvalList extends EntityQuery<AlertasEval> {

	private static final String EJBQL = "select alertasEval from AlertasEval alertasEval";

	private static final String[] RESTRICTIONS = {
			"alertasEval.activo =#{alertasEvalList.alertasEval.activo}",
			"alertasEval.configuracionUoCab.idConfiguracionUo =#{alertasEvalList.configuracionUoCab.idConfiguracionUo} ",
			"alertasEval.configuracionUoCab.entidad.sinEntidad.idSinEntidad =#{alertasEvalList.idSinEntidad} ",
			"alertasEval.configuracionUoCab.entidad.sinEntidad.nenCodigo = #{alertasEvalList.nenCodigo} ",
			};

	private AlertasEval alertasEval = new AlertasEval();
	private static final String ORDER = "alertasEval.configuracionUoCab.denominacionUnidad,alertasEval.periodo";
	private Long idSinEntidad=null;
	private BigDecimal nenCodigo=null;
	private ConfiguracionUoCab configuracionUoCab= new  ConfiguracionUoCab();

	public AlertasEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<AlertasEval> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public AlertasEval getAlertasEval() {
		return alertasEval;
	}
	
	public Long getIdSinEntidad() {
		return idSinEntidad;
	}
	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}
	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}
	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}
	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}
	
	
}
