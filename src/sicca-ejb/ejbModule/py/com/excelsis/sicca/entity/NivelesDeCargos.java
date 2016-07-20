


package py.com.excelsis.sicca.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.Length;


/**
* Niveles de Cargo posible
*/
@SuppressWarnings("serial")
@Entity
@Table(name = "niveles_de_cargos", schema = "planificacion")
public class NivelesDeCargos implements java.io.Serializable {

	private Long idNivelesDeCargo;
	private String tipo;
	private String descripcion;
	private Boolean activo;
	private String usuarioAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	
	
	public NivelesDeCargos() {
	}

	public NivelesDeCargos(Long idNivelesDeCargo, String descripcion, String tipo, Boolean activo) {
		this.idNivelesDeCargo = idNivelesDeCargo;
		this.descripcion = descripcion;
		this.tipo = tipo;
		this.activo = activo;
	}

	@Id
	@Column(name = "id_niveles_de_cargos", unique = true, nullable = false)
	@GeneratedValue(generator="NIVELES_DE_CARGOS_GENERATOR")
	@SequenceGenerator(name="NIVELES_DE_CARGOS_GENERATOR",sequenceName="planificacion.niveles_de_cargos_id_niveles_de_cargos_seq")
	public Long getIdNivelesDeCargo() {
		return this.idNivelesDeCargo;
	}

	public void setIdNivelesDeCargo(Long idNivelesDeCargo) {
		this.idNivelesDeCargo = idNivelesDeCargo;
	}

	@Column(name = "descripcion", length = 250)
	@Length(max = 250)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	@Column(name = "tipo", length = 1)
	@Length(max = 1)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	@Column(name = "usu_alta", length = 50)
	@Length(max = 50)
	public String getUsuarioAlta() {
		return usuarioAlta;
	}

	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	@Column(name = "usu_mod", length = 50)
	@Length(max = 50)
	public String getUsuMod() {
		return usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}

	

}
