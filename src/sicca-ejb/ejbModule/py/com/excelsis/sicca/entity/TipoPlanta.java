package py.com.excelsis.sicca.entity;


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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import py.com.excelsis.sicca.seguridad.entity.Usuario;
import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

/**
 * TipoPlanta generated by Generality
 */
@Entity
@Table(name = "tipo_planta", schema = "planificacion")
public class TipoPlanta extends EntityBase {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9015584458807843213L;
	private Long idTipoPlanta;
	private String descripcion;
	private Boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;

	private List<TipoNombramiento> tipoNombramientos = new ArrayList<TipoNombramiento>(
			0);

	public TipoPlanta() {
	}

	public TipoPlanta(Long idTipoPlanta, String descripcion, Boolean activo,
			String usuAlta, Date fechaAlta) {
		this.idTipoPlanta = idTipoPlanta;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public TipoPlanta(Long idTipoPlanta, String descripcion, Boolean activo,
			String usuAlta, Date fechaAlta, String usuMod, Date fechaMod,
			List<TipoNombramiento> tipoNombramientos) {
		this.idTipoPlanta = idTipoPlanta;
		this.descripcion = descripcion;
		this.activo = activo;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.usuMod = usuMod;
		this.fechaMod = fechaMod;
		this.tipoNombramientos = tipoNombramientos;
	}


	@Id
	@GeneratedValue(generator="TIPO_PLANTA_GENERATOR")
	@SequenceGenerator(name="TIPO_PLANTA_GENERATOR",sequenceName="planificacion.tipo_planta_id_tipo_planta_seq")

	@Column(name = "id_tipo_planta")
	public Long getIdTipoPlanta() {
		return this.idTipoPlanta;
	}

	public void setIdTipoPlanta(Long idTipoPlanta) {
		this.idTipoPlanta = idTipoPlanta;
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
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoPlanta")
	public List<TipoNombramiento> getTipoNombramientos() {
		return this.tipoNombramientos;
	}

	public void setTipoNombramientos(List<TipoNombramiento> tipoNombramientos) {
		this.tipoNombramientos = tipoNombramientos;
	}
	
	
	

	@Transient
	public Long getId() {
		return idTipoPlanta;
	}

	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		properties.put(SICCASessionParameters.TIPO_PLANTA_ID, getId());
		properties.put(SICCASessionParameters.TIPO_PLANTA_DESCRIPCION,
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
