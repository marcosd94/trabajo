package py.com.excelsis.sicca.entity;

// Generated 21-jun-2012 8:24:32 by Hibernate Tools 3.4.0.Beta1

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
 * RevisionCapacitacion generated by hbm2java
 */
@Entity
@Table(name = "revision_capacitacion", schema = "capacitacion")
public class RevisionCapacitacion implements java.io.Serializable {

	private Long idRevision;
	private Capacitaciones capacitaciones;
	private String observacion;
	private String usuObs;
	private Date fechaObs;
	private String respuesta;
	private String usuRpta;
	private Date fechaRpta;

	public RevisionCapacitacion() {
	}

	public RevisionCapacitacion(Long idRevision, Capacitaciones capacitaciones,
			String observacion, String usuObs, Date fechaObs) {
		this.idRevision = idRevision;
		this.capacitaciones = capacitaciones;
		this.observacion = observacion;
		this.usuObs = usuObs;
		this.fechaObs = fechaObs;
	}

	public RevisionCapacitacion(Long idRevision, Capacitaciones capacitaciones,
			String observacion, String usuObs, Date fechaObs, String respuesta,
			String usuRpta, Date fechaRpta) {
		this.idRevision = idRevision;
		this.capacitaciones = capacitaciones;
		this.observacion = observacion;
		this.usuObs = usuObs;
		this.fechaObs = fechaObs;
		this.respuesta = respuesta;
		this.usuRpta = usuRpta;
		this.fechaRpta = fechaRpta;
	}

	@Id
	@GeneratedValue(generator="REVISION_CAPACITACION_GENERATOR")
	@SequenceGenerator(name="REVISION_CAPACITACION_GENERATOR",sequenceName="capacitacion.revision_capacitacion_id_revision_seq")
	@Column(name = "id_revision", unique = true, nullable = false)
	public Long getIdRevision() {
		return this.idRevision;
	}

	public void setIdRevision(Long idRevision) {
		this.idRevision = idRevision;
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

	@Column(name = "observacion", nullable = false, length = 1000)
	@NotNull
	@Length(max = 1000)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "usu_obs", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getUsuObs() {
		return this.usuObs;
	}

	public void setUsuObs(String usuObs) {
		this.usuObs = usuObs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_obs", nullable = false, length = 29)
	@NotNull
	public Date getFechaObs() {
		return this.fechaObs;
	}

	public void setFechaObs(Date fechaObs) {
		this.fechaObs = fechaObs;
	}

	@Column(name = "respuesta", length = 1000)
	@Length(max = 1000)
	public String getRespuesta() {
		return this.respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	@Column(name = "usu_rpta", length = 50)
	@Length(max = 50)
	public String getUsuRpta() {
		return this.usuRpta;
	}

	public void setUsuRpta(String usuRpta) {
		this.usuRpta = usuRpta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_rpta", length = 29)
	public Date getFechaRpta() {
		return this.fechaRpta;
	}

	public void setFechaRpta(Date fechaRpta) {
		this.fechaRpta = fechaRpta;
	}

}