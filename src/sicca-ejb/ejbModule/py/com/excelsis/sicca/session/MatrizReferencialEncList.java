package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.MatrizReferencialEnc;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("matrizReferencialEncList")
public class MatrizReferencialEncList extends EntityQuery<MatrizReferencialEnc> {

	private static final String EJBQL = "select matrizReferencialEnc from MatrizReferencialEnc matrizReferencialEnc";

	private static final String[] RESTRICTIONS = {
			"lower(matrizReferencialEnc.descripcion) like lower(concat(#{matrizReferencialEncList.matrizReferencialEnc.descripcion},'%'))",
			"lower(matrizReferencialEnc.usuAlta) like lower(concat(#{matrizReferencialEncList.matrizReferencialEnc.usuAlta},'%'))",
			"lower(matrizReferencialEnc.usuMod) like lower(concat(#{matrizReferencialEncList.matrizReferencialEnc.usuMod},'%'))", };

	private MatrizReferencialEnc matrizReferencialEnc = new MatrizReferencialEnc();

	public MatrizReferencialEncList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MatrizReferencialEnc getMatrizReferencialEnc() {
		return matrizReferencialEnc;
	}
}
