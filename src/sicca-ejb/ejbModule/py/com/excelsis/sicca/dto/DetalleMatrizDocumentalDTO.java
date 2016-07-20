package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.DatosEspecificos;

public class DetalleMatrizDocumentalDTO {

	private Long id;
	private Long idDatosEspecificos;
	private DatosEspecificos datosEspecificos;
	private Boolean activo;
	private Integer orden;
	private Boolean bloquear;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getIdDatosEspecificos() {
		return idDatosEspecificos;
	}
	public void setIdDatosEspecificos(Long idDatosEspecificos) {
		this.idDatosEspecificos = idDatosEspecificos;
	}
	public DatosEspecificos getDatosEspecificos() {
		return datosEspecificos;
	}
	public void setDatosEspecificos(DatosEspecificos datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	public Boolean getBloquear() {
		return bloquear;
	}
	public void setBloquear(Boolean bloquear) {
		this.bloquear = bloquear;
	}

	
	
}
