package py.com.excelsis.sicca.legajo.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.DiscapacidadApoyos;

import java.util.Arrays;

@Name("discapacidadApoyosList")
public class DiscapacidadApoyosList extends EntityQuery<DiscapacidadApoyos> {

	private static final String EJBQL = "select discapacidadApoyos from DiscapacidadApoyos discapacidadApoyos";

	private static final String[] RESTRICTIONS = {
			"lower(discapacidadApoyos.comentario) like lower(concat(#{discapacidadApoyosList.discapacidadApoyos.comentario},'%'))",
			"lower(discapacidadApoyos.usuAlta) like lower(concat(#{discapacidadApoyosList.discapacidadApoyos.usuAlta},'%'))",
			"lower(discapacidadApoyos.usuMod) like lower(concat(#{discapacidadApoyosList.discapacidadApoyos.usuMod},'%'))", };

	private DiscapacidadApoyos discapacidadApoyos = new DiscapacidadApoyos();

	public DiscapacidadApoyosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public DiscapacidadApoyos getDiscapacidadApoyos() {
		return discapacidadApoyos;
	}
}
