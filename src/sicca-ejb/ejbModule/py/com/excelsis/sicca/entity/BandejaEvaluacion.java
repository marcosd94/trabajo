package py.com.excelsis.sicca.entity;

// Generated 21-sep-2012 14:01:08 by Hibernate Tools 3.4.0.Beta1

import java.math.BigDecimal;
import java.util.Date;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;

/**
 * BandejaEvaluacion generated by hbm2java
 */
@Entity
@Table(name = "bandeja_evaluacion", schema = "eval_desempeno")
public class BandejaEvaluacion implements java.io.Serializable {

	private Long idEvaluacionDesempeno;
	private Boolean activo;
	private String tituloEval;
	private Long idConfiguracionUo;
	private Integer ordenConfiguracion;
	private String denominacionUnidadCab;
//	private Long idConfiguracionUoDet;
//	private Integer ordenConfiguracionDet;
//	private String denominacionUnidadDet;
	private Byte nenCodigo;
	private Short entCodigo;
	private String entNombre;
	private Byte nivelCod;
	private String nivelNombre;
	private String actividad;
	private String usuario;
	private Date fechaRecepcion;
	private Date fechaInicio;
	private Integer diasCreacion;
	private Integer diasInicio;
	private BigDecimal atraso;
	private Long idActividad;
	private Long idEntidad;
	private Long idSinNivelEntidad;
	private Long idTaskinstance;
	private Long idProcessInstance;
	private String codActividad;
	private Long idProceso;
	private ActividadProceso actividadProceso;
	private String tipoActividad;
	private Long idSinEntidad;
	private String estadoEval;

	public BandejaEvaluacion() {
	}

	public BandejaEvaluacion(Long idEvaluacionDesempeno, Boolean activo,
			String tituloEval, Long idConfiguracionUo,
			Integer ordenConfiguracion, String denominacionUnidadCab,
			/*Long idConfiguracionUoDet, Integer ordenConfiguracionDet,
			String denominacionUnidadDet,*/ Byte nenCodigo, Short entCodigo,
			String entNombre, Byte nivelCod, String nivelNombre,
			String actividad, String usuario, Date fechaRecepcion,
			Date fechaInicio, Integer diasCreacion, Integer diasInicio,
			BigDecimal atraso, Long idActividad, Long idEntidad,
			Long idSinNivelEntidad, Long idTaskinstance,
			Long idProcessInstance, String codActividad, Long idProceso,
			ActividadProceso actividadProceso, String tipoActividad, Long idSinEntidad,
			String estadoEval) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
		this.activo = activo;
		this.tituloEval = tituloEval;
		this.idConfiguracionUo = idConfiguracionUo;
		this.ordenConfiguracion = ordenConfiguracion;
		this.denominacionUnidadCab = denominacionUnidadCab;
//		this.idConfiguracionUoDet = idConfiguracionUoDet;
//		this.ordenConfiguracionDet = ordenConfiguracionDet;
//		this.denominacionUnidadDet = denominacionUnidadDet;
		this.nenCodigo = nenCodigo;
		this.entCodigo = entCodigo;
		this.entNombre = entNombre;
		this.nivelCod = nivelCod;
		this.nivelNombre = nivelNombre;
		this.actividad = actividad;
		this.usuario = usuario;
		this.fechaRecepcion = fechaRecepcion;
		this.fechaInicio = fechaInicio;
		this.diasCreacion = diasCreacion;
		this.diasInicio = diasInicio;
		this.atraso = atraso;
		this.idActividad = idActividad;
		this.idEntidad = idEntidad;
		this.idSinNivelEntidad = idSinNivelEntidad;
		this.idTaskinstance = idTaskinstance;
		this.idProcessInstance = idProcessInstance;
		this.codActividad = codActividad;
		this.idProceso = idProceso;
		this.actividadProceso = actividadProceso;
		this.tipoActividad = tipoActividad;
		this.idSinEntidad = idSinEntidad;
		this.estadoEval = estadoEval;
	}

	@Id
	@Column(name = "id_evaluacion_desempeno")
	public Long getIdEvaluacionDesempeno() {
		return this.idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Column(name = "titulo_eval", length = 250)
	@Length(max = 250)
	public String getTituloEval() {
		return this.tituloEval;
	}

	public void setTituloEval(String tituloEval) {
		this.tituloEval = tituloEval;
	}

	@Column(name = "id_configuracion_uo")
	public Long getIdConfiguracionUo() {
		return this.idConfiguracionUo;
	}

	public void setIdConfiguracionUo(Long idConfiguracionUo) {
		this.idConfiguracionUo = idConfiguracionUo;
	}

	@Column(name = "orden_configuracion")
	public Integer getOrdenConfiguracion() {
		return this.ordenConfiguracion;
	}

	public void setOrdenConfiguracion(Integer ordenConfiguracion) {
		this.ordenConfiguracion = ordenConfiguracion;
	}

	@Column(name = "denominacion_unidad_cab", length = 100)
	@Length(max = 100)
	public String getDenominacionUnidadCab() {
		return this.denominacionUnidadCab;
	}

	public void setDenominacionUnidadCab(String denominacionUnidadCab) {
		this.denominacionUnidadCab = denominacionUnidadCab;
	}

//	@Column(name = "id_configuracion_uo_det")
//	public Long getIdConfiguracionUoDet() {
//		return this.idConfiguracionUoDet;
//	}
//
//	public void setIdConfiguracionUoDet(Long idConfiguracionUoDet) {
//		this.idConfiguracionUoDet = idConfiguracionUoDet;
//	}
//
//	@Column(name = "orden_configuracion_det")
//	public Integer getOrdenConfiguracionDet() {
//		return this.ordenConfiguracionDet;
//	}
//
//	public void setOrdenConfiguracionDet(Integer ordenConfiguracionDet) {
//		this.ordenConfiguracionDet = ordenConfiguracionDet;
//	}
//
//	@Column(name = "denominacion_unidad_det", length = 100)
//	@Length(max = 100)
//	public String getDenominacionUnidadDet() {
//		return this.denominacionUnidadDet;
//	}
//
//	public void setDenominacionUnidadDet(String denominacionUnidadDet) {
//		this.denominacionUnidadDet = denominacionUnidadDet;
//	}

	@Column(name = "nen_codigo", precision = 2, scale = 0)
	public Byte getNenCodigo() {
		return this.nenCodigo;
	}

	public void setNenCodigo(Byte nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	@Column(name = "ent_codigo", precision = 3, scale = 0)
	public Short getEntCodigo() {
		return this.entCodigo;
	}

	public void setEntCodigo(Short entCodigo) {
		this.entCodigo = entCodigo;
	}

	@Column(name = "ent_nombre", length = 60)
	@Length(max = 60)
	public String getEntNombre() {
		return this.entNombre;
	}

	public void setEntNombre(String entNombre) {
		this.entNombre = entNombre;
	}

	@Column(name = "nivel_cod", precision = 2, scale = 0)
	public Byte getNivelCod() {
		return this.nivelCod;
	}

	public void setNivelCod(Byte nivelCod) {
		this.nivelCod = nivelCod;
	}

	@Column(name = "nivel_nombre", length = 60)
	@Length(max = 60)
	public String getNivelNombre() {
		return this.nivelNombre;
	}

	public void setNivelNombre(String nivelNombre) {
		this.nivelNombre = nivelNombre;
	}

	@Column(name = "actividad", length = 200)
	@Length(max = 200)
	public String getActividad() {
		return this.actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	@Column(name = "usuario")
	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Column(name = "fecha_recepcion", length = 29)
	public Date getFechaRecepcion() {
		return this.fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	@Column(name = "fecha_inicio", length = 29)
	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Column(name = "dias_creacion")
	public Integer getDiasCreacion() {
		return this.diasCreacion;
	}

	public void setDiasCreacion(Integer diasCreacion) {
		this.diasCreacion = diasCreacion;
	}

	@Column(name = "dias_inicio")
	public Integer getDiasInicio() {
		return this.diasInicio;
	}

	public void setDiasInicio(Integer diasInicio) {
		this.diasInicio = diasInicio;
	}

	@Column(name = "atraso", precision = 131089, scale = 0)
	public BigDecimal getAtraso() {
		return this.atraso;
	}

	public void setAtraso(BigDecimal atraso) {
		this.atraso = atraso;
	}

	@Column(name = "id_actividad")
	public Long getIdActividad() {
		return this.idActividad;
	}

	public void setIdActividad(Long idActividad) {
		this.idActividad = idActividad;
	}

	@Column(name = "id_entidad")
	public Long getIdEntidad() {
		return this.idEntidad;
	}

	public void setIdEntidad(Long idEntidad) {
		this.idEntidad = idEntidad;
	}

	@Column(name = "id_sin_nivel_entidad")
	public Long getIdSinNivelEntidad() {
		return this.idSinNivelEntidad;
	}

	public void setIdSinNivelEntidad(Long idSinNivelEntidad) {
		this.idSinNivelEntidad = idSinNivelEntidad;
	}

	@Column(name = "id_taskinstance")
	public Long getIdTaskinstance() {
		return this.idTaskinstance;
	}

	public void setIdTaskinstance(Long idTaskinstance) {
		this.idTaskinstance = idTaskinstance;
	}

	@Column(name = "id_process_instance")
	public Long getIdProcessInstance() {
		return this.idProcessInstance;
	}

	public void setIdProcessInstance(Long idProcessInstance) {
		this.idProcessInstance = idProcessInstance;
	}

	@Column(name = "cod_actividad", length = 50)
	@Length(max = 50)
	public String getCodActividad() {
		return this.codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	@Column(name = "id_proceso")
	public Long getIdProceso() {
		return this.idProceso;
	}

	public void setIdProceso(Long idProceso) {
		this.idProceso = idProceso;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_actividad_proceso")
	public ActividadProceso getActividadProceso() {
		return actividadProceso;
	}

	public void setActividadProceso(ActividadProceso actividadProceso) {
		this.actividadProceso = actividadProceso;
	}

	@Column(name = "tipo_actividad", length = 20)
	@Length(max = 20)
	public String getTipoActividad() {
		return this.tipoActividad;
	}

	public void setTipoActividad(String tipoActividad) {
		this.tipoActividad = tipoActividad;
	}

	@Column(name = "id_sin_entidad")
	public Long getIdSinEntidad() {
		return this.idSinEntidad;
	}

	public void setIdSinEntidad(Long idSinEntidad) {
		this.idSinEntidad = idSinEntidad;
	}

	@Column(name = "estado_eval", length = 30)
	@Length(max = 30)
	public String getEstadoEval() {
		return this.estadoEval;
	}

	public void setEstadoEval(String estadoEval) {
		this.estadoEval = estadoEval;
	}

	
}
