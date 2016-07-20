package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.PAGE)
@Name("historicoEvaluacionList")
public class HistoricoEvaluacionList extends CustomEntityQuery<HistoricoEvaluacion> {

	private static final String EJBQL = "select historicoEvaluacion from HistoricoEvaluacion historicoEvaluacion";

	private static final String[] RESTRICTIONS = {};

	private HistoricoEvaluacion historicoEvaluacion;

	public HistoricoEvaluacionList() {
		historicoEvaluacion = new HistoricoEvaluacion();

		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public List<HistoricoEvaluacion> listaResultados(String ejbql) {

		// setEjbql(ejbql);

		setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		// setOrder(ORDER);
		List<HistoricoEvaluacion> lista = getResultList();
		return lista;
	}

	public HistoricoEvaluacion getHistoricoEvaluacion() {
		return historicoEvaluacion;
	}
}
