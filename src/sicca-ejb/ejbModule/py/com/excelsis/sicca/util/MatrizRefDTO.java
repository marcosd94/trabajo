package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.*;

public class MatrizRefDTO {
	MatrizRefConf matrizRefConf;
	MatrizRefConfEnc matrizRefConfEnc;
	MatrizRefConfDet MatrizRefConfDet;
	Boolean mostrarAcciones;

	public Boolean getMostrarAcciones() {
		return mostrarAcciones;
	}

	public void setMostrarAcciones(Boolean mostrarAcciones) {
		this.mostrarAcciones = mostrarAcciones;
	}

	public MatrizRefConf getMatrizRefConf() {
		return matrizRefConf;
	}

	public void setMatrizRefConf(MatrizRefConf matrizRefConf) {
		this.matrizRefConf = matrizRefConf;
	}

	public MatrizRefConfEnc getMatrizRefConfEnc() {
		return matrizRefConfEnc;
	}

	public void setMatrizRefConfEnc(MatrizRefConfEnc matrizRefConfEnc) {
		this.matrizRefConfEnc = matrizRefConfEnc;
	}

	public MatrizRefConfDet getMatrizRefConfDet() {
		return MatrizRefConfDet;
	}

	public void setMatrizRefConfDet(MatrizRefConfDet matrizRefConfDet) {
		MatrizRefConfDet = matrizRefConfDet;
	}

}
