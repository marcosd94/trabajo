package py.com.excelsis.sicca.legajo.session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.EmpleadoPuesto;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.PAGE)
@Name("gestionarLegajos660ListCustom")
public class GestionarLegajos660ListCustom extends CustomEntityQuery<EmpleadoPuesto> {
	private static final String EJBQL = "select EmpleadoPuesto from EmpleadoPuesto EmpleadoPuesto ";

	private static final String[] RESTRICTIONS = {};

	public GestionarLegajos660ListCustom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<EmpleadoPuesto> listaResultados(String ejbql, Map<String, QueryValue> parametros) {
		setCustomEjbql(ejbql);
		if (parametros != null)
			setParams(parametros);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<EmpleadoPuesto> lista = getResultList();
		return lista;
	}
}
