package py.com.excelsis.sicca.entity;

// Generated 30/08/2012 01:05:43 PM by Hibernate Tools 3.4.0.Beta1

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
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * ListaElegiblesDet generated by hbm2java
 */
@Entity
@Table(name = "lista_elegibles_det", schema = "seleccion")
public class ListaElegiblesDet implements java.io.Serializable {

	private long idListaElegiblesDet;
	private DatosEspecificos datosEspecificos;
	private Postulacion postulacion;
	private ListaElegiblesCab listaElegiblesCab;
	private Boolean seleccionado;
	private boolean disponible;
	private String obsEstado;
	private Long idDocumento;
	private String usuAlta;
	private Date fechaAlta;
	// transient
	private Boolean incluir = false;

	public ListaElegiblesDet() {
	}

	public ListaElegiblesDet(
			long idListaElegiblesDet, Postulacion postulacion, ListaElegiblesCab listaElegiblesCab,
			boolean disponible, String usuAlta, Date fechaAlta) {
		this.idListaElegiblesDet = idListaElegiblesDet;
		this.postulacion = postulacion;
		this.listaElegiblesCab = listaElegiblesCab;
		this.disponible = disponible;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ListaElegiblesDet(
			long idListaElegiblesDet, DatosEspecificos datosEspecificos, Postulacion postulacion,
			ListaElegiblesCab listaElegiblesCab, Boolean seleccionado, boolean disponible,
			String obsEstado, Long idDocumento, String usuAlta, Date fechaAlta) {
		this.idListaElegiblesDet = idListaElegiblesDet;
		this.datosEspecificos = datosEspecificos;
		this.postulacion = postulacion;
		this.listaElegiblesCab = listaElegiblesCab;
		this.seleccionado = seleccionado;
		this.disponible = disponible;
		this.obsEstado = obsEstado;
		this.idDocumento = idDocumento;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	@Id
	@GeneratedValue(generator = "LISTA_ELEGIBLES_DET_GENERATOR")
	@SequenceGenerator(name = "LISTA_ELEGIBLES_DET_GENERATOR", sequenceName = "seleccion.lista_elegibles_det_seq")
	@Column(name = "id_lista_elegibles_det", unique = true, nullable = false)
	public long getIdListaElegiblesDet() {
		return this.idListaElegiblesDet;
	}

	public void setIdListaElegiblesDet(long idListaElegiblesDet) {
		this.idListaElegiblesDet = idListaElegiblesDet;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_estado_elegible")
	public DatosEspecificos getDatosEspecificos() {
		return this.datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_postulacion", nullable = false)
	@NotNull
	public Postulacion getPostulacion() {
		return this.postulacion;
	}

	public void setPostulacion(Postulacion postulacion) {
		this.postulacion = postulacion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_lista_elegibles_cab", nullable = false)
	@NotNull
	public ListaElegiblesCab getListaElegiblesCab() {
		return this.listaElegiblesCab;
	}

	public void setListaElegiblesCab(ListaElegiblesCab listaElegiblesCab) {
		this.listaElegiblesCab = listaElegiblesCab;
	}

	@Column(name = "seleccionado")
	public Boolean getSeleccionado() {
		return this.seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	@Column(name = "disponible", nullable = false)
	public boolean isDisponible() {
		return this.disponible;
	}

	public void setDisponible(boolean disponible) {
		this.disponible = disponible;
	}

	@Column(name = "obs_estado", length = 250)
	@Length(max = 250)
	public String getObsEstado() {
		return this.obsEstado;
	}

	public void setObsEstado(String obsEstado) {
		this.obsEstado = obsEstado;
	}

	@Column(name = "id_documento")
	public Long getIdDocumento() {
		return this.idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
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

	@Transient
	public Boolean getIncluir() {
		return incluir;
	}

	public void setIncluir(Boolean incluir) {
		this.incluir = incluir;
	}

}
