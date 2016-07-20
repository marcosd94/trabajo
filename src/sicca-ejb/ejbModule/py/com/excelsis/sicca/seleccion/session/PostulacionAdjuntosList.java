package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("postulacionAdjuntosList")
public class PostulacionAdjuntosList extends EntityQuery<PostulacionAdjuntos> {

	private static final String EJBQL = "select postulacionAdjuntos from PostulacionAdjuntos postulacionAdjuntos";

	private static final String[] RESTRICTIONS = {
			"lower(postulacionAdjuntos.usuAlta) like lower(concat(#{postulacionAdjuntosList.postulacionAdjuntos.usuAlta},'%'))",
			"lower(postulacionAdjuntos.usuMod) like lower(concat(#{postulacionAdjuntosList.postulacionAdjuntos.usuMod},'%'))", };

	private PostulacionAdjuntos postulacionAdjuntos = new PostulacionAdjuntos();

	public PostulacionAdjuntosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PostulacionAdjuntos getPostulacionAdjuntos() {
		return postulacionAdjuntos;
	}
}
