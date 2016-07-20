package py.com.excelsis.sicca.movilidadLaboral.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.PAGE)
@Name("bandejaTrasladoList")
public class BandejaTrasladoList extends CustomEntityQuery<BandejaTraslado> {

	private static final String EJBQL = "select bandejaTraslado from BandejaTraslado bandejaTraslado";

	private static final String[] RESTRICTIONS = {};

	private BandejaTraslado bandejaTraslado;

	public BandejaTrasladoList() {
		bandejaTraslado = new BandejaTraslado();

		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public List<BandejaTraslado> listaResultados(String ejbql) {
		setEjbql(ejbql);
		// setCustomEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		// setOrder(ORDER);
		List<BandejaTraslado> lista = getResultList();
		return lista;
	}

	public BandejaTraslado getBandejaTraslado() {
		return bandejaTraslado;
	}
}
