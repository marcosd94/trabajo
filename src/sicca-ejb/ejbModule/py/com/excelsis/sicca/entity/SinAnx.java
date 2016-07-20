package py.com.excelsis.sicca.entity;

// Generated 22/09/2011 10:45:35 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * SinAnx generated by hbm2java
 */
@Entity
@Table(name = "sin_anx", schema = "sinarh")
public class SinAnx implements java.io.Serializable {

	private Long idAnx;
	private Integer aniAniopre;
	private Integer nenCodigo;
	private Integer entCodigo;
	private Integer tipCodigo;
	private Integer proCodigo;
	private Integer subCodigo;
	private Integer pryCodigo;
	private Integer objCodigo;
	private Integer fueCodigo;
	private Integer finCodigo;
	private Integer vrsCodigo;
	private Integer anxLinea;
	private Integer catGrupo;
	private String ctgCodigo;
	private String mdfCodigo;
	private String anxTiporeg;
	private Integer anxMesaplic;
	private Integer anxDurac;
	private Long anxVlran;
	private Integer anxCargos;
	private String anxDescrip;
	private Integer paiCodigo;
	private Integer dptCodigo;
	private Integer cantDisponible;
	private String estado;
	private Date fechaAlta;

	public SinAnx() {
	}

	public SinAnx(
			Long idAnx, Integer aniAniopre, Integer nenCodigo, Integer entCodigo,
			Integer tipCodigo, Integer proCodigo, Integer subCodigo, Integer pryCodigo,
			Integer objCodigo, Integer fueCodigo, Integer finCodigo, Integer vrsCodigo,
			Integer anxLinea, Integer catGrupo, String ctgCodigo, String mdfCodigo,
			String anxTiporeg, Integer anxMesaplic, Integer anxDurac, Long anxVlran,
			Integer anxCargos, String anxDescrip, Integer paiCodigo, Integer dptCodigo) {
		this.idAnx = idAnx;
		this.aniAniopre = aniAniopre;
		this.nenCodigo = nenCodigo;
		this.entCodigo = entCodigo;
		this.tipCodigo = tipCodigo;
		this.proCodigo = proCodigo;
		this.subCodigo = subCodigo;
		this.pryCodigo = pryCodigo;
		this.objCodigo = objCodigo;
		this.fueCodigo = fueCodigo;
		this.finCodigo = finCodigo;
		this.vrsCodigo = vrsCodigo;
		this.anxLinea = anxLinea;
		this.catGrupo = catGrupo;
		this.ctgCodigo = ctgCodigo;
		this.mdfCodigo = mdfCodigo;
		this.anxTiporeg = anxTiporeg;
		this.anxMesaplic = anxMesaplic;
		this.anxDurac = anxDurac;
		this.anxVlran = anxVlran;
		this.anxCargos = anxCargos;
		this.anxDescrip = anxDescrip;
		this.paiCodigo = paiCodigo;
		this.dptCodigo = dptCodigo;
	}

	@Id
	@Column(name = "id_anx", unique = true, nullable = false)
	@GeneratedValue(generator = "SINANX_GENERATOR")
	@SequenceGenerator(name = "SINANX_GENERATOR", sequenceName = "sinarh.sin_anx_id_anx_seq")
	public Long getIdAnx() {
		return this.idAnx;
	}

	public void setIdAnx(Long idAnx) {
		this.idAnx = idAnx;
	}

	@Column(name = "ani_aniopre" )
	public Integer getAniAniopre() {
		return this.aniAniopre;
	}

	public void setAniAniopre(Integer aniAniopre) {
		this.aniAniopre = aniAniopre;
	}

	@Column(name = "nen_codigo" )
	public Integer getNenCodigo() {
		return this.nenCodigo;
	}

	public void setNenCodigo(Integer nenCodigo) {
		this.nenCodigo = nenCodigo;
	}

	@Column(name = "ent_codigo" )
	public Integer getEntCodigo() {
		return this.entCodigo;
	}

	public void setEntCodigo(Integer entCodigo) {
		this.entCodigo = entCodigo;
	}

	@Column(name = "tip_codigo" )
	public Integer getTipCodigo() {
		return this.tipCodigo;
	}

	public void setTipCodigo(Integer tipCodigo) {
		this.tipCodigo = tipCodigo;
	}

	@Column(name = "pro_codigo" )
	public Integer getProCodigo() {
		return this.proCodigo;
	}

	public void setProCodigo(Integer proCodigo) {
		this.proCodigo = proCodigo;
	}

	@Column(name = "sub_codigo" )
	public Integer getSubCodigo() {
		return this.subCodigo;
	}

	public void setSubCodigo(Integer subCodigo) {
		this.subCodigo = subCodigo;
	}

	@Column(name = "pry_codigo" )
	public Integer getPryCodigo() {
		return this.pryCodigo;
	}

	public void setPryCodigo(Integer pryCodigo) {
		this.pryCodigo = pryCodigo;
	}

	@Column(name = "obj_codigo" )
	public Integer getObjCodigo() {
		return this.objCodigo;
	}

	public void setObjCodigo(Integer objCodigo) {
		this.objCodigo = objCodigo;
	}

	@Column(name = "fue_codigo" )
	public Integer getFueCodigo() {
		return this.fueCodigo;
	}

	public void setFueCodigo(Integer fueCodigo) {
		this.fueCodigo = fueCodigo;
	}

	@Column(name = "fin_codigo" )
	public Integer getFinCodigo() {
		return this.finCodigo;
	}

	public void setFinCodigo(Integer finCodigo) {
		this.finCodigo = finCodigo;
	}

	@Column(name = "vrs_codigo" )
	public Integer getVrsCodigo() {
		return this.vrsCodigo;
	}

	public void setVrsCodigo(Integer vrsCodigo) {
		this.vrsCodigo = vrsCodigo;
	}

	@Column(name = "anx_linea" )
	public Integer getAnxLinea() {
		return this.anxLinea;
	}

	public void setAnxLinea(Integer anxLinea) {
		this.anxLinea = anxLinea;
	}

	@Column(name = "cat_grupo" )
	public Integer getCatGrupo() {
		return this.catGrupo;
	}

	public void setCatGrupo(Integer catGrupo) {
		this.catGrupo = catGrupo;
	}

	@Column(name = "ctg_codigo" , length = 20)
	@Length(max = 20)
	public String getCtgCodigo() {
		return this.ctgCodigo;
	}

	public void setCtgCodigo(String ctgCodigo) {
		this.ctgCodigo = ctgCodigo;
	}

	@Column(name = "mdf_codigo" , length = 20)
	@Length(max = 20)
	public String getMdfCodigo() {
		return this.mdfCodigo;
	}

	public void setMdfCodigo(String mdfCodigo) {
		this.mdfCodigo = mdfCodigo;
	}

	@Column(name = "anx_tiporeg" , length = 10)	 
	@Length(max = 10)
	public String getAnxTiporeg() {
		return this.anxTiporeg;
	}

	public void setAnxTiporeg(String anxTiporeg) {
		this.anxTiporeg = anxTiporeg;
	}

	@Column(name = "anx_mesaplic" )
	public Integer getAnxMesaplic() {
		return this.anxMesaplic;
	}

	public void setAnxMesaplic(Integer anxMesaplic) {
		this.anxMesaplic = anxMesaplic;
	}

	@Column(name = "anx_durac" )
	public Integer getAnxDurac() {
		return this.anxDurac;
	}

	public void setAnxDurac(Integer anxDurac) {
		this.anxDurac = anxDurac;
	}

	@Column(name = "anx_vlran" )
	public Long getAnxVlran() {
		return this.anxVlran;
	}

	public void setAnxVlran(Long anxVlran) {
		this.anxVlran = anxVlran;
	}

	@Column(name = "anx_cargos" )
	public Integer getAnxCargos() {
		return this.anxCargos;
	}

	public void setAnxCargos(Integer anxCargos) {
		this.anxCargos = anxCargos;
	}

	@Column(name = "anx_descrip" , length = 200)
 
	@Length(max = 200)
	public String getAnxDescrip() {
		return this.anxDescrip;
	}

	public void setAnxDescrip(String anxDescrip) {
		this.anxDescrip = anxDescrip;
	}

	@Column(name = "pai_codigo" )
	public Integer getPaiCodigo() {
		return this.paiCodigo;
	}

	public void setPaiCodigo(Integer paiCodigo) {
		this.paiCodigo = paiCodigo;
	}

	@Column(name = "dpt_codigo" )
	public Integer getDptCodigo() {
		return this.dptCodigo;
	}

	public void setDptCodigo(Integer dptCodigo) {
		this.dptCodigo = dptCodigo;
	}

	@Column(name = "cant_disponible")
	public Integer getCantDisponible() {
		return cantDisponible;
	}

	public void setCantDisponible(Integer cantDisponible) {
		this.cantDisponible = cantDisponible;
	}

	@Transient
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta", nullable = false, length = 29)
	@NotNull
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

}