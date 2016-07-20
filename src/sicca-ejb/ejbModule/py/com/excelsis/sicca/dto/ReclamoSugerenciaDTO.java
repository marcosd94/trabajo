package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.ReclamoSugerencia;

public class ReclamoSugerenciaDTO {
	private Long id;
	private ReclamoSugerencia reclamoSugerencia;
	private Boolean ver;
	private Boolean responder;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ReclamoSugerencia getReclamoSugerencia() {
		return reclamoSugerencia;
	}
	public void setReclamoSugerencia(ReclamoSugerencia reclamoSugerencia) {
		this.reclamoSugerencia = reclamoSugerencia;
	}
	public Boolean getVer() {
		return ver;
	}
	public void setVer(Boolean ver) {
		this.ver = ver;
	}
	public Boolean getResponder() {
		return responder;
	}
	public void setResponder(Boolean responder) {
		this.responder = responder;
	}
	
	
}
