package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.HistoricosEstado;
import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("historicosEstadoList")
public class HistoricosEstadoList extends EntityQuery<HistoricosEstado> {

	private static final String EJBQL = "select historicosEstado from HistoricosEstado historicosEstado";

	private static final String[] RESTRICTIONS = {
			"lower(historicosEstado.observacion) like lower(concat(#{historicosEstadoList.historicosEstado.observacion},'%'))",
			"lower(historicosEstado.usuMod) like lower(concat(#{historicosEstadoList.historicosEstado.usuMod},'%'))", };

	private HistoricosEstado historicosEstado = new HistoricosEstado();

	public HistoricosEstadoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public HistoricosEstado getHistoricosEstado() {
		return historicosEstado;
	}
}
