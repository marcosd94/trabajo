package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Name("organizacionDiscapacitadosList")
public class OrganizacionDiscapacitadosList extends CustomEntityQuery<OrganizacionDiscapacitados> {

	private static final String EJBQL =
		"select organizacionDiscapacitados from OrganizacionDiscapacitados organizacionDiscapacitados";

	private static final String[] RESTRICTIONS =
		{
			"lower(organizacionDiscapacitados.nombre) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.nombre},'%'))",
			"lower(organizacionDiscapacitados.ruc) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.ruc},'%'))",
			"lower(organizacionDiscapacitados.EMail) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.EMail},'%'))",
			"lower(organizacionDiscapacitados.direccion) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.direccion},'%'))",
			"lower(organizacionDiscapacitados.telefono) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.telefono},'%'))",
			"lower(organizacionDiscapacitados.usuAlta) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.usuAlta},'%'))",
			"lower(organizacionDiscapacitados.usuMod) like lower(concat(#{organizacionDiscapacitadosList.organizacionDiscapacitados.usuMod},'%'))", };

	private OrganizacionDiscapacitados organizacionDiscapacitados =
		new OrganizacionDiscapacitados();

	public OrganizacionDiscapacitadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public List<OrganizacionDiscapacitados> searchOrgaDis(String query, Map<String, QueryValue> parametros) {
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		if (parametros != null && parametros.size() > 0) {
			setParams(parametros);
		}
		try {
			getResultList(query);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return getResultList(query);
	}

	public OrganizacionDiscapacitados getOrganizacionDiscapacitados() {
		return organizacionDiscapacitados;
	}

}
