package py.com.excelsis.sicca.entity;

// Generated 09/08/2012 11:24:58 AM by Hibernate Tools 3.4.0.Beta1

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * EntidadOee generated by hbm2java
 */
@Entity
@Table(name = "entidad_oee", schema = "planificacion")
public class EntidadOee implements java.io.Serializable {

	private long idEntidadOee;
	private ConfiguracionUoCab configuracionUoCab;
	private Integer anho;
	private SinEntidad sinEntidad;
	private BigDecimal nenCodigo;
	private BigDecimal entCodigo;
	private Integer orden;

	public EntidadOee() {
	}

	public EntidadOee(long idEntidadOee) {
		this.idEntidadOee = idEntidadOee;
	}

	public EntidadOee(long idEntidadOee, ConfiguracionUoCab configuracionUoCab,
			Integer anho, SinEntidad idSinEntidad, BigDecimal nenCodigo, BigDecimal entCodigo,
			Integer orden) {
		this.idEntidadOee = idEntidadOee;
		this.configuracionUoCab = configuracionUoCab;
		this.anho = anho;
		this.sinEntidad = idSinEntidad;
		this.nenCodigo = nenCodigo;
		this.entCodigo = entCodigo;
		this.orden = orden;
	}

	@Id
	@Column(name = "id_entidad_oee", unique = true, nullable = false)
	@GeneratedValue(generator="ENTIDAD_OEE_GENERATOR")
	@SequenceGenerator(name="ENTIDAD_OEE_GENERATOR",sequenceName="planificacion.entidad_oee_id_entidad_oee_seq")
	public long getIdEntidadOee() {
		return this.idEntidadOee;
	}

	public void setIdEntidadOee(long idEntidadOee) {
		this.idEntidadOee = idEntidadOee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return this.configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@Column(name = "anho")
	public Integer getAnho() {
		return this.anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sin_entidad")
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}

	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	@Column(name = "nen_codigo", precision = 2, scale = 0)
	public BigDecimal getNenCodigo() {
		return nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	
	

	
	@Column(name = "ent_codigo", precision = 3, scale = 0)
	public BigDecimal getEntCodigo() {
		return entCodigo;
	}

	public void setEntCodigo(BigDecimal entCodigo) {
		this.entCodigo = entCodigo;
	}

	@Column(name = "orden")
	public Integer getOrden() {
		return this.orden;
	}

	

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

}