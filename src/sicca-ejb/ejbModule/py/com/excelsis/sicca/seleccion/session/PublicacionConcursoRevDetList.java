package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("publicacionConcursoRevDetList")
public class PublicacionConcursoRevDetList extends
		EntityQuery<PublicacionConcursoRevDet> {

	private static final String EJBQL = "select publicacionConcursoRevDet from PublicacionConcursoRevDet publicacionConcursoRevDet";

	private static final String[] RESTRICTIONS = {
			"lower(publicacionConcursoRevDet.observacion) like lower(concat(#{publicacionConcursoRevDetList.publicacionConcursoRevDet.observacion},'%'))",
			"lower(publicacionConcursoRevDet.usuObs) like lower(concat(#{publicacionConcursoRevDetList.publicacionConcursoRevDet.usuObs},'%'))",
			"lower(publicacionConcursoRevDet.respuesta) like lower(concat(#{publicacionConcursoRevDetList.publicacionConcursoRevDet.respuesta},'%'))",
			"lower(publicacionConcursoRevDet.usuRpta) like lower(concat(#{publicacionConcursoRevDetList.publicacionConcursoRevDet.usuRpta},'%'))",
			"lower(publicacionConcursoRevDet.usuMod) like lower(concat(#{publicacionConcursoRevDetList.publicacionConcursoRevDet.usuMod},'%'))", };

	private PublicacionConcursoRevDet publicacionConcursoRevDet = new PublicacionConcursoRevDet();

	public PublicacionConcursoRevDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PublicacionConcursoRevDet getPublicacionConcursoRevDet() {
		return publicacionConcursoRevDet;
	}
}
