package py.com.excelsis.sicca.entity;

// Generated 18/10/2011 05:11:07 PM by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * MatrizDocumentalEnc generated by hbm2java
 */
@Entity
@Table(name = "matriz_documental_enc", schema = "seleccion")
public class MatrizDocumentalEnc implements java.io.Serializable {

	private Long idMatrizDocumentalEnc;
	private DatosEspecificos datosEspecificos;
	private String descripcion;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	
	private List<MatrizDocumentalDet> matrizDocumentalDets = new ArrayList<MatrizDocumentalDet>(
			0);

	public MatrizDocumentalEnc() {
	}

	public MatrizDocumentalEnc(Long idMatrizDocumentalEnc,
			DatosEspecificos datosEspecificos, String descripcion,
			Boolean activo, String usuAlta, Date fechaAlta) {
		this.idMatrizDocumentalEnc = idMatrizDocumentalEnc;
		this.datosEspecificos = datosEspecificos;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public MatrizDocumentalEnc(Long idMatrizDocumentalEnc,
			DatosEspecificos datosEspecificos, String descripcion,
			Boolean activo, String usuAlta, Date fechaAlta, String usuMod,
			Date fechaMod) {
		this.idMatrizDocumentalEnc = idMatrizDocumentalEnc;
		this.datosEspecificos = datosEspecificos;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_matriz_documental_enc", unique = true, nullable = false)
	@GeneratedValue(generator="MATRIZ_DOCUMENTAL_ENC_GENERATOR")
	@SequenceGenerator(name="MATRIZ_DOCUMENTAL_ENC_GENERATOR",sequenceName="seleccion.matriz_documental_enc_id_matriz_documental_enc_seq")
	public Long getIdMatrizDocumentalEnc() {
		return this.idMatrizDocumentalEnc;
	}

	public void setIdMatrizDocumentalEnc(Long idMatrizDocumentalEnc) {
		this.idMatrizDocumentalEnc = idMatrizDocumentalEnc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_tipo_conc", nullable = false)
	@NotNull
	public DatosEspecificos getDatosEspecificos() {
		return this.datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	@Column(name = "descripcion", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "matrizDocumentalEnc")
	public List<MatrizDocumentalDet> getMatrizDocumentalDets() {
		return this.matrizDocumentalDets;
	}

	public void setMatrizDocumentalDets(
			List<MatrizDocumentalDet> matrizDocumentalDets) {
		this.matrizDocumentalDets = matrizDocumentalDets;
	}
}
