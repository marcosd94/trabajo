package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("publicacionConcursoList")
public class PublicacionConcursoList extends EntityQuery<PublicacionConcurso> {

	private static final String EJBQL = "select publicacionConcurso from PublicacionConcurso publicacionConcurso";

	private static final String[] RESTRICTIONS = {
			"lower(publicacionConcurso.usuSolicitud) like lower(concat(#{publicacionConcursoList.publicacionConcurso.usuSolicitud},'%'))",
			"lower(publicacionConcurso.usuAprobacion) like lower(concat(#{publicacionConcursoList.publicacionConcurso.usuAprobacion},'%'))",
			"lower(publicacionConcurso.usuUltRev) like lower(concat(#{publicacionConcursoList.publicacionConcurso.usuUltRev},'%'))", };

	private PublicacionConcurso publicacionConcurso = new PublicacionConcurso();

	public PublicacionConcursoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PublicacionConcurso getPublicacionConcurso() {
		return publicacionConcurso;
	}
}
