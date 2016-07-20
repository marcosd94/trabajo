package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.PAGE)
@Name("bandejaCapacitacionList")
public class BandejaCapacitacionList extends
		CustomEntityQuery<BandejaCapacitacion> {

	private static final String EJBQL = "select bandejaCapacitacion from BandejaCapacitacion bandejaCapacitacion";

	private static final String[] RESTRICTIONS = {};

	private BandejaCapacitacion bandejaCapacitacion;

	public BandejaCapacitacionList() {
		bandejaCapacitacion = new BandejaCapacitacion();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<BandejaCapacitacion> listaResultados(String ejbql) {
		
		setEjbql(ejbql);

		// setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		// setOrder(ORDER);
		List<BandejaCapacitacion> lista = getResultList();
		return lista;
	}

	public BandejaCapacitacion getBandejaCapacitacion() {
		return bandejaCapacitacion;
	}
}
