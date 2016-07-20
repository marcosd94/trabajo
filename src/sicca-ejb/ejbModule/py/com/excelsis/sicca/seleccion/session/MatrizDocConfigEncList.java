package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.MatrizDocConfigEnc;

import java.util.Arrays;

@Name("matrizDocConfigEncList")
public class MatrizDocConfigEncList extends EntityQuery<MatrizDocConfigEnc> {

	private static final String EJBQL = "select matrizDocConfigEnc from MatrizDocConfigEnc matrizDocConfigEnc";

	private static final String[] RESTRICTIONS = {
			"lower(matrizDocConfigEnc.usuAlta) like lower(concat(#{matrizDocConfigEncList.matrizDocConfigEnc.usuAlta},'%'))",
			"lower(matrizDocConfigEnc.usuMod) like lower(concat(#{matrizDocConfigEncList.matrizDocConfigEnc.usuMod},'%'))", };

	private MatrizDocConfigEnc matrizDocConfigEnc = new MatrizDocConfigEnc();

	public MatrizDocConfigEncList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MatrizDocConfigEnc getMatrizDocConfigEnc() {
		return matrizDocConfigEnc;
	}
}
