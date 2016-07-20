package py.com.excelsis.sicca.entity;

// Generated 28/09/2011 11:21:46 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * EstadoCab generated by hbm2java
 */
@Entity
@Table(name = "estado_cab", schema = "planificacion")
public class EstadoCab implements java.io.Serializable {

	private Long idEstadoCab;
	private String descripcion;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Boolean activo;
	private Set<TransicionEstado> transicionEstadosForPosibleEstado = new HashSet<TransicionEstado>(
			0);
	private Set<TransicionEstado> transicionEstadosForIdEstadoCab = new HashSet<TransicionEstado>(
			0);
	private Set<PlantaCargoDet> plantaCargoDets = new HashSet<PlantaCargoDet>(0);
	private Set<HistoricosEstado> historicosEstados = new HashSet<HistoricosEstado>(
			0);
	private Set<EstadoDet> estadoDets = new HashSet<EstadoDet>(0);

	public EstadoCab() {
	}

	public EstadoCab(Long idEstadoCab, String descripcion, String usuAlta,
			Date fechaAlta, Boolean activo) {
		this.idEstadoCab = idEstadoCab;
		this.descripcion = descripcion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.activo = activo;
	}

	public EstadoCab(Long idEstadoCab, String descripcion, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod, Boolean activo,
			Set<TransicionEstado> transicionEstadosForPosibleEstado,
			Set<TransicionEstado> transicionEstadosForIdEstadoCab,
			Set<PlantaCargoDet> plantaCargoDets,
			Set<HistoricosEstado> historicosEstados, Set<EstadoDet> estadoDets) {
		this.idEstadoCab = idEstadoCab;
		this.descripcion = descripcion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.activo = activo;
		this.transicionEstadosForPosibleEstado = transicionEstadosForPosibleEstado;
		this.transicionEstadosForIdEstadoCab = transicionEstadosForIdEstadoCab;
		this.plantaCargoDets = plantaCargoDets;
		this.historicosEstados = historicosEstados;
		this.estadoDets = estadoDets;
	}

	@Id
	@GeneratedValue(generator="ESTADO_CAB_GENERATOR")
	@SequenceGenerator(name="ESTADO_CAB_GENERATOR",sequenceName="planificacion.estado_cab_id_estado_cab_seq")
	@Column(name = "id_estado_cab", unique = true, nullable = false)
	public Long getIdEstadoCab() {
		return this.idEstadoCab;
	}

	public void setIdEstadoCab(Long idEstadoCab) {
		this.idEstadoCab = idEstadoCab;
	}

	@Column(name = "descripcion", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	@Column(name = "activo", nullable = false)
		public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoCabByPosibleEstado")
	public Set<TransicionEstado> getTransicionEstadosForPosibleEstado() {
		return this.transicionEstadosForPosibleEstado;
	}

	public void setTransicionEstadosForPosibleEstado(
			Set<TransicionEstado> transicionEstadosForPosibleEstado) {
		this.transicionEstadosForPosibleEstado = transicionEstadosForPosibleEstado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoCabByIdEstadoCab")
	public Set<TransicionEstado> getTransicionEstadosForIdEstadoCab() {
		return this.transicionEstadosForIdEstadoCab;
	}

	public void setTransicionEstadosForIdEstadoCab(
			Set<TransicionEstado> transicionEstadosForIdEstadoCab) {
		this.transicionEstadosForIdEstadoCab = transicionEstadosForIdEstadoCab;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoCab")
	public Set<PlantaCargoDet> getPlantaCargoDets() {
		return this.plantaCargoDets;
	}

	public void setPlantaCargoDets(Set<PlantaCargoDet> plantaCargoDets) {
		this.plantaCargoDets = plantaCargoDets;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoCab")
	public Set<HistoricosEstado> getHistoricosEstados() {
		return this.historicosEstados;
	}

	public void setHistoricosEstados(Set<HistoricosEstado> historicosEstados) {
		this.historicosEstados = historicosEstados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estadoCab")
	public Set<EstadoDet> getEstadoDets() {
		return this.estadoDets;
	}

	public void setEstadoDets(Set<EstadoDet> estadoDets) {
		this.estadoDets = estadoDets;
	}

}