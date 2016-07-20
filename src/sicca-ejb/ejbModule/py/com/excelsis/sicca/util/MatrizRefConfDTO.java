package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.MatrizRefConfDet;

public class MatrizRefConfDTO implements Comparable {
	MatrizRefConfDet matrizRefConfDet;
	Boolean mostrarAcciones;
	Boolean mostrarAcciones2;
	Integer orden;

	public MatrizRefConfDTO(Integer orden){
		this.orden =orden;
	}
	public Boolean getMostrarAcciones2() {
		return mostrarAcciones2;
	}

	public void setMostrarAcciones2(Boolean mostrarAcciones2) {
		this.mostrarAcciones2 = mostrarAcciones2;
	}

	public MatrizRefConfDet getMatrizRefConfDet() {
		return matrizRefConfDet;
	}

	public void setMatrizRefConfDet(MatrizRefConfDet matrizRefConfDet) {
		this.matrizRefConfDet = matrizRefConfDet;
	}

	public Boolean getMostrarAcciones() {
		return mostrarAcciones;
	}

	public void setMostrarAcciones(Boolean mostrarAcciones) {
		this.mostrarAcciones = mostrarAcciones;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public int compareTo(Object arg0) {
		MatrizRefConfDTO obj = (MatrizRefConfDTO) arg0;
		return this.orden.compareTo(obj.orden);
	}

}
