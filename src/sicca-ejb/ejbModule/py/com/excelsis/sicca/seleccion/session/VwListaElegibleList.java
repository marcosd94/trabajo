package py.com.excelsis.sicca.seleccion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;


import py.com.excelsis.sicca.entity.VwListaElegible;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("vwListaElegibleList")
public class VwListaElegibleList extends EntityQuery<VwListaElegible> {

	private static final String EJBQL = "select vwListaElegible from VwListaElegible vwListaElegible";

	private static final String[] RESTRICTIONS = {
			"lower(vwListaElegible.concurso) like concat('%',lower(concat(#{vwListaElegibleList.vwListaElegible.concurso},'%')))",
			"lower(vwListaElegible.grupo) like concat('%',lower(concat(#{vwListaElegibleList.vwListaElegible.grupo},'%')))",
			"vwListaElegible.idConfiguracionUoCab = #{vwListaElegibleList.idOee} ", 
			"vwListaElegible.nenCodigo = #{vwListaElegibleList.vwListaElegible.nenCodigo} ", 
			"vwListaElegible.entCodigo = #{vwListaElegibleList.vwListaElegible.entCodigo} ", };

	private VwListaElegible vwListaElegible;
	private static final String ORDER = " vwListaElegible.concurso ";
	private Long idOee;
	
	public VwListaElegibleList() {
		vwListaElegible = new VwListaElegible();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);	
	}
	
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<VwListaElegible> listaResultados() {
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
	public List<VwListaElegible> limpiar() {
		vwListaElegible= new VwListaElegible();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	




	public VwListaElegible getVwListaElegible() {
		return vwListaElegible;
	}


	public Long getIdOee() {
		return idOee;
	}


	public void setIdOee(Long idOee) {
		this.idOee = idOee;
	}
	
	
}
