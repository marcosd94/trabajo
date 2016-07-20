package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.CondicionTrabajo;

public class CondicionTrabajoDTO {
	private Long id;
	private CondicionTrabajo condicionTrabajo;
	private Float puntaje;
	private String puntajeString;
	private Boolean activo;
	
	
	public CondicionTrabajoDTO() {
	
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CondicionTrabajo getCondicionTrabajo() {
		return condicionTrabajo;
	}
	public void setCondicionTrabajo(CondicionTrabajo condicionTrabajo) {
		this.condicionTrabajo = condicionTrabajo;
	}
	public Float getPuntaje() {
		return puntaje;
	}
	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
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
