package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.MemoHomologacion;

import java.util.Arrays;

@Name("memoHomologacionList")
public class MemoHomologacionList extends EntityQuery<MemoHomologacion> {

	private static final String EJBQL = "select memoHomologacion from MemoHomologacion memoHomologacion";

	private static final String[] RESTRICTIONS = {
			"lower(memoHomologacion.titulo) like lower(concat(#{memoHomologacionList.memoHomologacion.titulo},'%'))",
			"lower(memoHomologacion.texto) like lower(concat(#{memoHomologacionList.memoHomologacion.texto},'%'))",
			"lower(memoHomologacion.fecha) like lower(concat(#{memoHomologacionList.memoHomologacion.fecha},'%'))",
			"lower(memoHomologacion.ref) like lower(concat(#{memoHomologacionList.memoHomologacion.ref},'%'))",
			"lower(memoHomologacion.a) like lower(concat(#{memoHomologacionList.memoHomologacion.a},'%'))",
			"lower(memoHomologacion.usuMod) like lower(concat(#{memoHomologacionList.memoHomologacion.usuMod},'%'))",
			"lower(memoHomologacion.usuAlta) like lower(concat(#{memoHomologacionList.memoHomologacion.usuAlta},'%'))", };

	private MemoHomologacion memoHomologacion = new MemoHomologacion();

	public MemoHomologacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MemoHomologacion getMemoHomologacion() {
		return memoHomologacion;
	}
}
