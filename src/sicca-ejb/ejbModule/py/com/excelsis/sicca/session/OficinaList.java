package py.com.excelsis.sicca.session;
import java.math.BigDecimal;
import java.util.Arrays;
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
import py.com.excelsis.sicca.entity.Oficina;
import py.com.excelsis.sicca.util.SICCASessionParameters;
@Scope(ScopeType.CONVERSATION)
@Name("oficinaList")
public class OficinaList extends EntityQuery<Oficina> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
    private List<Oficina> listaOficinas;
	
	private static final String EJBQL = "select oficina from Oficina oficina";

	private static final String[] RESTRICTIONS = {
			"lower(oficina.descripcion) like concat('%',lower(concat(#{oficinaList.oficina.descripcion},'%')))",
			"lower(oficina.direccion) like lower(concat(#{oficinaList.oficina.direccion},'%'))",
			"lower(oficina.numero) like lower(concat(#{oficinaList.oficina.numero},'%'))",
			"lower(oficina.piso) like lower(concat(#{oficinaList.oficina.piso},'%'))",
			"lower(oficina.coordX) like lower(concat(#{oficinaList.oficina.coordX},'%'))",
			"lower(oficina.coordY) like lower(concat(#{oficinaList.oficina.coordY},'%'))",
			"lower(oficina.nroTelefono) like lower(concat(#{oficinaList.oficina.nroTelefono},'%'))",
			"lower(oficina.nroInterno) like lower(concat(#{oficinaList.oficina.nroInterno},'%'))",
			"lower(oficina.usuAlta) like lower(concat(#{oficinaList.oficina.usuAlta},'%'))",
			"lower(oficina.usuMod) like lower(concat(#{oficinaList.oficina.usuMod},'%'))",
			"oficina.configuracionUoCab.idConfiguracionUo =#{oficinaList.configuracionUoCab.idConfiguracionUo} ",
			"oficina.configuracionUoCab.entidad.idEntidad =#{oficinaList.idEntidad} ",
			"oficina.configuracionUoCab.entidad.sinEntidad.nenCodigo = #{oficinaList.nenCodigo} ",
			"oficina.configuracionUoCab.entidad.sinEntidad.entCodigo = #{oficinaList.entCodigo} ",
			"oficina.configuracionUoCab.entidad.sinEntidad.idSinEntidad = #{oficinaList.idSinEntidad} ",
			"oficina.ciudad.idCiudad =#{oficinaList.idCiudad} ",
			"oficina.barrio.idBarrio = #{oficinaList.idBarrio} ",
			"oficina.ciudad.departamento.idDepartamento =#{oficinaList.idDepartamento} ",
			"oficina.ciudad.departamento.pais.idPais =#{oficinaList.idPais} ",
			"oficina.activo=#{oficinaList.estado.valor}",
};

	private Oficina oficina = new Oficina();
	private Estado estado= Estado.ACTIVO;
	private static final String ORDER = "oficina.configuracionUoCab.entidad.sinEntidad.nenCodigo,oficina.configuracionUoCab.entidad.sinEntidad.entCodigo,oficina.configuracionUoCab.orden,oficina.descripcion";
	private Long idEntidad=null;
	private BigDecimal nenCodigo=null;
	private ConfiguracionUoCab configuracionUoCab= new  ConfiguracionUoCab();
	private Long idCiudad= null;
	private Long idDepartamento=null;
	private Long idBarrio=null;
	private Long idPais=null;
	private BigDecimal entCodigo=null;
	private Long idSinEntidad=null;
	
	public OficinaList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
	}
	/**
	 * 
	 * @return la lista resultado de la búsqueda.
	 */
	public List<Oficina> listaResultados() {
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
	public List<Oficina> limpiar() {
		oficina= new Oficina();
		estado = Estado.ACTIVO;
		idEntidad=null;
		nenCodigo=null;
		configuracionUoCab= new ConfiguracionUoCab();
		idCiudad= null;
		idDepartamento=null;
		idBarrio=null;
		idPais=null;
		idSinEntidad=null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

//	GETTERS Y SETTERS
	public Oficina getOficina() {
		return oficina;
	}
	
	public List<Oficina> obtenerListaOficinas(){
    	listaOficinas = getResultList(); 
    	return listaOficinas;
    }
	
    public List<Oficina> getListaOficinas() {
		return listaOficinas;
	}

	public Long getIdEntidad() {
		return idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}

	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Long getIdCiudad() {
		return idCiudad;
	}
	public void setIdCiudad(Long idCiudad) {
		this.idCiudad = idCiudad;
	}
	public Long getIdDepartamento() {
		return idDepartamento;
	}
	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}
	public Long getIdBarrio() {
		return idBarrio;
	}
	public void setIdBarrio(Long idBarrio) {
		this.idBarrio = idBarrio;
	}
	public Long getIdPais() {
		return idPais;
	}
	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}
	public BigDecimal getEntCodigo() {
		return entCodigo;
	}
	public void setEntCodigo(BigDecimal entCodigo) {
		this.entCodigo = entCodigo;
	}
	public Long getIdSinEntidad() {
		return idSinEntidad;
	}
	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}
    
    
}
