package py.com.excelsis.sicca.dto;

public class ValidadorDTO {

	private boolean valido;
	private String mensaje;

	public ValidadorDTO() {

	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

}
