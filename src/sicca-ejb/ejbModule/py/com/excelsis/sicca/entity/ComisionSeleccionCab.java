package py.com.excelsis.sicca.entity;

// Generated 11/11/2011 02:29:19 PM by Hibernate Tools 3.4.0.Beta1

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
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * ComisionSeleccionCab generated by hbm2java
 */
@Entity
@Table(name = "comision_seleccion_cab", schema = "seleccion")
public class ComisionSeleccionCab implements java.io.Serializable {

	private Long idComisionSel;
	private Concurso concurso;
	private String descripcion;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	Documentos documentos;
	private Long idDocumento;

	private Set<ComisionSeleccionDet> comisionSeleccionDets = new HashSet<ComisionSeleccionDet>(0);
	private Set<Excepcion> excepcions = new HashSet<Excepcion>(0);
	private Boolean seleccionado;

	public ComisionSeleccionCab() {
	}

	public ComisionSeleccionCab(
			Long idComisionSel, Concurso concurso, String descripcion, String usuAlta,
			Date fechaAlta) {
		this.idComisionSel = idComisionSel;
		this.concurso = concurso;
		this.descripcion = descripcion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ComisionSeleccionCab(
			Long idComisionSel, Concurso concurso, String descripcion, String usuAlta,
			Date fechaAlta, String usuMod, Date fechaMod,
			Set<ComisionSeleccionDet> comisionSeleccionDets) {
		this.idComisionSel = idComisionSel;
		this.concurso = concurso;
		this.descripcion = descripcion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.comisionSeleccionDets = comisionSeleccionDets;
	}

	@Id
	@Column(name = "id_comision_sel", unique = true, nullable = false)
	@GeneratedValue(generator = "COMISION_SELE_CAB_GENERATOR")
	@SequenceGenerator(name = "COMISION_SELE_CAB_GENERATOR", sequenceName = "seleccion.comision_seleccion_cab_id_comision_sel_seq")
	public Long getIdComisionSel() {
		return this.idComisionSel;
	}

	public void setIdComisionSel(Long idComisionSel) {
		this.idComisionSel = idComisionSel;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso", nullable = false)
	@NotNull
	public Concurso getConcurso() {
		return this.concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	@Column(name = "descripcion", nullable = false, length = 256)
	@NotNull
	@Length(max = 256)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "comisionSeleccionCab")
	public Set<ComisionSeleccionDet> getComisionSeleccionDets() {
		return this.comisionSeleccionDets;
	}

	public void setComisionSeleccionDets(Set<ComisionSeleccionDet> comisionSeleccionDets) {
		this.comisionSeleccionDets = comisionSeleccionDets;
	}

	@Transient
	public Boolean getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	//@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento")
	public Documentos getDocumentos() {
		return documentos;
	}
	
	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}
	@Column(name = "id_documento")
	public Long getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "comisionSeleccionCab")
	public Set<Excepcion> getExcepcions() {
		return this.excepcions;
	}

	public void setExcepcions(Set<Excepcion> excepcions) {
		this.excepcions = excepcions;
	}

}
