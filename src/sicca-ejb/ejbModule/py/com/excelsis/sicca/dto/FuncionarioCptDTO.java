package py.com.excelsis.sicca.dto;


import py.com.excelsis.sicca.entity.DetContenidoFuncional;
import py.com.excelsis.sicca.entity.PlantillaEvalPdec;
import py.com.excelsis.sicca.entity.PlantillaEvalPdecCab;

import py.com.excelsis.sicca.entity.Sujetos;

public class FuncionarioCptDTO {

	private Sujetos sujetos;
	private DetContenidoFuncional detContenidoFuncional;
	private String tipo;
	private Float puntaje;
	private String actividades;
	private PlantillaEvalPdec plantillaEvalPdec;
	private PlantillaEvalPdecCab plantillaEvalPdecCab;
	private boolean mostarEstilo;

	
	public FuncionarioCptDTO() {
		 
	}


	public Sujetos getSujetos() {
		return sujetos;
	}


	public void setSujetos(Sujetos sujetos) {
		this.sujetos = sujetos;
	}


	


	public Float getPuntaje() {
		return puntaje;
	}


	public void setPuntaje(Float puntaje) {
		this.puntaje = puntaje;
	}


	public DetContenidoFuncional getDetContenidoFuncional() {
		return detContenidoFuncional;
	}


	public void setDetContenidoFuncional(DetContenidoFuncional detContenidoFuncional) {
		this.detContenidoFuncional = detContenidoFuncional;
	}


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	


	public String getActividades() {
		return actividades;
	}


	public void setActividades(String actividades) {
		this.actividades = actividades;
	}


	public PlantillaEvalPdec getPlantillaEvalPdec() {
		return plantillaEvalPdec;
	}


	public void setPlantillaEvalPdec(PlantillaEvalPdec plantillaEvalPdec) {
		this.plantillaEvalPdec = plantillaEvalPdec;
	}


	public boolean isMostarEstilo() {
		return mostarEstilo;
	}


	public void setMostarEstilo(boolean mostarEstilo) {
		this.mostarEstilo = mostarEstilo;
	}


	public PlantillaEvalPdecCab getPlantillaEvalPdecCab() {
		return plantillaEvalPdecCab;
	}


	public void setPlantillaEvalPdecCab(PlantillaEvalPdecCab plantillaEvalPdecCab) {
		this.plantillaEvalPdecCab = plantillaEvalPdecCab;
	}








	
	
}
