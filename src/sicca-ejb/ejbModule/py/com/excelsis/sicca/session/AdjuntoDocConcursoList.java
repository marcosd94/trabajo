package py.com.excelsis.sicca.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.AdjuntoDocConcurso;

import java.util.Arrays;

@Name("adjuntoDocConcursoList")
public class AdjuntoDocConcursoList extends EntityQuery<AdjuntoDocConcurso> {

	private static final String EJBQL = "select adjuntoDocConcurso from AdjuntoDocConcurso adjuntoDocConcurso";

	private static final String[] RESTRICTIONS = {
			"lower(adjuntoDocConcurso.usuAlta) like lower(concat(#{adjuntoDocConcursoList.adjuntoDocConcurso.usuAlta},'%'))",
			"lower(adjuntoDocConcurso.usuMod) like lower(concat(#{adjuntoDocConcursoList.adjuntoDocConcurso.usuMod},'%'))", };

	private AdjuntoDocConcurso adjuntoDocConcurso = new AdjuntoDocConcurso();

	public AdjuntoDocConcursoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AdjuntoDocConcurso getAdjuntoDocConcurso() {
		return adjuntoDocConcurso;
	}
}
