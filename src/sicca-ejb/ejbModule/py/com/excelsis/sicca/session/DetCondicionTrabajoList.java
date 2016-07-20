package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetCondicionTrabajo;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detCondicionTrabajoList")
public class DetCondicionTrabajoList extends EntityQuery<DetCondicionTrabajo> {

	private static final String EJBQL = "select detCondicionTrabajo from DetCondicionTrabajo detCondicionTrabajo";

	private static final String[] RESTRICTIONS = { "lower(detCondicionTrabajo.tipo) like lower(concat(#{detCondicionTrabajoList.detCondicionTrabajo.tipo},'%'))", };

	private DetCondicionTrabajo detCondicionTrabajo = new DetCondicionTrabajo();

	public DetCondicionTrabajoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetCondicionTrabajo getDetCondicionTrabajo() {
		return detCondicionTrabajo;
	}
}
