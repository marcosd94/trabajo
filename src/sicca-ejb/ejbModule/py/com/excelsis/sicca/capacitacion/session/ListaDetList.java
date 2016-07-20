package py.com.excelsis.sicca.capacitacion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("listaDetList")
public class ListaDetList extends EntityQuery<ListaDet> {

	private static final String EJBQL = "select listaDet from ListaDet listaDet";

	private static final String[] RESTRICTIONS = {
			"lower(listaDet.tipo) like lower(concat(#{listaDetList.listaDet.tipo},'%'))",
			"lower(listaDet.usuAlta) like lower(concat(#{listaDetList.listaDet.usuAlta},'%'))",
			"lower(listaDet.observacion) like lower(concat(#{listaDetList.listaDet.observacion},'%'))",
			"lower(listaDet.usuDesv) like lower(concat(#{listaDetList.listaDet.usuDesv},'%'))", };

	private ListaDet listaDet = new ListaDet();

	public ListaDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ListaDet getListaDet() {
		return listaDet;
	}
}
