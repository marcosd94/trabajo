package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("reglaCabList")
public class ReglaCabList extends EntityQuery<ReglaCab> {

	private static final String EJBQL = "select reglaCab from ReglaCab reglaCab";

	private static final String[] RESTRICTIONS = {
			"lower(reglaCab.descripcion) like concat('%',lower(concat(#{reglaCabList.reglaCab.descripcion},'%')))",
			"reglaCab.fechaVigenciaDesde=#{reglaCabList.reglaCab.fechaVigenciaDesde}",
			"reglaCab.fechaVigenciaHasta=#{reglaCabList.reglaCab.fechaVigenciaHasta}",
			"reglaCab.activo=#{reglaCabList.estado.valor}",};

	private ReglaCab reglaCab = new ReglaCab();
	private static final String ORDER = "reglaCab.descripcion";
	private Estado estado = Estado.ACTIVO;

	public ReglaCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ReglaCab> listaResultados() {
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
	public List<ReglaCab> limpiar() {
		reglaCab = new ReglaCab();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public ReglaCab getReglaCab() {
		return reglaCab;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}
