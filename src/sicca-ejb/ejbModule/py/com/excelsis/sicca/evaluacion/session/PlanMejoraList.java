package py.com.excelsis.sicca.evaluacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;
 
import py.com.excelsis.sicca.entity.PlanMejora;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("planMejoraList")
public class PlanMejoraList extends EntityQuery<PlanMejora> {

	private static final String EJBQL = "select planMejora from PlanMejora planMejora";

	private static final String[] RESTRICTIONS = {
			"lower(planMejora.descripcion) like concat('%',lower(concat(#{planMejoraList.planMejora.descripcion},'%')))",
			"planMejora.identificador =#{planMejoraList.planMejora.identificador} ",
			"planMejora.activo=#{planMejoraList.estado.valor}",
			"planMejora.configuracionUo.idConfiguracionUo=#{planMejoraList.idConfiguracionUo}",
			
			};

	private PlanMejora planMejora = new PlanMejora();
	private Estado estado= Estado.ACTIVO;
	private Long idConfiguracionUo=null;
	private static final String ORDER = "planMejora.identificador";
	
	public PlanMejoraList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<PlanMejora> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public PlanMejora getPlanMejora() {
		return planMejora;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}
	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}
	
	
}
