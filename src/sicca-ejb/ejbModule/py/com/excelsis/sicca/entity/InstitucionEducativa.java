package py.com.excelsis.sicca.entity;

// Generated 07/10/2011 10:08:33 AM by Hibernate Tools 3.4.0.Beta1

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
 * InstitucionEducativa generated by hbm2java
 */
@Entity
@Table(name = "institucion_educativa", schema = "seleccion")
public class InstitucionEducativa implements java.io.Serializable {

	private Long idInstEducativa;
	private Pais pais;
	private String descripcion;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<EstudiosRealizados> estudiosRealizados = new HashSet<EstudiosRealizados>(0);
	private Set<EstudiosRealizadosLegajo> estudiosRealizadosLegajo = new HashSet<EstudiosRealizadosLegajo>(0);

	public InstitucionEducativa() {
	}

	public InstitucionEducativa(Long idInstEducativa, Pais pais,
			String descripcion, Boolean activo, String usuAlta, Date fechaAlta) {
		this.idInstEducativa = idInstEducativa;
		this.pais = pais;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public InstitucionEducativa(Long idInstEducativa, Pais pais,
			String descripcion, Boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod) {
		this.idInstEducativa = idInstEducativa;
		this.pais = pais;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_inst_educativa", unique = true, nullable = false)
	@GeneratedValue(generator="INSTITUCION_EDUCATIVA_GENERATOR")
	@SequenceGenerator(name="INSTITUCION_EDUCATIVA_GENERATOR",sequenceName="seleccion.institucion_educativa_id_inst_educativa_seq")
	public Long getIdInstEducativa() {
		return this.idInstEducativa;
	}

	public void setIdInstEducativa(Long idInstEducativa) {
		this.idInstEducativa = idInstEducativa;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", nullable = false)
	@NotNull
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@Column(name = "descripcion", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "institucionEducativa")
	public Set<EstudiosRealizados> getEstudiosRealizados() {
		return estudiosRealizados;
	}

	public void setEstudiosRealizados(Set<EstudiosRealizados> estudiosRealizados) {
		this.estudiosRealizados = estudiosRealizados;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "institucionEducativa")
	public Set<EstudiosRealizadosLegajo> getEstudiosRealizadosLegajo() {
		return estudiosRealizadosLegajo;
	}

	public void setEstudiosRealizadosLegajo(Set<EstudiosRealizadosLegajo> estudiosRealizadosLegajo) {
		this.estudiosRealizadosLegajo = estudiosRealizadosLegajo;
	}
	
	
}