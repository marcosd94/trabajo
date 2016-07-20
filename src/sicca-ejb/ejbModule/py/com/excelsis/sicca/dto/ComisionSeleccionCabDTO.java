package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ComisionSeleccionCab;
import py.com.excelsis.sicca.entity.ComisionSeleccionDet;



public class ComisionSeleccionCabDTO {
	ComisionSeleccionCab comisionSeleccionCab;
	ComisionSeleccionDet comisionSeleccionDet;
	Boolean mostrarAcciones;
	
	Boolean mostrarCab ;
	Boolean mostrarDet;
	 
	
	public ComisionSeleccionCab getComisionSeleccionCab() {
		return comisionSeleccionCab;
	}
	public void setComisionSeleccionCab(ComisionSeleccionCab comisionSeleccionCab) {
		this.comisionSeleccionCab = comisionSeleccionCab;
	}
	public ComisionSeleccionDet getComisionSeleccionDet() {
		return comisionSeleccionDet;
	}
	public void setComisionSeleccionDet(ComisionSeleccionDet comisionSeleccionDet) {
		this.comisionSeleccionDet = comisionSeleccionDet;
	}
	public Boolean getMostrarCab() {
		return mostrarCab;
	}
	public void setMostrarCab(Boolean mostrarCab) {
		this.mostrarCab = mostrarCab;
	}
	public Boolean getMostrarDet() {
		return mostrarDet;
	}
	public void setMostrarDet(Boolean mostrarDet) {
		this.mostrarDet = mostrarDet;
	}
	public Boolean getMostrarAcciones() {
		return mostrarAcciones;
	}
	public void setMostrarAcciones(Boolean mostrarAcciones) {
		this.mostrarAcciones = mostrarAcciones;
	}
	
	
}
