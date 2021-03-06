package py.com.excelsis.sicca.entity;

// Generated 10/02/2012 08:43:06 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * PublicacionPortal generated by hbm2java
 */
@Entity
@Table(name = "publicacion_portal", schema = "seleccion")
public class PublicacionPortal implements java.io.Serializable {

	private long idPublicacionPortal;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Concurso concurso;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String texto;
	private Boolean observacion;
	

	public PublicacionPortal() {
	}

	public PublicacionPortal(long idPublicacionPortal,
			ConcursoPuestoAgr concursoPuestoAgr, Concurso concurso,
			boolean activo, String usuAlta, Date fechaAlta, String texto) {
		this.idPublicacionPortal = idPublicacionPortal;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.concurso = concurso;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.texto = texto;
	}

	public PublicacionPortal(long idPublicacionPortal,
			ConcursoPuestoAgr concursoPuestoAgr, Concurso concurso,
			boolean activo, String usuAlta, Date fechaAlta, String usuMod,
			Date fechaMod, String texto) {
		this.idPublicacionPortal = idPublicacionPortal;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.concurso = concurso;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.texto = texto;
	}

	@Id
	@GeneratedValue(generator="PUBLICACION_PORTAL_GENERATOR")
	@SequenceGenerator(name="PUBLICACION_PORTAL_GENERATOR",sequenceName="seleccion.publicacion_portal_id_publicacion_portal_seq")
	@Column(name = "id_publicacion_portal", unique = true, nullable = false)
	public long getIdPublicacionPortal() {
		return this.idPublicacionPortal;
	}

	public void setIdPublicacionPortal(long idPublicacionPortal) {
		this.idPublicacionPortal = idPublicacionPortal;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso", nullable = false)
	@NotNull
	public Concurso getConcurso() {
		return this.concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return this.activo;
	}

	public void setActivo(boolean activo) {
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

	@Column(name = "texto", nullable = false)
	@NotNull
	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	@Column(name = "observacion")
	public Boolean getObservacion() {
		return this.observacion;
	}

	public void setObservacion(Boolean observacion) {
		this.observacion = observacion;
	}

}
