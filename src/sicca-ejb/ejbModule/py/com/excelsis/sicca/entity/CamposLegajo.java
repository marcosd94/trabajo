package py.com.excelsis.sicca.entity;

// Generated 15-oct-2012 15:52:09 by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * CamposLegajo generated by hbm2java
 */
@Entity
@Table(name = "campos_legajo", schema = "legajo")
public class CamposLegajo implements java.io.Serializable {

	private Long idCamposLegajo;
	private Long ficha;
	private String tabla;
	private String campo;

	public CamposLegajo() {
	}

	public CamposLegajo(Long idCamposLegajo, Long ficha) {
		this.idCamposLegajo = idCamposLegajo;
		this.ficha = ficha;
	}

	public CamposLegajo(Long idCamposLegajo, Long ficha, String tabla,
			String campo) {
		this.idCamposLegajo = idCamposLegajo;
		this.ficha = ficha;
		this.tabla = tabla;
		this.campo = campo;
	}

	@Id
	@GeneratedValue(generator = "CAMPOS_LEGAJO_GENERATOR")
	@SequenceGenerator(name = "CAMPOS_LEGAJO_GENERATOR", sequenceName = "legajo.campos_legajo_id_campos_legajo_seq")
	@Column(name = "id_campos_legajo", unique = true, nullable = false)
	public Long getIdCamposLegajo() {
		return this.idCamposLegajo;
	}

	public void setIdCamposLegajo(Long idCamposLegajo) {
		this.idCamposLegajo = idCamposLegajo;
	}

	@Column(name = "ficha", nullable = false)
	public Long getFicha() {
		return this.ficha;
	}

	public void setFicha(Long ficha) {
		this.ficha = ficha;
	}

	@Column(name = "tabla")
	public String getTabla() {
		return this.tabla;
	}

	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	@Column(name = "campo")
	public String getCampo() {
		return this.campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

}
