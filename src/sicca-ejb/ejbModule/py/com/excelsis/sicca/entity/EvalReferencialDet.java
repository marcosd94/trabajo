package py.com.excelsis.sicca.entity;

// Generated 09/12/2011 10:17:53 AM by Hibernate Tools 3.4.0.Beta1

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
 * EvalReferencialDet generated by hbm2java
 */
@Entity
@Table(name = "eval_referencial_det", schema = "seleccion")
public class EvalReferencialDet implements java.io.Serializable {

	private Long idEvalReferencialDet;
	private EvalReferencialCab evalReferencialCab;
	private MatrizRefConfDet matrizRefConfDet;
	private float puntajeRealizado;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public EvalReferencialDet() {
	}

	public EvalReferencialDet(Long idEvalReferencialDet,
			EvalReferencialCab evalReferencialCab,
			MatrizRefConfDet matrizRefConfDet, float puntajeRealizado,
			boolean activo, String usuAlta, Date fechaAlta) {
		this.idEvalReferencialDet = idEvalReferencialDet;
		this.evalReferencialCab = evalReferencialCab;
		this.matrizRefConfDet = matrizRefConfDet;
		this.puntajeRealizado = puntajeRealizado;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public EvalReferencialDet(Long idEvalReferencialDet,
			EvalReferencialCab evalReferencialCab,
			MatrizRefConfDet matrizRefConfDet, float puntajeRealizado,
			boolean activo, String usuAlta, Date fechaAlta, String usuMod,
			Date fechaMod) {
		this.idEvalReferencialDet = idEvalReferencialDet;
		this.evalReferencialCab = evalReferencialCab;
		this.matrizRefConfDet = matrizRefConfDet;
		this.puntajeRealizado = puntajeRealizado;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_eval_referencial_det", unique = true, nullable = false)
	@GeneratedValue(generator="EVAL_REFERENCIAL_DET_GENERATOR")
	@SequenceGenerator(name="EVAL_REFERENCIAL_DET_GENERATOR", sequenceName="seleccion.eval_referencial_det_id_eval_referencial_det_seq")
	public Long getIdEvalReferencialDet() {
		return this.idEvalReferencialDet;
	}

	public void setIdEvalReferencialDet(Long idEvalReferencialDet) {
		this.idEvalReferencialDet = idEvalReferencialDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_eval_referencial_cab", nullable = false)
	@NotNull
	public EvalReferencialCab getEvalReferencialCab() {
		return this.evalReferencialCab;
	}

	public void setEvalReferencialCab(EvalReferencialCab evalReferencialCab) {
		this.evalReferencialCab = evalReferencialCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matriz_ref_conf_det", nullable = false)
	@NotNull
	public MatrizRefConfDet getMatrizRefConfDet() {
		return this.matrizRefConfDet;
	}

	public void setMatrizRefConfDet(MatrizRefConfDet matrizRefConfDet) {
		this.matrizRefConfDet = matrizRefConfDet;
	}

	@Column(name = "puntaje_realizado", nullable = false, precision = 8, scale = 8)
	public float getPuntajeRealizado() {
		return this.puntajeRealizado;
	}

	public void setPuntajeRealizado(float puntajeRealizado) {
		this.puntajeRealizado = puntajeRealizado;
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

}
