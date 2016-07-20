package py.com.excelsis.sicca.capacitacion.session;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.HistorialCircuitoCap;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.PAGE)
@Name("historialCircuitoCapList")
public class HistorialCircuitoCapList extends CustomEntityQuery<HistorialCircuitoCap>{
	private static final String EJBQL = "select historialCircuitoCap from HistorialCircuitoCap historialCircuitoCap";

	private static final String[] RESTRICTIONS = {};

	private HistorialCircuitoCap historialCircuitoCap;

	public HistorialCircuitoCapList() {
		historialCircuitoCap = new HistorialCircuitoCap();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<HistorialCircuitoCap> listaResultados(String ejbql) {
		
		//setEjbql(ejbql);

		 setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		// setOrder(ORDER);
		List<HistorialCircuitoCap> lista = getResultList();
		return lista;
	}

	public HistorialCircuitoCap getHistorialCircuitoCap() {
		return historialCircuitoCap;
	}
}
