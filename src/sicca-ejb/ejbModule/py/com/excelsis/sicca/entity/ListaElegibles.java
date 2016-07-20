package py.com.excelsis.sicca.entity;

// Generated 30/08/2012 01:05:43 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import java.util.HashSet;
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

/**
 * ListaElegibles generated by hbm2java
 */
@Entity
@Table(name = "lista_elegibles", schema = "seleccion")
public class ListaElegibles implements java.io.Serializable {

	private Long idListaElegible;
	private Date vtoValidezLista;
	private Long idConcursoGrupoAgr;
	private Integer cantElegibles;
	private Long usuAlta;
	private Date fechaAlta;
	private Set<DetListaElegibles> detListaElegibleses = new HashSet<DetListaElegibles>(
			0);

	public ListaElegibles() {
	}

	public ListaElegibles(Long idListaElegible) {
		this.idListaElegible = idListaElegible;
	}

	public ListaElegibles(Long idListaElegible, Date vtoValidezLista,
			Long idConcursoGrupoAgr, Integer cantElegibles, Long usuAlta,
			Date fechaAlta, Set<DetListaElegibles> detListaElegibleses) {
		this.idListaElegible = idListaElegible;
		this.vtoValidezLista = vtoValidezLista;
		this.idConcursoGrupoAgr = idConcursoGrupoAgr;
		this.cantElegibles = cantElegibles;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.detListaElegibleses = detListaElegibleses;
	}

	@Id
	@GeneratedValue(generator="CAB_LISTA_ELEGIBLE_GENERATOR")
	@SequenceGenerator(name="CAB_LISTA_ELEGIBLE_GENERATOR",sequenceName="general.cab_validacion_id_cab_validacion_seq")
	@Column(name = "id_lista_elegible", unique = true, nullable = false)
	public Long getIdListaElegible() {
		return this.idListaElegible;
	}

	public void setIdListaElegible(Long idListaElegible) {
		this.idListaElegible = idListaElegible;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "vto_validez_lista", length = 29)
	public Date getVtoValidezLista() {
		return this.vtoValidezLista;
	}

	public void setVtoValidezLista(Date vtoValidezLista) {
		this.vtoValidezLista = vtoValidezLista;
	}

	@Column(name = "id_concurso_grupo_agr")
	public Long getIdConcursoGrupoAgr() {
		return this.idConcursoGrupoAgr;
	}

	public void setIdConcursoGrupoAgr(Long idConcursoGrupoAgr) {
		this.idConcursoGrupoAgr = idConcursoGrupoAgr;
	}

	@Column(name = "cant_elegibles")
	public Integer getCantElegibles() {
		return this.cantElegibles;
	}

	public void setCantElegibles(Integer cantElegibles) {
		this.cantElegibles = cantElegibles;
	}

	@Column(name = "usu_alta")
	public Long getUsuAlta() {
		return this.usuAlta;
	}

	public void setUsuAlta(Long usuAlta) {
		this.usuAlta = usuAlta;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta", length = 29)
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "listaElegibles")
	public Set<DetListaElegibles> getDetListaElegibleses() {
		return this.detListaElegibleses;
	}

	public void setDetListaElegibleses(
			Set<DetListaElegibles> detListaElegibleses) {
		this.detListaElegibleses = detListaElegibleses;
	}

}
