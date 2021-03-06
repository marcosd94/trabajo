package py.com.excelsis.sicca.entity;

// Generated 12-sep-2012 9:08:43 by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.HashSet;
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
 * PlantillaEval generated by hbm2java
 */
@Entity
@Table(name = "plantilla_eval", schema = "eval_desempeno")
public class PlantillaEval implements java.io.Serializable {

	private Long idPlantillaEval;
	private String nombre;
	private DatosEspecificos datosEspecifMetod;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<PlantillaEvalDet> plantillaEvalDets = new HashSet<PlantillaEvalDet>(
			0);

	public PlantillaEval() {
	}

	public PlantillaEval(Long idPlantillaEval, String nombre,
			DatosEspecificos datosEspecifMetod, boolean activo, String usuAlta,
			Date fechaAlta) {
		this.idPlantillaEval = idPlantillaEval;
		this.nombre = nombre;
		this.datosEspecifMetod = datosEspecifMetod;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public PlantillaEval(Long idPlantillaEval, String nombre,
			DatosEspecificos datosEspecifMetod, boolean activo, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod,
			Set<PlantillaEvalDet> plantillaEvalDets) {
		this.idPlantillaEval = idPlantillaEval;
		this.nombre = nombre;
		this.datosEspecifMetod = datosEspecifMetod;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.plantillaEvalDets = plantillaEvalDets;
	}

	@Id
	@GeneratedValue(generator="PLANTILLA_EVAL_GENERATOR")
	@SequenceGenerator(name="PLANTILLA_EVAL_GENERATOR",sequenceName="eval_desempeno.plantilla_eval_id_plantilla_eval_seq")
	@Column(name = "id_plantilla_eval", unique = true, nullable = false)
	public Long getIdPlantillaEval() {
		return this.idPlantillaEval;
	}

	public void setIdPlantillaEval(Long idPlantillaEval) {
		this.idPlantillaEval = idPlantillaEval;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especif_metod", nullable = false)
	@NotNull
	public DatosEspecificos getDatosEspecifMetod() {
		return datosEspecifMetod;
	}

	public void setDatosEspecifMetod(DatosEspecificos datosEspecifMetod) {
		this.datosEspecifMetod = datosEspecifMetod;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plantillaEval")
	public Set<PlantillaEvalDet> getPlantillaEvalDets() {
		return this.plantillaEvalDets;
	}

	public void setPlantillaEvalDets(Set<PlantillaEvalDet> plantillaEvalDets) {
		this.plantillaEvalDets = plantillaEvalDets;
	}

}
