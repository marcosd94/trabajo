package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("sujetosList")
public class SujetosList extends EntityQuery<Sujetos> {

	private static final String EJBQL = "select sujetos from Sujetos sujetos";

	private static final String[] RESTRICTIONS = {
			"lower(sujetos.usuAlta) like lower(concat(#{sujetosList.sujetos.usuAlta},'%'))",
			"lower(sujetos.usuMod) like lower(concat(#{sujetosList.sujetos.usuMod},'%'))", };

	private Sujetos sujetos = new Sujetos();

	public SujetosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Sujetos getSujetos() {
		return sujetos;
	}
}
