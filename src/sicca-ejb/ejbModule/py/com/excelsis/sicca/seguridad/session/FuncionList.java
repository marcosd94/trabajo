package py.com.excelsis.sicca.seguridad.session;

import py.com.excelsis.sicca.seguridad.entity.Funcion;
import py.com.excelsis.sicca.seguridad.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("funcionList")
public class FuncionList extends EntityQuery<Funcion> {

	private static final String EJBQL = "select funcion from Funcion funcion";

	private static final String[] RESTRICTIONS = {
			"lower(funcion.descripcion) like lower(concat(#{funcionList.funcion.descripcion},'%'))",
			"lower(funcion.url) like lower(concat(#{funcionList.funcion.url},'%'))", };

	private Funcion funcion = new Funcion();

	public FuncionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Funcion getFuncion() {
		return funcion;
	}
}
