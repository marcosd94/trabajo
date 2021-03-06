package py.com.excelsis.sicca.entity;

// Generated 05/11/2012 03:13:42 PM by Hibernate Tools 3.4.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * Remuneraciones generated by hbm2java
 */
@Entity
@Table(name = "remuneraciones_mh_tmp", schema = "remuneracion")
public class RemuneracionesMHTMP implements java.io.Serializable {

	private Long idRemuneracionMhTmp;
	private Long filaId;
	private Long planillaUniqueId;
	private String cedula;
	private Integer anio;
	private Integer nivel;
	private Integer entidad;
	private Integer tipoPresupuesto;
	private Integer programa;
	private Integer mes;
	private String nombres;
	private String lineaPresupuestaria;
	private String codObjeto;
	private String objetoGasto;
	private Integer fuenteFinanc;
	private Integer orgFinanciador;
	private String categoria;
	private String descripCategoria;
	private Double montoPresupuestado;
	private Double montoDevengado;
	private Double montoLiquido;
	private Date fechaActualizacion;
	private Date fechaDescarga;

	
	
	
	
	 
	public RemuneracionesMHTMP() {
	}


	@Id
	@Column(name = "id_remuneracion_mh_tmp", unique = true, nullable = false)
	@GeneratedValue(generator="REMUNERACIONTmp_GENERATOR")
	@SequenceGenerator(name="REMUNERACIONTmp_GENERATOR",sequenceName="remuneracion.remuneraciones_mh_tmp_id_remuneracion_mh_tmp_seq")
		public Long getIdRemuneracionMhTmp() {
		return idRemuneracionMhTmp;
	}


	public void setIdRemuneracionMhTmp(Long idRemuneracionMh) {
		this.idRemuneracionMhTmp = idRemuneracionMh;
	}

	@Column(name = "anio")
	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	@Column(name = "nivel")
	public Integer getNivel() {
		return nivel;
	}


	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}
	@Column(name = "cedula")
	public String getCedula() {
		return cedula;
	}


	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	@Column(name = "nombres")
	public String getNombres() {
		return nombres;
	}


	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	
	@Column(name = "cod_objeto")
	public String getCodObjeto() {
		return codObjeto;
	}


	public void setCodObjeto(String codObjeto) {
		this.codObjeto = codObjeto;
	}
	
	@Column(name = "fecha_actualizacion")
	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}


	public void setFechaActualizacion(Date fecha) {
		this.fechaActualizacion = fecha;
	}
	
	
	@Column(name = "fecha_descarga")
	public Date getFechaDescarga() {
		return fechaDescarga;
	}


	public void setFechaDescarga(Date fecha) {
		this.fechaDescarga = fecha;
	}
	
	@Column(name = "entidad")

	public Integer getEntidad() {
		return entidad;
	}


	public void setEntidad(Integer entidad) {
		this.entidad = entidad;
	}

	@Column(name = "tipo_presupuesto")

	public Integer getTipoPresupuesto() {
		return tipoPresupuesto;
	}


	public void setTipoPresupuesto(Integer tipoPresupuesto) {
		this.tipoPresupuesto = tipoPresupuesto;
	}

	@Column(name = "programa")

	public Integer getPrograma() {
		return programa;
	}


	public void setPrograma(Integer programa) {
		this.programa = programa;
	}

	@Column(name = "fuente_finac")
	public Integer getFuenteFinanc() {
		return fuenteFinanc;
	}


	public void setFuenteFinanc(Integer fuenteFinanc) {
		this.fuenteFinanc = fuenteFinanc;
	}

	@Column(name = "objeto_gasto")

	public String getObjetoGasto() {
		return objetoGasto;
	}


	public void setObjetoGasto(String objetoGasto) {
		this.objetoGasto = objetoGasto;
	}

	@Column(name = "org_financiador")

	public Integer getOrgFinanciador() {
		return orgFinanciador;
	}


	public void setOrgFinanciador(Integer orgFinanciador) {
		this.orgFinanciador = orgFinanciador;
	}



	@Column(name = "linea_presupuestaria")

	public String getLineaPresupuestaria() {
		return lineaPresupuestaria;
	}


	public void setLineaPresupuestaria(String lineaPresupuestaria) {
		this.lineaPresupuestaria = lineaPresupuestaria;
	}


	@Column(name = "monto_presupuestado")

	public Double getMontoPresupuestado() {
		return montoPresupuestado;
	}


	public void setMontoPresupuestado(Double montoPresupuestado) {
		this.montoPresupuestado = montoPresupuestado;
	}
	
	@Column(name = "monto_devengado")

	public Double getMontoDevengado() {
		return montoDevengado;
	}


	public void setMontoDevengado(Double montoDevengado) {
		this.montoDevengado = montoDevengado;
	}
	
	@Column(name = "monto_liquido")

	public Double getMontoLiquido() {
		return montoLiquido;
	}


	public void setMontoLiquido(Double montoLiquido) {
		this.montoLiquido = montoLiquido;
	}

	@Column(name = "planilla_unique_id")
	public Long getPlanillaUniqueId() {
		return planillaUniqueId;
	}


	public void setPlanillaUniqueId(Long planillaUniqueId) {
		this.planillaUniqueId = planillaUniqueId;
	}

	@Column(name = "fila_id")
	public Long getFilaId() {
		return filaId;
	}


	public void setFilaId(Long filaId) {
		this.filaId = filaId;
	}

	@Column(name = "mes")

	public Integer getMes() {
		return mes;
	}


	public void setMes(Integer mes) {
		this.mes = mes;
	}

	@Column(name = "categoria")

	public String getCategoria() {
		return categoria;
	}


	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Column(name = "descrip_categoria")
	public String getDescripCategoria() {
		return descripCategoria;
	}


	public void setDescripCategoria(String descripCategoria) {
		this.descripCategoria = descripCategoria;
	}



	
}
