package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("gruposSujetosList")
public class GruposSujetosList extends EntityQuery<GruposSujetos> {

	private static final String EJBQL = "select gruposSujetos from GruposSujetos gruposSujetos";

	private static final String[] RESTRICTIONS = {
			"lower(gruposSujetos.usuAlta) like lower(concat(#{gruposSujetosList.gruposSujetos.usuAlta},'%'))",
			"lower(gruposSujetos.usuMod) like lower(concat(#{gruposSujetosList.gruposSujetos.usuMod},'%'))", };

	private GruposSujetos gruposSujetos = new GruposSujetos();

	public GruposSujetosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public GruposSujetos getGruposSujetos() {
		return gruposSujetos;
	}
}
