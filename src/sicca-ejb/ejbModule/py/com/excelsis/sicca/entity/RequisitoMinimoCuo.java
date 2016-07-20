package py.com.excelsis.sicca.entity;


import java.util.Date;

import java.util.Properties;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * RequisitoMinimoCuo generated by Generality
 */
@Entity
@Table(name = "requisito_minimo_cuo", schema = "planificacion")
public class RequisitoMinimoCuo extends EntityBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6657431929304553700L;
	private Long idRequisitoMinimoCuo;
	private String descripcion;
	private Boolean activo;
	private Integer orden;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	public RequisitoMinimoCuo() {
	}

	public RequisitoMinimoCuo(Long idRequisitoMinimoCuo) {
		this.idRequisitoMinimoCuo = idRequisitoMinimoCuo;
	}

	@Id
	@GeneratedValue(generator="REQUISITO_MINIMO_CUO_GENERATOR")
	@SequenceGenerator(name="REQUISITO_MINIMO_CUO_GENERATOR",sequenceName="planificacion.requisito_minimo_cuo_id_requisito_minimo_cuo_seq")

	@Column(name = "id_requisito_minimo_cuo")
	public Long getIdRequisitoMinimoCuo() {
		return this.idRequisitoMinimoCuo;
	}

	public void setIdRequisitoMinimoCuo(Long idRequisitoMinimoCuo) {
		this.idRequisitoMinimoCuo = idRequisitoMinimoCuo;
	}
	

	@Column(name = "descripcion")
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	

	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	

	@Column(name = "orden")
	public Integer getOrden() {
		return this.orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	

	@Column(name = "usu_alta")
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	

	@Column(name = "usu_mod")
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}
	
	@Transient
	public Long getId() {
		return idRequisitoMinimoCuo;
	}

	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.REQUISITO_MINIMO_CUO_ID, getId());
		properties.put(SICCASessionParameters.REQUISITO_MINIMO_CUO_DESCRIPCION,
				descripcion);

		return properties;
	}

	/**
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub

	}
}