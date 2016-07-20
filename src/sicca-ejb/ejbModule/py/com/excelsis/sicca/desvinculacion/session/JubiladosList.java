package py.com.excelsis.sicca.desvinculacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Name("jubiladosList")
public class JubiladosList extends CustomEntityQuery<Jubilados> {

	private static final String EJBQL = "select jubilados from Jubilados jubilados";

	private static final String[] RESTRICTIONS = {
			"lower(jubilados.otroTipo) like lower(concat('%', concat(#{jubiladosList.jubilados.otroTipo},'%')))",
			"lower(jubilados.usuAlta) like lower(concat('%', concat(#{jubiladosList.jubilados.usuAlta},'%')))",
			"lower(jubilados.usuMod) like lower(concat('%', concat(#{jubiladosList.jubilados.usuMod},'%')))", };

	private static final String ORDER = "jubilados.fechaAlta";
	
	private Jubilados jubilados = new Jubilados();

	public JubiladosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Jubilados getJubilados() {
		return jubilados;
	}
	
	public List<Jubilados> listaResultadosCU442(String ejbql, Map<String, QueryValue> params) {
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(params);
		return getResultList(ejbql);
	}
}
