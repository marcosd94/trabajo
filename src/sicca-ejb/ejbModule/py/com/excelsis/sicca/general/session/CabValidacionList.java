package py.com.excelsis.sicca.general.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("cabValidacionList")
public class CabValidacionList extends EntityQuery<CabValidacion> {

	private static final String EJBQL = "select cabValidacion from CabValidacion cabValidacion";

	private static final String[] RESTRICTIONS = {
			"lower(cabValidacion.nombreGrupoValidacion) like concat('%',lower(concat(#{cabValidacionList.cabValidacion.nombreGrupoValidacion},'%')))",
			" cabValidacion.activo=#{cabValidacionList.estado.valor}", };

	private CabValidacion cabValidacion = new CabValidacion();
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "cabValidacion.idCabValidacion";

	public CabValidacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CabValidacion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<CabValidacion> limpiar() {
		cabValidacion = new CabValidacion();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	


	public CabValidacion getCabValidacion() {
		return cabValidacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
