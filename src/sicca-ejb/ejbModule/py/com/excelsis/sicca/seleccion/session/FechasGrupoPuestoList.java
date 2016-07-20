package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("fechasGrupoPuestoList")
public class FechasGrupoPuestoList extends EntityQuery<FechasGrupoPuesto> {

	private static final String EJBQL = "select fechasGrupoPuesto from FechasGrupoPuesto fechasGrupoPuesto";

	private static final String[] RESTRICTIONS = {
			"lower(fechasGrupoPuesto.usuAlta) like lower(concat(#{fechasGrupoPuestoList.fechasGrupoPuesto.usuAlta},'%'))",
			"lower(fechasGrupoPuesto.usuMod) like lower(concat(#{fechasGrupoPuestoList.fechasGrupoPuesto.usuMod},'%'))", };

	private FechasGrupoPuesto fechasGrupoPuesto = new FechasGrupoPuesto();

	public FechasGrupoPuestoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public FechasGrupoPuesto getFechasGrupoPuesto() {
		return fechasGrupoPuesto;
	}
}
