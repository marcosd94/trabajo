package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("revisionCapacitacionList")
public class RevisionCapacitacionList extends EntityQuery<RevisionCapacitacion> {

	private static final String EJBQL = "select revisionCapacitacion from RevisionCapacitacion revisionCapacitacion";

	private static final String[] RESTRICTIONS = {
			"lower(revisionCapacitacion.observacion) like lower(concat(#{revisionCapacitacionList.revisionCapacitacion.observacion},'%'))",
			"lower(revisionCapacitacion.usuObs) like lower(concat(#{revisionCapacitacionList.revisionCapacitacion.usuObs},'%'))",
			"lower(revisionCapacitacion.respuesta) like lower(concat(#{revisionCapacitacionList.revisionCapacitacion.respuesta},'%'))",
			"lower(revisionCapacitacion.usuRpta) like lower(concat(#{revisionCapacitacionList.revisionCapacitacion.usuRpta},'%'))", };

	private RevisionCapacitacion revisionCapacitacion = new RevisionCapacitacion();

	public RevisionCapacitacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public RevisionCapacitacion getRevisionCapacitacion() {
		return revisionCapacitacion;
	}
}
