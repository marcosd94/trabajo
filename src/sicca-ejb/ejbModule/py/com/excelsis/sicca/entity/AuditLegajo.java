package py.com.excelsis.sicca.entity;

// Generated 15-oct-2012 15:52:09 by Hibernate Tools 3.4.0.Beta1

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
 * AuditLegajo generated by hbm2java
 */
@Entity
@Table(name = "audit_legajo", schema = "legajo")
public class AuditLegajo implements java.io.Serializable {

	private Long auditLegajo;
	private Persona persona;
	private Integer estado;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<AuditLegajoObs> auditLegajoObses = new HashSet<AuditLegajoObs>(
			0);
	private Set<AuditLegajoDet> auditLegajoDets = new HashSet<AuditLegajoDet>(0);

	public AuditLegajo() {
	}

	public AuditLegajo(long auditLegajo, Persona persona, Integer estado,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.auditLegajo = auditLegajo;
		this.persona = persona;
		this.estado = estado;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	public AuditLegajo(Long auditLegajo, Persona persona, Integer estado,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			Set<AuditLegajoObs> auditLegajoObses,
			Set<AuditLegajoDet> auditLegajoDets) {
		this.auditLegajo = auditLegajo;
		this.persona = persona;
		this.estado = estado;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.auditLegajoObses = auditLegajoObses;
		this.auditLegajoDets = auditLegajoDets;
	}

	@Id
	@GeneratedValue(generator = "AUDIT_LEGAJO_GENERATOR")
	@SequenceGenerator(name = "AUDIT_LEGAJO_GENERATOR", sequenceName = "legajo.audit_legajo_audit_legajo_seq")
	@Column(name = "audit_legajo", unique = true, nullable = false)
	public Long getAuditLegajo() {
		return this.auditLegajo;
	}

	public void setAuditLegajo(Long auditLegajo) {
		this.auditLegajo = auditLegajo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Column(name = "estado", nullable = false)
	public Integer getEstado() {
		return this.estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
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

	@Column(name = "usu_mod", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod", nullable = false, length = 29)
	@NotNull
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "auditLegajo")
	public Set<AuditLegajoObs> getAuditLegajoObses() {
		return this.auditLegajoObses;
	}

	public void setAuditLegajoObses(Set<AuditLegajoObs> auditLegajoObses) {
		this.auditLegajoObses = auditLegajoObses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "auditLegajo")
	public Set<AuditLegajoDet> getAuditLegajoDets() {
		return this.auditLegajoDets;
	}

	public void setAuditLegajoDets(Set<AuditLegajoDet> auditLegajoDets) {
		this.auditLegajoDets = auditLegajoDets;
	}

}
