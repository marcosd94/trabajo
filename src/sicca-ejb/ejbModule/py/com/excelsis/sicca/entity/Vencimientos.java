package py.com.excelsis.sicca.entity;

// Generated 05/11/2012 03:13:42 PM by Hibernate Tools 3.4.0.Beta1

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
 * Remuneraciones generated by hbm2java
 */
@Entity
@Table(name = "vencimientos", schema = "remuneracion")
public class Vencimientos implements java.io.Serializable {

	private Long idVencimiento;
	private Integer anho;
	private Integer mes;
	private Date vencimiento;
	private Date prorroga;
    private boolean activo;
    private String usuAlta;
    private Date fechaAlta;
    private String usuMod;
    private Date fechaMod;

	public Vencimientos() {
	}

	public Vencimientos(Long idVencimiento, Integer anho, Integer mes, Date vencimiento, Date prorroga) {
		
		this.idVencimiento = idVencimiento;
		this.anho = anho;
		this.mes = mes;
		this.vencimiento = vencimiento;
		this.prorroga = prorroga;
	}

	@Id
	@Column(name = "id_vencimiento", unique = true, nullable = false)
	@GeneratedValue(generator="VENCIMIENTOS_GENERATOR")
	@SequenceGenerator(name="VENCIMIENTOS_GENERATOR",sequenceName="remuneracion.vencimientos_id_vencimiento_seq")
	public Long getIdVencimiento() {
		return this.idVencimiento;
	}

	public void setIdVencimiento(Long idVencimiento) {
		this.idVencimiento = idVencimiento;
	}
	
	@Column(name = "anho", nullable = false)
	@NotNull
	public Integer getAnho() {
		return this.anho;
	}

	public void setAnho(Integer anho) {
		this.anho = anho;
	}
	
	@Column(name = "mes", nullable = false)
	@NotNull
	public Integer getMes() {
		return this.mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}
	
	@Column(name = "activo", nullable = false)
	public boolean isActivo() {
		return this.activo;
	}
	
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "vencimiento", nullable = false, length = 29)
	@NotNull
	public Date getVencimiento() {
		return this.vencimiento;
	}

	public void setVencimiento(Date vencimiento) {
		this.vencimiento = vencimiento;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "prorroga", nullable = false, length = 29)
	@NotNull
	public Date getProrroga() {
		return this.prorroga;
	}

	public void setProrroga(Date prorroga) {
		this.prorroga = prorroga;
	}
	
	@Column(name = "usu_alta", length = 50)
	@Length(max = 50)
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta", length = 29)
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
	
}
