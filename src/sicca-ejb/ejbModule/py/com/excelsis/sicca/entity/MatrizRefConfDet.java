package py.com.excelsis.sicca.entity;

// Generated 10/11/2011 03:16:02 PM by Hibernate Tools 3.4.0.Beta1

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
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * MatrizRefConfDet generated by hbm2java
 */
@Entity
@Table(name = "matriz_ref_conf_det", schema = "seleccion")
public class MatrizRefConfDet implements java.io.Serializable {

	private Long idMatrizRefConfDet;
	private MatrizRefConfEnc matrizRefConfEnc;
	private String descripcion;
	private float puntajeMinimo;
	private float puntajeMaximo;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Float puntaje;
	private String criteriosEvaluacion;//MODIFICADO RV
	
	public MatrizRefConfDet() {
	}

	public MatrizRefConfDet(Long idMatrizRefConfDet,
			MatrizRefConfEnc matrizRefConfEnc, String descripcion,
			float puntajeMinimo, float puntajeMaximo, boolean activo,
			String usuAlta, Date fechaAlta) {
		this.idMatrizRefConfDet = idMatrizRefConfDet;
		this.matrizRefConfEnc = matrizRefConfEnc;
		this.descripcion = descripcion;
		this.puntajeMinimo = puntajeMinimo;
		this.puntajeMaximo = puntajeMaximo;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public MatrizRefConfDet(Long idMatrizRefConfDet,
			MatrizRefConfEnc matrizRefConfEnc, String descripcion,
			float puntajeMinimo, float puntajeMaximo, boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idMatrizRefConfDet = idMatrizRefConfDet;
		this.matrizRefConfEnc = matrizRefConfEnc;
		this.descripcion = descripcion;
		this.puntajeMinimo = puntajeMinimo;
		this.puntajeMaximo = puntajeMaximo;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator="MATRIZ_REF_CONF_DET_GENERATOR")
	@SequenceGenerator(name="MATRIZ_REF_CONF_DET_GENERATOR",sequenceName="seleccion.matriz_ref_conf_det_id_matriz_ref_conf_det_seq")
	@Column(name = "id_matriz_ref_conf_det", unique = true, nullable = false)
	public Long getIdMatrizRefConfDet() {
		return this.idMatrizRefConfDet;
	}

	public void setIdMatrizRefConfDet(Long idMatrizRefConfDet) {
		this.idMatrizRefConfDet = idMatrizRefConfDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matriz_ref_conf_enc", nullable = false)
	@NotNull
	public MatrizRefConfEnc getMatrizRefConfEnc() {
		return this.matrizRefConfEnc;
	}

	public void setMatrizRefConfEnc(MatrizRefConfEnc matrizRefConfEnc) {
		this.matrizRefConfEnc = matrizRefConfEnc;
	}

	@Column(name = "descripcion", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "puntaje_minimo", nullable = false, precision = 8, scale = 8)
	public float getPuntajeMinimo() {
		return this.puntajeMinimo;
	}

	public void setPuntajeMinimo(float puntajeMinimo) {
		this.puntajeMinimo = puntajeMinimo;
	}

	@Column(name = "puntaje_maximo", nullable = false, precision = 8, scale = 8)
	public float getPuntajeMaximo() {
		return this.puntajeMaximo;
	}

	public void setPuntajeMaximo(float puntajeMaximo) {
		this.puntajeMaximo = puntajeMaximo;
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

	
	@Transient
	public Float getPuntaje() {
		return puntaje;
	}

	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}
	
	@Column(name = "criterios_evaluacion")
	public String getCriteriosEvaluacion() {
		return this.criteriosEvaluacion;
	}

	public void setCriteriosEvaluacion(String criteriosEvaluacion) {
		this.criteriosEvaluacion = criteriosEvaluacion;
	}

}
