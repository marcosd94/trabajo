package py.com.excelsis.sicca.juridicos.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.entity.SumarioCab;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Name("sumarioCabList")
public class SumarioCabList extends CustomEntityQuery<SumarioCab> {

	private static final String EJBQL = "select sumarioCab from SumarioCab sumarioCab";
	
	private static final String ORDER = "sumarioCab.fechaAlta";

	private static final String[] RESTRICTIONS = {
			"lower(sumarioCab.estado) like lower(concat(#{sumarioCabList.sumarioCab.estado},'%'))",
			"lower(sumarioCab.estadoJuez) like lower(concat(#{sumarioCabList.sumarioCab.estadoJuez},'%'))",
			"lower(sumarioCab.expresadoJ) like lower(concat(#{sumarioCabList.sumarioCab.expresadoJ},'%'))",
			"lower(sumarioCab.obsJ) like lower(concat(#{sumarioCabList.sumarioCab.obsJ},'%'))",
			"lower(sumarioCab.expresadoM) like lower(concat(#{sumarioCabList.sumarioCab.expresadoM},'%'))",
			"lower(sumarioCab.obsM) like lower(concat(#{sumarioCabList.sumarioCab.obsM},'%'))",
			"lower(sumarioCab.usuAlta) like lower(concat(#{sumarioCabList.sumarioCab.usuAlta},'%'))",
			"lower(sumarioCab.usuMod) like lower(concat(#{sumarioCabList.sumarioCab.usuMod},'%'))", };

	private SumarioCab sumarioCab = new SumarioCab();

	public SumarioCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SumarioCab getSumarioCab() {
		return sumarioCab;
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SumarioCab> listaResultadosCU455(String ejbql, Map<String, QueryValue> params) {
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setParams(params);
		return getResultList(ejbql);
	}
}
