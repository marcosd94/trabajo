package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.AdjuntoDocPuestoAgr;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("adjuntoDocPuestoAgrList")
public class AdjuntoDocPuestoAgrList extends EntityQuery<AdjuntoDocPuestoAgr> {

	private static final String EJBQL = "select adjuntoDocPuestoAgr from AdjuntoDocPuestoAgr adjuntoDocPuestoAgr";

	private static final String[] RESTRICTIONS = {
			"lower(adjuntoDocPuestoAgr.usuMod) like lower(concat(#{adjuntoDocPuestoAgrList.adjuntoDocPuestoAgr.usuMod},'%'))",
			"lower(adjuntoDocPuestoAgr.usuAlta) like lower(concat(#{adjuntoDocPuestoAgrList.adjuntoDocPuestoAgr.usuAlta},'%'))", };

	private AdjuntoDocPuestoAgr adjuntoDocPuestoAgr = new AdjuntoDocPuestoAgr();

	public AdjuntoDocPuestoAgrList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AdjuntoDocPuestoAgr getAdjuntoDocPuestoAgr() {
		return adjuntoDocPuestoAgr;
	}
}
