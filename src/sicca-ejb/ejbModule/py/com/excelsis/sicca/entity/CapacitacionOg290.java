package py.com.excelsis.sicca.entity;

// Generated 26/06/2012 10:48:30 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
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
 * CapacitacionOg290 generated by hbm2java
 */
@Entity
@Table(name = "capacitacion_og290", schema = "capacitacion")
public class CapacitacionOg290 implements java.io.Serializable {

	private Long idCapacitacion;
	private String denominacionUnidad;
	private String nombre;
	private String descripcion;
	private String modalidad;
	private Date fechaPubDesde;
	private Date fechaPubHasta;
	private Ciudad ciudad;
	private DatosEspecificos datosEspecificos;
	private Date fechaInicio;
	private Date fechaFin;
	private String mod;
	private ConfiguracionUoCab configuracionUoCab;

	public CapacitacionOg290() {
	}
	
	@Id
	@Column(name = "id_capacitacion")
	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	@Column(name = "denominacion_unidad", length = 100)
	@Length(max = 100)
	public String getDenominacionUnidad() {
		return denominacionUnidad;
	}

	public void setDenominacionUnidad(String denominacionUnidad) {
		this.denominacionUnidad = denominacionUnidad;
	}

	@Column(name = "nombre", length = 300)
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "descripcion", length = 500)
	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "modalidad")
	public String getModalidad() {
		return modalidad;
	}

	public void setModalidad(String modalidad) {
		this.modalidad = modalidad;
	}

	@Column(name = "fecha_pub_desde", length = 29)
	public Date getFechaPubDesde() {
		return fechaPubDesde;
	}

	public void setFechaPubDesde(Date fechaPubDesde) {
		this.fechaPubDesde = fechaPubDesde;
	}

	@Column(name = "fecha_pub_hasta", length = 29)
	public Date getFechaPubHasta() {
		return fechaPubHasta;
	}

	public void setFechaPubHasta(Date fechaPubHasta) {
		this.fechaPubHasta = fechaPubHasta;
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
	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Column(name = "mod")
	public String getMod() {
		return mod;
	}

	public void setMod(String mod) {
		this.mod = mod;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	
}
