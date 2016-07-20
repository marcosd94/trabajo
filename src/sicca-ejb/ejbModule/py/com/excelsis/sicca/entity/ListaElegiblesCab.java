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
 * ListaElegiblesCab generated by hbm2java
 */
@Entity
@Table(name = "lista_elegibles_cab", schema = "seleccion")
public class ListaElegiblesCab implements java.io.Serializable {

	private Long idListaElegiblesCab;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Date vtoValidezLista;
	private int cantElegibles;
	private String usuAlta;
	private Date fechaAlta;
	private Set<ListaElegiblesDet> listaElegiblesDets = new HashSet<ListaElegiblesDet>(0);

	public ListaElegiblesCab() {
	}

	public ListaElegiblesCab(
			Long idListaElegiblesCab, ConcursoPuestoAgr concursoPuestoAgr, Date vtoValidezLista,
			int cantElegibles, String usuAlta, Date fechaAlta) {
		this.idListaElegiblesCab = idListaElegiblesCab;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.vtoValidezLista = vtoValidezLista;
		this.cantElegibles = cantElegibles;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
	}

	public ListaElegiblesCab(
			Long idListaElegiblesCab, ConcursoPuestoAgr concursoPuestoAgr, Date vtoValidezLista,
			int cantElegibles, String usuAlta, Date fechaAlta,
			Set<ListaElegiblesDet> listaElegiblesDets) {
		this.idListaElegiblesCab = idListaElegiblesCab;
		this.concursoPuestoAgr = concursoPuestoAgr;
		this.vtoValidezLista = vtoValidezLista;
		this.cantElegibles = cantElegibles;
		this.usuAlta = usuAlta;
		this.fechaAlta = fechaAlta;
		this.listaElegiblesDets = listaElegiblesDets;
	}

	@Id
	@Column(name = "id_lista_elegibles_cab", unique = true, nullable = false)
	@GeneratedValue(generator = "LISTA_ELEGIBLES_CAB_GENERATOR")
	@SequenceGenerator(name = "LISTA_ELEGIBLES_CAB_GENERATOR", sequenceName = "seleccion.lista_elegibles_cab_seq")
	public Long getIdListaElegiblesCab() {
		return this.idListaElegiblesCab;
	}

	public void setIdListaElegiblesCab(Long idListaElegiblesCab) {
		this.idListaElegiblesCab = idListaElegiblesCab;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_concurso_puesto_agr", nullable = false)
	@NotNull
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return this.concursoPuestoAgr;
	}

	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "vto_validez_lista", nullable = false, length = 29)
	@NotNull
	public Date getVtoValidezLista() {
		return this.vtoValidezLista;
	}

	public void setVtoValidezLista(Date vtoValidezLista) {
		this.vtoValidezLista = vtoValidezLista;
	}

	@Column(name = "cant_elegibles", nullable = false)
	public int getCantElegibles() {
		return this.cantElegibles;
	}

	public void setCantElegibles(int cantElegibles) {
		this.cantElegibles = cantElegibles;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "listaElegiblesCab")
	public Set<ListaElegiblesDet> getListaElegiblesDets() {
		return this.listaElegiblesDets;
	}

	public void setListaElegiblesDets(Set<ListaElegiblesDet> listaElegiblesDets) {
		this.listaElegiblesDets = listaElegiblesDets;
	}

}
