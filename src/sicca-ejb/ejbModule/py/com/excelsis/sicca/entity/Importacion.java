package py.com.excelsis.sicca.entity;

// Generated 08/11/2012 10:10:53 AM by Hibernate Tools 3.4.0.Beta1

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
 * Importacion generated by hbm2java
 */
@Entity
@Table(name = "importacion", schema = "remuneracion")
public class Importacion implements java.io.Serializable {

	private Long idImportacion;
	private EmpleadoPuesto empleadoPuesto;
	private ConfiguracionUoCab configuracionUoCab;
	private Integer mes;
	private Integer anho;
	private Integer nivel;
	private Integer entidad;
	private Integer oee;
	private String documentoIdentidad;
	private String nombres;
	private String apellidos;
	private String estado;
	private Integer objetoGto;
	private String categoria;
	private Integer presupuestado;
	private Integer devengado;
	private String movimiento;
	private Date fecha;
	private String cargo;
	private String discapacidad;
	private String estadoImport;
	private String motivo;
	private String usuAlta;
	private Date fechaAlta;
	private String origen;
	private String descripCategoria;
	private String descripConcepto;
	private String concepto;
	private Integer linea;
	private Integer presupuesto;
	private Integer programa;
	private Integer cantProcesada;
	private Integer fuenteFinanciamiento;
	
	//Nuevas columnas
	private Integer remuneracionTotal;
	private String lugar;
	private String funcionCumplida;
	private String cargaHoraria;
	private Integer tipoDiscapacidad;
	private Integer anhoIngreso;

	public Importacion() {
	}

	public Importacion(
			Long idImportacion, ConfiguracionUoCab configuracionUoCab, Integer mes, Integer anho,
			int nivel, Integer entidad, Integer oee, String documentoIdentidad,
			String nombres, String apellidos, String estado, Integer objetoGto, String categoria,
			String estadoImport, String usuAlta, Date fechaAlta, String origen) {
		this.idImportacion = idImportacion;
		this.configuracionUoCab = configuracionUoCab;
		this.mes = mes;
		this.anho = anho;
		this.nivel = nivel;
		this.entidad = entidad;
		this.oee = oee;
		this.documentoIdentidad = documentoIdentidad;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.estado = estado;
		this.objetoGto = objetoGto;
		this.categoria = categoria;
		this.estadoImport = estadoImport;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.origen = origen;
	}

	public Importacion(
			Long idImportacion, EmpleadoPuesto empleadoPuesto,
			ConfiguracionUoCab configuracionUoCab, Integer mes, Integer anho, int nivel,
			Integer entidad, Integer oee, Integer dependencia, String documentoIdentidad, String nombres,
			String apellidos, String estado, Integer objetoGto, String categoria,
			Integer presupuestado, Integer devengado, String movimiento, Date fecha, String cargo,
			String discapacidad, String estadoImport, String motivo, String usuAlta,
			Date fechaAlta, String origen, String descripCategoria, String descripConcepto, String concepto,
			Integer linea, Integer presupuesto, Integer programa, Integer fuenteFinanciamiento, 
			Integer remuneracionTotal, String lugar, String funcionCumplida, String cargaHoraria, Integer tipoDiscapacidad, Integer anhoIngreso) {
		this.idImportacion = idImportacion;
		this.empleadoPuesto = empleadoPuesto;
		this.configuracionUoCab = configuracionUoCab;
		this.mes = mes;
		this.anho = anho;
		this.nivel = nivel;
		this.entidad = entidad;
		this.oee = oee;
		this.documentoIdentidad = documentoIdentidad;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.estado = estado;
		this.objetoGto = objetoGto;
		this.categoria = categoria;
		this.presupuestado = presupuestado;
		this.devengado = devengado;
		this.movimiento = movimiento;
		this.fecha = fecha;
		this.cargo = cargo;
		this.discapacidad = discapacidad;
		this.estadoImport = estadoImport;
		this.motivo = motivo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.origen = origen;
		this.descripCategoria = descripCategoria;
		this.descripConcepto = descripConcepto;
		this.concepto = concepto;
		this.linea = linea;
		this.presupuesto = presupuesto;
		this.programa = programa;
		this.fuenteFinanciamiento = fuenteFinanciamiento;
		this.remuneracionTotal = remuneracionTotal;
		this.lugar = lugar;
		this.funcionCumplida = funcionCumplida;
		this.cargaHoraria = cargaHoraria;
		this.tipoDiscapacidad = tipoDiscapacidad;
		this.anhoIngreso = anhoIngreso;
	}

	@Id
	@GeneratedValue(generator = "IMPORTACION_GENERATOR")
	@SequenceGenerator(name = "IMPORTACION_GENERATOR", sequenceName = "remuneracion.importacion_id_importacion_seq")
	@Column(name = "id_importacion", unique = true, nullable = false)
	public Long getIdImportacion() {
		return this.idImportacion;
	}

	public void setIdImportacion(Long idImportacion) {
		this.idImportacion = idImportacion;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empleado_puesto")
	public EmpleadoPuesto getEmpleadoPuesto() {
		return this.empleadoPuesto;
	}

	public void setEmpleadoPuesto(EmpleadoPuesto empleadoPuesto) {
		this.empleadoPuesto = empleadoPuesto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo", nullable = false)
	@NotNull
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return this.configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@Column(name = "mes", nullable = false)
	public Integer getMes() {
		return this.mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	@Column(name = "anho", nullable = false)
	public Integer getAnho() {
		return this.anho;
	}

	public void setAnho(int anho) {
		this.anho = anho;
	}

	@Column(name = "nivel", nullable = false)
	public Integer getNivel() {
		return this.nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}

	@Column(name = "entidad", nullable = false)
	public Integer getEntidad() {
		return this.entidad;
	}

	public void setEntidad(int entidad) {
		this.entidad = entidad;
	}
	
	@Column(name = "oee", nullable = false)
	public Integer getOee() {
		return this.oee;
	}

	public void setOee(int oee) {
		this.oee = oee;
	}

	@Column(name = "documento_identidad", nullable = false, length = 30)
	@NotNull
	@Length(max = 30)
	public String getDocumentoIdentidad() {
		return this.documentoIdentidad;
	}

	public void setDocumentoIdentidad(String documentoIdentidad) {
		this.documentoIdentidad = documentoIdentidad;
	}

	@Column(name = "nombres", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getNombres() {
		return this.nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	@Column(name = "apellidos", nullable = false, length = 80)
	@NotNull
	@Length(max = 80)
	public String getApellidos() {
		return this.apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	@Column(name = "estado", length = 15)
	@Length(max = 15)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "objeto_gto")
	public Integer getObjetoGto() {
		return this.objetoGto;
	}

	public void setObjetoGto(int objetoGto) {
		this.objetoGto = objetoGto;
	}

	@Column(name = "categoria", length = 10)
	@Length(max = 10)
	public String getCategoria() {
		return this.categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Column(name = "presupuestado")
	public Integer getPresupuestado() {
		return this.presupuestado;
	}

	public void setPresupuestado(Integer presupuestado) {
		this.presupuestado = presupuestado;
	}

	@Column(name = "devengado")
	public Integer getDevengado() {
		return this.devengado;
	}

	public void setDevengado(Integer devengado) {
		this.devengado = devengado;
	}

	@Column(name = "movimiento", length = 2)
	@Length(max = 2)
	public String getMovimiento() {
		return this.movimiento;
	}

	public void setMovimiento(String movimiento) {
		this.movimiento = movimiento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha", length = 29)
	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Column(name = "cargo", length = 250)
	@Length(max = 250)
	public String getCargo() {
		return this.cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Column(name = "discapacidad", length = 1)
	@Length(max = 1)
	public String getDiscapacidad() {
		return this.discapacidad;
	}

	public void setDiscapacidad(String discapacidad) {
		this.discapacidad = discapacidad;
	}

	@Column(name = "estado_import", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getEstadoImport() {
		return this.estadoImport;
	}

	public void setEstadoImport(String estadoImport) {
		this.estadoImport = estadoImport;
	}

	@Column(name = "motivo", length = 100)
	@Length(max = 100)
	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
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

	@Column(name = "origen", nullable = false, length = 1)
	@NotNull
	@Length(max = 1)
	public String getOrigen() {
		return this.origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	@Column(name = "descrip_categoria", length = 200)
	@Length(max = 200)
	public String getDescripCategoria() {
		return this.descripCategoria;
	}

	public void setDescripCategoria(String descripCategoria) {
		this.descripCategoria = descripCategoria;
	}
	
	@Column(name = "descrip_concepto", length = 200)
	@Length(max = 200)
	public String getDescripConcepto() {
		return this.descripConcepto;
	}

	public void setDescripConcepto(String descripConcepto) {
		this.descripConcepto = descripConcepto;
	}

	@Column(name = "concepto", length = 200)
	@Length(max = 200)
	public String getConcepto() {
		return this.concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	@Column(name = "linea")
	public Integer getLinea() {
		return this.linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}

	@Column(name = "presupuesto")
	public Integer getPresupuesto() {
		return this.presupuesto;
	}

	public void setPresupuesto(Integer presupuesto) {
		this.presupuesto = presupuesto;
	}

	@Column(name = "programa")
	public Integer getPrograma() {
		return this.programa;
	}

	public void setPrograma(Integer programa) {
		this.programa = programa;
	}

	@Transient
	public Integer getCantProcesada() {
		return cantProcesada;
	}

	public void setCantProcesada(Integer cantProcesada) {
		this.cantProcesada = cantProcesada;
	}

	@Column(name = "fuente_financiamiento")
	public Integer getFuenteFinanciamiento() {
		return this.fuenteFinanciamiento;
	}

	public void setFuenteFinanciamiento(Integer fuenteFinanciamiento) {
		this.fuenteFinanciamiento = fuenteFinanciamiento;
	}
	
	@Column(name = "lugar", length = 125)
	@Length(max = 125)
	public String getLugar() {
		return this.lugar;
	}
	
	public void setLugar(String lugar) {
		this.lugar = lugar;
	}
	
	@Column(name = "funcion_cumplida", length = 500)
	@Length(max = 500)
	public String getFuncionCumplida() {
		return this.funcionCumplida;
	}
	
	public void setFuncionCumplida(String funcionCumplida) {
		this.funcionCumplida = funcionCumplida;
	}
	
	@Column(name = "carga_horaria")
	public String getCargaHoraria() {
		return this.cargaHoraria;
	}
	
	public void setCargaHoraria(String cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}
	
	@Column(name = "remuneracion_total")
	public Integer getRemuneracionTotal() {
		return this.remuneracionTotal;
	}

	public void setRemuneracionTotal(Integer remuneracionTotal) {
		this.remuneracionTotal = remuneracionTotal;
	}
	
	@Column(name = "tipo_discapacidad")
	public Integer getTipoDiscapacidad() {
		return this.tipoDiscapacidad;
	}

	public void setTipoDiscapacidad(Integer tipoDiscapacidad) {
		this.tipoDiscapacidad = tipoDiscapacidad;
	}
	
	@Column(name = "anho_ingreso")
	public Integer getAnhoIngreso() {
		return this.anhoIngreso;
	}

	public void setAnhoIngreso(Integer anhoIngreso) {
		this.anhoIngreso = anhoIngreso;
	}
}
