package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("listaList")
public class ListaList extends EntityQuery<Lista> {

	private static final String EJBQL = "select lista from Lista lista";

	private static final String[] RESTRICTIONS = {
			"lower(lista.usuPublicacion) like lower(concat(#{listaList.lista.usuPublicacion},'%'))",
			"lower(lista.lugar) like lower(concat(#{listaList.lista.lugar},'%'))",
			"lower(lista.direccion) like lower(concat(#{listaList.lista.direccion},'%'))",
			"lower(lista.observacion) like lower(concat(#{listaList.lista.observacion},'%'))",
			"lower(lista.usuMod) like lower(concat(#{listaList.lista.usuMod},'%'))", };

	private Lista lista = new Lista();

	public ListaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Lista getLista() {
		return lista;
	}
}
