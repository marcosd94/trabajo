package py.com.excelsis.sicca.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.validator.NotNull;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
* @author erivas
*/
@Entity
@Table(name = "bandeja_excepcion", schema = "proceso")
public class BandejaExcepcion implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7773374641859173551L;
	private Long id;
	private Excepcion excepcion;
	private Concurso concurso;
	private String usuario;
	private Date fechaRecepcion;
	private Date fechaInicio;
	private Integer diasCreacion;
	private Integer diasInicio;
	private BigDecimal atraso;
	private Entidad entidad;
	private SinNivelEntidad sinNivelEntidad;
	private SinEntidad sinEntidad;
	private ConcursoPuestoAgr concursoPuestoAgr; //id_concurso_puesto_agr
	private Long idProcessInstance; //id_process_instance
	private Proceso proceso;
	private Actividad actividad;
	private ActividadProceso actividadProceso;
	private ConfiguracionUoCab configuracionUoCab;


	public BandejaExcepcion(){
		
	}
	
	@Id 
	@Column(name = "id_taskinstance", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso")
	public Concurso getConcurso() {
		return this.concurso;
	}
	public void setConcurso(Concurso concurso) {
		this.concurso = concurso;
	}


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return this.configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@Column(name = "usuario")
	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Column(name = "fecha_recepcion", length = 29)
	public Date getFechaRecepcion() {
		return this.fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	@Column(name = "fecha_inicio", length = 29)
	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	@Column(name = "dias_creacion")
	public Integer getDiasCreacion() {
		return this.diasCreacion;
	}

	public void setDiasCreacion(Integer diasCreacion) {
		this.diasCreacion = diasCreacion;
	}

	@Column(name = "dias_inicio")
	public Integer getDiasInicio() {
		return this.diasInicio;
	}

	public void setDiasInicio(Integer diasInicio) {
		this.diasInicio = diasInicio;
	}

	@Column(name = "atraso")
	public BigDecimal getAtraso() {
		return this.atraso;
	}

	public void setAtraso(BigDecimal atraso) {
		this.atraso = atraso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_actividad", nullable = false)
	@NotNull
	public Actividad getActividad() {
		return this.actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidad")
	public Entidad getEntidad() {
		return this.entidad;
	}
	public void setEntidad(Entidad entidad) {
		this.entidad = entidad;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sin_nivel_entidad")
	public SinNivelEntidad getSinNivelEntidad() {
		return this.sinNivelEntidad;
	}
	public void setSinNivelEntidad(SinNivelEntidad sinNivelEntidad) {
		this.sinNivelEntidad = sinNivelEntidad;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr")
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@Column(name = "id_process_instance")
	public Long getIdProcessInstance() {
		return idProcessInstance;
	}
	public void setIdProcessInstance(Long idProcessInstance) {
		this.idProcessInstance = idProcessInstance;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proceso", nullable = false)
	public Proceso getProceso() {
		return this.proceso;
	}
	public void setProceso(Proceso proceso) {
		this.proceso = proceso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_actividad_proceso", nullable = false)
	public ActividadProceso getActividadProceso() {
		return actividadProceso;
	}
	public void setActividadProceso(ActividadProceso actividadProceso) {
		this.actividadProceso = actividadProceso;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sin_entidad")
	public SinEntidad getSinEntidad() {
		return sinEntidad;
	}
	public void setSinEntidad(SinEntidad sinEntidad) {
		this.sinEntidad = sinEntidad;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_excepcion")
	public Excepcion getExcepcion() {
		return excepcion;
	}
	public void setExcepcion(Excepcion excepcion) {
		this.excepcion = excepcion;
	}
	
}

