package py.com.excelsis.sicca.entity;

// Generated 23/10/2012 03:25:46 PM by Hibernate Tools 3.4.0.Beta1

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
 * DatosVacaciones generated by hbm2java
 */
@Entity
@Table(name = "datos_vacaciones", schema = "legajo")
public class DatosVacaciones implements java.io.Serializable {

	private Long idDatosVacaciones;
	private Documentos documentos;
	private Persona persona;
	private int totalDias;
	private int diasUtilizar;
	private int saldo;
	private Date fechaInicio;
	private Date fechaFin;
	private String obsVacaciones;
	private Integer nroActo;
	private Date fechaActo;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public DatosVacaciones() {
	}

	public DatosVacaciones(
			Long idDatosVacaciones, int totalDias, int diasUtilizar, int saldo, Date fechaInicio,
			boolean activo, String usuAlta, Date fechaAlta) {
		this.idDatosVacaciones = idDatosVacaciones;
		this.totalDias = totalDias;
		this.diasUtilizar = diasUtilizar;
		this.saldo = saldo;
		this.fechaInicio = fechaInicio;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public DatosVacaciones(
			Long idDatosVacaciones, Documentos documentos, Persona persona, int totalDias,
			int diasUtilizar, int saldo, Date fechaInicio, Date fechaFin, String obsVacaciones,
			Integer nroActo, Date fechaActo, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod) {
		this.idDatosVacaciones = idDatosVacaciones;
		this.documentos = documentos;
		this.persona = persona;
		this.totalDias = totalDias;
		this.diasUtilizar = diasUtilizar;
		this.saldo = saldo;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.obsVacaciones = obsVacaciones;
		this.nroActo = nroActo;
		this.fechaActo = fechaActo;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_datos_vacaciones", unique = true, nullable = false)
	@GeneratedValue(generator = "DATOS_VACACIONES_GENERATOR")
	@SequenceGenerator(name = "DATOS_VACACIONES_GENERATOR", sequenceName = "legajo.datos_vacaciones_id_datos_vacaciones_seq")
	public Long getIdDatosVacaciones() {
		return this.idDatosVacaciones;
	}

	public void setIdDatosVacaciones(Long idDatosVacaciones) {
		this.idDatosVacaciones = idDatosVacaciones;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento")
	public Documentos getDocumentos() {
		return this.documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "total_dias", nullable = false)
	public int getTotalDias() {
		return this.totalDias;
	}

	public void setTotalDias(int totalDias) {
		this.totalDias = totalDias;
	}

	@Column(name = "dias_utilizar", nullable = false)
	public int getDiasUtilizar() {
		return this.diasUtilizar;
	}

	public void setDiasUtilizar(int diasUtilizar) {
		this.diasUtilizar = diasUtilizar;
	}

	@Column(name = "saldo", nullable = false)
	public int getSaldo() {
		return this.saldo;
	}

	public void setSaldo(int saldo) {
		this.saldo = saldo;
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

	@Column(name = "obs_vacaciones", length = 500)
	@Length(max = 500)
	public String getObsVacaciones() {
		return this.obsVacaciones;
	}

	public void setObsVacaciones(String obsVacaciones) {
		this.obsVacaciones = obsVacaciones;
	}

	@Column(name = "nro_acto")
	public Integer getNroActo() {
		return this.nroActo;
	}

	public void setNroActo(Integer nroActo) {
		this.nroActo = nroActo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_acto", length = 13)
	public Date getFechaActo() {
		return this.fechaActo;
	}

	public void setFechaActo(Date fechaActo) {
		this.fechaActo = fechaActo;
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