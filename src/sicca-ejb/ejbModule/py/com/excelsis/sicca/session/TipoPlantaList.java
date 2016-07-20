package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.TipoPlanta;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("tipoPlantaList")
public class TipoPlantaList extends EntityQuery<TipoPlanta> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3956046920628941303L;

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<TipoPlanta> listaTipoPlantas;
	
	private static final String EJBQL = "select tipoPlanta from TipoPlanta tipoPlanta";

	private static final String[] RESTRICTIONS = {
			"lower(tipoPlanta.descripcion) like lower(concat('%', concat(#{tipoPlantaList.tipoPlanta.descripcion},'%')))",
			"tipoPlanta.activo=#{tipoPlantaList.estado.valor}",
			"lower(tipoPlanta.usuAlta) like lower(concat(#{tipoPlantaList.tipoPlanta.usuAlta},'%'))",
			"lower(tipoPlanta.usuMod) like lower(concat(#{tipoPlantaList.tipoPlanta.usuMod},'%'))",
};
	private static final String ORDER = "tipoPlanta.descripcion";
	private TipoPlanta tipoPlanta = new TipoPlanta();
	private Estado estado = Estado.ACTIVO;

	public TipoPlantaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<TipoPlanta> listaResultados() {
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
	public List<TipoPlanta> limpiar() {
		tipoPlanta = new TipoPlanta();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public TipoPlanta getTipoPlanta() {
		return tipoPlanta;
	}
	
	public List<TipoPlanta> obtenerListaTipoPlantas(){
    	listaTipoPlantas = getResultList(); 
    	return listaTipoPlantas;
    }
	
    public List<TipoPlanta> getListaTipoPlantas() {
		return listaTipoPlantas;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
    
    
}
