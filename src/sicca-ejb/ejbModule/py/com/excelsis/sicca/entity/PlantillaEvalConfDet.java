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
 * PlantillaEvalConfDet generated by hbm2java
 */
@Entity
@Table(name = "plantilla_eval_conf_det", schema = "eval_desempeno")
public class PlantillaEvalConfDet implements java.io.Serializable {

	private Long idPlantillaEvalConfDet;
	private GrupoMetodologia grupoMetodologia;
	private Long orden;
	private String resultadoEsperado;
	private String actividades;
	private String indicadoresCump;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String descripcion;
	private DatosEspecificos datosEspecificoByTipoVar;
	private Set<ResultadoEval> resultadoEvals = new HashSet<ResultadoEval>(0);
	
	//agregado; Werner.
	private Float pesoPercepcion;
	//******************
	
	public PlantillaEvalConfDet() {
	}

	public PlantillaEvalConfDet(Long idPlantillaEvalConfDet,
			GrupoMetodologia grupoMetodologia, Long orden,
			String resultadoEsperado, String actividades,
			String indicadoresCump, boolean activo, String usuAlta,
			Date fechaAlta, Float pesoPercepcion) {
		this.idPlantillaEvalConfDet = idPlantillaEvalConfDet;
		this.grupoMetodologia = grupoMetodologia;
		this.orden = orden;
		this.resultadoEsperado = resultadoEsperado;
		this.actividades = actividades;
		this.indicadoresCump = indicadoresCump;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.pesoPercepcion = pesoPercepcion;
	}

	public PlantillaEvalConfDet(Long idPlantillaEvalConfDet,
			GrupoMetodologia grupoMetodologia, Long orden,
			String resultadoEsperado, String actividades,
			String indicadoresCump, boolean activo, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod,Set<ResultadoEval> resultadoEvals, Float pesoPercepcion) {
		this.idPlantillaEvalConfDet = idPlantillaEvalConfDet;
		this.grupoMetodologia = grupoMetodologia;
		this.orden = orden;
		this.resultadoEsperado = resultadoEsperado;
		this.actividades = actividades;
		this.indicadoresCump = indicadoresCump;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.resultadoEvals = resultadoEvals;
		this.pesoPercepcion = pesoPercepcion;
	}

	@Id
	@Column(name = "id_plantilla_eval_conf_det", unique = true, nullable = false)
	@GeneratedValue(generator = "PLANTILLA_EVAL_CONF_DET_GENERATOR")
	@SequenceGenerator(name = "PLANTILLA_EVAL_CONF_DET_GENERATOR", sequenceName = "eval_desempeno.plantilla_eval_conf_det_id_plantilla_eval_conf_det_seq")
	public Long getIdPlantillaEvalConfDet() {
		return this.idPlantillaEvalConfDet;
	}

	public void setIdPlantillaEvalConfDet(Long idPlantillaEvalConfDet) {
		this.idPlantillaEvalConfDet = idPlantillaEvalConfDet;
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

	@Column(name = "orden", nullable = false)
	public Long getOrden() {
		return this.orden;
	}

	public void setOrden(Long orden) {
		this.orden = orden;
	}

	@Column(name = "resultado_esperado", nullable = false, length = 1000)
	@NotNull
	@Length(max = 1000)
	public String getResultadoEsperado() {
		return this.resultadoEsperado;
	}

	public void setResultadoEsperado(String resultadoEsperado) {
		this.resultadoEsperado = resultadoEsperado;
	}

	@Column(name = "actividades",  length = 2000)
	@Length(max = 2000)
	public String getActividades() {
		return this.actividades;
	}

	public void setActividades(String actividades) {
		this.actividades = actividades;
	}

	@Column(name = "indicadores_cump", length = 2000)
	@Length(max = 2000)
	public String getIndicadoresCump() {
		return this.indicadoresCump;
	}

	public void setIndicadoresCump(String indicadoresCump) {
		this.indicadoresCump = indicadoresCump;
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
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plantillaEvalConfDet")
	public Set<ResultadoEval> getResultadoEvals() {
		return this.resultadoEvals;
	}

	public void setResultadoEvals(Set<ResultadoEval> resultadoEvals) {
		this.resultadoEvals = resultadoEvals;
	}

	@Column(name = "descripcion", length = 5000)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especif_tipo_var")
	public DatosEspecificos getDatosEspecificoByTipoVar() {
		return datosEspecificoByTipoVar;
	}

	public void setDatosEspecificoByTipoVar(
			DatosEspecificos datosEspecificoByTipoVar) {
		this.datosEspecificoByTipoVar = datosEspecificoByTipoVar;
	}

	@Column(name = "peso_percepcion", precision = 8, scale = 8)
	public Float getPesoPercepcion() {
		return pesoPercepcion;
	}

	public void setPesoPercepcion(Float pesoPercepcion) {
		this.pesoPercepcion = pesoPercepcion;
	}
	
	
}
