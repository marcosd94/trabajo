package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("movUsuariosTareaList")
public class MovUsuariosTareaList extends EntityQuery<MovUsuariosTarea> {

	private static final String EJBQL = "select movUsuariosTarea from MovUsuariosTarea movUsuariosTarea";

	private static final String[] RESTRICTIONS = {
			"lower(movUsuariosTarea.usuOrigen) like lower(concat(#{movUsuariosTareaList.movUsuariosTarea.usuOrigen},'%'))",
			"lower(movUsuariosTarea.motivo) like lower(concat(#{movUsuariosTareaList.movUsuariosTarea.motivo},'%'))",
			"lower(movUsuariosTarea.usuAlta) like lower(concat(#{movUsuariosTareaList.movUsuariosTarea.usuAlta},'%'))",
			"lower(movUsuariosTarea.usuDest) like lower(concat(#{movUsuariosTareaList.movUsuariosTarea.usuDest},'%'))",
			"lower(movUsuariosTarea.tipo) like lower(concat(#{movUsuariosTareaList.movUsuariosTarea.tipo},'%'))", };

	private MovUsuariosTarea movUsuariosTarea = new MovUsuariosTarea();

	public MovUsuariosTareaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public MovUsuariosTarea getMovUsuariosTarea() {
		return movUsuariosTarea;
	}
}
