package py.com.excelsis.sicca.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;

@Entity
@Table(name = "desbloqueo", schema = "capacitacion")
public class Desbloqueo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4821059387745776198L;
	private String pais;
	private Long idPais;
	private String tipoCapacitacion;
	private String capacitacion;
	private String participante;
	private String ci;
	private String nombres;
	private String apellidos;
	private String motivo;
	private String observacion;
	private Date fechaBloqueo;
	private String motivoHabilitacion;
	private Date fechaDesbloqueo;
	private String estado;
	private Long idDocumento;
	private String oee;
	private Long idCapacitacion;
	private Long idListaDet;

	public Desbloqueo() {
		
	}

	public Desbloqueo(String pais, Long idPais, String tipoCapacitacion,
			String capacitacion, String participante, String ci,
			String nombres, String apellidos, String motivo,
			String observacion, Date fechaBloqueo, String motivoHabilitacion,
			Date fechaDesbloqueo, String estado, Long idDocumento, String oee,
			Long idCapacitacion, Long idListaDet) {

		this.pais = pais;
		this.idPais = idPais;
		this.tipoCapacitacion = tipoCapacitacion;
		this.capacitacion = capacitacion;
		this.participante = participante;
		this.ci = ci;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.motivo = motivo;
		this.observacion = observacion;
		this.fechaBloqueo = fechaBloqueo;
		this.motivoHabilitacion = motivoHabilitacion;
		this.fechaDesbloqueo = fechaDesbloqueo;
		this.estado = estado;
		this.idDocumento = idDocumento;
		this.oee = oee;
		this.idCapacitacion = idCapacitacion;
		this.idListaDet = idListaDet;

	}

	@Id
	@Column(name = "id_lista_det")
	public Long getIdListaDet() {
		return idListaDet;
	}

	public void setIdListaDet(Long idListaDet) {
		this.idListaDet = idListaDet;
	}

	@Column(name = "pais")
	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	@Column(name = "id_pais")
	public Long getIdPais() {
		return idPais;
	}

	public void setIdPais(Long idPais) {
		this.idPais = idPais;
	}

	@Column(name = "tipo_capacitacion")
	public String getTipoCapacitacion() {
		return tipoCapacitacion;
	}

	public void setTipoCapacitacion(String tipoCapacitacion) {
		this.tipoCapacitacion = tipoCapacitacion;
	}

	@Column(name = "capacitacion")
	public String getCapacitacion() {
		return capacitacion;
	}

	public void setCapacitacion(String capacitacion) {
		this.capacitacion = capacitacion;
	}

	@Column(name = "participante")
	public String getParticipante() {
		return participante;
	}

	public void setParticipante(String participante) {
		this.participante = participante;
	}

	@Column(name = "ci")
	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	@Column(name = "nombres")
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	@Column(name = "apellidos")
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	@Column(name = "motivo")
	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	@Column(name = "observacion")
	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "fecha_bloqueo", length = 29)
	public Date getFechaBloqueo() {
		return fechaBloqueo;
	}

	public void setFechaBloqueo(Date fechaBloqueo) {
		this.fechaBloqueo = fechaBloqueo;
	}

	@Column(name = "motivo_habilitacion")
	public String getMotivoHabilitacion() {
		return motivoHabilitacion;
	}

	public void setMotivoHabilitacion(String motivoHabilitacion) {
		this.motivoHabilitacion = motivoHabilitacion;
	}

	@Column(name = "fecha_desbloqueo", length = 29)
	public Date getFechaDesbloqueo() {
		return fechaDesbloqueo;
	}

	public void setFechaDesbloqueo(Date fechaDesbloqueo) {
		this.fechaDesbloqueo = fechaDesbloqueo;
	}

	@Column(name = "estado")
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "id_documento")
	public Long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	@Column(name = "oee")
	public String getOee() {
		return oee;
	}

	public void setOee(String oee) {
		this.oee = oee;
	}

	@Column(name = "id_capacitacion")
	public Long getIdCapacitacion() {
		return idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

}
