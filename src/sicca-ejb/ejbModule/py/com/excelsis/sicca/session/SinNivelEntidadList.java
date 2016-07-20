package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.util.SICCASessionParameters;


@Scope(ScopeType.CONVERSATION)
@Name("sinNivelEntidadList")
public class SinNivelEntidadList extends EntityQuery<SinNivelEntidad> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<SinNivelEntidad> listaSinNivelEntidads;
	
	private static final String EJBQL = "select sinNivelEntidad from SinNivelEntidad sinNivelEntidad";

	private static final String[] RESTRICTIONS = {
			"sinNivelEntidad.nenCodigo = #{sinNivelEntidadList.sinNivelEntidad.nenCodigo}",
			"lower(sinNivelEntidad.nenNombre) like concat('%',lower(concat(#{sinNivelEntidadList.sinNivelEntidad.nenNombre},'%')))",
			"lower(sinNivelEntidad.nenNomabr) like lower(concat(#{sinNivelEntidadList.sinNivelEntidad.nenNomabr},'%'))",
			"lower(sinNivelEntidad.nenImputable) like lower(concat(#{sinNivelEntidadList.sinNivelEntidad.nenImputable},'%'))",
			"sinNivelEntidad.aniAniopre = #{sinNivelEntidadList.sinNivelEntidad.aniAniopre}",
			"sinNivelEntidad.activo=#{sinNivelEntidadList.estado.valor}",
	};
	private static final String ORDER = "sinNivelEntidad.nenNombre";
	
	private SinNivelEntidad sinNivelEntidad = new SinNivelEntidad();
	private Estado estado= Estado.ACTIVO;
	
	public SinNivelEntidadList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public SinNivelEntidad nivelEntidadMaxAnho(){
		setEjbql(EJBQL);
		setOrder("sinNivelEntidad.aniAniopre DESC");
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(1);
		List<SinNivelEntidad> listResult = getResultList(); 
		return listResult.isEmpty() ? null : listResult.get(0);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<SinNivelEntidad> listaResultados() {
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
	public List<SinNivelEntidad> limpiar() {
		sinNivelEntidad= new SinNivelEntidad();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

//	GETTERS Y SETTERS
	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}
	public List<SinNivelEntidad> obtenerListaSinNivelEntidads(){
    	listaSinNivelEntidads = getResultList(); 
    	return listaSinNivelEntidads;
    }
    public List<SinNivelEntidad> getListaSinNivelEntidads() {
		return listaSinNivelEntidads;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}
	
}
