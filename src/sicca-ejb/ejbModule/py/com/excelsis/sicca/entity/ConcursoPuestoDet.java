package py.com.excelsis.sicca.entity;

// Generated 28/10/2011 05:04:36 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * ConcursoPuestoDet generated by hbm2java
 */
@Entity
@Table(name = "concurso_puesto_det", schema = "seleccion")
public class ConcursoPuestoDet extends EntityBase {

	private Long idConcursoPuestoDet;
	private PlantaCargoDet plantaCargoDet;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private EstadoDet estadoDet;
	private Integer nroOrden;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Concurso concurso;
	private Boolean seleccionado;
	private String observacion;
	private Excepcion excepcion;
	private Boolean excepcionado;
	// Transients
	private Boolean desierto = false;;
	private Boolean excepcionar = false;

	public ConcursoPuestoDet() {
	}

	public ConcursoPuestoDet(
			Long idConcursoPuestoDet, PlantaCargoDet plantaCargoDet, EstadoDet estadoDet,
			boolean activo, String usuAlta, Date fechaAlta) {
		this.idConcursoPuestoDet = idConcursoPuestoDet;
		this.plantaCargoDet = plantaCargoDet;
		this.estadoDet = estadoDet;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ConcursoPuestoDet(
			Long idConcursoPuestoDet, PlantaCargoDet plantaCargoDet,
			ConcursoPuestoAgr concursoPuestoAgr, EstadoDet estadoDet, Integer nroOrden,
			boolean activo, String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idConcursoPuestoDet = idConcursoPuestoDet;
		this.plantaCargoDet = plantaCargoDet;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.estadoDet = estadoDet;
		this.nroOrden = nroOrden;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator = "CONCURSO_PUESTO_DET_GENERATOR")
	@SequenceGenerator(name = "CONCURSO_PUESTO_DET_GENERATOR", sequenceName = "seleccion.concurso_puesto_det_id_concurso_puesto_det_seq")
	@Column(name = "id_concurso_puesto_det", unique = true, nullable = false)
	public Long getIdConcursoPuestoDet() {
		return this.idConcursoPuestoDet;
	}

	public void setIdConcursoPuestoDet(Long idConcursoPuestoDet) {
		this.idConcursoPuestoDet = idConcursoPuestoDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_planta_cargo_det", nullable = false)
	@NotNull
	public PlantaCargoDet getPlantaCargoDet() {
		return this.plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr")
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return this.concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_det", nullable = false)
	@NotNull
	public EstadoDet getEstadoDet() {
		return this.estadoDet;
	}

	public void setEstadoDet(EstadoDet estadoDet) {
		this.estadoDet = estadoDet;
	}

	@Column(name = "nro_orden")
	public Integer getNroOrden() {
		return this.nroOrden;
	}

	public void setNroOrden(Integer nroOrden) {
		this.nroOrden = nroOrden;
	}

	@Column(name = "activo", nullable = false)
	public Boolean isActivo() {
		return this.activo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso", nullable = false)
	@NotNull
	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	@Transient
	public Long getId() {
		return idConcursoPuestoDet;
	}

	/**
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.CONCURSO_PUESTO_DET_ID, getId());

		return properties;
	}

	/**
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

	@Transient
	public Boolean getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	@Column(name = "observacion", length = 250)
	@Length(max = 250)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Transient
	public Boolean getDesierto() {
		return desierto;
	}

	public void setDesierto(Boolean desierto) {
		this.desierto = desierto;
	}

	@Transient
	public Boolean getExcepcionar() {
		return excepcionar;
	}

	public void setExcepcionar(Boolean excepcionar) {
		this.excepcionar = excepcionar;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_excepcion" )	 
	public Excepcion getExcepcion() {
		return excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	@Column(name = "excepcionado")
	public Boolean getExcepcionado() {
		return excepcionado;
	}

	public void setExcepcionado(Boolean excepcionado) {
		this.excepcionado = excepcionado;
	}

	
	
}
