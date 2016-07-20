package py.com.excelsis.sicca.entity;

// Generated 24/10/2011 09:36:22 AM by Hibernate Tools 3.4.0.Beta1

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
 * ExperienciaLaboral generated by hbm2java
 */
@Entity
@Table(name = "experiencia_laboral", schema = "seleccion")
public class ExperienciaLaboral implements java.io.Serializable {

	private Long idExperienciaLab;
	private Persona persona;
	private Date fechaDesde;
	private Date fechaHasta;
	private String empresa;
	private String funcionesRealizadas;
	private String referenciasLaborales;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Boolean activo;
	private Integer posicion;
	private Documentos documentos;
	private PersonaPostulante personaPostulante;
	private String puestoCargo;
	private String otroVincu;
	private String otroSector;
	private Pais pais;
	private DatosEspecificos datosEspecificosTipoVinculo;
	private DatosEspecificos datosEspecificosSector;
	private String tipo;
	private String usuAltaOee;
	private Date fechaAltaOee;
	private String usuModOee;
	private Date fechaModOee;
	private Boolean general;
	private Boolean especifica;

	
	public ExperienciaLaboral() {
	}

	public ExperienciaLaboral(Long idExperienciaLab, Persona persona,
			Date fechaDesde, String empresa, String funcionesRealizadas,
			String referenciasLaborales, String usuAlta, Date fechaAlta) {
		this.idExperienciaLab = idExperienciaLab;
		this.persona = persona;
		this.fechaDesde = fechaDesde;
		this.empresa = empresa;
		this.funcionesRealizadas = funcionesRealizadas;
		this.referenciasLaborales = referenciasLaborales;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ExperienciaLaboral(Long idExperienciaLab, Persona persona,
			Date fechaDesde, Date fechaHasta, String empresa,
			String funcionesRealizadas, String referenciasLaborales,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod) {
		this.idExperienciaLab = idExperienciaLab;
		this.persona = persona;
		this.fechaDesde = fechaDesde;
		this.fechaHasta = fechaHasta;
		this.empresa = empresa;
		this.funcionesRealizadas = funcionesRealizadas;
		this.referenciasLaborales = referenciasLaborales;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
	}

	@Id
	@Column(name = "id_experiencia_lab", unique = true, nullable = false)
	@GeneratedValue(generator="EXPERIENCIA_LABORAL_GENERATOR")
	@SequenceGenerator(name="EXPERIENCIA_LABORAL_GENERATOR",sequenceName="seleccion.sel_experiencia_laboral_id_experiencia_lab_seq")
	public Long getIdExperienciaLab() {
		return this.idExperienciaLab;
	}

	public void setIdExperienciaLab(Long idExperienciaLab) {
		this.idExperienciaLab = idExperienciaLab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona" ) 
	public Persona getPersona() {
		return this.persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_desde", length = 13)
	public Date getFechaDesde() {
		return this.fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecha_hasta", length = 13)
	public Date getFechaHasta() {
		return this.fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	@Column(name = "empresa", nullable = false, length = 150)
	@NotNull
	@Length(max = 150)
	public String getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	@Column(name = "funciones_realizadas", length = 250)
	@Length(max = 250)
	public String getFuncionesRealizadas() {
		return this.funcionesRealizadas;
	}

	public void setFuncionesRealizadas(String funcionesRealizadas) {
		this.funcionesRealizadas = funcionesRealizadas;
	}

	@Column(name = "referencias_laborales", length = 250)
	@Length(max = 250)
	public String getReferenciasLaborales() {
		return this.referenciasLaborales;
	}

	public void setReferenciasLaborales(String referenciasLaborales) {
		this.referenciasLaborales = referenciasLaborales;
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
	
	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	@Transient
	public Integer getPosicion() {
		return posicion;
	}

	public void setPosicion(Integer posicion) {
		this.posicion = posicion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento")
	public Documentos getDocumentos() {
		return documentos;
	}

	public void setDocumentos(Documentos documentos) {
		this.documentos = documentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_persona_postulante")
	public PersonaPostulante getPersonaPostulante() {
		return personaPostulante;
	}

	public void setPersonaPostulante(PersonaPostulante personaPostulante) {
		this.personaPostulante = personaPostulante;
	}

	@Column(name = "puesto_cargo")
	public String getPuestoCargo() {
		return puestoCargo;
	}

	public void setPuestoCargo(String puestoCargo) {
		this.puestoCargo = puestoCargo;
	}

	@Column(name = "otro_vincu")
	public String getOtroVincu() {
		return otroVincu;
	}

	public void setOtroVincu(String otroVincu) {
		this.otroVincu = otroVincu;
	}

	@Column(name = "otro_sector")
	public String getOtroSector() {
		return otroSector;
	}

	public void setOtroSector(String otroSector) {
		this.otroSector = otroSector;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais")
	public Pais getPais() {
		return pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_vincu")
	public DatosEspecificos getDatosEspecificosTipoVinculo() {
		return datosEspecificosTipoVinculo;
	}

	public void setDatosEspecificosTipoVinculo(
			DatosEspecificos datosEspecificosTipoVinculo) {
		this.datosEspecificosTipoVinculo = datosEspecificosTipoVinculo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_datos_especificos_sector")
	public DatosEspecificos getDatosEspecificosSector() {
		return datosEspecificosSector;
	}

	public void setDatosEspecificosSector(DatosEspecificos datosEspecificosSector) {
		this.datosEspecificosSector = datosEspecificosSector;
	}

	@Column(name = "tipo")
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "usu_alta_oee", length = 50)
	@Length(max = 50)
	public String getUsuAltaOee() {
		return usuAltaOee;
	}

	public void setUsuAltaOee(String usuAltaOee) {
		this.usuAltaOee = usuAltaOee;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta_oee", length = 29)
	public Date getFechaAltaOee() {
		return fechaAltaOee;
	}

	public void setFechaAltaOee(Date fechaAltaOee) {
		this.fechaAltaOee = fechaAltaOee;
	}

	@Column(name = "usu_mod_oee", length = 50)
	@Length(max = 50)
	public String getUsuModOee() {
		return usuModOee;
	}

	public void setUsuModOee(String usuModOee) {
		this.usuModOee = usuModOee;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod_oee", length = 29)
	public Date getFechaModOee() {
		return fechaModOee;
	}

	public void setFechaModOee(Date fechaModOee) {
		this.fechaModOee = fechaModOee;
	}

	@Column(name = "general")
	public Boolean getGeneral() {
		return general;
	}

	public void setGeneral(Boolean general) {
		this.general = general;
	}

	@Column(name = "especifica")
	public Boolean getEspecifica() {
		return especifica;
	}

	public void setEspecifica(Boolean especifica) {
		this.especifica = especifica;
	}
	
	
	
	

}
