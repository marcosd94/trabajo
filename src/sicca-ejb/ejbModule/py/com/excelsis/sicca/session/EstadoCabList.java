package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.EstadoCab;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("estadoCabList")
public class EstadoCabList extends EntityQuery<EstadoCab> {

	private static final String EJBQL = "select estadoCab from EstadoCab estadoCab";

	private static final String[] RESTRICTIONS = {
			"lower(estadoCab.descripcion) like concat('%',lower(concat(#{estadoCabList.estadoCab.descripcion},'%')))",
			"lower(estadoCab.usuAlta) like lower(concat(#{estadoCabList.estadoCab.usuAlta},'%'))",
			"lower(estadoCab.usuMod) like lower(concat(#{estadoCabList.estadoCab.usuMod},'%'))", 
			"estadoCab.activo=#{estadoCabList.estado.valor}",
	};

	private EstadoCab estadoCab = new EstadoCab();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "estadoCab.descripcion";

	public EstadoCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EstadoCab> listaResultados() {
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
	public List<EstadoCab> limpiar() {
		estadoCab= new EstadoCab();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

//	GETTERS Y SETTERS
	
	public EstadoCab getEstadoCab() {
		return estadoCab;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	
	
}
