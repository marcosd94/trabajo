package py.com.excelsis.sicca.entity;

// Generated 31/10/2011 03:26:27 PM by Hibernate Tools 3.4.0.Beta1

import java.io.File;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import javax.persistence.OneToMany;

/**
 * Documentos generated by hbm2java
 */
@Entity
@Table(name = "documentos", schema = "general")
public class Documentos implements java.io.Serializable {

	private Long idDocumento;
	private DatosEspecificos datosEspecificos;
	private String nombreLogDoc;
	private Integer nroDocumento;
	private Integer anhoDocumento;
	private String nombreFisicoDoc;
	private String descripcion;
	private String tamanhoDoc;
	private String ubicacionFisica;
	private String mimetype;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String usuUltDesc;
	private Date fechaUltDesc;
	private Long idTabla;
	private String nombreTabla;
	private Concurso concurso;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private String nombrePantalla;
	private Persona persona;
	private String checksum;
	private Date fechaDoc;
	private Date fechaFirmaContrato;
	private Date fechaFinContrato;
	private Integer nroContrato;

	private ConfiguracionUoCab configuracionUoCab;
	private Set<AdjuntosCap> adjuntosCaps = new HashSet<AdjuntosCap>(0);
	private Set<GruposSujetos> gruposSujetoses = new HashSet<GruposSujetos>(0);
	private Set<DatosVacaciones> datosVacacioneses = new HashSet<DatosVacaciones>(0);
	private Set<DatosPermiso> datosPermisoes = new HashSet<DatosPermiso>(0);
	private File inputFile;
	private Boolean validoLegajo;

	public Documentos() {
	}

	public Documentos(
			Long idDocumento, DatosEspecificos datosEspecificos, String nombreLogDoc,
			String ubicacionFisica, String mimetype, boolean activo, String usuAlta, Date fechaAlta) {
		this.idDocumento = idDocumento;
		this.datosEspecificos = datosEspecificos;
		this.nombreLogDoc = nombreLogDoc;
		this.ubicacionFisica = ubicacionFisica;
		this.mimetype = mimetype;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public Documentos(
			Long idDocumento, Long idDatosEspecificosTipoDocumento, String nombreLogDoc,
			Integer nroDocumento, Integer anhoDocumento, String nombreFisicoDoc,
			String descripcion, String tamanhoDoc, String ubicacionFisica, String mimetype,
			boolean activo, String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			String usuUltDesc, Date fechaUltDesc, Set<GruposSujetos> gruposSujetoses,
			Set<DatosVacaciones> datosVacacioneses) {
		this.idDocumento = idDocumento;
		this.datosEspecificos = datosEspecificos;
		this.nombreLogDoc = nombreLogDoc;
		this.nroDocumento = nroDocumento;
		this.anhoDocumento = anhoDocumento;
		this.nombreFisicoDoc = nombreFisicoDoc;
		this.descripcion = descripcion;
		this.tamanhoDoc = tamanhoDoc;
		this.ubicacionFisica = ubicacionFisica;
		this.mimetype = mimetype;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.usuUltDesc = usuUltDesc;
		this.fechaUltDesc = fechaUltDesc;
		this.gruposSujetoses = gruposSujetoses;
		this.datosVacacioneses = datosVacacioneses;
	}

	@Id
	@Column(name = "id_documento", unique = true, nullable = false)
	@GeneratedValue(generator = "DOCUMENTO_GENERATOR")
	@SequenceGenerator(name = "DOCUMENTO_GENERATOR", sequenceName = "general.documentos_id_documento_seq")
	public Long getIdDocumento() {
		return this.idDocumento;
	}

	public void setIdDocumento(Long idDocumento) {
		this.idDocumento = idDocumento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_tipo_documento")
	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}

	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

	@Column(name = "nombre_log_doc")
	public String getNombreLogDoc() {
		return this.nombreLogDoc;
	}

	public void setNombreLogDoc(String nombreLogDoc) {
		this.nombreLogDoc = nombreLogDoc;
	}

	@Column(name = "nro_documento")
	public Integer getNroDocumento() {
		return this.nroDocumento;
	}

	public void setNroDocumento(Integer nroDocumento) {
		this.nroDocumento = nroDocumento;
	}

	@Column(name = "anho_documento")
	public Integer getAnhoDocumento() {
		return this.anhoDocumento;
	}

	public void setAnhoDocumento(Integer anhoDocumento) {
		this.anhoDocumento = anhoDocumento;
	}

	@Column(name = "nombre_fisico_doc")
	public String getNombreFisicoDoc() {
		return this.nombreFisicoDoc;
	}

	public void setNombreFisicoDoc(String nombreFisicoDoc) {
		this.nombreFisicoDoc = nombreFisicoDoc;
	}

	@Column(name = "descripcion", length = 500)
	@Length(max = 500)
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Column(name = "tamanho_doc", length = 10)
	@Length(max = 10)
	public String getTamanhoDoc() {
		return this.tamanhoDoc;
	}

	public void setTamanhoDoc(String tamanhoDoc) {
		this.tamanhoDoc = tamanhoDoc;
	}

	@Column(name = "ubicacion_fisica", nullable = false)
	@NotNull
	public String getUbicacionFisica() {
		return this.ubicacionFisica;
	}

	public void setUbicacionFisica(String ubicacionFisica) {
		this.ubicacionFisica = ubicacionFisica;
	}

	@Column(name = "mimetype", nullable = false, length = 100)
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

	@Column(name = "usu_ult_desc", length = 50)
	@Length(max = 50)
	public String getUsuUltDesc() {
		return this.usuUltDesc;
	}

	public void setUsuUltDesc(String usuUltDesc) {
		this.usuUltDesc = usuUltDesc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_ult_desc", length = 29)
	public Date getFechaUltDesc() {
		return this.fechaUltDesc;
	}

	public void setFechaUltDesc(Date fechaUltDesc) {
		this.fechaUltDesc = fechaUltDesc;
	}

	@Column(name = "id_tabla")
	public Long getIdTabla() {
		return idTabla;
	}

	public void setIdTabla(Long idTabla) {
		this.idTabla = idTabla;
	}

	@Column(name = "nombre_tabla")
	public String getNombreTabla() {
		return nombreTabla;
	}

	public void setNombreTabla(String nombreTabla) {
		this.nombreTabla = nombreTabla;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso")
	public Concurso getConcurso() {
		return concurso;
	}

	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}

	@Column(name = "nombre_pantalla")
	public String getNombrePantalla() {
		return nombrePantalla;
	}

	public void setNombrePantalla(String nombrePantalla) {
		this.nombrePantalla = nombrePantalla;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona")
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Transient
	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	@Column(name = "checksum")
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "documentos")
	public Set<AdjuntosCap> getAdjuntosCaps() {
		return this.adjuntosCaps;
	}

	public void setAdjuntosCaps(Set<AdjuntosCap> adjuntosCaps) {
		this.adjuntosCaps = adjuntosCaps;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_doc", length = 29)
	public Date getFechaDoc() {
		return fechaDoc;
	}

	public void setFechaDoc(Date fechaDoc) {
		this.fechaDoc = fechaDoc;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_firma_contrato", length = 29)
	public Date getFechaFirmaContrato() {
		return fechaFirmaContrato;
	}

	public void setFechaFirmaContrato(Date fechaFirmaContrato) {
		this.fechaFirmaContrato = fechaFirmaContrato;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_fin_Contrato", length = 29)
	public Date getFechaFinContrato() {
		return fechaFinContrato;
	}

	public void setFechaFinContrato(Date fechaFinContrato) {
		this.fechaFinContrato = fechaFinContrato;
	}

	@Column(name = "nro_contrato")
	public Integer getNroContrato() {
		return nroContrato;
	}

	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "documentos")
	public Set<GruposSujetos> getGruposSujetoses() {
		return this.gruposSujetoses;
	}

	public void setGruposSujetoses(Set<GruposSujetos> gruposSujetoses) {
		this.gruposSujetoses = gruposSujetoses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "documentos")
	public Set<DatosVacaciones> getDatosVacacioneses() {
		return this.datosVacacioneses;
	}

	public void setDatosVacacioneses(Set<DatosVacaciones> datosVacacioneses) {
		this.datosVacacioneses = datosVacacioneses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "documentos")
	public Set<DatosPermiso> getDatosPermisoes() {
		return this.datosPermisoes;
	}

	public void setDatosPermisoes(Set<DatosPermiso> datosPermisoes) {
		this.datosPermisoes = datosPermisoes;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr")
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@Column(name = "valido_legajo")
	public Boolean getValidoLegajo() {
		return validoLegajo;
	}

	public void setValidoLegajo(Boolean validoLegajo) {
		this.validoLegajo = validoLegajo;
	}


}
