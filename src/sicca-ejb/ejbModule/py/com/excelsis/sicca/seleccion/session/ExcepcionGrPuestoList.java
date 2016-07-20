package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ExcepcionGrPuesto;

import java.util.Arrays;

@Name("excepcionGrPuestoList")
public class ExcepcionGrPuestoList extends EntityQuery<ExcepcionGrPuesto> {

	private static final String EJBQL = "select excepcionGrPuesto from ExcepcionGrPuesto excepcionGrPuesto";

	private static final String[] RESTRICTIONS = {
			"lower(excepcionGrPuesto.usuAlta) like lower(concat(#{excepcionGrPuestoList.excepcionGrPuesto.usuAlta},'%'))",
			"lower(excepcionGrPuesto.usuModif) like lower(concat(#{excepcionGrPuestoList.excepcionGrPuesto.usuModif},'%'))", };

	private ExcepcionGrPuesto excepcionGrPuesto = new ExcepcionGrPuesto();

	public ExcepcionGrPuestoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ExcepcionGrPuesto getExcepcionGrPuesto() {
		return excepcionGrPuesto;
	}
}
