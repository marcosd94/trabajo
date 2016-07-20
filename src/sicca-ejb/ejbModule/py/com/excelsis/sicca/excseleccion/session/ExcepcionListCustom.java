package py.com.excelsis.sicca.excseleccion.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.BandejaCapacitacion;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.PAGE)
@Name("excepcionListCustom")
public class ExcepcionListCustom extends CustomEntityQuery<Excepcion> {
	private static final String EJBQL = "select Excepcion from Excepcion Excepcion";

	private static final String[] RESTRICTIONS =
		{ "lower(Excepcion.tipo) like concat('',lower(concat(#{excepcionListCustom.tipo},'%')))", };

	private String tipo = "INGRESO";

	public ExcepcionListCustom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<Excepcion> listaResultados(String ejbql, Map<String, QueryValue> parametros) {
		setCustomEjbql(ejbql);		
		if (parametros != null)
			setParams(parametros);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<Excepcion> lista = getResultList();
		return lista;
	}

	public String getTipo() {
		return tipo;
	}
	
	
}
