package py.com.excelsis.sicca.general.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessages;

import enums.Estado;

import java.util.Arrays;
import java.util.List;

@Name("detValidacionList")
public class DetValidacionList extends EntityQuery<DetValidacion> {

	@In(required = false)
	Usuario usuarioLogueado;
	
	@In(required = false)
	StatusMessages statusMessages;
	private static final String EJBQL = "select detValidacion from DetValidacion detValidacion";

	private static final String[] RESTRICTIONS = {
		"lower(detValidacion.nombreValidacion) like concat('%',lower(concat(#{detValidacionList.detValidacion.nombreValidacion},'%')))",
		" detValidacion.activo=#{detValidacionList.estado.valor}",
		" detValidacion.cabValidacion.idCabValidacion=#{detValidacionList.idGrupo}",};

	private DetValidacion detValidacion = new DetValidacion();
	private Long idGrupo;
	private Estado estado = Estado.ACTIVO;
	private static final String ORDER = "detValidacion.idDetValidacion";

	public DetValidacionList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	
	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<DetValidacion> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa.
	 */
	public List<DetValidacion> limpiar() {
		detValidacion = new DetValidacion();
		estado = Estado.ACTIVO;
		idGrupo = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public DetValidacion getDetValidacion() {
		return detValidacion;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}


	public Long getIdGrupo() {
		return idGrupo;
	}


	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}
	
	
}
