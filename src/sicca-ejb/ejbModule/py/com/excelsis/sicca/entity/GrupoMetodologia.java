package py.com.excelsis.sicca.entity;

// Generated 10/09/2012 04:57:06 PM by Hibernate Tools 3.4.0.Beta1

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
 * GrupoMetodologia generated by hbm2java
 */
@Entity
@Table(name = "grupo_metodologia", schema = "eval_desempeno")
public class GrupoMetodologia implements java.io.Serializable {

	private Long idGrupoMetodologia;
	private GruposEvaluacion gruposEvaluacion;
	private DatosEspecificos datosEspecifMetod;
	private boolean activo;
	private String tipo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String valor;
	private Integer estado;
	private Set<ResultadoEval> resultadoEvals = new HashSet<ResultadoEval>(0);
	private Set<ResultadoEvalComision> resultadoEvalComisions = new HashSet<ResultadoEvalComision>(
			0);
	//agregado; Werner.
	private boolean cargado;

	public GrupoMetodologia() {
	}

	public GrupoMetodologia(Long idGrupoMetodologia,
			GruposEvaluacion gruposEvaluacion, DatosEspecificos idDatosEspecifMetod,
			boolean activo, String tipo, String usuAlta, Date fechaAlta, boolean cargado) {
		this.idGrupoMetodologia = idGrupoMetodologia;
		this.gruposEvaluacion = gruposEvaluacion;
		this.datosEspecifMetod = idDatosEspecifMetod;
		this.activo = activo;
		this.tipo = tipo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.cargado = cargado;
	}

	public GrupoMetodologia(Long idGrupoMetodologia,
			GruposEvaluacion gruposEvaluacion, DatosEspecificos idDatosEspecifMetod,
			boolean activo, String tipo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod,
			Set<ResultadoEval> resultadoEvals,Set<ResultadoEvalComision> resultadoEvalComisions, boolean cargado) {
		this.idGrupoMetodologia = idGrupoMetodologia;
		this.gruposEvaluacion = gruposEvaluacion;
		this.datosEspecifMetod = idDatosEspecifMetod;
		this.activo = activo;
		this.tipo = tipo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.resultadoEvals = resultadoEvals;
		this.resultadoEvalComisions = resultadoEvalComisions;
		this.cargado = cargado;
	}

	@Id
	@Column(name = "id_grupo_metodologia", unique = true, nullable = false)
	@GeneratedValue(generator = "GRUPO_METODOLOGIA_GENERATOR")
	@SequenceGenerator(name = "GRUPO_METODOLOGIA_GENERATOR", sequenceName = "eval_desempeno.grupo_metodologia_id_grupo_metodologia_seq")
	public Long getIdGrupoMetodologia() {
		return this.idGrupoMetodologia;
	}

	public void setIdGrupoMetodologia(Long idGrupoMetodologia) {
		this.idGrupoMetodologia = idGrupoMetodologia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupos_evaluacion", nullable = false)
	@NotNull
	public GruposEvaluacion getGruposEvaluacion() {
		return this.gruposEvaluacion;
	}

	public void setGruposEvaluacion(GruposEvaluacion gruposEvaluacion) {
		this.gruposEvaluacion = gruposEvaluacion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especif_metod", nullable = false)
	public DatosEspecificos getDatosEspecifMetod() {
		return datosEspecifMetod;
	}

	public void setDatosEspecifMetod(DatosEspecificos datosEspecifMetod) {
		this.datosEspecifMetod = datosEspecifMetod;
	}

	
	

	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return this.activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	@Column(name = "tipo", nullable = false, length = 1)
	@NotNull
	@Length(max = 1)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	@Column(name = "valor", length = 1)
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Column(name = "estado")
	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "grupoMetodologia")
	public Set<ResultadoEval> getResultadoEvals() {
		return this.resultadoEvals;
	}

	public void setResultadoEvals(Set<ResultadoEval> resultadoEvals) {
		this.resultadoEvals = resultadoEvals;
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "grupoMetodologia")
	public Set<ResultadoEvalComision> getResultadoEvalComisions() {
		return this.resultadoEvalComisions;
	}

	public void setResultadoEvalComisions(
			Set<ResultadoEvalComision> resultadoEvalComisions) {
		this.resultadoEvalComisions = resultadoEvalComisions;
	}

	@Column(name = "cargado", nullable = false)
	public boolean getCargado() {
		return cargado;
	}

	public void setCargado(boolean cargado) {
		this.cargado = cargado;
	}
	
}