package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detMinimosRequeridosList")
public class DetMinimosRequeridosList extends EntityQuery<DetMinimosRequeridos> {

	private static final String EJBQL = "select detMinimosRequeridos from DetMinimosRequeridos detMinimosRequeridos";

	private static final String[] RESTRICTIONS = {
			"lower(detMinimosRequeridos.tipo) like lower(concat(#{detMinimosRequeridosList.detMinimosRequeridos.tipo},'%'))",
			"lower(detMinimosRequeridos.minimosRequeridos) like lower(concat(#{detMinimosRequeridosList.detMinimosRequeridos.minimosRequeridos},'%'))", };

	private DetMinimosRequeridos detMinimosRequeridos = new DetMinimosRequeridos();

	public DetMinimosRequeridosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetMinimosRequeridos getDetMinimosRequeridos() {
		return detMinimosRequeridos;
	}
}
