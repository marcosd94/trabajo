package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.PostulacionCapAdj;

import java.util.Arrays;

@Name("postulacionCapAdjList")
public class PostulacionCapAdjList extends EntityQuery<PostulacionCapAdj> {

	private static final String EJBQL = "select postulacionCapAdj from PostulacionCapAdj postulacionCapAdj";

	private static final String[] RESTRICTIONS = {
			"lower(postulacionCapAdj.usuAlta) like lower(concat(#{postulacionCapAdjList.postulacionCapAdj.usuAlta},'%'))",
			"lower(postulacionCapAdj.usuMod) like lower(concat(#{postulacionCapAdjList.postulacionCapAdj.usuMod},'%'))", };

	private PostulacionCapAdj postulacionCapAdj = new PostulacionCapAdj();

	public PostulacionCapAdjList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PostulacionCapAdj getPostulacionCapAdj() {
		return postulacionCapAdj;
	}
}
