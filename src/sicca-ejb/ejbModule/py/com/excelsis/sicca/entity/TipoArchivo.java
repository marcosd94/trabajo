package py.com.excelsis.sicca.entity;

// Generated 05/10/2011 11:41:21 AM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * TipoArchivo generated by hbm2java
 */
@Entity
@Table(name = "tipo_archivo", schema = "seleccion")
public class TipoArchivo implements java.io.Serializable {
	

	private Long idTipoArchivo;
	private String descripcion;
	private String extension;
	private Integer tamanho;
	private String unidMedida;
	private String mimetype;
	private boolean activo;
	private Date fechaAlta;
	private String usuAlta;
	private Date fechaMod;
	private String usuMod;
	private Integer unidMedidaByte;

	public TipoArchivo() {
	}

	public TipoArchivo(Long idTipoArchivo, String descripcion,
			String extension, Integer tamanho, String unidMedida, String mimetype,
			boolean activo, Date fechaAlta, String usuAlta) {
		this.idTipoArchivo = idTipoArchivo;
		this.descripcion = descripcion;
		this.extension = extension;
		this.tamanho = tamanho;
		this.unidMedida = unidMedida;
		this.mimetype = mimetype;
		this.activo = activo;
		this.fechaAlta = fechaAlta;
		this.usuAlta = usuAlta;
	}

	public TipoArchivo(Long idTipoArchivo, String descripcion,
			String extension, Integer tamanho, String unidMedida, String mimetype,
			boolean activo, Date fechaAlta, String usuAlta, Date fechaMod,
			String usuMod) {
		this.idTipoArchivo = idTipoArchivo;
		this.descripcion = descripcion;
		this.extension = extension;
		this.tamanho = tamanho;
		this.unidMedida = unidMedida;
		this.mimetype = mimetype;
		this.activo = activo;
		this.fechaAlta = fechaAlta;
		this.usuAlta = usuAlta;
		this.fechaMod = fechaMod;
		this.usuMod = usuMod;
	}
	@Id
	@Column(name = "id_tipo_archivo", unique = true, nullable = false)
	@GeneratedValue(generator="TIPO_ARCHIVO_GENERATOR")
	@SequenceGenerator(name="TIPO_ARCHIVO_GENERATOR",sequenceName="seleccion.tbl_tipo_archivo_id_tipo_archivo_seq")
	public Long getIdTipoArchivo() {
		return idTipoArchivo;
	}

	public void setIdTipoArchivo(Long idTipoArchivo) {
		this.idTipoArchivo = idTipoArchivo;
	}


	@Column(name = "descripcion", nullable = false)
	@NotNull
	@Length(max = 100)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "extension", nullable = false)
	@NotNull
	@Length(max = 10)
	public String getExtension() {
		return this.extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Column(name = "tamanho", nullable = false)
	public Integer getTamanho() {
		return this.tamanho;
	}

	public void setTamanho(Integer tamanho) {
		this.tamanho = tamanho;
	}

	@Column(name = "unid_medida", nullable = false)
	@NotNull
	@Length(max = 10)
	public String getUnidMedida() {
		return this.unidMedida;
	}

	public void setUnidMedida(String unidMedida) {
		this.unidMedida = unidMedida;
	}

	@Column(name = "mimetype", nullable = false)
	@NotNull
	@Length(max = 100)
	public String getMimetype() {
		return this.mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return this.activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta", nullable = false)
	@NotNull
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "usu_alta", nullable = false)
	@NotNull
	@Length(max = 50)
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Column(name = "usu_mod")
	@Length(max = 50)
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}

	@Column(name = "unid_medida_byte")
	public Integer getUnidMedidaByte() {
		return unidMedidaByte;
	}

	public void setUnidMedidaByte(Integer unidMedidaByte) {
		this.unidMedidaByte = unidMedidaByte;
	}
	
	
	
}
