package py.com.excelsis.sicca.entity;

// Generated 01/11/2011 11:13:13 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * AdjuntoDocConcurso generated by hbm2java
 */
@Entity
@Table(name = "adjunto_doc_concurso", schema = "seleccion")
public class AdjuntoDocConcurso implements java.io.Serializable {

	private Long idAdjuntoDocConcurso;
	private Concurso concurso;
	private Documentos documentos;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public AdjuntoDocConcurso() {
	}

	public AdjuntoDocConcurso(Long idAdjuntoDocConcurso, Concurso concurso,
			Documentos documentos, boolean activo, String usuAlta, Date fechaAlta) {
		this.idAdjuntoDocConcurso = idAdjuntoDocConcurso;
		this.concurso = concurso;
		this.documentos = documentos;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public AdjuntoDocConcurso(Long idAdjuntoDocConcurso, Concurso concurso,
			Documentos documentos, boolean activo, String usuAlta, Date fechaAlta,
			String usuMod, Date fechaMod) {
		this.idAdjuntoDocConcurso = idAdjuntoDocConcurso;
		this.concurso = concurso;
		this.documentos = documentos;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_adjunto_doc_concurso", unique = true, nullable = false)
	public Long getIdAdjuntoDocConcurso() {
		return this.idAdjuntoDocConcurso;
	}

	public void setIdAdjuntoDocConcurso(Long idAdjuntoDocConcurso) {
		this.idAdjuntoDocConcurso = idAdjuntoDocConcurso;
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

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento", nullable = false)
	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
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

}
