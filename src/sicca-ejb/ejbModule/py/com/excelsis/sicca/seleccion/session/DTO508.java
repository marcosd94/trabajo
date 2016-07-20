package py.com.excelsis.sicca.seleccion.session;

import py.com.excelsis.sicca.entity.Persona;

public class DTO508 implements Comparable {
	private Persona persona;
	private String estado;
	private Boolean habLinkPostular;
	private Boolean habLinkDatosDetallados;
	private Boolean habLinkIngresarObs;
	private Boolean habLinkVer;
 

	public DTO508() {
		persona = null;
		habLinkPostular = false;
		habLinkDatosDetallados = false;
		habLinkIngresarObs = false;
		habLinkVer = false;
	 
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {

		this.persona = persona;
	}

	public Boolean getHabLinkPostular() {
		return habLinkPostular;
	}

	public void setHabLinkPostular(Boolean habLinkPostular) {
		this.habLinkPostular = habLinkPostular;
	}

	public Boolean getHabLinkDatosDetallados() {
		return habLinkDatosDetallados;
	}

	public void setHabLinkDatosDetallados(Boolean habLinkDatosDetallados) {
		this.habLinkDatosDetallados = habLinkDatosDetallados;
	}

	public Boolean getHabLinkIngresarObs() {
		return habLinkIngresarObs;
	}

	public void setHabLinkIngresarObs(Boolean habLinkIngresarObs) {
		this.habLinkIngresarObs = habLinkIngresarObs;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Boolean getHabLinkVer() {
		return habLinkVer;
	}

	public void setHabLinkVer(Boolean habLinkVer) {
		this.habLinkVer = habLinkVer;
	}

	 

	public int compareTo(Object arg0) {
		DTO508 dto = (DTO508) arg0;

		if (this.persona.getNombres().compareToIgnoreCase(dto.persona.getNombres()) == 0) {
			if (this.persona.getApellidos().compareToIgnoreCase(dto.persona.getApellidos()) == 0) {
				return this.persona.getDocumentoIdentidad().compareTo(dto.persona.getDocumentoIdentidad());
			} else {
				return this.persona.getApellidos().compareToIgnoreCase(dto.persona.getApellidos());
			}
		} else {
			return this.persona.getNombres().compareToIgnoreCase(dto.persona.getNombres());
		}
	}
}
