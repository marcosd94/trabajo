package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("comisionGrupoList")
public class ComisionGrupoList extends EntityQuery<ComisionGrupo> {

	private static final String EJBQL = "select comisionGrupo from ComisionGrupo comisionGrupo";

	private static final String[] RESTRICTIONS = {
			"lower(comisionGrupo.usuAlta) like lower(concat(#{comisionGrupoList.comisionGrupo.usuAlta},'%'))",
			"lower(comisionGrupo.usuMod) like lower(concat(#{comisionGrupoList.comisionGrupo.usuMod},'%'))", };

	private ComisionGrupo comisionGrupo = new ComisionGrupo();

	public ComisionGrupoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ComisionGrupo getComisionGrupo() {
		return comisionGrupo;
	}
}
