package py.com.excelsis.sicca.entity;

// Generated 12/01/2012 08:42:27 AM by Hibernate Tools 3.4.0.Beta1

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
 * ExcepcionDet generated by hbm2java
 */
@Entity
@Table(name = "excepcion_det", schema = "seleccion")
public class ExcepcionDet implements java.io.Serializable {

	private long idExcepcionDet;
	private Excepcion excepcion;
	private String observacion;
	private String usuObs;
	private Date fechaObs;
	private String respuesta;
	private String usuRpta;
	private Date fechaRpta;
	private Boolean activo;
	private Boolean ajustadoOee;
	private Boolean enviadoSfp;

	public ExcepcionDet() {
	}

	public ExcepcionDet(long idExcepcionDet, Excepcion excepcion,
			String observacion, Date fechaObs, Boolean activo,
			Boolean ajustadoOee, Boolean enviadoSfp) {
		this.idExcepcionDet = idExcepcionDet;
		this.excepcion = excepcion;
		this.observacion = observacion;
		this.fechaObs = fechaObs;
		this.activo = activo;
		this.ajustadoOee = ajustadoOee;
		this.enviadoSfp = enviadoSfp;
	}

	public ExcepcionDet(long idExcepcionDet, Excepcion excepcion,
			String observacion, String usuObs, Date fechaObs, String respuesta,
			String usuRpta, Date fechaRpta, Boolean activo,
			Boolean ajustadoOee, Boolean enviadoSfp) {
		this.idExcepcionDet = idExcepcionDet;
		this.excepcion = excepcion;
		this.observacion = observacion;
		this.usuObs = usuObs;
		this.fechaObs = fechaObs;
		this.respuesta = respuesta;
		this.usuRpta = usuRpta;
		this.fechaRpta = fechaRpta;
		this.activo = activo;
		this.ajustadoOee = ajustadoOee;
		this.enviadoSfp = enviadoSfp;
	}

	@Id
	@Column(name = "id_excepcion_det", unique = true, nullable = false)
	@GeneratedValue(generator="EXCEPCION_DET_GENERATOR")
	@SequenceGenerator(name="EXCEPCION_DET_GENERATOR",sequenceName="seleccion.sel_excepcion_det_id_excepcion_det_seq")
	public long getIdExcepcionDet() {
		return this.idExcepcionDet;
	}

	public void setIdExcepcionDet(long idExcepcionDet) {
		this.idExcepcionDet = idExcepcionDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_excepcion", nullable = false)
	@NotNull
	public Excepcion getExcepcion() {
		return this.excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}

	@Column(name = "observacion", nullable = false, length = 500)
	@NotNull
	@Length(max = 500)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "usu_obs", length = 50)
	@Length(max = 50)
	public String getUsuObs() {
		return this.usuObs;
	}

	public void setUsuObs(String usuObs) {
		this.usuObs = usuObs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_obs", nullable = false, length = 29)
	@NotNull
	public Date getFechaObs() {
		return this.fechaObs;
	}

	public void setFechaObs(Date fechaObs) {
		this.fechaObs = fechaObs;
	}

	@Column(name = "respuesta", length = 500)
	@Length(max = 500)
	public String getRespuesta() {
		return this.respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	@Column(name = "usu_rpta", length = 50)
	@Length(max = 50)
	public String getUsuRpta() {
		return this.usuRpta;
	}

	public void setUsuRpta(String usuRpta) {
		this.usuRpta = usuRpta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_rpta", length = 29)
	public Date getFechaRpta() {
		return this.fechaRpta;
	}

	public void setFechaRpta(Date fechaRpta) {
		this.fechaRpta = fechaRpta;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Column(name = "ajustado_oee", nullable = false)
	public Boolean getAjustadoOee() {
		return ajustadoOee;
	}


	public void setAjustadoOee(Boolean ajustadoOee) {
		this.ajustadoOee = ajustadoOee;
	}

	@Column(name = "enviado_sfp", nullable = false)
	public Boolean getEnviadoSfp() {
		return enviadoSfp;
	}

	

	public void setEnviadoSfp(Boolean enviadoSfp) {
		this.enviadoSfp = enviadoSfp;
	}

	
	

}
