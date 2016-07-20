package py.com.excelsis.sicca.dto;

import java.util.List;

import py.com.excelsis.sicca.entity.ContenidoFuncional;
import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;

public class ContenidoFuncionalDTO {

	Long id;
	ContenidoFuncional contenidoFuncional;
	Float puntaje;
	String puntajeString;
	private Boolean mostrar;
	List<DetDescripcionContFuncional> listaDetDescripContFuncional;
	
	
	
	public ContenidoFuncionalDTO() {
	
	}
	
	
	
	public ContenidoFuncionalDTO(Long id,
			ContenidoFuncional contenidoFuncional, Float puntaje,
			List<DetDescripcionContFuncional> listaDetDescripContFuncional) {
		
		this.id = id;
		this.contenidoFuncional = contenidoFuncional;
		this.puntaje = puntaje;
		this.listaDetDescripContFuncional = listaDetDescripContFuncional;
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ContenidoFuncional getContenidoFuncional() {
		return contenidoFuncional;
	}
	public void setContenidoFuncional(ContenidoFuncional contenidoFuncional) {
		this.contenidoFuncional = contenidoFuncional;
	}
	public Float getPuntaje() {
		return puntaje;
	}
	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}
	public List<DetDescripcionContFuncional> getListaDetDescripContFuncional() {
		return listaDetDescripContFuncional;
	}
	public void setListaDetDescripContFuncional(
			List<DetDescripcionContFuncional> listaDetDescripContFuncional) {
		this.listaDetDescripContFuncional = listaDetDescripContFuncional;
	}

	public Boolean getMostrar() {
		return mostrar;
	}

	public void setMostrar(Boolean mostrar) {
		this.mostrar = mostrar;
	}



	public String getPuntajeString() {
		return puntajeString;
	}



	public void setPuntajeString(String puntajeString) {
		this.puntajeString = puntajeString;
	}
	
	
}
