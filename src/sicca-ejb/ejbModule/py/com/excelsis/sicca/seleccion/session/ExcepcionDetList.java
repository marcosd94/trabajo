package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ExcepcionDet;

import java.util.Arrays;

@Name("excepcionDetList")
public class ExcepcionDetList extends EntityQuery<ExcepcionDet> {

	private static final String EJBQL = "select excepcionDet from ExcepcionDet excepcionDet";

	private static final String[] RESTRICTIONS = {
			"lower(excepcionDet.observacion) like lower(concat(#{excepcionDetList.excepcionDet.observacion},'%'))",
			"lower(excepcionDet.usuObs) like lower(concat(#{excepcionDetList.excepcionDet.usuObs},'%'))",
			"lower(excepcionDet.respuesta) like lower(concat(#{excepcionDetList.excepcionDet.respuesta},'%'))",
			"lower(excepcionDet.usuRpta) like lower(concat(#{excepcionDetList.excepcionDet.usuRpta},'%'))", };

	private ExcepcionDet excepcionDet = new ExcepcionDet();

	public ExcepcionDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ExcepcionDet getExcepcionDet() {
		return excepcionDet;
	}
}
