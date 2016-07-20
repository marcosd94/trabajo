package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.DetDescripcionContFuncional;

public class ContenFuncionalDTO {

	private Boolean selected = false;
	private DetDescripcionContFuncional detDesc ;
	 
	private Boolean mostrarAcciones;
	public Boolean getSelected() {
		return selected;
	}
	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	public DetDescripcionContFuncional getDetDesc() {
		 
		return detDesc;
	}
	public void setDetDesc(DetDescripcionContFuncional detDesc) {
		this.detDesc = detDesc;
	}
	 
	public Boolean getMostrarAcciones() {
		return mostrarAcciones;
	}
	public void setMostrarAcciones(Boolean mostrarAcciones) {
		this.mostrarAcciones = mostrarAcciones;
	}	
}
