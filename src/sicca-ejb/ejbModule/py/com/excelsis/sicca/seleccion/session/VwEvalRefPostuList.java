package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.VwEvalRefPostu;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("vwEvalRefPostuList")
public class VwEvalRefPostuList extends EntityQuery<VwEvalRefPostu> {

	private static final String EJBQL = "select vwEvalRefPostu from VwEvalRefPostu vwEvalRefPostu";

	private static final String[] RESTRICTIONS = {
			"lower(vwEvalRefPostu.nombre) like concat('%',lower(concat(#{vwEvalRefPostuList.vwEvalRefPostu.nombre},'%')))",
			"lower(vwEvalRefPostu.grupo) like concat('%',lower(concat(#{vwEvalRefPostuList.vwEvalRefPostu.grupo},'%')))",
			"lower(vwEvalRefPostu.estado) like lower(concat(#{vwEvalRefPostuList.vwEvalRefPostu.estado},'%'))",
			"vwEvalRefPostu.denominacion = #{vwEvalRefPostuList.vwEvalRefPostu.denominacion} ", 
			"vwEvalRefPostu.nenCodigo = #{vwEvalRefPostuList.vwEvalRefPostu.nenCodigo} ", 
			"vwEvalRefPostu.entCodigo = #{vwEvalRefPostuList.vwEvalRefPostu.entCodigo} ", };

	private VwEvalRefPostu vwEvalRefPostu;
	private static final String ORDER = " vwEvalRefPostu.nombre ";
	
	public VwEvalRefPostuList() {
		vwEvalRefPostu = new VwEvalRefPostu();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);	
	}
	
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<VwEvalRefPostu> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<VwEvalRefPostu> limpiar() {
		vwEvalRefPostu= new VwEvalRefPostu();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	

	public VwEvalRefPostu getVwEvalRefPostu() {
		return vwEvalRefPostu;
	}
}
