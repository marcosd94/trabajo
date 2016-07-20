package py.com.excelsis.sicca.entity;

// Generated 11/11/2011 02:29:19 PM by Hibernate Tools 3.4.0.Beta1

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

import py.com.excelsis.sicca.seguridad.entity.Rol;

/**
 * ComisionSeleccionDet generated by hbm2java
 */
@Entity
@Table(name = "comision_seleccion_det", schema = "seleccion")
public class ComisionSeleccionDet implements java.io.Serializable {

	private Long idComisionSelDet;
	private ComisionSeleccionCab comisionSeleccionCab;
	private Persona persona;
	private ConfiguracionUoCab configuracionUo;
	private Rol rol;
	private String titularSuplente;
	private Boolean equipoTecnico;
	private Documentos documentos;
	private String puesto;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String tipo;
	private Excepcion excepcion;
	private Boolean seleccionado;

	public ComisionSeleccionDet() {
	}

	public ComisionSeleccionDet(long idComisionSelDet,
			ComisionSeleccionCab comisionSeleccionCab, Persona idPersona,
			ConfiguracionUoCab idConfiguracionUo, Rol idRol, String titularSuplente,
			Boolean equipoTecnico) {
		this.idComisionSelDet = idComisionSelDet;
		this.comisionSeleccionCab = comisionSeleccionCab;
		this.persona = idPersona;
		this.configuracionUo = idConfiguracionUo;
		this.rol = idRol;
		this.titularSuplente = titularSuplente;
		this.equipoTecnico = equipoTecnico;
	}

	@Id
	@Column(name = "id_comision_sel_det", unique = true, nullable = false)
	@GeneratedValue(generator="COMISION_SELE_DET_GENERATOR")
	@SequenceGenerator(name="COMISION_SELE_DET_GENERATOR",sequenceName="seleccion.comision_seleccion_det_id_comision_sel_det_seq")
	public Long getIdComisionSelDet() {
		return this.idComisionSelDet;
	}

	public void setIdComisionSelDet(Long idComisionSelDet) {
		this.idComisionSelDet = idComisionSelDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comision_sel", nullable = false)
	@NotNull
	public ComisionSeleccionCab getComisionSeleccionCab() {
		return this.comisionSeleccionCab;
	}

	public void setComisionSeleccionCab(
			ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
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
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUo() {
		return configuracionUo;
	}

	public void setConfiguracionUo(ConfiguracionUoCab configuracionUo) {
		this.configuracionUo = configuracionUo;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rol")
	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	

	@Column(name = "titular_suplente", nullable = false, length = 1)
	@NotNull
	@Length(max = 1)
	public String getTitularSuplente() {
		return this.titularSuplente;
	}

	public void setTitularSuplente(String titularSuplente) {
		this.titularSuplente = titularSuplente;
	}

	@Column(name = "equipo_tecnico", nullable = false)
	@NotNull
	public Boolean getEquipoTecnico() {
		return equipoTecnico;
	}

	public void setEquipoTecnico(Boolean equipoTecnico) {
		this.equipoTecnico = equipoTecnico;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento")
	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	@Column(name = "puesto")
	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}
	
	@Column(name = "tipo")
	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
	@Column(name = "activo")
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
	@JoinColumn(name = "id_excepcion")
	public Excepcion getExcepcion() {
		return this.excepcion;
	}

	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}
	
	@Transient
	public Boolean getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
}