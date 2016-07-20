package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import java.util.Arrays;
import java.util.List;

@Name("bandejaEvaluacionList")
public class BandejaEvaluacionList extends CustomEntityQuery<BandejaEvaluacion> {

	private static final String EJBQL = "select bandejaEvaluacion from BandejaEvaluacion bandejaEvaluacion";

	private static final String[] RESTRICTIONS = { };

	private BandejaEvaluacion bandejaEvaluacion;

	public BandejaEvaluacionList() {
		bandejaEvaluacion = new BandejaEvaluacion();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public List<BandejaEvaluacion> listaResultados(String ejbql) {

		setEjbql(ejbql);

		// setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		// setOrder(ORDER);
		List<BandejaEvaluacion> lista = getResultList();
		return lista;
	}

	public BandejaEvaluacion getBandejaEvaluacion() {
		return bandejaEvaluacion;
	}
}
