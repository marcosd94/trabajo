package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import py.com.excelsis.sicca.entity.ComisionEval;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.GrupoMetodologia;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("grupoMetodologiaList")
public class GrupoMetodologiaList extends CustomEntityQuery<GrupoMetodologia> {

	private static final String EJBQL = "select grupoMetodologia from GrupoMetodologia grupoMetodologia";

	private static final String[] RESTRICTIONS = {
			"lower(grupoMetodologia.tipo) like lower(concat(#{grupoMetodologiaList.grupoMetodologia.tipo},'%'))",
			"lower(grupoMetodologia.usuAlta) like lower(concat(#{grupoMetodologiaList.grupoMetodologia.usuAlta},'%'))",
			" grupoMetodologia.activo = #{grupoMetodologiaList.grupoMetodologia.activo} ",
			" grupoMetodologia.gruposEvaluacion.evaluacionDesempeno.evaluacionDesempeno.configuracionUoDet.configuracionUoCab.idConfiguracionUo =#{grupoMetodologiaList.configuracionUoCab.idConfiguracionUo}", 
			" grupoMetodologia.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno = #{grupoMetodologiaList.idEvaluacionDesempeno} ",
	};
	
	private static final String[] RESTRICTIONS_CU570 = { 
		" grupoMetodologia.activo = #{grupoMetodologiaList.grupoMetodologia.activo} ",
		" grupoMetodologia.gruposEvaluacion.evaluacionDesempeno.idEvaluacionDesempeno = #{grupoMetodologiaList.idEvaluacionDesempeno} ",
};

	private GrupoMetodologia grupoMetodologia = new GrupoMetodologia();
	private Long idEvaluacionDesempeno=null;
	private ConfiguracionUoCab configuracionUoCab= new ConfiguracionUoCab();
	private static final String ORDER = "grupoMetodologia.tipo";
	private static final String ORDER_GRUPO = "grupoMetodologia.gruposEvaluacion.grupo";
	
	
	public GrupoMetodologiaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<GrupoMetodologia> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	
	public List<GrupoMetodologia> listaResultadosEvaluacion(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU570));
		setOrder(ORDER_GRUPO);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		
		List<GrupoMetodologia> lista = getResultList(); 
		
		return lista;
	}
	public GrupoMetodologia getGrupoMetodologia() {
		return grupoMetodologia;
	}

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public void setGrupoMetodologia(GrupoMetodologia grupoMetodologia) {
		this.grupoMetodologia = grupoMetodologia;
	}
	
	
}
