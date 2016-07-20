package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.RevisionEvaluacion;

import java.util.Arrays;

@Name("revisionEvaluacionList")
public class RevisionEvaluacionList extends EntityQuery<RevisionEvaluacion> {

	private static final String EJBQL = "select revisionEvaluacion from RevisionEvaluacion revisionEvaluacion";

	private static final String[] RESTRICTIONS = {
			"lower(revisionEvaluacion.observacion) like lower(concat(#{revisionEvaluacionList.revisionEvaluacion.observacion},'%'))",
			"lower(revisionEvaluacion.usuObs) like lower(concat(#{revisionEvaluacionList.revisionEvaluacion.usuObs},'%'))",
			"lower(revisionEvaluacion.respuesta) like lower(concat(#{revisionEvaluacionList.revisionEvaluacion.respuesta},'%'))",
			"lower(revisionEvaluacion.usuRpta) like lower(concat(#{revisionEvaluacionList.revisionEvaluacion.usuRpta},'%'))", };

	private RevisionEvaluacion revisionEvaluacion = new RevisionEvaluacion();

	public RevisionEvaluacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RevisionEvaluacion getRevisionEvaluacion() {
		return revisionEvaluacion;
	}
}
