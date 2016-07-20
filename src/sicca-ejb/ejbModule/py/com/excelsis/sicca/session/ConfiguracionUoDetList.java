package py.com.excelsis.sicca.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.ScopeType;

import enums.Estado;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.juridicos.reportes.form.LisAccInsLeyArtFC;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Name("configuracionUoDetList")
public class ConfiguracionUoDetList extends EntityQuery<ConfiguracionUoDet> {

	@Out(scope = ScopeType.CONVERSATION, required = false)
	private List<ConfiguracionUoDet> listaConfiguracionUoDets;

	private static final String EJBQL =
		"select configuracionUoDet from ConfiguracionUoDet configuracionUoDet";

	private static final String[] RESTRICTIONS =
		{
			"lower(configuracionUoDet.descripcionCorta) like lower(concat(#{configuracionUoDetList.configuracionUoDet.descripcionCorta},'%'))",
			"lower(configuracionUoDet.denominacionUnidad) like lower(concat('%',concat(#{configuracionUoDetList.configuracionUoDet.denominacionUnidad},'%')))",
			"lower(configuracionUoDet.descripcionFinalidad) like lower(concat(#{configuracionUoDetList.configuracionUoDet.descripcionFinalidad},'%'))",
			"lower(configuracionUoDet.direccion) like lower(concat(#{configuracionUoDetList.configuracionUoDet.direccion},'%'))",
			"lower(configuracionUoDet.telefono) like lower(concat(#{configuracionUoDetList.configuracionUoDet.telefono},'%'))",
			"lower(configuracionUoDet.usuAlta) like lower(concat(#{configuracionUoDetList.configuracionUoDet.usuAlta},'%'))",
			"lower(configuracionUoDet.usuMod) like lower(concat(#{configuracionUoDetList.configuracionUoDet.usuMod},'%'))",
			"configuracionUoDet.configuracionUoCab.entidad.sinEntidad.entCodigo  =#{configuracionUoDetList.entCod}",
			"configuracionUoDet.configuracionUoCab.entidad.sinEntidad.nenCodigo  =#{configuracionUoDetList.nenCodigo}",
			"configuracionUoDet.configuracionUoCab.orden  =#{configuracionUoDetList.orden}",
			"configuracionUoDet.activo =#{configuracionUoDetList.estado.valor} ",
			"configuracionUoDet.configuracionUoCab.entidad.anho  =#{configuracionUoDetList.anio}", };
	private static final String[] RESTRICTIONS_UC117 =
		{
			"configuracionUoDet.configuracionUoCab.idConfiguracionUo = #{configuracionUoDetList.configuracionUoCab.idConfiguracionUo}",
			"lower(configuracionUoDet.denominacionUnidad) like lower(concat('%', concat(#{configuracionUoDetList.configuracionUoDet.denominacionUnidad},'%')))",
			"configuracionUoDet.codigoUo like concat('%', concat(#{configuracionUoDetList.configuracionUoDet.codigoUo},'%'))",
			"configuracionUoDet.activo =#{configuracionUoDetList.activo} ", };

	private ConfiguracionUoDet configuracionUoDet = new ConfiguracionUoDet();
	private ConfiguracionUoCab configuracionUoCab = new ConfiguracionUoCab();
	private static final String ORDER = "configuracionUoCab.codigoUo";
	private Estado estado = Estado.ACTIVO;
	private Long idEntidad = null;
	private BigDecimal nenCodigo;
	private Integer orden;
	private Boolean activo;
	private Integer anio;
	private BigDecimal entCod;

	public ConfiguracionUoDetList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
	}

	/**
	 * @return la lista resultado de la búsqueda.
	 */
	public List<ConfiguracionUoDet> listaResultados() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista resultado de la búsqueda para el UC117
	 */
	public List<ConfiguracionUoDet> listaResultadosUC117() {
		
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_UC117));
//		setOrder("configuracionUoCab.entidad.sinEntidad.nenCodigo,configuracionUoCab.entidad.sinEntidad.entCodigo,configuracionUoCab.orden");
		setOrder( ""
				+ " configuracionUoDet.codigoUo"
				);
		
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
			
		return getResultList();
		
		}
	
	/**
	 * @return la lista completa.
	 */
	public List<ConfiguracionUoDet> limpiar() {
		configuracionUoDet = new ConfiguracionUoDet();
		estado = Estado.ACTIVO;
		idEntidad = null;
		nenCodigo = null;
		orden = null;
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setOrder(ORDER);
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		return getResultList();
	}

	/**
	 * @return la lista completa para el UC117
	 */
	public List<ConfiguracionUoDet> limpiarUC117() {
		configuracionUoDet = new ConfiguracionUoDet();
		configuracionUoCab = new ConfiguracionUoCab();
		activo = Estado.ACTIVO.getValor();
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS_UC117));
		setMaxResults(SICCASessionParameters.SEARCH_MAX_RESULT);
		setOrder("configuracionUoDet.denominacionUnidad");
		return getResultList();
	}
	
	
	// GETTERS Y SETTERS
	public ConfiguracionUoDet getConfiguracionUoDet() {
		return configuracionUoDet;
	}

	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public List<ConfiguracionUoDet> obtenerListaConfiguracionUoDets() {
				
		listaConfiguracionUoDets = getResultList();
		return listaConfiguracionUoDets;
	}

	public List<ConfiguracionUoDet> getListaConfiguracionUoDets() {
		return listaConfiguracionUoDets;
	}
	
	public void setListaConfiguracionUoDets(List<ConfiguracionUoDet> lista) {
		this.listaConfiguracionUoDets = lista;
		
			
	}
	

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
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

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Boolean getActivo() {
		return activo;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public BigDecimal getEntCod() {
		return entCod;
	}

	public void setEntCod(BigDecimal entCod) {
		this.entCod = entCod;
	}

}
