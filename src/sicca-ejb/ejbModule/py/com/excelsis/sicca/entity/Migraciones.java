package py.com.excelsis.sicca.entity;

// Generated 13/03/2013 02:34:33 PM by Hibernate Tools 3.4.0.Beta1

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
 * Migraciones generated by hbm2java
 */
@Entity
@Table(name = "migraciones", schema = "legajo")
public class Migraciones implements java.io.Serializable {

	private Long idMigracion;
	private ConfiguracionUoCab configuracionUoCab;
	private Persona persona;
	private String documentoIdentidad;
	private String nombre;
	private String apellido;
	private String tablaSicca;
	private Long idTabla;
	private Long mensaje;
	private String usuAlta;
	private Date fechaAlta;
	private String tipoPersona;

	public Migraciones() {
	}

	public Migraciones(Long idMigracion, String usuAlta, Date fechaAlta) {
		this.idMigracion = idMigracion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public Migraciones(
			Long idMigracion, ConfiguracionUoCab configuracionUoCab, Persona persona,
			String documentoIdentidad, String nombre, String apellido, String tablaSicca,
			Long idTabla, Long mensaje, String usuAlta, Date fechaAlta, String tipoPersona) {
		this.idMigracion = idMigracion;
		this.configuracionUoCab = configuracionUoCab;
		this.persona = persona;
		this.documentoIdentidad = documentoIdentidad;
		this.nombre = nombre;
		this.apellido = apellido;
		this.tablaSicca = tablaSicca;
		this.idTabla = idTabla;
		this.mensaje = mensaje;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.tipoPersona = tipoPersona;
	}

	@Id
	@Column(name = "id_migracion", unique = true, nullable = false)
	@GeneratedValue(generator = "MIGRACIONES_GENERATOR")
	@SequenceGenerator(name = "MIGRACIONES_GENERATOR", sequenceName = "legajo.id_migraciones_seq")
	public Long getIdMigracion() {
		return this.idMigracion;
	}

	public void setIdMigracion(Long idMigracion) {
		this.idMigracion = idMigracion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return this.configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "documento_identidad", length = 30)
	@Length(max = 30)
	public String getDocumentoIdentidad() {
		return this.documentoIdentidad;
	}

	public void setDocumentoIdentidad(String documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}

	@Column(name = "nombre", length = 50)
	@Length(max = 50)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "apellido", length = 50)
	@Length(max = 50)
	public String getApellido() {
		return this.apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	@Column(name = "tabla_sicca", length = 50)
	@Length(max = 50)
	public String getTablaSicca() {
		return this.tablaSicca;
	}

	public void setTablaSicca(String tablaSicca) {
		this.tablaSicca = tablaSicca;
	}

	@Column(name = "id_tabla")
	public Long getIdTabla() {
		return this.idTabla;
	}

	public void setIdTabla(Long idTabla) {
		this.idTabla = idTabla;
	}

	@Column(name = "mensaje")
	public Long getMensaje() {
		return this.mensaje;
	}

	public void setMensaje(Long mensaje) {
		this.mensaje = mensaje;
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

	@Column(name = "tipo_persona", length = 1)
	@Length(max = 1)
	public String getTipoPersona() {
		return this.tipoPersona;
	}

	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}

}