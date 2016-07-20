package py.com.excelsis.sicca.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ingreso_renovacion_contrato", schema = "general")
public class IngresoRenovacionContrato implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5134561026554556622L;
	private Long idEmpleado;
	private Long idPuesto;
	private Long idPersona;
	private Long idPago;
	private String nombre;
	private String apellido;
	private String descripcion;
	private Integer nroContrato;
	private Date fechaInicio;
	private Date fechaFin;
	private int monto;
	
	
	public IngresoRenovacionContrato() {
		
	}

	public IngresoRenovacionContrato(Long idEmpleado, Long idPuesto,
			Long idPersona, Long idPago, String nombre, String apellido,
			String descripcion, Integer nroContrato, Date fechaInicio,
			Date fechaFin) {
		
		this.idEmpleado = idEmpleado;
		this.idPuesto = idPuesto;
		this.idPersona = idPersona;
		this.idPago = idPago;
		this.nombre = nombre;
		this.apellido = apellido;
		this.descripcion = descripcion;
		this.nroContrato = nroContrato;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}
	
	@Id 
	@Column(name = "id_empleado")
	public Long getIdEmpleado() {
		return idEmpleado;
	}
	public void setIdEmpleado(Long idEmpleado) {
		this.idEmpleado = idEmpleado;
	}
	
	@Column(name = "id_puesto")
	public Long getIdPuesto() {
		return idPuesto;
	}
	public void setIdPuesto(Long idPuesto) {
		this.idPuesto = idPuesto;
	}
	
	@Column(name = "id_persona")
	public Long getIdPersona() {
		return idPersona;
	}
	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}
	
	@Column(name = "id_pago")
	public Long getIdPago() {
		return idPago;
	}
	public void setIdPago(Long idPago) {
		this.idPago = idPago;
	}
	
	@Column(name = "nombre")
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	@Column(name = "apellido")
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	@Column(name = "descripcion")
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	@Column(name = "nro_contrato")
	public Integer getNroContrato() {
		return nroContrato;
	}
	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}
	
	@Column(name = "fecha_inicio", length = 29)
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	@Column(name = "fecha_fin", length = 29)
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	@Column(name = "monto")
	public int getMonto() {
		return this.monto;
	}

	public void setMonto(int monto) {
		this.monto = monto;
	}
	
	
}
