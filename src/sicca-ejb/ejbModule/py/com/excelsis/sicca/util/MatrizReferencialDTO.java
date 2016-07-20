package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.MatrizReferencial;
import py.com.excelsis.sicca.entity.MatrizReferencialDet;
import py.com.excelsis.sicca.entity.MatrizReferencialEnc;

public class MatrizReferencialDTO implements Comparable {
	MatrizReferencial matrizReferencial;
	MatrizReferencialEnc matrizReferencialEnc;
	MatrizReferencialDet matrizReferencialDet;
	Boolean mostrarAcciones;
	Integer orden;

	public MatrizReferencial getMatrizReferencial() {
		return matrizReferencial;
	}
	public  MatrizReferencialDTO(Integer orden){		
		this.orden = orden;
	}

	public void setMatrizReferencial(MatrizReferencial matrizReferencial) {
		this.matrizReferencial = matrizReferencial;
	}

	public MatrizReferencialEnc getMatrizReferencialEnc() {
		return matrizReferencialEnc;
	}

	public void setMatrizReferencialEnc(MatrizReferencialEnc matrizReferencialEnc) {
		this.matrizReferencialEnc = matrizReferencialEnc;
	}

	public MatrizReferencialDet getMatrizReferencialDet() {
		return matrizReferencialDet;
	}

	public void setMatrizReferencialDet(MatrizReferencialDet matrizReferencialDet) {
		this.matrizReferencialDet = matrizReferencialDet;
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
		MatrizReferencialDTO obj = (MatrizReferencialDTO) arg0;
		return this.orden.compareTo(obj.orden);
	}

}
