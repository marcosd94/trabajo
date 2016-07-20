package py.com.excelsis.sicca.session;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;

import py.com.excelsis.sicca.entity.CondicionSegur;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.Entidad;
import py.com.excelsis.sicca.entity.SinEntidad;
import py.com.excelsis.sicca.util.SICCASessionParameters;
@Scope(ScopeType.CONVERSATION)
@Name("configuracionUoCabList")
public class ConfiguracionUoCabList extends EntityQuery<ConfiguracionUoCab> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<ConfiguracionUoCab> listaConfiguracionUoCabs;
	
	private static final String EJBQL = "select configuracionUoCab from ConfiguracionUoCab configuracionUoCab";

	private static final String[] RESTRICTIONS = {
		"lower(configuracionUoCab.mision) like lower(concat(#{configuracionUoCabList.configuracionUoCab.mision},'%'))",
		"configuracionUoCab.orden = #{configuracionUoCabList.configuracionUoCab.orden}",
		"lower(configuracionUoCab.denominacionUnidad) like lower(concat('%',concat(#{configuracionUoCabList.configuracionUoCab.denominacionUnidad},'%')))",
		"lower(configuracionUoCab.codigoSinarh) like lower(concat(#{configuracionUoCabList.configuracionUoCab.codigoSinarh},'%'))",
		"configuracionUoCab.entidad.sinEntidad.nenCodigo= #{configuracionUoCabList.nenCodigo} ",
		"configuracionUoCab.entidad.sinEntidad.entCodigo= #{configuracionUoCabList.entCodigo} ",
		"configuracionUoCab.entidad.idEntidad = #{configuracionUoCabList.idEntidad}",
		"configuracionUoCab.entidad.sinEntidad.idSinEntidad = #{configuracionUoCabList.idSinEntidad}",
		"configuracionUoCab.activo=#{configuracionUoCabList.estado.valor}",
		"date(configuracionUoCab.vigenciaDesde) >= #{configuracionUoCabList.vigDesde}",
		"date(configuracionUoCab.vigenciaDesde)<= #{configuracionUoCabList.vigHasta}",
		"configuracionUoCab.entidad.anho = #{configuracionUoCabList.entidad.anho}",
	};

	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private static final String ORDER = "concat(configuracionUoCab.entidad.sinEntidad.nenCodigo,'.',configuracionUoCab.entidad.sinEntidad.entCodigo,'.',configuracionUoCab.orden)";
	private Entidad entidad = new Entidad();
	private Estado estado= Estado.ACTIVO;
	private Date vigDesde=null;
	private Date vigHasta=null ;
	private BigDecimal nenCodigo;
	private Long idEntidad;
	private Long idSinEntidad;
	private BigDecimal entCodigo;

	public ConfiguracionUoCabList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}
	
	public ConfiguracionUoCab configuracionFind(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(1);
		List<ConfiguracionUoCab> listResult = getResultList(); 
		return listResult.isEmpty() ? null : listResult.get(0);
	}

	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ConfiguracionUoCab> listaResultados() {
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
	public List<ConfiguracionUoCab> limpiar() {
		configuracionUoCab= new ConfiguracionUoCab();
		estado = Estado.ACTIVO;
		entidad = new Entidad();
		vigDesde=null;
		vigHasta=null ;
		nenCodigo=null;
		idEntidad=null;
		idSinEntidad=null;
		entCodigo=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}
	public List<ConfiguracionUoCab> limpiarTodos() {
		configuracionUoCab= new ConfiguracionUoCab();
		entCodigo=null;
		entidad= new Entidad();
		 nenCodigo=null;
		 idEntidad=null;
		idSinEntidad=null;
		estado = Estado.ACTIVO;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	public ConfiguracionUoCab searchUnidad(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(1);
		List<ConfiguracionUoCab> listResult = getResultList();
		return listResult.isEmpty() ? null : listResult.get(0);
	}

//	GETTERS Y SETTERS
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}
	
	public List<ConfiguracionUoCab> obtenerListaConfiguracionUoCabs(){
    	listaConfiguracionUoCabs = getResultList(); 
    	return listaConfiguracionUoCabs;
    }
	
    public List<ConfiguracionUoCab> getListaConfiguracionUoCabs() {
		return listaConfiguracionUoCabs;
	}

	public Entidad getEntidad() {
		return entidad;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Date getVigDesde() {
		return vigDesde;
	}

	public void setVigDesde(Date vigDesde) {
		this.vigDesde = vigDesde;
	}

	public Date getVigHasta() {
		return vigHasta;
	}

	public void setVigHasta(Date vigHasta) {
		this.vigHasta = vigHasta;
	}

	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	public Long getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}

	public Long getIdSinEntidad() {
		return idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	public BigDecimal getEntCodigo() {
		return entCodigo;
	}

	public void setEntCodigo(BigDecimal entCodigo) {
		this.entCodigo = entCodigo;
	}
	
	
	
}
