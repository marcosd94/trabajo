package py.com.excelsis.sicca.entity;

// Generated 14/11/2011 04:49:57 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * PlantillaMemoHomologacion generated by hbm2java
 */
@Entity
@Table(name = "plantilla_memo_homologacion", schema = "seleccion")
public class PlantillaMemoHomologacion implements java.io.Serializable {

	private long idPlantillaMemoHomologacion;
	private String descripcion;
	private String titulo;
	private String a;
	private String de;
	private String ref;
	private String usuMod;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private Date fechaMod;
	private String texto;
	private Set<MemoHomologacion> memoHomologacions = new HashSet<MemoHomologacion>(
			0);

	public PlantillaMemoHomologacion() {
	}

	public PlantillaMemoHomologacion(long idPlantillaMemoHomologacion,
			String descripcion, String de, boolean activo, String usuAlta,
			Date fechaAlta) {
		this.idPlantillaMemoHomologacion = idPlantillaMemoHomologacion;
		this.descripcion = descripcion;
		this.de = de;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public PlantillaMemoHomologacion(long idPlantillaMemoHomologacion,
			String descripcion, String titulo, String a, String de, String ref,
			String usuMod, boolean activo, String usuAlta, Date fechaAlta,
			Date fechaMod, String texto, Set<MemoHomologacion> memoHomologacions) {
		this.idPlantillaMemoHomologacion = idPlantillaMemoHomologacion;
		this.descripcion = descripcion;
		this.titulo = titulo;
		this.a = a;
		this.de = de;
		this.ref = ref;
		this.usuMod = usuMod;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.fechaMod = fechaMod;
		this.texto = texto;
		this.memoHomologacions = memoHomologacions;
	}

	@Id
	@Column(name = "id_plantilla_memo_homologacion", unique = true, nullable = false)
	public long getIdPlantillaMemoHomologacion() {
		return this.idPlantillaMemoHomologacion;
	}

	public void setIdPlantillaMemoHomologacion(long idPlantillaMemoHomologacion) {
		this.idPlantillaMemoHomologacion = idPlantillaMemoHomologacion;
	}

	@Column(name = "descripcion", nullable = false, length = 150)
	@NotNull
	@Length(max = 150)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "titulo", length = 500)
	@Length(max = 500)
	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Column(name = "a", length = 500)
	@Length(max = 500)
	public String getA() {
		return this.a;
	}

	public void setA(String a) {
		this.a = a;
	}

	@Column(name = "de", nullable = false, length = 500)
	@NotNull
	@Length(max = 500)
	public String getDe() {
		return this.de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	@Column(name = "ref", length = 500)
	@Length(max = 500)
	public String getRef() {
		return this.ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@Column(name = "usu_mod", length = 50)
	@Length(max = 50)
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod", length = 29)
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Column(name = "texto", length = 2000)
	@Length(max = 2000)
	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "plantillaMemoHomologacion")
	public Set<MemoHomologacion> getMemoHomologacions() {
		return this.memoHomologacions;
	}

	public void setMemoHomologacions(Set<MemoHomologacion> memoHomologacions) {
		this.memoHomologacions = memoHomologacions;
	}

}
