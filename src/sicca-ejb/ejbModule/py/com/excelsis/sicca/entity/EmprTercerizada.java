package py.com.excelsis.sicca.entity;

// Generated 13/10/2011 09:48:59 AM by Hibernate Tools 3.4.0.Beta1

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;


/**
 * EmprTercerizada generated by hbm2java
 */
@Entity
@Table(name = "empr_tercerizada", schema = "seleccion")
public class EmprTercerizada implements java.io.Serializable {

	private Long idEmpresaTercerizada;
	private Ciudad ciudad;
	private String nombre;
	private String direccion;
	private String telefono;
	private String ruc;
	private String EMail;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<EmpresaPersona> empresaPersonas = new HashSet<EmpresaPersona>(0);

	public EmprTercerizada() {
	}

	public EmprTercerizada(Long idEmpresaTercerizada, Ciudad ciudad,
			String nombre, String direccion, String telefono, String ruc,
			String EMail, Boolean activo, String usuAlta, Date fechaAlta) {
		this.idEmpresaTercerizada = idEmpresaTercerizada;
		this.ciudad = ciudad;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.ruc = ruc;
		this.EMail = EMail;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public EmprTercerizada(Long idEmpresaTercerizada, Ciudad ciudad,
			String nombre, String direccion, String telefono, String ruc,
			String EMail, Boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod, Set<EmpresaPersona> empresaPersonas) {
		this.idEmpresaTercerizada = idEmpresaTercerizada;
		this.ciudad = ciudad;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.ruc = ruc;
		this.EMail = EMail;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.empresaPersonas = empresaPersonas;
	}

	@Id
	@Column(name = "id_empresa_tercerizada", unique = true, nullable = false)
	@GeneratedValue(generator="EMPR_TERCERIZADA_GENERATOR")
	@SequenceGenerator(name="EMPR_TERCERIZADA_GENERATOR",sequenceName="seleccion.empr_tercerizada_id_empresa_tercerizada_seq")
	public Long getIdEmpresaTercerizada() {
		return this.idEmpresaTercerizada;
	}

	public void setIdEmpresaTercerizada(Long idEmpresaTercerizada) {
		this.idEmpresaTercerizada = idEmpresaTercerizada;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ciudad", nullable = false)
	@NotNull
	public Ciudad getCiudad() {
		return this.ciudad;
	}

	public void setCiudad(Ciudad ciudad) {
		this.ciudad = ciudad;
	}

	@Column(name = "nombre", nullable = false, length = 150)
	@NotNull
	@Length(max = 150)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "direccion", nullable = false, length = 200)
	@NotNull
	@Length(max = 200)
	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@Column(name = "telefono", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Column(name = "ruc", nullable = false, length = 30)
	@NotNull
	@Length(max = 30)
	public String getRuc() {
		return this.ruc;
	}

	public void setRuc(String ruc) {
		this.ruc = ruc;
	}

	@Column(name = "e_mail",  length = 50)
	@Length(max = 50)
	public String getEMail() {
		return this.EMail;
	}

	public void setEMail(String EMail) {
		this.EMail = EMail;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "emprTercerizada")
	public Set<EmpresaPersona> getEmpresaPersonas() {
		return this.empresaPersonas;
	}

	public void setEmpresaPersonas(Set<EmpresaPersona> empresaPersonas) {
		this.empresaPersonas = empresaPersonas;
	}

}
