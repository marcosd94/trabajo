package py.com.excelsis.sicca.entity;

// Generated 10-feb-2012 9:53:04 by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Inhabilitados generated by hbm2java
 */
@Entity
@Table(name = "inhabilitados", schema = "desvinculacion")
public class Inhabilitados implements java.io.Serializable {

	private Long idInhabilitado;
	private EmpleadoPuesto empleadoPuesto;
	private Boolean inhabilitado;
	private Date fechaDesde;
	private Date fechaHasta;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Date  fechaHabilit;
	private String usuHabilit;
	private String tipo;
	private Persona persona;
	private ConfiguracionUoCab configuracionUoCab;
	private String motivoHabilit;

	public Inhabilitados() {
	}

	@Id
	@GeneratedValue(generator="INHABILITADO_GENERATOR")
	@SequenceGenerator(name="INHABILITADO_GENERATOR",sequenceName="desvinculacion.inhabilitados_id_inhabilitado_seq")
	@Column(name = "id_inhabilitado", unique = true, nullable = false)
	public Long getIdInhabilitado() {
		return this.idInhabilitado;
	}

	public void setIdInhabilitado(Long idInhabilitado) {
		this.idInhabilitado = idInhabilitado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado_puesto", nullable = false)
	@NotNull
	public EmpleadoPuesto getEmpleadoPuesto() {
		return this.empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	@Column(name = "inhabilitado", nullable = false)
	public Boolean getInhabilitado() {
		return this.inhabilitado;
	}

	public void setInhabilitado(Boolean inhabilitado) {
		this.inhabilitado = inhabilitado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_desde", length = 29)
	public Date getFechaDesde() {
		return this.fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_hasta", length = 29)
	public Date getFechaHasta() {
		return this.fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	@Column(name = "usu_alta", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta", nullable = false, length = 29)
	@NotNull
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "usu_mod", length = 50)
	@Length(max = 50)
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod", length = 29)
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_habilit", length = 29)
	public Date getFechaHabilit() {
		return fechaHabilit;
	}

	public void setFechaHabilit(Date fechaHabilit) {
		this.fechaHabilit = fechaHabilit;
	}

	@Column(name = "usu_habilit", length = 50)
	@Length(max = 50)
	public String getUsuHabilit() {
		return usuHabilit;
	}

	public void setUsuHabilit(String usuHabilit) {
		this.usuHabilit = usuHabilit;
	}

	
	@Column(name = "tipo", length = 1)
	@Length(max = 1)
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@Column(name = "motivo_habilit", length = 1000)
	@Length(max = 1000)
	public String getMotivoHabilit() {
		return motivoHabilit;
	}

	public void setMotivoHabilit(String motivoHabilit) {
		this.motivoHabilit = motivoHabilit;
	}
	
	

}
