package py.com.excelsis.sicca.entity;

// Generated 21/10/2011 01:18:35 PM by Hibernate Tools 3.4.0.Beta1

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
 * ReprPersonaDiscapacidad generated by hbm2java
 */
@Entity
@Table(name = "repr_persona_discapacidad", schema = "seleccion")
public class ReprPersonaDiscapacidad implements java.io.Serializable {

	private Integer idReprPersonaDiscapacidad;
	private Persona persona;
	private String observacion;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private PersonaPostulante personaPostulante;
	private Persona personaRep;

	public ReprPersonaDiscapacidad() {
	}

	public ReprPersonaDiscapacidad(Integer idReprPersonaDiscapacidad,
			Persona idPersona, Boolean activo, String usuAlta, Date fechaAlta) {
		this.idReprPersonaDiscapacidad = idReprPersonaDiscapacidad;
		this.persona = idPersona;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ReprPersonaDiscapacidad(Integer idReprPersonaDiscapacidad,
			Persona idPersona, String observacion, Boolean activo, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod) {
		this.idReprPersonaDiscapacidad = idReprPersonaDiscapacidad;
		this.persona = idPersona;
		this.observacion = observacion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_repr_persona_discapacidad", unique = true, nullable = false)
	@GeneratedValue(generator="REPRE_PERSONA_DISCAPACIDAD_GENERATOR")
	@SequenceGenerator(name="REPRE_PERSONA_DISCAPACIDAD_GENERATOR",sequenceName="seleccion.repr_persona_discapacidad_id_repr_persona_discapacidad_seq")
	public Integer getIdReprPersonaDiscapacidad() {
		return this.idReprPersonaDiscapacidad;
	}

	public void setIdReprPersonaDiscapacidad(Integer idReprPersonaDiscapacidad) {
		this.idReprPersonaDiscapacidad = idReprPersonaDiscapacidad;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "observacion", length = 500)
	@Length(max = 500)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	
	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona_postulante")
	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}

	public void setPersonaPostulante(PersonaPostulante personaPostulante) {
		this.personaPostulante = personaPostulante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona_rep")
	public Persona getPersonaRep() {
		return personaRep;
	}

	public void setPersonaRep(Persona personaRep) {
		this.personaRep = personaRep;
	}
	
	
	
	

}
