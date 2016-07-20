package py.com.excelsis.sicca.entity;

// Generated 07/12/2012 01:59:24 PM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;

/**
 * VerCursosEvaluacionPostulantes generated by hbm2java
 */
@Entity
@Table(name = "ver_cursos_evaluacion_postulantes", schema = "capacitacion")
public class VerCursosEvaluacionPostulantes implements java.io.Serializable {

	private String nombre;
	private String descripcion;
	private String modalidad;
	private Long cargaHoraria;
	private Long creditos;
	private String fecha;
	private String estado;
	private Long idPersona;

	public VerCursosEvaluacionPostulantes() {
	}

	public VerCursosEvaluacionPostulantes(String nombre, String descripcion,
			String modalidad, Long cargaHoraria, Long creditos, String fecha, String estado, Long idPersona) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.modalidad = modalidad;
		this.cargaHoraria = cargaHoraria;
		this.creditos = creditos;
		this.fecha = fecha;
		this.estado = estado;
		this.idPersona = idPersona;
	}

	@Column(name = "nombre", length = 300)
	@Length(max = 300)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "descripcion", length = 500)
	@Length(max = 500)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "modalidad")
	public String getModalidad() {
		return this.modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}
	
	@Column(name = "carga_horaria")
	public Long getCargaHoraria() {
		return this.cargaHoraria;
	}

	public void setCargaHoraria(Long cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}
	
	@Column(name = "creditos")
	public Long getCreditos() {
		return this.creditos;
	}

	public void setCreditos(Long creditos) {
		this.creditos = creditos;
	}
	
	@Column(name = "fecha")
	public String getFecha() {
		return this.fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	@Column(name = "estado")
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Id
	@Column(name = "id_persona")
	public Long getIdPersona() {
		return this.idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

}