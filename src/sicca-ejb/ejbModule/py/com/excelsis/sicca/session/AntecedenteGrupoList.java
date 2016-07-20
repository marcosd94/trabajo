package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("antecedenteGrupoList")
public class AntecedenteGrupoList extends EntityQuery<AntecedenteGrupo> {

	private static final String EJBQL = "select antecedenteGrupo from AntecedenteGrupo antecedenteGrupo";

	private static final String[] RESTRICTIONS = { "lower(antecedenteGrupo.descripcion) like lower(concat(#{antecedenteGrupoList.antecedenteGrupo.descripcion},'%'))", };

	private AntecedenteGrupo antecedenteGrupo = new AntecedenteGrupo();

	public AntecedenteGrupoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public AntecedenteGrupo getAntecedenteGrupo() {
		return antecedenteGrupo;
	}
}
