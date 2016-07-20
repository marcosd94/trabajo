package py.com.excelsis.sicca.session;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.GradoSalarial;
import py.com.excelsis.sicca.entity.GradoSalarialCab;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("gradoSalarialCabList")
public class GradoSalarialCabList extends EntityQuery<GradoSalarialCab> {

	private static final String EJBQL = "select gradoSalarialCab from GradoSalarialCab gradoSalarialCab";

	private static final String[] RESTRICTIONS = {
			"lower(gradoSalarialCab.usuAlta) like lower(concat(#{gradoSalarialCabList.gradoSalarialCab.usuAlta},'%'))",
			"lower(gradoSalarialCab.usuMod) like lower(concat(#{gradoSalarialCabList.gradoSalarialCab.usuMod},'%'))",
			"gradoSalarialCab.tipoCce.idTipoCce = #{gradoSalarialCabList.idTipoCce}",	
			"gradoSalarialCab.activo=#{gradoSalarialCabList.estado.valor}",
			"gradoSalarialCab.anho=#{gradoSalarialCabList.gradoSalarialCab.anho}",

	};

	

	private GradoSalarialCab gradoSalarialCab = new GradoSalarialCab();
	private static final String ORDER = "gradoSalarialCab.tipoCce.descripcion";
	private Estado estado= Estado.ACTIVO;
	private Long idTipoCce;
	
	public GradoSalarialCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<GradoSalarialCab> listaResultados() {
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
	public List<GradoSalarialCab> limpiar() {
		gradoSalarialCab= new GradoSalarialCab();
		estado = Estado.ACTIVO;
		idTipoCce=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public GradoSalarialCab getGradoSalarialCab() {
		return gradoSalarialCab;
	}

	public Long getIdTipoCce() {
		return idTipoCce;
	}

	public void setIdTipoCce(Long idTipoCce) {
		this.idTipoCce = idTipoCce;
	}
	public Estado getEstado() {
		return estado;
	}


	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	
}
