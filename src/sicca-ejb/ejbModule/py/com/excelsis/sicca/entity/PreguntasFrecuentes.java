package py.com.excelsis.sicca.entity;

// Generated 17/10/2011 10:16:38 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.Properties;

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
import org.jboss.seam.annotations.In;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * PreguntasFrecuentes generated by hbm2java
 */
@Entity
@Table(name = "preguntas_frecuentes", schema = "seleccion")
public class PreguntasFrecuentes extends EntityBase {

	private Long idPreguntaFrecuente;
	private DatosEspecificos datosEspecificos;
	private Integer nroOrden;
	private String preguntaFrecuente;
	private String respuestaPregunta;
	private boolean publicarSN;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Boolean mostrar;

	public PreguntasFrecuentes() {
	}

	public PreguntasFrecuentes(Long idPreguntaFrecuente,
			DatosEspecificos datosEspecificos, Integer nroOrden,
			String preguntaFrecuente, String respuestaPregunta,
			boolean publicarSN, boolean activo, String usuAlta, Date fechaAlta) {
		this.idPreguntaFrecuente = idPreguntaFrecuente;
		this.datosEspecificos = datosEspecificos;
		this.nroOrden = nroOrden;
		this.preguntaFrecuente = preguntaFrecuente;
		this.respuestaPregunta = respuestaPregunta;
		this.publicarSN = publicarSN;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public PreguntasFrecuentes(Long idPreguntaFrecuente,
			DatosEspecificos datosEspecificos, Integer nroOrden,
			String preguntaFrecuente, String respuestaPregunta,
			boolean publicarSN, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod) {
		this.idPreguntaFrecuente = idPreguntaFrecuente;
		this.datosEspecificos = datosEspecificos;
		this.nroOrden = nroOrden;
		this.preguntaFrecuente = preguntaFrecuente;
		this.respuestaPregunta = respuestaPregunta;
		this.publicarSN = publicarSN;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator="PREGUNTAS_FRECUENTES_GENERATOR")
	@SequenceGenerator(name="PREGUNTAS_FRECUENTES_GENERATOR",sequenceName="seleccion.preguntas_frecuentes_id_pregunta_frecuente_seq")
	@Column(name = "id_pregunta_frecuente", unique = true, nullable = false)
	public Long getIdPreguntaFrecuente() {
		return this.idPreguntaFrecuente;
	}

	public void setIdPreguntaFrecuente(Long idPreguntaFrecuente) {
		this.idPreguntaFrecuente = idPreguntaFrecuente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_tipo_preg", nullable = false)
	@NotNull
	public DatosEspecificos getDatosEspecificos() {
		return this.datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	@Column(name = "nro_orden", nullable = false)
	public Integer getNroOrden() {
		return this.nroOrden;
	}

	public void setNroOrden(Integer nroOrden) {
		this.nroOrden = nroOrden;
	}

	@Column(name = "pregunta_frecuente", nullable = false, length = 250)
	@NotNull
	@Length(max = 250)
	public String getPreguntaFrecuente() {
		return this.preguntaFrecuente;
	}

	public void setPreguntaFrecuente(String preguntaFrecuente) {
		this.preguntaFrecuente = preguntaFrecuente;
	}

	@Column(name = "respuesta_pregunta", nullable = false, length = 1000)
	@NotNull
	@Length(max = 1000)
	public String getRespuestaPregunta() {
		return this.respuestaPregunta;
	}

	public void setRespuestaPregunta(String respuestaPregunta) {
		this.respuestaPregunta = respuestaPregunta;
	}

	@Column(name = "publicar_s_n", nullable = false)
	public boolean isPublicarSN() {
		return this.publicarSN;
	}

	public void setPublicarSN(boolean publicarSN) {
		this.publicarSN = publicarSN;
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
	public Boolean getMostrar() {
		return mostrar;
	}

	public void setMostrar(Boolean mostrar) {
		this.mostrar = mostrar;
	}

	@Transient
	public Long getId() {
		return idPreguntaFrecuente;
	}

	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.PREGUNTAS_FRECUENTES_ID, getId());
		properties.put(SICCASessionParameters.PREGUNTAS_FRECUENTES_PREGUNTA,
				preguntaFrecuente);
		properties.put(SICCASessionParameters.PREGUNTAS_FRECUENTES_RESPUESTA,
				respuestaPregunta);

		return properties;
	}

	/**
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

}
