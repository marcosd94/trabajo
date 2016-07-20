package py.com.excelsis.sicca.entity;

// Generated 12-dic-2011 17:04:12 by Hibernate Tools 3.4.0.Beta1

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * SolicProrrogaCab generated by hbm2java
 */
@Entity
@Table(name = "solic_prorroga_cab", schema = "seleccion")
public class SolicProrrogaCab implements java.io.Serializable {

	private Long idSolicCab;
	private CronogramaConcCab cronogramaConcCab;
	private FechasGrupoPuesto fechasGrupoPuesto;
	private String tipo;
	private String estado;
	private Integer nroSol;
	private Date fechaSol;
	private Date fechaVigHastaAnt;
	private Integer cantDiasProrrog;
	private Date fechaVigHastaNuev;
	private String usuAlta;
	private Date fechaAlta;
	private String usuMod;
	private Date fechaMod;
	private String usuPublic;
	private Date fechaPublic;
	private List<SolicProrrogaDet> solicProrrogaDets = new ArrayList<SolicProrrogaDet>(0);

	public SolicProrrogaCab() {
	}

	
	
	
	@Id
	@GeneratedValue(generator="solic_prorroga_cab")
	@SequenceGenerator(name="solic_prorroga_cab",sequenceName="seleccion.solic_prorroga_cab_id_solic_cab_seq")
	@Column(name = "id_solic_cab", unique = true, nullable = false)
	public Long getIdSolicCab() {
		return this.idSolicCab;
	}

	public void setIdSolicCab(Long idSolicCab) {
		this.idSolicCab = idSolicCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cronograma_conc_cab")
	public CronogramaConcCab getCronogramaConcCab() {
		return this.cronogramaConcCab;
	}

	public void setCronogramaConcCab(CronogramaConcCab cronogramaConcCab) {
		this.cronogramaConcCab = cronogramaConcCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_fechas", nullable = false)
	@NotNull
	public FechasGrupoPuesto getFechasGrupoPuesto() {
		return this.fechasGrupoPuesto;
	}

	public void setFechasGrupoPuesto(FechasGrupoPuesto fechasGrupoPuesto) {
		this.fechasGrupoPuesto = fechasGrupoPuesto;
	}

	@Column(name = "tipo", nullable = false, length = 20)
	@NotNull
	@Length(max = 20)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Column(name = "estado", nullable = false, length = 20)
	@NotNull
	@Length(max = 20)
	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Column(name = "nro_sol")
	public Integer getNroSol() {
		return this.nroSol;
	}

	public void setNroSol(Integer nroSol) {
		this.nroSol = nroSol;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_sol", nullable = false, length = 29)
	@NotNull
	public Date getFechaSol() {
		return this.fechaSol;
	}

	public void setFechaSol(Date fechaSol) {
		this.fechaSol = fechaSol;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_vig_hasta_ant", length = 29)
	public Date getFechaVigHastaAnt() {
		return this.fechaVigHastaAnt;
	}

	public void setFechaVigHastaAnt(Date fechaVigHastaAnt) {
		this.fechaVigHastaAnt = fechaVigHastaAnt;
	}

	@Column(name = "cant_dias_prorrog")
	public Integer getCantDiasProrrog() {
		return this.cantDiasProrrog;
	}

	public void setCantDiasProrrog(Integer cantDiasProrrog) {
		this.cantDiasProrrog = cantDiasProrrog;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_vig_hasta_nuev", length = 29)
	public Date getFechaVigHastaNuev() {
		return this.fechaVigHastaNuev;
	}

	public void setFechaVigHastaNuev(Date fechaVigHastaNuev) {
		this.fechaVigHastaNuev = fechaVigHastaNuev;
	}

	@Column(name = "usu_alta", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(String usuAlta) {
		this.usuAlta = usuAlta;
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

	@Column(name = "usu_mod", length = 50)
	@Length(max = 50)
	public String getUsuMod() {
		return this.usuMod;
	}

	public void setUsuMod(String usuMod) {
		this.usuMod = usuMod;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_mod", length = 29)
	public Date getFechaMod() {
		return this.fechaMod;
	}

	public void setFechaMod(Date fechaMod) {
		this.fechaMod = fechaMod;
	}

	@Column(name = "usu_public", length = 50)
	@Length(max = 50)
	public String getUsuPublic() {
		return this.usuPublic;
	}

	public void setUsuPublic(String usuPublic) {
		this.usuPublic = usuPublic;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_public", length = 29)
	public Date getFechaPublic() {
		return this.fechaPublic;
	}

	public void setFechaPublic(Date fechaPublic) {
		this.fechaPublic = fechaPublic;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "solicProrrogaCab")
	public List<SolicProrrogaDet> getSolicProrrogaDets() {
		return this.solicProrrogaDets;
	}

	public void setSolicProrrogaDets(List<SolicProrrogaDet> solicProrrogaDets) {
		this.solicProrrogaDets = solicProrrogaDets;
	}

}
