package py.com.excelsis.sicca.entity;

// Generated 14/10/2011 09:15:23 AM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * SinFue generated by hbm2java
 */
@Entity
@Table(name = "sin_fue", schema = "sinarh")
public class SinFue implements java.io.Serializable {

	private Integer idFue;
	private Integer aniAniopre;
	private Integer fueCodigo;
	private String fueNombre;
	private String fueNomabr;
	private String fueSigla;
	private String fueImputable;
	private String fueProcede;

	public SinFue() {
	}

	public SinFue(Integer idFue, Integer aniAniopre, Integer fueCodigo, String fueNombre,
			String fueNomabr, String fueSigla, String fueImputable,
			String fueProcede) {
		this.idFue = idFue;
		this.aniAniopre = aniAniopre;
		this.fueCodigo = fueCodigo;
		this.fueNombre = fueNombre;
		this.fueNomabr = fueNomabr;
		this.fueSigla = fueSigla;
		this.fueImputable = fueImputable;
		this.fueProcede = fueProcede;
	}

	@Id
	@Column(name = "id_fue", unique = true, nullable = false)
	public Integer getIdFue() {
		return this.idFue;
	}

	public void setIdFue(Integer idFue) {
		this.idFue = idFue;
	}

	@Column(name = "ani_aniopre", nullable = false)
	public Integer getAniAniopre() {
		return this.aniAniopre;
	}

	public void setAniAniopre(Integer aniAniopre) {
		this.aniAniopre = aniAniopre;
	}

	@Column(name = "fue_codigo", nullable = false)
	public Integer getFueCodigo() {
		return this.fueCodigo;
	}

	public void setFueCodigo(Integer fueCodigo) {
		this.fueCodigo = fueCodigo;
	}

	@Column(name = "fue_nombre", nullable = false, length = 200)
	@NotNull
	@Length(max = 200)
	public String getFueNombre() {
		return this.fueNombre;
	}

	public void setFueNombre(String fueNombre) {
		this.fueNombre = fueNombre;
	}

	@Column(name = "fue_nomabr", nullable = false, length = 100)
	@NotNull
	@Length(max = 100)
	public String getFueNomabr() {
		return this.fueNomabr;
	}

	public void setFueNomabr(String fueNomabr) {
		this.fueNomabr = fueNomabr;
	}

	@Column(name = "fue_sigla", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getFueSigla() {
		return this.fueSigla;
	}

	public void setFueSigla(String fueSigla) {
		this.fueSigla = fueSigla;
	}

	@Column(name = "fue_imputable", nullable = false, length = 10)
	@NotNull
	@Length(max = 10)
	public String getFueImputable() {
		return this.fueImputable;
	}

	public void setFueImputable(String fueImputable) {
		this.fueImputable = fueImputable;
	}

	@Column(name = "fue_procede", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getFueProcede() {
		return this.fueProcede;
	}

	public void setFueProcede(String fueProcede) {
		this.fueProcede = fueProcede;
	}

}
