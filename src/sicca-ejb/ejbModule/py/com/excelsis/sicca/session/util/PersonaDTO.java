package py.com.excelsis.sicca.session.util;

import py.com.excelsis.sicca.entity.Persona;

public class PersonaDTO {
	private String estado;
	private Boolean activo;
	private Persona persona;
	private String mensaje;
	private  Boolean insertarSug;
	private Boolean habilitarBtn = true;
	public PersonaDTO() {

	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Boolean getInsertarSug() {
		return insertarSug;
	}

	public void setInsertarSug(Boolean insertarSug) {
		this.insertarSug = insertarSug;
	}

	public Boolean getHabilitarBtn() {
		return habilitarBtn;
	}

	public void setHabilitarBtn(Boolean habilitarBtn) {
		this.habilitarBtn = habilitarBtn;
	}

	 
	

}
