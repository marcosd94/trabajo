package py.com.excelsis.sicca.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "historial_circuito_cap", schema = "capacitacion")
public class HistorialCircuitoCap implements Serializable {
	
	private Long idActividad;
	private Capacitaciones capacitaciones;
	private String estado;
	private String codActividad;
	private String actividad;
	private Date fechaCreacion;
	private String usuarioInicio;
	private Date fechaInicio;
	private String usuarioFin;
	private Date fechaFin;

	
	
	
	public HistorialCircuitoCap(Long idActividad,
			Capacitaciones capacitaciones, String estado, String codActividad,
			String actividad, Date fechaCreacion, String usuarioInicio,
			Date fechaInicio, String usuarioFin, Date fechaFin) {
		
		this.idActividad = idActividad;
		this.capacitaciones = capacitaciones;
		this.estado = estado;
		this.codActividad = codActividad;
		this.actividad = actividad;
		this.fechaCreacion = fechaCreacion;
		this.usuarioInicio = usuarioInicio;
		this.fechaInicio = fechaInicio;
		this.usuarioFin = usuarioFin;
		this.fechaFin = fechaFin;
	}


	public HistorialCircuitoCap() {
		
	}


	

	
	@Id
	@Column(name = "id_actividad")
	public Long getIdActividad() {
		return idActividad;
	}


	public void setIdActividad(Long idActividad) {
		this.idActividad = idActividad;
	}


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_capacitacion")
	public Capacitaciones getCapacitaciones() {
		return capacitaciones;
	}


	public void setCapacitaciones(Capacitaciones capacitaciones) {
		this.capacitaciones = capacitaciones;
	}


	@Column(name = "estado")
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "cod_actividad")
	public String getCodActividad() {
		return codActividad;
	}

	public void setCodActividad(String codActividad) {
		this.codActividad = codActividad;
	}

	@Column(name = "actividad")
	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	@Column(name = "fecha_creacion", length = 29)
	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Column(name = "usuario_inicio")
	public String getUsuarioInicio() {
		return usuarioInicio;
	}
	
	public void setUsuarioInicio(String usuarioInicio) {
		this.usuarioInicio = usuarioInicio;
	}

	@Column(name = "fecha_inicio", length = 29)
	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Column(name = "usuario_fin")
	public String getUsuarioFin() {
		return usuarioFin;
	}

	public void setUsuarioFin(String usuarioFin) {
		this.usuarioFin = usuarioFin;
	}

	@Column(name = "fecha_fin", length = 29)
	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

}
