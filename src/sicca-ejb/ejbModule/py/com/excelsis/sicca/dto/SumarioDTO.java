package py.com.excelsis.sicca.dto;

import java.util.Date;

public class SumarioDTO {

	private Long id;
	private String oee;
	private Date fecha;
	private String nroInterno;
	private String faltas;
	private String recomendacionJuez;
	private String estado;
	private String decisionMai;
	private String obsMai;
	
	
	
	public SumarioDTO() {
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOee() {
		return oee;
	}
	public void setOee(String oee) {
		this.oee = oee;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getNroInterno() {
		return nroInterno;
	}
	public void setNroInterno(String nroInterno) {
		this.nroInterno = nroInterno;
	}
	public String getFaltas() {
		return faltas;
	}
	public void setFaltas(String faltas) {
		this.faltas = faltas;
	}
	public String getRecomendacionJuez() {
		return recomendacionJuez;
	}
	public void setRecomendacionJuez(String recomendacionJuez) {
		this.recomendacionJuez = recomendacionJuez;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getDecisionMai() {
		return decisionMai;
	}
	public void setDecisionMai(String decisionMai) {
		this.decisionMai = decisionMai;
	}
	public String getObsMai() {
		return obsMai;
	}
	public void setObsMai(String obsMai) {
		this.obsMai = obsMai;
	}
	
	
}
