package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.CondicionTrabajoEspecif;

public class CondicionTrabajoEspecifDTO {
	private Long id;
	private CondicionTrabajoEspecif condicionTrabajoEspecif;
	private Float puntaje;
	private String puntajeString;
	private String justificacion;
	private String ajustes;
	private Boolean activo;
	
	
	
	public CondicionTrabajoEspecifDTO() {
		
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CondicionTrabajoEspecif getCondicionTrabajoEspecif() {
		return condicionTrabajoEspecif;
	}
	public void setCondicionTrabajoEspecif(
			CondicionTrabajoEspecif condicionTrabajoEspecif) {
		this.condicionTrabajoEspecif = condicionTrabajoEspecif;
	}
	public Float getPuntaje() {
		return puntaje;
	}
	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}
	public String getJustificacion() {
		return justificacion;
	}
	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}
	public String getAjustes() {
		return ajustes;
	}
	public void setAjustes(String ajustes) {
		this.ajustes = ajustes;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public String getPuntajeString() {
		return puntajeString;
	}
	public void setPuntajeString(String puntajeString) {
		this.puntajeString = puntajeString;
	}
	
	
	
}
