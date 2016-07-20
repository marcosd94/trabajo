package py.com.excelsis.sicca.entity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * CondicionSegur generated by Generality
 */
@Entity
@Table(name = "condicion_segur", schema = "planificacion")
public class CondicionSegur implements java.io.Serializable {
	private Long idCondicionSegur;
	private String descripcion;
	private Boolean activo;
	private Integer orden;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private List<DetCondicionSegur> detCondicionSegurs = new ArrayList<DetCondicionSegur>(
			0);

	public CondicionSegur() {
	}
	public CondicionSegur(Long idCondicionSegur, String descripcion,
			Boolean activo, Integer orden, String usuAlta, Date fechaAlta) {
		this.idCondicionSegur = idCondicionSegur;
		this.descripcion = descripcion;
		this.activo = activo;
		this.orden = orden;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public CondicionSegur(Long idCondicionSegur, String descripcion,
			Boolean activo, Integer orden, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod,
			List<DetCondicionSegur> detCondicionSegurs) {
		this.idCondicionSegur = idCondicionSegur;
		this.descripcion = descripcion;
		this.activo = activo;
		this.orden = orden;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.detCondicionSegurs = detCondicionSegurs;
	}

	@Id
	@GeneratedValue(generator="CONDICION_SEGUR_GENERATOR")
	@SequenceGenerator(name="CONDICION_SEGUR_GENERATOR",sequenceName="planificacion.condicion_segur_id_condicion_segur_seq")

	@Column(name = "id_condicion_segur")
	public Long getIdCondicionSegur() {
		return this.idCondicionSegur;
	}

	public void setIdCondicionSegur(Long idCondicionSegur) {
		this.idCondicionSegur = idCondicionSegur;
	}
	

	@Column(name = "descripcion")
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	

	@Column(name = "orden")
	public Integer getOrden() {
		return this.orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	

	@Column(name = "usu_alta")
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	

	@Column(name = "usu_mod")
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "condicionSegur")
	public List<DetCondicionSegur> getDetCondicionSegurs() {
		return this.detCondicionSegurs;
	}

	public void setDetCondicionSegurs(List<DetCondicionSegur> detCondicionSegurs) {
		this.detCondicionSegurs = detCondicionSegurs;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCondicionSegur == null) ? 0 : idCondicionSegur.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CondicionSegur other = (CondicionSegur) obj;
		if (idCondicionSegur == null) {
			if (other.idCondicionSegur != null)
				return false;
		} else if (!idCondicionSegur.equals(other.idCondicionSegur))
			return false;
		return true;
	}
	
	@Transient
	public Long getId() {
		return idCondicionSegur;
	}

	
	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.CONDICION_SEGUR_ID, getId());
		properties.put(SICCASessionParameters.CONDICION_SEGUR_DESCRIPCION,
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
