package py.com.excelsis.sicca.entity;

// Generated 14/10/2011 09:15:23 AM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * SinTip generated by hbm2java
 */
@Entity
@Table(name = "sin_tip", schema = "sinarh")
public class SinTip implements java.io.Serializable {

	private Integer idTip;
	private Integer aniAniopre;
	private Integer tipCodigo;
	private String tipNombre;
	private String tipNomabr;

	public SinTip() {
	}

	public SinTip(Integer idTip, Integer aniAniopre, Integer tipCodigo, String tipNombre,
			String tipNomabr) {
		this.idTip = idTip;
		this.aniAniopre = aniAniopre;
		this.tipCodigo = tipCodigo;
		this.tipNombre = tipNombre;
		this.tipNomabr = tipNomabr;
	}

	@Id
	@Column(name = "id_tip", unique = true, nullable = false)
	public Integer getIdTip() {
		return this.idTip;
	}

	public void setIdTip(Integer idTip) {
		this.idTip = idTip;
	}

	@Column(name = "ani_aniopre", nullable = false)
	public Integer getAniAniopre() {
		return this.aniAniopre;
	}

	public void setAniAniopre(Integer aniAniopre) {
		this.aniAniopre = aniAniopre;
	}

	@Column(name = "tip_codigo", nullable = false)
	public Integer getTipCodigo() {
		return this.tipCodigo;
	}

	public void setTipCodigo(Integer tipCodigo) {
		this.tipCodigo = tipCodigo;
	}

	@Column(name = "tip_nombre", nullable = false, length = 200)
	@NotNull
	@Length(max = 200)
	public String getTipNombre() {
		return this.tipNombre;
	}

	public void setTipNombre(String tipNombre) {
		this.tipNombre = tipNombre;
	}

	@Column(name = "tip_nomabr", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getTipNomabr() {
		return this.tipNomabr;
	}

	public void setTipNomabr(String tipNomabr) {
		this.tipNomabr = tipNomabr;
	}

}