package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
 
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Name("excepcionGrPuestoList2")
public class ExcepcionGrPuestoList extends CustomEntityQuery<ExcepcionGrPuesto> {

	private static final String EJBQL =
		"select excepcionGrPuesto from ExcepcionGrPuesto excepcionGrPuesto";

	private static final String[] RESTRICTIONS =
		{
			"lower(excepcionGrPuesto.usuAlta) like lower(concat(#{excepcionGrPuestoList.excepcionGrPuesto.usuAlta},'%'))",
			"lower(excepcionGrPuesto.usuModif) like lower(concat(#{excepcionGrPuestoList.excepcionGrPuesto.usuModif},'%'))", };

	private ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();

	public List<ExcepcionGrPuesto> searchExcepcionesGrPuesto(String query, Map<String, QueryValue> parametros) {
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		if (parametros != null && parametros.size() > 0) {
			setParams(parametros);
		}
		try {
			getResultList(query);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return getResultList(query);
	}

	public ExcepcionGrPuestoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ExcepcionGrPuesto getExcepcionGrPuesto() {
		return excepcionGrPuesto;
	}
}
