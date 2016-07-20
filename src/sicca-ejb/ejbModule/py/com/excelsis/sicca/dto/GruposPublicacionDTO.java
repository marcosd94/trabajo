package py.com.excelsis.sicca.dto;

import py.com.excelsis.sicca.entity.FechasGrupo;

public class GruposPublicacionDTO {
	private Long id;
	private FechasGrupo fechasGrupo;
	private Boolean seleccionado;
	private String estado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(Boolean seleccionado) {
		this.seleccionado = seleccionado;
	}

	public FechasGrupo getFechasGrupo() {
		return fechasGrupo;
	}

	public void setFechasGrupo(FechasGrupo fechasGrupo) {
		this.fechasGrupo = fechasGrupo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	
	
}
