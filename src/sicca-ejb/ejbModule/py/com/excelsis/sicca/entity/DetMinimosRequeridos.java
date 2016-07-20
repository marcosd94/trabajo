package py.com.excelsis.sicca.entity;

// Generated 15-sep-2011 8:16:18 by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * DetMinimosRequeridos generated by hbm2java
 */
@Entity
@Table(name = "det_minimos_requeridos", schema = "planificacion")
public class DetMinimosRequeridos implements java.io.Serializable {

	
	
	private Long idMinimosRequeridos;
	private DetReqMin detReqMin;
	private String minimosRequeridos;
	private Boolean activo;
	protected boolean selected = false;

	public DetMinimosRequeridos() {
	}

	public DetMinimosRequeridos(Long idMinimosRequeridos,
			String minimosRequeridos, Boolean activo) {
		this.idMinimosRequeridos = idMinimosRequeridos;
		this.minimosRequeridos = minimosRequeridos;
		this.activo = activo;
	}

	public DetMinimosRequeridos(Long idMinimosRequeridos, DetReqMin detReqMin,
			String minimosRequeridos, Boolean activo) {
		this.idMinimosRequeridos = idMinimosRequeridos;
		this.detReqMin = detReqMin;
		this.minimosRequeridos = minimosRequeridos;
		this.activo = activo;
	}

	@Id
	@GeneratedValue(generator="DET_MINIMOS_REQUERIDOS_GENERATOR")
	@SequenceGenerator(name="DET_MINIMOS_REQUERIDOS_GENERATOR",sequenceName="planificacion.det_minimos_requeridos_id_minimos_requeridos_seq")
	@Column(name = "id_minimos_requeridos", unique = true, nullable = false)
	public Long getIdMinimosRequeridos() {
		return this.idMinimosRequeridos;
	}

	public void setIdMinimosRequeridos(Long idMinimosRequeridos) {
		this.idMinimosRequeridos = idMinimosRequeridos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_det_req_min")
	public DetReqMin getDetReqMin() {
		return this.detReqMin;
	}

	public void setDetReqMin(DetReqMin detReqMin) {
		this.detReqMin = detReqMin;
	}

	@Column(name = "minimos_requeridos", nullable = false)
	@NotNull
	
	public String getMinimosRequeridos() {
		return this.minimosRequeridos;
	}

	public void setMinimosRequeridos(String minimosRequeridos) {
		this.minimosRequeridos = minimosRequeridos;
	}

	@Column(name = "activo", nullable = false)
	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
    @Transient
    public boolean isSelected() 
    {
        return selected;
    }
    
    public void setSelected(boolean selected) 
    {
        this.selected = selected;
    }
	
	
}
