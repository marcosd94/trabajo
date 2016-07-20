package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("publicacionConcursoDetList")
public class PublicacionConcursoDetList extends
		EntityQuery<PublicacionConcursoDet> {

	private static final String EJBQL = "select publicacionConcursoDet from PublicacionConcursoDet publicacionConcursoDet";

	private static final String[] RESTRICTIONS = {
			"lower(publicacionConcursoDet.usuAlta) like lower(concat(#{publicacionConcursoDetList.publicacionConcursoDet.usuAlta},'%'))",
			"lower(publicacionConcursoDet.usuMod) like lower(concat(#{publicacionConcursoDetList.publicacionConcursoDet.usuMod},'%'))", };

	private PublicacionConcursoDet publicacionConcursoDet = new PublicacionConcursoDet();

	public PublicacionConcursoDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PublicacionConcursoDet getPublicacionConcursoDet() {
		return publicacionConcursoDet;
	}
}
