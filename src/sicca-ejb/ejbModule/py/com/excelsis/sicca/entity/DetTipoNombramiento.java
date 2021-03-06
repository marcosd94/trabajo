package py.com.excelsis.sicca.entity;

// Generated 15-sep-2011 8:16:18 by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * DetTipoNombramiento generated by hbm2java
 */
@Entity
@Table(name = "det_tipo_nombramiento", schema = "planificacion")
public class DetTipoNombramiento implements java.io.Serializable {

	private Long idDetTipoNombramiento;
	private PlantaCargoDet plantaCargoDet;
	private TipoNombramiento tipoNombramiento;
	private Cpt cpt;
	private String tipo;
	

	public DetTipoNombramiento() {
	}

	public DetTipoNombramiento(Long idDetTipoNombramiento,
			PlantaCargoDet plantaCargoDet, TipoNombramiento tipoNombramiento,
			Cpt cpt, String tipo) {
		this.idDetTipoNombramiento = idDetTipoNombramiento;
		this.plantaCargoDet = plantaCargoDet;
		this.tipoNombramiento = tipoNombramiento;
		this.cpt = cpt;
		this.tipo = tipo;
		
	}

	@Id
	@GeneratedValue(generator="DET_TIPO_NOMBRAMIENTO_GENERATOR")
	@SequenceGenerator(name="DET_TIPO_NOMBRAMIENTO_GENERATOR",sequenceName="planificacion.tipo_nombramiento_id_tipo_nombramiento_seq")
	@Column(name = "id_det_tipo_nombramiento", unique = true, nullable = false)
	public Long getIdDetTipoNombramiento() {
		return this.idDetTipoNombramiento;
	}

	public void setIdDetTipoNombramiento(Long idDetTipoNombramiento) {
		this.idDetTipoNombramiento = idDetTipoNombramiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_planta_cargo_det")
	public PlantaCargoDet getPlantaCargoDet() {
		return this.plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_nombramiento")
	public TipoNombramiento getTipoNombramiento() {
		return this.tipoNombramiento;
	}

	public void setTipoNombramiento(TipoNombramiento tipoNombramiento) {
		this.tipoNombramiento = tipoNombramiento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cpt")
	public Cpt getCpt() {
		return this.cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	@Column(name = "tipo", nullable = false, length = 10)
	@NotNull
	@Length(max = 10)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
}
