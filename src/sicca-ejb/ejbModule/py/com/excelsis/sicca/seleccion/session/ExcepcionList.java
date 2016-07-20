package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

@Name("excepcionList")
public class ExcepcionList extends CustomEntityQuery<Excepcion> {

	private static final String EJBQL = "select excepcion from Excepcion excepcion WHERE excepcion.activo is true and (excepcion.estado = 'APROBADO' or excepcion.estado ='REGISTRADO' or excepcion.estado ='A SOLICITAR')";

	
	private Excepcion excepcion = new Excepcion();

	public ExcepcionList() {
		setEjbql(EJBQL);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public List<Excepcion> searchExcepciones(String query, Map<String, QueryValue> parametros) {
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		if (parametros != null && parametros.size() > 0) {
			setParams(parametros);
		}
		return getResultList(query);
	}

	public Excepcion getExcepcion() {
		return excepcion;
	}
}
