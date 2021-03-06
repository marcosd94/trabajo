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
import org.jboss.seam.annotations.In;

/**
 * PersonaDiscapacidad generated by hbm2java
 */
@Entity
@Table(name = "persona_discapacidad", schema = "legajo")
public class PersonaDiscapacidadLegajo implements java.io.Serializable {

	private Long idPersonaDiscapacidad;
	private DatosEspecificos datosEspecificosByIdDatosEspecificosDiscapac;
	private DatosEspecificos datosEspecificosByIdDatosEspecificosGradoAutonom;
	private Persona persona;
	private String causa;
	private Integer grado;
	private Integer nroCertificado;
	private Date fechaEmision;
	private String dificultadActividad;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String actividadRealizar;
	private Documentos documentos;
	private PersonaPostulante personaPostulante;
	private String usuModOee;
	private Date fechaModOee;
	private String usuAltaOee;
	private Date fechaAltaOee;
	private String tipo;
	private DatosEspecificos datosEspecificoByIdDatosEspecificosCausa;

	public PersonaDiscapacidadLegajo() {
	}

	public PersonaDiscapacidadLegajo(
			Long idPersonaDiscapacidad,
			DatosEspecificos datosEspecificosByIdDatosEspecificosDiscapac,
			DatosEspecificos datosEspecificosByIdDatosEspecificosGradoAutonom, Integer grado,
			boolean activo, String usuAlta, Date fechaAlta) {
		this.idPersonaDiscapacidad = idPersonaDiscapacidad;
		this.datosEspecificosByIdDatosEspecificosDiscapac =
			datosEspecificosByIdDatosEspecificosDiscapac;
		this.datosEspecificosByIdDatosEspecificosGradoAutonom =
			datosEspecificosByIdDatosEspecificosGradoAutonom;
		this.grado = grado;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public PersonaDiscapacidadLegajo(
			Long idPersonaDiscapacidad,
			DatosEspecificos datosEspecificosByIdDatosEspecificosDiscapac,
			DatosEspecificos datosEspecificosByIdDatosEspecificosGradoAutonom, String causa,
			Integer grado, Integer nroCertificado, Date fechaEmision, String dificultadActividad,
			boolean activo, String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idPersonaDiscapacidad = idPersonaDiscapacidad;
		this.datosEspecificosByIdDatosEspecificosDiscapac =
			datosEspecificosByIdDatosEspecificosDiscapac;
		this.datosEspecificosByIdDatosEspecificosGradoAutonom =
			datosEspecificosByIdDatosEspecificosGradoAutonom;
		this.causa = causa;
		this.grado = grado;
		this.nroCertificado = nroCertificado;
		this.fechaEmision = fechaEmision;
		this.dificultadActividad = dificultadActividad;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator = "PERSONA_DISCAPACIDADLEGAJO_GENERATOR")
	@SequenceGenerator(name = "PERSONA_DISCAPACIDADLEGAJO_GENERATOR", sequenceName = "legajo.persona_discapacidad_id_persona_discapacidad_seq")
	@Column(name = "id_persona_discapacidad", unique = true, nullable = false)
	public Long getIdPersonaDiscapacidad() {
		return this.idPersonaDiscapacidad;
	}

	public void setIdPersonaDiscapacidad(Long idPersonaDiscapacidad) {
		this.idPersonaDiscapacidad = idPersonaDiscapacidad;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_discapac", nullable = false)
	@NotNull
	public DatosEspecificos getDatosEspecificosByIdDatosEspecificosDiscapac() {
		return this.datosEspecificosByIdDatosEspecificosDiscapac;
	}

	public void setDatosEspecificosByIdDatosEspecificosDiscapac(DatosEspecificos datosEspecificosByIdDatosEspecificosDiscapac) {
		this.datosEspecificosByIdDatosEspecificosDiscapac =
			datosEspecificosByIdDatosEspecificosDiscapac;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_grado_autonom")
	public DatosEspecificos getDatosEspecificosByIdDatosEspecificosGradoAutonom() {
		return this.datosEspecificosByIdDatosEspecificosGradoAutonom;
	}

	public void setDatosEspecificosByIdDatosEspecificosGradoAutonom(DatosEspecificos datosEspecificosByIdDatosEspecificosGradoAutonom) {
		this.datosEspecificosByIdDatosEspecificosGradoAutonom =
			datosEspecificosByIdDatosEspecificosGradoAutonom;
	}

	@Column(name = "causa", length = 200)
	@Length(max = 200)
	public String getCausa() {
		return this.causa;
	}

	public void setCausa(String causa) {
		this.causa = causa;
	}

	@Column(name = "grado", nullable = false)
	public Integer getGrado() {
		return this.grado;
	}

	public void setGrado(Integer grado) {
		this.grado = grado;
	}

	@Column(name = "nro_certificado")
	public Integer getNroCertificado() {
		return this.nroCertificado;
	}

	public void setNroCertificado(Integer nroCertificado) {
		this.nroCertificado = nroCertificado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_emision", length = 29)
	public Date getFechaEmision() {
		return this.fechaEmision;
	}

	public void setFechaEmision(Date fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	@Column(name = "dificultad_actividad", length = 250)
	@Length(max = 250)
	public String getDificultadActividad() {
		return this.dificultadActividad;
	}

	public void setDificultadActividad(String dificultadActividad) {
		this.dificultadActividad = dificultadActividad;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "actividad_realizar")
	public String getActividadRealizar() {
		return actividadRealizar;
	}

	public void setActividadRealizar(String actividadRealizar) {
		this.actividadRealizar = actividadRealizar;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento")
	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona_postulante")
	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}

	public void setPersonaPostulante(PersonaPostulante personaPostulante) {
		this.personaPostulante = personaPostulante;
	}

	@Column(name = "usu_alta_oee", length = 50)
	@Length(max = 50)
	public String getUsuAltaOee() {
		return usuAltaOee;
	}

	public void setUsuAltaOee(String usuAltaOee) {
		this.usuAltaOee = usuAltaOee;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta_oee", length = 29)
	public Date getFechaAltaOee() {
		return fechaAltaOee;
	}

	public void setFechaAltaOee(Date fechaAltaOee) {
		this.fechaAltaOee = fechaAltaOee;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod_oee", length = 29)
	public Date getFechaModOee() {
		return fechaModOee;
	}

	public void setFechaModOee(Date fechaModOee) {
		this.fechaModOee = fechaModOee;
	}

	@Column(name = "usu_mod_oee", length = 50)
	public String getUsuModOee() {
		return usuModOee;
	}

	public void setUsuModOee(String usuModOee) {
		this.usuModOee = usuModOee;
	}

	@Column(name = "tipo", length = 100)
	@Length(max = 100)
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_causa")
	public DatosEspecificos getDatosEspecificoByIdDatosEspecificosCausa() {
		return datosEspecificoByIdDatosEspecificosCausa;
	}

	public void setDatosEspecificoByIdDatosEspecificosCausa(
			DatosEspecificos datosEspecificoByIdDatosEspecificosCausa) {
		this.datosEspecificoByIdDatosEspecificosCausa = datosEspecificoByIdDatosEspecificosCausa;
	}
	
	
}
