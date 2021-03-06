package py.com.excelsis.sicca.entity;

// Generated 01-oct-2012 15:29:48 by Hibernate Tools 3.4.0.Beta1

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
 * AlertasEvalDet generated by hbm2java
 */
@Entity
@Table(name = "alertas_eval_det", schema = "eval_desempeno")
public class AlertasEvalDet implements java.io.Serializable {

	private Long idAlertasEvalDet;
	private AlertasEval alertasEval;
	private Persona persona;
	private String descripcion;
	private String EMail;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public AlertasEvalDet() {
	}

	public AlertasEvalDet(Long idAlertasEvalDet, AlertasEval alertasEval,
			String EMail, boolean activo, String usuAlta, Date fechaAlta) {
		this.idAlertasEvalDet = idAlertasEvalDet;
		this.alertasEval = alertasEval;
		this.EMail = EMail;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public AlertasEvalDet(Long idAlertasEvalDet, AlertasEval alertasEval,
			Persona persona, String descripcion, String EMail, boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idAlertasEvalDet = idAlertasEvalDet;
		this.alertasEval = alertasEval;
		this.persona = persona;
		this.descripcion = descripcion;
		this.EMail = EMail;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator="ALERTAS_EVAL_DET_GENERATOR")
	@SequenceGenerator(name="ALERTAS_EVAL_DET_GENERATOR",sequenceName="eval_desempeno.alertas_eval_det_id_alertas_eval_det_seq")
	@Column(name = "id_alertas_eval_det", unique = true, nullable = false)
	public Long getIdAlertasEvalDet() {
		return this.idAlertasEvalDet;
	}

	public void setIdAlertasEvalDet(Long idAlertasEvalDet) {
		this.idAlertasEvalDet = idAlertasEvalDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_alertas_eval", nullable = false)
	@NotNull
	public AlertasEval getAlertasEval() {
		return this.alertasEval;
	}

	public void setAlertasEval(AlertasEval alertasEval) {
		this.alertasEval = alertasEval;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "descripcion", length = 150)
	@Length(max = 150)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "e_mail", nullable = false, length = 300)
	@NotNull
	@Length(max = 300)
	public String getEMail() {
		return this.EMail;
	}

	public void setEMail(String EMail) {
		this.EMail = EMail;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
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

}
