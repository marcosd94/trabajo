package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ReclamoSugerencia;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.entity.SinNivelEntidad;
import py.com.excelsis.sicca.entity.SolicProrrogaCab;
import py.com.excelsis.sicca.util.SICCASessionParameters;
import py.com.excelsis.sicca.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.TipoReclamo;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Scope(ScopeType.CONVERSATION)
@Name("reclamoSugerenciaList")
public class ReclamoSugerenciaList extends EntityQuery<ReclamoSugerencia> {

	private static final String EJBQL = "select reclamoSugerencia from ReclamoSugerencia reclamoSugerencia";

	private static final String[] RESTRICTIONS = {
			"lower(reclamoSugerencia.origen) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.origen},'%'))",
			"lower(reclamoSugerencia.asunto) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.asunto},'%'))",
			"lower(reclamoSugerencia.descripcion) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.descripcion},'%'))",
			"lower(reclamoSugerencia.reclamoSugerencia) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.reclamoSugerencia},'%'))",
			"lower(reclamoSugerencia.asuntoRespuesta) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.asuntoRespuesta},'%'))",
			"lower(reclamoSugerencia.descripcionRespuesta) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.descripcionRespuesta},'%'))",
			"lower(reclamoSugerencia.estado) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.estado},'%'))",
			"lower(reclamoSugerencia.usuAlta) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.usuAlta},'%'))",
			"lower(reclamoSugerencia.usuMod) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.usuMod},'%'))",
			"lower(reclamoSugerencia.usuPostulante) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.usuPostulante},'%'))",
			"lower(reclamoSugerencia.usuRespuesta) like lower(concat(#{reclamoSugerenciaList.reclamoSugerencia.usuRespuesta},'%'))", };
	
	private static final String[] RESTRICTIONS_CU367 = {
	
		"reclamoSugerencia.reclamoSugerencia=#{reclamoSugerenciaList.tipoReclamo.valor}",
	 };
	
	private static final String[] RESTRICTIONS_CU107 = {
		
		"reclamoSugerencia.reclamoSugerencia=#{reclamoSugerenciaList.tipoReclamo.valor}",
	 };
	
	private static final String[] RESTRICTIONS_CU200 = {
		"reclamoSugerencia.estado !=#{reclamoSugerenciaList.estP}   ",
		"date(reclamoSugerencia.fechaReclamoSugerencia) BETWEEN #{reclamoSugerenciaList.desde}",
		" #{reclamoSugerenciaList.hasta}",
		"reclamoSugerencia.postulacion.concursoPuestoAgr.concurso.configuracionUoCab.entidad.sinEntidad.idSinEntidad=#{reclamoSugerenciaList.sinEntidad.idSinEntidad}",
		"reclamoSugerencia.postulacion.concursoPuestoAgr.concurso.configuracionUoCab.entidad.sinEntidad.nenCodigo=#{reclamoSugerenciaList.sinNivelEntidad.nenCodigo}",
		"reclamoSugerencia.postulacion.concursoPuestoAgr.concurso.configuracionUoCab.idConfiguracionUo=#{reclamoSugerenciaList.configuracionUoCab.idConfiguracionUo}",
		"lower(reclamoSugerencia.postulacion.concursoPuestoAgr.concurso.nombre)like concat('%',lower(concat(#{reclamoSugerenciaList.nomConcurso},'%')))",
		"lower(reclamoSugerencia.postulacion.concursoPuestoAgr.descripcionGrupo)like concat('%',lower(concat(#{reclamoSugerenciaList.nomGrupo},'%')))",
	 };

	private static final String ORDER = "reclamoSugerencia.reclamoSugerencia, reclamoSugerencia.fechaReclamoSugerencia desc";
	private static final String ORDER_200 = "reclamoSugerencia.fechaReclamoSugerencia desc";
	private ReclamoSugerencia reclamoSugerencia = new ReclamoSugerencia();
	private TipoReclamo tipoReclamo;
	private String estado;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConfiguracionUoCab configuracionUoCab;
	private String nomConcurso;
	private String nomGrupo;
	private Date desde=null;
	private Date hasta =null;
	private String estP="P";


	
	public ReclamoSugerenciaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ReclamoSugerencia> listaResultadosCU367() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU367));
		setOrder(ORDER_200);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<ReclamoSugerencia> limpiarCU367() {
		reclamoSugerencia = new ReclamoSugerencia();
		tipoReclamo = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU367));
		setOrder(ORDER_200);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	
	/**
	 * 
	 * @return la lista resultado de la búsqueda. CU200
	 */
	public List<ReclamoSugerencia> listaResultadosCU200() {
		setEjbql(EJBQL);
		System.out.println(nomConcurso);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU200));
		setOrder(ORDER_200);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * 
	 * @return la lista completa. CU200
	 */
	public List<ReclamoSugerencia> limpiarCU200() {
		reclamoSugerencia = new ReclamoSugerencia();
		sinNivelEntidad = null;
		sinEntidad = null;
		configuracionUoCab = null;
		nomConcurso=null;
		nomGrupo=null;
		estado=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU200));
		setOrder(ORDER_200);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public ReclamoSugerencia getReclamoSugerencia() {
		return reclamoSugerencia;
	}

	public TipoReclamo getTipoReclamo() {
		return tipoReclamo;
	}

	public void setTipoReclamo(TipoReclamo tipoReclamo) {
		this.tipoReclamo = tipoReclamo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public SinNivelEntidad getSinNivelEntidad() {
		return sinNivelEntidad;
	}

	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}

	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	public String getNomConcurso() {
		return nomConcurso;
	}

	public void setNomConcurso(String nomConcurso) {
		this.nomConcurso = nomConcurso;
	}

	public String getNomGrupo() {
		return nomGrupo;
	}

	public void setNomGrupo(String nomGrupo) {
		this.nomGrupo = nomGrupo;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public String getEstP() {
		return estP;
	}

	public void setEstP(String estP) {
		this.estP = estP;
	}

	
	
	
}
