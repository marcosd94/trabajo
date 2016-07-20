package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetTipoNombramiento;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detTipoNombramientoList")
public class DetTipoNombramientoList extends EntityQuery<DetTipoNombramiento> {

	private static final String EJBQL = "select detTipoNombramiento from DetTipoNombramiento detTipoNombramiento";

	private static final String[] RESTRICTIONS = { "lower(detTipoNombramiento.tipo) like lower(concat(#{detTipoNombramientoList.detTipoNombramiento.tipo},'%'))", };

	private DetTipoNombramiento detTipoNombramiento = new DetTipoNombramiento();

	public DetTipoNombramientoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetTipoNombramiento getDetTipoNombramiento() {
		return detTipoNombramiento;
	}
}
