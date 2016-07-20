package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("publicacionConcursoCabList")
public class PublicacionConcursoCabList extends
		EntityQuery<PublicacionConcursoCab> {

	private static final String EJBQL = "select publicacionConcursoCab from PublicacionConcursoCab publicacionConcursoCab";

	private static final String[] RESTRICTIONS = {
			"lower(publicacionConcursoCab.usuSolicitud) like lower(concat(#{publicacionConcursoCabList.publicacionConcursoCab.usuSolicitud},'%'))",
			"lower(publicacionConcursoCab.usuAprobacion) like lower(concat(#{publicacionConcursoCabList.publicacionConcursoCab.usuAprobacion},'%'))",
			"lower(publicacionConcursoCab.usuPublicacion) like lower(concat(#{publicacionConcursoCabList.publicacionConcursoCab.usuPublicacion},'%'))", };

	private PublicacionConcursoCab publicacionConcursoCab = new PublicacionConcursoCab();

	public PublicacionConcursoCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PublicacionConcursoCab getPublicacionConcursoCab() {
		return publicacionConcursoCab;
	}
}
