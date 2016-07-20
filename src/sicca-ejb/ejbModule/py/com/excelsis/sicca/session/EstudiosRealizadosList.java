package py.com.excelsis.sicca.session;


import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.EstudiosRealizados;

import java.util.Arrays;

@Name("estudiosRealizadosList")
public class EstudiosRealizadosList extends EntityQuery<EstudiosRealizados> {

	private static final String EJBQL = "select estudiosRealizados from EstudiosRealizados estudiosRealizados";

	private static final String[] RESTRICTIONS = {
			"lower(estudiosRealizados.otroTituloObt) like lower(concat(#{estudiosRealizadosList.estudiosRealizados.otroTituloObt},'%'))",
			"lower(estudiosRealizados.usuAlta) like lower(concat(#{estudiosRealizadosList.estudiosRealizados.usuAlta},'%'))",
			"lower(estudiosRealizados.usuMod) like lower(concat(#{estudiosRealizadosList.estudiosRealizados.usuMod},'%'))", };

	private EstudiosRealizados estudiosRealizados = new EstudiosRealizados();

	public EstudiosRealizadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EstudiosRealizados getEstudiosRealizados() {
		return estudiosRealizados;
	}
}
