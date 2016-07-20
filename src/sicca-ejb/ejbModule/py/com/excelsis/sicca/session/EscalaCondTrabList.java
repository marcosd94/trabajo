package py.com.excelsis.sicca.session;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.CondicionTrabajo;
import py.com.excelsis.sicca.entity.EscalaCondTrab;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("escalaCondTrabList")
public class EscalaCondTrabList extends EntityQuery<EscalaCondTrab> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7676568533896596703L;

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<EscalaCondTrab> listaEscalaCondTrabs;
	
	private static final String EJBQL = "select escalaCondTrab from EscalaCondTrab escalaCondTrab";

	private static final String[] RESTRICTIONS = {
			"lower(escalaCondTrab.descripcion) like lower(concat('%', concat(#{escalaCondTrabList.escalaCondTrab.descripcion},'%')))",
			"lower(escalaCondTrab.usuAlta) like lower(concat(#{escalaCondTrabList.escalaCondTrab.usuAlta},'%'))",
			"lower(escalaCondTrab.usuMod) like lower(concat(#{escalaCondTrabList.escalaCondTrab.usuMod},'%'))",
			"escalaCondTrab.activo=#{escalaCondTrabList.estado.valor}",
			"escalaCondTrab.condicionTrabajo.idCondicionTrabajo=#{escalaCondTrabList.idCondicionTrabajo}",
};
	private static final String ORDER = "escalaCondTrab.condicionTrabajo.descripcion, escalaCondTrab.desde, escalaCondTrab.hasta";
	private EscalaCondTrab escalaCondTrab = new EscalaCondTrab();

	Estado estado = Estado.ACTIVO;
	private Long idCondicionTrabajo;
	public EscalaCondTrabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<EscalaCondTrab> listaResultados() {
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
	public List<EscalaCondTrab> limpiar() {
		escalaCondTrab = new EscalaCondTrab();
		estado = Estado.ACTIVO;
		idCondicionTrabajo = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}


	public EscalaCondTrab getEscalaCondTrab() {
		return escalaCondTrab;
	}
	
	public List<EscalaCondTrab> obtenerListaEscalaCondTrabs(){
    	listaEscalaCondTrabs = getResultList(); 
    	return listaEscalaCondTrabs;
    }
	
    public List<EscalaCondTrab> getListaEscalaCondTrabs() {
		return listaEscalaCondTrabs;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdCondicionTrabajo() {
		return idCondicionTrabajo;
	}

	public void setIdCondicionTrabajo(Long idCondicionTrabajo) {
		this.idCondicionTrabajo = idCondicionTrabajo;
	}
    
    
}
