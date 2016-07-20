package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("detOpcionesConvenientesList")
public class DetOpcionesConvenientesList extends
		EntityQuery<DetOpcionesConvenientes> {

	private static final String EJBQL = "select detOpcionesConvenientes from DetOpcionesConvenientes detOpcionesConvenientes";

	private static final String[] RESTRICTIONS = {
			"lower(detOpcionesConvenientes.tipo) like lower(concat(#{detOpcionesConvenientesList.detOpcionesConvenientes.tipo},'%'))",
			"lower(detOpcionesConvenientes.opcConvenientes) like lower(concat(#{detOpcionesConvenientesList.detOpcionesConvenientes.opcConvenientes},'%'))", };

	private DetOpcionesConvenientes detOpcionesConvenientes = new DetOpcionesConvenientes();

	public DetOpcionesConvenientesList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DetOpcionesConvenientes getDetOpcionesConvenientes() {
		return detOpcionesConvenientes;
	}
}
