package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetCondicionTrabajoEspecif;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detCondicionTrabajoEspecifList")
public class DetCondicionTrabajoEspecifList extends
		EntityQuery<DetCondicionTrabajoEspecif> {

	private static final String EJBQL = "select detCondicionTrabajoEspecif from DetCondicionTrabajoEspecif detCondicionTrabajoEspecif";

	private static final String[] RESTRICTIONS = {
			"lower(detCondicionTrabajoEspecif.tipo) like lower(concat(#{detCondicionTrabajoEspecifList.detCondicionTrabajoEspecif.tipo},'%'))",
			"lower(detCondicionTrabajoEspecif.justificacion) like lower(concat(#{detCondicionTrabajoEspecifList.detCondicionTrabajoEspecif.justificacion},'%'))",
			"lower(detCondicionTrabajoEspecif.ajustes) like lower(concat(#{detCondicionTrabajoEspecifList.detCondicionTrabajoEspecif.ajustes},'%'))", };

	private DetCondicionTrabajoEspecif detCondicionTrabajoEspecif = new DetCondicionTrabajoEspecif();

	public DetCondicionTrabajoEspecifList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetCondicionTrabajoEspecif getDetCondicionTrabajoEspecif() {
		return detCondicionTrabajoEspecif;
	}
}
