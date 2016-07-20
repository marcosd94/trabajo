package py.com.excelsis.sicca.seleccion.session.form;

import javax.persistence.Query;

import py.com.excelsis.sicca.entity.EvalDocumentalCab;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

public class RealizarEvalsListFC extends CustomEntityQuery<EvalDocumentalCab> {

	private static final String EJBQL =
		" SELECT fechasGrupoPuesto from FechasGrupoPuesto fechasGrupoPuesto  ";
	private static final String ORDER_BASE = "concurso.nombre, concurso.fechaAlta";

	public RealizarEvalsListFC() {
		setEjbql(EJBQL);
		setMaxResults(25);
	}

	private void executeQuery(Query q) {
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		//lista = getResultList(q);
	}
}
