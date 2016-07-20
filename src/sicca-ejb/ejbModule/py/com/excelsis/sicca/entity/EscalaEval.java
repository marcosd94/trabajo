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
 * EscalaEval generated by hbm2java
 */
@Entity
@Table(name = "escala_eval", schema = "eval_desempeno")
public class EscalaEval implements java.io.Serializable {

	private Long idEscalaEval;
	private GrupoMetodologia grupoMetodologia;
	private String descripcion;
	private Integer desde;
	private Integer hasta;
	private String nivelEvaluacion;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public EscalaEval() {
	}

	public EscalaEval(
			Long idEscalaEval, GrupoMetodologia grupoMetodologia, String descripcion,
			Integer desde, Integer hasta, boolean activo, String usuAlta, Date fechaAlta) {
		this.idEscalaEval = idEscalaEval;
		this.grupoMetodologia = grupoMetodologia;
		this.descripcion = descripcion;
		this.desde = desde;
		this.hasta = hasta;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public EscalaEval(
			Long idEscalaEval, GrupoMetodologia grupoMetodologia, String descripcion,
			Integer desde, Integer hasta, String nivelEvaluacion, Boolean activo, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod) {
		this.idEscalaEval = idEscalaEval;
		this.grupoMetodologia = grupoMetodologia;
		this.descripcion = descripcion;
		this.desde = desde;
		this.hasta = hasta;
		this.nivelEvaluacion = nivelEvaluacion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_escala_eval", unique = true, nullable = false)
	@GeneratedValue(generator = "ESCALA_EVAL_GENERATOR")
	@SequenceGenerator(name = "ESCALA_EVAL_GENERATOR", sequenceName = "eval_desempeno.escala_eval_id_escala_eval_seq")
	public Long getIdEscalaEval() {
		return this.idEscalaEval;
	}

	public void setIdEscalaEval(Long idEscalaEval) {
		this.idEscalaEval = idEscalaEval;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupo_metodologia", nullable = false)
	@NotNull
	public GrupoMetodologia getGrupoMetodologia() {
		return this.grupoMetodologia;
	}

	public void setGrupoMetodologia(GrupoMetodologia grupoMetodologia) {
		this.grupoMetodologia = grupoMetodologia;
	}

	@Column(name = "descripcion", nullable = false, length = 250)
	@NotNull
	@Length(max = 250)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "desde", nullable = false)
	public Integer getDesde() {
		return this.desde;
	}

	public void setDesde(Integer desde) {
		this.desde = desde;
	}

	@Column(name = "hasta", nullable = false)
	public Integer getHasta() {
		return this.hasta;
	}

	public void setHasta(Integer hasta) {
		this.hasta = hasta;
	}

	@Column(name = "nivel_evaluacion", length = 500)
	@Length(max = 500)
	public String getNivelEvaluacion() {
		return this.nivelEvaluacion;
	}

	public void setNivelEvaluacion(String nivelEvaluacion) {
		this.nivelEvaluacion = nivelEvaluacion;
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
