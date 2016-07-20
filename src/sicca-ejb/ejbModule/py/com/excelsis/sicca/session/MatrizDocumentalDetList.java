package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.MatrizDocumentalDet;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("matrizDocumentalDetList")
public class MatrizDocumentalDetList extends EntityQuery<MatrizDocumentalDet> {

	private static final String EJBQL = "select matrizDocumentalDet from MatrizDocumentalDet matrizDocumentalDet";

	private static final String[] RESTRICTIONS = {
			"lower(matrizDocumentalDet.usuAlta) like lower(concat(#{matrizDocumentalDetList.matrizDocumentalDet.usuAlta},'%'))",
			"lower(matrizDocumentalDet.usuMod) like lower(concat(#{matrizDocumentalDetList.matrizDocumentalDet.usuMod},'%'))", };

	private MatrizDocumentalDet matrizDocumentalDet = new MatrizDocumentalDet();

	public MatrizDocumentalDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MatrizDocumentalDet getMatrizDocumentalDet() {
		return matrizDocumentalDet;
	}
}
