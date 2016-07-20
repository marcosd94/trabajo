package py.com.excelsis.sicca.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.jboss.seam.annotations.Name;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpt_niveles_cargos", schema = "planificacion")
public class CptNivelesCargos {
	
	private Long idCpt;
	private Long idNivelesCargos;
	private Long idCptNivelesCargos;
	
	
	public CptNivelesCargos(){
		
	}
	public CptNivelesCargos(Long idCpt, Long idNivelesCargos, Long idCptNivelesCargos){
		this.idCpt=idCpt;
		this.idCptNivelesCargos=idCptNivelesCargos;
		this.idNivelesCargos=idNivelesCargos;
	}
	
	@Id
	@Column(name = "id_cpt_niveles", unique = true, nullable = false)
	@GeneratedValue(generator="ID_CPT_NIVELES_GENERATOR")
	@SequenceGenerator(name="ID_CPT_NIVELES_GENERATOR",sequenceName="planificacion.cpt_niveles_cargos_id_cpt_niveles_seq")
	public Long getIdCptNivelesCargos() {
		return idCptNivelesCargos;
	}
	public void setIdCptNivelesCargos(Long idCptNivelesCargos) {
		this.idCptNivelesCargos = idCptNivelesCargos;
	}
	@Column(name = "id_cpt")
	public Long getIdCpt() {
		return idCpt;
	}
	public void setIdCpt(Long idCpt) {
		this.idCpt = idCpt;
	}
	@Column(name="id_niveles_de_cargos")
	public Long getIdNivelesCargos() {
		return idNivelesCargos;
	}
	public void setIdNivelesCargos(Long idNivelesCargos) {
		this.idNivelesCargos = idNivelesCargos;
	}
	

}
