package py.com.excelsis.sicca.capacitacion.session;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import py.com.excelsis.sicca.entity.Desbloqueo;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;


@Scope(ScopeType.PAGE)
@Name("desbloqueoList")
public class DesbloqueoList extends CustomEntityQuery<Desbloqueo> {
	private static final String EJBQL = "select desbloqueo from Desbloqueo desbloqueo";

	private static final String[] RESTRICTIONS = {};

	private Desbloqueo desbloqueo;

	public DesbloqueoList() {
		desbloqueo = new Desbloqueo();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<Desbloqueo> listaResultados(String ejbql) {
		
		setEjbql(ejbql);

		// setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		// setOrder(ORDER);
		List<Desbloqueo> lista = getResultList();
		return lista;
	}

	public Desbloqueo getDesbloqueo() {
		return desbloqueo;
	}
}
