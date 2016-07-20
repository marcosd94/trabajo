package py.com.excelsis.sicca.session;

import py.com.excelsis.sicca.entity.*;
import py.com.excelsis.sicca.session.util.CustomEntityQuery;
import py.com.excelsis.sicca.session.util.QueryValue;
import py.com.excelsis.sicca.util.SICCASessionParameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

import enums.Estado;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Scope(ScopeType.CONVERSATION)
@Name("concursoPuestoAgrList")
public class ConcursoPuestoAgrList extends CustomEntityQuery<ConcursoPuestoAgr> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<ConcursoPuestoAgr> listaConcursosAgr;
	
	private static final String EJBQL = "select concursoPuestoAgr from ConcursoPuestoAgr concursoPuestoAgr";

	private static final String[] RESTRICTIONS = {
			"lower(concursoPuestoAgr.codGrupo) like lower(concat(#{concursoPuestoAgrList.concursoPuestoAgr.codGrupo},'%'))",
			"lower(concursoPuestoAgr.descripcionGrupo) like lower(concat(#{concursoPuestoAgrList.concursoPuestoAgr.descripcionGrupo},'%'))",
			"lower(concursoPuestoAgr.observacion) like lower(concat(#{concursoPuestoAgrList.concursoPuestoAgr.observacion},'%'))",
			"lower(concursoPuestoAgr.usuAlta) like lower(concat(#{concursoPuestoAgrList.concursoPuestoAgr.usuAlta},'%'))",
			"concursoPuestoAgr.concurso.idConcurso =#{concursoPuestoAgrList.idConcurso}",
			"lower(concursoPuestoAgr.usuMod) like lower(concat(#{concursoPuestoAgrList.concursoPuestoAgr.usuMod},'%'))",
			"concursoPuestoAgr.activo = #{concursoPuestoAgrList.activo}",
			"concursoPuestoAgr.estado =#{concursoPuestoAgrList.valorEstado}", };
	
	
	private static final String[] RESTRICTIONS_CU463 = {
		"lower(concursoPuestoAgr.descripcionGrupo) like concat('%',lower(concat(#{concursoPuestoAgrList.grupo},'%')))",
		"lower(concursoPuestoAgr.concurso.nombre) like concat('%',lower(concat(#{concursoPuestoAgrList.concurso},'%')))",
		"concursoPuestoAgr.activo = #{concursoPuestoAgrList.activo}",
		"concursoPuestoAgr.concurso.nroExpediente = #{concursoPuestoAgrList.nroExpediente}",
		"concursoPuestoAgr.concurso.datosEspecificosTipoConc.idDatosEspecificos = #{concursoPuestoAgrList.idTipoConcurso}",
		"year(concursoPuestoAgr.concurso.fechaExpediente) = #{concursoPuestoAgrList.anho}",
		"concursoPuestoAgr.concurso.configuracionUoCab.idConfiguracionUo = #{concursoPuestoAgrList.idConfiguracionUo}",};
	
	

	private ConcursoPuestoAgr concursoPuestoAgr = new ConcursoPuestoAgr();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "concursoPuestoAgr.codGrupo";
	private static final String ORDER414 = "concursoPuestoAgr.nroOrden";
	private Long idConcurso;
	private Integer valorEstado;
	private Boolean activo;
	private String concurso;
	private String grupo;
	private Long idConfiguracionUo;
	private Integer nroExpediente;
	private Integer anho ;
	private Long idTipoConcurso;
	
	public ConcursoPuestoAgrList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	
	
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ConcursoPuestoAgr> listaResultados() {
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
	
	public List<ConcursoPuestoAgr> listaResultadosCU414(String ejbql){
		setEjbql(ejbql);
		setMaxResults(null);
		setOrder(ORDER414);
		List<ConcursoPuestoAgr> lista = getResultList(); 
		
		return lista;
	}

	/**
	 * 
	 * @return la lista completa.
	 */
	public List<ConcursoPuestoAgr> limpiar() {
		concursoPuestoAgr= new ConcursoPuestoAgr();
		estado = Estado.ACTIVO;
		idConcurso=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	
	public List<ConcursoPuestoAgr> listaResultadosCU463() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_CU463));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	
	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getIdConcurso() {
		return idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	public Integer getValorEstado() {
		return valorEstado;
	}

	public void setValorEstado(Integer valorEstado) {
		this.valorEstado = valorEstado;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	public List<ConcursoPuestoAgr> obtenerListaConcursosAgr(){
    //	listaConcursosAgr = listaResultadosCU414(); 
    	return listaConcursosAgr;
    }
	
    public List<ConcursoPuestoAgr> getListaConcursosAgr() {
		return listaConcursosAgr;
	}

	public void setConcurso(String concurso) {
		this.concurso = concurso;
	}

	public String getConcurso() {
		return concurso;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}

	public Long getIdConfiguracionUo() {
		return idConfiguracionUo;
	}

	public Integer getNroExpediente() {
		return nroExpediente;
	}

	public void setNroExpediente(Integer nroExpediente) {
		this.nroExpediente = nroExpediente;
	}

	public Integer getAnho() {
		return anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	public Long getIdTipoConcurso() {
		return idTipoConcurso;
	}

	public void setIdTipoConcurso(Long idTipoConcurso) {
		this.idTipoConcurso = idTipoConcurso;
	}
	
	
}
