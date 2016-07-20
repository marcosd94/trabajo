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
 * DetCondicionSegur generated by hbm2java
 */
@Entity
@Table(name = "det_condicion_segur", schema = "planificacion")
public class DetCondicionSegur implements java.io.Serializable {

	private Long idDetCondicionSegur;
	private PlantaCargoDet plantaCargoDet;
	private Cpt cpt;
	private CondicionSegur condicionSegur;
	private String tipo;
	private Float puntajeCondSegur;
	private String ajustes;
	private String justificacion;
	private Boolean activo;
	private ConcursoPuestoAgr concursoPuestoAgr;
	
	public DetCondicionSegur() {
	}

	public DetCondicionSegur(Long idDetCondicionSegur,
			PlantaCargoDet plantaCargoDet, Cpt cpt,
			CondicionSegur condicionSegur, String tipo, Float puntajeCondSegur,
			String ajustes, String justificacion, Boolean activo) {
		this.idDetCondicionSegur = idDetCondicionSegur;
		this.plantaCargoDet = plantaCargoDet;
		this.cpt = cpt;
		this.condicionSegur = condicionSegur;
		this.tipo = tipo;
		this.puntajeCondSegur = puntajeCondSegur;
		this.ajustes = ajustes;
		this.justificacion = justificacion;
		this.activo = activo;
	}

	@Id
	@GeneratedValue(generator="DET_CONDICION_SEGUR_GENERATOR")
	@SequenceGenerator(name="DET_CONDICION_SEGUR_GENERATOR",sequenceName="planificacion.det_condicion_segur_id_det_condicion_segur_seq")
	@Column(name = "id_det_condicion_segur", unique = true, nullable = false)
	public Long getIdDetCondicionSegur() {
		return this.idDetCondicionSegur;
	}

	public void setIdDetCondicionSegur(Long idDetCondicionSegur) {
		this.idDetCondicionSegur = idDetCondicionSegur;
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
	@JoinColumn(name = "id_condicion_segur", nullable = false)
	@NotNull
	public CondicionSegur getCondicionSegur() {
		return this.condicionSegur;
	}

	public void setCondicionSegur(CondicionSegur condicionSegur) {
		this.condicionSegur = condicionSegur;
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

	@Column(name = "puntaje_cond_segur", nullable = false, precision = 8, scale = 8)
	public Float getPuntajeCondSegur() {
		return this.puntajeCondSegur;
	}

	public void setPuntajeCondSegur(Float puntajeCondSegur) {
		this.puntajeCondSegur = puntajeCondSegur;
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

	@Column(name = "justificacion", nullable = false, length = 400)
	@NotNull
	@Length(max = 400)
	public String getJustificacion() {
		return this.justificacion;
	}

	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
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