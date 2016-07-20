package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("datosGrupoPuestoList")
public class DatosGrupoPuestoList extends EntityQuery<DatosGrupoPuesto> {

	private static final String EJBQL = "select datosGrupoPuesto from DatosGrupoPuesto datosGrupoPuesto";

	private static final String[] RESTRICTIONS = {
			"lower(datosGrupoPuesto.usuAlta) like lower(concat(#{datosGrupoPuestoList.datosGrupoPuesto.usuAlta},'%'))",
			"lower(datosGrupoPuesto.usuMod) like lower(concat(#{datosGrupoPuestoList.datosGrupoPuesto.usuMod},'%'))", };

	private DatosGrupoPuesto datosGrupoPuesto = new DatosGrupoPuesto();

	public DatosGrupoPuestoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DatosGrupoPuesto getDatosGrupoPuesto() {
		return datosGrupoPuesto;
	}
}
