package py.com.excelsis.sicca.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
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
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * PromocionSalarial generated by ecespedes
 */
@Entity
@Table(name = "promocion_salarial", schema = "seleccion")
public class PromocionSalarial  implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7755893856005534546L;
	
	private Long idPromocionSalarial;
	private Cpt cpt;
	private String categoria;
	private Long monto;
	private Long objCodigo;
	private Long anho;
	private Boolean disponible;
	private Integer orden;
	private EstadoCab estadoCab;
	private ConfiguracionUoCab configuracionUoCab;
	private String descripcion;
	private String usuAlta;
	private String usuMod;
	private Date fechaMod;
	private Date fechaAlta;
	private Boolean activo;
	private Boolean seleccionado;
	
	
	
	
	public PromocionSalarial() {
	}

	public PromocionSalarial(
			Long idPlantaCargoDet, Cpt cpt, Oficina oficina, ConfiguracionUoCab configuracionUoCab,
			String descripcion, Boolean contratado, String usuAlta, Date fechaAlta, Boolean activo
		) {
		this.idPromocionSalarial = idPlantaCargoDet;
		this.cpt = cpt;
		this.configuracionUoCab = configuracionUoCab;
		this.descripcion = descripcion;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.activo = activo;
		
	}

	public PromocionSalarial(
			Long idPlantaCargoDet, Cpt cpt, EstadoCab estadoCab,
			ConfiguracionUoCab configuracionUoCab, String descripcion,
			String usuAlta, String usuMod, Date fechaMod, Date fechaAlta,
			Boolean activo) {
		this.idPromocionSalarial = idPlantaCargoDet;
		this.cpt = cpt;
		
		this.estadoCab = estadoCab;
		this.configuracionUoCab = configuracionUoCab;
		this.descripcion = descripcion;
		
		this.usuAlta = usuAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.fechaAlta = fechaAlta;
		this.activo = activo;
		
	}

	@Id
	@GeneratedValue(generator = "PROMOCION_SALARIAL_GENERATOR")
	@SequenceGenerator(name = "PROMOCION_SALARIAL_GENERATOR", sequenceName = "seleccion.promocion_salarial_id_promocion_salarial_seq")
	@Column(name = "id_promocion_salarial")
	public Long getIdPromocionSalarial() {
		return this.idPromocionSalarial;
	}

	public void setIdPromocionSalarial(Long idPromocionSalarial) {
		this.idPromocionSalarial = idPromocionSalarial;
	}
	
	@Column(name = "categoria")
	@Length(max = 60, message = "#{messages.GENERICO_NO_SUPERAR_MAX_LEN_TEXTO}")
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Column(name = "monto")
	public Long getMonto() {
		return monto;
	}

	public void setMonto(Long monto) {
		this.monto = monto;
	}
	@Column(name = "obj_codigo")
	public Long getObjCodigo() {
		return objCodigo;
	}

	public void setObjCodigo(Long objCodigo) {
		this.objCodigo = objCodigo;
	}

	
	@Column(name = "anho")
	public Long getAnho() {
		return anho;
	}

	public void setAnho(Long anho) {
		this.anho = anho;
	}
	
	@Column(name = "disponible")
	public Boolean getDisponible() {
		return disponible;
	}

	public void setDisponible(Boolean disponible) {
		this.disponible = disponible;
	}
	
	@Column(name = "orden")
	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	@Column(name = "descripcion")
	@Length(max = 200, message = "#{messages.GENERICO_NO_SUPERAR_MAX_LEN_TEXTO}")
	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "activo")
	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_configuracion_uo_cab")
	public ConfiguracionUoCab getConfiguracionUoCab() {
		return this.configuracionUoCab;
	}

	public void setConfiguracionUoCab(ConfiguracionUoCab configuracionUoCab) {
		this.configuracionUoCab = configuracionUoCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_cab")
	public EstadoCab getEstadoCab() {
		return this.estadoCab;
	}

	public void setEstadoCab(EstadoCab estadoCab) {
		this.estadoCab = estadoCab;
	}

	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cpt")
	public Cpt getCpt() {
		return this.cpt;
	}

	public void setCpt(Cpt cpt) {
		this.cpt = cpt;
	}

	@Transient
	public Boolean getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	

}