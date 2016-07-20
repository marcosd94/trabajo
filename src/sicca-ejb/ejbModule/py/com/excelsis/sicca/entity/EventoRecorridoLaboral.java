package py.com.excelsis.sicca.entity;

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


@Entity
@Table(name = "eventos_recorrido_laboral", schema = "legajo")
public class EventoRecorridoLaboral implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long idEventoRecorridoLaboral;
	private EmpleadoPuesto empleadoPuesto;
	private Documentos documento;
	private DatosEspecificos tipoEventoDatosEspecificos;
	private Integer nroActoAdministrativo;
	private Date fechaActoAdministrativo;
	private String encabezado;
	private String observaciones;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String descTipoEvento;
	
	public EventoRecorridoLaboral() {
	}

	public EventoRecorridoLaboral(Long idEventoRecorridoLaboral,
			EmpleadoPuesto empleadoPuesto,
			Documentos documento, DatosEspecificos tipoEventoDatosEspecificos,Integer nroActoAdministrativo, Date fechaActoAdministrativo,
			String encabezado, String observaciones, boolean activo, String usuAlta,
			Date fechaAlta, String descTipoEvento) {
		this.idEventoRecorridoLaboral = idEventoRecorridoLaboral;
		this.empleadoPuesto = empleadoPuesto;
		this.documento = documento;
		this.tipoEventoDatosEspecificos = tipoEventoDatosEspecificos;
		this.nroActoAdministrativo = nroActoAdministrativo;
		this.fechaActoAdministrativo = fechaActoAdministrativo;
		this.encabezado = encabezado;
		this.observaciones = observaciones;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.descTipoEvento = descTipoEvento;
	}

	

	@Id
	@GeneratedValue(generator="EVENTO_RECORRIDO_LABORAL")
	@SequenceGenerator(name="EVENTO_RECORRIDO_LABORAL",sequenceName="legajo.eventos_recorrido_laboral_id_evento_seq")
	@Column(name = "id_evento", unique = true, nullable = false)
	public Long getIdEventoRecorridoLaboral() {
		return this.idEventoRecorridoLaboral;
	}

	public void setIdEventoRecorridoLaboral(Long idEventoRecorridoLaboral) {
		this.idEventoRecorridoLaboral = idEventoRecorridoLaboral;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado_puesto", nullable = false)
	@NotNull
	public EmpleadoPuesto getEmpleadoPuesto() {
		return this.empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_documento", nullable = false)
	@NotNull
	public Documentos getDocumento() {
		return this.documento;
	}

	public void setDocumento(Documentos documento) {
		this.documento = documento;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_evento_datos_especificos", nullable = false)
	@NotNull
	public DatosEspecificos getTipoEventoDatosEspecificos() {
		return tipoEventoDatosEspecificos;
	}

	public void setTipoEventoDatosEspecificos(
			DatosEspecificos tipoEventoDatosEspecificos) {
		this.tipoEventoDatosEspecificos = tipoEventoDatosEspecificos;
	}

	@Column(name = "nro_acto_administrativo", nullable = false)
	public Integer getNroActoAdministrativo() {
		return nroActoAdministrativo;
	}

	public void setNroActoAdministrativo(Integer nroActoAdministrativo) {
		this.nroActoAdministrativo = nroActoAdministrativo;
	}
	
	@Column(name = "fecha_acto_administrativo", nullable = false)
	public Date getFechaActoAdministrativo() {
		return fechaActoAdministrativo;
	}

	public void setFechaActoAdministrativo(Date fechaActoAdministrativo) {
		this.fechaActoAdministrativo = fechaActoAdministrativo;
	}
	
	@Column(name = "encabezado", nullable = false)
	public String getEncabezado() {
		return encabezado;
	}

	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}

	@Column(name = "observaciones", nullable = false)
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
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

	@Column(name = "desc_tipo_evento")
	public String getDescTipoEvento() {
		return descTipoEvento;
	}

	public void setDescTipoEvento(String descTipoEvento) {
		this.descTipoEvento = descTipoEvento;
	}
	
	


}
