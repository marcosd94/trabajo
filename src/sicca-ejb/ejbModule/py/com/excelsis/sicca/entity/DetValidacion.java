package py.com.excelsis.sicca.entity;

// Generated 22-ago-2012 15:32:43 by Hibernate Tools 3.4.0.Beta1

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
 * DetValidacion generated by hbm2java
 */
@Entity
@Table(name = "det_validacion", schema = "general")
public class DetValidacion implements java.io.Serializable {

	private Long idDetValidacion;
	private CabValidacion cabValidacion;
	private String nombreValidacion;
	private String explicValidacion;
	private Boolean activo;
	private String justifEstado;
	private Boolean bloquea;
	private String valorIdentif;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String tipo;
	private String mensaje;

	public DetValidacion() {
	}

	public DetValidacion(Long idDetValidacion, CabValidacion cabValidacion,
			String nombreValidacion, String usuAlta, Date fechaAlta) {
		this.idDetValidacion = idDetValidacion;
		this.cabValidacion = cabValidacion;
		this.nombreValidacion = nombreValidacion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public DetValidacion(Long idDetValidacion, CabValidacion cabValidacion,
			String nombreValidacion, String explicValidacion, Boolean activo,
			String justifEstado, Boolean bloquea, String valorIdentif,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			String tipo, String mensaje) {
		this.idDetValidacion = idDetValidacion;
		this.cabValidacion = cabValidacion;
		this.nombreValidacion = nombreValidacion;
		this.explicValidacion = explicValidacion;
		this.activo = activo;
		this.justifEstado = justifEstado;
		this.bloquea = bloquea;
		this.valorIdentif = valorIdentif;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.tipo = tipo;
		this.mensaje = mensaje;
	}

	@Id
	@GeneratedValue(generator="DET_VALIDACION_GENERATOR")
	@SequenceGenerator(name="DET_VALIDACION_GENERATOR",sequenceName="general.det_validacion_id_det_validacion_seq")
	@Column(name = "id_det_validacion", unique = true, nullable = false)
	public Long getIdDetValidacion() {
		return this.idDetValidacion;
	}

	public void setIdDetValidacion(Long idDetValidacion) {
		this.idDetValidacion = idDetValidacion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cab_validacion", nullable = false)
	@NotNull
	public CabValidacion getCabValidacion() {
		return this.cabValidacion;
	}

	public void setCabValidacion(CabValidacion cabValidacion) {
		this.cabValidacion = cabValidacion;
	}

	@Column(name = "nombre_validacion", nullable = false, length = 20)
	@NotNull
	@Length(max = 20)
	public String getNombreValidacion() {
		return this.nombreValidacion;
	}

	public void setNombreValidacion(String nombreValidacion) {
		this.nombreValidacion = nombreValidacion;
	}

	@Column(name = "explic_validacion")
	public String getExplicValidacion() {
		return this.explicValidacion;
	}

	public void setExplicValidacion(String explicValidacion) {
		this.explicValidacion = explicValidacion;
	}

	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Column(name = "justif_estado")
	public String getJustifEstado() {
		return this.justifEstado;
	}

	public void setJustifEstado(String justifEstado) {
		this.justifEstado = justifEstado;
	}

	@Column(name = "bloquea")
	public Boolean getBloquea() {
		return this.bloquea;
	}

	public void setBloquea(Boolean bloquea) {
		this.bloquea = bloquea;
	}

	@Column(name = "valor_identif", length = 20)
	@Length(max = 20)
	public String getValorIdentif() {
		return this.valorIdentif;
	}

	public void setValorIdentif(String valorIdentif) {
		this.valorIdentif = valorIdentif;
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
	@Column(name = "fecha_alta", nullable = false, length = 22)
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
	@Column(name = "fecha_mod", length = 22)
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Column(name = "tipo", length = 7)
	@Length(max = 7)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "mensaje", length = 75)
	@Length(max = 75)
	public String getMensaje() {
		return this.mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}