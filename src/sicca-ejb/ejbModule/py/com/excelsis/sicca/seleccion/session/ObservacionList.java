package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("observacionList")
public class ObservacionList extends EntityQuery<Observacion> {

	private static final String EJBQL = "select observacion from Observacion observacion";

	private static final String[] RESTRICTIONS = {
			"lower(observacion.observacion) like lower(concat(#{observacionList.observacion.observacion},'%'))",
			"lower(observacion.usuAlta) like lower(concat(#{observacionList.observacion.usuAlta},'%'))",
			"lower(observacion.usuMod) like lower(concat(#{observacionList.observacion.usuMod},'%'))", };

	private Observacion observacion = new Observacion();

	public ObservacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Observacion getObservacion() {
		return observacion;
	}
}
