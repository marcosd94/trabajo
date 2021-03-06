package py.com.excelsis.sicca.entity;

// Generated 13/07/2012 08:32:42 AM by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * PlantillaEncuesta generated by hbm2java
 */
@Entity
@Table(name = "plantilla_encuesta", schema = "capacitacion")
public class PlantillaEncuesta implements java.io.Serializable {

	private Long idPlantilla;
	private ConfiguracionUoCab configuracionUoCab;
	private String nombre;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private List<Preguntas> preguntases = new ArrayList<Preguntas>();

	public PlantillaEncuesta() {
	}

	public PlantillaEncuesta(Long idPlantilla, ConfiguracionUoCab idConfiguracionUo,
			String nombre, boolean activo, String usuAlta, Date fechaAlta) {
		this.idPlantilla = idPlantilla;
		this.configuracionUoCab = idConfiguracionUo;
		this.nombre = nombre;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public PlantillaEncuesta(long idPlantilla, ConfiguracionUoCab idConfiguracionUo,
			String nombre, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod, List<Preguntas> preguntases) {
		this.idPlantilla = idPlantilla;
		this.configuracionUoCab = idConfiguracionUo;
		this.nombre = nombre;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.preguntases = preguntases;
	}

	@Id
	@GeneratedValue(generator="PLANTILLA_ENCUESTA_GENERATOR")
	@SequenceGenerator(name="PLANTILLA_ENCUESTA_GENERATOR",sequenceName="capacitacion.plantilla_encuesta_id_plantilla_seq")
	@Column(name = "id_plantilla", unique = true, nullable = false)
	public Long getIdPlantilla() {
		return this.idPlantilla;
	}

	public void setIdPlantilla(Long idPlantilla) {
		this.idPlantilla = idPlantilla;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	@NotNull
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@Column(name = "nombre", nullable = false, length = 150)
	@NotNull
	@Length(max = 150)
	public String getNombre() {
		return this.nombre;
	}

	

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return this.activo;
	}

	public void setActivo(boolean activo) {
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plantillaEncuesta")
	public List<Preguntas> getPreguntases() {
		return this.preguntases;
	}

	public void setPreguntases(List<Preguntas> preguntases) {
		this.preguntases = preguntases;
	}

}
