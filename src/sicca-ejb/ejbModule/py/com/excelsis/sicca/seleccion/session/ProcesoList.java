package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("procesoList")
public class ProcesoList extends EntityQuery<Proceso> {

	private static final String EJBQL = "select proceso from Proceso proceso";

	private static final String[] RESTRICTIONS = {
			"lower(proceso.descripcion) like lower(concat(#{procesoList.proceso.descripcion},'%'))",
			"lower(proceso.usuAlta) like lower(concat(#{procesoList.proceso.usuAlta},'%'))",
			"lower(proceso.usuMod) like lower(concat(#{procesoList.proceso.usuMod},'%'))", };

	private Proceso proceso = new Proceso();

	public ProcesoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Proceso getProceso() {
		return proceso;
	}
}
