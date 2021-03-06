package py.com.excelsis.sicca.entity;

// Generated 26/06/2012 10:48:30 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;

/**
 * CapacitacionPocPost generated by hbm2java
 */
@Entity
@Table(name = "capacitacion_poc_post", schema = "capacitacion")
public class CapacitacionPocPost implements java.io.Serializable {

	private Long idCapacitacion;
	private String denominacionUnidad;
	private String nombre;
	private String descripcion;
	private String modalidad;
	private Date desde;
	private Date hasta;
	private Ciudad ciudad;
	private DatosEspecificos datosEspecificos;
	private String modVal;
	private Date fechaRecepDesde;
	private Date fechaPubHasta;
	private Boolean recepcionPostulacion;
	private Date fechaInicio;
	private Date fechafin;

	public CapacitacionPocPost() {
	}

	public CapacitacionPocPost(Long idCapacitacion,
			String denominacionUnidad, String nombre, String descripcion,
			String modalidad, Date desde, Date hasta, Pais idPais,
			Departamento idDepartamento, Ciudad idCiudad) {
		this.idCapacitacion = idCapacitacion;
		this.denominacionUnidad = denominacionUnidad;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.modalidad = modalidad;
		this.desde = desde;
		this.hasta = hasta;
	
		this.ciudad = idCiudad;
	}
	@Id
	@Column(name = "id_capacitacion")
	public Long getIdCapacitacion() {
		return this.idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	@Column(name = "denominacion_unidad", length = 100)
	@Length(max = 100)
	public String getDenominacionUnidad() {
		return this.denominacionUnidad;
	}

	public void setDenominacionUnidad(String denominacionUnidad) {
		this.denominacionUnidad = denominacionUnidad;
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

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "desde",  length = 29)
	public Date getDesde() {
		return this.desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "hasta",  length = 29)
	public Date getHasta() {
		return this.hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ciudad")
	public Ciudad getCiudad() {
		return ciudad;
	}

	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos")
	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	@Column(name = "mod")
	public String getModVal() {
		return modVal;
	}

	public void setModVal(String modVal) {
		this.modVal = modVal;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_recep_desde",  length = 29)
	public Date getFechaRecepDesde() {
		return fechaRecepDesde;
	}

	

	public void setFechaRecepDesde(Date fechaRecepDesde) {
		this.fechaRecepDesde = fechaRecepDesde;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_pub_hasta",  length = 29)
	public Date getFechaPubHasta() {
		return fechaPubHasta;
	}

	

	public void setFechaPubHasta(Date fechaPubHasta) {
		this.fechaPubHasta = fechaPubHasta;
	}

	@Column(name = "recepcion_postulacion")
	public Boolean getRecepcionPostulacion() {
		return recepcionPostulacion;
	}

	public void setRecepcionPostulacion(Boolean recepcionPostulacion) {
		this.recepcionPostulacion = recepcionPostulacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_inicio",  length = 29)
	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_fin",  length = 29)
	public Date getFechafin() {
		return fechafin;
	}

	public void setFechafin(Date fechafin) {
		this.fechafin = fechafin;
	}

	
	

	
	

	

}
