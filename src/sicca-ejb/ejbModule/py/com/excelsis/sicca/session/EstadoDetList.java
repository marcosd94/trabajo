package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.EstadoDet;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("estadoDetList")
public class EstadoDetList extends EntityQuery<EstadoDet> {

	private static final String EJBQL = "select estadoDet from EstadoDet estadoDet";

	private static final String[] RESTRICTIONS = {
			"lower(estadoDet.descripcion) like concat('%',lower(concat(#{estadoDetList.estadoDet.descripcion},'%')))",
			"lower(estadoDet.usuAlta) like lower(concat(#{estadoDetList.estadoDet.usuAlta},'%'))",
			"lower(estadoDet.usuMod) like lower(concat(#{estadoDetList.estadoDet.usuMod},'%'))", 
			"estadoDet.estadoCab.idEstadoCab = #{estadoDetList.idEstadoCab}",
			" estadoDet.activo=#{estadoDetList.estado.valor}",
	};
	

	private EstadoDet estadoDet = new EstadoDet();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "estadoDet.descripcion";
	private Long idEstadoCab;

	public EstadoDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EstadoDet> listaResultados() {
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
	public List<EstadoDet> limpiar() {
		estadoDet= new EstadoDet();
		estado = Estado.ACTIVO;
		idEstadoCab=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

//	GETTERS Y SETTERS
	public EstadoDet getEstadoDet() {
		return estadoDet;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Long getIdEstadoCab() {
		return idEstadoCab;
	}
	public void setIdEstadoCab(Long idEstadoCab) {
		this.idEstadoCab = idEstadoCab;
	}
	
	
	
}
