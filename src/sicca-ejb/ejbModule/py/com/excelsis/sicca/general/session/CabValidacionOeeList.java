package py.com.excelsis.sicca.general.session;

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
@Name("cabValidacionOeeList")
public class CabValidacionOeeList extends EntityQuery<CabValidacionOee> {

	private static final String EJBQL = "select cabValidacionOee from CabValidacionOee cabValidacionOee";

	private static final String[] RESTRICTIONS = { 
		"lower(cabValidacionOee.nombreGrupoValidacion) like concat('%',lower(concat(#{cabValidacionOeeList.cabValidacionOee.nombreGrupoValidacion},'%')))", 
		" cabValidacionOee.activo=#{cabValidacionOeeList.estado.valor}",
		" cabValidacionOee.configuracionUoCab.idConfiguracionUo=#{cabValidacionOeeList.configuracionUoCab.idConfiguracionUo}",};

	private CabValidacionOee cabValidacionOee = new CabValidacionOee();
	private Estado estado = Estado.ACTIVO;
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private static final String ORDER = "cabValidacionOee.idCabValidacionOee";

	public CabValidacionOeeList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CabValidacionOee> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<CabValidacionOee> limpiar() {
		cabValidacionOee = new CabValidacionOee();
		estado = Estado.ACTIVO;
		configuracionUoCab = new ConfiguracionUoCab();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public CabValidacionOee getCabValidacionOee() {
		return cabValidacionOee;
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

}
