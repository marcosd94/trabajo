package py.com.excelsis.sicca.entity;

// Generated 19/10/2011 10:58:12 AM by Hibernate Tools 3.4.0.Beta1

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
 * MatrizReferencialDet generated by hbm2java
 */
@Entity
@Table(name = "matriz_referencial_det", schema = "seleccion")
public class MatrizReferencialDet implements java.io.Serializable, Cloneable {

	private Long idMatrizReferencialDet;
	private MatrizReferencialEnc matrizReferencialEnc;
	private String descripcion;
	private Float puntajeMinimo;
	private Float puntajeMaximo;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String criteriosEvaluacion;//MODIFICADO RV

	public MatrizReferencialDet() {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public MatrizReferencialDet(
			Long idMatrizReferencialDet, MatrizReferencialEnc matrizReferencialEnc,
			String descripcion, Float puntajeMinimo, Float puntajeMaximo, Boolean activo,
			String usuAlta, Date fechaAlta) {
		this.idMatrizReferencialDet = idMatrizReferencialDet;
		this.matrizReferencialEnc = matrizReferencialEnc;
		this.descripcion = descripcion;
		this.puntajeMinimo = puntajeMinimo;
		this.puntajeMaximo = puntajeMaximo;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public MatrizReferencialDet(
			Long idMatrizReferencialDet, MatrizReferencialEnc matrizReferencialEnc,
			String descripcion, Float puntajeMinimo, Float puntajeMaximo, Boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idMatrizReferencialDet = idMatrizReferencialDet;
		this.matrizReferencialEnc = matrizReferencialEnc;
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
	@GeneratedValue(generator = "MATRIZ_REFERENCIAL_DET_GENERATOR")
	@SequenceGenerator(name = "MATRIZ_REFERENCIAL_DET_GENERATOR", sequenceName = "seleccion.matriz_referencial_det_id_matriz_referencial_det_seq")
	@Column(name = "id_matriz_referencial_det", unique = true, nullable = false)
	public Long getIdMatrizReferencialDet() {
		return this.idMatrizReferencialDet;
	}

	public void setIdMatrizReferencialDet(Long idMatrizReferencialDet) {
		this.idMatrizReferencialDet = idMatrizReferencialDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matriz_referencial_enc", nullable = false)
	@NotNull
	public MatrizReferencialEnc getMatrizReferencialEnc() {
		return this.matrizReferencialEnc;
	}

	public void setMatrizReferencialEnc(MatrizReferencialEnc matrizReferencialEnc) {
		this.matrizReferencialEnc = matrizReferencialEnc;
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
	public Float getPuntajeMinimo() {
		return this.puntajeMinimo;
	}

	public void setPuntajeMinimo(Float puntajeMinimo) {
		this.puntajeMinimo = puntajeMinimo;
	}

	@Column(name = "puntaje_maximo", nullable = false, precision = 8, scale = 8)
	public Float getPuntajeMaximo() {
		return this.puntajeMaximo;
	}

	public void setPuntajeMaximo(Float puntajeMaximo) {
		this.puntajeMaximo = puntajeMaximo;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
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
	
	@Column(name = "criterios_evaluacion", length = 2000)
	@Length(max = 2000)
	public String getCriteriosEvaluacion() {
		return this.criteriosEvaluacion;
	}

	public void setCriteriosEvaluacion(String criteriosEvaluacion) {
		this.criteriosEvaluacion = criteriosEvaluacion;
	}

}
