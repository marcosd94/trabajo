package py.com.excelsis.sicca.entity;

// Generated 25/10/2011 03:30:56 PM by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
 * Concurso generated by hbm2java
 */
@Entity
@Table(name = "concurso", schema = "seleccion")
public class Concurso implements java.io.Serializable {

	private Long idConcurso;
	private DatosEspecificos datosEspecificosTipoConc;
	private String nombre;
	private Boolean adReferendum;
	private Boolean desprecarizacion;
	private String observacion;
	private Integer estado;
	private Date fechaCreacion;
	private ConfiguracionUoCab configuracionUoCab;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String observacionReserva;
	private Integer numero;
	private Integer anhio;
	private Date fechaFinalizacion;
	private String usuFinalizacion;
	private Integer nroExpediente;
	private Date fechaExpediente;
	private Boolean pcd;
	private Boolean institucional;
	private Boolean interinstitucional;
	private Boolean promocion;

	private Set<Observacion> observacions = new HashSet<Observacion>(0);
	private Set<Excepcion> excepcions = new HashSet<Excepcion>(0);
	private List<ConcursoPuestoAgr> concursoPuestoAgrs = new ArrayList<ConcursoPuestoAgr>(0);
	private Set<PublicacionPortal> publicacionPortals = new HashSet<PublicacionPortal>(0);

	private Set<AdjuntoDocConcurso> adjuntoDocConcursos = new HashSet<AdjuntoDocConcurso>();
	private Set<BandejaExcepcion> bandejaExcepcions = new HashSet<BandejaExcepcion>();
	private Set<ComisionSeleccionCab> comisionSeleccionCabs = new HashSet<ComisionSeleccionCab>();
	private Set<ConcursoPuestoDet> concursoPuestoDets = new HashSet<ConcursoPuestoDet>();
	private Set<CronogramaConcCab> cronogramaConcCabs = new HashSet<CronogramaConcCab>();
	private Set<Documentos> documentos = new HashSet<Documentos>();
	private Set<FechasGrupoPuesto> fechasGrupoPuestos = new HashSet<FechasGrupoPuesto>();
	private Set<PresentAclaracDoc> presentAclaracDocs = new HashSet<PresentAclaracDoc>();
	private Set<PublicacionConcurso> publicacionConcursos = new HashSet<PublicacionConcurso>();

	private Set<Excepcion> excepciones = new HashSet<Excepcion>();

	public Concurso() {
	}

	public Concurso(
			Long idConcurso, DatosEspecificos datosEspecificos, String nombre, Integer estado,
			Date fechaCreacion, boolean activo, String usuAlta, Date fechaAlta) {
		this.idConcurso = idConcurso;
		this.datosEspecificosTipoConc = datosEspecificos;
		this.nombre = nombre;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public Concurso(
			Long idConcurso, DatosEspecificos datosEspecificos, String nombre,
			Boolean adReferendum, Boolean desprecarizacion, String observacion, Integer estado,
			Date fechaCreacion, boolean activo, String usuAlta, Date fechaAlta, String usuMod,
			Date fechaMod) {
		this.idConcurso = idConcurso;
		this.datosEspecificosTipoConc = datosEspecificos;

		this.nombre = nombre;
		this.adReferendum = adReferendum;
		this.desprecarizacion = desprecarizacion;
		this.observacion = observacion;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@GeneratedValue(generator = "CONCURSO_GENERATOR")
	@SequenceGenerator(name = "CONCURSO_GENERATOR", sequenceName = "seleccion.concurso_id_concurso_seq")
	@Column(name = "id_concurso", unique = true, nullable = false)
	public Long getIdConcurso() {
		return this.idConcurso;
	}

	public void setIdConcurso(Long idConcurso) {
		this.idConcurso = idConcurso;
	}

	@Column(name = "nombre", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "ad_referendum")
	public Boolean getAdReferendum() {
		return this.adReferendum;
	}

	public void setAdReferendum(Boolean adReferendum) {
		this.adReferendum = adReferendum;
	}

	@Column(name = "desprecarizacion")
	public Boolean getDesprecarizacion() {
		return this.desprecarizacion;
	}

	public void setDesprecarizacion(Boolean desprecarizacion) {
		this.desprecarizacion = desprecarizacion;
	}

	@Column(name = "observacion", length = 250)
	@Length(max = 250)
	public String getObservacion() {
		return this.observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	@Column(name = "observacion_reserva", length = 250)
	@Length(max = 250)
	public String getObservacionReserva() {
		return observacionReserva;
	}

	public void setObservacionReserva(String observacionReserva) {
		this.observacionReserva = observacionReserva;
	}

	@Column(name = "estado", nullable = false)
	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_creacion", nullable = false, length = 29)
	@NotNull
	public Date getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_esp_tipo_conc", nullable = false)
	@NotNull
	public DatosEspecificos getDatosEspecificosTipoConc() {
		return datosEspecificosTipoConc;
	}

	public void setDatosEspecificosTipoConc(DatosEspecificos datosEspecificosTipoConc) {
		this.datosEspecificosTipoConc = datosEspecificosTipoConc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo", nullable = false)
	@NotNull
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@Column(name = "numero")
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name = "anio")
	public Integer getAnhio() {
		return anhio;
	}

	public void setAnhio(Integer anhio) {
		this.anhio = anhio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_finalizacion", length = 29)
	public Date getFechaFinalizacion() {
		return fechaFinalizacion;
	}

	public void setFechaFinalizacion(Date fechaFinalizacion) {
		this.fechaFinalizacion = fechaFinalizacion;
	}

	@Column(name = "usu_finalizacion", length = 50)
	@Length(max = 50)
	public String getUsuFinalizacion() {
		return usuFinalizacion;
	}

	public void setUsuFinalizacion(String usuFinalizacion) {
		this.usuFinalizacion = usuFinalizacion;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<Observacion> getObservacions() {
		return this.observacions;
	}

	public void setObservacions(Set<Observacion> observacions) {
		this.observacions = observacions;
	}

	@Column(name = "nro_expediente")
	public Integer getNroExpediente() {
		return nroExpediente;
	}

	public void setNroExpediente(Integer nroExpediente) {
		this.nroExpediente = nroExpediente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_expediente", length = 29)
	public Date getFechaExpediente() {
		return fechaExpediente;
	}

	public void setFechaExpediente(Date fechaExpediente) {
		this.fechaExpediente = fechaExpediente;
	}

	@Column(name = "pcd", nullable = false)
	public Boolean getPcd() {
		return pcd;
	}

	public void setPcd(Boolean pcd) {
		this.pcd = pcd;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<Excepcion> getExcepcions() {
		return this.excepcions;
	}

	public void setExcepcions(Set<Excepcion> excepcions) {
		this.excepcions = excepcions;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public List<ConcursoPuestoAgr> getConcursoPuestoAgrs() {
		return this.concursoPuestoAgrs;
	}

	public void setConcursoPuestoAgrs(List<ConcursoPuestoAgr> concursoPuestoAgrs) {
		this.concursoPuestoAgrs = concursoPuestoAgrs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<PublicacionPortal> getPublicacionPortals() {
		return this.publicacionPortals;
	}

	public void setPublicacionPortals(Set<PublicacionPortal> publicacionPortals) {
		this.publicacionPortals = publicacionPortals;
	}

	@Column(name = "institucional")
	public Boolean getInstitucional() {
		return institucional;
	}

	public void setInstitucional(Boolean institucional) {
		this.institucional = institucional;
	}

	@Column(name = "interinstitucional")
	public Boolean getInterinstitucional() {
		return interinstitucional;
	}

	public void setInterinstitucional(Boolean interinstitucional) {
		this.interinstitucional = interinstitucional;
	}
	
	
	@Column(name = "promocion")
	public Boolean getPromocion() {
		return promocion;
	}

	public void setPromocion(Boolean promocion) {
		this.promocion = promocion;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<AdjuntoDocConcurso> getAdjuntoDocConcursos() {
		return adjuntoDocConcursos;
	}

	public void setAdjuntoDocConcursos(Set<AdjuntoDocConcurso> adjuntoDocConcursos) {
		this.adjuntoDocConcursos = adjuntoDocConcursos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<BandejaExcepcion> getBandejaExcepcions() {
		return bandejaExcepcions;
	}

	public void setBandejaExcepcions(Set<BandejaExcepcion> bandejaExcepcions) {
		this.bandejaExcepcions = bandejaExcepcions;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<ComisionSeleccionCab> getComisionSeleccionCabs() {
		return comisionSeleccionCabs;
	}

	public void setComisionSeleccionCabs(Set<ComisionSeleccionCab> comisionSeleccionCabs) {
		this.comisionSeleccionCabs = comisionSeleccionCabs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<ConcursoPuestoDet> getConcursoPuestoDets() {
		return concursoPuestoDets;
	}

	public void setConcursoPuestoDets(Set<ConcursoPuestoDet> concursoPuestoDets) {
		this.concursoPuestoDets = concursoPuestoDets;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<CronogramaConcCab> getCronogramaConcCabs() {
		return cronogramaConcCabs;
	}

	public void setCronogramaConcCabs(Set<CronogramaConcCab> cronogramaConcCabs) {
		this.cronogramaConcCabs = cronogramaConcCabs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<Documentos> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Set<Documentos> documentos) {
		this.documentos = documentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<FechasGrupoPuesto> getFechasGrupoPuestos() {
		return fechasGrupoPuestos;
	}

	public void setFechasGrupoPuestos(Set<FechasGrupoPuesto> fechasGrupoPuestos) {
		this.fechasGrupoPuestos = fechasGrupoPuestos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<PresentAclaracDoc> getPresentAclaracDocs() {
		return presentAclaracDocs;
	}

	public void setPresentAclaracDocs(Set<PresentAclaracDoc> presentAclaracDocs) {
		this.presentAclaracDocs = presentAclaracDocs;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<PublicacionConcurso> getPublicacionConcursos() {
		return publicacionConcursos;
	}

	public void setPublicacionConcursos(Set<PublicacionConcurso> publicacionConcursos) {
		this.publicacionConcursos = publicacionConcursos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concurso")
	public Set<Excepcion> getExcepciones() {
		return excepciones;
	}

	public void setExcepciones(Set<Excepcion> excepciones) {
		this.excepciones = excepciones;
	}

}
