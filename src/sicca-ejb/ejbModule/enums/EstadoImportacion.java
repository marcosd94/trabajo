package enums;

import py.com.excelsis.sicca.util.SICCAAppHelper;

public enum EstadoImportacion {
	todo(1,"TODOS", null),
	Exito(2, "EXITOSO", "EXITOSO"),
	Fracaso(3, "FRACASO", "FRACASO")
	;
	
	
	private Integer id;
	private String descripcion;
	private String valor;
	
	
	private EstadoImportacion(Integer id, String descripcion, String valor) {
		this.id = id;
		this.descripcion = descripcion;
		this.valor = valor;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public static EstadoImportacion getEstadoImportacionPorId(String valor) {
		for (EstadoImportacion tr : EstadoImportacion.values()) {
			if (valor.equals(tr.getValor())) {
				return tr;
			}
		}
		return null;
	}
	
	
}
