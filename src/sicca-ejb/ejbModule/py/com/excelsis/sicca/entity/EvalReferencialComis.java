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
 * EvalReferencialComis generated by hbm2java
 */
@Entity
@Table(name = "eval_referencial_comis", schema = "seleccion")
public class EvalReferencialComis implements java.io.Serializable {

	private Long idEvalReferencialComis;
	private EvalReferencial evalReferencial;
	private EmpresaPersona empresaPersona;
	private ComisionSeleccionDet comisionSeleccionDet;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public EvalReferencialComis() {
	}

	public EvalReferencialComis(Long idEvalReferencialComis,
			EvalReferencial evalReferencial, boolean activo, String usuAlta,
			Date fechaAlta) {
		this.idEvalReferencialComis = idEvalReferencialComis;
		this.evalReferencial = evalReferencial;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public EvalReferencialComis(Long idEvalReferencialComis,
			EvalReferencial evalReferencial, EmpresaPersona empresaPersona,
			ComisionSeleccionDet comisionSeleccionDet, boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idEvalReferencialComis = idEvalReferencialComis;
		this.evalReferencial = evalReferencial;
		this.empresaPersona = empresaPersona;
		this.comisionSeleccionDet = comisionSeleccionDet;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_eval_referencial_comis", unique = true, nullable = false)
	@GeneratedValue(generator="EVAL_REFERENCIAL_COMIS_GENERATOR")
	@SequenceGenerator(name="EVAL_REFERENCIAL_COMIS_GENERATOR", sequenceName="seleccion.eval_referencial_comis_id_eval_referencial_comis_seq")
	public Long getIdEvalReferencialComis() {
		return this.idEvalReferencialComis;
	}

	public void setIdEvalReferencialComis(Long idEvalReferencialComis) {
		this.idEvalReferencialComis = idEvalReferencialComis;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_eval_referencial", nullable = false)
	@NotNull
	public EvalReferencial getEvalReferencial() {
		return this.evalReferencial;
	}

	public void setEvalReferencial(EvalReferencial evalReferencial) {
		this.evalReferencial = evalReferencial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sel_empresa_persona")
	public EmpresaPersona getEmpresaPersona() {
		return this.empresaPersona;
	}

	public void setEmpresaPersona(EmpresaPersona empresaPersona) {
		this.empresaPersona = empresaPersona;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comision_sel_det")
	public ComisionSeleccionDet getComisionSeleccionDet() {
		return this.comisionSeleccionDet;
	}

	public void setComisionSeleccionDet(
			ComisionSeleccionDet comisionSeleccionDet) {
		this.comisionSeleccionDet = comisionSeleccionDet;
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