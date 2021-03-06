package py.com.excelsis.sicca.entity;

// Generated 02/05/2012 10:03:12 AM by Hibernate Tools 3.4.0.Beta1

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.Length;

/**
 * VwEvalRefPostu generated by hbm2java
 */
@Entity
@Table(name = "vw_eval_ref_postu", schema = "seleccion")
public class VwEvalRefPostu implements java.io.Serializable {

	private String nombre;
	private String grupo;
	private String estado;
	private Date fechaVenc;
	private Long idGrupo;
	private String denominacion;
	private BigDecimal nenCodigo;
	private BigDecimal entCodigo;
	private BigDecimal aniAniopre;

	public VwEvalRefPostu() {
	}

	public VwEvalRefPostu(String nombre, String grupo, String estado,
			Date fechaVenc, Long idGrupo, String denominacion) {
		this.nombre = nombre;
		this.grupo = grupo;
		this.estado = estado;
		this.fechaVenc = fechaVenc;
		this.idGrupo = idGrupo;
		this.denominacion = denominacion;
	}

	@Column(name = "nombre", length = 100)
	@Length(max = 100)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "grupo", length = 100)
	@Length(max = 100)
	public String getGrupo() {
		return this.grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	

	@Column(name = "estado", length = 60)
	@Length(max = 60)
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "fecha_venc", length = 29)
	public Date getFechaVenc() {
		return this.fechaVenc;
	}

	public void setFechaVenc(Date fechaVenc) {
		this.fechaVenc = fechaVenc;
	}

	@Id
	@Column(name = "id_grupo")
	public Long getIdGrupo() {
		return this.idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	@Column(name = "denominacion", length = 100)
	@Length(max = 100)
	public String getDenominacion() {
		return this.denominacion;
	}

	public void setDenominacion(String denominacion) {
		this.denominacion = denominacion;
	}


	@Column(name = "nen_codigo")
	public BigDecimal getNenCodigo() {
		return this.nenCodigo;
	}

	public void setNenCodigo(BigDecimal nenCodigo) {
		this.nenCodigo = nenCodigo;
	}
	

	@Column(name = "ent_codigo")
	public BigDecimal getEntCodigo() {
		return this.entCodigo;
	}

	public void setEntCodigo(BigDecimal entCodigo) {
		this.entCodigo = entCodigo;
	}
	@Column(name = "ani_aniopre")
	public BigDecimal getAniAniopre() {
		return this.aniAniopre;
	}

	public void setAniAniopre(BigDecimal aniAniopre) {
		this.aniAniopre = aniAniopre;
	}
	
	

}
