package py.com.excelsis.sicca.entity;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import py.com.excelsis.sicca.entity.ContenidoFuncional.ValorNivelOrgComparator;

import com.sun.org.apache.xpath.internal.operations.Bool;

/**
 * RequisitoMinimoCpt generated by Generality
 */
@Entity
@Table(name = "requisito_minimo_cpt", schema = "planificacion")
public class RequisitoMinimoCpt implements java.io.Serializable {
	
	private Long idRequisitoMinimoCpt;
	private String descripcion;
	private Boolean activo;
	private Integer orden;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private List<EscalaReqMin> escalaReqMins = new ArrayList<EscalaReqMin>(0);
	private List<DetReqMin> detReqMins = new ArrayList<DetReqMin>(0);

	public RequisitoMinimoCpt() {
	}

	public RequisitoMinimoCpt(Long idRequisitoMinimoCpt, String descripcion,
			Boolean activo, Integer orden, String usuAlta, Date fechaAlta) {
		this.idRequisitoMinimoCpt = idRequisitoMinimoCpt;
		this.descripcion = descripcion;
		this.activo = activo;
		this.orden = orden;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public RequisitoMinimoCpt(Long idRequisitoMinimoCpt, String descripcion,
			Boolean activo, Integer orden, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod, List<EscalaReqMin> escalaReqMins,
			List<DetReqMin> detReqMins) {
		this.idRequisitoMinimoCpt = idRequisitoMinimoCpt;
		this.descripcion = descripcion;
		this.activo = activo;
		this.orden = orden;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.escalaReqMins = escalaReqMins;
		this.detReqMins = detReqMins;
	}

	@Id
	@GeneratedValue(generator="REQUISITO_MINIMO_CPT_GENERATOR")
	@SequenceGenerator(name="REQUISITO_MINIMO_CPT_GENERATOR",sequenceName="planificacion.requisito_minimo_cpt_id_requisito_minimo_cpt_seq")
	@Column(name = "id_requisito_minimo_cpt", unique = true, nullable = false)
	public Long getIdRequisitoMinimoCpt() {
		return this.idRequisitoMinimoCpt;
	}

	public void setIdRequisitoMinimoCpt(Long idRequisitoMinimoCpt) {
		this.idRequisitoMinimoCpt = idRequisitoMinimoCpt;
	}

	@Column(name = "descripcion", nullable = false, length = 250)
	@NotNull
	@Length(max = 250)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Column(name = "orden", nullable = false)
	public Integer getOrden() {
		return this.orden;
	}

	

	public void setOrden(Integer orden) {
		this.orden = orden;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "requisitoMinimoCpt")
	public List<EscalaReqMin> getEscalaReqMins() {
		return this.escalaReqMins;
	}

	public void setEscalaReqMins(List<EscalaReqMin> escalaReqMins) {
		this.escalaReqMins = escalaReqMins;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "requisitoMinimoCpt")
	public List<DetReqMin> getDetReqMins() {
		return this.detReqMins;
	}

	public void setDetReqMins(List<DetReqMin> detReqMins) {
		this.detReqMins = detReqMins;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idRequisitoMinimoCpt == null) ? 0 : idRequisitoMinimoCpt.hashCode());
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
		RequisitoMinimoCpt other = (RequisitoMinimoCpt) obj;
		if (idRequisitoMinimoCpt == null) {
			if (other.idRequisitoMinimoCpt != null)
				return false;
		} else if (!idRequisitoMinimoCpt.equals(other.idRequisitoMinimoCpt))
			return false;
		return true;
	}
	
	

	@Transient
	public List<EscalaReqMin> getEscalaReqMinsActivos() {
		if(this.escalaReqMins != null){
			List<EscalaReqMin> lista = new ArrayList<EscalaReqMin>();
			for (EscalaReqMin escalaReqMin : escalaReqMins){
				if (escalaReqMin.getActivo())
					lista.add(escalaReqMin);
			}
			this.escalaReqMins = lista;
			
			Collections.sort(this.escalaReqMins, new EscalaReqMinComparator());
		}
		return this.escalaReqMins;
	}
	

	public class EscalaReqMinComparator implements Comparator<EscalaReqMin>{
		public int compare(EscalaReqMin o1, EscalaReqMin o2) {
			if(o1.getDesde().compareTo(o2.getDesde()) == 0) {           
				return o1.getHasta().compareTo(o2.getHasta());
		    } else {
		    	return o1.getDesde().compareTo(o2.getDesde());
		    }       
	    }
	}
}
