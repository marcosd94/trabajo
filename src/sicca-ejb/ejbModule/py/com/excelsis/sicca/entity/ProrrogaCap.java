package py.com.excelsis.sicca.entity;

// Generated 02/07/2012 09:08:40 AM by Hibernate Tools 3.4.0.Beta1

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
 * ProrrogaCap generated by hbm2java
 */
@Entity
@Table(name = "prorroga_cap", schema = "capacitacion")
public class ProrrogaCap implements java.io.Serializable {

	private Long idProrroga;
	private Capacitaciones capacitaciones;
	private String estado;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Date fechaRecDesA;
	private Date fechaRecHasA;
	private Date fechaRecDesN;
	private Date fechaRecHasN;
	private Date fechaPubDesA;
	private Date fechaPubHasA;
	private Date fechaPubDesN;
	private Date fechaPubHasN;
	private String motivo;

	public ProrrogaCap() {
	}

	public ProrrogaCap(Long idProrroga, Capacitaciones capacitaciones,
			String estado, boolean activo, String usuAlta, Date fechaAlta) {
		this.idProrroga = idProrroga;
		this.capacitaciones = capacitaciones;
		this.estado = estado;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ProrrogaCap(Long idProrroga, Capacitaciones capacitaciones,
			String estado, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod, Date fechaRecDesA, Date fechaRecHasA,
			Date fechaRecDesN, Date fechaRecHasN, Date fechaPubDesA, Date fechaPubHasA,
			Date fechaPubDesN, Date fechaPubHasN) {
		this.idProrroga = idProrroga;
		this.capacitaciones = capacitaciones;
		this.estado = estado;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.fechaRecDesA = fechaRecDesA;
		this.fechaRecHasA = fechaRecHasA;
		this.fechaRecDesN = fechaRecDesN;
		this.fechaRecHasN = fechaRecHasN;
		this.fechaPubDesA = fechaPubDesA;
		this.fechaPubHasA = fechaPubHasA;
		this.fechaPubDesN = fechaPubDesN;
		this.fechaPubHasN = fechaPubHasN;
	}

	@Id
	@GeneratedValue(generator = "PRORROGA_CAB_GENERATOR")
	@SequenceGenerator(name = "PRORROGA_CAB_GENERATOR", sequenceName = "capacitacion.prorroga_cap_id_prorroga_seq")
	@Column(name = "id_prorroga", unique = true, nullable = false)
	public Long getIdProrroga() {
		return this.idProrroga;
	}

	public void setIdProrroga(Long idProrroga) {
		this.idProrroga = idProrroga;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_capacitacion", nullable = false)
	@NotNull
	public Capacitaciones getCapacitaciones() {
		return this.capacitaciones;
	}

	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}

	@Column(name = "estado", nullable = false, length = 15)
	@NotNull
	@Length(max = 15)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return this.activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
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
	@Column(name = "fecha_rec_des_a", length = 29)
	public Date getFechaRecDesA() {
		return this.fechaRecDesA;
	}

	public void setFechaRecDesA(Date fechaRecDesA) {
		this.fechaRecDesA = fechaRecDesA;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_rec_has_a", length = 29)
	public Date getFechaRecHasA() {
		return this.fechaRecHasA;
	}

	public void setFechaRecHasA(Date fechaRecHasA) {
		this.fechaRecHasA = fechaRecHasA;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_rec_des_n", length = 29)
	public Date getFechaRecDesN() {
		return this.fechaRecDesN;
	}

	public void setFechaRecDesN(Date fechaRecDesN) {
		this.fechaRecDesN = fechaRecDesN;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_rec_has_n", length = 29)
	public Date getFechaRecHasN() {
		return this.fechaRecHasN;
	}

	public void setFechaRecHasN(Date fechaRecHasN) {
		this.fechaRecHasN = fechaRecHasN;
	}

	
	@Column(name = "motivo_cancela", length = 500)
	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_pub_des_a", length = 29)
	public Date getFechaPubDesA() {
		return this.fechaPubDesA;
	}

	public void setFechaPubDesA(Date fechaPubDesA) {
		this.fechaPubDesA = fechaPubDesA;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_pub_has_a", length = 29)
	public Date getFechaPubHasA() {
		return this.fechaPubHasA;
	}

	public void setFechaPubHasA(Date fechaPubHasA) {
		this.fechaPubHasA = fechaPubHasA;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_pub_des_n", length = 29)
	public Date getFechaPubDesN() {
		return this.fechaPubDesN;
	}

	public void setFechaPubDesN(Date fechaPubDesN) {
		this.fechaPubDesN = fechaPubDesN;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_pub_has_n", length = 29)
	public Date getFechaPubHasN() {
		return this.fechaPubHasN;
	}

	public void setFechaPubHasN(Date fechaPubHasN) {
		this.fechaPubHasN = fechaPubHasN;
	}
	

}
