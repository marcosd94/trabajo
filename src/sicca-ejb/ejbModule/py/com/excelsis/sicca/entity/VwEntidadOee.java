package py.com.excelsis.sicca.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
@Entity
@Table(name = "vw_entidad_oee", schema = "planificacion")
public class VwEntidadOee {

	private Long idEntidadOee;
	private SinEntidad sinEntidad;
	private Long idNivelEntidad;
	private BigDecimal codigoNivel;
	private String nivel;
	private Short entCodigo;
	private String codigoEntidad;
	private String entidad;
	private ConfiguracionUoCab configuracionUo;
	private String codigoOee;
	private String denominacionUnidad;
	private Integer orden;

	public VwEntidadOee() {
	}

	public VwEntidadOee(Long idEntidadOee, SinEntidad idSinEntidad,
			BigDecimal codigoNivel, String nivel, Short entCodigo,
			String codigoEntidad, String entidad, ConfiguracionUoCab idConfiguracionUo,
			String codigoOee, String denominacionUnidad, Integer orden) {
		this.idEntidadOee = idEntidadOee;
		this.sinEntidad = idSinEntidad;
		this.codigoNivel = codigoNivel;
		this.nivel = nivel;
		this.entCodigo = entCodigo;
		this.codigoEntidad = codigoEntidad;
		this.entidad = entidad;
		this.configuracionUo = idConfiguracionUo;
		this.codigoOee = codigoOee;
		this.denominacionUnidad = denominacionUnidad;
		this.orden = orden;
	}

	@Id
	@Column(name = "id_entidad_oee")
	public Long getIdEntidadOee() {
		return this.idEntidadOee;
	}

	public void setIdEntidadOee(Long idEntidadOee) {
		this.idEntidadOee = idEntidadOee;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sin_entidad")
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	
	
	@Column(name = "codigo_nivel", precision = 2, scale = 0)
	public BigDecimal getCodigoNivel() {
		return codigoNivel;
	}

	public void setCodigoNivel(BigDecimal codigoNivel) {
		this.codigoNivel = codigoNivel;
	}

	
	

	@Column(name = "nivel", length = 60)
	@Length(max = 60)
	public String getNivel() {
		return this.nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	@Column(name = "ent_codigo", precision = 3, scale = 0)
	public Short getEntCodigo() {
		return this.entCodigo;
	}

	public void setEntCodigo(Short entCodigo) {
		this.entCodigo = entCodigo;
	}

	@Column(name = "codigo_entidad")
	public String getCodigoEntidad() {
		return this.codigoEntidad;
	}

	public void setCodigoEntidad(String codigoEntidad) {
		this.codigoEntidad = codigoEntidad;
	}

	@Column(name = "entidad", length = 60)
	@Length(max = 60)
	public String getEntidad() {
		return this.entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUo() {
		return configuracionUo;
	}

	public void setConfiguracionUo(ConfiguracionUoCab configuracionUo) {
		this.configuracionUo = configuracionUo;
	}

	
	@Column(name = "codigo_oee")
	public String getCodigoOee() {
		return this.codigoOee;
	}

	public void setCodigoOee(String codigoOee) {
		this.codigoOee = codigoOee;
	}

	@Column(name = "denominacion_unidad", length = 100)
	@Length(max = 100)
	public String getDenominacionUnidad() {
		return this.denominacionUnidad;
	}

	public void setDenominacionUnidad(String denominacionUnidad) {
		this.denominacionUnidad = denominacionUnidad;
	}

	@Column(name = "orden")
	public Integer getOrden() {
		return this.orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	@Column(name = "id_sin_nivel_entidad")
	public Long getIdNivelEntidad() {
		return idNivelEntidad;
	}

	public void setIdNivelEntidad(Long idNivelEntidad) {
		this.idNivelEntidad = idNivelEntidad;
	}

	


}
