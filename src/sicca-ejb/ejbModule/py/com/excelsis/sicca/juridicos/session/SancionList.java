package py.com.excelsis.sicca.juridicos.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.Sancion;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("sancionList")
public class SancionList extends EntityQuery<Sancion> {

	private static final String EJBQL = "select sancion from Sancion sancion";

	private static final String[] RESTRICTIONS = {
		"lower(sancion.descripcion) like concat('%',lower(concat(#{sancionList.sancion.descripcion},'%')))",
		" sancion.activo=#{sancionList.estado.valor}", };

	private Sancion sancion = new Sancion();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "sancion.descripcion";

	public SancionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}
	
	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Sancion> limpiar() {
		sancion = new Sancion();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Sancion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public Sancion getSancion() {
		return sancion;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstado() {
		return estado;
	}
}
