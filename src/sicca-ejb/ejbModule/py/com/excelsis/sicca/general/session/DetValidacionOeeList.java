package py.com.excelsis.sicca.general.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("detValidacionOeeList")
public class DetValidacionOeeList extends EntityQuery<DetValidacionOee> {

	private static final String EJBQL = "select detValidacionOee from DetValidacionOee detValidacionOee";

	private static final String[] RESTRICTIONS = {
		"lower(detValidacionOee.nombreValidacion) like concat('%',lower(concat(#{detValidacionOeeList.detValidacionOee.nombreValidacion},'%')))",
		" detValidacionOee.activo=#{detValidacionOeeList.estado.valor}",
		" detValidacionOee.cabValidacionOee.configuracionUoCab.idConfiguracionUo=#{detValidacionOeeList.configuracionUoCab.idConfiguracionUo}",
		" detValidacionOee.cabValidacionOee.idCabValidacionOee=#{detValidacionOeeList.idGrupo}",};

	private DetValidacionOee detValidacionOee = new DetValidacionOee();
	private Estado estado = Estado.ACTIVO;
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private Long idGrupo;
	private static final String ORDER = "detValidacionOee.idDetValidacionOee";

	public DetValidacionOeeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<DetValidacionOee> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<DetValidacionOee> limpiar() {
		detValidacionOee = new DetValidacionOee();
		estado = Estado.ACTIVO;
		idGrupo = null;
		configuracionUoCab = new ConfiguracionUoCab();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public DetValidacionOee getDetValidacionOee() {
		return detValidacionOee;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}
	
	
}
