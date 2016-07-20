package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.Instructores;

import java.util.Arrays;

@Name("instructoresList")
public class InstructoresList extends EntityQuery<Instructores> {

	private static final String EJBQL = "select instructores from Instructores instructores";

	private static final String[] RESTRICTIONS = {
			"lower(instructores.descripcionCv) like lower(concat(#{instructoresList.instructores.descripcionCv},'%'))",
			"lower(instructores.usuAlta) like lower(concat(#{instructoresList.instructores.usuAlta},'%'))",
			"lower(instructores.usuMod) like lower(concat(#{instructoresList.instructores.usuMod},'%'))", };

	private Instructores instructores = new Instructores();

	public InstructoresList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Instructores getInstructores() {
		return instructores;
	}
}
