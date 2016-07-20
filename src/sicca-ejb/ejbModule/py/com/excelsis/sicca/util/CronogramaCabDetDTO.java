package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.CronogramaConcCab;
import py.com.excelsis.sicca.entity.CronogramaConcDet;

public class CronogramaCabDetDTO {
	CronogramaConcCab cronogramaCab;
	CronogramaConcDet cronogramaDet;
	Boolean mostrarAcciones;
	
	Boolean mostrarCab ;
	Boolean mostrarDet;
	 
	public CronogramaConcCab getCronogramaCab() {
		return cronogramaCab;
	}
	public void setCronogramaCab(CronogramaConcCab cronogramaCab) {
		this.cronogramaCab = cronogramaCab;
	}
	public CronogramaConcDet getCronogramaDet() {
		return cronogramaDet;
	}
	public void setCronogramaDet(CronogramaConcDet cronogramaDet) {
		this.cronogramaDet = cronogramaDet;
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
