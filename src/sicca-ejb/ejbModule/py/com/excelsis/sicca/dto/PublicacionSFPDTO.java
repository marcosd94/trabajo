package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ConcursoPuestoAgr;

public class PublicacionSFPDTO {

	private Long id;
	private ConcursoPuestoAgr concursoPuestoAgr;
	private Boolean seleccionado;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ConcursoPuestoAgr getConcursoPuestoAgr() {
		return concursoPuestoAgr;
	}
	public void setConcursoPuestoAgr(ConcursoPuestoAgr concursoPuestoAgr) {
		this.concursoPuestoAgr = concursoPuestoAgr;
	}
	public Boolean getSeleccionado() {
		return seleccionado;
	}
	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	
	
}
