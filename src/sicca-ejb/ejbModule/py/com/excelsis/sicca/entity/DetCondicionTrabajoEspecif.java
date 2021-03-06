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
 * DetCondicionTrabajoEspecif generated by hbm2java
 */
@Entity
@Table(name = "det_condicion_trabajo_especif", schema = "planificacion")
public class DetCondicionTrabajoEspecif implements java.io.Serializable {

	private Long idDetCondicionTrabajoEspecif;
	private PlantaCargoDet plantaCargoDet;
	private Cpt cpt;
	private CondicionTrabajoEspecif condicionTrabajoEspecif;
	private String tipo;
	private String justificacion;
	private String ajustes;
	private Float puntajeCondTrabEspecif;
	private Boolean activo;
	private ConcursoPuestoAgr concursoPuestoAgr;

	public DetCondicionTrabajoEspecif() {
	}

	public DetCondicionTrabajoEspecif(Long idDetCondicionTrabajoEspecif,
			PlantaCargoDet plantaCargoDet, Cpt cpt,
			CondicionTrabajoEspecif condicionTrabajoEspecif, String tipo,
			String justificacion, String ajustes, Float puntajeCondTrabEspecif,
			Boolean activo) {
		this.idDetCondicionTrabajoEspecif = idDetCondicionTrabajoEspecif;
		this.plantaCargoDet = plantaCargoDet;
		this.cpt = cpt;
		this.condicionTrabajoEspecif = condicionTrabajoEspecif;
		this.tipo = tipo;
		this.justificacion = justificacion;
		this.ajustes = ajustes;
		this.puntajeCondTrabEspecif = puntajeCondTrabEspecif;
		this.activo = activo;
	}

	@Id
	@GeneratedValue(generator="DET_COND_TRAB_ESPECIF_GENERATOR")
	@SequenceGenerator(name="DET_COND_TRAB_ESPECIF_GENERATOR",sequenceName="planificacion.det_cond_trab_especif_id_det_cond_trab_especif_seq")
	@Column(name = "id_det_condicion_trabajo_especif", unique = true, nullable = false)
	public Long getIdDetCondicionTrabajoEspecif() {
		return this.idDetCondicionTrabajoEspecif;
	}

	public void setIdDetCondicionTrabajoEspecif(
			Long idDetCondicionTrabajoEspecif) {
		this.idDetCondicionTrabajoEspecif = idDetCondicionTrabajoEspecif;
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
	@JoinColumn(name = "id_cpt")
	public Cpt getCpt() {
		return this.cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_condiciones_trabajo_especif", nullable = false)
	@NotNull
	public CondicionTrabajoEspecif getCondicionTrabajoEspecif() {
		return this.condicionTrabajoEspecif;
	}

	public void setCondicionTrabajoEspecif(
			CondicionTrabajoEspecif condicionTrabajoEspecif) {
		this.condicionTrabajoEspecif = condicionTrabajoEspecif;
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

	@Column(name = "justificacion", nullable = false, length = 400)
	@NotNull
	@Length(max = 400)
	public String getJustificacion() {
		return this.justificacion;
	}

	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}

	@Column(name = "ajustes", nullable = false, length = 400)
	@NotNull
	@Length(max = 400)
	public String getAjustes() {
		return this.ajustes;
	}

	public void setAjustes(String ajustes) {
		this.ajustes = ajustes;
	}

	@Column(name = "puntaje_cond_trab_especif", nullable = false, precision = 8, scale = 8)
	public Float getPuntajeCondTrabEspecif() {
		return this.puntajeCondTrabEspecif;
	}

	public void setPuntajeCondTrabEspecif(Float puntajeCondTrabEspecif) {
		this.puntajeCondTrabEspecif = puntajeCondTrabEspecif;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr")
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

 

}
