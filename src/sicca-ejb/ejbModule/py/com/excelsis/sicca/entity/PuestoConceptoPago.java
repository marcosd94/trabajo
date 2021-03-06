package py.com.excelsis.sicca.entity;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * PuestoConceptoPago generated by Generality
 */
@Entity
@Table(name = "puesto_concepto_pago", schema = "planificacion")
public class PuestoConceptoPago extends EntityBase {
	private Long idPuestoConceptoPago;
	private PlantaCargoDet plantaCargoDet;
	private Integer monto;
	private String usuAlta;
	private String usuMod;
	private Date fechaAlta;
	private Date fechaMod;
	private String categoria;
	private Integer objCodigo;
	private Integer anho;
	private Boolean disponible;
	private Boolean activo;
	private Integer estado;

	public PuestoConceptoPago() {
	}

	public PuestoConceptoPago(Long idPuestoConceptoPago) {
		this.idPuestoConceptoPago = idPuestoConceptoPago;
	}

	@Id
	@GeneratedValue(generator="PUESTO_CONCEPTO_PAGO_GENERATOR")
	@SequenceGenerator(name="PUESTO_CONCEPTO_PAGO_GENERATOR",sequenceName="planificacion.puesto_concepto_pago_id_puesto_concepto_pago_seq")

	@Column(name = "id_puesto_concepto_pago")
	public Long getIdPuestoConceptoPago() {
		return this.idPuestoConceptoPago;
	}

	public void setIdPuestoConceptoPago(Long idPuestoConceptoPago) {
		this.idPuestoConceptoPago = idPuestoConceptoPago;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_planta_cargo_det")
	public PlantaCargoDet getPlantaCargoDet() {
		return this.plantaCargoDet;
	}

	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}
	
	@Column(name = "monto")
	public Integer getMonto() {
		return this.monto;
	}

	public void setMonto(Integer monto) {
		this.monto = monto;
	}
	

	@Column(name = "usu_alta")
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}
	

	@Column(name = "usu_mod")
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Column(name = "categoria")
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Column(name = "obj_codigo")
	public Integer getObjCodigo() {
		return objCodigo;
	}

	public void setObjCodigo(Integer objCodigo) {
		this.objCodigo = objCodigo;
	}
	
	@Column(name = "anho")
	public Integer getAnho() {
		return anho;
	}


	public void setAnho(Integer anho) {
		this.anho = anho;
	}
	
	@Column(name = "disponible")
	public Boolean getDisponible() {
		return disponible;
	}

	public void setDisponible(Boolean disponible) {
		this.disponible = disponible;
	}

	@Transient
	public Long getId() {
		return idPuestoConceptoPago;
	}

	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.PUESTO_CONCEPTO_PAGO_ID, getId());
		properties.put(SICCASessionParameters.PUESTO_CONCEPTO_PAGO_CATEGORIA,
				categoria);

		return properties;
	}

	/**
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}
	
	@Column(name = "activo")
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	@Column(name = "estado")
	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	
	
	
}
