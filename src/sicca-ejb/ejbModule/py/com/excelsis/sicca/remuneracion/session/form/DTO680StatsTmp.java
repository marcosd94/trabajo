package py.com.excelsis.sicca.remuneracion.session.form;

import java.math.BigInteger;

public class DTO680StatsTmp {
	private String denoUnidad;
	private String estadoImport;
	private String motivo;
	private BigInteger cantProcesada;

	public DTO680StatsTmp() {
	}

	public DTO680StatsTmp(String denoUnidad, String estadoImport, String motivo, BigInteger cantProcesada) {
		this.denoUnidad = denoUnidad;
		this.estadoImport = estadoImport;
		this.cantProcesada = cantProcesada;
		this.motivo = motivo;
	}

	public String getDenoUnidad() {
		return denoUnidad;
	}

	public void setDenoUnidad(String denoUnidad) {
		this.denoUnidad = denoUnidad;
	}

	public String getEstadoImport() {
		return estadoImport;
	}

	public void setEstadoImport(String estadoImport) {
		this.estadoImport = estadoImport;
	}

	public BigInteger getCantProcesada() {
		return cantProcesada;
	}

	public void setCantProcesada(BigInteger cantProcesada) {
		this.cantProcesada = cantProcesada;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

}
