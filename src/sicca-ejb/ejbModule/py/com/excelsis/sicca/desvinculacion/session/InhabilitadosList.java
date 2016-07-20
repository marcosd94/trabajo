package py.com.excelsis.sicca.desvinculacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Name("inhabilitadosList")
public class InhabilitadosList extends CustomEntityQuery<Inhabilitados> {

	private static final String EJBQL = "select inhabilitados from Inhabilitados inhabilitados";

	private static final String[] RESTRICTIONS = {
			"lower(inhabilitados.usuAlta) like lower(concat(#{inhabilitadosList.inhabilitados.usuAlta},'%'))",
			"lower(inhabilitados.usuMod) like lower(concat(#{inhabilitadosList.inhabilitados.usuMod},'%'))", };

	private static final String ORDER = "inhabilitados.fechaAlta";
	
	private Inhabilitados inhabilitados = new Inhabilitados();

	public InhabilitadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public Inhabilitados getInhabilitados() {
		return inhabilitados;
	}
	
	public List<Inhabilitados> listaResultadosCU441(String ejbql, Map<String, QueryValue> params) {
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(params);
		return getResultList(ejbql);
	}
}
