package py.com.excelsis.sicca.capacitacion.session;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import py.com.excelsis.sicca.entity.DatosEspecificos;
import py.com.excelsis.sicca.entity.EvaluacionInscPost;
import py.com.excelsis.sicca.entity.Persona;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import java.util.Arrays;
import java.util.List;

@Name("evaluacionInscPostList")
public class EvaluacionInscPostList extends EntityQuery<EvaluacionInscPost> {

	private static final String EJBQL = "select evaluacionInscPost from EvaluacionInscPost evaluacionInscPost";

	private static final String[] RESTRICTIONS = {
			"lower(evaluacionInscPost.postulacionCap.persona.nombres) like lower(concat('%',concat(#{evaluacionInscPostList.persona.nombres},'%')))",
			"lower(evaluacionInscPost.postulacionCap.persona.apellidos) like lower(concat('%',concat(#{evaluacionInscPostList.persona.apellidos},'%')))",
			"evaluacionInscPost.postulacionCap.persona.documentoIdentidad  = #{evaluacionInscPostList.persona.documentoIdentidad} ",
			"evaluacionInscPost.postulacionCap.persona.paisByIdPaisExpedicionDoc.idPais = #{evaluacionInscPostList.idPaisExp} ",
			"evaluacionInscPost.activo =#{evaluacionInscPostList.estado.valor} ", 
			"evaluacionInscPost.evaluacionTipo.tipo =#{evaluacionInscPostList.tipo}",
			"evaluacionInscPost.resultado =#{evaluacionInscPostList.evaluacionInscPost.resultado}",
			"evaluacionInscPost.evaluacionTipo.capacitaciones.idCapacitacion =#{evaluacionInscPostList.idCapacitacion} ", };

	private EvaluacionInscPost evaluacionInscPost = new EvaluacionInscPost();
	private static final String ORDER = "evaluacionInscPost.idEvalIp";
	private Persona persona= new Persona();
	private Long idPaisExp=null;
	private Estado estado=Estado.ACTIVO;
	private String tipo=null;
	private Long idCapacitacion=null;
	public EvaluacionInscPostList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda. cu 488
	 */
	public List<EvaluacionInscPost> listaResultadosCU488() {
		evaluacionInscPost.setResultado("L");
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.CU 488
	 */
	public List<EvaluacionInscPost> limpiarCU488() {
		evaluacionInscPost= new EvaluacionInscPost();
		evaluacionInscPost.setResultado("L");
		idPaisExp=null;
		persona= new Persona();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public EvaluacionInscPost getEvaluacionInscPost() {
		return evaluacionInscPost;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdPaisExp() {
		return idPaisExp;
	}

	public void setIdPaisExp(Long idPaisExp) {
		this.idPaisExp = idPaisExp;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setEvaluacionInscPost(EvaluacionInscPost evaluacionInscPost) {
		this.evaluacionInscPost = evaluacionInscPost;
	}

	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}
	
	
}
