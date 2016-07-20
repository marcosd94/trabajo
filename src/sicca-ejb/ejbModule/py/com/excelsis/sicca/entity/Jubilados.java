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
 * Jubilados generated by hbm2java
 */
@Entity
@Table(name = "jubilados", schema = "desvinculacion")
public class Jubilados implements java.io.Serializable {

	private Long idJubilado;
	private EmpleadoPuesto empleadoPuesto;
	private Boolean inhabilitado;
	private DatosEspecificos datosEspecifTipo;
	private String otroTipo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Date  fechaHabilit;
	private String usuHabilit;
	private Persona persona;
	private Date fecha;
	private ConfiguracionUoCab configuracionUoCab;
	private String motivoHabilit;


	public Jubilados() {
	}


	@Id
	@GeneratedValue(generator = "JUBILADO_GENERATOR")
	@SequenceGenerator(name = "JUBILADO_GENERATOR", sequenceName = "desvinculacion.jubilados_id_jubilado_seq")
	@Column(name = "id_jubilado", unique = true, nullable = false)
	public Long getIdJubilado() {
		return this.idJubilado;
	}

	public void setIdJubilado(Long idJubilado) {
		this.idJubilado = idJubilado;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especif_tipo")
	public DatosEspecificos getDatosEspecifTipo() {
		return datosEspecifTipo;
	}

	public void setDatosEspecifTipo(DatosEspecificos datosEspecifTipo) {
		this.datosEspecifTipo = datosEspecifTipo;
	}

	@Column(name = "otro_tipo", length = 100)
	@Length(max = 100)
	public String getOtroTipo() {
		return this.otroTipo;
	}

	public void setOtroTipo(String otroTipo) {
		this.otroTipo = otroTipo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}


	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", length = 29)
	public Date getFecha() {
		return fecha;
	}


	public void setFecha(Date fecha) {
		this.fecha = fecha;
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