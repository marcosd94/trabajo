package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.PlanCapacitacion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Name("planCapacitacionList")
public class PlanCapacitacionList extends EntityQuery<PlanCapacitacion> {

	private static final String EJBQL = "select planCapacitacion from PlanCapacitacion planCapacitacion";

	private static final String[] RESTRICTIONS = {
			"planCapacitacion.nro =#{planCapacitacionList.planCapacitacion.nro}",
			"planCapacitacion.anio =#{planCapacitacionList.planCapacitacion.anio}",
			"planCapacitacion.configuracionUoCab.idConfiguracionUo =#{planCapacitacionList.configuracionUoCab.idConfiguracionUo} ",
			"planCapacitacion.configuracionUoDet.idConfiguracionUoDet = #{planCapacitacionList.idConfiguracionUoDet} ",
			"planCapacitacion.activo =#{planCapacitacionList.estado.valor} ",
			"planCapacitacion.configuracionUoCab.entidad.sinEntidad.nenCodigo =#{planCapacitacionList.nenCodigo} ",};

	private PlanCapacitacion planCapacitacion = new PlanCapacitacion();
	private Estado estado=Estado.ACTIVO;
	private static final String ORDER = "planCapacitacion.anio desc";
	private Long idConfiguracionUoDet;
	private BigDecimal nenCodigo;
	private ConfiguracionUoCab configuracionUoCab= new ConfiguracionUoCab();
	
	public PlanCapacitacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlanCapacitacion> listaResultados() {
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
	public List<PlanCapacitacion> limpiarCU495() {
		planCapacitacion= new PlanCapacitacion();
		estado = Estado.ACTIVO;
		nenCodigo = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public PlanCapacitacion getPlanCapacitacion() {
		return planCapacitacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdConfiguracionUoDet() {
		return idConfiguracionUoDet;
	}

	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
		this.idConfiguracionUoDet = idConfiguracionUoDet;
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
