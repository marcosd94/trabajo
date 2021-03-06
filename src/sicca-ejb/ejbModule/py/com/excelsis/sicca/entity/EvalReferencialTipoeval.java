package py.com.excelsis.sicca.entity;

// Generated 02/01/2012 10:19:22 AM by Hibernate Tools 3.4.0.Beta1

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
 * EvalReferencialTipoeval generated by hbm2java
 */
@Entity
@Table(name = "eval_referencial_tipoeval", schema = "seleccion")
public class EvalReferencialTipoeval implements java.io.Serializable {

	private Long idEvalReferencialTipoeval;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private DatosEspecificos datosEspecificos;
	private DatosGrupoPuesto datosGrupoPuesto;
	private String observacion;
	private String usuCierreEvaluacion;
	private Date fechaCierreEvaluacion;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<EvalReferencial> evalReferencials = new HashSet<EvalReferencial>(0);
	private Set<EvalReferencialConvocatoria> evalReferencialConvocatorias =
		new HashSet<EvalReferencialConvocatoria>(0);

	public EvalReferencialTipoeval() {
	}

	public EvalReferencialTipoeval(
			Long idEvalReferencialTipoeval, ConcursoPuestoAgr concursoPuestoAgr,
			DatosEspecificos datosEspecificos, boolean activo, String usuAlta, Date fechaAlta) {
		this.idEvalReferencialTipoeval = idEvalReferencialTipoeval;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.datosEspecificos = datosEspecificos;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public EvalReferencialTipoeval(
			Long idEvalReferencialTipoeval, ConcursoPuestoAgr concursoPuestoAgr,
			DatosEspecificos datosEspecificos, DatosGrupoPuesto datosGrupoPuesto,
			String observacion, String usuCierreEvaluacion, Date fechaCierreEvaluacion,
			boolean activo, String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			Set<EvalReferencial> evalReferencials,
			Set<EvalReferencialConvocatoria> evalReferencialConvocatorias) {
		this.idEvalReferencialTipoeval = idEvalReferencialTipoeval;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.datosEspecificos = datosEspecificos;
		this.datosGrupoPuesto = datosGrupoPuesto;
		this.observacion = observacion;
		this.usuCierreEvaluacion = usuCierreEvaluacion;
		this.fechaCierreEvaluacion = fechaCierreEvaluacion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.evalReferencials = evalReferencials;
		this.evalReferencialConvocatorias = evalReferencialConvocatorias;
	}

	@Id
	@GeneratedValue(generator = "EVAL_REFERENCIAL_CONVOCATORIA_GENERATOR")
	@SequenceGenerator(name = "EVAL_REFERENCIAL_CONVOCATORIA_GENERATOR", sequenceName = "seleccion.eval_referencial_tipoeval_id_eval_referencial_tipoeval_seq")
	@Column(name = "id_eval_referencial_tipoeval", unique = true, nullable = false)
	public Long getIdEvalReferencialTipoeval() {
		return this.idEvalReferencialTipoeval;
	}

	public void setIdEvalReferencialTipoeval(Long idEvalReferencialTipoeval) {
		this.idEvalReferencialTipoeval = idEvalReferencialTipoeval;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr", nullable = false)
	@NotNull
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return this.concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_tipo_eval", nullable = false)
	@NotNull
	public DatosEspecificos getDatosEspecificos() {
		return this.datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_grupo_puesto")
	public DatosGrupoPuesto getDatosGrupoPuesto() {
		return this.datosGrupoPuesto;
	}

	public void setDatosGrupoPuesto(DatosGrupoPuesto datosGrupoPuesto) {
		this.datosGrupoPuesto = datosGrupoPuesto;
	}

	@Column(name = "observacion", length = 250)
	@Length(max = 250)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "usu_cierre_evaluacion", length = 50)
	@Length(max = 50)
	public String getUsuCierreEvaluacion() {
		return this.usuCierreEvaluacion;
	}

	public void setUsuCierreEvaluacion(String usuCierreEvaluacion) {
		this.usuCierreEvaluacion = usuCierreEvaluacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_cierre_evaluacion", length = 29)
	public Date getFechaCierreEvaluacion() {
		return this.fechaCierreEvaluacion;
	}

	public void setFechaCierreEvaluacion(Date fechaCierreEvaluacion) {
		this.fechaCierreEvaluacion = fechaCierreEvaluacion;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evalReferencialTipoeval")
	public Set<EvalReferencial> getEvalReferencials() {
		return this.evalReferencials;
	}

	public void setEvalReferencials(Set<EvalReferencial> evalReferencials) {
		this.evalReferencials = evalReferencials;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "evalReferencialTipoeval")
	public Set<EvalReferencialConvocatoria> getEvalReferencialConvocatorias() {
		return this.evalReferencialConvocatorias;
	}

	public void setEvalReferencialConvocatorias(Set<EvalReferencialConvocatoria> evalReferencialConvocatorias) {
		this.evalReferencialConvocatorias = evalReferencialConvocatorias;
	}

}
