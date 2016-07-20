package py.com.excelsis.sicca.util;

import java.util.Date;

public class CU422DTO {
	Date fecha;
	String descripcion;

	public CU422DTO() {
		
	}
	public CU422DTO(Date laFecha, String laDesc) {
		this.fecha = laFecha;
		this.descripcion = laDesc;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
