package py.com.excelsis.sicca.entity;

// Generated 11/03/2013 02:41:42 PM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * EquivalenciasLegajo generated by hbm2java
 */
@Entity
@Table(name = "equivalencias_legajo", schema = "legajo")
public class EquivalenciasLegajo implements java.io.Serializable {

	private Long idRelacion;
	private String tablaLegajo;
	private String tablaSicca;
	private Long codLegajo;
	private Long codSicca;
	private String codLegajoChar;
	private String codSiccaChar;

	public EquivalenciasLegajo() {
	}

	public EquivalenciasLegajo(Long idRelacion, String tablaLegajo, String tablaSicca) {
		this.idRelacion = idRelacion;
		this.tablaLegajo = tablaLegajo;
		this.tablaSicca = tablaSicca;
	}

	public EquivalenciasLegajo(
			Long idRelacion, String tablaLegajo, String tablaSicca, Long codLegajo, Long codSicca,
			String codLegajoChar, String codSiccaChar) {
		this.idRelacion = idRelacion;
		this.tablaLegajo = tablaLegajo;
		this.tablaSicca = tablaSicca;
		this.codLegajo = codLegajo;
		this.codSicca = codSicca;
		this.codLegajoChar = codLegajoChar;
		this.codSiccaChar = codSiccaChar;
	}

	@Id
	@Column(name = "id_relacion", unique = true, nullable = false)
	@GeneratedValue(generator = "EQUIVALENCIAS_LEGAJO_GENERATOR")
	@SequenceGenerator(name = "EQUIVALENCIAS_LEGAJO_GENERATOR", sequenceName = "legajo.id_relacion_seq")
	public Long getIdRelacion() {
		return this.idRelacion;
	}

	public void setIdRelacion(Long idRelacion) {
		this.idRelacion = idRelacion;
	}

	@Column(name = "tabla_legajo", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getTablaLegajo() {
		return this.tablaLegajo;
	}

	public void setTablaLegajo(String tablaLegajo) {
		this.tablaLegajo = tablaLegajo;
	}

	@Column(name = "tabla_sicca", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getTablaSicca() {
		return this.tablaSicca;
	}

	public void setTablaSicca(String tablaSicca) {
		this.tablaSicca = tablaSicca;
	}

	@Column(name = "cod_legajo")
	public Long getCodLegajo() {
		return this.codLegajo;
	}

	public void setCodLegajo(Long codLegajo) {
		this.codLegajo = codLegajo;
	}

	@Column(name = "cod_sicca")
	public Long getCodSicca() {
		return this.codSicca;
	}

	public void setCodSicca(Long codSicca) {
		this.codSicca = codSicca;
	}

	@Column(name = "cod_legajo_char", length = 50)
	@Length(max = 50)
	public String getCodLegajoChar() {
		return this.codLegajoChar;
	}

	public void setCodLegajoChar(String codLegajoChar) {
		this.codLegajoChar = codLegajoChar;
	}

	@Column(name = "cod_sicca_char", length = 50)
	@Length(max = 50)
	public String getCodSiccaChar() {
		return this.codSiccaChar;
	}

	public void setCodSiccaChar(String codSiccaChar) {
		this.codSiccaChar = codSiccaChar;
	}

}
