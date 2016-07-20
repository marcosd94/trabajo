package py.com.excelsis.sicca.excseleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.HistorialExcepcion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("historialExcepcionList")
public class HistorialExcepcionList extends EntityQuery<HistorialExcepcion> {

	private static final String EJBQL = "select historialExcepcion from HistorialExcepcion historialExcepcion";

	private static final String[] RESTRICTIONS = {
			"historialExcepcion.excepcion.idExcepcion =#{historialExcepcionList.idExcepcion} ", };

	private HistorialExcepcion historialExcepcion;
	private Long idExcepcion;
	private static final String ORDER = "historialExcepcion.estado,historialExcepcion.excepcion.usuAlta";
	public HistorialExcepcionList() {
		historialExcepcion = new HistorialExcepcion();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<HistorialExcepcion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public HistorialExcepcion getHistorialExcepcion() {
		return historialExcepcion;
	}

	public Long getIdExcepcion() {
		return idExcepcion;
	}

	public void setIdExcepcion(Long idExcepcion) {
		this.idExcepcion = idExcepcion;
	}
	
	
}
