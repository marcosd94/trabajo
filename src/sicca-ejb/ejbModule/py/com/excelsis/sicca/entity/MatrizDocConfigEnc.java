package py.com.excelsis.sicca.entity;

// Generated 08/11/2011 01:56:15 PM by Hibernate Tools 3.4.0.Beta1

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
 * MatrizDocConfigEnc generated by hbm2java
 */
@Entity
@Table(name = "matriz_doc_config_enc", schema = "seleccion")
public class MatrizDocConfigEnc implements java.io.Serializable {

	private long idMatrizDocConfigEnc;
	private DatosEspecificos datosEspecificos;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Set<MatrizDocConfigDet> matrizDocConfigDets = new HashSet<MatrizDocConfigDet>(
			0);

	public MatrizDocConfigEnc() {
	}

	public MatrizDocConfigEnc(long idMatrizDocConfigEnc,
			DatosEspecificos datosEspecificos,
			ConcursoPuestoAgr concursoPuestoAgr, boolean activo,
			String usuAlta, Date fechaAlta) {
		this.idMatrizDocConfigEnc = idMatrizDocConfigEnc;
		this.datosEspecificos = datosEspecificos;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public MatrizDocConfigEnc(long idMatrizDocConfigEnc,
			DatosEspecificos datosEspecificos,
			ConcursoPuestoAgr concursoPuestoAgr, boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			Set<MatrizDocConfigDet> matrizDocConfigDets) {
		this.idMatrizDocConfigEnc = idMatrizDocConfigEnc;
		this.datosEspecificos = datosEspecificos;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.matrizDocConfigDets = matrizDocConfigDets;
	}

	@Id
	@Column(name = "id_matriz_doc_config_enc", unique = true, nullable = false)
	@GeneratedValue(generator="MATRIZ_CONFIG_DOC_ENC_GENERATOR")
	@SequenceGenerator(name="MATRIZ_CONFIG_DOC_ENC_GENERATOR",sequenceName="seleccion.matriz_doc_config_enc_id_matriz_documental_config_seq")
	public long getIdMatrizDocConfigEnc() {
		return this.idMatrizDocConfigEnc;
	}

	public void setIdMatrizDocConfigEnc(long idMatrizDocConfigEnc) {
		this.idMatrizDocConfigEnc = idMatrizDocConfigEnc;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr", nullable = false)
	@NotNull
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return this.concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "matrizDocConfigEnc")
	public Set<MatrizDocConfigDet> getMatrizDocConfigDets() {
		return this.matrizDocConfigDets;
	}

	public void setMatrizDocConfigDets(
			Set<MatrizDocConfigDet> matrizDocConfigDets) {
		this.matrizDocConfigDets = matrizDocConfigDets;
	}

}
