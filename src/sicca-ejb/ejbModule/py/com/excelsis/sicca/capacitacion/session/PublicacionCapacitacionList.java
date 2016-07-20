package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("publicacionCapacitacionList")
public class PublicacionCapacitacionList extends
		EntityQuery<PublicacionCapacitacion> {

	private static final String EJBQL = "select publicacionCapacitacion from PublicacionCapacitacion publicacionCapacitacion";

	private static final String[] RESTRICTIONS = {
			"lower(publicacionCapacitacion.texto) like lower(concat(#{publicacionCapacitacionList.publicacionCapacitacion.texto},'%'))",
			"lower(publicacionCapacitacion.usuAlta) like lower(concat(#{publicacionCapacitacionList.publicacionCapacitacion.usuAlta},'%'))",
			"lower(publicacionCapacitacion.usuMod) like lower(concat(#{publicacionCapacitacionList.publicacionCapacitacion.usuMod},'%'))", };

	private PublicacionCapacitacion publicacionCapacitacion = new PublicacionCapacitacion();

	public PublicacionCapacitacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PublicacionCapacitacion getPublicacionCapacitacion() {
		return publicacionCapacitacion;
	}
}
