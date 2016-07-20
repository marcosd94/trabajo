package py.com.excelsis.sicca.session;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;
import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.CptObs;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Scope(ScopeType.CONVERSATION)
@Name("cptObsList")
public class CptObsList extends CustomEntityQuery<CptObs> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<CptObs> listaCptObs;
	private static final String EJBQL = "SELECT cptObs FROM CptObs cptObs";

	private static final String[] RESTRICTIONS = {
//			"cpt.nivel=#{cptList.nivel}",
//			"lower(cpt.denominacion) like lower(concat('%', concat(#{cptList.cpt.denominacion},'%')))",
//			"cpt.numero=#{cptList.nro}",
//			"cpt.tipoCpt.idTipoCpt=#{cptList.idTipoCpt}",
//			"cpt.nroEspecifico=#{cptList.cpt.nroEspecifico}",
			"cptObs.activo=#{cptList.estado.valor}",
	};
	
	

	private static final String ORDER = "cptObs.activo";
	private CptObs cptObs = new CptObs();
	private Estado estado = Estado.ACTIVO;
	
	

	public CptObsList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CptObs> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<CptObs> listaResultadosCpt(String ejbql) {
		//setEjbql(ejbql);
		setCustomEjbql(ejbql);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<CptObs> lista = getResultList(); 
		return lista;
		
		
	}


	
	/**
	 * 
	 * @return la lista completa.
	 */
	public List<CptObs> limpiar() {
		cptObs = new CptObs();
		estado = Estado.ACTIVO;
		
		
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	
//	GETTERS Y SETTERS
	public CptObs getCptObs() {
		return cptObs;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
	public List<CptObs> obtenerListaCptObs(){
    	listaCptObs = getResultList(); 
    	return listaCptObs;
    }
    public List<CptObs> getListaCptObs() {
		return listaCptObs;
	}
	

	
	
}
