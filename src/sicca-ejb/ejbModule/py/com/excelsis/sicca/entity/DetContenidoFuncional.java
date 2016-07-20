package py.com.excelsis.sicca.entity;

// Generated 15-sep-2011 8:16:18 by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * DetContenidoFuncional generated by hbm2java
 */
@Entity
@Table(name = "det_contenido_funcional", schema = "planificacion")
public class DetContenidoFuncional implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8830857265142270025L;
	private Long idDetContenidoFuncional;
	private PlantaCargoDet plantaCargoDet;
	private Cpt cpt;
	private ContenidoFuncional contenidoFuncional;
	private String tipo;
	private Float puntaje;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private HomologacionPerfilMatriz homologacionPerfilMatriz;
	private Boolean activo;
	
	private List<DetDescripcionContFuncional> detDescripcionContFuncionals = new ArrayList<DetDescripcionContFuncional>(0);

	public DetContenidoFuncional() {
	}

	public DetContenidoFuncional(PlantaCargoDet plantaCargoDet, Cpt cpt,
			ContenidoFuncional contenidoFuncional, String tipo, Float puntaje) {
		this.plantaCargoDet = plantaCargoDet;
		this.cpt = cpt;
		this.contenidoFuncional = contenidoFuncional;
		this.tipo = tipo;
		this.puntaje = puntaje;
	}

	public DetContenidoFuncional(PlantaCargoDet plantaCargoDet, Cpt cpt,
			ContenidoFuncional contenidoFuncional, String tipo, Float puntaje,
			List<DetDescripcionContFuncional> detDescripcionContFuncionals) {
		this.plantaCargoDet = plantaCargoDet;
		this.cpt = cpt;
		this.contenidoFuncional = contenidoFuncional;
		this.tipo = tipo;
		this.puntaje = puntaje;
		this.detDescripcionContFuncionals = detDescripcionContFuncionals;
	}
	
	@Id
	@GeneratedValue(generator="DET_CONTENIDO_FUNCIONAL_GENERATOR")
	@SequenceGenerator(name="DET_CONTENIDO_FUNCIONAL_GENERATOR",sequenceName="planificacion.det_contenido_funcional_id_det_contenido_funcional_seq")
	@Column(name = "id_det_contenido_funcional")
	public Long getIdDetContenidoFuncional() {
		return idDetContenidoFuncional;
	}

	public void setIdDetContenidoFuncional(Long idDetContenidoFuncional) {
		this.idDetContenidoFuncional = idDetContenidoFuncional;
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
	@JoinColumn(name = "id_contenido_funcional")
	public ContenidoFuncional getContenidoFuncional() {
		return this.contenidoFuncional;
	}

	public void setContenidoFuncional(ContenidoFuncional contenidoFuncional) {
		this.contenidoFuncional = contenidoFuncional;
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

	@Column(name = "puntaje", precision = 8, scale = 8)
	public Float getPuntaje() {
		return this.puntaje;
	}

	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr")
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "detContenidoFuncional")
	public List<DetDescripcionContFuncional> getDetDescripcionContFuncionals() {
		return this.detDescripcionContFuncionals;
	}

	public void setDetDescripcionContFuncionals(
			List<DetDescripcionContFuncional> detDescripcionContFuncionals) {
		this.detDescripcionContFuncionals = detDescripcionContFuncionals;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_homologacion")
	public HomologacionPerfilMatriz getHomologacionPerfilMatriz() {
		return homologacionPerfilMatriz;
	}
	
	public void setHomologacionPerfilMatriz(HomologacionPerfilMatriz homologacionPerfilMatriz) {
		this.homologacionPerfilMatriz = homologacionPerfilMatriz;
	}
	@Column(name = "activo")
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}