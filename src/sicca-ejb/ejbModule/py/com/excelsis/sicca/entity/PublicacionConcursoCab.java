package py.com.excelsis.sicca.entity;

// Generated 16-nov-2011 8:26:22 by Hibernate Tools 3.4.0.Beta1

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
 * PublicacionConcursoCab generated by hbm2java
 */
@Entity
@Table(name = "publicacion_concurso_cab", schema = "seleccion")
public class PublicacionConcursoCab implements java.io.Serializable {

	private Long idPublicacionConc;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Date fechaSolicitud;
	private String usuSolicitud;
	private Date fechaAprobacion;
	private String usuAprobacion;
	private Date fechaPublicacion;
	private String usuPublicacion;
	private PublicacionConcurso publicacionConcurso;
	private Set<PublicacionConcursoDet> publicacionConcursoDets = new HashSet<PublicacionConcursoDet>(
			0);

	public PublicacionConcursoCab() {
	}

	public PublicacionConcursoCab(Long idPublicacionConc,
			ConcursoPuestoAgr concursoPuestoAgr, Date fechaSolicitud,
			String usuSolicitud) {
		this.idPublicacionConc = idPublicacionConc;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.fechaSolicitud = fechaSolicitud;
		this.usuSolicitud = usuSolicitud;
	}

	public PublicacionConcursoCab(Long idPublicacionConc,
			ConcursoPuestoAgr concursoPuestoAgr, Date fechaSolicitud,
			String usuSolicitud, Date fechaAprobacion, String usuAprobacion,
			Date fechaPublicacion, String usuPublicacion,
			Set<PublicacionConcursoDet> publicacionConcursoDets) {
		this.idPublicacionConc = idPublicacionConc;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.fechaSolicitud = fechaSolicitud;
		this.usuSolicitud = usuSolicitud;
		this.fechaAprobacion = fechaAprobacion;
		this.usuAprobacion = usuAprobacion;
		this.fechaPublicacion = fechaPublicacion;
		this.usuPublicacion = usuPublicacion;
		this.publicacionConcursoDets = publicacionConcursoDets;
	}

	@Id
	@GeneratedValue(generator="PUBLICACION_CONCURSO_CAB_GENERATOR")
	@SequenceGenerator(name="PUBLICACION_CONCURSO_CAB_GENERATOR",sequenceName="seleccion.publicacion_concurso_cab_id_publicacion_conc_seq")
	@Column(name = "id_publicacion_conc", unique = true, nullable = false)
	public Long getIdPublicacionConc() {
		return this.idPublicacionConc;
	}

	public void setIdPublicacionConc(Long idPublicacionConc) {
		this.idPublicacionConc = idPublicacionConc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr", nullable = false)
	@NotNull
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return this.concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_solicitud", nullable = false, length = 29)
	@NotNull
	public Date getFechaSolicitud() {
		return this.fechaSolicitud;
	}

	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	@Column(name = "usu_solicitud", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getUsuSolicitud() {
		return this.usuSolicitud;
	}

	public void setUsuSolicitud(String usuSolicitud) {
		this.usuSolicitud = usuSolicitud;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_aprobacion", length = 29)
	public Date getFechaAprobacion() {
		return this.fechaAprobacion;
	}

	public void setFechaAprobacion(Date fechaAprobacion) {
		this.fechaAprobacion = fechaAprobacion;
	}

	@Column(name = "usu_aprobacion", length = 50)
	@Length(max = 50)
	public String getUsuAprobacion() {
		return this.usuAprobacion;
	}

	public void setUsuAprobacion(String usuAprobacion) {
		this.usuAprobacion = usuAprobacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_publicacion", length = 29)
	public Date getFechaPublicacion() {
		return this.fechaPublicacion;
	}

	public void setFechaPublicacion(Date fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	@Column(name = "usu_publicacion", length = 50)
	@Length(max = 50)
	public String getUsuPublicacion() {
		return this.usuPublicacion;
	}

	public void setUsuPublicacion(String usuPublicacion) {
		this.usuPublicacion = usuPublicacion;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_publicacion_concurso")
	public PublicacionConcurso getPublicacionConcurso() {
		return publicacionConcurso;
	}

	public void setPublicacionConcurso(PublicacionConcurso publicacionConcurso) {
		this.publicacionConcurso = publicacionConcurso;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "publicacionConcursoCab")
	public Set<PublicacionConcursoDet> getPublicacionConcursoDets() {
		return this.publicacionConcursoDets;
	}

	public void setPublicacionConcursoDets(
			Set<PublicacionConcursoDet> publicacionConcursoDets) {
		this.publicacionConcursoDets = publicacionConcursoDets;
	}

}
