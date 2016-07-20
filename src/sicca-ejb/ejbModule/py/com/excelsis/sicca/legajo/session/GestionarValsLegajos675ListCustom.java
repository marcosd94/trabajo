package py.com.excelsis.sicca.legajo.session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.PAGE)
@Name("gestionarValsLegajos675ListCustom")
public class GestionarValsLegajos675ListCustom extends CustomEntityQuery<Persona> {
	private static final String EJBQL =
		"select EmpleadoPuesto.persona  from EmpleadoPuesto EmpleadoPuesto ";

	private static final String[] RESTRICTIONS = {};

	public GestionarValsLegajos675ListCustom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<Persona> listaResultados(String ejbql, Map<String, QueryValue> parametros) {
		setCustomEjbql(ejbql);
		if (parametros != null)
			setParams(parametros);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<Persona> lista = getResultList();
		return lista;
	}
}