package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("grupoConceptoPagoList")
public class GrupoConceptoPagoList extends EntityQuery<GrupoConceptoPago> {

	private static final String EJBQL = "select grupoConceptoPago from GrupoConceptoPago grupoConceptoPago";

	private static final String[] RESTRICTIONS = {
			"lower(grupoConceptoPago.categoria) like lower(concat(#{grupoConceptoPagoList.grupoConceptoPago.categoria},'%'))",
			"lower(grupoConceptoPago.usuAlta) like lower(concat(#{grupoConceptoPagoList.grupoConceptoPago.usuAlta},'%'))",
			"lower(grupoConceptoPago.usuMod) like lower(concat(#{grupoConceptoPagoList.grupoConceptoPago.usuMod},'%'))", };

	private GrupoConceptoPago grupoConceptoPago = new GrupoConceptoPago();

	public GrupoConceptoPagoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public GrupoConceptoPago getGrupoConceptoPago() {
		return grupoConceptoPago;
	}
}
