package py.com.excelsis.sicca.entity;

// Generated 24-sep-2012 13:57:28 by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;

/**
 * HistoricoEvaluacion generated by hbm2java
 */
@Entity
@Table(name = "historico_evaluacion", schema = "eval_desempeno")
public class HistoricoEvaluacion implements java.io.Serializable {

	private Long idActividad;
	private Long idEvaluacion;
	private String oee;
//	private String unidadOrganizativa;
	private String titulo;
	private String usuarioAlta;
	private Date fechaAlta;
	private String estado;
	private String codActividad;
	private String actividad;
	private Date fechaCreacion;
	private String usuarioInicio;
	private Date fechaInicio;
	private String usuarioFin;
	private Date fechaFin;

	public HistoricoEvaluacion() {
	}

	public HistoricoEvaluacion(Long idEvaluacion, String oee,
			/*String unidadOrganizativa,*/ String titulo, String usuarioAlta,
			Date fechaAlta, String estado, String codActividad,
			String actividad, Date fechaCreacion, String usuarioInicio,
			Date fechaInicio, String usuarioFin, Date fechaFin) {
		this.idEvaluacion = idEvaluacion;
		this.oee = oee;
//		this.unidadOrganizativa = unidadOrganizativa;
		this.titulo = titulo;
		this.usuarioAlta = usuarioAlta;
		this.fechaAlta = fechaAlta;
		this.estado = estado;
		this.codActividad = codActividad;
		this.actividad = actividad;
		this.fechaCreacion = fechaCreacion;
		this.usuarioInicio = usuarioInicio;
		this.fechaInicio = fechaInicio;
		this.usuarioFin = usuarioFin;
		this.fechaFin = fechaFin;
	}
	
	
	@Id
	@Column(name = "id_actividad")
	public Long getIdActividad() {
		return idActividad;
	}

	public void setIdActividad(Long idActividad) {
		this.idActividad = idActividad;
	}

	@Column(name = "id_evaluacion")
	public Long getIdEvaluacion() {
		return this.idEvaluacion;
	}

	public void setIdEvaluacion(Long idEvaluacion) {
		this.idEvaluacion = idEvaluacion;
	}

	@Column(name = "oee", length = 100)
	@Length(max = 100)
	public String getOee() {
		return this.oee;
	}

	public void setOee(String oee) {
		this.oee = oee;
	}

//	@Column(name = "unidad_organizativa", length = 100)
//	@Length(max = 100)
//	public String getUnidadOrganizativa() {
//		return this.unidadOrganizativa;
//	}
//
//	public void setUnidadOrganizativa(String unidadOrganizativa) {
//		this.unidadOrganizativa = unidadOrganizativa;
//	}

	@Column(name = "titulo", length = 250)
	@Length(max = 250)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "usuario_alta", length = 50)
	@Length(max = 50)
	public String getUsuarioAlta() {
		return this.usuarioAlta;
	}

	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}

	@Column(name = "fecha_alta", length = 29)
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "estado", length = 60)
	@Length(max = 60)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "cod_actividad", length = 50)
	@Length(max = 50)
	public String getCodActividad() {
		return this.codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	@Column(name = "actividad", length = 200)
	@Length(max = 200)
	public String getActividad() {
		return this.actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	@Column(name = "fecha_creacion", length = 29)
	public Date getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Column(name = "usuario_inicio")
	public String getUsuarioInicio() {
		return this.usuarioInicio;
	}

	public void setUsuarioInicio(String usuarioInicio) {
		this.usuarioInicio = usuarioInicio;
	}

	@Column(name = "fecha_inicio", length = 29)
	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Column(name = "usuario_fin")
	public String getUsuarioFin() {
		return this.usuarioFin;
	}

	public void setUsuarioFin(String usuarioFin) {
		this.usuarioFin = usuarioFin;
	}

	@Column(name = "fecha_fin", length = 29)
	public Date getFechaFin() {
		return this.fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	
}