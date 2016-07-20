package py.com.excelsis.sicca.evaluacion.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;
import java.util.List;

@Name("comisionEvalList")
public class ComisionEvalList extends EntityQuery<ComisionEval> {

	private static final String EJBQL = "select comisionEval from ComisionEval comisionEval";

	private static final String[] RESTRICTIONS = {
			"lower(comisionEval.nombre) like concat('%',lower(concat(#{comisionEvalList.comisionEval.nombre},'%')))",
			" comisionEval.activo = #{comisionEvalList.comisionEval.activo} ",
			" comisionEval.evaluacionDesempeno.configuracionUoCab.idConfiguracionUo=#{comisionEvalList.configuracionUoCab.idConfiguracionUo}",
			" comisionEval.evaluacionDesempeno.idEvaluacionDesempeno=#{comisionEvalList.idEvaluacionDesempeno}", };

	private ComisionEval comisionEval = new ComisionEval();
	private static final String ORDER = "comisionEval.nombre";
	private ConfiguracionUoCab configuracionUoCab= new ConfiguracionUoCab();
	private Long idEvaluacionDesempeno=null;
	
	public ComisionEvalList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public ComisionEval getComisionEval() {
		return comisionEval;
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ComisionEval> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public Long getIdEvaluacionDesempeno() {
		return idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}
	
	

}
