package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ClasificadorUo;
import py.com.excelsis.sicca.entity.RequisitoMinimoCuo;

public class RequisitosMinDTO {

	private Long idClasificadorUoReq;
	private RequisitoMinimoCuo requisitoMinimoCuo;
	private ClasificadorUo clasificadorUo;
	private String descripcion;
	private Boolean activo;
	
	
	
	public RequisitosMinDTO() {
		
	}
	
	public RequisitosMinDTO(Long idClasificadorUoReq,
			RequisitoMinimoCuo requisitoMinimoCuo,
			ClasificadorUo clasificadorUo, String descripcion, Boolean activo) {

		this.idClasificadorUoReq = idClasificadorUoReq;
		this.requisitoMinimoCuo = requisitoMinimoCuo;
		this.clasificadorUo = clasificadorUo;
		this.descripcion = descripcion;
		this.activo = activo;
	}




	public Long getIdClasificadorUoReq() {
		return idClasificadorUoReq;
	}
	public void setIdClasificadorUoReq(Long idClasificadorUoReq) {
		this.idClasificadorUoReq = idClasificadorUoReq;
	}
	public RequisitoMinimoCuo getRequisitoMinimoCuo() {
		return requisitoMinimoCuo;
	}
	public void setRequisitoMinimoCuo(RequisitoMinimoCuo requisitoMinimoCuo) {
		this.requisitoMinimoCuo = requisitoMinimoCuo;
	}
	public ClasificadorUo getClasificadorUo() {
		return clasificadorUo;
	}
	public void setClasificadorUo(ClasificadorUo clasificadorUo) {
		this.clasificadorUo = clasificadorUo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	
	
}
