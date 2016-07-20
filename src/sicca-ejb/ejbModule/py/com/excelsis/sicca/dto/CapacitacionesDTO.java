package py.com.excelsis.sicca.dto;

public class CapacitacionesDTO {

	private String tipo;
	private String cursoBeca;
	private String oee;
	private String fechaDesdeHasta;
	private String cargaHoraria;
	private String certificado;

	public CapacitacionesDTO() {

	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getCursoBeca() {
		return cursoBeca;
	}

	public void setCursoBeca(String cursoBeca) {
		this.cursoBeca = cursoBeca;
	}

	public String getOee() {
		return oee;
	}

	public void setOee(String oee) {
		this.oee = oee;
	}

	public String getFechaDesdeHasta() {
		return fechaDesdeHasta;
	}

	public void setFechaDesdeHasta(String fechaDesdeHasta) {
		this.fechaDesdeHasta = fechaDesdeHasta;
	}

	public String getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(String cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public String getCertificado() {
		return certificado;
	}

	public void setCertificado(String certificado) {
		this.certificado = certificado;
	}

}
