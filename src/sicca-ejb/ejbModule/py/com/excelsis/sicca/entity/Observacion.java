package py.com.excelsis.sicca.entity;

// Generated 15-nov-2011 13:29:25 by Hibernate Tools 3.4.0.Beta1

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
 * Observacion generated by hbm2java
 */
@Entity
@Table(name = "observacion", schema = "seleccion")
public class Observacion implements java.io.Serializable {

	private Long idObservacion;
	private Concurso concurso;
	private String observacion;
	private Long idTaskInstance;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public Observacion() {
	}


	@Id
	@Column(name = "id_observacion", unique = true, nullable = false)
	@GeneratedValue(generator="OBSERVACION_GENERATOR")
	@SequenceGenerator(name="OBSERVACION_GENERATOR", sequenceName="seleccion.observacion_id_obs_adjuntos_seq")
	public Long getIdObservacion() {
		return this.idObservacion;
	}

	public void setIdObservacion(Long idObservacion) {
		this.idObservacion = idObservacion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso", nullable = false)
	@NotNull
	public Concurso getConcurso() {
		return this.concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	@Column(name = "observacion", length = 250)
	@Length(max = 250)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "id_task_instance")
	public Long getIdTaskInstance() {
		return this.idTaskInstance;
	}

	public void setIdTaskInstance(Long idTaskInstance) {
		this.idTaskInstance = idTaskInstance;
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
	@Column(name = "fecha_mod",length = 29)
	public Date getFechaMod() {
		return fechaMod;
	}


	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	
	

}
