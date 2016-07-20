package py.com.excelsis.sicca.dto;

import java.util.List;

import py.com.excelsis.sicca.entity.DetMinimosRequeridos;
import py.com.excelsis.sicca.entity.DetOpcionesConvenientes;
import py.com.excelsis.sicca.entity.RequisitoMinimoCpt;

public class RequisitosMinimosDTO {

	private Long id;
	private RequisitoMinimoCpt requisitoMinimoCpt;
	private Float puntaje;
	private String puntajeString;
	private Boolean mostrar;
	private List<DetOpcionesConvenientes> listaDetOpcionesConvenientes;
	private List<DetMinimosRequeridos> listaDetMinimosRequeridos;
	
	
	
	public RequisitosMinimosDTO() {
	
	}
	
	
	public RequisitosMinimosDTO(Long id,
			RequisitoMinimoCpt requisitoMinimoCpt, Float puntaje,
			List<DetOpcionesConvenientes> listaDetOpcionesConvenientes,
			List<DetMinimosRequeridos> listaDetMinimosRequeridos) {
		
		this.id = id;
		this.requisitoMinimoCpt = requisitoMinimoCpt;
		this.puntaje = puntaje;
		this.listaDetOpcionesConvenientes = listaDetOpcionesConvenientes;
		this.listaDetMinimosRequeridos = listaDetMinimosRequeridos;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public RequisitoMinimoCpt getRequisitoMinimoCpt() {
		return requisitoMinimoCpt;
	}
	public void setRequisitoMinimoCpt(RequisitoMinimoCpt requisitoMinimoCpt) {
		this.requisitoMinimoCpt = requisitoMinimoCpt;
	}
	public Float getPuntaje() {
		return puntaje;
	}
	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}
	public List<DetOpcionesConvenientes> getListaDetOpcionesConvenientes() {
		return listaDetOpcionesConvenientes;
	}
	public void setListaDetOpcionesConvenientes(
			List<DetOpcionesConvenientes> listaDetOpcionesConvenientes) {
		this.listaDetOpcionesConvenientes = listaDetOpcionesConvenientes;
	}
	public List<DetMinimosRequeridos> getListaDetMinimosRequeridos() {
		return listaDetMinimosRequeridos;
	}
	public void setListaDetMinimosRequeridos(
			List<DetMinimosRequeridos> listaDetMinimosRequeridos) {
		this.listaDetMinimosRequeridos = listaDetMinimosRequeridos;
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
