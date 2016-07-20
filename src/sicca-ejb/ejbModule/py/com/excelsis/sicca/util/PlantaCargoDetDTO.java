package py.com.excelsis.sicca.util;

import py.com.excelsis.sicca.entity.PlantaCargoDet;

public class PlantaCargoDetDTO {

	PlantaCargoDet plantaCargoDet;
	Boolean reservar;
	Boolean asignarCat=false;
	
	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}
	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}
	public Boolean getReservar() {
		return reservar;
	}
	public void setReservar(Boolean reservar) {
		this.reservar = reservar;
	}
	
	public Boolean getAsignarCat() {
		return asignarCat;
	}
	public void setAsignarCat(Boolean asignarCat) {
		this.asignarCat = asignarCat;
	}
	
	
	
}
