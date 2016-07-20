package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;
import py.com.excelsis.sicca.entity.PlantaCargoDet;
import py.com.excelsis.sicca.entity.PromocionSalarial;

public class ConcursoCategoriaAgrDTO {

	private ConcursoPuestoAgr concursoPuestoAgr;
	private PromocionSalarial promocionSalarial;
	private Integer cantidad;
	Boolean reservar;
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}
	public PromocionSalarial getPromocionSalarial() {
		return promocionSalarial;
	}
	public void setPromocionSalarial(PromocionSalarial promocionSalarial) {
		this.promocionSalarial = promocionSalarial;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Boolean getReservar() {
		return reservar;
	}
	public void setReservar(Boolean reservar) {
		this.reservar = reservar;
	}
	
}
