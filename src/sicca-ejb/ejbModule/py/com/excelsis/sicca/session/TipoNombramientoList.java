package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.TipoNombramiento;
import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("tipoNombramientoList")
public class TipoNombramientoList extends EntityQuery<TipoNombramiento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4126074559913837964L;

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<TipoNombramiento> listaTipoNombramientos;
	
	private static final String EJBQL = "select tipoNombramiento from TipoNombramiento tipoNombramiento";

	private static final String[] RESTRICTIONS = {
			"lower(tipoNombramiento.descripcion) like lower(concat('%', concat(#{tipoNombramientoList.tipoNombramiento.descripcion},'%')))",
			"tipoNombramiento.tipoPlanta.idTipoPlanta = #{tipoNombramientoList.tipoPlanta.idTipoPlanta}",
			"lower(tipoNombramiento.usuMod) like lower(concat(#{tipoNombramientoList.tipoNombramiento.usuMod},'%'))",
			"tipoNombramiento.activo=#{tipoNombramientoList.estado.valor}",
			"tipoNombramiento.tipoPlanta.activo=#{tipoNombramientoList.estadoTipoPlanta.valor}",
			"tipoNombramiento.tipoPlanta.idTipoPlanta=#{tipoNombramientoList.idTipoPlanta}",
	};
	private static final String ORDER = "tipoNombramiento.tipoPlanta.descripcion";
	private TipoNombramiento tipoNombramiento = new TipoNombramiento();
	private TipoPlanta tipoPlanta = new TipoPlanta();
	private Estado estado = Estado.ACTIVO;
	private Estado estadoTipoPlanta = Estado.ACTIVO;
	private Long idTipoPlanta;

	public TipoNombramientoList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<TipoNombramiento> listaResultados() {
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
	public List<TipoNombramiento> limpiar() {
		tipoNombramiento = new TipoNombramiento();
		estado = Estado.ACTIVO;
		idTipoPlanta = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public TipoNombramiento getTipoNombramiento() {
		return tipoNombramiento;
	}
	
	public List<TipoNombramiento> obtenerListaTipoNombramientos(){
    	listaTipoNombramientos = getResultList(); 
    	return listaTipoNombramientos;
    }
	
    public List<TipoNombramiento> getListaTipoNombramientos() {
		return listaTipoNombramientos;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdTipoPlanta() {
		return idTipoPlanta;
	}

	public void setIdTipoPlanta(Long idTipoPlanta) {
		this.idTipoPlanta = idTipoPlanta;
	}

	public TipoPlanta getTipoPlanta() {
		return tipoPlanta;
	}

	public Estado getEstadoTipoPlanta() {
		return estadoTipoPlanta;
	}

	public void setEstadoTipoPlanta(Estado estadoTipoPlanta) {
		this.estadoTipoPlanta = estadoTipoPlanta;
	}
	
    
    
}
