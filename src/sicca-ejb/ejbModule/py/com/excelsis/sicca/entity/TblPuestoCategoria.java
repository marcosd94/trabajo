package py.com.excelsis.sicca.entity;


import java.math.BigDecimal;
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
 * TblPuestoCategoria generated by Generality
 */
@Entity
@Table(name = "tbl_puesto_categoria", schema = "planificacion")
public class TblPuestoCategoria implements java.io.Serializable {
	private Integer idPuestoCategoria;
	private String codigoCategoria;
	private String descripcion;
	private Integer estado;
	private Integer usuAlta;
	private Integer usuMod;
	private Date fechaMod;
	private Date fechaAlta;
	private Integer activo;

	public TblPuestoCategoria() {
	}

	public TblPuestoCategoria(Integer idPuestoCategoria) {
		this.idPuestoCategoria = idPuestoCategoria;
	}

	@Id
	@GeneratedValue(generator="TBL_PUESTO_CATEGORIA_GENERATOR")
	@SequenceGenerator(name="TBL_PUESTO_CATEGORIA_GENERATOR",sequenceName="planificacion.tbl_puesto_categoria_id_puesto_categoria_seq")

	@Column(name = "id_puesto_categoria")
	public Integer getIdPuestoCategoria() {
		return this.idPuestoCategoria;
	}

	public void setIdPuestoCategoria(Integer idPuestoCategoria) {
		this.idPuestoCategoria = idPuestoCategoria;
	}
	

	@Column(name = "codigo_categoria")
	public String getCodigoCategoria() {
		return this.codigoCategoria;
	}

	public void setCodigoCategoria(String codigoCategoria) {
		this.codigoCategoria = codigoCategoria;
	}
	

	@Column(name = "descripcion")
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	

	@Column(name = "estado")
	public Integer getEstado() {
		return this.estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	

	@Column(name = "usu_alta")
	public Integer getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(Integer usuAlta) {
		this.usuAlta = usuAlta;
	}
	

	@Column(name = "usu_mod")
	public Integer getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(Integer usuMod) {
		this.usuMod = usuMod;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	

	@Column(name = "activo")
	public Integer getActivo() {
		return this.activo;
	}

	public void setActivo(Integer activo) {
		this.activo = activo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPuestoCategoria == null) ? 0 : idPuestoCategoria.hashCode());
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
		TblPuestoCategoria other = (TblPuestoCategoria) obj;
		if (idPuestoCategoria == null) {
			if (other.idPuestoCategoria != null)
				return false;
		} else if (!idPuestoCategoria.equals(other.idPuestoCategoria))
			return false;
		return true;
	}
}
