package py.com.excelsis.sicca.entity;



import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.Length;

import py.com.excelsis.sicca.util.EXCProperties;
import py.com.excelsis.sicca.util.EntityBase;
import py.com.excelsis.sicca.util.SICCASessionParameters;

@Entity
@Table(name = "cpt_obs", schema = "planificacion")
public class CptObs extends EntityBase  
{
	
	private static final long serialVersionUID = 1L;
	
	private Long idCptObs;
	private Cpt cpt;
	private String observacionSfp;
	private String usuObservacionSfp;
	private Date fechaObservacionSfp;
	private String respuestaOee;
	private String usuRespuestaOee;
	private Date fechaRespuestaOee;
	private boolean activo;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private Boolean envioObservacion;

	public CptObs() {
	}
	
	@Id
	@GeneratedValue(generator="CPT_OBS_GENERATOR")
	@SequenceGenerator(name="CPT_OBS_GENERATOR",sequenceName="planificacion.cpt_obs_id_cpt_obs_seq")

	@Column(name = "id_cpt_obs")
	public Long getIdCptObs() {
		return idCptObs;
	}

	public void setIdCptObs(Long idCptObs) {
		this.idCptObs = idCptObs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cpt")
	public Cpt getCpt() {
		return cpt;
	}

	public void setCpt(Cpt idCpt) {
		this.cpt = idCpt;
	}

	
	@Column(name = "observacion_sfp")
	public String getObservacionSfp() {
		return observacionSfp;
	}

	public void setObservacionSfp(String observacionSfp) {
		this.observacionSfp = observacionSfp;
	}

	@Column(name = "usu_observacion_sfp")
	public String getUsuObservacionSfp() {
		return usuObservacionSfp;
	}

	public void setUsuObservacionSfp(String usuObservacionSfp) {
		this.usuObservacionSfp = usuObservacionSfp;
	}

	@Column(name = "fecha_observacion_sfp")
	public Date getFechaObservacionSfp() {
		return fechaObservacionSfp;
	}

	public void setFechaObservacionSfp(Date fechaObservacionSfp) {
		this.fechaObservacionSfp = fechaObservacionSfp;
	}

	@Column(name = "respuesta_oee")
	public String getRespuestaOee() {
		return respuestaOee;
	}

	public void setRespuestaOee(String respuestaOee) {
		this.respuestaOee = respuestaOee;
	}

	@Column(name = "usu_respuesta_oee")
	public String getUsuRespuestaOee() {
		return usuRespuestaOee;
	}

	public void setUsuRespuestaOee(String usuRespuestaOee) {
		this.usuRespuestaOee = usuRespuestaOee;
	}

	@Column(name = "fecha_respuesta_oee")
	public Date getFechaRespuestaOee() {
		return fechaRespuestaOee;
	}

	public void setFechaRespuestaOee(Date fechaRespuestaOee) {
		this.fechaRespuestaOee = fechaRespuestaOee;
	}

	@Column(name = "activo")
	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	@Column(name = "envio_observacion")
	public Boolean isEnvioObservacion() {
		if(envioObservacion == null)
			return false;
		else
			return envioObservacion;
	}

	public void setEnvioObservacion(Boolean envioObservacion) {
		this.envioObservacion = envioObservacion;
	}

	@Column(name = "usu_alta")
	public String getUsuAlta() {
		return usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
	}

	@Column(name = "fecha_alta")
	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "usu_mod")
	public String getUsuMod() {
		return usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}

	@Column(name = "fecha_mod")
	public Date getFechaMod() {
		return fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Transient
	public Long getId() {
		return idCptObs;
	}

	/**
	 * 
	 * @return properties
	 */
	@Transient
	public Properties getProperties() {
		Properties properties = new EXCProperties();
		//properties.put(SICCASessionParameters.CPT_ID, getId());
		

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
