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
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * EvaluacionInscPost generated by hbm2java
 */
@Entity
@Table(name = "evaluacion_insc_post", schema = "capacitacion", uniqueConstraints = @UniqueConstraint(columnNames = {
		"id_postulacion", "id_eval" }))
public class EvaluacionInscPost implements java.io.Serializable {

	private Long idEvalIp;
	private PostulacionCap postulacionCap;
	private EvaluacionTipo evaluacionTipo;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuEval;
	private Date fechaEval;
	private String resultado;
	private String observacion;

	public EvaluacionInscPost() {
	}

	public EvaluacionInscPost(Long idEvalIp, PostulacionCap postulacionCap,
			EvaluacionTipo evaluacionTipo, boolean activo, String usuAlta,
			Date fechaAlta) {
		this.idEvalIp = idEvalIp;
		this.postulacionCap = postulacionCap;
		this.evaluacionTipo = evaluacionTipo;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public EvaluacionInscPost(Long idEvalIp, PostulacionCap postulacionCap,
			EvaluacionTipo evaluacionTipo, boolean activo, String usuAlta,
			Date fechaAlta, String usuEval, Date fechaEval, String resultado,
			String observacion) {
		this.idEvalIp = idEvalIp;
		this.postulacionCap = postulacionCap;
		this.evaluacionTipo = evaluacionTipo;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuEval = usuEval;
		this.fechaEval = fechaEval;
		this.resultado = resultado;
		this.observacion = observacion;
	}

	@Id
	@Column(name = "id_eval_ip", unique = true, nullable = false)
	@GeneratedValue(generator = "EVALUACION_INS_POS_GENERATOR")
	@SequenceGenerator(name = "EVALUACION_INS_POS_GENERATOR", sequenceName = "capacitacion.lista_det_id_lista_seq")
	public Long getIdEvalIp() {
		return this.idEvalIp;
	}

	public void setIdEvalIp(Long idEvalIp) {
		this.idEvalIp = idEvalIp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_postulacion", nullable = false)
	@NotNull
	public PostulacionCap getPostulacionCap() {
		return this.postulacionCap;
	}

	public void setPostulacionCap(PostulacionCap postulacionCap) {
		this.postulacionCap = postulacionCap;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_eval", nullable = false)
	@NotNull
	public EvaluacionTipo getEvaluacionTipo() {
		return this.evaluacionTipo;
	}

	public void setEvaluacionTipo(EvaluacionTipo evaluacionTipo) {
		this.evaluacionTipo = evaluacionTipo;
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

	@Column(name = "usu_eval", length = 50)
	@Length(max = 50)
	public String getUsuEval() {
		return this.usuEval;
	}

	public void setUsuEval(String usuEval) {
		this.usuEval = usuEval;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_eval", length = 29)
	public Date getFechaEval() {
		return this.fechaEval;
	}

	public void setFechaEval(Date fechaEval) {
		this.fechaEval = fechaEval;
	}

	@Column(name = "resultado", length = 1)
	@Length(max = 1)
	public String getResultado() {
		return this.resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}

	@Column(name = "observacion", length = 5000)
	@Length(max = 5000)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	
}
