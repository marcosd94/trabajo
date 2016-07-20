package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("feriadosList")
public class FeriadosList extends EntityQuery<Feriados> {

	private static final String EJBQL = "select feriados from Feriados feriados";

	private static final String[] RESTRICTIONS = {
			"lower(feriados.descripcion) like lower(concat('%', concat(#{feriadosList.feriados.descripcion},'%')))",
			"feriados.activo=#{feriadosList.estado.valor}", };

	private static final String ORDER = "feriados.fecha desc";
	private Feriados feriados = new Feriados();
	private Estado estado = Estado.ACTIVO;

	public FeriadosList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Feriados> listaResultados() {
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
	public List<Feriados> limpiar() {
		feriados = new Feriados();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public Feriados getFeriados() {
		return feriados;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
