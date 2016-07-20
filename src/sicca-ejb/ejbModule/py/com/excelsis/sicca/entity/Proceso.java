package py.com.excelsis.sicca.entity;

// Generated 03-nov-2011 17:17:28 by Hibernate Tools 3.4.0.Beta1

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * Proceso generated by hbm2java
 */
@Entity
@Table(name = "proceso", schema = "proceso")
public class Proceso extends EntityBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4186619820856468538L;
	private long idProceso;
	private String descripcion;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<ActividadProceso> actividadProcesos = new HashSet<ActividadProceso>(
			0);

	public Proceso() {
	}

	public Proceso(long idProceso, String descripcion, boolean activo,
			String usuAlta, Date fechaAlta) {
		this.idProceso = idProceso;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public Proceso(long idProceso, String descripcion, boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			Set<ActividadProceso> actividadProcesos) {
		this.idProceso = idProceso;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.actividadProcesos = actividadProcesos;
	}

	@Id
	@Column(name = "id_proceso", unique = true, nullable = false)
	@GeneratedValue(generator="PROCESO_GENERATOR")
	@SequenceGenerator(name="PROCESO_GENERATOR", sequenceName="proceso.tbl_proceso_id_proceso_seq")
	public long getIdProceso() {
		return this.idProceso;
	}

	public void setIdProceso(long idProceso) {
		this.idProceso = idProceso;
	}

	@Column(name = "descripcion", nullable = false, length = 200)
	@NotNull
	@Length(max = 200)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "proceso")
	public Set<ActividadProceso> getActividadProcesos() {
		return this.actividadProcesos;
	}

	public void setActividadProcesos(Set<ActividadProceso> actividadProcesos) {
		this.actividadProcesos = actividadProcesos;
	}

	@Transient
	public Long getId() {
		return idProceso;
	}

	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.PROCESO_ID, getId());
		properties.put(SICCASessionParameters.PROCESO_DESCRIPCION,
				descripcion);

		return properties;
	}

	/**
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}

}
