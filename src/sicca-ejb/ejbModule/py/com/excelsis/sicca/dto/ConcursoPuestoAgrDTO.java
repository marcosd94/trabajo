package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.PlantaCargoDet;

public class ConcursoPuestoAgrDTO {

	private ConcursoPuestoAgr concursoPuestoAgr;
	private PlantaCargoDet plantaCargoDet;
	private Integer cantidad;
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}
	public PlantaCargoDet getPlantaCargoDet() {
		return plantaCargoDet;
	}
	public void setPlantaCargoDet(PlantaCargoDet plantaCargoDet) {
		this.plantaCargoDet = plantaCargoDet;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	
	
}
