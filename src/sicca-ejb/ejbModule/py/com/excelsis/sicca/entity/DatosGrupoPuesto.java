package py.com.excelsis.sicca.entity;

// Generated 11-nov-2011 15:32:40 by Hibernate Tools 3.4.0.Beta1

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
 * DatosGrupoPuesto generated by hbm2java
 */
@Entity
@Table(name = "datos_grupo_puesto", schema = "seleccion")
public class DatosGrupoPuesto implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2231945453601930035L;
	private Long idDatosGrupoPuesto;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Boolean terna;
	private Boolean merito;
	private Integer porcMinimo;
	private Boolean porMinPorEvaluacion;
	private Boolean porMinFinEvaluacion;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Boolean preferenciaPersDiscapacidad;
	private String tituloAPublicar;
	private String observacionAPublicar;

	public DatosGrupoPuesto() {
	}
	
	
	@Id
	@Column(name = "id_datos_grupo_puesto", unique = true, nullable = false)
	@GeneratedValue(generator="datos_grupo_puesto")
	@SequenceGenerator(name="datos_grupo_puesto", sequenceName="seleccion.datos_grupo_puesto_id_datos_grupo_puesto_seq")
	public Long getIdDatosGrupoPuesto() {
		return this.idDatosGrupoPuesto;
	}

	public void setIdDatosGrupoPuesto(Long idDatosGrupoPuesto) {
		this.idDatosGrupoPuesto = idDatosGrupoPuesto;
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

	@Column(name = "terna")
	public Boolean getTerna() {
		return this.terna;
	}

	public void setTerna(Boolean terna) {
		this.terna = terna;
	}

	@Column(name = "merito")
	public Boolean getMerito() {
		return this.merito;
	}

	public void setMerito(Boolean merito) {
		this.merito = merito;
	}

	@Column(name = "porc_minimo", nullable = false)
	@NotNull
	public Integer getPorcMinimo() {
		return this.porcMinimo;
	}

	public void setPorcMinimo(Integer porcMinimo) {
		this.porcMinimo = porcMinimo;
	}

	@Column(name = "por_min_por_evaluacion")
	public Boolean getPorMinPorEvaluacion() {
		return this.porMinPorEvaluacion;
	}

	public void setPorMinPorEvaluacion(Boolean porMinPorEvaluacion) {
		this.porMinPorEvaluacion = porMinPorEvaluacion;
	}

	@Column(name = "por_min_fin_evaluacion")
	public Boolean getPorMinFinEvaluacion() {
		return this.porMinFinEvaluacion;
	}

	public void setPorMinFinEvaluacion(Boolean porMinFinEvaluacion) {
		this.porMinFinEvaluacion = porMinFinEvaluacion;
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


	@Column(name = "preferencia_pers_discapacidad", nullable = false)
	public Boolean getPreferenciaPersDiscapacidad() {
		return preferenciaPersDiscapacidad;
	}


	public void setPreferenciaPersDiscapacidad(Boolean preferenciaPersDiscapacidad) {
		this.preferenciaPersDiscapacidad = preferenciaPersDiscapacidad;
	}

	@Column(name = "titulo_a_publicar", length = 200)
	@Length(max = 200)
	public String getTituloAPublicar() {
		return tituloAPublicar;
	}


	public void setTituloAPublicar(String tituloAPublicar) {
		this.tituloAPublicar = tituloAPublicar;
	}


	@Column(name = "observacion_a_publicar", length = 1000)
	@Length(max = 1000)
	public String getObservacionAPublicar() {
		return observacionAPublicar;
	}


	public void setObservacionAPublicar(String observacionAPublicar) {
		this.observacionAPublicar = observacionAPublicar;
	}
	
	
	

}