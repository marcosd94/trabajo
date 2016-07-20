package py.com.excelsis.sicca.seguridad.entity;

// Generated 14-sep-2011 13:12:06 by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.ConfiguracionUoCab;
import py.com.excelsis.sicca.entity.ConfiguracionUoDet;
import py.com.excelsis.sicca.entity.Persona;

/**
 * Usuario generated by hbm2java
 */
@NamedQueries( { @NamedQuery(name = "miUsuario", query = "SELECT o FROM Usuario o WHERE lower(o.codigoUsuario)=:U "),
	@NamedQuery(name = "Usuario.login", query = "SELECT o FROM Usuario o WHERE lower(o.codigoUsuario)=lower(:U) and o.contrasenha=:P AND o.activo = true"),
	@NamedQuery(name = "Usuario.portal", query = "SELECT o FROM Usuario o WHERE lower(o.persona.documentoIdentidad)=lower(:U) " +
			"AND o.contrasenha=:P AND o.persona.paisByIdPaisExpedicionDoc.idPais=:T " +
			"AND o.activo = true AND lower(o.usuAlta) like '%portal%'")
})


@Entity
@Table(name = "usuario", schema = "seguridad")
public class Usuario implements java.io.Serializable {

	private Long idUsuario;
	private String codigoUsuario;
	private String contrasenha;
	private Persona persona;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String tipo;
	private ConfiguracionUoCab configuracionUoCab;
	private ConfiguracionUoDet configuracionUoDet;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String usuAltaOee;
	private Date fechaAltaOee;
	private int codigo_usuario_numero;
	private String token;
	private Long idDocumentoAlta;
	
	private Set<UsuarioRol> usuarioRols = new HashSet<UsuarioRol>(0);

	public Usuario() {
	}

	public Usuario(Long idUsuario, String codigoUsuario, String contrasenha,
			Persona persona, Boolean activo, String usuAlta, Date fechaAlta, Long idDocumentoAlta) {
		this.idUsuario = idUsuario;
		this.codigoUsuario = codigoUsuario;
		this.contrasenha = contrasenha;
		this.persona = persona;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.idDocumentoAlta = idDocumentoAlta;
	}

	public Usuario(Long idUsuario, String codigoUsuario, String contrasenha,
			Persona persona,Boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod, Long idDocumentoAlta, Set<UsuarioRol> usuarioRols) {
		this.idUsuario = idUsuario;
		this.codigoUsuario = codigoUsuario;
		this.contrasenha = contrasenha;
		this.persona = persona;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.usuarioRols = usuarioRols;
		this.idDocumentoAlta = idDocumentoAlta;
	}

	@Id
	@GeneratedValue(generator="USUARIO_GENERATOR")
	@SequenceGenerator(name="USUARIO_GENERATOR",sequenceName="seguridad.usuario_id_usuario_seq")
	@Column(name = "id_usuario", unique = true, nullable = false)
	public Long getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Column(name = "codigo_usuario", unique = true, nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getCodigoUsuario() {
		return this.codigoUsuario;
	}

	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

	@Column(name = "contrasenha", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getContrasenha() {
		return this.contrasenha;
	}

	public void setContrasenha(String contrasenha) {
		this.contrasenha = contrasenha;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	@NotNull
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return this.activo;
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
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return this.configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "usuario")
	public Set<UsuarioRol> getUsuarioRols() {
		return this.usuarioRols;
	}

	public void setUsuarioRols(Set<UsuarioRol> usuarioRols) {
		this.usuarioRols = usuarioRols;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo_det")
	public ConfiguracionUoDet getConfiguracionUoDet() {
		return this.configuracionUoDet;
	}

	public void setConfiguracionUoDet(ConfiguracionUoDet configuracionUoDet) {
		this.configuracionUoDet = configuracionUoDet;
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
	@JoinColumn(name = "id_concurso_puesto_agr")
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@Column(name = "usu_alta_oee", length = 50)
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

	//Columna nueva para el generar el c�digo de usuario

	@Column(name = "codigo_usuario_numero")
	public int getCodigo_usuario_numero() {
		return codigo_usuario_numero;
	}

	public void setCodigo_usuario_numero(int codigo_usuario_numero) {
		this.codigo_usuario_numero = codigo_usuario_numero;
	}

	@Transient
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	@Column(name = "id_documento")
	public Long getIdDocumentoAlta() {
		return this.idDocumentoAlta;
	}

	public void setIdDocumentoAlta(Long idDocumentoAlta) {
		this.idDocumentoAlta = idDocumentoAlta;
	}

}