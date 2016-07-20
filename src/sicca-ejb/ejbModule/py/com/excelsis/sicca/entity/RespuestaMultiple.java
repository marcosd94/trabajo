package py.com.excelsis.sicca.entity;

// Generated 13/07/2012 08:32:42 AM by Hibernate Tools 3.4.0.Beta1

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
 * RespuestaMultiple generated by hbm2java
 */
@Entity
@Table(name = "respuesta_multiple", schema = "capacitacion")
public class RespuestaMultiple implements java.io.Serializable {

	private long idRespuestaM;
	private Preguntas preguntas;
	private String respuestaOpc;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public RespuestaMultiple() {
	}

	public RespuestaMultiple(long idRespuestaM, Preguntas preguntas,
			String respuestaOpc, boolean activo, String usuAlta, Date fechaAlta) {
		this.idRespuestaM = idRespuestaM;
		this.preguntas = preguntas;
		this.respuestaOpc = respuestaOpc;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public RespuestaMultiple(long idRespuestaM, Preguntas preguntas,
			String respuestaOpc, boolean activo, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod) {
		this.idRespuestaM = idRespuestaM;
		this.preguntas = preguntas;
		this.respuestaOpc = respuestaOpc;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator = "RESPUESTA_MULTIPLE_GENERATOR")
	@SequenceGenerator(name = "RESPUESTA_MULTIPLE_GENERATOR", sequenceName = "capacitacion.respuesta_multiple_id_respuesta_m_seq")
	@Column(name = "id_respuesta_m", unique = true, nullable = false)
	public long getIdRespuestaM() {
		return this.idRespuestaM;
	}

	public void setIdRespuestaM(long idRespuestaM) {
		this.idRespuestaM = idRespuestaM;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pregunta", nullable = false)
	@NotNull
	public Preguntas getPreguntas() {
		return this.preguntas;
	}

	public void setPreguntas(Preguntas preguntas) {
		this.preguntas = preguntas;
	}

	@Column(name = "respuesta_opc", nullable = false, length = 250)
	@NotNull
	@Length(max = 250)
	public String getRespuestaOpc() {
		return this.respuestaOpc;
	}

	public void setRespuestaOpc(String respuestaOpc) {
		this.respuestaOpc = respuestaOpc;
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