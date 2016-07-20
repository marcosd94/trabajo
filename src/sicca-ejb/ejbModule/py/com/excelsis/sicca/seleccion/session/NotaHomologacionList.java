package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.NotaHomologacion;

import java.util.Arrays;

@Name("notaHomologacionList")
public class NotaHomologacionList extends EntityQuery<NotaHomologacion> {

	private static final String EJBQL = "select notaHomologacion from NotaHomologacion notaHomologacion";

	private static final String[] RESTRICTIONS = {
			"lower(notaHomologacion.descripcion) like lower(concat(#{notaHomologacionList.notaHomologacion.descripcion},'%'))",
			"lower(notaHomologacion.lugar) like lower(concat(#{notaHomologacionList.notaHomologacion.lugar},'%'))",
			"lower(notaHomologacion.fecha) like lower(concat(#{notaHomologacionList.notaHomologacion.fecha},'%'))",
			"lower(notaHomologacion.referencia) like lower(concat(#{notaHomologacionList.notaHomologacion.referencia},'%'))",
			"lower(notaHomologacion.titulo1) like lower(concat(#{notaHomologacionList.notaHomologacion.titulo1},'%'))",
			"lower(notaHomologacion.titulo2) like lower(concat(#{notaHomologacionList.notaHomologacion.titulo2},'%'))",
			"lower(notaHomologacion.cuerpo) like lower(concat(#{notaHomologacionList.notaHomologacion.cuerpo},'%'))",
			"lower(notaHomologacion.firma) like lower(concat(#{notaHomologacionList.notaHomologacion.firma},'%'))",
			"lower(notaHomologacion.cargo) like lower(concat(#{notaHomologacionList.notaHomologacion.cargo},'%'))",
			"lower(notaHomologacion.institucionFirma) like lower(concat(#{notaHomologacionList.notaHomologacion.institucionFirma},'%'))",
			"lower(notaHomologacion.a) like lower(concat(#{notaHomologacionList.notaHomologacion.a},'%'))",
			"lower(notaHomologacion.don) like lower(concat(#{notaHomologacionList.notaHomologacion.don},'%'))",
			"lower(notaHomologacion.institucion) like lower(concat(#{notaHomologacionList.notaHomologacion.institucion},'%'))",
			"lower(notaHomologacion.itemFinal) like lower(concat(#{notaHomologacionList.notaHomologacion.itemFinal},'%'))",
			"lower(notaHomologacion.usuAlta) like lower(concat(#{notaHomologacionList.notaHomologacion.usuAlta},'%'))",
			"lower(notaHomologacion.usuMod) like lower(concat(#{notaHomologacionList.notaHomologacion.usuMod},'%'))", };

	private NotaHomologacion notaHomologacion = new NotaHomologacion();

	public NotaHomologacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public NotaHomologacion getNotaHomologacion() {
		return notaHomologacion;
	}
}
