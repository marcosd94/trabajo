package py.com.excelsis.sicca.excseleccion.session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConcursoPuestoAgrExc;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("concursoPuestoAgrExcList")
public class ConcursoPuestoAgrExcList extends CustomEntityQuery<ConcursoPuestoAgrExc> {

	private static final String EJBQL = "select concursoPuestoAgrExc from ConcursoPuestoAgrExc concursoPuestoAgrExc";

	private static final String[] RESTRICTIONS = {
			"lower(concursoPuestoAgrExc.estado) like lower(concat(#{concursoPuestoAgrExcList.concursoPuestoAgrExc.estado},'%'))",
			"lower(concursoPuestoAgrExc.usuAlta) like lower(concat(#{concursoPuestoAgrExcList.concursoPuestoAgrExc.usuAlta},'%'))",
			"lower(concursoPuestoAgrExc.usuMod) like lower(concat(#{concursoPuestoAgrExcList.concursoPuestoAgrExc.usuMod},'%'))", };

	private ConcursoPuestoAgrExc concursoPuestoAgrExc = new ConcursoPuestoAgrExc();
	private static final String ORDER596 = "agr.nroOrden";

	public ConcursoPuestoAgrExcList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */

	public List<ConcursoPuestoAgrExc> listaResultadosCU596(String ejbql) {
		setEjbql(ejbql);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder(ORDER596);
		List<ConcursoPuestoAgrExc> lista = getResultList();

		return lista;
	}

	public ConcursoPuestoAgrExc getConcursoPuestoAgrExc() {
		return concursoPuestoAgrExc;
	}
}
