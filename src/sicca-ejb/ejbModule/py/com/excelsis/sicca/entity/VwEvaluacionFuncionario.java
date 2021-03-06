package py.com.excelsis.sicca.entity;

// Generated 09/11/2012 02:17:30 PM by Hibernate Tools 3.4.0.Beta1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * VwEvaluacionFuncionario generated by hbm2java
 */
@Entity
@Table(name = "vw_evaluacion_funcionario", schema = "movilidad")
public class VwEvaluacionFuncionario implements java.io.Serializable {

	private String item;
	private String fechas;
	private String cargaHoraria;
	private String puntaje;
	private String origen;
	private Long idPersona;
	private Long idEvaluacionDesempeno;
	private String idEstudiosRealizados;
	private Long idCapacitacion;
	private Long documento;


	public VwEvaluacionFuncionario() {
	}

	public VwEvaluacionFuncionario(String item, String fechas,
			String cargaHoraria, String puntaje, String origen, Long idPersona,
			Long idEvaluacionDesempeno, String idEstudiosRealizados,
			Long idCapacitacion, Long documento) {
		this.item = item;
		this.fechas = fechas;
		this.cargaHoraria = cargaHoraria;
		this.puntaje = puntaje;
		this.origen = origen;
		this.idPersona = idPersona;
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
		this.idEstudiosRealizados = idEstudiosRealizados;
		this.idCapacitacion = idCapacitacion;
		this.documento = documento;
	
	}

	@Column(name = "item")
	public String getItem() {
		return this.item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	@Column(name = "fechas")
	public String getFechas() {
		return this.fechas;
	}

	public void setFechas(String fechas) {
		this.fechas = fechas;
	}

	@Column(name = "carga_horaria")
	public String getCargaHoraria() {
		return this.cargaHoraria;
	}

	public void setCargaHoraria(String cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	@Column(name = "puntaje")
	public String getPuntaje() {
		return this.puntaje;
	}

	public void setPuntaje(String puntaje) {
		this.puntaje = puntaje;
	}

	@Column(name = "origen")
	public String getOrigen() {
		return this.origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	@Column(name = "id_persona")
	public Long getIdPersona() {
		return this.idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	@Column(name = "id_evaluacion_desempeno")
	public Long getIdEvaluacionDesempeno() {
		return this.idEvaluacionDesempeno;
	}

	public void setIdEvaluacionDesempeno(Long idEvaluacionDesempeno) {
		this.idEvaluacionDesempeno = idEvaluacionDesempeno;
	}

	@Column(name = "id_estudios_realizados")
	@Id
	public String getIdEstudiosRealizados() {
		return this.idEstudiosRealizados;
	}

	public void setIdEstudiosRealizados(String idEstudiosRealizados) {
		this.idEstudiosRealizados = idEstudiosRealizados;
	}

	@Column(name = "id_capacitacion")
	public Long getIdCapacitacion() {
		return this.idCapacitacion;
	}

	public void setIdCapacitacion(Long idCapacitacion) {
		this.idCapacitacion = idCapacitacion;
	}

	@Column(name = "documento")
	public Long getDocumento() {
		return this.documento;
	}

	public void setDocumento(Long documento) {
		this.documento = documento;
	}


}
