package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.TipoCpt;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("tipoCptList")
public class TipoCptList extends EntityQuery<TipoCpt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -358508917716816521L;

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<TipoCpt> listaTipoCpts;
	
	private static final String EJBQL = "select tipoCpt from TipoCpt tipoCpt";

	private static final String[] RESTRICTIONS = {
			"lower(tipoCpt.descripcion) like lower(concat('%', concat(#{tipoCptList.tipoCpt.descripcion},'%')))",
			"lower(tipoCpt.usuAlta) like lower(concat(#{tipoCptList.tipoCpt.usuAlta},'%'))",
			"lower(tipoCpt.usuMod) like lower(concat(#{tipoCptList.tipoCpt.usuMod},'%'))",
			"tipoCpt.activo=#{tipoCptList.estado.valor}",
};
	
	private static final String ORDER = "tipoCpt.descripcion";
	private TipoCpt tipoCpt = new TipoCpt();
	private Estado estado = Estado.ACTIVO;

	public TipoCptList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<TipoCpt> listaResultados() {
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
	public List<TipoCpt> limpiar() {
		tipoCpt = new TipoCpt();
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public TipoCpt getTipoCpt() {
		return tipoCpt;
	}
	
	public List<TipoCpt> obtenerListaTipoCpts(){
    	listaTipoCpts = getResultList(); 
    	return listaTipoCpts;
    }
	
    public List<TipoCpt> getListaTipoCpts() {
		return listaTipoCpts;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}
    
    
}
