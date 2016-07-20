package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.Cpt;
import py.com.excelsis.sicca.entity.Departamento;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Scope(ScopeType.CONVERSATION)
@Name("cptList")
public class CptList extends CustomEntityQuery<Cpt> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Cpt> listaCpts;
	private static final String EJBQL = "SELECT cpt FROM Cpt cpt";

	private static final String[] RESTRICTIONS = {
			"cpt.nivel=#{cptList.nivel}",
			"lower(cpt.denominacion) like lower(concat('%', concat(#{cptList.cpt.denominacion},'%')))",
			"cpt.numero=#{cptList.nro}",
			"cpt.tipoCpt.idTipoCpt=#{cptList.idTipoCpt}",
			"cpt.nroEspecifico=#{cptList.cpt.nroEspecifico}",
			"cpt.activo=#{cptList.estado.valor}",
			"cpt.estadoHomologacion= #{cptList.cpt.estadoHomologacion}",
			
	};
	
	private static final String[] RESTRICTIONS_UC192 = {
		"lower(cpt.denominacion) like lower(concat('%', concat(#{cptList.cpt.denominacion},'%')))",
		"cpt.nivel=#{cptList.cpt.nivel}",
		"cpt.numero=#{cptList.cpt.numero}",
		"cpt.tipoCpt.idTipoCpt=#{cptList.idTipoCpt}",
		"cpt.nroEspecifico=#{cptList.cpt.nroEspecifico}",
		"cpt.activo=#{cptList.cpt.activo}",
		"cpt.estadoHomologacion= #{cptList.cpt.estadoHomologacion}",
	};

	private static final String ORDER = "cpt.nivel desc,cpt.gradoSalarialMin.numero desc, cpt.gradoSalarialMax.numero  desc, cpt.numero asc ";
	private Cpt cpt = new Cpt();
	private Integer nivel;
	private Integer nro;
	private Estado estado = Estado.ACTIVO;
	private Long idTipoCpt=null;
	private Cpt cptAux = new Cpt();

	public CptList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Cpt> listaResultados() {
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
	public List<Cpt> listaResultadosCpt(String ejbql) {
		//setEjbql(ejbql);
		setCustomEjbql(ejbql);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<Cpt> lista = getResultList(); 
		return lista;
		
		
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Cpt> listaResultadosCptParaHomologacion(String ejbql) {
		//setEjbql(ejbql);
		
		
		setCustomEjbql(ejbql);
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		List<Cpt> lista = getResultList(); 
		return lista;
		
		
	}


	/**
	 * 
	 * @return la lista resultado de la búsqueda para el UC192
	 */
	public List<Cpt> buscarResultadosUC192() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_UC192));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<Cpt> limpiar() {
		cpt = new Cpt();
		estado = Estado.ACTIVO;
		nivel = null;
		nro = null;
		idTipoCpt = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	
//	GETTERS Y SETTERS
	public Cpt getCpt() {
		return cpt;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Long getIdTipoCpt() {
		return idTipoCpt;
	}
	public void setIdTipoCpt(Long idTipoCpt) {
		this.idTipoCpt = idTipoCpt;
	}
	public List<Cpt> obtenerListaCpts(){
    	listaCpts = getResultList(); 
    	return listaCpts;
    }
    public List<Cpt> getListaCpts() {
		return listaCpts;
	}
	public Integer getNivel() {
		return nivel;
	}
	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}
	public Integer getNro() {
		return nro;
	}
	public void setNro(Integer nro) {
		this.nro = nro;
	}

	public Cpt getCptAux() {
		return cptAux;
	}

	public void setCptAux(Cpt cptAux) {
		this.cptAux = cptAux;
	}
	
	

}
