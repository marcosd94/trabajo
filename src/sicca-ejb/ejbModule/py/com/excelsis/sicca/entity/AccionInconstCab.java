package py.com.excelsis.sicca.entity;

// Generated 23-feb-2012 8:44:13 by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
 * AccionInconstCab generated by hbm2java
 */
@Entity
@Table(name = "accion_inconst_cab", schema = "juridicos")
public class AccionInconstCab implements java.io.Serializable {

	private Long idAccionCab;
	private EmpleadoPuesto empleadoPuesto;
	private String estado;
	private String observacion;
	private String resultado;
	private Date fechaAlta;
	private String usuAlta;
	private Date fechaMod;
	private String usuMod;
	private Persona persona;
	private List<AccionInconstDet> accionInconstDets = new ArrayList<AccionInconstDet>(
			0);

	public AccionInconstCab() {
	}

	public AccionInconstCab(Long idAccionCab, EmpleadoPuesto empleadoPuesto,
			String estado, Date fechaAlta, String usuAlta) {
		this.idAccionCab = idAccionCab;
		this.empleadoPuesto = empleadoPuesto;
		this.estado = estado;
		this.fechaAlta = fechaAlta;
		this.usuAlta = usuAlta;
	}

	public AccionInconstCab(Long idAccionCab, EmpleadoPuesto empleadoPuesto,
			String estado, String observacion, String resultado,
			Date fechaAlta, String usuAlta, Date fechaMod, String usuMod,
			List<AccionInconstDet> accionInconstDets) {
		this.idAccionCab = idAccionCab;
		this.empleadoPuesto = empleadoPuesto;
		this.estado = estado;
		this.observacion = observacion;
		this.resultado = resultado;
		this.fechaAlta = fechaAlta;
		this.usuAlta = usuAlta;
		this.fechaMod = fechaMod;
		this.usuMod = usuMod;
		this.accionInconstDets = accionInconstDets;
	}

	@Id
	@Column(name = "id_accion_cab", unique = true, nullable = false)
	@GeneratedValue(generator="ACCION_INCONST_CAB_GENERATOR")
	@SequenceGenerator(name="ACCION_INCONST_CAB_GENERATOR", sequenceName="juridicos.accion_inconst_cab_id_accion_cab_seq")
	public Long getIdAccionCab() {
		return this.idAccionCab;
	}

	public void setIdAccionCab(Long idAccionCab) {
		this.idAccionCab = idAccionCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado_puesto", nullable = false)
	@NotNull
	public EmpleadoPuesto getEmpleadoPuesto() {
		return this.empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	@Column(name = "estado", nullable = false, length = 1)
	@NotNull
	@Length(max = 1)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "observacion", length = 500)
	@Length(max = 500)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "resultado", length = 500)
	@Length(max = 500)
	public String getResultado() {
		return this.resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
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
	@Column(name = "fecha_mod", length = 29)
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Column(name = "usu_mod", length = 50)
	@Length(max = 50)
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "accionInconstCab")
	public List<AccionInconstDet> getAccionInconstDets() {
		return this.accionInconstDets;
	}

	public void setAccionInconstDets(List<AccionInconstDet> accionInconstDets) {
		this.accionInconstDets = accionInconstDets;
	}

}
