package py.com.excelsis.sicca.entity;

// Generated 28/09/2011 11:21:46 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
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
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * HistoricosEstado generated by hbm2java
 */
@Entity
@Table(name = "historicos_estado", schema = "planificacion")
public class HistoricosEstado implements java.io.Serializable {

	private Long idHistoricoEstado;
	private PlantaCargoDet plantaCargoDet;
	private EstadoCab estadoCab;
	private String observacion;
	private String usuMod;
	private Date fechaMod;

	public HistoricosEstado() {
	}

	public HistoricosEstado(Long idHistoricoEstado,
			PlantaCargoDet plantaCargoDet, EstadoCab estadoCab) {
		this.idHistoricoEstado = idHistoricoEstado;
		this.plantaCargoDet = plantaCargoDet;
		this.estadoCab = estadoCab;
	}

	public HistoricosEstado(Long idHistoricoEstado,
			PlantaCargoDet plantaCargoDet, EstadoCab estadoCab,
			String observacion, String usuMod, Date fechaMod) {
		this.idHistoricoEstado = idHistoricoEstado;
		this.plantaCargoDet = plantaCargoDet;
		this.estadoCab = estadoCab;
		this.observacion = observacion;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator="HISTORICO_ESTADO_GENERATOR")
	@SequenceGenerator(name="HISTORICO_ESTADO_GENERATOR",sequenceName="planificacion.historicos_estado_id_historico_estado_seq")
	@Column(name = "id_historico_estado", unique = true, nullable = false)
	public Long getIdHistoricoEstado() {
		return this.idHistoricoEstado;
	}

	public void setIdHistoricoEstado(Long idHistoricoEstado) {
		this.idHistoricoEstado = idHistoricoEstado;
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
	@JoinColumn(name = "id_estado_cab", nullable = false)
	@NotNull
	public EstadoCab getEstadoCab() {
		return this.estadoCab;
	}

	public void setEstadoCab(EstadoCab estadoCab) {
		this.estadoCab = estadoCab;
	}

	@Column(name = "observacion", length = 250)
	@Length(max = 250)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
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

}
