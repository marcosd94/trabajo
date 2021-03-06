package py.com.excelsis.sicca.entity;

// Generated 25/09/2012 10:17:38 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * PlantillaEvalEscala generated by hbm2java
 */
@Entity
@Table(name = "plantilla_eval_escala", schema = "eval_desempeno")
public class PlantillaEvalEscala implements java.io.Serializable {

	private Long  idPlantillaEvalEscala;
	private PlantillaEvalConfDet plantillaEvalConfDet;
	private EscalaEval escalaEval;
	private String descripcion;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<ResultadoEval> resultadoEvals = new HashSet<ResultadoEval>(0);

	public PlantillaEvalEscala() {
	}

	public PlantillaEvalEscala(Long idPlantillaEvalEscala,
			PlantillaEvalConfDet plantillaEvalConfDet, EscalaEval escalaEval,
			String descripcion, boolean activo, String usuAlta, Date fechaAlta) {
		this.idPlantillaEvalEscala = idPlantillaEvalEscala;
		this.plantillaEvalConfDet = plantillaEvalConfDet;
		this.escalaEval = escalaEval;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public PlantillaEvalEscala(Long idPlantillaEvalEscala,
			PlantillaEvalConfDet plantillaEvalConfDet, EscalaEval escalaEval,
			String descripcion, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod, Set<ResultadoEval> resultadoEvals) {
		this.idPlantillaEvalEscala = idPlantillaEvalEscala;
		this.plantillaEvalConfDet = plantillaEvalConfDet;
		this.escalaEval = escalaEval;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.resultadoEvals = resultadoEvals;
	}

	@Id
	@Column(name = "id_plantilla_eval_escala", unique = true, nullable = false)
	@GeneratedValue(generator = "PLANTILLA_EVAL_ESCALA_GENERATOR")
	@SequenceGenerator(name = "PLANTILLA_EVAL_ESCALA_GENERATOR", sequenceName = "eval_desempeno.plantilla_eval_escala_id_plantilla_eval_escala_seq")
	public Long getIdPlantillaEvalEscala() {
		return this.idPlantillaEvalEscala;
	}

	public void setIdPlantillaEvalEscala(Long idPlantillaEvalEscala) {
		this.idPlantillaEvalEscala = idPlantillaEvalEscala;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plantilla_eval_conf_det", nullable = false)
	@NotNull
	public PlantillaEvalConfDet getPlantillaEvalConfDet() {
		return this.plantillaEvalConfDet;
	}

	public void setPlantillaEvalConfDet(
			PlantillaEvalConfDet plantillaEvalConfDet) {
		this.plantillaEvalConfDet = plantillaEvalConfDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_escala_eval", nullable = false)
	@NotNull
	public EscalaEval getEscalaEval() {
		return this.escalaEval;
	}

	public void setEscalaEval(EscalaEval escalaEval) {
		this.escalaEval = escalaEval;
	}

	@Column(name = "descripcion", /*nullable = false,*/ length = 500)
	//@NotNull
	@Length(max = 500)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plantillaEvalEscala")
	public Set<ResultadoEval> getResultadoEvals() {
		return this.resultadoEvals;
	}

	public void setResultadoEvals(Set<ResultadoEval> resultadoEvals) {
		this.resultadoEvals = resultadoEvals;
	}
	

}
