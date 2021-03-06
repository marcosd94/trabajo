package py.com.excelsis.sicca.entity;

// Generated 15-oct-2012 15:52:09 by Hibernate Tools 3.4.0.Beta1

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
 * DatosLicenciaMedica generated by hbm2java
 */
@Entity
@Table(name = "datos_licencia_medica", schema = "legajo")
public class DatosLicenciaMedica implements java.io.Serializable {

	private Long idDatosLicenciaMedica;
	private Persona persona;
	private String obsLicenciaMedica;
	private Date fechaInicio;
	private Date fechaFin;
	private Documentos documento;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public DatosLicenciaMedica() {
	}

	public DatosLicenciaMedica(Long idDatosLicenciaMedica, Date fechaInicio,
			boolean activo, String usuAlta, Date fechaAlta) {
		this.idDatosLicenciaMedica = idDatosLicenciaMedica;
		this.fechaInicio = fechaInicio;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public DatosLicenciaMedica(Long idDatosLicenciaMedica, Persona persona,
			String obsLicenciaMedica, Date fechaInicio, Date fechaFin,
			Documentos documento, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod) {
		this.idDatosLicenciaMedica = idDatosLicenciaMedica;
		this.persona = persona;
		this.obsLicenciaMedica = obsLicenciaMedica;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.documento = documento;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator="DATOS_LICENCIA_MEDICA_GENERATOR")
	@SequenceGenerator(name="DATOS_LICENCIA_MEDICA_GENERATOR",sequenceName="legajo.datos_licencia_medica_id_datos_licencia_medica_seq")
	@Column(name = "id_datos_licencia_medica", unique = true, nullable = false)
	public Long getIdDatosLicenciaMedica() {
		return this.idDatosLicenciaMedica;
	}

	public void setIdDatosLicenciaMedica(Long idDatosLicenciaMedica) {
		this.idDatosLicenciaMedica = idDatosLicenciaMedica;
	}



	@Column(name = "obs_licencia_medica", length = 250)
	@Length(max = 250)
	public String getObsLicenciaMedica() {
		return this.obsLicenciaMedica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento")
	public Documentos getDocumento() {
		return documento;
	}

	public void setDocumento(Documentos documento) {
		this.documento = documento;
	}

	public void setObsLicenciaMedica(String obsLicenciaMedica) {
		this.obsLicenciaMedica = obsLicenciaMedica;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_inicio", nullable = false, length = 13)
	@NotNull
	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_fin", length = 13)
	public Date getFechaFin() {
		return this.fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
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

}
