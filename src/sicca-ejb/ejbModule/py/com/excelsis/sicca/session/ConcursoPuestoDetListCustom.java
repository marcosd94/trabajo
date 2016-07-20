package py.com.excelsis.sicca.session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import py.com.excelsis.sicca.entity.ConcursoPuestoDet;
import py.com.excelsis.sicca.entity.Excepcion;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;
@Scope(ScopeType.CONVERSATION)
@Name("concursoPuestoDetListCustom")
public class ConcursoPuestoDetListCustom extends CustomEntityQuery<ConcursoPuestoDet> {
	private static final String EJBQL =
		"select concursoPuestoDet from ConcursoPuestoDet concursoPuestoDet";

	private static final String[] RESTRICTIONS =
		{
			"lower(concursoPuestoDet.usuAlta) like lower(concat(#{concursoPuestoDetList.concursoPuestoDet.usuAlta},'%'))",
			"lower(concursoPuestoDet.usuMod) like lower(concat(#{concursoPuestoDetList.concursoPuestoDet.usuMod},'%'))", };

	private ConcursoPuestoDet concursoPuestoDet = new ConcursoPuestoDet();

	public ConcursoPuestoDetListCustom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public ConcursoPuestoDet getConcursoPuestoDet() {
		return concursoPuestoDet;
	}

	public List<ConcursoPuestoDet> search(String query, Map<String, QueryValue> parametros) {
		setEjbql(query);
		setMaxResults(null);
		if (parametros != null && parametros.size() > 0) {
			setParams(parametros);
		}
		return getResultList(query);
	}
}
