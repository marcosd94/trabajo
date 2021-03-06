package py.com.excelsis.sicca.entity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import org.hibernate.validator.Length;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

/**
 * CondicionTrabajoEspecif generated by Generality
 */
@Entity
@Table(name = "condicion_trabajo_especif", schema = "planificacion")
public class CondicionTrabajoEspecif implements java.io.Serializable {
	private Long idCondicionesTrabajoEspecif;
	private String tipo;
	private String descripcion;
	private Boolean activo;
	private Integer orden;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private List<DetCondicionTrabajoEspecif> detCondicionTrabajoEspecifs = new ArrayList<DetCondicionTrabajoEspecif>(
			0);

	public CondicionTrabajoEspecif() {
	}

	public CondicionTrabajoEspecif(Long idCondicionesTrabajoEspecif,
			String tipo, String descripcion, Boolean activo, Integer orden,
			String usuAlta, Date fechaAlta) {
		this.idCondicionesTrabajoEspecif = idCondicionesTrabajoEspecif;
		this.tipo = tipo;
		this.descripcion = descripcion;
		this.activo = activo;
		this.orden = orden;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public CondicionTrabajoEspecif(Long idCondicionesTrabajoEspecif,
			String tipo, String descripcion, Boolean activo, Integer orden,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			List<DetCondicionTrabajoEspecif> detCondicionTrabajoEspecifs) {
		this.idCondicionesTrabajoEspecif = idCondicionesTrabajoEspecif;
		this.tipo = tipo;
		this.descripcion = descripcion;
		this.activo = activo;
		this.orden = orden;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.detCondicionTrabajoEspecifs = detCondicionTrabajoEspecifs;
	}


	@Id
	@GeneratedValue(generator="CONDICION_TRABAJO_ESPECIF_GENERATOR")
	@SequenceGenerator(name="CONDICION_TRABAJO_ESPECIF_GENERATOR",sequenceName="planificacion.condicion_trabajo_especif_id_condiciones_trabajo_especif_seq")

	@Column(name = "id_condiciones_trabajo_especif")
	public Long getIdCondicionesTrabajoEspecif() {
		return this.idCondicionesTrabajoEspecif;
	}

	public void setIdCondicionesTrabajoEspecif(Long idCondicionesTrabajoEspecif) {
		this.idCondicionesTrabajoEspecif = idCondicionesTrabajoEspecif;
	}
	

	@Column(name = "tipo")
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "condicionTrabajoEspecif")
	public List<DetCondicionTrabajoEspecif> getDetCondicionTrabajoEspecifs() {
		return this.detCondicionTrabajoEspecifs;
	}

	public void setDetCondicionTrabajoEspecifs(
			List<DetCondicionTrabajoEspecif> detCondicionTrabajoEspecifs) {
		this.detCondicionTrabajoEspecifs = detCondicionTrabajoEspecifs;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCondicionesTrabajoEspecif == null) ? 0 : idCondicionesTrabajoEspecif.hashCode());
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
		CondicionTrabajoEspecif other = (CondicionTrabajoEspecif) obj;
		if (idCondicionesTrabajoEspecif == null) {
			if (other.idCondicionesTrabajoEspecif != null)
				return false;
		} else if (!idCondicionesTrabajoEspecif.equals(other.idCondicionesTrabajoEspecif))
			return false;
		return true;
	}
}
